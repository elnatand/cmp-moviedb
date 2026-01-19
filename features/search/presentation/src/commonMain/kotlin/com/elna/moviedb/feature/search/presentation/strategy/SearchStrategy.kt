package com.elna.moviedb.feature.search.presentation.strategy



import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.domain.repository.SearchRepository
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import kotlinx.coroutines.flow.Flow

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
 * Uses category-based repository following OCP.
 */
class AllSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.search(SearchFilter.ALL, query, page)
    }
}

/**
 * Strategy for searching only movies.
 * Uses category-based repository following OCP.
 */
class MovieSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.search(SearchFilter.MOVIES, query, page)
    }
}

/**
 * Strategy for searching only TV shows.
 * Uses category-based repository following OCP.
 */
class TvShowSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.search(SearchFilter.TV_SHOWS, query, page)
    }
}

/**
 * Strategy for searching only people.
 * Uses category-based repository following OCP.
 */
class PeopleSearchStrategy(
    private val repository: SearchRepository
) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.search(SearchFilter.PEOPLE, query, page)
    }
}
