package com.example.axelnonogram.game

data class NonogramInfo(
val solution: List<List<Boolean>>,
val rowHints: List<List<Int>>,
val colHints: List<List<Int>>,
val width: Int,
val height: Int,
val rowHintsSize: Int,
val colHintsSize: Int,
val isComplete: Boolean = false
)