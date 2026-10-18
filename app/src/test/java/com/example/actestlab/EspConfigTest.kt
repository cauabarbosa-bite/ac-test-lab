package com.example.actestlab

import com.example.actestlab.data.EspConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class EspConfigTest {
    @Test fun defaults_stable() {
        val c = EspConfig()
        assertEquals(6, c.entityCount)
        assertEquals(30, c.refreshHz)
        assertEquals(0xFF22C55E, c.boxColorArgb)
    }
}
