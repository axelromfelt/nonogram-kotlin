package com.example.axelnonogram.game

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator


val conversionTable = mapOf(
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

fun decompress(compressedNonogram: String): List<String> {

        val list = compressedNonogram.split("x")
        if (list.count()<3){
            return listOf("false")
        }
        val x = list[0]
        val y = list[1]
        val grid = list[2]
        val result = mutableListOf<String>()


        for (letter in grid) {
            for ((key, value) in conversionTable) {
                if (value == letter) {
                    result.add(key)
                    break
                }
            }
        }
        if (x.toInt() * y.toInt() < result.count()) {
            return listOf("false")
        }

        return listOf(x, y, result.joinToString(""))

}

fun loadNonogramInfo(compressedNonogram: String): NonogramInfo{
    val decompressedNonogram = decompress(compressedNonogram)
    val x = decompressedNonogram[0].toInt()
    val y = decompressedNonogram[1].toInt()
    val solutionString = decompressedNonogram[2]

    val solution = MutableList(y) { MutableList(x) { false } }
    var index = 0
    for (row in 0 until y) {
        for (col in 0 until x) {
            solution[row][col] = solutionString[row*x+col].code == 49
            index++
        }
    }


    val rowHints = mutableListOf<List<Int>>()

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
        rowHints.add(rowHint)
    }


    val colHints = mutableListOf<List<Int>>()

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

        colHints.add(colHint)
    }


    var rowHintsSize = 0
    for (rowHint in rowHints) {
        if (rowHint.count() > rowHintsSize) {
            rowHintsSize = rowHint.count()
        }
    }


    var colHintsSize = 0
    for (colHint in colHints) {
        if (colHint.count() > colHintsSize) {
            colHintsSize = colHint.count()
        }
    }


    val nonogramInfo = NonogramInfo(
        solution = solution,
        rowHints = rowHints,
        colHints = colHints,
        width = x+rowHintsSize,
        height = y+colHintsSize,
        rowHintsSize = rowHintsSize,
        colHintsSize = colHintsSize,
        isComplete = false
    )

    return nonogramInfo
}