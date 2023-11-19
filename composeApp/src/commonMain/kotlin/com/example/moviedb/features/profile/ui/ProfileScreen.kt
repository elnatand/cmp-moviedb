package com.example.moviedb.features.profile.ui

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
import com.example.moviedb.ui.strings.Strings

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
                title = { Text(text = Strings.profile.get()) },
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