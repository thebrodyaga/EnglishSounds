package com.thebrodyaga.ad.api

import android.content.Context
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.Flow

interface SingleAdLoader {
    fun flowAd(): Flow<AppAd>
    fun loadAd(lifecycle: Lifecycle, adType: AdType, context: Context)
}