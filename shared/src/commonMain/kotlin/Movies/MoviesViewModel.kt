package Movies

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import Movies.model.Movie
import Movies.model.MoviesPage

data class MoviesUiState(
    val movies: List<Movie> = emptyList()
)

class MoviesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
    }

    init {
        updateMovies()
    }

    override fun onCleared() {
        httpClient.close()
    }

    private fun updateMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val movies = getMovies()
            _uiState.update {
                it.copy(movies = movies)
            }
        }
    }

    private suspend fun getMovies(): List<Movie> {
        val moviesPages = httpClient
            .get("https://api.themoviedb.org/3/movie/popular?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body<MoviesPage>()
        return moviesPages.results
    }
}