package dev.connect0459.mbtchasmkmp.shared.guest

import kotlin.test.Test

// Real-scale (100,000 iterations) run to get an actual JVM-target number,
// comparable to mbt-wasmkit-ios's WasmKit measurement methodology and to
// this same benchmark's iosSimulatorArm64 number recorded in docs/todo.md.
// Printed rather than asserted — this is a measurement, not a pass/fail
// check (GuestBenchmarkTest already covers the latter).
class GuestBenchmarkJvmTest {
    @Test
    fun printIncrementAndMovePointBenchmarksAtRealScale() {
        val iterations = 100_000
        println(formatGuestBenchmarkResult(benchmarkIncrement(iterations)))
        println(formatGuestBenchmarkResult(benchmarkMovePoint(iterations)))
    }
}
