package movies.ui.movies

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import movies.data.MoviesRepository
import movies.model.Movie
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

data class MoviesUiState(
    val movies: List<Movie> = emptyList()
)

class MoviesViewModel : ViewModel(), KoinComponent {

    private val moviesRepository: MoviesRepository = get()

    private val _uiState = MutableStateFlow(MoviesUiState())
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
}