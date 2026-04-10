package com.elna.moviedb.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Review
import com.elna.moviedb.core.model.ReviewsPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewsUiState(
    val reviews: List<Review> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val isLoadingInitial: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null
) {
    val canLoadMore: Boolean get() = !isLoadingMore && currentPage < totalPages
}

sealed interface ReviewsEvent {
    data object LoadMore : ReviewsEvent
    data object Retry : ReviewsEvent
}

class ReviewsViewModel(
    private val contentId: Int,
    private val isMovie: Boolean,
    private val moviesRepository: MoviesRepository,
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    init {
        loadNextPage()
    }

    fun onEvent(event: ReviewsEvent) {
        when (event) {
            ReviewsEvent.LoadMore -> loadNextPage()
            ReviewsEvent.Retry -> {
                _uiState.value = ReviewsUiState()
                loadNextPage()
            }
        }
    }

    private fun loadNextPage() {
        val state = _uiState.value
        if (!state.canLoadMore && state.currentPage > 0) return

        viewModelScope.launch {
            val nextPage = state.currentPage + 1
            _uiState.update {
                if (nextPage == 1) it.copy(isLoadingInitial = true, error = null)
                else it.copy(isLoadingMore = true, error = null)
            }

            val result: AppResult<ReviewsPage> =
                if (isMovie) moviesRepository.getMovieReviews(contentId, nextPage)
                else tvShowsRepository.getTvShowReviews(contentId, nextPage)

            when (result) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        reviews = it.reviews + result.data.reviews,
                        currentPage = result.data.page,
                        totalPages = result.data.totalPages,
                        isLoadingInitial = false,
                        isLoadingMore = false
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isLoadingInitial = false,
                        isLoadingMore = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
