package features.movies.ui.movies


import features.movies.data.MoviesRepository
import features.movies.model.Movie
import features.movies.model.MoviesPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val page = 1

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val movies = moviesRepository.getMoviesPage(page)
            _uiState.update {
                it.copy(movies = movies)
            }
        }
    }

    data class UiState(
        val movies: List<Movie> = emptyList()
    )
}