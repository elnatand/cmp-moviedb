package com.example.moviedb.feature.movies.ui.movie_details


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class MovieDetailsViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        getMovieDetails(movieId)
    }

   private fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val movieDetails = moviesRepository.getMovieDetails(movieId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movieDetails = movieDetails,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun retry() {
        getMovieDetails(movieId)
    }

    data class UiState(
        val isLoading: Boolean = false,
        val movieDetails: MovieDetails? = null,
        val errorMessage: String? = null
    )
}