package dev.connect0459.mbtchasmkmp.shared.guest

import kotlin.test.Test

// Real-scale (100,000 iterations) run directly on the Kotlin/Native
// iosSimulatorArm64 test binary — the same runtime iosApp/ embeds, without
// the Swift/SwiftUI layer in between. Printed rather than asserted; see
// GuestBenchmarkJvmTest for the JVM-side counterpart.
class GuestBenchmarkIosSimulatorArm64Test {
    @Test
    fun printIncrementAndMovePointBenchmarksAtRealScale() {
        val iterations = 100_000
        println(formatGuestBenchmarkResult(benchmarkIncrement(iterations)))
        println(formatGuestBenchmarkResult(benchmarkMovePoint(iterations)))
    }
}
