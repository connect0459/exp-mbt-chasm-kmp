package dev.connect0459.mbtchasmkmp.shared

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTest {
    @Test
    fun platformNameIdentifiesTheRunningTarget() {
        assertTrue(platformName().isNotBlank())
    }
}
