package com.example.axelnonogram

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clipToBounds
import kotlin.math.min
import kotlin.math.max


val dict = mapOf(
    "00000" to 'a',
    "00001" to 'A',
    "00010" to 'b',
    "00011" to 'B',
    "00100" to 'c',
    "00101" to 'C',
    "00110" to 'd',
    "00111" to 'D',
    "01000" to 'e',
    "01001" to 'E',
    "01010" to 'f',
    "01011" to 'F',
    "01100" to 'g',
    "01101" to 'G',
    "01110" to 'h',
    "01111" to 'H',
    "10000" to 'i',
    "10001" to 'I',
    "10010" to 'j',
    "10011" to 'J',
    "10100" to 'k',
    "10101" to 'K',
    "10110" to 'l',
    "10111" to 'L',
    "11000" to 'm',
    "11001" to 'M',
    "11010" to 'n',
    "11011" to 'N',
    "11100" to 'o',
    "11101" to 'O',
    "11110" to 'p',
    "11111" to 'P'
)
data class GameInfo(
    val width: Int,
    val height: Int,
    val solution: Array<Array<Boolean>>,
    val currentState: List<List<CellState>>,
    val rowHints: List<List<Int>>,
    val colHints: List<List<Int>>,
    val rowMaxHints: Int,
    val colMaxHints: Int,
    val isComplete: Boolean = false
)
//  Log.e("rggggg", "${puzzleDecompressed[2][y*x-1].code},${puzzleDecompressed[2][y*x-1]}")

fun decompress(puzzleKey: String): List<String> {
    val list = puzzleKey.split("x")
    val x = list[0]
    val y = list[1]
    val grid = list[2]
    val result = mutableListOf<String>()

    for (letter in grid) {
        for ((key, value) in dict) {
            if (value == letter) {
                result.add(key)
                break
            }
        }
    }
    return listOf(x, y, result.joinToString(""))
}

@Composable
fun NonogramMain(puzzleCompressed: String) {
    val puzzleDecompressed = decompress(puzzleCompressed)
    val solution = Array(puzzleDecompressed[1].toInt()) { Array(puzzleDecompressed[0].toInt()) { true } }

    for (y in 1 .. puzzleDecompressed[1].toInt()){
        for (x in 1 .. puzzleDecompressed[0].toInt()){
            solution[y - 1][x - 1] = puzzleDecompressed[2][y*x-1].code == 49
        }
    }

    val rowHints = remember {
        val hints = mutableListOf<List<Int>>()

        for (row in solution) {
            val rowHint = mutableListOf<Int>()
            var count = 0

            for (cell in row) {
                if (cell) {
                    count++
                } else if (count > 0) {
                    rowHint.add(count)
                    count = 0
                }
            }
            if (count > 0) {
                rowHint.add(count)
            }
            if (rowHint.isEmpty()) {
                rowHint.add(0)
            }
            hints.add(rowHint)
        }
        hints
    }


    val colHints = remember {
        val hints = mutableListOf<List<Int>>()

        for (column in solution[0].indices) {
            val colHint = mutableListOf<Int>()
            var count = 0

            for (row in solution.indices) {
                if (solution[row][column]) {
                    count++
                } else if (count > 0) {
                    colHint.add(count)
                    count = 0
                }
            }

            if (count > 0) {
                colHint.add(count)
            }

            if (colHint.isEmpty()) {
                colHint.add(0)
            }

            hints.add(colHint)
        }

        hints
    }

    var rowMaxHints = 0
    for (rowHint in rowHints) {
        if (rowHint.count() > rowMaxHints){
            rowMaxHints = rowHint.count()
        }
    }
    var colMaxHints = 0
    for (colHint in colHints) {
        if (colHint.count() > colMaxHints){
            colMaxHints = colHint.count()
        }
    }
    val gameInfo = remember {
        mutableStateOf(
            GameInfo(
                height = puzzleDecompressed[0].toInt(),
                width = puzzleDecompressed[1].toInt(),
                solution = solution,
                currentState = List(puzzleDecompressed[1].toInt()) {
                    List(puzzleDecompressed[0].toInt()) { CellState.EMPTY }
                },
                rowHints = rowHints,
                colHints = colHints,
                rowMaxHints = rowMaxHints,
                colMaxHints = colMaxHints
            )
        )
    }
}