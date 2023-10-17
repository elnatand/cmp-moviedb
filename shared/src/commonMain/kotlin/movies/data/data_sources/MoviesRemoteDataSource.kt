package movies.data.data_sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import movies.model.Movie
import movies.model.MoviesPage

class MoviesRemoteDataSource {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getMoviesPage(): List<Movie> {
        val moviesPages = httpClient
            .get("https://api.themoviedb.org/3/movie/popular?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body<MoviesPage>()
        return moviesPages.results
    }

    fun onCleared() {
        httpClient.close()
    }
}