package com.example.moviedb.feature.tvshows.ui.tv_show_details

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

import org.koin.core.parameter.parametersOf
import com.example.moviedb.core.ui.extansions.mirror
import com.example.moviedb.core.ui.utils.ImageLoader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvShowDetailsScreen(
    tvShowId: Int,
    tvShowTitle: String,
    onBackPressed: () -> Unit,
) {
    val viewModel = koinViewModel<TvShowDetailsViewModel> { parametersOf(tvShowId) }
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        uiState.tvShowDetails?.let {
            ImageLoader(
                imageUrl = "$TMDB_IMAGE_URL${it.poster_path}",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}