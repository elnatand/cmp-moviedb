package com.elna.moviedb.feature.person.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.formatDate
import com.elna.moviedb.feature.person.model.PersonUiState
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.also_known_as
import com.elna.moviedb.resources.biography
import com.elna.moviedb.resources.birthday
import com.elna.moviedb.resources.birthplace
import com.elna.moviedb.resources.deathday
import com.elna.moviedb.resources.gender
import com.elna.moviedb.resources.personal_details
import com.elna.moviedb.resources.place_of_birth
import com.elna.moviedb.resources.popularity
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PersonDetailsScreen(
    personId: Int
) {
    val viewModel = koinViewModel<PersonDetailsViewModel> { parametersOf(personId) }
    val uiState by viewModel.uiState.collectAsState()

    PersonDetailsScreen(
        uiState = uiState,
        onRetry = { viewModel.onEvent(com.elna.moviedb.feature.person.model.PersonDetailsEvent.Retry) }
    )
}

@Composable
private fun PersonDetailsScreen(
    uiState: PersonUiState,
    onRetry: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
    ) {
        when (uiState) {
            is PersonUiState.Loading -> AppLoader()

            is PersonUiState.Error -> AppErrorComponent(
                message = uiState.message,
                onRetry = onRetry
            )

            is PersonUiState.Success -> {
                PersonDetailsContent(person = uiState.person)
            }
        }
    }
}

@Composable
private fun PersonDetailsContent(person: PersonDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section with Profile Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Background with gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            // Profile Picture
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                person.profilePath?.let { profilePath ->
                    Card(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        ImageLoader(
                            imageUrl = profilePath,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Name
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Known For Department
                if (person.knownForDepartment.isNotEmpty()) {
                    Text(
                        text = person.knownForDepartment,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Main Content
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Quick Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                person.birthday?.let { birthday ->
                    InfoItem(
                        icon = Icons.Default.Cake,
                        label = stringResource(Res.string.birthday),
                        value = formatDate(birthday)
                    )
                }

                InfoItem(
                    icon = Icons.Default.Person,
                    label = stringResource(Res.string.gender),
                    value = person.gender
                )

                person.placeOfBirth?.let { place ->
                    InfoItem(
                        icon = Icons.Default.Place,
                        label = stringResource(Res.string.birthplace),
                        value = place.split(",").firstOrNull() ?: place
                    )
                }
            }

            // Biography Section
            if (person.biography.isNotEmpty()) {
                SectionCard(
                    title = stringResource(Res.string.biography),
                    content = {
                        Text(
                            text = person.biography,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                        )
                    }
                )
            }

            // Also Known As Section
            if (person.alsoKnownAs.isNotEmpty()) {
                SectionCard(
                    title = stringResource(Res.string.also_known_as),
                    content = {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            person.alsoKnownAs.take(10).forEach { alias ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(alias) }
                                )
                            }
                        }
                    }
                )
            }

            // Personal Details Section
            SectionCard(
                title = stringResource(Res.string.personal_details),
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        person.birthday?.let { birthday ->
                            DetailItem(
                                label = stringResource(Res.string.birthday),
                                value = formatDate(birthday)
                            )
                        }

                        person.deathday?.let { deathday ->
                            DetailItem(
                                label = stringResource(Res.string.deathday),
                                value = formatDate(deathday)
                            )
                        }

                        person.placeOfBirth?.let { place ->
                            DetailItem(
                                label = stringResource(Res.string.place_of_birth),
                                value = place
                            )
                        }

                        DetailItem(
                            label = stringResource(Res.string.gender),
                            value = person.gender
                        )

                        person.popularity?.let { popularity ->
                            DetailItem(
                                label = stringResource(Res.string.popularity),
                                value = "${(popularity * 10).toInt() / 10.0}"
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal
        )
    }
}