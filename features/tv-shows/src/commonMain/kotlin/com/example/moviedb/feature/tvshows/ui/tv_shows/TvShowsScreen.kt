package com.example.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb.core.model.Strings
import moe.tlaster.precompose.koin.koinViewModel


@Composable
fun TvShowsRoute(
    onClick: (id: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel(TvShowsViewModel::class)
    val uiState by viewModel.uiState.collectAsState()
    TvShowsScreen(
        uiState = uiState,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowsScreen(
    uiState: TvShowsViewModel.UiState,
    onClick: (id: Int, title: String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = Strings.tv_shows.get()) },
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = uiState.tvShows.isNotEmpty(),
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
                content = {
                    items(uiState.tvShows) {
                        TvShowCell(
                            tvShow = it,
                            onClick = onClick
                        )
                    }
                }
            )
        }
    }

}