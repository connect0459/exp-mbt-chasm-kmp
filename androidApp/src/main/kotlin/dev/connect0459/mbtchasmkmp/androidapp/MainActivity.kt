package dev.connect0459.mbtchasmkmp.androidapp

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import dev.connect0459.mbtchasmkmp.shared.guest.benchmarkIncrement
import dev.connect0459.mbtchasmkmp.shared.guest.benchmarkMovePoint
import dev.connect0459.mbtchasmkmp.shared.guest.createGuestService
import dev.connect0459.mbtchasmkmp.shared.guest.formatGuestBenchmarkResult

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val guestService = createGuestService()
        val value = guestService.increment(41)

        val iterations = 100_000
        val incrementBench = benchmarkIncrement(iterations)
        val movePointBench = benchmarkMovePoint(iterations)

        val resultText =
            """
            increment(41) = $value

            ${formatGuestBenchmarkResult(incrementBench)}

            ${formatGuestBenchmarkResult(movePointBench)}
            """.trimIndent()

        setContentView(
            TextView(this).apply {
                text = resultText
                setPadding(32, 32, 32, 32)
                typeface = Typeface.MONOSPACE
            },
        )
    }
}
