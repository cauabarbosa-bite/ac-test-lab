package com.example.actestlab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.actestlab.data.*
import com.example.actestlab.domain.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen { HOME, ESP, SETTINGS, RESULTS }

data class SelfTestStatus(
    val running: Boolean = false,
    val runId: String? = null,
    val lastFinishedAt: Long? = null
)

data class UiState(
    val screen: Screen = Screen.HOME,
    val config: EspConfig = EspConfig(),
    val entities: List<Entity> = emptyList(),
    val status: SelfTestStatus = SelfTestStatus(),
    val results: List<DiagnosticResult> = emptyList(),
    val total: Int = 0,
    val passed: Int = 0,
    val failed: Int = 0
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val settings = SettingsRepository(app)
    private val runner = SelfTestRunner()
    private val source = SyntheticEntitySource()

    private val _ui = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _ui.asStateFlow()

    private var renderJob: Job? = null
    private var testJob: Job? = null

    init {
        viewModelScope.launch {
            settings.configFlow.collect { cfg ->
                _ui.update { it.copy(config = cfg) }
                AppLogger.setMinLevel(cfg.logLevel)
                restartRender()
                refreshEntities()
            }
        }
        viewModelScope.launch {
            runner.events.collect { ev ->
                when (ev) {
                    is SelfTestRunner.Event.Started -> _ui.update {
                        it.copy(status = SelfTestStatus(running = true, runId = ev.id))
                    }
                    is SelfTestRunner.Event.Stopped -> _ui.update {
                        it.copy(status = it.status.copy(running = false, lastFinishedAt = System.currentTimeMillis()))
                    }
                    is SelfTestRunner.Event.Result -> _ui.update { s ->
                        val r = s.results + ev.r
                        s.copy(
                            results = r,
                            total = r.size,
                            passed = r.count { x -> x.passed },
                            failed = r.count { x -> !x.passed }
                        )
                    }
                }
            }
        }
    }

    fun navigate(screen: Screen) = _ui.update { it.copy(screen = screen) }

    fun updateConfig(mut: (EspConfig) -> EspConfig) {
        val newCfg = mut(_ui.value.config)
        _ui.update { it.copy(config = newCfg) }
        viewModelScope.launch { settings.save(newCfg) }
        refreshEntities()
        restartRender()
    }

    fun startTests() {
        if (_ui.value.status.running) return
        _ui.update { it.copy(results = emptyList(), total = 0, passed = 0, failed = 0) }
        testJob = viewModelScope.launch { runner.run(_ui.value.config) }
    }

    fun stopTests() {
        testJob?.cancel()
        testJob = null
        _ui.update { it.copy(status = it.status.copy(running = false)) }
    }

    private fun refreshEntities() {
        val cfg = _ui.value.config
        val t = System.currentTimeMillis() / 1000f
        _ui.update { it.copy(entities = source.generate(cfg.entityCount, cfg.syntheticMotion, t)) }
    }

    private fun restartRender() {
        renderJob?.cancel()
        if (!com.example.actestlab.BuildConfig.DEBUG_ESP_ENABLED) return
        val hz = _ui.value.config.refreshHz.coerceIn(5, 60)
        val periodMs = 1000L / hz
        renderJob = viewModelScope.launch {
            while (true) {
                refreshEntities()
                kotlinx.coroutines.delay(periodMs)
            }
        }
    }

    override fun onCleared() {
        renderJob?.cancel()
        testJob?.cancel()
        super.onCleared()
    }
}
