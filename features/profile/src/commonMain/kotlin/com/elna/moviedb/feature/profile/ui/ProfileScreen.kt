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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class Language(
    val code: String,
    val nameRes: org.jetbrains.compose.resources.StringResource
)

@Composable
fun ProfileScreen() {
    val viewModel = koinViewModel<ProfileViewModel>()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    ProfileScreen(
        selectedLanguage = selectedLanguage,
        onLanguageSelected = viewModel::setLanguage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        Language("en", Res.string.english),
        Language("hi", Res.string.hindi),
        Language("ar", Res.string.arabic),
        Language("he", Res.string.hebrew)
    )

    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingLanguageCode by remember { mutableStateOf<String?>(null) }

    val selectedLanguageObj = languages.find { it.code == selectedLanguage } ?: languages[0]

    // Confirmation Dialog
    if (showConfirmDialog && pendingLanguageCode != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                pendingLanguageCode = null
            },
            title = { Text(stringResource(Res.string.change_language_title)) },
            text = { Text(stringResource(Res.string.change_language_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingLanguageCode?.let { onLanguageSelected(it) }
                        showConfirmDialog = false
                        pendingLanguageCode = null
                    }
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        pendingLanguageCode = null
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

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = stringResource(selectedLanguageObj.nameRes),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.select_language)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(stringResource(language.nameRes)) },
                            onClick = {
                                expanded = false
                                if (language.code != selectedLanguage) {
                                    pendingLanguageCode = language.code
                                    showConfirmDialog = true
                                }
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}
