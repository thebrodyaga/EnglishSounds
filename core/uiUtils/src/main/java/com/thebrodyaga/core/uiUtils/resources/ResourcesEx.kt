package com.thebrodyaga.core.uiUtils.resources

import android.content.res.Resources

val Int.dp: Int
    get() = toFloat().dp.toInt()

val Int.px: Int
    get() = toFloat().px.toInt()

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)