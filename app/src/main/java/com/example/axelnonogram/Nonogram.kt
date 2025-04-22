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

// Cell state enum
enum class CellState {
    EMPTY,
    FILLED,
    MARKED
}

// Paint mode enum
enum class PaintMode {
    FILL,
    MARK
}

// GameState class to hold the current state of the game
data class GameState(
    val size: Int,
    val solution: List<List<Boolean>>,
    val currentState: List<List<CellState>>,
    val rowHints: List<List<Int>>,
    val colHints: List<List<Int>>,
    val isComplete: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NonogramGame(samplePuzzle: List<List<Boolean>>) {
    // Sample 5x5 nonogram puzzle

    // Generate row hints from the puzzle
    val rowHints = remember {
        samplePuzzle.map { row ->
            val hints = mutableListOf<Int>()
            var count = 0
            for (cell in row) {
                if (cell) {
                    count++
                } else if (count > 0) {
                    hints.add(count)
                    count = 0
                }
            }
            if (count > 0) {
                hints.add(count)
            }
            if (hints.isEmpty()) {
                hints.add(0)
            }
            hints
        }
    }

    // Generate column hints from the puzzle
    val colHints = remember {
        val hints = mutableListOf<List<Int>>()

        for (colIndex in samplePuzzle[0].indices) {
            val colHint = mutableListOf<Int>()
            var count = 0

            for (rowIndex in samplePuzzle.indices) {
                if (samplePuzzle[rowIndex][colIndex]) {
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

    // Create initial game state
    val gameState = remember {
        mutableStateOf(
            GameState(
                size = samplePuzzle.size,
                solution = samplePuzzle,
                currentState =
                List(samplePuzzle.size) {
                    List(samplePuzzle[0].size) { CellState.EMPTY }
                },
                rowHints = rowHints,
                colHints = colHints
            )
        )
    }

    // Current paint mode
    var paintMode by remember { mutableStateOf(PaintMode.FILL) }

    // Add zoom functionality
    var zoomFactor by remember { mutableStateOf(1f) }
    val minZoom = 0.5f
    val maxZoom = 3.0f

    // Scroll states for panning when zoomed
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    // Last cell state during drag operation
    val lastDragCellState = remember { mutableStateOf<CellState?>(null) }
    // Track which cells have been modified during current drag to prevent flicker
    val modifiedDuringDrag = remember { mutableStateOf(mutableSetOf<Pair<Int, Int>>()) }

    // Create individual cell states to update without recreating entire grid
    val cellStates = remember {
        Array(gameState.value.size) { row ->
            Array(gameState.value.size) { col ->
                mutableStateOf(CellState.EMPTY)
            }
        }
    }

    // Get screen width to calculate cell size
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    // Calculate responsive cell size based on screen width and zoom factor
    // Account for the hint area (40.dp) and some padding
    var rowHintsSize = 0
    for (rowHint in rowHints) {
        if (rowHint.count() > rowHintsSize){
            rowHintsSize = rowHint.count()
        }
    }
    var colHintsSize = 0
    for (colHint in colHints) {
        if (colHint.count() > colHintsSize){
            colHintsSize = colHint.count()
        }
    }
    val gridAreaWidth = screenWidth - (16+rowHintsSize*20).dp // 40.dp for hints + 16.dp for padding
    val baseCellSize = (gridAreaWidth / (gameState.value.size+rowHintsSize)).coerceAtMost(40.dp)
    val cellSize = baseCellSize * zoomFactor

    // Calculate hint area sizes proportionally
    val hintAreaWidth = baseCellSize

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nonogram Puzzle ${rowHintsSize},${gameState.value.size}",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp)
            )

            // Paint mode toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mode: ",
                    fontSize = 12.sp
                )

                Button(
                    onClick = {
                        paintMode =
                            if (paintMode == PaintMode.FILL) PaintMode.MARK else PaintMode.FILL
                    },
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                        if (paintMode == PaintMode.FILL)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector =
                            if (paintMode == PaintMode.FILL) Icons.Filled.Edit
                            else Icons.Filled.Clear,
                            contentDescription = "Paint Mode",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (paintMode == PaintMode.FILL) "Fill" else "Mark",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Zoom controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Zoom: ",
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 4.dp)
            )

            IconButton(
                onClick = {
                    zoomFactor = (zoomFactor - 0.25f).coerceAtLeast(minZoom)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "${(zoomFactor * 100).toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {
                    zoomFactor = (zoomFactor + 0.25f).coerceAtMost(maxZoom)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Game board inside a scrollable container when zoomed
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Game content with grid
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Column hints area


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Row hints - vertically scrollable when zoomed


                    // Scrollable grid container
                    Box(
                        modifier = Modifier
                            .size(baseCellSize * gameState.value.size)
                            .horizontalScroll(horizontalScrollState)
                            .verticalScroll(verticalScrollState)
                            .clipToBounds()
                    ) {
                        // Grid with pinch-to-zoom capability
                        Log.e("TAGGGG","${gameState.value.size}")
                        Box(
                            modifier = Modifier
                                .size(cellSize * gameState.value.size)
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        // Update zoom factor with pinch gesture
                                        zoomFactor = (zoomFactor * zoom).coerceIn(minZoom, maxZoom)
                                    }
                                }
                                .pointerInput(paintMode) {
                                    detectDragGestures(
                                        onDragStart = { dragStartPosition ->
                                            // Convert position to grid coordinates considering zoom
                                            val row =
                                                (dragStartPosition.y / (size.height / gameState.value.size)).toInt()
                                                    .coerceIn(0, gameState.value.size - 1)
                                            val col =
                                                (dragStartPosition.x / (size.width / gameState.value.size)).toInt()
                                                    .coerceIn(0, gameState.value.size - 1)

                                            // Clear the modified cells set at the start of a new drag
                                            modifiedDuringDrag.value.clear()

                                            // Determine the state to apply based on the paint mode and current cell state
                                            val currentCellState = cellStates[row][col].value

                                            lastDragCellState.value = when (paintMode) {
                                                PaintMode.FILL -> {
                                                    if (currentCellState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
                                                }

                                                PaintMode.MARK -> {
                                                    if (currentCellState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
                                                }
                                            }

                                            // Update the cell and mark it as modified
                                            cellStates[row][col].value = lastDragCellState.value!!
                                            modifiedDuringDrag.value.add(Pair(row, col))

                                            // Update the game state
                                            updateGameState(gameState, cellStates)
                                        },
                                        onDrag = { change, _ ->
                                            // Consume the change to prevent scrolling
                                            change.consumeAllChanges()

                                            // Convert position to grid coordinates considering zoom
                                            val row =
                                                (change.position.y / (size.height / gameState.value.size)).toInt()
                                                    .coerceIn(0, gameState.value.size - 1)
                                            val col =
                                                (change.position.x / (size.width / gameState.value.size)).toInt()
                                                    .coerceIn(0, gameState.value.size - 1)

                                            // Check if this cell has already been modified in this drag operation
                                            val cellCoord = Pair(row, col)
                                            if (!modifiedDuringDrag.value.contains(cellCoord)) {
                                                // Update the cell and mark it as modified
                                                cellStates[row][col].value =
                                                    lastDragCellState.value!!
                                                modifiedDuringDrag.value.add(cellCoord)

                                                // Update the game state
                                                updateGameState(gameState, cellStates)
                                            }
                                        },
                                        onDragEnd = {
                                            // Update completion status
                                            val newGameState = createGameStateFromCellStates(
                                                gameState.value,
                                                cellStates
                                            )
                                            gameState.value = newGameState.copy(
                                                isComplete = checkCompletion(
                                                    newGameState.currentState,
                                                    gameState.value.solution
                                                )
                                            )
                                        }
                                    )
                                }
                        ) {
                            // Game grid inside the box with drag handler
                            GridBoard(
                                size = gameState.value.size,
                                cellStates = cellStates,
                                cellSize = cellSize,
                                paintMode = paintMode,
                                onCellClick = { row, col ->
                                    // Apply the appropriate state based on paint mode and current state
                                    val currentState = cellStates[row][col].value

                                    cellStates[row][col].value = when (paintMode) {
                                        PaintMode.FILL -> {
                                            if (currentState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
                                        }
                                        PaintMode.MARK -> {
                                            if (currentState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
                                        }
                                    }

                                    // Update the game state
                                    val newGameState = createGameStateFromCellStates(
                                        gameState.value,
                                        cellStates
                                    )
                                    gameState.value = newGameState.copy(
                                        isComplete = checkCompletion(newGameState.currentState, gameState.value.solution)
                                    )
                                },
                                rowHints = rowHints,
                                colHints = colHints,
                                rowHintsMax = rowHintsSize,
                                colHintsMax = colHintsSize
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Game status
        if (gameState.value.isComplete) {
            Text(
                text = "Puzzle Completed!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Green,
                modifier = Modifier.padding(4.dp)
            )
        }

        // Button row with Reset and Reset Zoom buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset button
            Button(
                onClick = {
                    // Reset all cell states
                    for (row in 0 until gameState.value.size) {
                        for (col in 0 until gameState.value.size) {
                            cellStates[row][col].value = CellState.EMPTY
                        }
                    }

                    // Reset game state
                    gameState.value = gameState.value.copy(
                        currentState = List(gameState.value.size) { List(gameState.value.size) { CellState.EMPTY } },
                        isComplete = false
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Reset Puzzle")
            }

            // Reset zoom button
            Button(
                onClick = {
                    zoomFactor = 1f
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Reset Zoom")
            }
        }

        // Instructions
        Text(
            text = "• Tap/Drag: Fill or mark cells\n" +
                    "• Use Fill/Mark button to switch painting mode\n" +
                    "• Pinch to zoom or use zoom controls\n" +
                    "• Scroll when zoomed to pan the view",
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun GridBoard(
    size: Int,
    cellStates: Array<Array<MutableState<CellState>>>,
    cellSize: androidx.compose.ui.unit.Dp,
    paintMode: PaintMode,
    onCellClick: (Int, Int) -> Unit,
    rowHints: List<List<Int>>,
    colHints: List<List<Int>>,
    rowHintsMax: Int,
    colHintsMax: Int
) {
    Column {
        var rowCount = 0
        for (row in 0 until size+rowHintsMax-1) {
            var colCountRow = 0
            var colCountCol = 0
//            Log.e("AAAAAAAAA", "$rowCount")
            Row {
                for (col in 0 until size+colHintsMax-1) {

//                    Log.e("AAAAAAAAA", "row $row\ncol $col\nrowCountROw: $colCountRow")

                    val interactionSource = remember { MutableInteractionSource() }
                    val density = LocalDensity.current


                    if (row <= rowHintsMax && col <= colHintsMax) {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(Color.Cyan)
                        )
                    } else if (col <= rowHintsMax) {
                        if (rowHints[rowCount-colHintsMax].count() < colCountRow) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(0.5.dp, Color.Gray)
                                    .background(Color.Yellow)
                            ) {
                                Text(
//                                    rowHints[colCountRow - 1 - rowCount].count()
                                    text = "${rowHints[rowCount-colHintsMax][colCountRow - 1 - rowHints[rowCount-colHintsMax].count()]}",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    fontSize = with(density) {
                                        min(
                                            14.dp.toPx(),
                                            cellSize.toPx() * 0.6f
                                        ).toSp()
                                    }
                                )
                            }
                        }
                        else {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(0.5.dp, Color.Gray)
                                    .background(Color.Green)
                            )
                        }
                        colCountRow++
                    } else if (row <= colHintsMax) {
                        if (colHints[colCountCol].count() < rowCount-rowHintsMax) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(0.5.dp, Color.Gray)
                                    .background(Color.Red)
                            ) {
                                Text(
                                    text = "${colHints[colCountCol][rowCount - 1 - colHints[colCountCol].count() - rowHintsMax]}",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    fontSize = with(density) {
                                        min(
                                            14.dp.toPx(),
                                            cellSize.toPx() * 0.6f
                                        ).toSp()
                                    }
                                )
                            }
                        }
                        else {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(0.5.dp, Color.Gray)
                                    .background(Color.Blue)
                            )
                        }
                        colCountCol++
                    }
                    // Use an interaction source that doesn't trigger ripples for better performance
                    else {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(
                                    when (cellStates[row - rowHintsMax][col - colHintsMax].value) {
                                        CellState.FILLED -> Color.Black
                                        CellState.MARKED -> Color.LightGray
                                        CellState.EMPTY -> Color.White
                                    }
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    onCellClick(row, col)
                                }
                        ) {
                            if (cellStates[row-rowHintsMax][col-colHintsMax].value == CellState.MARKED) {
                                Text(
                                    text = "X",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    fontSize = with(density) {
                                        min(
                                            14.dp.toPx(),
                                            cellSize.toPx() * 0.6f
                                        ).toSp()
                                    }
                                )
                            }
                        }
                    }
                }
                rowCount++
            }
        }
    }
}

// Helper function to update game state from cell states
fun updateGameState(
    gameState: MutableState<GameState>,
    cellStates: Array<Array<MutableState<CellState>>>
) {
    val newState = createGameStateFromCellStates(gameState.value, cellStates)
    gameState.value = newState
}

// Helper function to create a new game state from cell states
fun createGameStateFromCellStates(
    currentGameState: GameState,
    cellStates: Array<Array<MutableState<CellState>>>
): GameState {
    val size = currentGameState.size
    val newCurrentState = List(size) { row ->
        List(size) { col ->
            cellStates[row][col].value
        }
    }

    return currentGameState.copy(currentState = newCurrentState)
}

// Check if the current state matches the solution
fun checkCompletion(currentState: List<List<CellState>>, solution: List<List<Boolean>>): Boolean {
    for (row in currentState.indices) {
        for (col in currentState[row].indices) {
            val cellFilled = currentState[row][col] == CellState.FILLED
            val shouldBeFilled = solution[row][col]

            if (cellFilled != shouldBeFilled) {
                return false
            }
        }
    }
    return true
}

//@Preview(showBackground = true)
//@Composable
//fun NonogramGamePreview() {
//    MaterialTheme {
//        NonogramGame()
//    }
//