//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package com.example.axelnonogram
//
//import android.R.attr.contentDescription
//import android.util.Log
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
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
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.semantics.semantics
//import androidx.compose.ui.unit.Dp
//import kotlin.div
//import kotlin.text.toInt
//
//enum class PaintMode {
//    FILL,
//    MARK,
//    CLEAR,
//    MOVE
//}
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
//
//data class CellChange(
//    val row: Int,
//    val col: Int,
//    val oldState: CellState,
//    val newState: CellState
//)
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
//    val gameHistory = remember { GameHistory() }
//    val pendingChanges = remember { mutableStateOf(mutableListOf<CellChange>()) }
//
//    val isCompleted = remember { mutableStateOf(false) }
//    val hideWinPopup = remember { mutableStateOf(false) }
//
//    val completionAlpha = animateFloatAsState(
//        targetValue = if (isCompleted.value) 0.7f else 0f,
//        animationSpec = tween(durationMillis = 500)
//    )
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
//    Box(modifier = Modifier.fillMaxSize()) {
//        // Main game view
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Game title and header
//            TopAppBar(
//                title = { Text("Nonogram Puzzle") },
//                actions = {
//                    // Reset button
//                    IconButton(onClick = {
//                        // Add reset functionality
//                        resetGame(cellStates)
//                        gameHistory.clear()
//                        isCompleted.value = false
//                        hideWinPopup.value = false
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.Clear,
//                            contentDescription = "Reset Puzzle"
//                        )
//                    }
//                }
//            )
//
//            // Game grid with zoom container
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//                    .clipToBounds()
//                    .background(Color.White)
//                    .pointerInput(Unit) {
//                        detectTransformGestures { _, pan, zoom, _ ->
//                            // Only allow zoom and pan in MOVE mode or when completed
//                            if (paintMode == PaintMode.MOVE || isCompleted.value) {
//                                scale = (scale * zoom).coerceIn(0.5f, 3f)
//                                offset += pan
//                            }
//                        }
//                    }
//            ) {
//                // Game grid with transforms applied
//                Box(
//                    modifier = Modifier
//                        .graphicsLayer(
//                            scaleX = scale,
//                            scaleY = scale,
//                            translationX = offset.x,
//                            translationY = offset.y
//                        )
//                        .width(cellSize * (gameInfo.value.rowMaxHints + gameInfo.value.width))
//                        .height(cellSize * (gameInfo.value.colMaxHints + gameInfo.value.height))
//                        .pointerInput(paintMode, isCompleted.value) {
//                            // Only enable drag gestures when not in MOVE mode and not completed
//                            if (!isCompleted.value && paintMode != PaintMode.MOVE) {
//                                detectDragGestures(
//                                    onDragStart = { position ->
//                                        // Clear pending changes for this drag operation
//                                        pendingChanges.value.clear()
//
//                                        // Calculate cell position
//
//                                        val row =
//                                            (position.y / (size.height / (gameInfo.value.height + gameInfo.value.colMaxHints)) - gameInfo.value.colMaxHints).toInt()
//
//                                        val col =
//                                            (position.x / (size.width / (gameInfo.value.width + gameInfo.value.rowMaxHints)) - gameInfo.value.rowMaxHints).toInt()
//
//                                        if (isInRange(0, row, gameInfo.value.height) && isInRange(0, col, gameInfo.value.width)) {
//                                            // Record original state before changing
//                                            val originalState = cellStates[row][col].value
////                                            val newState = getNewCellState(originalState, paintMode)
//                                            lastDragCellState.value = getNewCellState(originalState, paintMode)
//                                            // Record the change
//                                            pendingChanges.value.add(
//                                                CellChange(row, col, originalState, lastDragCellState.value!!)
//                                            )
//
//                                            // Apply the change
//                                            cellStates[row][col].value = lastDragCellState.value!!
//                                        }
//                                    },
//                                    onDrag = { change, _ ->
//                                        // Calculate cell position for drag
//
//                                        val row =
//                                            (change.position.y / (size.height / (gameInfo.value.height + gameInfo.value.colMaxHints)) - gameInfo.value.colMaxHints).toInt()
//
//                                        val col =
//                                            (change.position.x / (size.width / (gameInfo.value.width + gameInfo.value.rowMaxHints)) - gameInfo.value.rowMaxHints).toInt()
//
//                                        if (isInRange(0, row, gameInfo.value.height) && isInRange(0, col, gameInfo.value.width)) {
//                                            // Check if this cell was already modified in this drag
//                                            val alreadyModified = pendingChanges.value.any {
//                                                it.row == row && it.col == col
//                                            }
//
//                                            if (!alreadyModified) {
//                                                // Record original state
//                                                val originalState = cellStates[row][col].value
////                                                val newState = getNewCellState(originalState, paintMode)
//
//                                                // Record the change
//                                                pendingChanges.value.add(
//                                                    CellChange(row, col, originalState, lastDragCellState.value!!)
//                                                )
//
//                                                // Apply the change
//                                                cellStates[row][col].value = lastDragCellState.value!!
//                                            }
//                                        }
//                                    },
//                                    onDragEnd = {
//                                        // If any changes were made during this drag, record them for undo
//                                        if (pendingChanges.value.isNotEmpty()) {
//                                            gameHistory.recordChanges(pendingChanges.value.toList())
//
//                                            // Check if solution is complete
//                                            isCompleted.value = checkSolution(gameInfo.value.solution, cellStates)
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                ) {
//                    // Game grid component
//                    GameGrid(
//                        height = gameInfo.value.height,
//                        width = gameInfo.value.width,
//                        cellStates = cellStates,
//                        cellSize = cellSize,
//                        rowHints = gameInfo.value.rowHints,
//                        colHints = gameInfo.value.colHints,
//                        rowHintsSize = gameInfo.value.rowMaxHints,
//                        colHintsSize = gameInfo.value.colMaxHints,
//                        onCellClicked = { row, col ->
//                            if (!isCompleted.value && paintMode != PaintMode.MOVE) {
//                                // Record the single cell change
//                                val originalState = cellStates[row][col].value
//                                val newState = getNewCellState(originalState, paintMode)
//
//                                gameHistory.recordChanges(
//                                    listOf(CellChange(row, col, originalState, newState))
//                                )
//
//                                // Apply the change
//                                cellStates[row][col].value = newState
//
//                                // Check if solution is complete
//                                isCompleted.value = checkSolution(gameInfo.value.solution, cellStates)
//                            }
//                        },
//                        paintMode = paintMode,
//                        isComplete = isCompleted.value
//                    )
//
//                    // Show completion overlay when puzzle is solved
//
//                }
//                if (isCompleted.value && !hideWinPopup.value) {
//                    Box(
//                        modifier = Modifier
//                            .matchParentSize()
//                            .background(Color.Green.copy(alpha = completionAlpha.value))
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(
//                                "Puzzle Completed!",
//                                style = MaterialTheme.typography.headlineMedium,
//                                color = Color.White,
//
//                                )
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            Button(
//                                onClick = {
//                                    // Clear the completion overlay but keep the solved puzzle visible
//                                    hideWinPopup.value = true
//                                },
//                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
//                            ) {
//                                Text("Continue", color = Color.Black)
//                            }
//                        }
//                    }
//                }
//            }
//
//            // Bottom control bar with paint modes and undo/redo
//            BottomAppBar(
//                modifier = Modifier.height(56.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // Undo/Redo controls
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        IconButton(
//                            onClick = {
//                                if (gameHistory.undo(cellStates)) {
//                                    // Check solution state after undo
//                                    isCompleted.value = checkSolution(gameInfo.value.solution, cellStates)
//                                }
//                            },
//                            enabled = gameHistory.canUndo()
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Undo,
//                                contentDescription = "Undo",
//                                tint = if (gameHistory.canUndo()) Color.Black else Color.Gray
//                            )
//                        }
//
//                        IconButton(
//                            onClick = {
//                                if (gameHistory.redo(cellStates)) {
//                                    // Check solution state after redo
//                                    isCompleted.value = checkSolution(gameInfo.value.solution, cellStates)
//                                }
//                            },
//                            enabled = gameHistory.canRedo()
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Redo,
//                                contentDescription = "Redo",
//                                tint = if (gameHistory.canRedo()) Color.Black else Color.Gray
//                            )
//                        }
//                    }
//
//                    // Zoom controls
//                    ZoomControls(
//                        zoomFactor = scale,
//                        onZoomChanged = { newZoom -> scale = newZoom },
//                        onResetZoom = { scale = 1f; offset = Offset.Zero }
//                    )
//
//                    // Paint mode controls
//                    PaintModeSelector(
//                        currentPaintMode = paintMode,
//                        onPaintModeChange = { newMode -> paintMode = newMode }
//                    )
//                }
//            }
//        }
//    }
//}
//
//fun getNewCellState(currentState: CellState, paintMode: PaintMode): CellState {
//    return when (paintMode) {
//        PaintMode.FILL -> {
//            if (currentState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
//        }
//        PaintMode.MARK -> {
//            if (currentState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
//        }
//        PaintMode.CLEAR -> CellState.EMPTY
//        PaintMode.MOVE -> currentState // No change in move mode
//    }
//}
//fun resetGame(cellStates: Array<Array<MutableState<CellState>>>) {
//    for (row in cellStates.indices) {
//        for (col in cellStates[row].indices) {
//            cellStates[row][col].value = CellState.EMPTY
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
//    onCellClicked: (Int, Int) -> Unit,
//    paintMode: PaintMode,
//    isComplete: Boolean
//) {
//    // Use LazyColumn/LazyRow for better performance with large puzzles
//    Column {
//        for (row in 0 until height + colHintsSize) {
//            Row {
//                for (col in 0 until width + rowHintsSize) {
//                    val interactionSource = remember { MutableInteractionSource() }
//                    val density = LocalDensity.current
//
//                    when {
//                        // Empty corner cell
//                        col < rowHintsSize && row < colHintsSize -> {
//                            Box(
//                                modifier = Modifier
//                                    .size(cellSize)
//                                    .border(0.5.dp, Color.White)
//                                    .background(Color.White)
//                            )
//                        }
//
//                        // Row hints
//                        col < rowHintsSize -> {
//                            val currentRowHints = rowHints[row - colHintsSize]
//                            val currentHintIndex = col - (rowHintsSize - currentRowHints.size)
//
//                            Box(
//                                modifier = Modifier
//                                    .size(cellSize)
//                                    .border(0.5.dp, Color.Gray)
//                                    .background(Color.LightGray),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                if (currentHintIndex >= 0 && currentRowHints[currentHintIndex] != 0) {
//                                    Text(
//                                        text = "${currentRowHints[currentHintIndex]}",
//                                        color = Color.Black,
//                                        textAlign = TextAlign.Center,
//                                        fontSize = with(density) { (cellSize.toPx() * 0.4f).toSp() }
//                                    )
//                                }
//                            }
//                        }
//
//                        // Column hints
//                        row < colHintsSize -> {
//                            val currentColHints = colHints[col - rowHintsSize]
//                            val currentHintIndex = row - (colHintsSize - currentColHints.size)
//
//                            Box(
//                                modifier = Modifier
//                                    .size(cellSize)
//                                    .border(0.5.dp, Color.Gray)
//                                    .background(Color.LightGray),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                if (currentHintIndex >= 0 && currentColHints[currentHintIndex] != 0) {
//                                    Text(
//                                        text = "${currentColHints[currentHintIndex]}",
//                                        color = Color.Black,
//                                        textAlign = TextAlign.Center,
//                                        fontSize = with(density) { (cellSize.toPx() * 0.4f).toSp() }
//                                    )
//                                }
//                            }
//                        }
//
//                        // Game cells
//                        else -> {
//                            val rowIndex = row - colHintsSize
//                            val colIndex = col - rowHintsSize
//                            val cellState = cellStates[rowIndex][colIndex].value
//
//                            Box(
//                                modifier = Modifier
//                                    .size(cellSize)
//                                    .border(0.5.dp, Color.Gray)
//                                    .background(
//                                        when (cellState) {
//                                            CellState.FILLED -> Color.Black
//                                            else -> Color.White
//                                        }
//                                    )
//                                    .then(
//                                        if (paintMode != PaintMode.MOVE && !isComplete) {
//                                            Modifier
//                                                .clickable(
//                                                    interactionSource = interactionSource,
//                                                    indication = null
//                                                ) {
//                                                    onCellClicked(rowIndex, colIndex)
//                                                }
//                                                // Add content description for accessibility
//                                                .semantics {
//                                                }
//                                        } else {
//                                            Modifier
//                                        }
//                                    ),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                if (cellState == CellState.MARKED && !isComplete) {
//                                    Icon(
//                                        imageVector = Icons.Filled.Clear,
//                                        contentDescription = null,
//                                        modifier = Modifier.fillMaxSize(0.8f),
//                                        tint = Color.Black
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// 7. Improved ZoomControls with better layout and interaction
//@Composable
//fun ZoomControls(
//    zoomFactor: Float,
//    onZoomChanged: (Float) -> Unit,
//    onResetZoom: () -> Unit
//) {
//    val minZoom = 0.5f
//    val maxZoom = 3.0f
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(4.dp)
//    ) {
//        IconButton(
//            onClick = { onZoomChanged((zoomFactor - 0.25f).coerceAtLeast(minZoom)) }
//        ) {
//            Icon(
//                imageVector = Icons.Default.Remove,
//                contentDescription = "Zoom Out"
//            )
//        }
//
//        TextButton(
//            onClick = onResetZoom,
//            modifier = Modifier.widthIn(min = 60.dp)
//        ) {
//            Text(
//                text = "${(zoomFactor * 100).toInt()}%",
//                fontSize = 14.sp
//            )
//        }
//
//        IconButton(
//            onClick = { onZoomChanged((zoomFactor + 0.25f).coerceAtMost(maxZoom)) }
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Zoom In"
//            )
//        }
//    }
//}
//
//// 8. Improved PaintModeSelector with better labeling and accessibility
//@Composable
//fun PaintModeSelector(
//    currentPaintMode: PaintMode,
//    onPaintModeChange: (PaintMode) -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(4.dp)
//    ) {
//        // Fill button
//        IconButton(
//            onClick = { onPaintModeChange(PaintMode.FILL) },
//            modifier = Modifier.semantics {
//            }
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Square,
//                contentDescription = null,
//                tint = if (currentPaintMode == PaintMode.FILL)
//                    MaterialTheme.colorScheme.primary
//                else Color.Gray
//            )
//        }
//
//        // Mark button
//        IconButton(
//            onClick = { onPaintModeChange(PaintMode.MARK) },
//            modifier = Modifier.semantics {
//            }
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.DisabledByDefault,
//                contentDescription = null,
//                tint = if (currentPaintMode == PaintMode.MARK)
//                    MaterialTheme.colorScheme.primary
//                else Color.Gray
//            )
//        }
//
//        // Clear button
//        IconButton(
//            onClick = { onPaintModeChange(PaintMode.CLEAR) },
//            modifier = Modifier.semantics {
//            }
//        ) {
//            Icon(
//                imageVector = Icons.Filled.CheckBoxOutlineBlank,
//                contentDescription = null,
//                tint = if (currentPaintMode == PaintMode.CLEAR)
//                    MaterialTheme.colorScheme.primary
//                else Color.Gray
//            )
//        }
//
//        // Move button
//        IconButton(
//            onClick = { onPaintModeChange(PaintMode.MOVE) },
//            modifier = Modifier.semantics {
//            }
//        ) {
//            Icon(
//                imageVector = Icons.Filled.OpenWith,
//                contentDescription = null,
//                tint = if (currentPaintMode == PaintMode.MOVE)
//                    MaterialTheme.colorScheme.primary
//                else Color.Gray
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
//fun checkSolution(solution: Array<Array<Boolean>>, cellStates: Array<Array<MutableState<CellState>>>): Boolean {
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
//fun isInRange(min: Int, value: Int, max: Int): Boolean {
//    return value >= min && value < max
//}
//
//class GameHistory {
//    private val undoStack = mutableListOf<List<CellChange>>()
//    private val redoStack = mutableListOf<List<CellChange>>()
//
//    fun recordChanges(changes: List<CellChange>) {
//        undoStack.add(changes)
//        redoStack.clear() // Clear redo stack when new changes are made
//    }
//
//    fun canUndo(): Boolean = undoStack.isNotEmpty()
//
//    fun canRedo(): Boolean = redoStack.isNotEmpty()
//
//    fun undo(cellStates: Array<Array<MutableState<CellState>>>): Boolean {
//        if (!canUndo()) return false
//
//        val changes = undoStack.removeAt(undoStack.lastIndex)
//        val reverseChanges = mutableListOf<CellChange>()
//
//        // Apply reverse changes
//        for (change in changes) {
//            reverseChanges.add(
//                CellChange(
//                    row = change.row,
//                    col = change.col,
//                    oldState = cellStates[change.row][change.col].value,
//                    newState = change.oldState
//                )
//            )
//            cellStates[change.row][change.col].value = change.oldState
//        }
//
//        redoStack.add(reverseChanges)
//        return true
//    }
//
//    fun redo(cellStates: Array<Array<MutableState<CellState>>>): Boolean {
//        if (!canRedo()) return false
//
//        val changes = redoStack.removeAt(redoStack.lastIndex)
//        val reverseChanges = mutableListOf<CellChange>()
//
//        // Apply redo changes
//        for (change in changes) {
//            reverseChanges.add(
//                CellChange(
//                    row = change.row,
//                    col = change.col,
//                    oldState = cellStates[change.row][change.col].value,
//                    newState = change.oldState
//                )
//            )
//            cellStates[change.row][change.col].value = change.oldState
//        }
//
//        undoStack.add(reverseChanges)
//        return true
//    }
//
//    fun clear() {
//        undoStack.clear()
//        redoStack.clear()
//    }
//}