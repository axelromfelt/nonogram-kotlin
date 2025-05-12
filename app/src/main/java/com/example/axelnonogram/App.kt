@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.axelnonogram



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.axelnonogram.game.NonogramGame

@Composable
fun App(viewModel: NonogramViewModel) {
    val navController = rememberNavController()

    val standardNonograms by viewModel.defaultNonograms.collectAsState()

    Scaffold(
        topBar = {
//            PuzzleTopBar(navController)
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Back to menu"
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main_menu",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("main_menu") {
                MainMenuContent(navController)
            }
            composable("menu/Default") {
                MainMenuContent(navController,standardNonograms)
            }
            composable("nonogram/{type}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt()
                val type = backStackEntry.arguments?.getString("type")

                when(type){
                    "d" -> {
                        for (nonogram in standardNonograms){
                            if (nonogram.id == id){
                                NonogramGame(viewModel, nonogram)
                            }
                        }
                    }
                    "i" -> {
                        for (nonogram in standardNonograms){
                            if (nonogram.id == id){
                                NonogramGame(viewModel, nonogram)
                            }
                        }
                    }
                    "u" -> {
                        for (nonogram in standardNonograms){
                            if (nonogram.id == id){
                                NonogramGame(viewModel, nonogram)
                            }
                        }
                    }
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PuzzleTopBar(navController: NavHostController) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    val currentPuzzleId = navBackStackEntry?.arguments?.getString("puzzleId")
//
//    val showBackButton = currentRoute?.startsWith("puzzle/") == true
//    val title = when {
//        currentRoute == "main_menu" -> "Puzzle Collection"
//        showBackButton -> "Puzzle $currentPuzzleId"
//        else -> "Puzzles"
//    }
//
//    TopAppBar(
//        title = { Text(title) },
//        navigationIcon = {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "Back to menu"
//                    )
//                }
//        },
//        actions = {IconButton(onClick = { navController.popBackStack() }) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "Back to menu"
//            )
//        }},
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//        )
//    )
//}

@Composable
fun MainMenuContent(navController: NavHostController, nonogramList: List<NonogramData>?=null){

    val buttonOptions = remember {
        if (nonogramList!=null){
            nonogramList
        }
        else {
            listOf(
                "Default",
                "Import",
                "User",
            )
        }
    } as List<Any>

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(buttonOptions.indices.toList()) { index ->
            val buttonText: String
            val navigatePath: String

            when (val item = buttonOptions[index]) {
                is NonogramData -> {
                    buttonText = item.nonogram
                    navigatePath = "nonogram/${item.type}/${item.id}"
                }
                is String -> {
                    buttonText = item
                    navigatePath = "menu/${item}"
                }
                else -> {
                    buttonText = "Unknown"
                    navigatePath = "main_menu"
                }
            }

            ElevatedCard(
                onClick = {
                    navController.navigate(navigatePath)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PuzzleContent(puzzleId: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // This is where your actual puzzle content would go
        Text(
            text = "Puzzle $puzzleId Content",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun App() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "main_menu") {
//        composable("main_menu") {
//            MainMenuScreen(navController)
//        }
//        composable("puzzle/{puzzleId}") { backStackEntry ->
//            val puzzleId = backStackEntry.arguments?.getString("puzzleId")
//            PuzzleScreen(
//                puzzleId = puzzleId ?: "1",
//                onBackPressed = { navController.popBackStack() }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainMenuScreen(navController: NavHostController) {
//    val puzzles = remember {
//        listOf(
//            "Puzzle 1: Sudoku",
//            "Puzzle 2: Crossword",
//            "Puzzle 3: Word Search",
//            "Puzzle 4: Jigsaw",
//            "Puzzle 1: Sudoku",
//            "Puzzle 2: Crossword",
//            "Puzzle 3: Word Search",
//            "Puzzle 4: Jigsaw",
//            "Puzzle 1: Sudoku",
//            "Puzzle 2: Crossword",
//            "Puzzle 3: Word Search",
//            "Puzzle 4: Jigsaw",
//            "Puzzle 1: Sudoku",
//            "Puzzle 2: Crossword",
//            "Puzzle 3: Word Search",
//            "Puzzle 4: Jigsaw",
//            "Puzzle 5: Riddle"
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Puzzle Collection") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            item {
//                Text(
//                    text = "Choose a Puzzle",
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//            }
//
//            items(puzzles.indices.toList()) { index ->
//                ElevatedCard(
//                    onClick = {
//                        navController.navigate("puzzle/${index + 1}")
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        text = puzzles[index],
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PuzzleScreen(puzzleId: String, onBackPressed: () -> Unit) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Puzzle $puzzleId") },
//                navigationIcon = {
//                    IconButton(onClick = onBackPressed) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back to menu"
//                        )
//                    }
//                },
//                actions = {IconButton(onClick = onBackPressed) {
//                    Icon(
//                        imageVector = Icons.Default.Settings,
//                        contentDescription = "Back to menu"
//                    )
//                }},
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            contentAlignment = Alignment.Center
//        ) {
//            // This is where your actual puzzle content would go
//            Text(
//                text = "Puzzle $puzzleId Content",
//                style = MaterialTheme.typography.headlineMedium
//            )
//        }
//    }
//}



//import android.util.Log
//import androidx.activity.compose.setContent
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.input.pointer.PointerInputChange
//import androidx.compose.ui.input.pointer.positionChanged
//import androidx.compose.ui.unit.dp
//
//enum class Screen {
//    MainMenu,
//    Game
//}
//
//@Composable
//fun AppNavigation() {
//    // Define a state to track the current screen
//    var currentScreen by remember { mutableStateOf(Screen.MainMenu) }
//
//    // Define your puzzle data
//    val puzzle = remember { "3x6xlNGo" }
//
//    // Based on the current screen state, show the appropriate composable
//    when (currentScreen) {
//        Screen.MainMenu -> MainMenu(
//            onStartGame = { currentScreen = Screen.Game }
//        )
//        Screen.Game -> NonogramMain(puzzle)
//    }
//}
//
//@Composable
//fun MainMenu(onStartGame: () -> Unit) {
//    Column (
////        verticalArrangement =
//    ) {
//        Row (
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ){
//            Button(onClick = onStartGame) {
//                Text("Start Game")
//            }
//        }
//    }
//}