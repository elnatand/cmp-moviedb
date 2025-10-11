package com.elna.moviedb.feature.profile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.feature.profile.model.ProfileEvent
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.arabic
import com.elna.moviedb.resources.cancel
import com.elna.moviedb.resources.change_language_message
import com.elna.moviedb.resources.change_language_title
import com.elna.moviedb.resources.confirm
import com.elna.moviedb.resources.english
import com.elna.moviedb.resources.hebrew
import com.elna.moviedb.resources.hindi
import com.elna.moviedb.resources.powered_by_tmdb
import com.elna.moviedb.resources.profile
import com.elna.moviedb.resources.select_language
import com.elna.moviedb.resources.select_theme
import com.elna.moviedb.resources.theme_dark
import com.elna.moviedb.resources.theme_light
import com.elna.moviedb.resources.theme_system
import com.elna.moviedb.resources.tmdb_attribution
import com.elna.moviedb.resources.tmdb_full_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel = koinViewModel<ProfileViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        selectedLanguage = uiState.selectedLanguageCode,
        selectedTheme = uiState.selectedThemeValue,
        onLanguageSelected = { viewModel.onEvent(ProfileEvent.SetLanguage(it)) },
        onThemeSelected = { viewModel.onEvent(ProfileEvent.SetTheme(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    selectedLanguage: String,
    selectedTheme: String,
    onLanguageSelected: (AppLanguage) -> Unit,
    onThemeSelected: (AppTheme) -> Unit
) {
    var languageExpanded by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingLanguage by remember { mutableStateOf<AppLanguage?>(null) }

    val selectedAppLanguage = AppLanguage.getAppLanguageByCode(selectedLanguage)
    val selectedNameRes = when (selectedAppLanguage) {
        AppLanguage.ENGLISH -> Res.string.english
        AppLanguage.HINDI -> Res.string.hindi
        AppLanguage.ARABIC -> Res.string.arabic
        AppLanguage.HEBREW -> Res.string.hebrew
    }

    // Confirmation Dialog
    if (showConfirmDialog && pendingLanguage != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                pendingLanguage = null
            },
            title = { Text(stringResource(Res.string.change_language_title)) },
            text = { Text(stringResource(Res.string.change_language_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingLanguage?.let { onLanguageSelected(it) }
                        showConfirmDialog = false
                        pendingLanguage = null
                    }
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        pendingLanguage = null
                    }
                ) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.profile)) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))

            // Language Selector
            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it }
            ) {
                OutlinedTextField(
                    value = stringResource(selectedNameRes),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.select_language)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    AppLanguage.entries.forEach { appLanguage ->
                        val nameRes = when (appLanguage) {
                            AppLanguage.ENGLISH -> Res.string.english
                            AppLanguage.HINDI -> Res.string.hindi
                            AppLanguage.ARABIC -> Res.string.arabic
                            AppLanguage.HEBREW -> Res.string.hebrew
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(nameRes)) },
                            onClick = {
                                languageExpanded = false
                                if (appLanguage.code != selectedLanguage) {
                                    pendingLanguage = appLanguage
                                    showConfirmDialog = true
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Theme Selector
            val selectedAppTheme = AppTheme.getAppThemeByValue(selectedTheme)
            val selectedThemeNameRes = when (selectedAppTheme) {
                AppTheme.LIGHT -> Res.string.theme_light
                AppTheme.DARK -> Res.string.theme_dark
                AppTheme.SYSTEM -> Res.string.theme_system
            }

            ExposedDropdownMenuBox(
                expanded = themeExpanded,
                onExpandedChange = { themeExpanded = it }
            ) {
                OutlinedTextField(
                    value = stringResource(selectedThemeNameRes),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.select_theme)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }
                ) {
                    AppTheme.entries.forEach { appTheme ->
                        val themeNameRes = when (appTheme) {
                            AppTheme.LIGHT -> Res.string.theme_light
                            AppTheme.DARK -> Res.string.theme_dark
                            AppTheme.SYSTEM -> Res.string.theme_system
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(themeNameRes)) },
                            onClick = {
                                themeExpanded = false
                                onThemeSelected(appTheme)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val uriHandler = LocalUriHandler.current
                    val tmdbUrl = "https://www.themoviedb.org"
                    val tmdbLogoUrl =
                        "https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg"

                    Text(
                        text = stringResource(Res.string.powered_by_tmdb),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ImageLoader(
                        imageUrl = tmdbLogoUrl,
                        modifier = Modifier.size(width = 170.dp, height = 120.dp).clickable {
                            uriHandler.openUri(tmdbUrl)
                        },
                        contentDescription = stringResource(Res.string.tmdb_full_name)
                    )

                    Text(
                        text = stringResource(Res.string.tmdb_full_name),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            uriHandler.openUri(tmdbUrl)
                        }
                    )

                    Text(
                        text = stringResource(Res.string.tmdb_attribution),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
