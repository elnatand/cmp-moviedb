package com.elna.moviedb.feature.person.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.ui.utils.formatDate

@Composable
internal fun PersonInfoCard(person: PersonDetails) {
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
