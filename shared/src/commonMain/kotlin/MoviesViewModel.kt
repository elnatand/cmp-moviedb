import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Movie

data class MoviesUiState(
    val movies: List<Movie> = emptyList()
)

class MoviesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    init {
        updateMovies()
    }

    override fun onCleared() {
        httpClient.close()
    }

    private fun updateMovies() {
        viewModelScope.launch {
            val movies = getMovies()
            _uiState.update {
                it.copy(movies = movies)
            }
        }
    }

    private suspend fun getMovies(): List<Movie> {
        val movies = httpClient
            .get("movieDB url")
            .body<List<Movie>>()
        return movies
    }
}