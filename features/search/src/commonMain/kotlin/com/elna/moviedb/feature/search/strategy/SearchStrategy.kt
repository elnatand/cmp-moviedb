package com.elna.moviedb.feature.search.strategy

import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchResultItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Strategy interface for different search filter types.
 * Following the Strategy Pattern to eliminate OCP violations in SearchViewModel.
 *
 * Each strategy encapsulates the logic for searching a specific type of content
 * and mapping results to SearchResultItem.
 */
interface SearchStrategy {
    /**
     * Performs a search for the specific content type.
     *
     * @param query The search query string
     * @param page The page number to fetch
     * @return Flow emitting AppResult with list of SearchResultItem
     */
    fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>>
}

/**
 * Strategy for searching all content types (movies, TV shows, and people).
 */
class AllSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.searchAll(query, page)
    }
}

/**
 * Strategy for searching only movies.
 */
class MovieSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.searchMovies(query, page).map { result ->
            when (result) {
                is AppResult.Success -> AppResult.Success(result.data.map { it as SearchResultItem })
                is AppResult.Error -> result
            }
        }
    }
}

/**
 * Strategy for searching only TV shows.
 */
class TvShowSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.searchTvShows(query, page).map { result ->
            when (result) {
                is AppResult.Success -> AppResult.Success(result.data.map { it as SearchResultItem })
                is AppResult.Error -> result
            }
        }
    }
}

/**
 * Strategy for searching only people.
 */
class PeopleSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.searchPeople(query, page).map { result ->
            when (result) {
                is AppResult.Success -> AppResult.Success(result.data.map { it as SearchResultItem })
                is AppResult.Error -> result
            }
        }
    }
}
