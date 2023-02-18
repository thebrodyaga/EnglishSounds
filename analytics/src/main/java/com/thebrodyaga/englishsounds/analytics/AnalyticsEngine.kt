package com.thebrodyaga.englishsounds.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsEngine {

    lateinit var firebaseAnalytics: FirebaseAnalytics

    fun logEvent(name: String, bundle: Bundle?) {
        firebaseAnalytics.logEvent(name, bundle)
    }
}