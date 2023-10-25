import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import features.movies.data.MoviesRepository
import features.movies.data.MoviesRepositoryImpl
import features.movies.data.data_sources.MoviesRemoteDataSource
import features.movies.ui.movie_details.MovieDetailsViewModel
import features.movies.ui.movies.MoviesViewModel
import network.createHttpClient
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import ui.design_system.NavigationBar
import ui.navigation.RootNavGraph

@Composable
fun App(
    appState: AppState = rememberAppState()
) {
    KoinApplication(
        application = {
            modules(
                module {
                    single { createHttpClient() }
                    single { MoviesRemoteDataSource(httpClient = get()) }
                    single<MoviesRepository> { MoviesRepositoryImpl(moviesRemoteDataSource = get()) }

                    factory {
                        MoviesViewModel(
                            moviesRepository = get(),
                        )
                    }

                    factory { (id: Int) ->
                        MovieDetailsViewModel(
                            movieId = id,
                            moviesRepository = get()
                        )
                    }
                }
            )
        }
    ) {
        MaterialTheme {
            Scaffold(
                bottomBar = {
                    if (appState.shouldShowBottomBar()) {
                        NavigationBar(
                            topLevelDestinations = appState.topLevelDestinations,
                            onClick = appState::navigateToTopLevelDestination
                        )
                    }
                }
            ) {
                RootNavGraph()
            }
        }
    }

}

expect fun getPlatformName(): String