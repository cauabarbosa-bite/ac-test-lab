package com.example.actestlab

import com.example.actestlab.data.EspConfig
import com.example.actestlab.domain.SelfTestRunner
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class SelfTestRunnerTest {

    @Test fun all_diagnostics_pass() = runTest {
        val runner = SelfTestRunner()
        val results = runner.run(EspConfig(diagnosticsDurationMs = 10_000L))
        assertEquals(6, results.size)
        assertTrue(results.all { it.passed })
    }

    @Test fun expected_ids() = runTest {
        val runner = SelfTestRunner()
        val results = runner.run(EspConfig(diagnosticsDurationMs = 10_000L))
        assertEquals(
            listOf("DT-01", "DT-02", "DT-03", "DT-04", "DT-05", "DT-06"),
            results.map { it.caseId }
        )
    }

    @Test fun not_empty() = runTest {
        val runner = SelfTestRunner()
        val results = runner.run(EspConfig(diagnosticsDurationMs = 3_000L))
        assertTrue(results.isNotEmpty())
    }
}
