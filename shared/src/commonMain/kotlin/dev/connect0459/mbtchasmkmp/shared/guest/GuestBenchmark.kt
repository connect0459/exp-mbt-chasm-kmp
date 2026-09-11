package dev.connect0459.mbtchasmkmp.shared.guest

import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.math.round
import kotlin.time.TimeSource

data class GuestBenchmarkResult(
    val label: String,
    val iterations: Int,
    val setupSeconds: Double,
    val totalCallSeconds: Double,
) {
    val perCallMicroseconds: Double
        get() = totalCallSeconds / iterations * 1_000_000

    // How many calls fit inside a single 60fps frame budget (16.67ms), given
    // only this call's own cost — ignores everything else a real frame does.
    val maxCallsPerSixtyFpsFrame: Double
        get() = (1.0 / 60.0) / (totalCallSeconds / iterations)
}

// Repeatedly calls the Chasm-generated increment() binding to measure
// Chasm's per-call interpreter overhead for the cheapest possible call shape
// (bare Int in/out, no allocation).
fun benchmarkIncrement(iterations: Int): GuestBenchmarkResult {
    val setupStart = TimeSource.Monotonic.markNow()
    val guestService = createGuestService()
    val setupSeconds = setupStart.elapsedNow().inWholeNanoseconds / 1_000_000_000.0

    var value = 0
    val callStart = TimeSource.Monotonic.markNow()
    repeat(iterations) {
        value = guestService.increment(value)
    }
    val totalCallSeconds = callStart.elapsedNow().inWholeNanoseconds / 1_000_000_000.0

    return GuestBenchmarkResult("increment(Int) -> Int", iterations, setupSeconds, totalCallSeconds)
}

// Repeatedly calls move_point(x, y, dx, dy) -> (x', y'), which additionally
// exercises: guest-side heap allocation of the returned tuple, and a
// linear-memory read from the host side for every call. Can't use the
// generated GuestServiceImpl here (see GuestMemoryTest) — it hides the
// store/instance a returned pointer is only valid within — so this uses
// Chasm's low-level embedding API directly, same as that test.
fun benchmarkMovePoint(iterations: Int): GuestBenchmarkResult {
    val setupStart = TimeSource.Monotonic.markNow()
    val module = module(GUEST_WASM_BYTES).expect("Failed to decode guest.wasm")
    val store = store()
    val instance = instance(store, module, emptyList()).expect("Failed to instantiate guest.wasm")
    val memory = exports(instance).first { it.name == "memory" }.value as Memory
    val setupSeconds = setupStart.elapsedNow().inWholeNanoseconds / 1_000_000_000.0

    var x = 0
    var y = 0
    val callStart = TimeSource.Monotonic.markNow()
    repeat(iterations) {
        val result =
            invoke(
                store,
                instance,
                "move_point",
                listOf(NumberValue.I32(x), NumberValue.I32(y), NumberValue.I32(1), NumberValue.I32(1)),
            ).expect("Failed to invoke move_point")
        val pointer = (result.single() as NumberValue.I32).value
        x = readInt(store, memory, pointer).expect("Failed to read x")
        y = readInt(store, memory, pointer + 4).expect("Failed to read y")
    }
    val totalCallSeconds = callStart.elapsedNow().inWholeNanoseconds / 1_000_000_000.0

    return GuestBenchmarkResult(
        "move_point(Int x4) -> boxed tuple + memory read",
        iterations,
        setupSeconds,
        totalCallSeconds,
    )
}

// Round to 3 decimal places without java.lang.String.format, which Kotlin/
// Native doesn't have — this is diagnostic output, not a precision-critical
// calculation, so the occasional floating-point-rounding artifact is fine.
private fun round3(value: Double): Double = round(value * 1000) / 1000

fun formatGuestBenchmarkResult(result: GuestBenchmarkResult): String =
    """
    ${result.label} (${result.iterations} calls)
    setup: ${round3(result.setupSeconds * 1000)} ms
    total: ${round3(result.totalCallSeconds * 1000)} ms
    per call: ${round3(result.perCallMicroseconds)} us
    max calls / 60fps frame: ${round(result.maxCallsPerSixtyFpsFrame)}
    """.trimIndent()
