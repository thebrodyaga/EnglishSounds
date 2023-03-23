package com.thebrodyaga.englishsounds.base.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.core.uiUtils.insets.WindowInsetsDelegate

abstract class BaseActivity : AppCompatActivity() {

    private val insetsDelegate by lazy { WindowInsetsDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        insetsDelegate.onCreate()
    }
}