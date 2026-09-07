package com.example.smallworld

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smallworld.data.SmallWorldEntry

@Composable
fun FavoritesScreen(
    entries: List<SmallWorldEntry>,
    isFavorite: (SmallWorldEntry) -> Boolean,
    onToggleFavorite: (SmallWorldEntry) -> Unit,
    onEntryClick: (SmallWorldEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Избранное",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        if (entries.isEmpty()) {
            Text(
                text = "Избранное пусто",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(entries, key = { it.assetFileName }) { entry ->
                    val favorite = isFavorite(entry)
                    ListItem(
                        headlineContent = {
                            Text(
                                text = entry.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { onToggleFavorite(entry) }) {
                                Icon(
                                    painter = painterResource(
                                        if (favorite) R.drawable.ic_favorite
                                        else R.drawable.ic_favorite_border
                                    ),
                                    contentDescription = if (favorite) "Убрать из избранного" else "Добавить в избранное",
                                    tint = if (favorite) MaterialTheme.colorScheme.primary
                                    else LocalContentColor.current
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEntryClick(entry) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
