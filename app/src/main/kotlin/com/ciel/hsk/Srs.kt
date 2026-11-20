package com.ciel.hsk

/**
 * Spaced-repetition (Leitner) scheduling. Pure logic with no Android deps, so
 * it is unit-tested on the JVM alongside PracticeData / WritingEvaluator.
 * Storage (JSON in SharedPreferences) lives in MainActivity; only the math is here.
 */
object Srs {
    // Review interval per box, in minutes: 10m, 1h, 1d, 3d, 7d, 30d.
    // Box 1 = first review, up to box 6 = most mature. Intervals strictly increase.
    val INTERVALS_MIN: LongArray = longArrayOf(10, 60, 1440, 4320, 10080, 43200)

    /** Box after a successful review; starts at 1 and caps at the top interval. */
    fun nextBox(currentBox: Int): Int = (currentBox + 1).coerceIn(1, INTERVALS_MIN.size)

    /** Absolute due timestamp (ms epoch) for a word currently in [box]. */
    fun dueMillis(nowMs: Long, box: Int): Long {
        val idx = (box - 1).coerceIn(0, INTERVALS_MIN.size - 1)
        return nowMs + INTERVALS_MIN[idx] * 60_000L
    }

    /** A word is due once its scheduled time has passed. */
    fun isDue(dueMs: Long, nowMs: Long): Boolean = dueMs <= nowMs
}
