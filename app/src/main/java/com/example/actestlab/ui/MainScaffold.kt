package com.example.actestlab.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.actestlab.ui.screens.*

@Composable
fun MainScaffold(state: UiState, vm: MainViewModel) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        when (state.screen) {
            Screen.HOME -> HomeScreen(state, vm::startTests, vm::stopTests, vm::navigate)
            Screen.ESP -> EspScreen(state, vm::navigate)
            Screen.SETTINGS -> SettingsScreen(state, vm::updateConfig, vm::navigate)
            Screen.RESULTS -> ResultsScreen(state, vm::navigate)
        }
    }
}
