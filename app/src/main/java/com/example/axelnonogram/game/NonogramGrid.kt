package com.example.axelnonogram.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NonogramGrid(
    height: Int,
    width: Int,
    cellStates: Array<Array<MutableState<CellState>>>,
    cellSize: Dp,
    rowHints: List<List<Int>>,
    colHints: List<List<Int>>,
    rowHintsSize: Int,
    colHintsSize: Int,
    onCellClicked: (Int, Int) -> Unit,
    paintMode: PaintMode,
    isComplete: Boolean
) {

    Column {
        for (row in 0 until height) {
            Row {
                for (col in 0 until width) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val density = LocalDensity.current

                    if (col < rowHintsSize && row < colHintsSize) {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.White)
                                .background(Color.White)
                        )
                    }

                    else if (col < rowHintsSize ) {
                        val currentRowHints = rowHints[row - colHintsSize]
                        val currentHintIndex = col - (rowHintsSize - currentRowHints.size)

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentHintIndex >= 0 && currentRowHints[currentHintIndex] != 0) {
                                Text(
                                    text = "${currentRowHints[currentHintIndex]}",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontSize = with(density) { (cellSize.toPx() * 0.4f).toSp() }
                                )
                            }
                        }
                    }

                    else if (row < colHintsSize) {
                        val currentColHints = colHints[col - rowHintsSize]
                        val currentHintIndex = row - (colHintsSize - currentColHints.size)

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentHintIndex >= 0 && currentColHints[currentHintIndex] != 0) {
                                Text(
                                    text = "${currentColHints[currentHintIndex]}",
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontSize = with(density) { (cellSize.toPx() * 0.4f).toSp() }
                                )
                            }
                        }
                    }

                    else {
                        val rowIndex = row - colHintsSize
                        val colIndex = col - rowHintsSize
                        val cellState = cellStates[rowIndex][colIndex].value

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(0.5.dp, Color.Gray)
                                .background(
                                    when (cellState) {
                                        CellState.FILLED -> Color.Black
                                        else -> Color.White
                                    }
                                )
                                .then(
                                    if (paintMode != PaintMode.MOVE && !isComplete) {
                                        Modifier
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null
                                            ) {
                                                onCellClicked(rowIndex, colIndex)
                                            }
                                            .semantics {
                                            }
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cellState == CellState.MARKED && !isComplete) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(0.8f),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
