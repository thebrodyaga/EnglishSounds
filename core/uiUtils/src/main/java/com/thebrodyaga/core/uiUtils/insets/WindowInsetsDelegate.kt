package com.thebrodyaga.core.uiUtils.insets

import android.graphics.Color
import android.os.Build
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class WindowInsetsDelegate constructor(
    private val window: Window,
) {

    constructor(activity: AppCompatActivity) : this(activity.window)
    constructor(dialog: AppCompatDialogFragment) : this(
        dialog.requireDialog().window ?: error("window not found")
    )

    private val windowInsetsController: WindowInsetsControllerCompat
        get() = WindowCompat.getInsetsController(window, window.decorView)

    fun onCreate() {
        setupWindowInsets()
    }

    private fun setupWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }
}