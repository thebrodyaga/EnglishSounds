package com.thebrodyaga.core.uiUtils

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import com.thebrodyaga.core.uiUtils.view.viewScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Context.isSystemDarkMode(): Boolean? {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        // Night mode is not active, we're using the light theme
        Configuration.UI_MODE_NIGHT_NO -> false
        // Night mode is active, we're using dark theme
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> null
    }
}

fun ViewGroup.inflate(@LayoutRes res: Int, attachToRoot: Boolean = false) =
    LayoutInflater.from(context).inflate(res, this, attachToRoot)

fun calculateNoOfColumns(
    context: Context,
    @DimenRes columnWidthRes: Int
): Int {
    val displayMetrics = context.resources.displayMetrics
    val screenWidthPx = displayMetrics.widthPixels / displayMetrics.density
    val columnWidthPx =
        context.resources.getDimension(columnWidthRes) / displayMetrics.density
    return (screenWidthPx / columnWidthPx + 0.5).toInt()// +0.5 for correct rounding to int.
}

inline fun View.setDebouncOnClick(
    delayInClick: Long = 500L,
    crossinline listener: (View) -> Unit
) {
    var delayJob: Job? = null
    setOnClickListener {
        if (delayJob?.isActive == true) return@setOnClickListener
        listener.invoke(it)
        delayJob = it.viewScope.launch { runCatching { delay(delayInClick) } }
    }
}