package com.example.moviedb.features.movies.ui.movies



import com.example.moviedb.data.movies.MoviesRepository
import com.example.moviedb.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import com.example.moviedb.model.State
import com.example.moviedb.model.UiState

class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Movie>>())
    val uiState = _uiState.asStateFlow()

    private val page = 1

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val movies = moviesRepository.getMoviesPage(page)
            delay(5000)
            _uiState.update {
                it.copy(
                    state = State.SUCCESS,
                    data = movies
                )
            }
        }
    }
}