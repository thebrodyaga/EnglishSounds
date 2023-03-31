package com.thebrodyaga.core.uiUtils.common

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import com.google.android.material.color.MaterialColors

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

fun Context.getColorStateListCompat(@AttrRes attr: Int?): ColorStateList? =
    MaterialColors.getColorStateListOrNull(this, attr ?: 0)

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)