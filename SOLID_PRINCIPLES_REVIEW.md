# SOLID Principles Review - CMP MovieDB Project

**Review Date:** 2025-10-16
**Project:** Kotlin Multiplatform Movie Database Application
**Architecture:** Clean Architecture with MVVM Pattern

---

## Executive Summary

This document provides a comprehensive analysis of SOLID principles adherence across the CMP MovieDB codebase. The project demonstrates strong architectural foundations with proper dependency injection, interface-based design, and Clean Architecture layering. However, several violations exist primarily around Single Responsibility Principle (SRP) and Open/Closed Principle (OCP), particularly in repository implementations and category-based code patterns.

### Severity Overview

| Principle | Violations | Severity | Status |
|-----------|------------|----------|--------|
| Single Responsibility Principle (SRP) | 5 major | 🔴 High | Needs Attention |
| Open/Closed Principle (OCP) | 3 major | 🔴 High | Needs Attention |
| Liskov Substitution Principle (LSP) | 0 | ✅ Good | Compliant |
| Interface Segregation Principle (ISP) | 3 major | 🟡 Medium | Improvement Needed |
| Dependency Inversion Principle (DIP) | 2 minor | 🟡 Low | Mostly Compliant |

---

## 1. Single Responsibility Principle (SRP)

> **Principle:** "A class should have only one reason to change"

### 🔴 Critical Violation #1: MoviesRepositoryImpl

