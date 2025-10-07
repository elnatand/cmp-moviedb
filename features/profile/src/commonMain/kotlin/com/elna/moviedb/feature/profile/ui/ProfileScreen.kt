package com.elna.moviedb.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.arabic
import com.elna.moviedb.resources.cancel
import com.elna.moviedb.resources.change_language_message
import com.elna.moviedb.resources.change_language_title
import com.elna.moviedb.resources.confirm
import com.elna.moviedb.resources.english
import com.elna.moviedb.resources.hebrew
import com.elna.moviedb.resources.hindi
import com.elna.moviedb.resources.profile
import com.elna.moviedb.resources.select_language
import com.elna.moviedb.resources.select_theme
import com.elna.moviedb.resources.theme_dark
import com.elna.moviedb.resources.theme_light
import com.elna.moviedb.resources.theme_system
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel = koinViewModel<ProfileViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        selectedLanguage = uiState.selectedLanguageCode,
        selectedTheme = uiState.selectedThemeValue,
        onLanguageSelected = { viewModel.handleIntent(com.elna.moviedb.feature.profile.model.ProfileIntent.SetLanguage(it)) },
        onThemeSelected = { viewModel.handleIntent(com.elna.moviedb.feature.profile.model.ProfileIntent.SetTheme(it)) }
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
            modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

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
        }
    }
}
