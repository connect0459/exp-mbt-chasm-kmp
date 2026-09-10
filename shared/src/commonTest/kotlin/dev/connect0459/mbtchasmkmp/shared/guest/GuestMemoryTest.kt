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
import kotlin.test.Test
import kotlin.test.assertEquals

class GuestMemoryTest {
    // GuestServiceImpl (the Chasm-generated wrapper) exposes movePoint as a
    // plain Int, since Chasm's codegen has no notion of a MoonBit-boxed
    // tuple — it only sees a wasm function returning one i32. GuestServiceImpl
    // doesn't expose the store/instance that pointer is only valid within,
    // so decoding it requires Chasm's low-level embedding API directly.
    @Test
    fun movePointReturnsAPointerToAHeapAllocatedTupleInLinearMemory() {
        val module = module(GUEST_WASM_BYTES).expect("Failed to decode guest.wasm")
        val store = store()
        val instance = instance(store, module, emptyList()).expect("Failed to instantiate guest.wasm")

        val result =
            invoke(
                store,
                instance,
                "move_point",
                listOf(NumberValue.I32(10), NumberValue.I32(20), NumberValue.I32(3), NumberValue.I32(-5)),
            ).expect("Failed to invoke move_point")

        val pointer = (result.single() as NumberValue.I32).value
        val memory = exports(instance).first { it.name == "memory" }.value as Memory

        val x = readInt(store, memory, pointer).expect("Failed to read x")
        val y = readInt(store, memory, pointer + 4).expect("Failed to read y")

        assertEquals(13, x)
        assertEquals(15, y)
    }
}
