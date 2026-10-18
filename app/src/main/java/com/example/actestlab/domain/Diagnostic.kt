package com.example.actestlab.domain

data class DiagnosticCase(
    val id: String,
    val name: String,
    val description: String
)

data class DiagnosticResult(
    val caseId: String,
    val name: String,
    val passed: Boolean,
    val elapsedMs: Long,
    val metrics: Map<String, String>,
    val note: String
)
