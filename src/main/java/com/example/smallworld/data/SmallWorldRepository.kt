package com.example.smallworld.data

import android.content.Context

/**
 * Источник данных: список всех описаний (по png-файлам в assets).
 *
 * Народы лежат в assets/races/, способности — в assets/powers/.
 * Внутри могут быть вложенные папки тем (например, "База", "Небесные острова").
 * С точки зрения пользователя они не различаются; разделение
 * существует только в коде (поля [SmallWorldEntry.type] и [SmallWorldEntry.theme]).
 */
object SmallWorldRepository {

    fun loadEntries(context: Context): List<SmallWorldEntry> {
        val assetManager = context.assets
        return buildList {
            addAll(scanFolder(assetManager, "races", SmallWorldType.RACE))
            addAll(scanFolder(assetManager, "powers", SmallWorldType.POWER))
        }.sortedBy { it.name }
    }

    private fun scanFolder(
        assetManager: android.content.res.AssetManager,
        root: String,
        type: SmallWorldType,
    ): List<SmallWorldEntry> {
        val result = mutableListOf<SmallWorldEntry>()
        walk(assetManager, root, root, type, result)
        return result
    }

    /**
     * Рекурсивно обходит папку в assets и собирает png-файлы.
     * Тема = имя подпапки первого уровня внутри [root]
     * (или пустая строка, если файл лежит прямо в [root]).
     */
    private fun walk(
        assetManager: android.content.res.AssetManager,
        root: String,
        path: String,
        type: SmallWorldType,
        result: MutableList<SmallWorldEntry>,
    ) {
        val names = assetManager.list(path).orEmpty()
        for (name in names) {
            val child = "$path/$name"
            val isDir = assetManager.list(child).orEmpty().isNotEmpty()
            if (isDir) {
                walk(assetManager, root, child, type, result)
            } else if (name.endsWith(".png", ignoreCase = true)) {
                val entryName = name.removeSuffix(".png")
                val theme = if (path == root) "" else path.removePrefix("$root/")
                result += SmallWorldEntry(
                    name = entryName,
                    assetFileName = child,
                    type = type,
                    theme = theme,
                )
            }
        }
    }
}
