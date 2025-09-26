package com.example.moviedb.feature.movies.ui.movie_details


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class MovieDetailsViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getMovieDetails(movieId)
    }

   private fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val movieDetails = moviesRepository.getMovieDetails(movieId)
                _uiState.value = UiState.Success(movieDetails)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retry() {
        getMovieDetails(movieId)
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val movieDetails: MovieDetails) : UiState
        data class Error(val message: String) : UiState
    }
}