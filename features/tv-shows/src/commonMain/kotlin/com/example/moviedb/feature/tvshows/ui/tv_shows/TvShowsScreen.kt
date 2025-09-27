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
import com.example.moviedb.feature.tvshows.model.TvShowsUiState
import com.example.moviedb.resources.Res
import com.example.moviedb.resources.tv_shows
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun TvShowsScreen(
    onClick: (id: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel<TvShowsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    TvShowsScreen(
        uiState = uiState,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvShowsScreen(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.tv_shows)) },
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
                        TvShowTile(
                            tvShow = it,
                            onClick = onClick
                        )
                    }
                }
            )
        }
    }

}