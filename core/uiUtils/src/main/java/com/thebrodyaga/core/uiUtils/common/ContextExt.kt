package com.thebrodyaga.core.uiUtils.common

import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater

private fun Context.getAttrData(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

private fun Context.getAttrRes(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

@ColorInt
fun Context.getAttrColor(@AttrRes attr: Int): Int = getAttrData(attr)

@ColorRes
fun Context.getAttrColorRes(@AttrRes attr: Int): Int = getAttrRes(attr)

@StyleRes
fun Context.getAttrStyle(@AttrRes attr: Int): Int = getAttrRes(attr)

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)