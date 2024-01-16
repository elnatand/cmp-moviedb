package com.example.moviedb.feature.movies.ui.movie_details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import com.example.moviedb.core.ui.extansions.mirror
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieDetailsRoute(
    movieId: Int,
    onBackPressed: () -> Unit,
    title: String,
) {
    val viewModel = koinViewModel(MovieDetailsViewModel::class) { parametersOf(movieId) }
    val uiState by viewModel.uiState.collectAsState()
    MovieDetailsScreen(
        uiState = uiState,
        title = title,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsViewModel.UiState,
    onBackPressed: () -> Unit,
    title: String,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            modifier = Modifier.mirror(),
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }
    ) {
        uiState.movieDetails?.let {
            KamelImage(
                resource = asyncPainterResource("$TMDB_IMAGE_URL${it.poster_path}"),
                contentDescription = "",
            )
        }
    }
}