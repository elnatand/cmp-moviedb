package com.example.moviedb.feature.movies.ui.movie_details


import com.arkivanov.decompose.ComponentContext
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.model.MovieDetails
import com.example.moviedb.feature.movies.componentCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MovieDetailsComponent(
    componentContext: ComponentContext,
    private val movieId: Int,

    ) : ComponentContext by componentContext, KoinComponent {

    private val moviesRepository: MoviesRepository by inject()
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        getMovieDetails(movieId)
    }

    private fun getMovieDetails(movieId: Int) {
        componentCoroutineScope().launch(Dispatchers.IO) {
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