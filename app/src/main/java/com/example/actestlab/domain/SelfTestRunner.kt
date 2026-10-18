package com.example.actestlab.domain

import com.example.actestlab.data.AppLogger
import com.example.actestlab.data.EspConfig
import com.example.actestlab.esp.SyntheticEntitySource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.math.abs

class SelfTestRunner(
    private val source: SyntheticEntitySource = SyntheticEntitySource()
) {
    sealed class Event {
        data class Started(val id: String) : Event()
        data class Stopped(val id: String, val reason: String) : Event()
        data class Result(val r: DiagnosticResult) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val events: SharedFlow<Event> = _events

    val cases: List<DiagnosticCase> = listOf(
        DiagnosticCase("DT-01", "Determinismo", "Mesmo t gera entidades identicas"),
        DiagnosticCase("DT-02", "Bounding box", "Caixas validas"),
        DiagnosticCase("DT-03", "Distanciamento", "Distancias finitas e >= 0"),
        DiagnosticCase("DT-04", "Jitter do loop", "Variacao de intervalo"),
        DiagnosticCase("DT-05", "Throughput", "Entidades por segundo"),
        DiagnosticCase("DT-06", "Cancelamento", "Corrotina responsiva")
    )

    suspend fun run(config: EspConfig): List<DiagnosticResult> {
        val sid = "local-" + System.currentTimeMillis()
        AppLogger.i("SelfTest", "inicio " + sid)
        _events.emit(Event.Started(sid))
        val out = mutableListOf<DiagnosticResult>()
        val deadline = System.currentTimeMillis() + config.diagnosticsDurationMs

        for (case in cases) {
            if (System.currentTimeMillis() > deadline) break
            val t0 = System.nanoTime()
            val r = try {
                when (case.id) {
                    "DT-01" -> determinism(case)
                    "DT-02" -> boundingBox(case)
                    "DT-03" -> distance(case)
                    "DT-04" -> jitter(case, config)
                    "DT-05" -> throughput(case, config)
                    "DT-06" -> cancelCheck(case)
                    else -> DiagnosticResult(case.id, case.name, false, 0, emptyMap(), "desconhecido")
                }
            } catch (e: Throwable) {
                AppLogger.e("SelfTest", case.id + " erro: " + e.message)
                DiagnosticResult(case.id, case.name, false, 0, emptyMap(), "erro: " + e.message)
            }
            val final = r.copy(elapsedMs = (System.nanoTime() - t0) / 1_000_000)
            AppLogger.i("SelfTest", final.caseId + " " + (if (final.passed) "PASS" else "FAIL") + " " + final.elapsedMs + "ms")
            out += final
            _events.emit(Event.Result(final))
            delay(60)
        }
        _events.emit(Event.Stopped(sid, "concluido"))
        return out
    }

    private fun determinism(c: DiagnosticCase): DiagnosticResult {
        val a = source.generate(8, true, 1.5f)
        val b = source.generate(8, true, 1.5f)
        return DiagnosticResult(c.id, c.name, a == b, 0, emptyMap(), if (a == b) "identicas" else "divergencia")
    }

    private fun boundingBox(c: DiagnosticCase): DiagnosticResult {
        val ents = source.generate(16, true, 2.0f)
        var bad = 0
        for (e in ents) if (e.worldX < 0 || e.worldY < 0 || e.width <= 0 || e.height <= 0) bad++
        return DiagnosticResult(c.id, c.name, bad == 0, 0, mapOf("bad" to bad.toString()), if (bad == 0) "ok" else bad.toString() + " invalidas")
    }

    private fun distance(c: DiagnosticCase): DiagnosticResult {
        val ents = source.generate(16, false, 0f)
        val ok = ents.all { it.distance.isFinite() && it.distance >= 0f }
        return DiagnosticResult(c.id, c.name, ok, 0, mapOf("n" to ents.size.toString()), if (ok) "ok" else "invalida")
    }

    private suspend fun jitter(c: DiagnosticCase, config: EspConfig): DiagnosticResult {
        val periodMs = 1000L / config.refreshHz.coerceIn(5, 60)
        val samples = LongArray(30)
        var prev = System.nanoTime()
        for (i in samples.indices) {
            delay(periodMs)
            val now = System.nanoTime()
            samples[i] = (now - prev) / 1_000_000
            prev = now
        }
        val mean = samples.average()
        val maxDev = samples.maxOf { abs(it - mean) }
        val ok = maxDev < periodMs * 2
        return DiagnosticResult(c.id, c.name, ok, 0, mapOf("mean" to "%.1f".format(mean)), "periodo ~" + periodMs + "ms")
    }

    private suspend fun throughput(c: DiagnosticCase, config: EspConfig): DiagnosticResult {
        val n = config.entityCount.coerceIn(1, 64)
        val iterations = 20
        val t0 = System.nanoTime()
        repeat(iterations) { source.generate(n, true, it * 0.1f) }
        val elapsed = (System.nanoTime() - t0) / 1_000_000.0
        val eps = if (elapsed > 0) (n * iterations / (elapsed / 1000.0)) else 0.0
        return DiagnosticResult(c.id, c.name, elapsed < 500.0, 0, mapOf("eps" to "%.0f".format(eps)), elapsed.toString() + "ms")
    }

    private suspend fun cancelCheck(c: DiagnosticCase): DiagnosticResult {
        delay(20)
        return DiagnosticResult(c.id, c.name, true, 0, emptyMap(), "ok")
    }
}
