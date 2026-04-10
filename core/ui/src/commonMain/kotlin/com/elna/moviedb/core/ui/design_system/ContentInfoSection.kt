package com.elna.moviedb.core.ui.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.close
import com.elna.moviedb.resources.content_info_title
import com.elna.moviedb.resources.rating_description_g
import com.elna.moviedb.resources.rating_description_nc17
import com.elna.moviedb.resources.rating_description_pg
import com.elna.moviedb.resources.rating_description_pg13
import com.elna.moviedb.resources.rating_description_r
import com.elna.moviedb.resources.rating_description_unknown
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentInfoSection(
    ageRating: String?,
    contentDescriptors: List<String>,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = stringResource(Res.string.content_info_title),
        modifier = modifier
    ) {
        // Chips row
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ageRating?.let { rating ->
                AgeRatingBadge(rating = rating)
            }
            contentDescriptors.forEach { descriptor ->
                ContentDescriptorChip(descriptor = descriptor)
            }
        }
    }
}

@Composable
private fun AgeRatingBadge(rating: String) {
    val bgColor = ageRatingColor(rating)
    val description = ageRatingDescription(rating)
    var showDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(6.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = rating,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(rating) },
            text = { Text(description) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(Res.string.close))
                }
            }
        )
    }
}

@Composable
private fun ContentDescriptorChip(descriptor: String) {
    val icon = descriptorIcon(descriptor)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = descriptor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun ageRatingColor(rating: String): Color = when (rating.uppercase()) {
    "G", "TV-G", "TV-Y"                  -> Color(0xFF2E7D32) // green
    "PG", "TV-PG", "TV-Y7", "TV-Y7-FV"  -> Color(0xFFF57F17) // amber
    "PG-13", "TV-14"                     -> Color(0xFFE65100) // deep orange
    "R", "TV-MA"                         -> Color(0xFFC62828) // red
    "NC-17"                              -> Color(0xFF4A148C) // dark purple
    else                                 -> Color(0xFF37474F) // grey
}

@Composable
private fun ageRatingDescription(rating: String): String = when (rating.uppercase()) {
    "G", "TV-G", "TV-Y"                  -> stringResource(Res.string.rating_description_g)
    "PG", "TV-PG", "TV-Y7", "TV-Y7-FV"  -> stringResource(Res.string.rating_description_pg)
    "PG-13", "TV-14"                     -> stringResource(Res.string.rating_description_pg13)
    "R", "TV-MA"                         -> stringResource(Res.string.rating_description_r)
    "NC-17"                              -> stringResource(Res.string.rating_description_nc17)
    else                                 -> stringResource(Res.string.rating_description_unknown)
}

private fun descriptorIcon(descriptor: String): ImageVector = when {
    descriptor.contains("Violence", ignoreCase = true)  -> Icons.Default.SportsKabaddi
    descriptor.contains("Sexual", ignoreCase = true)    -> Icons.Default.NoAdultContent
    descriptor.contains("Drug", ignoreCase = true)      -> Icons.Default.LocalBar
    descriptor.contains("Alcohol", ignoreCase = true)   -> Icons.Default.LocalBar
    else                                                 -> Icons.Default.Warning
}
