package com.example.liliplayer.playback

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimerManager @Inject constructor() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    fun start(durationMs: Long, onFinish: () -> Unit) {
        cancel()
        _isActive.value = true
        _remainingMs.value = durationMs
        timerJob = scope.launch {
            var remaining = durationMs
            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                _remainingMs.value = remaining.coerceAtLeast(0)
            }
            _isActive.value = false
            onFinish()
        }
    }

    fun cancel() {
        timerJob?.cancel()
        timerJob = null
        _isActive.value = false
        _remainingMs.value = 0
    }
}
