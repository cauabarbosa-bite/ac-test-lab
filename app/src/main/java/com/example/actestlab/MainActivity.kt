package com.example.actestlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.actestlab.ui.MainViewModel
import com.example.actestlab.ui.MainScaffold
import com.example.actestlab.ui.theme.ACTestLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ACTestLabTheme {
                val vm: MainViewModel = viewModel()
                val state by vm.state.collectAsState()
                MainScaffold(state = state, vm = vm)
            }
        }
    }
}
