package com.example.smallworld

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.smallworld.data.SmallWorldEntry
import com.example.smallworld.data.SmallWorldRepository
import com.example.smallworld.ui.theme.SmallWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmallWorldTheme {
                SmallWorldApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SmallWorldApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedEntry by remember { mutableStateOf<SmallWorldEntry?>(null) }
    var favoriteKeys by rememberFavoriteKeys()

    val context = LocalContext.current
    persistFavoriteKeys(favoriteKeys)

    val entries = rememberEntries()

    val favoriteEntries = entries.filter { it.name in favoriteKeys }

    val isFavorite: (SmallWorldEntry) -> Boolean = { it.name in favoriteKeys }
    val onToggleFavorite: (SmallWorldEntry) -> Unit = {
        favoriteKeys = if (it.name in favoriteKeys) {
            favoriteKeys - it.name
        } else {
            favoriteKeys + it.name
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = {
                        currentDestination = it
                        selectedEntry = null
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val selected = selectedEntry
            if (selected != null) {
                DetailScreen(
                    entry = selected,
                    isFavorite = isFavorite(selected),
                    onToggleFavorite = { onToggleFavorite(selected) },
                    onBack = { selectedEntry = null },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                when (currentDestination) {
                    AppDestinations.HOME -> HomeScreen(
                        entries = entries,
                        isFavorite = isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onEntryClick = { selectedEntry = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                    AppDestinations.FAVORITES -> FavoritesScreen(
                        entries = favoriteEntries,
                        isFavorite = isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onEntryClick = { selectedEntry = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private const val PREFS_NAME = "small_world_prefs"
private const val KEY_FAVORITES = "favorite_keys"

@Composable
fun rememberFavoriteKeys(): androidx.compose.runtime.MutableState<Set<String>> {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return remember {
        mutableStateOf(prefs.getStringSet(KEY_FAVORITES, emptySet())?.toSet() ?: emptySet())
    }
}

@Composable
fun persistFavoriteKeys(keys: Set<String>) {
    val context = LocalContext.current
    androidx.compose.runtime.LaunchedEffect(keys) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_FAVORITES, keys)
            .apply()
    }
}

@Composable
fun rememberEntries(): List<SmallWorldEntry> {
    val context = LocalContext.current
    return androidx.compose.runtime.remember {
        SmallWorldRepository.loadEntries(context)
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Главная", R.drawable.ic_home),
    FAVORITES("Избранное", R.drawable.ic_favorite),
}