**File:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt:45`
**Lines:** ~460 lines of code
**Severity:** 🔴 Critical

#### Problems Identified

The `MoviesRepositoryImpl` class handles multiple distinct responsibilities:

1. **Network Data Fetching** - Making API calls for 3 different categories
2. **Local Data Caching** - Managing database storage
3. **Pagination State Management** - Tracking pages for 3 categories
4. **Language Change Observation** - Listening and reacting to language changes
5. **Data Transformation** - Converting between entities and domain models
6. **Image URL Construction** - Building full image URLs
7. **Coroutine Scope Management** - Creating and managing its own scope

#### Code Example

```kotlin
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers
) : MoviesRepository {

    // Responsibility #7: Scope management
    private val repositoryScope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    init {
        // Responsibility #4: Language change observation
        repositoryScope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    clearMovies()
                    loadPopularMoviesNextPage()
                    loadTopRatedMoviesNextPage()
                    loadNowPlayingMoviesNextPage()
                }
        }
    }

    // Responsibility #1 & #2 & #3: Network, Cache, Pagination
    override suspend fun loadPopularMoviesNextPage(): AppResult<Unit> {
        // 33 lines handling network + cache + pagination
    }

    // Similar methods for TopRated and NowPlaying...
}
```

#### Impact

- **High Coupling:** Changes to any concern affect the entire repository
- **Testing Complexity:** Difficult to test individual concerns in isolation
- **Maintenance Burden:** 460 lines make it hard to understand and modify
- **Code Duplication:** Similar logic repeated for each category

#### Recommended Solution

Extract separate classes for each responsibility:

```kotlin
// Responsibility #1: Network Operations
class MoviesNetworkManager(
    private val remoteDataSource: MoviesRemoteDataSource
) {
    suspend fun fetchMoviesPage(
        category: MovieCategory,
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage>
}

// Responsibility #2: Local Caching
class MoviesCacheManager(
    private val localDataSource: MoviesLocalDataSource
) {
    suspend fun cacheMovies(movies: List<MovieEntity>)
    fun observeMovies(category: MovieCategory): Flow<List<MovieEntity>>
}

// Responsibility #3: Pagination Management
class MoviesPaginationManager(
    private val preferencesManager: PreferencesManager
) {
    suspend fun getCurrentPage(category: MovieCategory): Int
    suspend fun updatePagination(category: MovieCategory, state: PaginationState)
}

// Responsibility #4: Language Observer
class MoviesLanguageObserver(
    private val preferencesManager: PreferencesManager,
    private val repository: MoviesRepository
) {
    fun observeLanguageChanges(scope: CoroutineScope)
}

// Responsibility #5: Data Transformation
object MoviesDataMapper {
    fun toEntity(remote: RemoteMovie, category: MovieCategory): MovieEntity
    fun toDomain(entity: MovieEntity): Movie
}

// Simplified Repository (Coordinator)
class MoviesRepositoryImpl(
    private val networkManager: MoviesNetworkManager,
    private val cacheManager: MoviesCacheManager,
    private val paginationManager: MoviesPaginationManager,
    private val dataMapper: MoviesDataMapper
) : MoviesRepository {

    override suspend fun loadMoviesNextPage(
        category: MovieCategory
    ): AppResult<Unit> {
        val currentPage = paginationManager.getCurrentPage(category)
        val language = getCurrentLanguage()

        return when (val result = networkManager.fetchMoviesPage(category, currentPage + 1, language)) {
            is AppResult.Success -> {
                val entities = result.data.results.map { dataMapper.toEntity(it, category) }
                cacheManager.cacheMovies(entities)
                paginationManager.updatePagination(category, PaginationState(...))
                AppResult.Success(Unit)
            }
            is AppResult.Error -> result
        }
    }
}
```

---

### 🔴 Critical Violation #2: PreferencesManagerImpl

**File:** `core/datastore/src/commonMain/kotlin/com/elna/moviedb/core/datastore/PreferencesManagerImpl.kt:20`
**Lines:** ~160 lines
**Severity:** 🔴 High

#### Problems Identified

The class mixes two unrelated concerns:

1. **Application Settings** - Language and theme preferences
2. **Feature-Specific State** - Pagination state for 3 movie categories

#### Code Structure

```kotlin
internal class PreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesManager {

    private object PreferenceKeys {
        // Application settings
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")

        // Movie pagination state
        val POPULAR_MOVIES_CURRENT_PAGE = intPreferencesKey("popular_movies_current_page")
        val POPULAR_MOVIES_TOTAL_PAGES = intPreferencesKey("popular_movies_total_pages")
        val TOP_RATED_MOVIES_CURRENT_PAGE = intPreferencesKey("top_rated_movies_current_page")
        val TOP_RATED_MOVIES_TOTAL_PAGES = intPreferencesKey("top_rated_movies_total_pages")
        val NOW_PLAYING_MOVIES_CURRENT_PAGE = intPreferencesKey("now_playing_movies_current_page")
        val NOW_PLAYING_MOVIES_TOTAL_PAGES = intPreferencesKey("now_playing_movies_total_pages")
    }

    // Methods for app settings
    override fun getAppLanguageCode(): Flow<String>
    override suspend fun setAppLanguageCode(language: AppLanguage)
    override fun getAppTheme(): Flow<String>
    override suspend fun setAppTheme(theme: AppTheme)

    // Methods for movie pagination
    override fun getPopularMoviesPaginationState(): Flow<PaginationState>
    override suspend fun savePopularMoviesPaginationState(state: PaginationState)
    // ... 4 more pagination methods
}
```

#### Impact

- Profile screen depends on pagination methods it never uses
- Movie feature depends on theme/language methods it doesn't need
- Adding TV show pagination would further bloat this class
- Violates cohesion principles

#### Recommended Solution

```kotlin
// Separate interface for app settings
interface AppSettingsPreferences {
    fun getAppLanguageCode(): Flow<String>
    suspend fun setAppLanguageCode(language: AppLanguage)
    fun getAppTheme(): Flow<String>
    suspend fun setAppTheme(theme: AppTheme)
}

class AppSettingsPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : AppSettingsPreferences {
    // Implementation focused only on app settings
}

// Separate interface for pagination
interface PaginationPreferences {
    fun getPaginationState(category: String): Flow<PaginationState>
    suspend fun savePaginationState(category: String, state: PaginationState)
}

class PaginationPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : PaginationPreferences {
    // Generic implementation for any category
    override fun getPaginationState(category: String): Flow<PaginationState> {
        return dataStore.data.map { preferences ->
            PaginationState(
                currentPage = preferences[intPreferencesKey("${category}_current_page")] ?: 0,
                totalPages = preferences[intPreferencesKey("${category}_total_pages")] ?: 0
            )
        }
    }
}

// DI Module
val dataStoreModule = module {
    single<AppSettingsPreferences> { AppSettingsPreferencesImpl(get()) }
    single<PaginationPreferences> { PaginationPreferencesImpl(get()) }
}
```

---

### 🔴 Major Violation #3: MoviesViewModel

**File:** `features/movies/src/commonMain/kotlin/com/elna/moviedb/feature/movies/ui/movies/MoviesViewModel.kt:31`
**Lines:** ~186 lines
**Severity:** 🔴 High

#### Problems Identified

The ViewModel manages 3 different movie categories with nearly identical logic:

1. **Popular Movies** - Observation + Loading + State
2. **Top Rated Movies** - Observation + Loading + State
3. **Now Playing Movies** - Observation + Loading + State

#### Code Pattern

```kotlin
class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState(state = MoviesUiState.State.LOADING))
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        observeMovies()
    }

    private fun observeMovies() {
        // Duplicated pattern #1: Observe popular
        viewModelScope.launch {
            moviesRepository.observePopularMovies().collect { movies ->
                _uiState.update { currentState ->
                    val updated = currentState.copy(popularMovies = movies)
                    updated.copy(
                        state = if (updated.hasAnyData) MoviesUiState.State.SUCCESS
                                else MoviesUiState.State.LOADING
                    )
                }
            }
        }

        // Duplicated pattern #2: Observe top rated (24 lines, almost identical)
        viewModelScope.launch {
            moviesRepository.observeTopRatedMovies().collect { movies ->
                // Same logic as above
            }
        }

        // Duplicated pattern #3: Observe now playing (24 lines, almost identical)
        viewModelScope.launch {
            moviesRepository.observeNowPlayingMovies().collect { movies ->
                // Same logic as above
            }
        }
    }

    // Duplicated loading methods (22 lines each, nearly identical)
    private fun loadNextPagePopular() { /* ... */ }
    private fun loadNextPageTopRated() { /* ... */ }
    private fun loadNextPageNowPlaying() { /* ... */ }
}
```

#### Impact

- Code duplication across 3 methods (72 lines just for observation)
- Adding a 4th category requires copying and pasting entire blocks
- Bug fixes must be applied to all 3 methods
- Testing requires covering same logic 3 times

#### Recommended Solution

**Option 1: Generic Category Handler**

```kotlin
class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        observeCategory(
            MovieCategory.POPULAR,
            moviesRepository::observePopularMovies
        ) { state, movies -> state.copy(popularMovies = movies) }

        observeCategory(
            MovieCategory.TOP_RATED,
            moviesRepository::observeTopRatedMovies
        ) { state, movies -> state.copy(topRatedMovies = movies) }

        observeCategory(
            MovieCategory.NOW_PLAYING,
            moviesRepository::observeNowPlayingMovies
        ) { state, movies -> state.copy(nowPlayingMovies = movies) }
    }

    private fun observeCategory(
        category: MovieCategory,
        observeFlow: suspend () -> Flow<List<Movie>>,
        updateState: (MoviesUiState, List<Movie>) -> MoviesUiState
    ) {
        viewModelScope.launch {
            observeFlow().collect { movies ->
                _uiState.update { currentState ->
                    val updated = updateState(currentState, movies)
                    updated.copy(
                        state = if (updated.hasAnyData) State.SUCCESS else State.LOADING
                    )
                }
            }
        }
    }

    private fun loadNextPage(category: MovieCategory) {
        val loadingState = when(category) {
            POPULAR -> uiState.value.isLoadingPopular
            TOP_RATED -> uiState.value.isLoadingTopRated
            NOW_PLAYING -> uiState.value.isLoadingNowPlaying
        }

        if (loadingState) return

        viewModelScope.launch {
            updateLoadingState(category, true)

            val result = when(category) {
                POPULAR -> moviesRepository.loadPopularMoviesNextPage()
                TOP_RATED -> moviesRepository.loadTopRatedMoviesNextPage()
                NOW_PLAYING -> moviesRepository.loadNowPlayingMoviesNextPage()
            }

            handleLoadResult(category, result)
        }
    }
}
```

**Option 2: Category-Specific ViewModels (Better)**

```kotlin
// Base class with common logic
abstract class CategoryMoviesViewModel(
    protected val category: MovieCategory,
    protected val moviesRepository: MoviesRepository
) : ViewModel() {

    protected val _uiState = MutableStateFlow(CategoryMoviesUiState())
    val uiState: StateFlow<CategoryMoviesUiState> = _uiState.asStateFlow()

    init {
        observeMovies()
    }

    private fun observeMovies() {
        viewModelScope.launch {
            moviesRepository.observeMovies(category).collect { movies ->
                _uiState.update {
                    it.copy(
                        movies = movies,
                        state = if (movies.isNotEmpty()) State.SUCCESS else State.LOADING
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = moviesRepository.loadMoviesNextPage(category)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                is AppResult.Error -> {
                    handleError(result)
                }
            }
        }
    }
}

// Specific implementations (if needed for custom behavior)
class PopularMoviesViewModel(
    moviesRepository: MoviesRepository
) : CategoryMoviesViewModel(MovieCategory.POPULAR, moviesRepository)

class TopRatedMoviesViewModel(
    moviesRepository: MoviesRepository
) : CategoryMoviesViewModel(MovieCategory.TOP_RATED, moviesRepository)
```

---

### 🔴 Similar Violation: TvShowsViewModel

**File:** `features/tv-shows/src/commonMain/kotlin/com/elna/moviedb/feature/tvshows/ui/tv_shows/TvShowsViewModel.kt:31`
**Severity:** 🔴 High

Same issues as `MoviesViewModel` - manages 3 categories (Popular, TopRated, OnTheAir) with duplicated logic. Apply the same refactoring solutions.

---

### 🟡 Minor Violation #4: MoviesLocalDataSource

**File:** `core/database/src/commonMain/kotlin/com/elna/moviedb/core/database/MoviesLocalDataSource.kt:9`
**Severity:** 🟡 Medium

#### Problem

This class handles multiple types of data:
- Movies (basic list)
- Movie Details (detailed info)
- Videos/Trailers
- Cast Members

#### Recommended Solution

```kotlin
// Separate data sources
class MoviesListDataSource(private val movieDao: MovieDao)
class MovieDetailsDataSource(private val movieDetailsDao: MovieDetailsDao)
class MovieVideosDataSource(private val movieDetailsDao: MovieDetailsDao)
class MovieCastDataSource(private val movieDetailsDao: MovieDetailsDao)
```

---

## 2. Open/Closed Principle (OCP)

> **Principle:** "Software entities should be open for extension, closed for modification"

### 🔴 Critical Violation #1: Category-Based Method Duplication

**Files:**
- `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt`
- `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/tv_shows/TvShowRepositoryImpl.kt`

**Severity:** 🔴 Critical

#### Problem

Nearly identical methods exist for each category, making the code closed for extension:

```kotlin
// MoviesRepositoryImpl.kt
override suspend fun loadPopularMoviesNextPage(): AppResult<Unit> {
    val paginationState = preferencesManager.getPopularMoviesPaginationState().first()
    // ... 33 lines of logic
}

override suspend fun loadTopRatedMoviesNextPage(): AppResult<Unit> {
    val paginationState = preferencesManager.getTopRatedMoviesPaginationState().first()
    // ... 33 lines of nearly identical logic
}

override suspend fun loadNowPlayingMoviesNextPage(): AppResult<Unit> {
    val paginationState = preferencesManager.getNowPlayingMoviesPaginationState().first()
    // ... 33 lines of nearly identical logic
}
```

#### Impact

**To add a new category (e.g., "Upcoming Movies"), you must:**

1. ✏️ Modify `MoviesRepository` interface (add new method)
2. ✏️ Modify `MoviesRepositoryImpl` class (implement new method)
3. ✏️ Modify `PreferencesManager` interface (add pagination methods)
4. ✏️ Modify `PreferencesManagerImpl` class (implement pagination)
5. ✏️ Modify `MoviesRemoteDataSource` (add new API call)
6. ✏️ Modify `MoviesViewModel` (add observation + loading)
7. ✏️ Modify `MoviesUiState` (add new list property)
8. ✏️ Modify `MoviesEvent` (add new load event)

**Total: 8 classes modified** - Massive OCP violation!

#### Recommended Solution

**Use category abstraction:**

```kotlin
// Domain Model
enum class MovieCategory(val apiPath: String) {
    POPULAR("movie/popular"),
    TOP_RATED("movie/top_rated"),
    NOW_PLAYING("movie/now_playing"),
    UPCOMING("movie/upcoming") // New category - no code changes needed!
}

// Repository Interface
interface MoviesRepository {
    suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>>
    suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit>
    suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails>
    suspend fun refresh()
    suspend fun clearMovies()
}

// Repository Implementation
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val paginationPreferences: PaginationPreferences,
    private val appDispatchers: AppDispatchers
) : MoviesRepository {

    override suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>> {
        val localMoviesStream = moviesLocalDataSource.getMoviesByCategoryAsFlow(category.name)

        if (localMoviesStream.first().isEmpty()) {
            loadMoviesNextPage(category)
        }

        return localMoviesStream.map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
        val paginationState = paginationPreferences.getPaginationState(category.name).first()

        if (paginationState.currentPage >= paginationState.totalPages && paginationState.totalPages > 0) {
            return AppResult.Success(Unit)
        }

        val nextPage = paginationState.currentPage + 1
        val language = getCurrentLanguage()

        return when (val result = moviesRemoteDataSource.fetchMoviesPage(
            category.apiPath,
            nextPage,
            language
        )) {
            is AppResult.Success -> {
                val entities = result.data.results.map {
                    it.asEntity().copy(category = category.name)
                }
                moviesLocalDataSource.insertMoviesPage(entities)

                paginationPreferences.savePaginationState(
                    category.name,
                    PaginationState(nextPage, result.data.totalPages)
                )

                AppResult.Success(Unit)
            }
            is AppResult.Error -> result
        }
    }
}

// Remote Data Source (simplified)
class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {
    suspend fun fetchMoviesPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}$apiPath") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteMoviesPage>()
            }
        }
    }
}
```

**Benefits:**
- ✅ Adding new category requires **zero code changes** (just add enum value)
- ✅ Single implementation for all categories
- ✅ Easy to test
- ✅ Reduced code duplication by ~70%

---

### 🔴 Major Violation #2: SearchViewModel Filter Handling

**File:** `features/search/src/commonMain/kotlin/com/elna/moviedb/feature/search/ui/SearchViewModel.kt:121`
**Severity:** 🔴 High

#### Problem

```kotlin
private fun performSearch(
    query: String,
    filter: SearchFilter,
    page: Int,
    isLoadingMore: Boolean = false
) {
    viewModelScope.launch {
        // ... setup code

        val result = when (filter) {
            SearchFilter.ALL -> searchRepository.searchAll(query, page).first()

            SearchFilter.MOVIES -> {
                when (val movieResult = searchRepository.searchMovies(query, page).first()) {
                    is AppResult.Success -> AppResult.Success(movieResult.data.map { it as SearchResultItem })
                    is AppResult.Error -> movieResult
                }
            }

            SearchFilter.TV_SHOWS -> {
                when (val tvShowResult = searchRepository.searchTvShows(query, page).first()) {
                    is AppResult.Success -> AppResult.Success(tvShowResult.data.map { it as SearchResultItem })
                    is AppResult.Error -> tvShowResult
                }
            }

            SearchFilter.PEOPLE -> {
                when (val peopleResult = searchRepository.searchPeople(query, page).first()) {
                    is AppResult.Success -> AppResult.Success(peopleResult.data.map { it as SearchResultItem })
                    is AppResult.Error -> peopleResult
                }
            }
        }

        // ... handle result
    }
}
```

#### Impact

Adding a new search filter (e.g., `COLLECTIONS`) requires modifying this `when` expression - violates OCP.

#### Recommended Solution

**Strategy Pattern:**

```kotlin
// Strategy interface
interface SearchStrategy {
    suspend fun search(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>>
}

// Concrete strategies
class MovieSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override suspend fun search(query: String, page: Int) = flow {
        repository.searchMovies(query, page).collect { result ->
            when (result) {
                is AppResult.Success -> emit(AppResult.Success(result.data.map { it as SearchResultItem }))
                is AppResult.Error -> emit(result)
            }
        }
    }
}

