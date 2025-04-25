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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.outlined.DisabledByDefault
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Square
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import kotlin.math.min
import kotlin.math.max
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import java.nio.file.WatchEvent

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
    Log.e("IJFISOEFIOSEFIOSFJSIEO","$grid,$result")

    return listOf(x, y, result.joinToString(""))
}
//101
//101
//101
//111

//101
//011
//111
//101
@Composable
fun NonogramMain(puzzleCompressed: String) {
    val puzzleDecompressed = decompress(puzzleCompressed)
    val solution = Array(puzzleDecompressed[1].toInt()) { Array(puzzleDecompressed[0].toInt()) { true } }
    var index = 0
    for (y in 0 until puzzleDecompressed[1].toInt()) {

        for (x in 0 until puzzleDecompressed[0].toInt()) {
            solution[y][x] = puzzleDecompressed[2][y*puzzleDecompressed[0].toInt()+x].code == 49
            index++
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


//    for (row in colHints){
//        for (col in row){
//            Log.e("ABC123","col $col")
//        }
//    }

//    for (row in rowHints){
//        for (col in row){
//            Log.e("ABC123","row $col")
//        }
//    }

    var rowMaxHints = 0
    for (rowHint in rowHints) {
        if (rowHint.count() > rowMaxHints) {
            rowMaxHints = rowHint.count()
        }
    }

    var colMaxHints = 0
    for (colHint in colHints) {
        if (colHint.count() > colMaxHints) {
            colMaxHints = colHint.count()
        }
    }

    val gameInfo = remember {
        mutableStateOf(
            GameInfo(
                height = puzzleDecompressed[1].toInt(),
                width = puzzleDecompressed[0].toInt(),
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

    var paintMode by remember { mutableStateOf(PaintMode.FILL) }

    var zoomFactor by remember { mutableStateOf(1f) }
    val minZoom = 0.5f
    val maxZoom = 3.0f

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    val lastDragCellState = remember { mutableStateOf<CellState?>(null) }

    val modifiedDuringDrag = remember { mutableStateOf(mutableSetOf<Pair<Int, Int>>()) }

    val cellStates = remember {
        Array(gameInfo.value.height) { row ->
            Array(gameInfo.value.width) { cell ->
                mutableStateOf(CellState.EMPTY)
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var pointerCount by remember { mutableStateOf(0) }

    val gridAreaWidth = screenWidth - 16.dp

    val baseCellSize = if (gridAreaWidth/(gameInfo.value.height+gameInfo.value.colMaxHints) < gridAreaWidth/(gameInfo.value.width+gameInfo.value.rowMaxHints)) {
        gridAreaWidth/(gameInfo.value.height+gameInfo.value.colMaxHints)
    } else {
        gridAreaWidth/(gameInfo.value.width+gameInfo.value.rowMaxHints)
    }//.coerceAtMost(40.dp)

    val cellSize = baseCellSize * zoomFactor

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
//                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nonogram Puzzle",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    pointerCount = event.changes.size
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures(
                                onGesture = { _, pan, zoom, _ ->
                                    if (pointerCount > 1) {
                                        scale = (scale * zoom).coerceAtLeast(minZoom)
                                            .coerceAtMost(maxZoom)
                                        offset += pan
                                    }
                                }
                            )
                        }
                ) {
                    Box(
                        Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
//                            .background(color = Color.Black)
                            .fillMaxSize()
//                            .pointerInput(Unit) {
//                                detectTransformGestures { _, pan, zoom, _ ->
//                                    // Update zoom factor with pinch gesture
//                                    zoomFactor = (zoomFactor * zoom).coerceIn(minZoom, maxZoom)
//                                }
//                            }
//                            .pointerInput(paintMode) {
//                                detectDragGestures(
//                                    onDragStart = { dragStartPosition ->
//                                        Log.e("AABBVV","${dragStartPosition.y/(size.height/gameInfo.value.height)}")
////                                        // Convert position to grid coordinates considering zoom
////                                        val row =
////                                            (dragStartPosition.y / (size.height / gameState.value.size)).toInt()
////                                                .coerceIn(0, gameState.value.size - 1)
////                                        val col =
////                                            (dragStartPosition.x / (size.width / gameState.value.size)).toInt()
////                                                .coerceIn(0, gameState.value.size - 1)
////
////                                        // Clear the modified cells set at the start of a new drag
////                                        modifiedDuringDrag.value.clear()
////
////                                        // Determine the state to apply based on the paint mode and current cell state
////                                        val currentCellState = cellStates[row][col].value
////
//////                                            lastDragCellState.value = when (paintMode) {
//////                                                PaintMode.FILL -> {
//////                                                    if (currentCellState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
//////                                                }
//////
//////                                                PaintMode.MARK -> {
//////                                                    if (currentCellState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
//////                                                }
//////                                            }
////
////                                        // Update the cell and mark it as modified
////                                        cellStates[row][col].value = lastDragCellState.value!!
////                                        modifiedDuringDrag.value.add(Pair(row, col))
////
////                                        // Update the game state
////                                        updateGameState(gameState, cellStates)
//                                    },
//                                    onDrag = { change, _ ->
////                                        // Consume the change to prevent scrolling
////                                        change.consume()
////
////                                        // Convert position to grid coordinates considering zoom
////                                        val row =
////                                            (change.position.y / (size.height / gameState.value.size)).toInt()
////                                                .coerceIn(0, gameState.value.size - 1)
////                                        val col =
////                                            (change.position.x / (size.width / gameState.value.size)).toInt()
////                                                .coerceIn(0, gameState.value.size - 1)
////
////                                        // Check if this cell has already been modified in this drag operation
////                                        val cellCoord = Pair(row, col)
////                                        if (!modifiedDuringDrag.value.contains(cellCoord)) {
////                                            // Update the cell and mark it as modified
////                                            cellStates[row][col].value =
////                                                lastDragCellState.value!!
////                                            modifiedDuringDrag.value.add(cellCoord)
////
////                                            // Update the game state
////                                            updateGameState(gameState, cellStates)
////                                        }
//                                    },
////                                    onDragEnd = {
////                                        // Update completion status
////                                        val newGameState = createGameStateFromCellStates(
////                                            gameState.value,
////                                            cellStates
////                                        )
////                                        gameState.value = newGameState.copy(
////                                            isComplete = checkCompletion(
////                                                newGameState.currentState,
////                                                gameState.value.solution
////                                            )
////                                        )
////                                    }
//                                )
//                            }
                    ) {
                        GameGrid(
                            height = gameInfo.value.height,
                            width = gameInfo.value.width,
                            cellStates = cellStates,
                            cellSize = cellSize,
                            rowHints = gameInfo.value.rowHints,
                            colHints = gameInfo.value.colHints,
                            rowHintsSize = gameInfo.value.rowMaxHints,
                            colHintsSize = gameInfo.value.colMaxHints,
                            onCellCLicked = { row, col ->
                                // Apply the appropriate state based on paint mode and current state
                                val currentState = cellStates[row][col].value

                                cellStates[row][col].value = when (paintMode) {
                                    PaintMode.FILL -> {
                                        if (currentState == CellState.FILLED) CellState.EMPTY else CellState.FILLED
                                    }
                                    PaintMode.MARK -> {
                                        if (currentState == CellState.MARKED) CellState.EMPTY else CellState.MARKED
                                    }
                                    PaintMode.CLEAR -> {
                                        CellState.EMPTY
                                    }
                                    else -> {
                                        currentState
                                    }

                                }

                                // Update the game state
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // This pins it to the bottom
                .fillMaxWidth()
                .background(Color.Gray),
            horizontalArrangement = Arrangement.SpaceBetween, // This will space out the buttons
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Docking Zoom Controls to the Left
            ZoomControls(
                zoomFactor = scale,
                onZoomChanged = { newZoom -> scale = newZoom },
                onResetZoom = { scale = 1f; offset = Offset.Zero }
            )

            // Docking Paint Mode Controls to the Right
            PaintModeSelector(
                currentPaintMode = paintMode,
                onPaintModeChange = { newMode -> paintMode = newMode }
            )
        }
    }
}

@Composable
fun GameGrid(
    height: Int,
    width: Int,
    cellStates: Array<Array<MutableState<CellState>>>,
    cellSize: Dp,
    rowHints: List<List<Int>>,
    colHints: List<List<Int>>,
    rowHintsSize: Int,
    colHintsSize: Int,
    onCellCLicked: (Int, Int) -> Unit
){
//    Log.e("ABC123","size: $colHintsSize , $rowHintsSize")
    Column {
        for (row in 0 until height + colHintsSize) {
            Row {
                for (col in 0 until width + rowHintsSize) {

                    val interactionSource = remember { MutableInteractionSource() }
                    val density = LocalDensity.current
//            Log.e("ABC123","$row , $col")

                    if (col < rowHintsSize && row < colHintsSize) {

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(Color.Cyan)
                        )
                    } else if (col < rowHintsSize) {

                        var currentRowHints = rowHints[row - colHintsSize]
                        var currentHintIndex = col - (rowHintsSize - currentRowHints.count())

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(Color.Yellow)
                        ) {
                            if (currentHintIndex >= 0) {
                                if (currentRowHints[currentHintIndex] != 0) {
                                    Text(
                                        text = "${currentRowHints[currentHintIndex]}",
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                            .wrapContentHeight(),
                                        fontSize = with(density) {
//                                        min(
                                            (
//                                            14.dp.toPx(),
                                                    cellSize.toPx() * 0.4f
                                                    ).toSp()
                                        }
                                    )
                                }
                            }
                        }
                    } else if (row < colHintsSize) {
                        Log.e("$col","$col")
                        var currentColHints = colHints[col - rowHintsSize]
                        var currentHintIndex = row - (colHintsSize - currentColHints.count())

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(Color.Yellow),

                        ) {
                            if (currentHintIndex >= 0) {
                                if (currentColHints[currentHintIndex] != 0) {
                                    Text(
                                        text = "${currentColHints[currentHintIndex]}",
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                            .wrapContentHeight(),
                                        fontSize = with(density) {
//                                        min(
                                            (
//                                            14.dp.toPx(),
                                                    cellSize.toPx() * 0.4f
                                                    ).toSp()
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(
                                    when (cellStates[row - colHintsSize][col - rowHintsSize].value) {
                                        CellState.FILLED -> Color.Black
                                        CellState.MARKED -> Color.White
                                        CellState.EMPTY -> Color.White
                                    }
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    onCellCLicked(row - colHintsSize, col - rowHintsSize)
                                }
                        ) {
                            if (cellStates[row - colHintsSize][col - rowHintsSize].value == CellState.MARKED) {
                                Text(
                                    text = "X",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentHeight(),
                                    fontSize = with(density) {
//                                        min(
                                        (
//                                            14.dp.toPx(),
                                            cellSize.toPx() * 0.8f
                                        ).toSp()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PaintModeSelector(
    currentPaintMode: PaintMode,
    onPaintModeChange: (PaintMode) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            shape = RectangleShape,
            modifier = Modifier
                .size(35.dp),
            onClick = { onPaintModeChange(PaintMode.FILL) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentPaintMode == PaintMode.FILL)
                    Color.White
                else Color.Gray
            ),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Square,
                    contentDescription = "Fill Mode",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
//                Text(text = "Fill", fontSize = 12.sp)
            }
        }

        Button(
            shape = RectangleShape,
            modifier = Modifier
                .size(35.dp),
            onClick = { onPaintModeChange(PaintMode.MARK) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentPaintMode == PaintMode.MARK)
                    Color.White
                else Color.Gray
            ),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DisabledByDefault,
                    contentDescription = "Mark Mode",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
//                Text(text = "Mark", fontSize = 12.sp)
            }
        }

        Button(
            shape = RectangleShape,
            modifier = Modifier
                .size(35.dp),
            onClick = { onPaintModeChange(PaintMode.CLEAR) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentPaintMode == PaintMode.CLEAR)
                    Color.White
                else Color.Gray
            ),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckBoxOutlineBlank,
                    contentDescription = "Clear Mode",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
//                Text(text = "Clear", fontSize = 12.sp)
            }
        }

        Button(
            shape = RectangleShape,
            modifier = Modifier
                .size(35.dp),
            onClick = { onPaintModeChange(PaintMode.MOVE) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentPaintMode == PaintMode.MOVE)
                    Color.White
                else Color.Gray
            ),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.OpenWith,
                    contentDescription = "Move Mode",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
//                Text(text = "Move", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ZoomControls(
    zoomFactor: Float,
    onZoomChanged: (Float) -> Unit,
    onResetZoom: () -> Unit
) {
    val minZoom = 0.5f
    val maxZoom = 3.0f

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                onZoomChanged((zoomFactor - 0.25f).coerceAtLeast(minZoom))
            },
            modifier = Modifier.size(35.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Zoom Out",
                modifier = Modifier.size(20.dp)
            )
        }

        TextButton(
            shape = RectangleShape,
            onClick = onResetZoom,
            modifier = Modifier
                .height(35.dp)
                .width(75.dp)
        ) {
            Text(
                text = "${(zoomFactor * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        IconButton(
            onClick = {
                onZoomChanged((zoomFactor + 0.25f).coerceAtMost(maxZoom))
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Zoom In",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}