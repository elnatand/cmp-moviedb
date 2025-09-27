package com.elna.moviedb.feature.movies.ui.movie_details


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MovieDetailsViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getMovieDetails(movieId)
    }

    private fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            try {
                val movieDetails = moviesRepository.getMovieDetails(movieId)
                _uiState.value = MovieDetailsUiState.Success(movieDetails)
            } catch (e: Exception) {
                _uiState.value = MovieDetailsUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retry() {
        getMovieDetails(movieId)
    }

    sealed interface MovieDetailsUiState {
        data object Loading : MovieDetailsUiState
        data class Success(val movieDetails: MovieDetails) : MovieDetailsUiState
        data class Error(val message: String) : MovieDetailsUiState
    }
}