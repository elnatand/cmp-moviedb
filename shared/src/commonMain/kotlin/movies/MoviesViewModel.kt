package movies

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import movies.data.MoviesRepository
import movies.model.Movie

data class MoviesUiState(
    val movies: List<Movie> = emptyList()
)

class MoviesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()
    private val moviesRepository = MoviesRepository()

    init {
        updateMovies()
    }

    override fun onCleared() {
        moviesRepository.onCleared()
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