package com.example.axelnonogram

import android.util.Log
import androidx.activity.compose.setContent

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.dp

enum class Screen {
    MainMenu,
    Game
}

@Composable
fun AppNavigation() {
    // Define a state to track the current screen
    var currentScreen by remember { mutableStateOf(Screen.MainMenu) }

    // Define your puzzle data
    val puzzle = remember { "3x6xlNGo" }

    // Based on the current screen state, show the appropriate composable
    when (currentScreen) {
        Screen.MainMenu -> MainMenu(
            onStartGame = { currentScreen = Screen.Game }
        )
        Screen.Game -> NonogramMain(puzzle)
    }
}

@Composable
fun MainMenu(onStartGame: () -> Unit) {
    Column (
//        verticalArrangement =
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Button(onClick = onStartGame) {
                Text("Start Game")
            }
        }
    }
}