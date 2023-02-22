package com.thebrodyaga.core.uiUtils

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.View

fun Context.isSystemDarkMode(): Boolean? {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        // Night mode is not active, we're using the light theme
        Configuration.UI_MODE_NIGHT_NO -> false
        // Night mode is active, we're using dark theme
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> null
    }
}

fun View.appbarBottomPadding() {
    val tv = TypedValue()
    if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        val actionBarHeight =
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        setPadding(paddingLeft, paddingTop, paddingRight, actionBarHeight)
    }
}