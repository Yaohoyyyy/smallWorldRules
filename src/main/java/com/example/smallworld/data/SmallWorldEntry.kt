package com.example.smallworld.data

/**
 * Тип записи. Разделение существует только на уровне кода —
 * для пользователя народ и способность выглядят одинаково.
 */
enum class SmallWorldType {
    /** Народ (классические расы). */
    RACE,

    /** Способность (особые умения). */
    POWER,
}

/**
 * Элемент описания (народ или способность) из игры Small World.
 *
 * @param name название, совпадает с именем png-файла без расширения
 * @param assetFileName путь к файлу внутри assets (с подпапками типа и темы)
 * @param type народ или способность
 * @param theme тема/набор, в которую входит запись (например, "База", "Небесные острова")
 */
data class SmallWorldEntry(
    val name: String,
    val assetFileName: String,
    val type: SmallWorldType,
    val theme: String,
)
