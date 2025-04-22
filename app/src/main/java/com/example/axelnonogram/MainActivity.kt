package com.example.axelnonogram
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.axelnonogram.ui.theme.AxelNonogramTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        enableEdgeToEdge()
////        setContent {
////            AxelNonogramTheme {
////                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
////                    Greeting(
////                        name = "Android",
////                        modifier = Modifier.padding(innerPadding)
////                    )
////                }
////            }
////        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AxelNonogramTheme {
//        Greeting("Android")
//    }
////}
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.Fill
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            NonogramGame()
//        }
//    }
//}
//
//@Composable
//fun NonogramGame(viewModel: NonogramViewModel = viewModel()) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Text("Nonogram Game", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(16.dp))
//        NonogramGrid(viewModel)
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { viewModel.checkSolution() }) {
//            Text("Check Solution")
//        }
//    }
//}
//
//@Composable
//fun NonogramGrid(viewModel: NonogramViewModel) {
//    Column {
//        for (row in 0 until viewModel.gridSize) {
//            Row {
//                for (col in 0 until viewModel.gridSize) {
//                    Cell(viewModel, row, col)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Cell(viewModel: NonogramViewModel, row: Int, col: Int) {
//    // Directly access the value of the mutableStateOf
//    val isFilled = viewModel.grid[row][col].value
//
//    Canvas(
//        modifier = Modifier
//            .size(40.dp)
//            .background(Color.LightGray)
//            .clickable { viewModel.toggleCell(row, col) }
//    ) {
//        if (isFilled) {
//            drawRect(Color.Black, size = size, style = Fill)
//        }
//    }
//}
//
//
//class NonogramViewModel : androidx.lifecycle.ViewModel() {
//    val gridSize = 5
//    val grid = List(gridSize) { List(gridSize) { mutableStateOf(false) } }
//    private val solution = listOf(
//        listOf(true, false, true, false, true),
//        listOf(false, true, false, true, false),
//        listOf(true, true, true, true, true),
//        listOf(false, true, false, true, false),
//        listOf(true, false, true, false, true)
//    )
//
//    fun toggleCell(row: Int, col: Int) {
//        grid[row][col].value = !grid[row][col].value
//    }
//
//    fun checkSolution() {
//        val isCorrect = grid.indices.all { row ->
//            grid[row].indices.all { col -> grid[row][col].value == solution[row][col] }
//        }
//        println(if (isCorrect) "Correct!" else "Try Again!")
//    }
//}
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
//        val samplePuzzle =listOf(
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true, true),
//                listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false, true),
//                listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false, true),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true, true),
//                listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false, true),
//                listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false, true),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true, true),
//                listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false, true),
//                listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false, true),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//                listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true, true),
//                listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false, true),
//                listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false, true),
//                listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true, false),
//            )
        val samplePuzzle =listOf(
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true),
            listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false),
            listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true),
            listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false),
            listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true),
            listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false),
            listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
            listOf(true, true, false, true, true,true, true, false, true, true,true, true, false, true, true,true, true, false, true),
            listOf(true, true, false, false, true,true, true, false, false, true,true, true, false, false, true,true, true, false, false),
            listOf(true, false, false, false, true,true, false, false, false, true,true, false, false, false, true,true, false, false, false),
            listOf(false, true, true, true, false,false, true, true, true, false,false, true, true, true, false,false, true, true, true),
        )
//        val puzzle = "20x20xEeeikidjOEcHMdneEeigGmPbgfCEpOKlNGijfNMJGaBggaKlGbOIhGklEKKdNnJkjMkdnhdgANChBogb"
        val puzzle = "3x4xfni"
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    NonogramGame(samplePuzzle)
                    NonogramMain(puzzle)
                }
            }
        }
    }
}

