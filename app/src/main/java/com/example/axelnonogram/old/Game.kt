//package com.example.axelnonogram.old
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.ui.unit.sp
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Remove
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.TextButton
//import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
//import androidx.compose.material.icons.outlined.DisabledByDefault
//import androidx.compose.material.icons.filled.OpenWith
//import androidx.compose.material.icons.filled.Redo
//import androidx.compose.material.icons.filled.Square
//import androidx.compose.material.icons.filled.Undo
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.unit.Dp
//import kotlin.collections.iterator
//import kotlin.text.iterator
//
//val dict = mapOf(
//    "00000" to 'a',
//    "00001" to 'A',
//    "00010" to 'b',
//    "00011" to 'B',
//    "00100" to 'c',
//    "00101" to 'C',
//    "00110" to 'd',
//    "00111" to 'D',
//    "01000" to 'e',
//    "01001" to 'E',
//    "01010" to 'f',
//    "01011" to 'F',
//    "01100" to 'g',
//    "01101" to 'G',
//    "01110" to 'h',
//    "01111" to 'H',
//    "10000" to 'i',
//    "10001" to 'I',
//    "10010" to 'j',
//    "10011" to 'J',
//    "10100" to 'k',
//    "10101" to 'K',
//    "10110" to 'l',
//    "10111" to 'L',
//    "11000" to 'm',
//    "11001" to 'M',
//    "11010" to 'n',
//    "11011" to 'N',
//    "11100" to 'o',
//    "11101" to 'O',
//    "11110" to 'p',
//    "11111" to 'P'
//)
//
//enum class CellState {
//    EMPTY,
//    FILLED,
//    MARKED
//}
//enum class PaintMode {
//    FILL,
//    MARK,
//    CLEAR,
//    MOVE
//}
//
//data class GameInfo(
//    val width: Int,
//    val height: Int,
//    val solution: Array<Array<Boolean>>,
//    val currentState: List<List<CellState>>,
//    val rowHints: List<List<Int>>,
//    val colHints: List<List<Int>>,
//    val rowMaxHints: Int,
//    val colMaxHints: Int,
//    var isComplete: Boolean = false
//)
//
//
//fun decompress(puzzleKey: String): List<String> {
//    val list = puzzleKey.split("x")
//    val x = list[0]
//    val y = list[1]
//    val grid = list[2]
//    val result = mutableListOf<String>()
//
//
//    for (letter in grid) {
//        for ((key, value) in dict) {
//            if (value == letter) {
//                result.add(key)
//                break
//            }
//        }
//    }
//
//    return listOf(x, y, result.joinToString(""))
//}
//
//@Composable
//fun NonogramMain(puzzleCompressed: String) {
//    val puzzleDecompressed = decompress(puzzleCompressed)
//    val solution = Array(puzzleDecompressed[1].toInt()) { Array(puzzleDecompressed[0].toInt()) { true } }
//    var index = 0
//    for (y in 0 until puzzleDecompressed[1].toInt()) {
//
//        for (x in 0 until puzzleDecompressed[0].toInt()) {
//            solution[y][x] = puzzleDecompressed[2][y*puzzleDecompressed[0].toInt()+x].code == 49
//            index++
//        }
//    }
//
//
//    val rowHints = remember {
//        val hints = mutableListOf<List<Int>>()
//
//        for (row in solution) {
//            val rowHint = mutableListOf<Int>()
//            var count = 0
//
//            for (cell in row) {
//                if (cell) {
//                    count++
//                } else if (count > 0) {
//                    rowHint.add(count)
//                    count = 0
//                }
//            }
//            if (count > 0) {
//                rowHint.add(count)
//            }
//            if (rowHint.isEmpty()) {
//                rowHint.add(0)
//            }
//            hints.add(rowHint)
//        }
//        hints
//    }
//
//    val colHints = remember {
//        val hints = mutableListOf<List<Int>>()
//
//        for (column in solution[0].indices) {
//            val colHint = mutableListOf<Int>()
//            var count = 0
//
//            for (row in solution.indices) {
//                if (solution[row][column]) {
//                    count++
//                } else if (count > 0) {
//                    colHint.add(count)
//                    count = 0
//                }
//            }
//
//            if (count > 0) {
//                colHint.add(count)
//            }
//
//            if (colHint.isEmpty()) {
//                colHint.add(0)
//            }
//
//            hints.add(colHint)
//        }
//
//        hints
//    }
//
//
//    var rowMaxHints = 0
//    for (rowHint in rowHints) {
//        if (rowHint.count() > rowMaxHints) {
//            rowMaxHints = rowHint.count()
//        }
//    }
//
//    var colMaxHints = 0
//    for (colHint in colHints) {
//        if (colHint.count() > colMaxHints) {
//            colMaxHints = colHint.count()
//        }
//    }
//
//    val gameInfo = remember {
//        mutableStateOf(
//            GameInfo(
//                height = puzzleDecompressed[1].toInt(),
//                width = puzzleDecompressed[0].toInt(),
//                solution = solution,
//                currentState = List(puzzleDecompressed[1].toInt()) {
//                    List(puzzleDecompressed[0].toInt()) { CellState.EMPTY }
//                },
//                rowHints = rowHints,
//                colHints = colHints,
//                rowMaxHints = rowMaxHints,
//                colMaxHints = colMaxHints
//            )
//        )
//    }
//
//    var paintMode by remember { mutableStateOf(PaintMode.FILL) }
//
//    var zoomFactor by remember { mutableStateOf(1f) }
//    val minZoom = 0.5f
//    val maxZoom = 3.0f
//
//    val horizontalScrollState = rememberScrollState()
//    val verticalScrollState = rememberScrollState()
//
//    val lastDragCellState = remember { mutableStateOf<CellState?>(null) }
//
//    val modifiedDuringDrag = remember { mutableStateOf(mutableSetOf<Pair<Int, Int>>()) }
//
//    val cellStates = remember {
//        Array(gameInfo.value.height) { row ->
//            Array(gameInfo.value.width) { cell ->
//                mutableStateOf(CellState.EMPTY)
//            }
//        }
//    }
//
//    val cellStatesHistoryBack = remember { mutableListOf<Array<Array<MutableState<CellState>>>>() }
//    val cellStatesHistoryFor = remember { mutableListOf<Array<Array<MutableState<CellState>>>>() }
//
//
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val density = LocalDensity.current
//
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//
//    var pointerCount by remember { mutableStateOf(0) }
//
//    val gridAreaWidth = screenWidth
//    val gridAreaHeight = configuration.screenHeightDp.dp
//
//
//    val baseCellSize = if (gridAreaHeight/(gameInfo.value.height+gameInfo.value.colMaxHints) < gridAreaWidth/(gameInfo.value.width+gameInfo.value.rowMaxHints)) {
//        gridAreaHeight/(gameInfo.value.height+gameInfo.value.colMaxHints)
//    } else {
//        gridAreaWidth/(gameInfo.value.width+gameInfo.value.rowMaxHints)
//    }//.coerceAtMost(40.dp)
//
//    val cellSize = baseCellSize * zoomFactor
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize(),
////                .padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
////                    .padding(bottom = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Nonogram Puzzle",
//                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    Modifier
//                        .fillMaxSize()
//                        .background(Color.White)
//                        .pointerInput(Unit) {
//                            awaitPointerEventScope {
//                                while (true) {
//                                    val event = awaitPointerEvent()
//                                    pointerCount = event.changes.size
//                                }
//                            }
//                        }
//                        .pointerInput(Unit) {
//                            detectTransformGestures(
//                                onGesture = { _, pan, zoom, _ ->
//                                    if (pointerCount > 1) {
//                                        scale = (scale * zoom).coerceAtLeast(minZoom)
//                                            .coerceAtMost(maxZoom)
//                                        offset += pan
//                                    }
//                                }
//                            )
//                        }
//                ) {
//                    Box(
//                        Modifier
//                            .graphicsLayer(
//                                scaleX = scale,
//                                scaleY = scale,
//                                translationX = offset.x,
//                                translationY = offset.y
//                            )
//                            .width(cellSize * (gameInfo.value.rowMaxHints + gameInfo.value.width))
//                            .height(cellSize * (gameInfo.value.colMaxHints + gameInfo.value.height))
//
//                            .pointerInput(paintMode) {
//                                if (!gameInfo.value.isComplete && paintMode!=PaintMode.MOVE) {
//                                    detectDragGestures(
//                                        onDragStart = { dragStartPosition ->
//
//                                            cellStatesHistoryBack.add(copyCellStates(cellStates))
//                                            cellStatesHistoryFor.clear()
//
//
//                                            val row =
//                                                (dragStartPosition.y / (size.height / (gameInfo.value.height + gameInfo.value.colMaxHints)) - gameInfo.value.colMaxHints).toInt()
//
//                                            val col =
//                                                (dragStartPosition.x / (size.width / (gameInfo.value.width + gameInfo.value.rowMaxHints)) - gameInfo.value.rowMaxHints).toInt()
//
//                                            modifiedDuringDrag.value.clear()
//
//
//
//                                            if (between(0, row, (gameInfo.value.height)) && between(
//                                                    0,
//                                                    col,
//                                                    (gameInfo.value.width)
//                                                )
//                                            ) {
//                                                val currentCellState = cellStates[row][col].value
//
//                                                lastDragCellState.value = when (paintMode) {
//                                                    PaintMode.FILL -> {
//                                                        if (currentCellState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
//                                                    }
//
//                                                    PaintMode.MARK -> {
//                                                        if (currentCellState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
//                                                    }
//
//                                                    PaintMode.CLEAR -> {
//                                                        CellState.EMPTY
//                                                    }
//
//                                                    else -> {
//                                                        CellState.EMPTY
//                                                    }
//                                                }
//
//                                                cellStates[row][col].value =
//                                                    lastDragCellState.value!!
//                                                modifiedDuringDrag.value.add(Pair(row, col))
//
//                                            } else {
//                                                lastDragCellState.value = when (paintMode) {
//                                                    PaintMode.FILL -> {
//                                                        CellState.FILLED
//                                                    }
//
//                                                    PaintMode.MARK -> {
//                                                        CellState.MARKED
//                                                    }
//
//                                                    PaintMode.CLEAR -> {
//                                                        CellState.EMPTY
//                                                    }
//
//                                                    else -> {
//                                                        CellState.EMPTY
//                                                    }
//                                                }
//                                            }
//
//
//                                        },
//                                        onDrag = { change, _ ->
////                                        // Consume the change to prevent scrolling
////                                        change.consume()
////
//
//                                            val row =
//                                                (change.position.y / (size.height / (gameInfo.value.height + gameInfo.value.colMaxHints)) - gameInfo.value.colMaxHints).toInt()
//
//                                            val col =
//                                                (change.position.x / (size.width / (gameInfo.value.width + gameInfo.value.rowMaxHints)) - gameInfo.value.rowMaxHints).toInt()
//
//                                            if (between(0, row, (gameInfo.value.height)) && between(
//                                                    0,
//                                                    col,
//                                                    (gameInfo.value.width)
//                                                )
//                                            ) {
//                                                val cellCord = Pair(row, col)
//                                                if (!modifiedDuringDrag.value.contains(cellCord)) {
//                                                    cellStates[row][col].value =
//                                                        lastDragCellState.value!!
//                                                    modifiedDuringDrag.value.add(cellCord)
//                                                }
//                                            }
//                                        },
//                                        onDragEnd = {
//                                            gameInfo.value.isComplete =
//                                                CheckSolution(gameInfo.value.solution, cellStates)
//                                        }
//                                    )
//                                }
//                            }
//                    ) {
//                        GameGrid(
//                            height = gameInfo.value.height,
//                            width = gameInfo.value.width,
//                            cellStates = cellStates,
//                            cellSize = cellSize,
//                            rowHints = gameInfo.value.rowHints,
//                            colHints = gameInfo.value.colHints,
//                            rowHintsSize = gameInfo.value.rowMaxHints,
//                            colHintsSize = gameInfo.value.colMaxHints,
//                            onCellCLicked = { row, col ->
//
//                                cellStatesHistoryBack.add(copyCellStates(cellStates))
//                                cellStatesHistoryFor.clear()
//
//                                val currentState = cellStates[row][col].value
//
//                                cellStates[row][col].value = when (paintMode) {
//                                    PaintMode.FILL -> {
//                                        if (currentState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
//                                    }
//                                    PaintMode.MARK -> {
//                                        if (currentState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
//                                    }
//                                    PaintMode.CLEAR -> {
//                                        CellState.EMPTY
//                                    }
//                                    else -> {
//                                        currentState
//                                    }
//
//                                }
//
//                                gameInfo.value.isComplete = CheckSolution(gameInfo.value.solution,cellStates)
//                            },
//                            paintMode = paintMode,
//                            isComplete = gameInfo.value.isComplete
//
//                        )
//                    }
//                }
//            }
//        }
//        Row(
//            modifier = Modifier
//                .align(Alignment.TopCenter) // This pins it to the bottom
//                .fillMaxWidth()
//                .background(Color.Gray),
//            horizontalArrangement = Arrangement.SpaceBetween, // This will space out the buttons
//            verticalAlignment = Alignment.CenterVertically
//        ){
//
//            Button(
//                shape = RectangleShape,
//                modifier = Modifier
//                    .size(35.dp),
//                onClick = {if (cellStatesHistoryBack.isNotEmpty()) {
//                    cellStatesHistoryFor.add(copyCellStates(cellStates))
//                    for (row in cellStates.indices) {
//                        for (col in cellStates[row].indices) {
//                            cellStates[row][col].value =
//                                cellStatesHistoryBack[cellStatesHistoryBack.count() - 1][row][col].value
//                        }
//                    }
//                    cellStatesHistoryBack.removeAt(cellStatesHistoryBack.count() - 1)
//                }
//                },
//                colors = ButtonDefaults.buttonColors(
//
//                    Color.Gray
//                ),
//                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.ArrowBack,
//                        contentDescription = "Undo",
//                        modifier = Modifier.size(24.dp),
//                        tint = Color.Black
//                    )
//                }
//            }
//        }
//
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomCenter) // This pins it to the bottom
//                .fillMaxWidth()
//                .background(Color.Gray),
//            horizontalArrangement = Arrangement.SpaceBetween, // This will space out the buttons
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Docking Zoom Controls to the Left
//            ZoomControls(
//                zoomFactor = scale,
//                onZoomChanged = { newZoom -> scale = newZoom },
//                onResetZoom = { scale = 1f; offset = Offset.Zero }
//            )
//
//            Button(
//                shape = RectangleShape,
//                modifier = Modifier
//                    .size(35.dp),
//                onClick = {if (cellStatesHistoryBack.isNotEmpty()) {
//                    cellStatesHistoryFor.add(copyCellStates(cellStates))
//                    for (row in cellStates.indices) {
//                        for (col in cellStates[row].indices) {
//                            cellStates[row][col].value =
//                                cellStatesHistoryBack[cellStatesHistoryBack.count() - 1][row][col].value
//                        }
//                    }
//                    cellStatesHistoryBack.removeAt(cellStatesHistoryBack.count() - 1)
//                }
//                },
//                colors = ButtonDefaults.buttonColors(
//
//                    Color.Gray
//                ),
//                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Undo,
//                        contentDescription = "Undo",
//                        modifier = Modifier.size(24.dp),
//                        tint = Color.Black
//                    )
//                }
//            }
//            Button(
//                shape = RectangleShape,
//                modifier = Modifier
//                    .size(35.dp),
//                onClick = {
//                    if (cellStatesHistoryFor.isNotEmpty()) {
//                        cellStatesHistoryBack.add(copyCellStates(cellStates))
//                        for (row in cellStates.indices) {
//                            for (col in cellStates[row].indices) {
//                                cellStates[row][col].value =
//                                    cellStatesHistoryFor[cellStatesHistoryFor.count() - 1][row][col].value
//                            }
//                        }
//                        cellStatesHistoryFor.removeAt(cellStatesHistoryFor.count() - 1)
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(
//
//                    Color.Gray
//                ),
//                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Redo,
//                        contentDescription = "Redo",
//                        modifier = Modifier.size(24.dp),
//                        tint = Color.Black
//                    )
////                Text(text = "Move", fontSize = 12.sp)
//                }
//            }
//
//            // Docking Paint Mode Controls to the Right
//            PaintModeSelector(
//                currentPaintMode = paintMode,
//                onPaintModeChange = { newMode -> paintMode = newMode }
//            )
//
//        }
//    }
//}
//
//@Composable
//fun GameGrid(
//    height: Int,
//    width: Int,
//    cellStates: Array<Array<MutableState<CellState>>>,
//    cellSize: Dp,
//    rowHints: List<List<Int>>,
//    colHints: List<List<Int>>,
//    rowHintsSize: Int,
//    colHintsSize: Int,
//    onCellCLicked: (Int, Int) -> Unit,
//    paintMode: PaintMode,
//    isComplete: Boolean
//){
//    Column {
//        for (row in 0 until height + colHintsSize) {
//            Row {
//                for (col in 0 until width + rowHintsSize) {
//
//                    val interactionSource = remember { MutableInteractionSource() }
//                    val density = LocalDensity.current
//
//                    if (col < rowHintsSize && row < colHintsSize) {
//
//                        Box(
//                            modifier = Modifier
//                                .size(cellSize)
//                                .border(0.5.dp, Color.White)
//                                .background(Color.White)
//                        )
//                    } else if (col < rowHintsSize) {
//
//                        var currentRowHints = rowHints[row - colHintsSize]
//                        var currentHintIndex = col - (rowHintsSize - currentRowHints.count())
//
//                        Box(
//                            modifier = Modifier
//                                .size(cellSize)
//                                .border(0.5.dp, Color.Gray)
//                                .background(Color.LightGray)
//                        ) {
//                            if (currentHintIndex >= 0) {
//                                if (currentRowHints[currentHintIndex] != 0) {
//                                    Text(
//                                        text = "${currentRowHints[currentHintIndex]}",
//                                        color = Color.Black,
//                                        textAlign = TextAlign.Center,
//                                        modifier = Modifier
//                                            .fillMaxSize()
//                                            .padding(2.dp)
//                                            .wrapContentHeight(),
//                                        fontSize = with(density) {
////                                        min(
//                                            (
////                                            14.dp.toPx(),
//                                                    cellSize.toPx() * 0.4f
//                                                    ).toSp()
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    } else if (row < colHintsSize) {
//                        var currentColHints = colHints[col - rowHintsSize]
//                        var currentHintIndex = row - (colHintsSize - currentColHints.count())
//
//                        Box(
//                            modifier = Modifier
//                                .size(cellSize)
//                                .border(0.5.dp, Color.Gray)
//                                .background(Color.LightGray),
//
//                        ) {
//                            if (currentHintIndex >= 0) {
//                                if (currentColHints[currentHintIndex] != 0) {
//                                    Text(
//                                        text = "${currentColHints[currentHintIndex]}",
//                                        color = Color.Black,
//                                        textAlign = TextAlign.Center,
//                                        modifier = Modifier
//                                            .fillMaxSize()
//                                            .padding(2.dp)
//                                            .wrapContentHeight(),
//                                        fontSize = with(density) {
////                                        min(
//                                            (
////                                            14.dp.toPx(),
//                                                    cellSize.toPx() * 0.4f
//                                                    ).toSp()
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                    else {
//                        Box(
//                            modifier = Modifier
//                                .size(cellSize)
//                                .border(0.5.dp, Color.Gray)
//                                .background(
//                                    when (cellStates[row - colHintsSize][col - rowHintsSize].value) {
//                                        CellState.FILLED -> Color.Black
//                                        CellState.MARKED -> Color.White
//                                        CellState.EMPTY -> Color.White
//                                    }
//                                )
//                                .then(
//                                    if (paintMode != PaintMode.MOVE && !isComplete) {
//                                        Modifier.clickable(
//                                            interactionSource = interactionSource,
//                                            indication = null
//                                        ) {
//                                            onCellCLicked(row - colHintsSize, col - rowHintsSize)
//                                        }
//                                    } else {
//                                        Modifier // No clickable modifier added
//                                    }
//                                )
//                        ) {
//                            if (cellStates[row - colHintsSize][col - rowHintsSize].value == CellState.MARKED && !isComplete) {
////                                Text(
////                                    text = "X",
////                                    color = Color.Black,
////                                    textAlign = TextAlign.Center,
////                                    modifier = Modifier
////                                        .fillMaxSize()
////                                        .wrapContentHeight(),
////                                    fontSize = with(density) {
//////                                        min(
////                                        (
//////                                            14.dp.toPx(),
////                                            cellSize.toPx() * 0.8f
////                                        ).toSp()
////                                    }
////                                )
//                                Icon(
//                                    imageVector = Icons.Filled.Clear,
//                                    contentDescription = "",
//                                    modifier = Modifier.fillMaxSize(), //.size(24.dp),
//                                    tint = Color.Black
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun PaintModeSelector(
//    currentPaintMode: PaintMode,
//    onPaintModeChange: (PaintMode) -> Unit
//) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Button(
//            shape = RectangleShape,
//            modifier = Modifier
//                .size(35.dp),
//            onClick = { onPaintModeChange(PaintMode.FILL) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (currentPaintMode == PaintMode.FILL)
//                    Color.White
//                else Color.Gray
//            ),
//            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Square,
//                    contentDescription = "Fill Mode",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.Black
//                )
////                Text(text = "Fill", fontSize = 12.sp)
//            }
//        }
//
//        Button(
//            shape = RectangleShape,
//            modifier = Modifier
//                .size(35.dp),
//            onClick = { onPaintModeChange(PaintMode.MARK) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (currentPaintMode == PaintMode.MARK)
//                    Color.White
//                else Color.Gray
//            ),
//            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.DisabledByDefault,
//                    contentDescription = "Mark Mode",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.Black
//                )
////                Text(text = "Mark", fontSize = 12.sp)
//            }
//        }
//
//        Button(
//            shape = RectangleShape,
//            modifier = Modifier
//                .size(35.dp),
//            onClick = { onPaintModeChange(PaintMode.CLEAR) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (currentPaintMode == PaintMode.CLEAR)
//                    Color.White
//                else Color.Gray
//            ),
//            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.CheckBoxOutlineBlank,
//                    contentDescription = "Clear Mode",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.Black
//                )
////                Text(text = "Clear", fontSize = 12.sp)
//            }
//        }
//
//        Button(
//            shape = RectangleShape,
//            modifier = Modifier
//                .size(35.dp),
//            onClick = { onPaintModeChange(PaintMode.MOVE) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (currentPaintMode == PaintMode.MOVE)
//                    Color.White
//                else Color.Gray
//            ),
//            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.OpenWith,
//                    contentDescription = "Move Mode",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.Black
//                )
////                Text(text = "Move", fontSize = 12.sp)
//            }
//        }
//    }
//}
//
//@Composable
//fun ZoomControls(
//    zoomFactor: Float,
//    onZoomChanged: (Float) -> Unit,
//    onResetZoom: () -> Unit
//) {
//    val minZoom = 0.5f
//    val maxZoom = 3.0f
//
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        IconButton(
//            onClick = {
//                onZoomChanged((zoomFactor - 0.25f).coerceAtLeast(minZoom))
//            },
//            modifier = Modifier.size(35.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Remove,
//                contentDescription = "Zoom Out",
//                modifier = Modifier.size(20.dp)
//            )
//        }
//
//        TextButton(
//            shape = RectangleShape,
//            onClick = onResetZoom,
//            modifier = Modifier
//                .height(35.dp)
//                .width(75.dp)
//        ) {
//            Text(
//                text = "${(zoomFactor * 100).toInt()}%",
//                fontSize = 14.sp,
//                color = Color.Black,
//                textAlign = TextAlign.Center
//            )
//        }
//
//        IconButton(
//            onClick = {
//                onZoomChanged((zoomFactor + 0.25f).coerceAtMost(maxZoom))
//            },
//            modifier = Modifier.size(32.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Zoom In",
//                modifier = Modifier.size(20.dp)
//            )
//        }
//    }
//}
//
//fun copyCellStates(cellStates: Array<Array<MutableState<CellState>>>): Array<Array<MutableState<CellState>>> {
//    return Array(cellStates.size) { row ->
//        Array(cellStates[row].size) { col ->
//            mutableStateOf(cellStates[row][col].value)
//        }
//    }
//}
//
//fun CheckSolution(solution: Array<Array<Boolean>>, cellStates: Array<Array<MutableState<CellState>>>): Boolean {
//    for (row in solution.indices){
//        for (col in solution[row].indices){
//
//            val currentCell = cellStates[row][col].value.equals(CellState.FILLED)
//            Log.e("AABBVV","${solution[row][col]},${currentCell}")
//            if (solution[row][col]!=currentCell){
//                Log.e("AABBVV","FALSE FAGGOT")
//                return false
//            }
//        }
//    }
//    Log.e("AABBVV","TRUE FAGGOT")
//    return true
//}
//
//fun between(firstNumber: Int, middleNumber:Int, lastNumber:Int): Boolean {
//    if (middleNumber>=firstNumber){
//        if (middleNumber<lastNumber){
//            return true
//        }
//    }
//    return false
//}