package com.example.moviedb.feature.tvshows.ui.tv_show_details

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
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf
import com.example.moviedb.core.ui.extansions.mirror

@Composable
fun TvShowDetailsRoute(
    tvShowId: Int,
    tvShowTitle: String,
    onBackPressed: () -> Unit,
) {
    val viewModel = koinViewModel(TvShowDetailsViewModel::class) { parametersOf(tvShowId) }
    val uiState by viewModel.uiState.collectAsState()
    TvShowDetailsScreen(
        uiState = uiState,
        tvShowTitle = tvShowTitle,
        onBackPressed = onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailsScreen(
    uiState: TvShowDetailsViewModel.UiState,
    tvShowTitle: String,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = tvShowTitle) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            modifier = Modifier.mirror(),
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        uiState.tvShowDetails?.let {
            KamelImage(
                resource = asyncPainterResource("$TMDB_IMAGE_URL${it.poster_path}"),
                contentDescription = "",
            )
        }
    }
}