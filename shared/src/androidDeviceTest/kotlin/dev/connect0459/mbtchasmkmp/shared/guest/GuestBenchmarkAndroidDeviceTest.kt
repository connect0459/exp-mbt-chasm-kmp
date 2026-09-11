package dev.connect0459.mbtchasmkmp.shared.guest

import kotlin.test.Test

// Real-scale (100,000 iterations) run on an actual Android emulator/device
// (ART), the real deliverable for this milestone. Printed rather than
// asserted; see GuestBenchmarkJvmTest / GuestBenchmarkIosSimulatorArm64Test
// for the other targets' counterparts.
class GuestBenchmarkAndroidDeviceTest {
    @Test
    fun printIncrementAndMovePointBenchmarksAtRealScale() {
        val iterations = 100_000
        println(formatGuestBenchmarkResult(benchmarkIncrement(iterations)))
        println(formatGuestBenchmarkResult(benchmarkMovePoint(iterations)))
    }
}