class TvShowSearchStrategy(private val repository: SearchRepository) : SearchStrategy {
    override suspend fun search(query: String, page: Int) = flow {
        repository.searchTvShows(query, page).collect { result ->
            when (result) {
                is AppResult.Success -> emit(AppResult.Success(result.data.map { it as SearchResultItem }))
                is AppResult.Error -> emit(result)
            }
        }
    }
}

class PeopleSearchStrategy(private val repository: SearchRepository) : SearchStrategy {
    override suspend fun search(query: String, page: Int) = flow {
        repository.searchPeople(query, page).collect { result ->
            when (result) {
                is AppResult.Success -> emit(AppResult.Success(result.data.map { it as SearchResultItem }))
                is AppResult.Error -> emit(result)
            }
        }
    }
}

class AllSearchStrategy(private val repository: SearchRepository) : SearchStrategy {
    override suspend fun search(query: String, page: Int) =
        repository.searchAll(query, page)
}

// Sealed class with strategy
sealed class SearchFilter(val strategy: SearchStrategy) {
    class All(repository: SearchRepository) : SearchFilter(AllSearchStrategy(repository))
    class Movies(repository: SearchRepository) : SearchFilter(MovieSearchStrategy(repository))
    class TvShows(repository: SearchRepository) : SearchFilter(TvShowSearchStrategy(repository))
    class People(repository: SearchRepository) : SearchFilter(PeopleSearchStrategy(repository))
}

