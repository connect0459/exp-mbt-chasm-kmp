package dev.connect0459.mbtchasmkmp.shared.guest

import kotlin.test.Test
import kotlin.test.assertEquals

class GuestServiceTest {
    @Test
    fun incrementReturnsItsArgumentPlusOne() {
        val guestService: GuestService = GuestServiceImpl(GUEST_WASM_BYTES)
        assertEquals(42, guestService.increment(41))
    }
}
