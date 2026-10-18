package com.example.actestlab.esp

import com.example.actestlab.domain.Entity
import com.example.actestlab.domain.EntityType
import kotlin.math.sin

class SyntheticEntitySource {
    fun generate(count: Int, motion: Boolean, t: Float): List<Entity> {
        val n = count.coerceIn(0, 64)
        val out = ArrayList<Entity>(n)
        for (i in 0 until n) {
            val seed = i * 37.31f
            val phase = if (motion) t else 0f
            val x = 40f + ((i * 73) % 280).toFloat() + 20f * sin(phase + seed)
            val y = 40f + ((i * 41) % 160).toFloat() + 15f * sin(phase * 0.8f + seed * 2f)
            val w = 22f + (i % 3) * 6f
            val h = 40f + (i % 4) * 8f
            val dist = 5f + (i * 3.7f) % 40f
            val type = when (i % 4) {
                0 -> EntityType.PLAYER
                1 -> EntityType.NPC
                2 -> EntityType.OBJECT
                else -> EntityType.UNKNOWN
            }
            val visible = ((i * 17) % 7) != 0
            out.add(Entity("E" + i.toString().padStart(2, '0'), type, x, y, w, h, dist, visible))
        }
        return out
    }
}