// Simplified ViewModel
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private fun performSearch(
        query: String,
        filter: SearchFilter,
        page: Int,
        isLoadingMore: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isLoadingMore,
                    isLoadingMore = isLoadingMore,
                    hasSearched = true,
                    errorMessage = null
                )
            }

            // No when expression needed - just use the strategy!
            val result = filter.strategy.search(query, page).first()

            handleSearchResult(result, isLoadingMore)
        }
    }
}
```

---

### 🔴 Major Violation #3: Remote Data Sources

**File:** `core/network/src/commonMain/kotlin/com/elna/moviedb/core/network/MoviesRemoteDataSource.kt:18`
**Severity:** 🟡 Medium

#### Problem

```kotlin
class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {
    suspend fun getPopularMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/popular", page, language)

    suspend fun getTopRatedMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/top_rated", page, language)

    suspend fun getNowPlayingMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/now_playing", page, language)

    // Private helper doing the actual work
    private suspend fun fetchMoviesPage(...)
}
```

#### Recommended Solution

Eliminate wrapper methods entirely:

```kotlin
class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {
    suspend fun fetchMoviesPage(
        apiPath: String,  // e.g., "movie/popular"
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}$apiPath") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteMoviesPage>()
            }
        }
    }

    // Keep specific methods only if they have unique logic
    suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails>
    suspend fun getMovieVideos(movieId: Int, language: String): AppResult<RemoteVideoResponse>
    suspend fun getMovieCredits(movieId: Int, language: String): AppResult<RemoteMovieCredits>
}
```

---

## 3. Liskov Substitution Principle (LSP)

> **Principle:** "Subtypes must be substitutable for their base types"

### ✅ **No Violations Found**

The codebase properly maintains contracts between interfaces and implementations:

- All repository implementations correctly fulfill their interface contracts
- No implementations throw unexpected exceptions
- No implementations violate preconditions or postconditions
- Proper use of sealed classes and inheritance

**Examples of Good LSP Adherence:**

```kotlin
// Interface contract
interface MoviesRepository {
    suspend fun observePopularMovies(): Flow<List<Movie>>
    suspend fun loadPopularMoviesNextPage(): AppResult<Unit>
}

