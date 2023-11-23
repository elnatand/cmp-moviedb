package com.example.moviedb.feature.movies.ui.movie_details


import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.model.MovieDetails
import moe.tlaster.precompose.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope


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
        viewModelScope.launch(Dispatchers.IO) {
            val movieDetails = moviesRepository.getMovieDetails(movieId)
            _uiState.update {
                it.copy(movieDetails = movieDetails)
            }
        }
    }

    data class UiState(
        val movieDetails: MovieDetails? = null
    )
}