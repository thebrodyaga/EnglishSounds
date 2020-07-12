package com.thebrodyaga.englishsounds.screen

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R

fun View.defaultTopMarginWindowInsert() {
    setOnApplyWindowInsetsListener { v, insets ->
        val params = v.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = insets.systemWindowInsetTop
        v.layoutParams = params
        insets
    }
}

fun View.defaultBottomMarginWindowInsert() {
    setOnApplyWindowInsetsListener { v, insets ->
        val params = v.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = insets.systemWindowInsetBottom
        v.layoutParams = params
        insets
    }
}

fun ViewGroup.defaultOnApplyWindowInsetsListener() {
    setOnApplyWindowInsetsListener { _, insets ->
        forEach { it.dispatchApplyWindowInsets(insets) }
        insets
    }
}

fun RecyclerView.appbarBottomPadding(includeFabSize: Boolean = false) {
    val tv = TypedValue()
    if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        val actionBarHeight =
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        val halfOfFab =
            if (includeFabSize)
                context.resources.getDimensionPixelSize(R.dimen.fab_mic_size) / 2
            else 0
        clipToPadding = false
        setPadding(paddingLeft, paddingTop, paddingRight, actionBarHeight + halfOfFab)
    }
}

fun ViewGroup.inflate(@LayoutRes res: Int, attachToRoot: Boolean = false) =
    LayoutInflater.from(context).inflate(res, this, attachToRoot)

fun View.appbarBottomPadding() {
    val tv = TypedValue()
    if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        val actionBarHeight =
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        setPadding(paddingLeft, paddingTop, paddingRight, actionBarHeight)
    }
}

fun View.isInvisible(isInvisible: Boolean) {
    this.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

fun View.isGone(isInvisible: Boolean) {
    this.visibility = if (isInvisible) View.GONE else View.VISIBLE
}

fun Context.isSystemDarkMode(): Boolean? {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        // Night mode is not active, we're using the light theme
        Configuration.UI_MODE_NIGHT_NO -> false
        // Night mode is active, we're using dark theme
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> null
    }
}

fun Context.getVideoAndDescription(): Pair<MutableMap<String, String>, MutableMap<String, String>> {
    val videoArray = resources.getStringArray(R.array.sound_video)
    val soundArray = resources.getStringArray(R.array.sound_description)
    val videoMap = mutableMapOf<String, String>()
    val descriptionMap = mutableMapOf<String, String>()
    videoArray.forEach {
        val split = it.split("::")
        videoMap[split.first()] = split[1]
    }
    soundArray.forEach {
        val split = it.split("::")
        descriptionMap[split.first()] = split[1]
    }
    return Pair(videoMap, descriptionMap)
}