// Implementation honors the contract
class MoviesRepositoryImpl(...) : MoviesRepository {
    // Properly implements all interface methods
    // Returns Flow<List<Movie>> as promised
    // Uses AppResult wrapper consistently
}
```

**Recommendation:** Continue maintaining this standard across the codebase.

---

## 4. Interface Segregation Principle (ISP)

> **Principle:** "Clients shouldn't depend on interfaces they don't use"

### 🔴 Major Violation #1: SearchRepository

**File:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/search/SearchRepository.kt:7`
**Severity:** 🟡 Medium

#### Problem

Fat interface forces all clients to depend on methods they don't use:

```kotlin
interface SearchRepository {
    fun searchMovies(query: String, page: Int): Flow<AppResult<List<SearchResultItem.MovieItem>>>
    fun searchTvShows(query: String, page: Int): Flow<AppResult<List<SearchResultItem.TvShowItem>>>
    fun searchPeople(query: String, page: Int): Flow<AppResult<List<SearchResultItem.PersonItem>>>
    fun searchAll(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>>
}
```

#### Impact

- A client searching only movies still depends on TV shows and people methods
- Changes to people search could trigger recompilation of movie search clients
- Testing becomes more complex (need to mock all methods)

#### Recommended Solution

**Option 1: Segregate by Entity Type**

```kotlin
// Focused interfaces
interface MovieSearchRepository {
    fun searchMovies(query: String, page: Int): Flow<AppResult<List<SearchResultItem.MovieItem>>>
}

interface TvShowSearchRepository {
    fun searchTvShows(query: String, page: Int): Flow<AppResult<List<SearchResultItem.TvShowItem>>>
}

interface PeopleSearchRepository {
    fun searchPeople(query: String, page: Int): Flow<AppResult<List<SearchResultItem.PersonItem>>>
}

interface MultiSearchRepository {
    fun searchAll(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>>
}

// Implementation can implement all if needed
class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val preferencesManager: PreferencesManager
) : MovieSearchRepository,
    TvShowSearchRepository,
    PeopleSearchRepository,
    MultiSearchRepository {
    // Implementation...
}

// DI
val dataModule = module {
    single<SearchRepositoryImpl> { SearchRepositoryImpl(get(), get()) }
    single<MovieSearchRepository> { get<SearchRepositoryImpl>() }
    single<TvShowSearchRepository> { get<SearchRepositoryImpl>() }
    single<PeopleSearchRepository> { get<SearchRepositoryImpl>() }
    single<MultiSearchRepository> { get<SearchRepositoryImpl>() }
}

// Client usage
class MovieSearchViewModel(
    private val movieSearchRepository: MovieSearchRepository // Only movie methods visible
) : ViewModel() {
    // Cannot accidentally call searchTvShows() or searchPeople()
}
```

**Option 2: Generic Search Repository**

```kotlin
interface SearchRepository {
    fun <T : SearchResultItem> search(
        type: SearchType<T>,
        query: String,
        page: Int
    ): Flow<AppResult<List<T>>>
}

sealed class SearchType<out T : SearchResultItem> {
    object Movies : SearchType<SearchResultItem.MovieItem>()
    object TvShows : SearchType<SearchResultItem.TvShowItem>()
    object People : SearchType<SearchResultItem.PersonItem>()
    object All : SearchType<SearchResultItem>()
}
```

---

### 🔴 Major Violation #2: PreferencesManager

**File:** `core/datastore/src/commonMain/kotlin/com/elna/moviedb/core/datastore/PreferencesManager.kt`
**Severity:** 🟡 Medium

#### Problem

Interface combines unrelated concerns (already discussed in SRP section):

```kotlin
interface PreferencesManager {
    // App settings methods
    fun getAppLanguageCode(): Flow<String>
    suspend fun setAppLanguageCode(language: AppLanguage)
    fun getAppTheme(): Flow<String>
    suspend fun setAppTheme(theme: AppTheme)

    // Movie pagination methods
    fun getPopularMoviesPaginationState(): Flow<PaginationState>
    suspend fun savePopularMoviesPaginationState(state: PaginationState)
    fun getTopRatedMoviesPaginationState(): Flow<PaginationState>
    suspend fun saveTopRatedMoviesPaginationState(state: PaginationState)
    fun getNowPlayingMoviesPaginationState(): Flow<PaginationState>
    suspend fun saveNowPlayingMoviesPaginationState(state: PaginationState)

    suspend fun clearAll()
}
```

