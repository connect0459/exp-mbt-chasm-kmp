package dev.connect0459.mbtchasmkmp.shared.guest

fun createGuestService(): GuestService = GuestServiceImpl(GUEST_WASM_BYTES)
