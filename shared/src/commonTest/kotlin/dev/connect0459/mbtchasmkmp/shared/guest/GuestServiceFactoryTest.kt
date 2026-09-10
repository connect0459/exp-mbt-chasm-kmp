package dev.connect0459.mbtchasmkmp.shared.guest

import kotlin.test.Test
import kotlin.test.assertEquals

class GuestServiceFactoryTest {
    @Test
    fun createGuestServiceReturnsAWorkingGuestService() {
        val guestService: GuestService = createGuestService()
        assertEquals(42, guestService.increment(41))
    }
}
