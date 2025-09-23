package com.example.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.model.State
import com.example.moviedb.feature.movies.model.MoviesUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState(state = MoviesUiState.State.Loading))
    val uiState = _uiState.asStateFlow()

    init {
        loadMovies()
    }


    private fun loadMovies() {
        val initialPage = 1
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.observeAllMovies(initialPage).collect { movies ->
                _uiState.update { currentState ->
                    currentState.copy(
                        state = MoviesUiState.State.Success(movies),
                        hasMorePages = movies.isNotEmpty()
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if ( !currentState.hasMorePages) return

        _uiState.update { state ->
            state.copy(
                state = MoviesUiState.State.Loading,
                currentPage = state.currentPage + 1
            )
        }

        val nextPage = _uiState.value.currentPage

        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.loadPage(nextPage)
        }
    }
}
