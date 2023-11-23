package com.example.moviedb.feature.profile.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.koin.koinViewModel
import com.example.moviedb.ui.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ProfileRoute(

) {
    val viewModel = koinViewModel(ProfileViewModel::class)
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileViewModel.UiState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(MR.strings.profile)) },
            )
        }
    ) {
        AnimatedVisibility(
            visible = uiState.tvShows.isNotEmpty(),
            modifier = Modifier.padding(it)
        ) {
            Text(text = "Profile")
        }
    }
}