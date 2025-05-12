//package com.example.axelnonogram.old
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectTransformGestures
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput
//
//@Composable
//fun TransformableSample() {
//    // set up all transformation states
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//
//    var pointerCount by remember { mutableStateOf(0) }
//
//    Box(
//        Modifier
//            .fillMaxSize()
//
//            .background(Color.Blue)
//            .pointerInput(Unit) {
//                awaitPointerEventScope {
//                    while (true) {
//                        val event = awaitPointerEvent()
//                        pointerCount = event.changes.size // Number of active pointers
//                    }
//                }
//            }
//            .pointerInput(Unit) {
//                detectTransformGestures(
//                    onGesture = { _, pan, zoom, _ ->
//                        if (pointerCount>1){
//                        scale *= zoom
//                        offset += pan
//                            }
//                    }
//                )
//            }
//    ) {
//        Box(
//            Modifier
//                .graphicsLayer(
//                    scaleX = scale,
//                    scaleY = scale,
//                    translationX = offset.x,
//                    translationY = offset.y
//                )
//                .background(color = Color.Red)
//                .fillMaxSize()
//        ) {
//            Button(
//                onClick = {
//                    scale = 1F
//                }
//            ) {
//                Text(text = "Reset Zoom")
//            }
//        }
//    }
//}