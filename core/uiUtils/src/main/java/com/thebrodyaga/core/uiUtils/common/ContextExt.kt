package com.thebrodyaga.core.uiUtils.common

import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater

private fun Context.getAttr(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

@ColorInt
fun Context.getAttrColor(@AttrRes attr: Int): Int = getAttr(attr)

@StyleRes
fun Context.getAttrStyle(@AttrRes attr: Int): Int = getAttr(attr)

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)