package com.example.smallworld

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smallworld.data.SmallWorldEntry

@Composable
fun HomeScreen(
    entries: List<SmallWorldEntry>,
    isFavorite: (SmallWorldEntry) -> Boolean,
    onToggleFavorite: (SmallWorldEntry) -> Unit,
    onEntryClick: (SmallWorldEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = if (query.isBlank()) {
        entries
    } else {
        val normalizedQuery = query.normalized()
        entries.filter { it.name.normalized().contains(normalizedQuery) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Народ или способность...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск"
                )
            },
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (query.isNotBlank()) {
                item {
                    Text(
                        text = "Результаты (${filtered.size}):",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }

            if (filtered.isEmpty() && query.isNotBlank()) {
                item {
                    Text(
                        text = "Ничего не найдено",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(filtered, key = { it.assetFileName }) { entry ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = entry.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingContent = {
                        val favorite = isFavorite(entry)
                        IconButton(onClick = { onToggleFavorite(entry) }) {
                            Icon(
                                painter = painterResource(
                                    if (favorite) R.drawable.ic_favorite
                                    else R.drawable.ic_favorite_border
                                ),
                                contentDescription = if (favorite) "Убрать из избранного" else "Добавить в избранное",
                                tint = if (favorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    },
                    colors = androidx.compose.material3.ListItemDefaults.colors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEntryClick(entry) }
                )
                HorizontalDivider()
            }
        }
    }
}

/**
 * Приводит строку к нижнему регистру и считает "е" и "ё" эквивалентными,
 * чтобы поиск по одной букве находил и другую.
 */
private fun String.normalized(): String =
    lowercase()
        .replace('ё', 'е')
        .replace('Ё', 'е')