#### Impact

```kotlin
// ProfileViewModel only needs app settings
class ProfileViewModel(
    private val preferencesManager: PreferencesManager // Depends on 9 unused methods!
) : ViewModel() {
    // Only uses getAppLanguageCode() and getAppTheme()
}

// MoviesRepository only needs pagination
class MoviesRepositoryImpl(
    private val preferencesManager: PreferencesManager // Depends on theme/language methods
) : MoviesRepository {
    // Only uses pagination methods
}
```

#### Solution

See SRP section for the recommended split.

---

### 🔴 Major Violation #3: MoviesRepository

**File:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepository.kt`
**Severity:** 🟡 Medium

#### Problem

Interface has too many responsibilities:

```kotlin
interface MoviesRepository {
    // Observation methods (3)
    suspend fun observePopularMovies(): Flow<List<Movie>>
    suspend fun observeTopRatedMovies(): Flow<List<Movie>>
    suspend fun observeNowPlayingMovies(): Flow<List<Movie>>

    // Loading methods (3)
    suspend fun loadPopularMoviesNextPage(): AppResult<Unit>
    suspend fun loadTopRatedMoviesNextPage(): AppResult<Unit>
    suspend fun loadNowPlayingMoviesNextPage(): AppResult<Unit>

    // Details methods (1)
    suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails>

    // Utility methods (2)
    suspend fun refresh(): AppResult<List<Movie>>
    suspend fun clearMovies()
}
```

#### Impact

- Movie details screen depends on pagination methods it doesn't use
- Movies list screen depends on details method it doesn't use

#### Recommended Solution

**Option 1: Split by feature area**

```kotlin
interface MoviesListRepository {
    suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>>
    suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit>
    suspend fun refresh(): AppResult<List<Movie>>
    suspend fun clearMovies()
}

interface MovieDetailsRepository {
    suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails>
}
```

**Option 2: Keep combined but use delegation**

```kotlin
interface MoviesRepository : MoviesListRepository, MovieDetailsRepository

class MoviesRepositoryImpl(
    private val listRepository: MoviesListRepository,
    private val detailsRepository: MovieDetailsRepository
) : MoviesRepository,
    MoviesListRepository by listRepository,
    MovieDetailsRepository by detailsRepository
```

---

## 5. Dependency Inversion Principle (DIP)

> **Principle:** "Depend on abstractions, not concretions"

### ✅ **Generally Strong Adherence**

The project demonstrates excellent DIP practices:

**Strengths:**
- ✅ ViewModels depend on repository **interfaces**, not implementations
- ✅ Proper use of Koin for dependency injection throughout
- ✅ Clean Architecture layers with proper abstractions
- ✅ Repository pattern properly implemented
- ✅ Network and database details abstracted away from business logic

**Example of Good DIP:**

```kotlin
// ViewModel depends on abstraction
class MoviesViewModel(
    private val moviesRepository: MoviesRepository  // Interface, not impl
) : ViewModel()

// DI Module wires up the concrete implementation
val moviesModule = module {
    factory {
        MoviesViewModel(
            moviesRepository = get()  // Koin resolves to MoviesRepositoryImpl
        )
    }
}

val dataModule = module {
    single<MoviesRepository> {  // Interface
        MoviesRepositoryImpl(  // Concrete implementation
            moviesRemoteDataSource = get(),
            moviesLocalDataSource = get(),
            preferencesManager = get(),
            appDispatchers = get()
        )
    }
}
```

---

### 🟡 Minor Issue #1: Repository Creates Own Scope

**File:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt:56`
**Severity:** 🟡 Low

#### Problem

```kotlin
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers
) : MoviesRepository {

    // Creates concrete CoroutineScope - hard to test
    private val repositoryScope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    init {
        repositoryScope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    clearMovies()
                    loadPopularMoviesNextPage()
                    loadTopRatedMoviesNextPage()
                    loadNowPlayingMoviesNextPage()
                }
        }
    }
}
```

#### Impact

- Difficult to test language change observer behavior
- Scope lifecycle tied to repository implementation
- Cannot inject different scope for testing

#### Recommended Solution

**Inject the scope as a dependency:**

```kotlin
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers,
    private val repositoryScope: CoroutineScope  // Injected dependency
) : MoviesRepository {

    init {
        repositoryScope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    clearMovies()
                    loadPopularMoviesNextPage()
                    loadTopRatedMoviesNextPage()
                    loadNowPlayingMoviesNextPage()
                }
        }
    }
}

// DI Module
val dataModule = module {
    single<MoviesRepository> {
        MoviesRepositoryImpl(
            moviesRemoteDataSource = get(),
            moviesLocalDataSource = get(),
            preferencesManager = get(),
            appDispatchers = get(),
            repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        )
    }
}

// Test
@Test
fun `test language change clears movies`() = runTest {
    val testScope = TestScope(UnconfinedTestDispatcher())

    val repository = MoviesRepositoryImpl(
        moviesRemoteDataSource = mockRemoteDataSource,
        moviesLocalDataSource = mockLocalDataSource,
        preferencesManager = mockPreferences,
        appDispatchers = testDispatchers,
        repositoryScope = testScope  // Test scope injection
    )

    // Test behavior...
}
```

---

### 🟡 Minor Issue #2: Data Sources are Concrete Classes

