package features.tv_shows.data.data_sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import features.tv_shows.model.TvShow
import features.tv_shows.model.TvShowDetails
import features.tv_shows.model.TvShowsPage

class TvShowsRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getMoviesPage(): List<TvShow> {
        val tvShowsPages = httpClient
            .get("https://api.themoviedb.org/3/movie/popular?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body<TvShowsPage>()
        return tvShowsPages.results
    }

    suspend fun getMovieDetails(tvShowId: Int): TvShowDetails {
        return httpClient
            .get("https://api.themoviedb.org/3/movie/${tvShowId}?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body()
    }
}