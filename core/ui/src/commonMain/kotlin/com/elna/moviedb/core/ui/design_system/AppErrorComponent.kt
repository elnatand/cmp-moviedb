package com.elna.moviedb.core.ui.design_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.error_title
import com.elna.moviedb.resources.network_error
import com.elna.moviedb.resources.retry
import com.elna.moviedb.resources.server_error
import com.elna.moviedb.resources.unknown_error_occurred
import org.jetbrains.compose.resources.stringResource

/**
 * Maps a [DataError] to a localized, user-facing message.
 *
 * Keeps the raw technical text from the data layer out of the UI — the presentation
 * layer only ever shows these translated strings.
 */
@Composable
fun DataError.toLocalizedMessage(): String = when (this) {
    DataError.NETWORK -> stringResource(Res.string.network_error)
    DataError.SERVER -> stringResource(Res.string.server_error)
    DataError.CLIENT -> stringResource(Res.string.unknown_error_occurred)
    DataError.UNKNOWN -> stringResource(Res.string.unknown_error_occurred)
}

@Composable
fun AppErrorComponent(
    onRetry: () -> Unit,
    message: String? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = message ?: stringResource(Res.string.error_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(Res.string.retry),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(Res.string.retry))
        }
    }
}