package com.elna.moviedb.feature.search.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.all
import com.elna.moviedb.resources.movies
import com.elna.moviedb.resources.people
import com.elna.moviedb.resources.tv_shows
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchFilters(
    selectedFilter: SearchFilter,
    onFilterChanged: (SearchFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterChanged(filter) },
                label = { Text(stringResource(getFilterLabel(filter))) }
            )
        }
    }
}

private fun getFilterLabel(filter: SearchFilter): StringResource {
    return when (filter) {
        SearchFilter.ALL -> Res.string.all
        SearchFilter.MOVIES -> Res.string.movies
        SearchFilter.TV_SHOWS -> Res.string.tv_shows
        SearchFilter.PEOPLE -> Res.string.people
    }
}