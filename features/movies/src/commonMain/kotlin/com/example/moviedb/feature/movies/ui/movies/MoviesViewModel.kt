package com.example.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.core.data.movies.MoviesRepository
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

    private val _uiState = MutableStateFlow(MoviesUiState(state = MoviesUiState.State.LOADING))
    val uiState = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        val initialPage = 1
        viewModelScope.launch {
            moviesRepository.observeAllMovies(initialPage).collect { movies ->
                _uiState.update { currentState ->
                    currentState.copy(
                        state = MoviesUiState.State.SUCCESS,
                        movies = movies
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        _uiState.update { state ->
            state.copy(
                state = MoviesUiState.State.LOADING,
                currentPage = state.currentPage + 1
            )
        }

        val nextPage = _uiState.value.currentPage

        viewModelScope.launch {
            moviesRepository.loadPage(nextPage)
        }
    }
}
