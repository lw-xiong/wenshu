package com.ciel.hsk

import kotlin.math.roundToInt

object WritingEvaluator {
    const val PASS_SCORE = 70

    fun traceScore(
        expected: BooleanArray,
        actual: BooleanArray,
        width: Int,
        height: Int,
        tolerance: Int = 2
    ): Int {
        require(width > 0 && height > 0)
        require(expected.size == width * height && actual.size == expected.size)
        val targetCount = expected.count { it }
        val inkCount = actual.count { it }
        if (targetCount == 0 || inkCount == 0) return 0

        val expandedInk = dilate(actual, width, height, tolerance)
        val expandedTarget = dilate(expected, width, height, tolerance)
        val covered = expected.indices.count { expected[it] && expandedInk[it] }
        val outside = actual.indices.count { actual[it] && !expandedTarget[it] }

        val recall = covered.toDouble() / targetCount
        val precision = (inkCount - outside).toDouble() / inkCount
        if (recall + precision == 0.0) return 0
        val f1 = 2 * precision * recall / (precision + recall)
        return (f1 * 100).roundToInt().coerceIn(0, 100)
    }

    private fun dilate(input: BooleanArray, width: Int, height: Int, radius: Int): BooleanArray {
        val output = BooleanArray(input.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (!input[y * width + x]) continue
                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {
                        val nx = x + dx
                        val ny = y + dy
                        if (nx in 0 until width && ny in 0 until height) {
                            output[ny * width + nx] = true
                        }
                    }
                }
            }
        }
        return output
    }
}
