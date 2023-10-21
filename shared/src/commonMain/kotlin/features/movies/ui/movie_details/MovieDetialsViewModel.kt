package features.movies.ui.movie_details

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import features.movies.data.MoviesRepository
import features.movies.model.MovieDetails
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MovieDetailsViewModel : ViewModel(), KoinComponent {

    private val moviesRepository: MoviesRepository = get()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun getMovieDetails(movieId: Int) {
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