package com.example.axelnonogram.game

import android.R.attr.contentDescription
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.DisabledByDefault
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.axelnonogram.NonogramData
import com.example.axelnonogram.NonogramViewModel
import kotlin.div
import kotlin.text.toInt


enum class PaintMode {
    FILL,
    MARK,
    CLEAR,
    MOVE
}

enum class CellState {
    EMPTY,
    FILLED,
    MARKED
}

data class CellsHistory(
    val row: Int,
    val col: Int,
    val pastState: CellState,
    val futureState: CellState
)

@Composable
fun NonogramGame(viewModel: NonogramViewModel, nonogram: NonogramData){
    val nonogramInfo = remember { mutableStateOf(loadNonogramInfo(nonogram.nonogram)) }

    Log.e("NYBGG","${nonogramInfo.value.colHints.count()}")
    for (colHint in nonogramInfo.value.colHints){
        Log.e("NYBGG","---")
        for (hint in colHint){
            Log.e("NYBGG","${hint}")
        }
    }
    Log.e("NYBGG","---")
    Log.e("NYBGG","---")

    Log.e("NYBGG","${nonogramInfo.value.rowHints.count()}")
    for (colHint in nonogramInfo.value.rowHints){
        Log.e("NYBGG","---")
        for (hint in colHint){
            Log.e("NYBGG","${hint}")
        }
    }

    val cellStates = remember {
        val rowCount = nonogramInfo.value.height-nonogramInfo.value.colHintsSize
        val colCount = nonogramInfo.value.width -nonogramInfo.value.rowHintsSize
        val tempArray = Array(rowCount) { row ->
            Array(colCount) { cell ->
                mutableStateOf(CellState.EMPTY)
            }
        }
        if (nonogram.currentState != "" && nonogram.currentState != null){
            val state = nonogram.currentState
            for (rowIndex in 0 until rowCount){
                for (colIndex in 0 until colCount){
                    tempArray[rowIndex][colIndex].value = when (state?.get(rowIndex*colCount+colIndex)){
                        '1' -> CellState.FILLED
                        '2' -> CellState.MARKED
                        '0' -> CellState.EMPTY
                        else -> CellState.EMPTY
                    }
                }
            }
        }
        tempArray
    }

    var paintMode by remember { mutableStateOf(PaintMode.FILL) }

    var zoomFactor by remember { mutableStateOf(1f) }

    val lastDragCellState = remember { mutableStateOf<CellState?>(null) }





    val modifiedDuringDrag = remember { mutableListOf<CellsHistory>() }

    val cellPastState = remember { mutableListOf<List<CellsHistory>>() }
    val cellFutureState = remember { mutableListOf<List<CellsHistory>>() }

//    val pendingChanges = remember { mutableStateOf(mutableListOf<CellChange>()) }

    val isCompleted = remember { mutableStateOf(nonogram.isComplete) }
    val hideWinPopup = remember { mutableStateOf(false) }




    var scale by remember { mutableStateOf(1f) }

    var offset by remember { mutableStateOf(Offset.Zero) }


    val configuration = LocalConfiguration.current
    val gridAreaWidth = configuration.screenWidthDp.dp
    val gridAreaHeight = configuration.screenHeightDp.dp


    val baseCellSize = if (gridAreaHeight/(nonogramInfo.value.height) < gridAreaWidth/(nonogramInfo.value.width)) {
        gridAreaHeight/(nonogramInfo.value.height)
    } else {
        gridAreaWidth/(nonogramInfo.value.width)
    }

    val cellSize = baseCellSize * zoomFactor


    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->

                            if (paintMode == PaintMode.MOVE || isCompleted.value) {
                                scale = (scale * zoom).coerceIn(0.5f, 3f)
                                offset += pan
                            }
                        }
                    }
            ) {

                Box(
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .width(cellSize * (nonogramInfo.value.width))
                        .height(cellSize * (nonogramInfo.value.height))
                        .pointerInput(paintMode, isCompleted.value) {
                            if (!isCompleted.value && paintMode != PaintMode.MOVE) {
                                detectDragGestures(
                                    onDragStart = { position ->
                                        modifiedDuringDrag.clear()

                                        val row =
                                            (position.y / (size.height / (nonogramInfo.value.height)) - nonogramInfo.value.colHintsSize).toInt()

                                        val col =
                                            (position.x / (size.width / (nonogramInfo.value.width)) - nonogramInfo.value.rowHintsSize).toInt()

                                        if (isInRange(
                                                0,
                                                row,
                                                (nonogramInfo.value.height - nonogramInfo.value.colHintsSize)
                                            ) && isInRange(
                                                0,
                                                col,
                                                (nonogramInfo.value.width - nonogramInfo.value.rowHintsSize)
                                            )
                                        ) {

                                            val originalState = cellStates[row][col].value

                                            lastDragCellState.value = getNewCellState(originalState, paintMode)

                                            modifiedDuringDrag.add(
                                                CellsHistory(
                                                    row,
                                                    col,
                                                    originalState,
                                                    lastDragCellState.value!!
                                                )
                                            )

                                            cellStates[row][col].value = lastDragCellState.value!!
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        val row =
                                            (change.position.y / (size.height / (nonogramInfo.value.height)) - nonogramInfo.value.colHintsSize).toInt()

                                        val col =
                                            (change.position.x / (size.width / (nonogramInfo.value.width)) - nonogramInfo.value.rowHintsSize).toInt()

                                        if (isInRange(
                                                0,
                                                row,
                                                (nonogramInfo.value.height - nonogramInfo.value.colHintsSize)
                                            ) && isInRange(
                                                0,
                                                col,
                                                (nonogramInfo.value.width - nonogramInfo.value.rowHintsSize)
                                            )
                                        ) {

                                            if (cellStates[row][col].value != lastDragCellState.value) {
                                                val originalState = cellStates[row][col].value

                                                modifiedDuringDrag.add(
                                                    CellsHistory(
                                                        row,
                                                        col,
                                                        originalState,
                                                        lastDragCellState.value!!
                                                    )
                                                )

                                                cellStates[row][col].value =
                                                    lastDragCellState.value!!
                                            }
                                        }
                                    },
                                    onDragEnd = {

                                        if (modifiedDuringDrag.isNotEmpty()) {
                                            cellPastState.add(modifiedDuringDrag.toList())
                                            cellFutureState.clear()

                                            isCompleted.value = checkSolution(nonogramInfo.value.solution, cellStates)
                                            updateSaveData(viewModel, nonogram,cellStates,isCompleted.value)
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    // Game grid component
                    NonogramGrid(
                        height = nonogramInfo.value.height,
                        width = nonogramInfo.value.width,
                        cellStates = cellStates,
                        cellSize = cellSize,
                        rowHints = nonogramInfo.value.rowHints,
                        colHints = nonogramInfo.value.colHints,
                        rowHintsSize = nonogramInfo.value.rowHintsSize,
                        colHintsSize = nonogramInfo.value.colHintsSize,
                        onCellClicked = { row, col ->
                            if (!isCompleted.value && paintMode != PaintMode.MOVE) {

                                val originalState = cellStates[row][col].value
                                val newState = getNewCellState(originalState, paintMode)

                                cellPastState.add(listOf(CellsHistory(row, col, originalState, newState)))
                                cellFutureState.clear()

                                cellStates[row][col].value = newState

                                isCompleted.value = checkSolution(nonogramInfo.value.solution, cellStates)
                                updateSaveData(viewModel, nonogram,cellStates,isCompleted.value)

                            }
                        },
                        paintMode = paintMode,
                        isComplete = isCompleted.value
                    )

                    // Show completion overlay when puzzle is solved

                }
                if (isCompleted.value && !hideWinPopup.value) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
//                            .background(Color.Green.copy(alpha = completionAlpha.value))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Puzzle Completed!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,

                                )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    // Clear the completion overlay but keep the solved puzzle visible
                                    hideWinPopup.value = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text("Continue", color = Color.Black)
                            }
                        }
                    }
                }
            }


            BottomAppBar(
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    UndoRedoButtons(
                        onUndo = {

                            if (cellPastState.isNotEmpty() && !isCompleted.value) {
                                val cells = cellPastState.removeAt(cellPastState.lastIndex)
                                for (cell in cells) {
                                    Log.e("AAAVVV","${cell.row},${cell.col},${cell.pastState}")
                                    cellStates[cell.row][cell.col].value = cell.pastState
                                }
                                cellFutureState.add(cells)
                            }
                            updateSaveData(viewModel, nonogram,cellStates,isCompleted.value)

                        },
                        onRedo = {

                            if (cellFutureState.isNotEmpty() && !isCompleted.value) {
                                val cells = cellFutureState.removeAt(cellFutureState.lastIndex)
                                for (cell in cells) {
                                    Log.e("AAAVVV","${cell.row},${cell.col},${cell.futureState}")
                                    cellStates[cell.row][cell.col].value = cell.futureState
                                }
                                cellPastState.add(cells)
                            }
                            updateSaveData(viewModel, nonogram,cellStates,isCompleted.value)

                        }
                    )

                    ZoomControls(
                        zoomFactor = scale,
                        onZoomChanged = { newZoom -> scale = newZoom },
                        onResetZoom = { scale = 1f; offset = Offset.Zero }
                    )

                    PaintModeSelector(
                        currentPaintMode = paintMode,
                        onPaintModeChange = { newMode -> paintMode = newMode }
                    )
                }

            }
        }
    }


}


