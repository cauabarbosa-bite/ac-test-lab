package com.example.actestlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.actestlab.ui.Screen
import com.example.actestlab.ui.UiState

@Composable
fun ResultsScreen(state: UiState, onNav: (Screen) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Resultados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { onNav(Screen.HOME) }) { Text("Voltar") }
        }
        Text(
            "Aprovados: " + state.passed + " - Reprovados: " + state.failed,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.results) { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.caseId + " - " + r.name, style = MaterialTheme.typography.titleMedium)
                        Text(r.elapsedMs.toString() + "ms", style = MaterialTheme.typography.labelSmall)
                        if (r.metrics.isNotEmpty()) {
                            Text(
                                r.metrics.entries.joinToString(" - ") { it.key + "=" + it.value },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Text(r.note, style = MaterialTheme.typography.labelSmall)
                        Text(
                            if (r.passed) "APROVADO" else "REPROVADO",
                            color = if (r.passed) Color(0xFF22C55E) else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}
