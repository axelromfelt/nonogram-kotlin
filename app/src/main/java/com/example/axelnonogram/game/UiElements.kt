package com.example.axelnonogram.game

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.DisabledByDefault
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp

@Composable
fun UndoRedoButtons(
    onUndo: () -> Unit,
    onRedo: () -> Unit
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = onUndo ,

        ) {
            Icon(
                imageVector = Icons.Filled.Undo,
                contentDescription = "Undo",
            )
        }

        IconButton(
            onClick =  onRedo ,
        ) {
            Icon(
                imageVector = Icons.Filled.Redo,
                contentDescription = "Redo",
            )
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onZoomChanged((zoomFactor - 0.25f).coerceAtLeast(minZoom)) }
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Zoom Out"
            )
        }

        TextButton(
            onClick = onResetZoom,
        ) {
            Text(
                text = "${(zoomFactor * 100).toInt()}%",
                fontSize = 14.sp
            )
        }

        IconButton(
            onClick = { onZoomChanged((zoomFactor + 0.25f).coerceAtMost(maxZoom)) }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Zoom In"
            )
        }
    }
}

@Composable
fun PaintModeSelector(
    currentPaintMode: PaintMode,
    onPaintModeChange: (PaintMode) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onPaintModeChange(PaintMode.FILL) },
            modifier = Modifier.semantics {
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Square,
                contentDescription = null,
                tint = if (currentPaintMode == PaintMode.FILL)
                    MaterialTheme.colorScheme.primary
                else Color.Gray
            )
        }

        IconButton(
            onClick = { onPaintModeChange(PaintMode.MARK) },
            modifier = Modifier.semantics {
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.DisabledByDefault,
                contentDescription = null,
                tint = if (currentPaintMode == PaintMode.MARK)
                    MaterialTheme.colorScheme.primary
                else Color.Gray
            )
        }

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

        IconButton(
            onClick = { onPaintModeChange(PaintMode.MOVE) },
            modifier = Modifier.semantics {
            }
        ) {
            Icon(
                imageVector = Icons.Filled.OpenWith,
                contentDescription = null,
                tint = if (currentPaintMode == PaintMode.MOVE)
                    MaterialTheme.colorScheme.primary
                else Color.Gray
            )
        }
    }
}

