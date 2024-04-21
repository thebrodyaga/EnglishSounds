package com.thebrodyaga.core.uiUtils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.withStarted
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun Lifecycle.saveWithStarted(crossinline block: () -> Unit) {
    this.coroutineScope.launch {
        runCatching { withStarted { block() } }
    }
}

inline fun <T> Flow<T>.launchWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: () -> Unit = {}
): Job {
    return lifecycle.coroutineScope.launch {
        this@launchWithLifecycle
            .flowWithLifecycle(lifecycle, minActiveState)
            .collect { block() }
    }
}