**Files:**
- `core/database/src/commonMain/kotlin/com/elna/moviedb/core/database/MoviesLocalDataSource.kt:9`
- `core/network/src/commonMain/kotlin/com/elna/moviedb/core/network/MoviesRemoteDataSource.kt:18`

**Severity:** 🟡 Low

#### Problem

Data sources are concrete classes, not interfaces:

```kotlin
// Concrete class, not interface
class MoviesLocalDataSource(
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao,
) {
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>
    suspend fun insertMoviesPage(movies: List<MovieEntity>)
    // ...
}

// Repository depends on concrete class
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,  // Concrete
    private val moviesLocalDataSource: MoviesLocalDataSource,    // Concrete
    // ...
) : MoviesRepository
```

#### Impact

- Harder to mock in unit tests
- Cannot easily swap implementations
- Couples repository to specific data source implementations

#### Recommended Solution

```kotlin
// Create interface
interface MoviesLocalDataSource {
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>
    suspend fun insertMoviesPage(movies: List<MovieEntity>)
    suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity?
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)
    suspend fun clearAllMovies()
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>
    suspend fun insertVideos(videos: List<VideoEntity>)
    suspend fun deleteVideosForMovie(movieId: Int)
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}

// Implementation
class MoviesLocalDataSourceImpl(
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao,
) : MoviesLocalDataSource {
    override fun getMoviesByCategoryAsFlow(category: String) =
        movieDao.getMoviesByCategoryAsFlow(category)

    override suspend fun insertMoviesPage(movies: List<MovieEntity>) {
        movies.forEach { movieDao.insertMovie(it) }
    }

    // ... other implementations
}

// DI Module
val databaseModule = module {
    single<MoviesLocalDataSource> {
        MoviesLocalDataSourceImpl(
            movieDao = get(),
            movieDetailsDao = get()
        )
    }
}

// Testing becomes easier
@Test
fun `test load movies`() = runTest {
    val mockLocalDataSource = mockk<MoviesLocalDataSource>()

    val repository = MoviesRepositoryImpl(
        moviesLocalDataSource = mockLocalDataSource,
        // ...
    )

    coEvery { mockLocalDataSource.insertMoviesPage(any()) } just Runs
    // Test...
}
```

**Apply the same pattern to:**
- `MoviesRemoteDataSource`
- `TvShowsRemoteDataSource`
- `SearchRemoteDataSource`
- `PersonRemoteDataSource`

---

## Priority Action Plan

### 🔴 Phase 1: High Priority (Weeks 1-2)

These violations have the highest impact on maintainability and should be addressed first.

#### 1.1 Extract Pagination Management
**Estimated Effort:** 4-6 hours
**Files Affected:**
- `core/datastore/src/commonMain/kotlin/com/elna/moviedb/core/datastore/PreferencesManager.kt`
- `core/datastore/src/commonMain/kotlin/com/elna/moviedb/core/datastore/PreferencesManagerImpl.kt`

**Steps:**
1. Create `PaginationPreferences` interface
2. Create `PaginationPreferencesImpl` implementation
3. Update DI modules
4. Update repositories to use new interface
5. Create tests for pagination preferences

**Before:**
```kotlin
interface PreferencesManager {
    fun getAppLanguageCode(): Flow<String>
    fun getAppTheme(): Flow<String>
    fun getPopularMoviesPaginationState(): Flow<PaginationState>
    fun getTopRatedMoviesPaginationState(): Flow<PaginationState>
    fun getNowPlayingMoviesPaginationState(): Flow<PaginationState>
    // ...
}
```

**After:**
```kotlin
interface AppSettingsPreferences {
    fun getAppLanguageCode(): Flow<String>
    fun getAppTheme(): Flow<String>
    suspend fun setAppLanguageCode(language: AppLanguage)
    suspend fun setAppTheme(theme: AppTheme)
}

interface PaginationPreferences {
    fun getPaginationState(category: String): Flow<PaginationState>
    suspend fun savePaginationState(category: String, state: PaginationState)
    suspend fun clearPaginationState(category: String)
}
```

#### 1.2 Introduce Category Abstraction
**Estimated Effort:** 8-12 hours
**Files Affected:**
- All repository interfaces and implementations
- All ViewModels
- All UI state classes

**Steps:**
1. Create `MovieCategory` enum
2. Update repository interfaces to use category parameter
3. Refactor repository implementations
4. Update ViewModels to use category-based approach
5. Update tests

**Benefits:**
- Reduces code duplication by ~70%
- Makes adding new categories trivial
- Improves testability

#### 1.3 Split MoviesRepositoryImpl Responsibilities
**Estimated Effort:** 12-16 hours
**Files Affected:**
- `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt`
- New files for extracted managers

**Steps:**
1. Extract `MoviesLanguageObserver` class
2. Extract data mapping functions to object/utility
3. Update repository to use composition
4. Create tests for each component
5. Apply same pattern to `TvShowsRepositoryImpl`

---

### 🟡 Phase 2: Medium Priority (Weeks 3-4)

#### 2.1 Segregate Repository Interfaces
**Estimated Effort:** 6-8 hours
**Files Affected:**
- Repository interfaces
- ViewModels
- DI modules

**Steps:**
1. Split `SearchRepository` by entity type
2. Consider splitting `MoviesRepository` by feature area
3. Update DI modules
4. Update client code
5. Update tests

#### 2.2 Refactor ViewModel Category Logic
**Estimated Effort:** 8-10 hours
**Files Affected:**
- `MoviesViewModel.kt`
- `TvShowsViewModel.kt`

**Options:**
- Create base `CategoryViewModel` class
- Use generic category handler
- Split into multiple category-specific ViewModels

