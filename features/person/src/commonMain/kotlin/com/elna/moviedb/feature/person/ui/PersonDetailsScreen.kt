package com.elna.moviedb.feature.person.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.core.ui.utils.formatDate
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.feature.person.model.PersonDetailsEvent
import com.elna.moviedb.feature.person.model.PersonUiState
import com.elna.moviedb.feature.person.ui.components.DetailItem
import com.elna.moviedb.feature.person.ui.components.FilmographySection
import com.elna.moviedb.feature.person.ui.components.PersonHeroSection
import com.elna.moviedb.feature.person.ui.components.SectionCard
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
    personId: Int,
    onCreditClick: (Int, MediaType) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val viewModel = koinViewModel<PersonDetailsViewModel> { parametersOf(personId) }
    val uiState by viewModel.uiState.collectAsState()

    PersonDetailsScreen(
        uiState = uiState,
        onRetry = { viewModel.onEvent(PersonDetailsEvent.Retry) },
        onCreditClick = onCreditClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@Composable
private fun PersonDetailsScreen(
    uiState: PersonUiState,
    onRetry: () -> Unit,
    onCreditClick: (Int, MediaType) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState) {
            is PersonUiState.Loading -> AppLoader()

            is PersonUiState.Error -> AppErrorComponent(
                onRetry = onRetry
            )

            is PersonUiState.Success -> {
                PersonDetailsContent(
                    person = uiState.person,
                    onCreditClick = onCreditClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@Composable
private fun PersonDetailsContent(
    person: PersonDetails,
    onCreditClick: (Int, MediaType) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section with Profile Image
        PersonHeroSection(
            person = person,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )

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

            // Filmography Section
            FilmographySection(
                filmography = person.filmography,
                onCreditClick = onCreditClick
            )

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
                                value = place.split(",").lastOrNull()?.trim() ?: place
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
