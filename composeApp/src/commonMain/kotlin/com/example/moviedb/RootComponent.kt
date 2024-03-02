package com.example.moviedb

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.data.movies.MoviesRepositoryImpl
import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsComponent
import com.example.moviedb.feature.movies.ui.movies.MoviesComponent
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.ScreenMovies,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            Configuration.ScreenMovies -> Child.ScreenMovies(
                MoviesComponent(
                    componentContext = context,
                )
            )

            is Configuration.ScreenMovieDetails -> Child.ScreenMovieDetails(
                MovieDetailsComponent(
                    movieId = 0,
                    componentContext = context,
                )
            )
        }
    }

    sealed class Child {
        data class ScreenMovies(val component: MoviesComponent) : Child()
        data class ScreenMovieDetails(val component: MovieDetailsComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object ScreenMovies : Configuration()

        @Serializable
        data class ScreenMovieDetails(val movieId: Int) : Configuration()
    }
}