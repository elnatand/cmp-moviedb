package com.elna.moviedb.feature.movies.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.CastMember
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toProfileUrl

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CastMemberCard(
    castMember: CastMember,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .padding(2.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Profile Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val profileUrl = castMember.profilePath.toProfileUrl()
                if (profileUrl.isNotEmpty()) {
                    val imageModifier = Modifier.fillMaxSize()
                    val finalModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                        with(sharedTransitionScope) {
                            imageModifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "${SharedElementKeys.CAST_MEMBER}${castMember.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        spring(
                                            stiffness = 300f,
                                            dampingRatio = 0.8f
                                        )
                                    }
                                )
                                .skipToLookaheadSize()
                        }
                    } else {
                        imageModifier
                    }

                    ImageLoader(
                        imageUrl = profileUrl,
                        modifier = finalModifier
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Cast Info
            Column(
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = castMember.name,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = castMember.character,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
