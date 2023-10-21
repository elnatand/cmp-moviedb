package features.tv_shows

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import features.movies.data.MoviesRepository
import features.movies.model.Movie
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class TvShowsViewModel : ViewModel(), KoinComponent {

    private val moviesRepository: MoviesRepository = get()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateMovies()
    }

    private fun updateMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val movies = moviesRepository.getMoviesPage()
            _uiState.update {
                it.copy(movies = movies)
            }
        }
    }

    data class UiState(
        val movies: List<Movie> = emptyList()
    )

}