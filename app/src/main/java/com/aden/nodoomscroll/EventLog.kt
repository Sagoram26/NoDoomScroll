package com.aden.nodoomscroll

import android.icu.text.SimpleDateFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import java.util.Locale

/**
 * In-process debug event ring buffer.
 *
 * The AccessibilityService runs in the same process as MainActivity, so the UI can observe
 * this StateFlow directly. We do NOT read our own logcat (READ_LOGS is a privileged permission
 * we don't hold). `Log.d(TAG, ...)` is still emitted in parallel for `adb logcat`.
 */
object EventLog {
    private const val CAP = 60
    private val timeFmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    private val _lines = MutableStateFlow<List<String>>(emptyList())
    val lines: StateFlow<List<String>> = _lines

    @Synchronized
    fun add(line: String) {
        val stamped = "${timeFmt.format(Date())}  $line"
        val next = ArrayList<String>(_lines.value.size + 1)
        next.add(stamped)          // newest first
        next.addAll(_lines.value)
        if (next.size > CAP) next.subList(CAP, next.size).clear()
        _lines.value = next
    }
}
