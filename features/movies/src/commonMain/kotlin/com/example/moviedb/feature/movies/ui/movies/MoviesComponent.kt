package com.example.moviedb.feature.movies.ui.movies



import com.arkivanov.decompose.ComponentContext
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.State
import com.example.moviedb.core.model.UiState
import com.example.moviedb.feature.movies.componentCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MoviesComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    private val moviesRepository: MoviesRepository by inject()

    private val _uiState = MutableStateFlow(UiState<List<Movie>>())
    val uiState = _uiState.asStateFlow()

    private val page = 1

    init {
        getMovies()
    }

    private fun getMovies() {
        componentCoroutineScope().launch(Dispatchers.IO) {
             moviesRepository.observeMoviesPage(page).collect{ movies->
                _uiState.update {
                    it.copy(
                        state = State.SUCCESS,
                        data = movies
                    )
                }
            }
        }
    }
}


