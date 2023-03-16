package com.thebrodyaga.core.uiUtils.common

import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.color.MaterialColors

val View.inflater: LayoutInflater
    get() = context.inflater

@ColorInt
fun View.getAttrColor(@AttrRes attr: Int): Int = context.getAttrColor(attr)

@ColorRes
fun View.getAttrColorRes(@AttrRes attr: Int): Int = context.getAttrColorRes(attr)

fun View.getColorStateList(@AttrRes attr: Int?): ColorStateList? =
    attr?.let { MaterialColors.getColorStateListOrNull(context, attr) }

@StyleRes
fun View.getAttrStyle(@AttrRes attr: Int): Int = context.getAttrStyle(attr)
