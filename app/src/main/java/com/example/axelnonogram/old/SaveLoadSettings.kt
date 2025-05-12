//package com.example.axelnonogram
//
//
//import android.content.Context
//import android.content.SharedPreferences
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//
///**
// * Settings data class that holds all user preferences
// */
//data class GameSettings(
//    val soundEnabled: Boolean = true,
//    val gridSize: Int = 10
//)
//
//enum class Difficulty {
//    EASY, MEDIUM, HARD
//}
//
///**
// * Settings manager to handle saving and loading settings
// */
//class SettingsManager(private val context: Context) {
//    private val PREFS_NAME = "nonogram_settings"
//    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//
//    // Keys for settings
//    private val KEY_SOUND_ENABLED = "sound_enabled"
//    private val KEY_VIBRATION_ENABLED = "vibration_enabled"
//    private val KEY_DARK_MODE = "dark_mode"
//    private val KEY_DIFFICULTY = "difficulty"
//    private val KEY_GRID_SIZE = "grid_size"
//
//    // Load settings from SharedPreferences
//    fun loadSettings(): GameSettings {
//        return GameSettings(
//            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
//            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
//            darkMode = prefs.getBoolean(KEY_DARK_MODE, false),
//            difficulty = Difficulty.valueOf(prefs.getString(KEY_DIFFICULTY, Difficulty.MEDIUM.name) ?: Difficulty.MEDIUM.name),
//            gridSize = prefs.getInt(KEY_GRID_SIZE, 10)
//        )
//    }
//
//    // Save settings to SharedPreferences
//    fun saveSettings(settings: GameSettings) {
//        prefs.edit().apply {
//            putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
//            putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
//            putBoolean(KEY_DARK_MODE, settings.darkMode)
//            putString(KEY_DIFFICULTY, settings.difficulty.name)
//            putInt(KEY_GRID_SIZE, settings.gridSize)
//            apply() // Apply changes asynchronously
//        }
//    }
//}
//
///**
// * Settings screen Composable
// */
//@Composable
//fun SettingsScreen(onBack: () -> Unit) {
//    val context = LocalContext.current
//    val settingsManager = remember { SettingsManager(context) }
//    var settings by remember { mutableStateOf(settingsManager.loadSettings()) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "Settings",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        // Sound toggle
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Sound")
//            Switch(
//                checked = settings.soundEnabled,
//                onCheckedChange = {
//                    settings = settings.copy(soundEnabled = it)
//                    settingsManager.saveSettings(settings)
//                }
//            )
//        }
//
//        // Vibration toggle
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Vibration")
//            Switch(
//                checked = settings.vibrationEnabled,
//                onCheckedChange = {
//                    settings = settings.copy(vibrationEnabled = it)
//                    settingsManager.saveSettings(settings)
//                }
//            )
//        }
//
//        // Dark mode toggle
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Dark Mode")
//            Switch(
//                checked = settings.darkMode,
//                onCheckedChange = {
//                    settings = settings.copy(darkMode = it)
//                    settingsManager.saveSettings(settings)
//                }
//            )
//        }
//
//        // Difficulty selector
//        Text("Difficulty")
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Difficulty.values().forEach { difficulty ->
//                OutlinedButton(
//                    onClick = {
//                        settings = settings.copy(difficulty = difficulty)
//                        settingsManager.saveSettings(settings)
//                    },
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        containerColor = if (settings.difficulty == difficulty)
//                            MaterialTheme.colorScheme.primaryContainer
//                        else
//                            MaterialTheme.colorScheme.surface
//                    )
//                ) {
//                    Text(difficulty.name)
//                }
//            }
//        }
//
//        // Grid size slider
//        Text("Grid Size: ${settings.gridSize}x${settings.gridSize}")
//        Slider(
//            value = settings.gridSize.toFloat(),
//            onValueChange = {
//                settings = settings.copy(gridSize = it.toInt())
//            },
//            onValueChangeFinished = {
//                settingsManager.saveSettings(settings)
//            },
//            valueRange = 5f..15f,
//            steps = 10,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // Back button
//        Button(
//            onClick = onBack,
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            Text("Save and Return")
//        }
//    }
//}
//
///**
// * Updated AppNavigation to include Settings screen
// */
//@Composable
//fun AppNavigation() {
//    var currentScreen by remember { mutableStateOf(Screen.MainMenu) }
//    val puzzle = remember { /* your puzzle data definition */ }
//    val context = LocalContext.current
//    val settingsManager = remember { SettingsManager(context) }
//    val settings by remember { mutableStateOf(settingsManager.loadSettings()) }
//
//    when (currentScreen) {
//        Screen.MainMenu -> MainMenu(
//            onStartGame = { currentScreen = Screen.Game },
//            onOpenSettings = { currentScreen = Screen.Settings }
//        )
//        Screen.Game -> NonogramMain(
//            puzzle = puzzle,
//            settings = settings,
//            onBack = { currentScreen = Screen.MainMenu }
//        )
//        Screen.Settings -> SettingsScreen(
//            onBack = { currentScreen = Screen.MainMenu }
//        )
//    }
//}
//
///**
// * Updated Screen enum to include Settings
// */
//enum class Screen {
//    MainMenu,
//    Game,
//    Settings
//}
//
///**
// * Updated MainMenu to include Settings button
// */
//@Composable
//fun MainMenu(onStartGame: () -> Unit, onOpenSettings: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Nonogram Game",
//            style = MaterialTheme.typography.headlineLarge,
//            modifier = Modifier.padding(bottom = 32.dp)
//        )
//
//        Button(
//            onClick = onStartGame,
//            modifier = Modifier
//                .fillMaxWidth(0.7f)
//                .padding(vertical = 8.dp)
//        ) {
//            Text("Start Game")
//        }
//
//        Button(
//            onClick = onOpenSettings,
//            modifier = Modifier
//                .fillMaxWidth(0.7f)
//                .padding(vertical = 8.dp)
//        ) {
//            Text("Settings")
//        }
//    }
//}
