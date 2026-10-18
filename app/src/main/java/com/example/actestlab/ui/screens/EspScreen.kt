package com.example.actestlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.actestlab.BuildConfig
import com.example.actestlab.esp.EspRenderer
import com.example.actestlab.ui.Screen
import com.example.actestlab.ui.UiState

@Composable
fun EspScreen(state: UiState, onNav: (Screen) -> Unit) {
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("ESP / Debug", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { onNav(Screen.HOME) }) { Text("Voltar") }
        }
        if (!BuildConfig.DEBUG_ESP_ENABLED) {
            Text("ESP desativado nesta build (producao).")
            return@Column
        }
        EspRenderer(
            entities = state.entities,
            config = state.config,
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
        Text(
            "Entidades: " + state.entities.size + " - Hz alvo: " + state.config.refreshHz,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