#### 2.3 Implement Strategy Pattern for Search
**Estimated Effort:** 4-6 hours
**Files Affected:**
- `SearchViewModel.kt`
- New strategy classes

**Steps:**
1. Create `SearchStrategy` interface
2. Implement concrete strategies
3. Update `SearchFilter` sealed class
4. Refactor `SearchViewModel`
5. Update tests

---

### 🟢 Phase 3: Low Priority (Future Enhancement)

#### 3.1 Create Data Source Interfaces
**Estimated Effort:** 4-6 hours
**Files Affected:**
- All data source classes
- Repository implementations
- DI modules

**Benefits:**
- Improved testability
- Better mocking support
- More flexible architecture

#### 3.2 Inject Repository Scope
**Estimated Effort:** 2-3 hours
**Files Affected:**
- Repository implementations
- DI modules
- Tests

---

## Testing Strategy

### Unit Test Coverage Improvements

After implementing SOLID refactorings, update tests to cover:

1. **Category-Based Logic**
```kotlin
@Test
fun `load movies for all categories`() = runTest {
    MovieCategory.values().forEach { category ->
        val result = repository.loadMoviesNextPage(category)
        assertTrue(result is AppResult.Success)
    }
}
```

2. **Pagination Manager**
```kotlin
@Test
fun `pagination manager handles multiple categories`() = runTest {
    paginationManager.savePaginationState("POPULAR", PaginationState(1, 10))
    paginationManager.savePaginationState("TOP_RATED", PaginationState(2, 15))

    val popularState = paginationManager.getPaginationState("POPULAR").first()
    assertEquals(1, popularState.currentPage)

    val topRatedState = paginationManager.getPaginationState("TOP_RATED").first()
    assertEquals(2, topRatedState.currentPage)
}
```

3. **Search Strategies**
```kotlin
@Test
fun `movie search strategy returns movies only`() = runTest {
    val strategy = MovieSearchStrategy(searchRepository)
    val results = strategy.search("test", 1).first()

    assertTrue(results is AppResult.Success)
    assertTrue(results.data.all { it is SearchResultItem.MovieItem })
}
```

---

## Metrics & Goals

### Current State
- **Average Class Lines:** 200-460 lines
- **Code Duplication:** ~40% across categories
- **Interface Methods:** 8-11 methods per interface
- **Test Coverage:** Unknown (needs measurement)

### Target State
- **Average Class Lines:** 100-200 lines
- **Code Duplication:** <10%
- **Interface Methods:** 3-5 methods per interface
- **Test Coverage:** >80% for business logic

### Success Metrics
- ✅ New category can be added without modifying existing code
- ✅ Repository classes under 300 lines
- ✅ No interface has more than 5 methods
- ✅ Pagination logic centralized in single class
- ✅ ViewModels can be tested without database or network

---

## Additional Recommendations

### 1. Consider Use Cases / Interactors Layer

For complex operations, consider adding a use cases layer between ViewModels and Repositories:

```kotlin
// Domain layer use case
class LoadMoviesByCategory(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(category: MovieCategory): AppResult<Unit> {
        return repository.loadMoviesNextPage(category)
    }
}

// ViewModel uses use case instead of repository directly
class MoviesViewModel(
    private val loadMoviesByCategory: LoadMoviesByCategory
) : ViewModel() {
    fun loadMovies(category: MovieCategory) {
        viewModelScope.launch {
            loadMoviesByCategory(category)
        }
    }
}
```

**Benefits:**
- Keeps ViewModels thin
- Reusable business logic
- Easier to test
- Better separation of concerns

### 2. Consider Repository Composition

Instead of god repositories, use composition:

```kotlin
interface MoviesRepository {
    val list: MoviesListOperations
    val details: MovieDetailsOperations
    val pagination: MoviesPaginationOperations
}

class MoviesRepositoryImpl(
    override val list: MoviesListOperations,
    override val details: MovieDetailsOperations,
    override val pagination: MoviesPaginationOperations
) : MoviesRepository
```

### 3. Document Architectural Decisions

Create ADR (Architecture Decision Records) for:
- Why category-based approach was chosen
- Pagination strategy decisions
- Cache-first vs Network-first strategies
- State management patterns

### 4. Set Up Architecture Tests

Use tools like ArchUnit or custom linting to enforce:
- Maximum class size
- Maximum method count per interface
- Dependency direction rules
- Package structure rules

```kotlin
// Example with Konsist or similar
@Test
fun `repositories should not depend on ViewModels`() {
    Konsist
        .scopeFromProject()
        .classes()
        .withNameEndingWith("Repository")
        .assertNot { it.dependsOn("ViewModel") }
}
```

---

## Conclusion

The CMP MovieDB project demonstrates solid architectural foundations with proper layering, dependency injection, and interface-based design. The main areas for improvement are:

1. **Breaking down large classes** (SRP violations)
2. **Eliminating category-based duplication** (OCP violations)
3. **Segregating fat interfaces** (ISP violations)

Implementing the recommended refactorings will result in:
- ✅ **More maintainable code** - Easier to understand and modify
- ✅ **Better testability** - Smaller, focused units
- ✅ **Reduced duplication** - DRY principle adherence
- ✅ **Easier feature addition** - Open for extension
- ✅ **Clearer responsibilities** - Single purpose classes

The priority action plan provides a structured approach to addressing these issues over 4-6 weeks, starting with the highest-impact changes.

---

## References

- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Repository Pattern](https://martinfowler.com/eaaCatalog/repository.html)

---

**Document Version:** 1.0
**Last Updated:** 2025-10-16
**Author:** SOLID Principles Review Analysis
**Next Review:** After Phase 1 completion