fun getNewCellState(currentState: CellState, paintMode: PaintMode): CellState {
    return when (paintMode) {
        PaintMode.FILL -> {
            if (currentState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
        }
        PaintMode.MARK -> {
            if (currentState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
        }
        PaintMode.CLEAR -> CellState.EMPTY
        PaintMode.MOVE -> currentState // No change in move mode
    }
}
fun resetGame(cellStates: Array<Array<MutableState<CellState>>>) {
    for (row in cellStates.indices) {
        for (col in cellStates[row].indices) {
            cellStates[row][col].value = CellState.EMPTY
        }
    }
}



fun copyCellStates(cellStates: Array<Array<MutableState<CellState>>>): Array<Array<MutableState<CellState>>> {
    return Array(cellStates.size) { row ->
        Array(cellStates[row].size) { col ->
            mutableStateOf(cellStates[row][col].value)
        }
    }
}

fun checkSolution(solution: List<List<Boolean>>, cellStates: Array<Array<MutableState<CellState>>>): Boolean {
    for (row in solution.indices){
        for (col in solution[row].indices){

            val currentCell = cellStates[row][col].value.equals(CellState.FILLED)
            if (solution[row][col]!=currentCell){
                return false
            }
        }
    }

    return true
}
fun updateSaveData(viewModel: NonogramViewModel,nonogramData: NonogramData,cellStates: Array<Array<MutableState<CellState>>>,isComplete:Boolean){

    var newState = ""

    for (row in cellStates){
        for (cell in row){
            newState += when (cell.value){
                CellState.FILLED -> "1"
                CellState.MARKED -> "2"
                CellState.EMPTY -> "0"
            }
        }
    }

    nonogramData.currentState = newState
    nonogramData.isComplete = isComplete

    viewModel.saveNonogram(
        nonogramData
    )
}
fun isInRange(min: Int, value: Int, max: Int): Boolean {
    return value >= min && value < max
}


