package com.elna.moviedb.feature.tvshows.data.repositories

import com.elna.moviedb.core.datastore.language.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.language.LanguageChangeListener
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShow
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.toDomain
import com.elna.moviedb.feature.tvshows.data.datasources.TvShowsRemoteService
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShow
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowDetails
import com.elna.moviedb.feature.tvshows.data.model.toDomain
import com.elna.moviedb.feature.tvshows.data.mappers.toTmdbPath
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Implementation of TvShowsRepository that manages TV show data from remote API.
 *
 * This repository uses category abstraction.
 * New TV show categories can be added to [TvShowCategory] enum without modifying this class.
 *
 * **Note:** This repository uses in-memory storage (MutableStateFlow) rather than
 * local database caching. TV shows are fetched from the API and held in memory
 * for the duration of the app session. For persistent offline-first storage,
 * see MoviesRepositoryImpl which uses Room database.
 *
 * This repository implements LanguageChangeListener and self-registers with the coordinator
 * during initialization, ensuring it's always properly set up to respond to language changes.
 *
 * @param remoteDataSource Remote data source for fetching TV shows from API
 * @param languageProvider Provider for formatted language strings
 * @param languageChangeCoordinator Coordinator for language change notifications
 */
class TvShowRepositoryImpl(
    private val remoteDataSource: TvShowsRemoteService,
    private val languageProvider: LanguageProvider,
    languageChangeCoordinator: LanguageChangeCoordinator,
) : TvShowsRepository, LanguageChangeListener {

    init {
        // Self-register with coordinator during initialization
        // Ensures repository is always properly set up to receive language change notifications
        languageChangeCoordinator.registerListener(this)
    }

    // Category-based pagination state using Maps for scalability
    private val currentPages = mutableMapOf<TvShowCategory, Int>()
    private val totalPages = mutableMapOf<TvShowCategory, Int>()
    private val tvShowsFlows = mutableMapOf<TvShowCategory, MutableStateFlow<List<TvShow>>>()

    /**
     * Helper function to get or create a StateFlow for a specific category.
     * Ensures lazy initialization of category flows.
     */
    private fun getFlowForCategory(category: TvShowCategory): MutableStateFlow<List<TvShow>> {
        return tvShowsFlows.getOrPut(category) { MutableStateFlow(emptyList()) }
    }

    /**
     * Observes TV shows for a specific category from in-memory storage.
     *
     * Returns a flow of TV shows from the in-memory cache. Automatically triggers
     * initial load if cache is empty for the given category.
     *
     * @param category The TV show category to observe
     * @return Flow emitting list of TV shows for the category
     */
    override suspend fun observeTvShows(category: TvShowCategory): Flow<List<TvShow>> {
        val flow = getFlowForCategory(category)

        if (flow.value.isEmpty()) {
            loadTvShowsNextPage(category)
        }

        return flow
    }

    /**
     * Loads the next page of TV shows for a specific category from the remote API.
     *
     * Ensures distinct TV shows by ID when adding new pages to prevent duplicates.
     *
     * @param category The TV show category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadTvShowsNextPage(category: TvShowCategory): AppResult<Unit> {
        val currentPage = currentPages[category] ?: 0
        val totalPage = totalPages[category] ?: 0

        if (totalPage in 1..currentPage) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = currentPage + 1

        return when (val result =
            remoteDataSource.fetchTvShowsPage(category.toTmdbPath(), nextPage, languageProvider.getCurrentLanguage())) {
            is AppResult.Success -> {
                totalPages[category] = result.data.totalPages
                val newTvShows = result.data.results.map { remoteTvShow ->
                    remoteTvShow.toDomain()
                }

                val flow = getFlowForCategory(category)
                flow.value = (flow.value + newTvShows).distinctBy { it.id }
                currentPages[category] = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Responds to language changes via the Observer Pattern.
     * Automatically called by LanguageChangeCoordinator when language changes.
     *
     * Implementation of LanguageChangeListener interface - delegates to clearAndReload().
     */
    override suspend fun onLanguageChanged() {
        clearAndReload()
    }

    /**
     * Clears all cached TV shows and reloads initial pages for all categories.
     *
     * This method is called when the app language changes via onLanguageChanged().
     * It clears the in-memory cache and fetches fresh data in the new language.
     */
    override suspend fun clearAndReload() {
        // Clear all pagination state and cached data for all categories
        currentPages.clear()
        totalPages.clear()
        tvShowsFlows.values.forEach { it.value = emptyList() }

        // Reload all categories in parallel
        coroutineScope {
            TvShowCategory.entries.forEach { category ->
                async { loadTvShowsNextPage(category) }
            }
        }
    }

    override suspend fun getTvShowDetails(tvShowId: Int): AppResult<TvShowDetails> =
        coroutineScope {
            val language = languageProvider.getCurrentLanguage()

            // Fetch details, videos, and credits in parallel
            val detailsDeferred =
                async { remoteDataSource.getTvShowDetails(tvShowId, language) }
            val videosDeferred =
                async { remoteDataSource.getTvShowVideos(tvShowId, language) }
            val creditsDeferred =
                async { remoteDataSource.getTvShowCredits(tvShowId, language) }

            val detailsResult = detailsDeferred.await()
            val videosResult = videosDeferred.await()
            val creditsResult = creditsDeferred.await()

            // Extract details or return error
            val details = when (detailsResult) {
                is AppResult.Success -> detailsResult.data
                is AppResult.Error -> return@coroutineScope detailsResult
            }

            // Map videos to domain and filter for trailers/teasers
            val trailers = when (videosResult) {
                is AppResult.Success -> {
                    videosResult.data.results
                        .filter { it.type == "Trailer" || it.type == "Teaser" }
                        .sortedWith(compareByDescending<RemoteVideo> { it.official }
                            .thenByDescending { it.publishedAt })
                        .map { it.toDomain() }
                }

                is AppResult.Error -> emptyList() // Videos are optional, don't fail if they error
            }

            // Map cast to domain and sort by order
            val cast = when (creditsResult) {
                is AppResult.Success -> {
                    creditsResult.data.cast
                        ?.sortedBy { it.order }
                        ?.map { remoteCastMember ->
                            remoteCastMember.toDomain()
                        }
                        ?: emptyList()
                }

                is AppResult.Error -> emptyList() // Cast is optional, don't fail if they error
            }

            // Combine details with trailers and cast
            val tvShowDetails = details.toDomain().copy(
                trailers = trailers,
                cast = cast
            )
            AppResult.Success(tvShowDetails)
        }
}

fun RemoteTvShow.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        posterPath = posterPath
    )
}


fun RemoteTvShowDetails.toDomain() = TvShowDetails(
    id = id,
    name = name,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    firstAirDate = firstAirDate,
    lastAirDate = lastAirDate,
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    episodeRunTime = episodeRunTime,
    status = status,
    tagline = tagline,
    type = type,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    originalName = originalName,
    originalLanguage = originalLanguage,
    originCountry = originCountry,
    homepage = homepage,
    inProduction = inProduction,
    languages = languages,
    genres = genres?.map { it.name },
    networks = networks?.mapNotNull { it.name },
    productionCompanies = productionCompanies?.map { it.name },
    productionCountries = productionCountries?.map { it.name },
    spokenLanguages = spokenLanguages?.map { it.englishName },
    seasonsCount = seasons?.size,
    createdBy = createdBy?.mapNotNull { it.name },
    lastEpisodeName = lastEpisodeToAir?.name,
    lastEpisodeAirDate = lastEpisodeToAir?.airDate,
    nextEpisodeToAir = nextEpisodeToAir?.airDate,
    nextEpisodeAirDate = null
)
