package com.thebrodyaga.englishsounds.base.app

import android.os.Bundle
import com.thebrodyaga.core.uiUtils.insets.WindowInsetsDelegate
import moxy.MvpAppCompatActivity

abstract class BaseActivity : MvpAppCompatActivity() {

    private val insetsDelegate by lazy { WindowInsetsDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        insetsDelegate.onCreate()
    }
}