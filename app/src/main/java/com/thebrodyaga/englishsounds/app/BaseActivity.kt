package com.thebrodyaga.englishsounds.app

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import moxy.MvpAppCompatActivity

abstract class BaseActivity : MvpAppCompatActivity() {
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }
}