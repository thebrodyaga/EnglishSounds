package com.thebrodyaga.core.uiUtils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.view.WindowCompat
import com.thebrodyaga.core.uiUtils.insets.imeInsetType

val Activity.insetsController
    get() = WindowCompat.getInsetsController(window, window.decorView)

fun Activity.showKeyboard() = insetsController.show(imeInsetType)
fun Activity.hideKeyboard() = insetsController.hide(imeInsetType)

fun Context.showKeyboard() = (this as? Activity)?.showKeyboard()
fun Context.hideKeyboard() = (this as? Activity)?.hideKeyboard()

fun View.showKeyboard() = context.showKeyboard()
fun View.hideKeyboard() = context.hideKeyboard()