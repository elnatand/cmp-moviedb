package com.elna.moviedb.feature.person.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toProfileUrl

@Composable
internal fun PersonHeroSection(
    person: PersonDetails,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
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
                            imageUrl = profilePath.toProfileUrl(),
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
                        val imageModifier = Modifier.fillMaxSize()
                        val finalModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                            with(sharedTransitionScope) {
                                imageModifier
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "${SharedElementKeys.CAST_MEMBER}${person.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    )
                                    .skipToLookaheadSize()
                            }
                        } else {
                            imageModifier
                        }

                        ImageLoader(
                            contentScale = ContentScale.Crop,
                            imageUrl = profilePath.toProfileUrl(),
                            modifier = finalModifier,
                            contentDescription = person.name
                        )
                    }
                } ?: Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(360.dp)
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

            // Person Info Card
            PersonInfoCard(person = person)
        }
    }
}
