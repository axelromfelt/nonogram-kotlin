package com.example.axelnonogram.game

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.axelnonogram.NonogramData
import com.example.axelnonogram.NonogramViewModel

@Composable
fun InitiateCreateNonogram(onSuccess: (nonogram:String) -> Unit){
    var rowCount = remember { mutableStateOf("")}
    var colCount = remember { mutableStateOf("")}
    Column(){
        TextField(
            value = rowCount.value,
            onValueChange = { rowCount.value = it },
            label = { Text("Enter height") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = colCount.value,
            onValueChange = { colCount.value = it },
            label = { Text("Enter width") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            try {
                val height = rowCount.value.toInt()
                val width = colCount.value.toInt()
                if (width>0 && height >0){
                    var nonogram = "${width}x${height}x"
                    for (i in 0 until (height*width+(height*width)%5)/5){
                        nonogram += "a"
                    }
                    onSuccess(nonogram)
                }
            } finally {
            }
            }
        ){
            Text(text = "Create Nonogram")
        }
    }
}


@Composable
fun CreateNonogram(viewModel: NonogramViewModel, nonogram: NonogramData){
    val nonogramInfo = remember { mutableStateOf(loadNonogramInfo(nonogram.nonogram)) }



    val cellStates = remember {
        val rowCount = nonogramInfo.value.height-nonogramInfo.value.colHintsSize
        val colCount = nonogramInfo.value.width -nonogramInfo.value.rowHintsSize
        val tempArray = Array(rowCount) { row ->
            Array(colCount) { cell ->
                mutableStateOf(CellState.EMPTY)
            }
        }
            for (row in 0 until rowCount){
                for (col in 0 until colCount){
                    tempArray[row][col].value = when (nonogramInfo.value.solution[row][col]){
                        true -> CellState.FILLED
                        false -> CellState.EMPTY
                    }
                }
            }
        tempArray
    }

    for (row in cellStates){
        for (cell in row){
            Log.e("AAAAA","${cell.value}")
        }
    }

    var paintMode by remember { mutableStateOf(PaintMode.FILL) }

    var zoomFactor by remember { mutableStateOf(1f) }

    val lastDragCellState = remember { mutableStateOf<CellState?>(null) }





    val modifiedDuringDrag = remember { mutableListOf<CellsHistory>() }

    val cellPastState = remember { mutableListOf<List<CellsHistory>>() }
    val cellFutureState = remember { mutableListOf<List<CellsHistory>>() }

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

                            if (paintMode == PaintMode.MOVE) {
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
                        .pointerInput(paintMode) {
                            if (paintMode != PaintMode.MOVE) {
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

                                            lastDragCellState.value =
                                                getNewCellState(originalState, paintMode)

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

                                            updateNonogramData(viewModel, nonogram, cellStates)
                                        }
                                    }
                                )
                            }
                        }
                ) {

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
                            if (paintMode != PaintMode.MOVE) {

                                val originalState = cellStates[row][col].value
                                val newState = getNewCellState(originalState, paintMode)

                                cellPastState.add(listOf(CellsHistory(row, col, originalState, newState)))
                                cellFutureState.clear()

                                cellStates[row][col].value = newState


                                updateNonogramData(viewModel, nonogram,cellStates)

                            }
                        },
                        paintMode = paintMode,
                        isComplete = false
                    )


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

                            if (cellPastState.isNotEmpty()) {
                                val cells = cellPastState.removeAt(cellPastState.lastIndex)
                                for (cell in cells) {
                                    cellStates[cell.row][cell.col].value = cell.pastState
                                }
                                cellFutureState.add(cells)
                            }
                            updateNonogramData(viewModel, nonogram,cellStates)

                        },
                        onRedo = {

                            if (cellFutureState.isNotEmpty()) {
                                val cells = cellFutureState.removeAt(cellFutureState.lastIndex)
                                for (cell in cells) {
                                    cellStates[cell.row][cell.col].value = cell.futureState
                                }
                                cellPastState.add(cells)
                            }
                            updateNonogramData(viewModel, nonogram,cellStates)

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

fun updateNonogramData(viewModel: NonogramViewModel,nonogramData: NonogramData,cellStates:  Array<Array<MutableState<CellState>>>){
    var newState = ""

    for (row in cellStates){
        for (cell in row){
            newState += when (cell.value){
                CellState.FILLED -> "1"
                CellState.MARKED -> "0"
                CellState.EMPTY -> "0"
            }
        }
    }
    for (i in 0 until newState.count()%5 ) {
        newState+="0"
    }

    var newNonogram = "${cellStates[0].count()}x${cellStates.count()}x"

    for (i in 0 until newState.count()/5 ) {

        for ((key, value) in conversionTable) {
            if (key == newState.substring(i*5, i*5+5)) {
                newNonogram+=value
                break
            }
        }
    }
    nonogramData.nonogram = newNonogram
    nonogramData.isComplete = false
    nonogramData.currentState = null
    viewModel.saveNonogram(
        nonogramData
    )
}