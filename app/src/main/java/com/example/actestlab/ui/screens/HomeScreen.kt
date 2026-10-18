package com.example.actestlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.actestlab.ui.Screen
import com.example.actestlab.ui.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(state: UiState, onStart: () -> Unit, onStop: () -> Unit, onNav: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("ACTestLab", style = MaterialTheme.typography.titleLarge)
        Text("Diagnostico local + ESP sintetico", style = MaterialTheme.typography.bodyMedium)

        StatusCard("Estado dos testes", if (state.status.running) "Em execucao" else "Parado")
        StatusCard(
            "Ultima execucao",
            state.status.lastFinishedAt?.let {
                SimpleDateFormat("dd/MM HH:mm:ss", Locale.US).format(Date(it))
            } ?: "-"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard("Total", state.total, Modifier.weight(1f))
            KpiCard("Aprovados", state.passed, Modifier.weight(1f))
            KpiCard("Reprovados", state.failed, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onStart, enabled = !state.status.running, modifier = Modifier.weight(1f)) {
                Text("Iniciar testes")
            }
            OutlinedButton(onClick = onStop, enabled = state.status.running, modifier = Modifier.weight(1f)) {
                Text("Interromper")
            }
        }
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onNav(Screen.ESP) }, modifier = Modifier.weight(1f)) { Text("ESP") }
            OutlinedButton(onClick = { onNav(Screen.SETTINGS) }, modifier = Modifier.weight(1f)) { Text("Config") }
            OutlinedButton(onClick = { onNav(Screen.RESULTS) }, modifier = Modifier.weight(1f)) { Text("Historico") }
        }
    }
}

@Composable
private fun StatusCard(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun KpiCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value.toString(), style = MaterialTheme.typography.titleLarge)
        }
    }
}
