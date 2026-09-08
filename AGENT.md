# AGENT.md — smallWorldRules

## Проект
Android-приложение «Small World Rules» — справочник народов и способностей из настольной игры Small World.
Пакет `com.example.smallworld`. Язык UI — русский.

## Стек
- Kotlin, Jetpack Compose + Material3, Adaptive NavigationSuite
- `compileSdk/targetSdk 36`, `minSdk 31`, Java 11
- Данные: PNG-файлы в `assets/races/` и `assets/powers/` (с вложенными папками тем). Сканируются через `AssetManager.list()` — JSON-файлов нет.

## Структура
- `src/main/java/com/example/smallworld/MainActivity.kt` — `SmallWorldApp()`, навигация `currentDestination: AppDestinations`, `selectedEntry`, `favoriteKeys`. Enum `AppDestinations` (HOME, FAVORITES).
- `HomeScreen.kt` — поиск + `LazyColumn` + `ListItem` + кнопка «избранное». Поиск через `String.normalized()` (lowercase + ё→е). Ключ элемента — `entry.assetFileName`.
- `DetailScreen.kt` — детали + изображение из assets через `BitmapFactory.decodeStream()`, `BackHandler`, кнопка «избранное».
- `FavoritesScreen.kt` — список избранных, пустое состояние «Избранное пусто».
- `data/SmallWorldEntry.kt` — `name, assetFileName, type (RACE/POWER), theme`
- `data/SmallWorldRepository.kt` — `loadEntries(context)`, рекурсивный `walk()` по assets, сортировка `sortedBy name`
- `ui/theme/` — `Theme.kt, Color.kt, Type.kt`

## Избранное
- Хранится в `SharedPreferences` (`small_world_prefs`, ключ `favorite_keys` — `Set<String>` имён).
- `rememberFavoriteKeys()` — читает из prefs, `persistFavoriteKeys()` — пишет через `LaunchedEffect`.

## Ассеты
- `assets/races/<тема>/<раса>.png` — изображения рас
- `assets/powers/<тема>/<способность>.png` — изображения способностей
- Тема = имя подпапки первого уровня (например, «База», «Королевские дары», «Небесные острова», «Паучьи сети»)
- Всего ~82 PNG-файла

## Иконки (res/drawable)
- `ic_home` — вкладка «Главная»
- `ic_favorite`, `ic_favorite_border` — кнопки избранного (заполненная/пустая звезда)
- `ic_account_box` — не используется в коде

## Команды
```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
.\gradlew.bat lint
.\gradlew.bat test
```

## Эмулятор
- АВД лежит на D: (`D:\Android\avd\`), переменная `ANDROID_AVD_HOME=D:\Android\avd`.
- Запускать эмулятор нужно **всегда с диска D** (на C: не хватает места):
  ```powershell
  $env:ANDROID_AVD_HOME = "D:\Android\avd"
  Start-Process -FilePath "D:\Work\Soft\Android\Sdk\emulator\emulator.exe" -ArgumentList "-avd Pixel_6_API_31 -no-snapshot -no-boot-anim"
  ```
- Установка APK и запуск:
  ```powershell
  & "D:\Work\Soft\Android\Sdk\platform-tools\adb.exe" install -r "build\outputs\apk\debug\smallWorldRules-debug.apk"
  & "D:\Work\Soft\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.smallworld/.MainActivity
  ```

## Правила для агента
1. UI-строки только на русском.
2. Поиск обязан оставаться ё-толерантным (`normalized()`).
3. Новые расы/способности — добавлять PNG-файлы в `assets/races/` или `assets/powers/` в нужную подпапку темы. Код сканирует автоматически, JSON не нужен.
4. Новые иконки — добавлять в `res/drawable/`, регистрировать в `R.drawable.*`.
5. Навигация — enum `AppDestinations`, `rememberSaveable` для `currentDestination`, `remember` для `selectedEntry`.
6. Избранное — `SharedPreferences`, не хардкодить.
7. Не понижать `minSdk`, не менять namespace без запроса.
8. Запуск эмулятора — только через `D:\Android\avd\` (на C: не хватает места).
