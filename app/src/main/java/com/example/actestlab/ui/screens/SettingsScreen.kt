package com.example.actestlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.actestlab.data.EspConfig
import com.example.actestlab.data.LogLevel
import com.example.actestlab.ui.Screen
import com.example.actestlab.ui.UiState

@Composable
fun SettingsScreen(state: UiState, onUpdate: ((EspConfig) -> EspConfig) -> Unit, onNav: (Screen) -> Unit) {
    val cfg = state.config
    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Configuracoes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { onNav(Screen.HOME) }) { Text("Voltar") }
        }

        SwitchRow("Mostrar caixas", cfg.showBoxes) { v -> onUpdate { it.copy(showBoxes = v) } }
        SwitchRow("Mostrar IDs", cfg.showIds) { v -> onUpdate { it.copy(showIds = v) } }
        SwitchRow("Mostrar distancia", cfg.showDistance) { v -> onUpdate { it.copy(showDistance = v) } }
        SwitchRow("Mostrar visibilidade", cfg.showVisibility) { v -> onUpdate { it.copy(showVisibility = v) } }
        SwitchRow("Movimento sintetico", cfg.syntheticMotion) { v -> onUpdate { it.copy(syntheticMotion = v) } }

        SliderRow("Entidades", cfg.entityCount.toFloat(), 0f..32f) { v ->
            onUpdate { it.copy(entityCount = v.toInt()) }
        }
        SliderRow("Escala", cfg.worldScale, 50f..200f) { v -> onUpdate { it.copy(worldScale = v) } }
        SliderRow("Refresh (Hz)", cfg.refreshHz.toFloat(), 5f..60f) { v ->
            onUpdate { it.copy(refreshHz = v.toInt()) }
        }
        SliderRow("Duracao dos testes (s)", cfg.diagnosticsDurationMs / 1000f, 3f..60f) { v ->
            onUpdate { it.copy(diagnosticsDurationMs = (v * 1000).toLong()) }
        }

        Text("Nivel de log", style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            LogLevel.values().forEach { lv ->
                FilterChip(
                    selected = cfg.logLevel == lv,
                    onClick = { onUpdate { it.copy(logLevel = lv) } },
                    label = { Text(lv.name) }
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Column {
        Row {
            Text(label)
            Spacer(Modifier.weight(1f))
            Text("%.1f".format(value), style = MaterialTheme.typography.labelSmall)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range)
    }
}
