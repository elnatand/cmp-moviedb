package features.movies.data.data_sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import features.movies.model.Movie
import features.movies.model.MovieDetails
import features.movies.model.MoviesPage

class MoviesRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getMoviesPage(page:Int): List<Movie> {
        val moviesPages = httpClient
            .get("https://api.themoviedb.org/3/movie/popular?api_key=fe3e15709f26d5df026b17a743dbd529"){
                url{
                    parameters.append("page", page.toString())
                }
            }
            .body<MoviesPage>()
        return moviesPages.results
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return httpClient
            .get("https://api.themoviedb.org/3/movie/${movieId}?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body()
    }
}