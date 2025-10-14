package com.elna.moviedb.feature.person.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
        modifier = Modifier.fillMaxSize(),
    ) {
        when (uiState) {
            is PersonUiState.Loading -> AppLoader()

            is PersonUiState.Error -> AppErrorComponent(
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
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            // Profile Picture and Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Profile Image with Background
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    person.profilePath?.let { profilePath ->
                        // Blurred background image (full width)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(450.dp)
                        ) {
                            ImageLoader(
                                imageUrl = profilePath,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blur(25.dp),
                                contentDescription = null
                            )
                            // Overlay to dim the background
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                            )
                        }

                        // Main profile image (on top)
                        Box(
                            modifier = Modifier
                                .padding(top = 40.dp)
                                .width(240.dp)
                                .height(360.dp)
                                .shadow(16.dp, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            ImageLoader(
                                contentScale = ContentScale.Fit,
                                imageUrl = profilePath,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = person.name
                            )
                        }
                    } ?: Box(
                        modifier = Modifier
                            .width(240.dp)
                            .height(320.dp)
                            .shadow(16.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Name and Department Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = person.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (person.knownForDepartment.isNotEmpty()) {
                            Text(
                                text = person.knownForDepartment,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Quick Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            person.birthday?.let { birthday ->
                                QuickStatItem(
                                    icon = Icons.Default.Cake,
                                    value = formatDate(birthday)
                                )
                            }

                            person.placeOfBirth?.let { place ->
                                QuickStatItem(
                                    icon = Icons.Default.Place,
                                    value = place.split(",").lastOrNull()?.trim() ?: place
                                )
                            }

                            person.popularity?.let { popularity ->
                                QuickStatItem(
                                    icon = Icons.Default.Person,
                                    value = "${(popularity * 10).toInt() / 10.0}"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main Content
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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
private fun QuickStatItem(
    icon: ImageVector,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
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