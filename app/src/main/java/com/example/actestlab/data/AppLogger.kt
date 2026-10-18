package com.example.actestlab.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogEntry(val ts: Long, val level: LogLevel, val tag: String, val message: String)

object AppLogger {
    private const val CAPACITY = 2_000
    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    val entries: StateFlow<List<LogEntry>> = _entries

    @Volatile private var minLevel: LogLevel = LogLevel.INFO
    private val fmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    fun install(context: Context) { }
    fun setMinLevel(l: LogLevel) { minLevel = l }

    fun log(level: LogLevel, tag: String, message: String) {
        if (level.ordinal > minLevel.ordinal) return
        val e = LogEntry(System.currentTimeMillis(), level, tag, message)
        val current = _entries.value
        val updated = if (current.size >= CAPACITY) current.drop(1) + e else current + e
        _entries.value = updated
    }

    fun i(tag: String, msg: String) = log(LogLevel.INFO, tag, msg)
    fun w(tag: String, msg: String) = log(LogLevel.WARN, tag, msg)
    fun e(tag: String, msg: String) = log(LogLevel.ERROR, tag, msg)
    fun d(tag: String, msg: String) = log(LogLevel.DEBUG, tag, msg)

    fun format(e: LogEntry): String = "${fmt.format(Date(e.ts))} ${e.level} [${e.tag}] ${e.message}"
}
