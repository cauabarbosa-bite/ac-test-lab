package com.example.actestlab.data

data class EspConfig(
    val showBoxes: Boolean = true,
    val showIds: Boolean = true,
    val showDistance: Boolean = true,
    val showVisibility: Boolean = true,
    val boxColorArgb: Long = 0xFF22C55E,
    val textColorArgb: Long = 0xFFE6E8EB,
    val boxAlpha: Float = 1.0f,
    val entityCount: Int = 6,
    val worldScale: Float = 100f,
    val refreshHz: Int = 30,
    val syntheticMotion: Boolean = true,
    val diagnosticsDurationMs: Long = 8_000L,
    val logLevel: LogLevel = LogLevel.INFO
)

enum class LogLevel { ERROR, WARN, INFO, DEBUG, VERBOSE }
