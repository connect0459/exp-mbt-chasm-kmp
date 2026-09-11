package dev.connect0459.mbtchasmkmp.shared.guest

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GuestBenchmarkTest {
    @Test
    fun benchmarkIncrementReportsATimingForEveryIteration() {
        val result = benchmarkIncrement(iterations = 1_000)

        assertEquals(1_000, result.iterations)
        assertTrue(result.totalCallSeconds >= 0.0)
        assertTrue(result.setupSeconds >= 0.0)
    }

    @Test
    fun benchmarkMovePointReportsATimingForEveryIteration() {
        val result = benchmarkMovePoint(iterations = 1_000)

        assertEquals(1_000, result.iterations)
        assertTrue(result.totalCallSeconds >= 0.0)
        assertTrue(result.setupSeconds >= 0.0)
    }
}
