package com.example.actestlab.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "actest_settings")

class SettingsRepository(private val context: Context) {
    private object K {
        val showBoxes = booleanPreferencesKey("show_boxes")
        val showIds = booleanPreferencesKey("show_ids")
        val showDistance = booleanPreferencesKey("show_distance")
        val showVisibility = booleanPreferencesKey("show_visibility")
        val boxColor = longPreferencesKey("box_color")
        val textColor = longPreferencesKey("text_color")
        val boxAlpha = floatPreferencesKey("box_alpha")
        val entityCount = intPreferencesKey("entity_count")
        val worldScale = floatPreferencesKey("world_scale")
        val refreshHz = intPreferencesKey("refresh_hz")
        val motion = booleanPreferencesKey("motion")
        val duration = longPreferencesKey("duration")
        val logLevel = stringPreferencesKey("log_level")
    }

    val configFlow: Flow<EspConfig> = context.dataStore.data.map { p ->
        EspConfig(
            showBoxes = p[K.showBoxes] ?: true,
            showIds = p[K.showIds] ?: true,
            showDistance = p[K.showDistance] ?: true,
            showVisibility = p[K.showVisibility] ?: true,
            boxColorArgb = p[K.boxColor] ?: 0xFF22C55E,
            textColorArgb = p[K.textColor] ?: 0xFFE6E8EB,
            boxAlpha = p[K.boxAlpha] ?: 1f,
            entityCount = p[K.entityCount] ?: 6,
            worldScale = p[K.worldScale] ?: 100f,
            refreshHz = p[K.refreshHz] ?: 30,
            syntheticMotion = p[K.motion] ?: true,
            diagnosticsDurationMs = p[K.duration] ?: 8_000L,
            logLevel = p[K.logLevel]?.let { runCatching { LogLevel.valueOf(it) }.getOrNull() } ?: LogLevel.INFO
        )
    }

    suspend fun save(cfg: EspConfig) {
        context.dataStore.edit { p ->
            p[K.showBoxes] = cfg.showBoxes
            p[K.showIds] = cfg.showIds
            p[K.showDistance] = cfg.showDistance
            p[K.showVisibility] = cfg.showVisibility
            p[K.boxColor] = cfg.boxColorArgb
            p[K.textColor] = cfg.textColorArgb
            p[K.boxAlpha] = cfg.boxAlpha
            p[K.entityCount] = cfg.entityCount
            p[K.worldScale] = cfg.worldScale
            p[K.refreshHz] = cfg.refreshHz
            p[K.motion] = cfg.syntheticMotion
            p[K.duration] = cfg.diagnosticsDurationMs
            p[K.logLevel] = cfg.logLevel.name
        }
    }
}
