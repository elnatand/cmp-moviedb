package com.elna.moviedb.feature.movies.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun BoxOfficeItem(
    label: String,
    amount: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$${formatNumberWithCommas(amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

internal fun formatNumberWithCommas(number: Long): String {
    val isNegative = number < 0
    val absoluteValue = if (isNegative) -number else number
    val formatted = absoluteValue.toString().reversed().chunked(3).joinToString(",").reversed()
    return if (isNegative) "-$formatted" else formatted
}
