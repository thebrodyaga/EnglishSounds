package com.thebrodyaga.core.uiUtils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.withStarted
import kotlinx.coroutines.launch

inline fun Lifecycle.saveWithStarted(crossinline block: () -> Unit) {
    this.coroutineScope.launch {
        runCatching { withStarted { block() } }
    }
}