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

fun View.appbarBottomPadding(includeFabSize: Boolean = false) {
    val tv = TypedValue()
    if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        val actionBarHeight =
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        val halfOfFab =
            if (includeFabSize)
                context.resources.getDimensionPixelSize(R.dimen.fab_mic_size) / 2
            else 0
        if (this is RecyclerView)
            clipToPadding = false
        setPadding(paddingLeft, paddingTop, paddingRight, actionBarHeight + halfOfFab)
    }
}