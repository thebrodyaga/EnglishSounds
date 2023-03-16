package com.thebrodyaga.core.uiUtils.common

import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

val View.inflater: LayoutInflater
    get() = context.inflater

@ColorInt
fun View.getAttrColor(@AttrRes attr: Int): Int = context.getAttrColor(attr)

@StyleRes
fun View.getAttrStyle(@AttrRes attr: Int): Int = context.getAttrStyle(attr)
