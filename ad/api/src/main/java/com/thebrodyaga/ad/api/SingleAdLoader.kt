package com.thebrodyaga.ad.api

import android.content.Context
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.Flow

interface SingleAdLoader {
    fun getAd(lifecycle: Lifecycle, adType: AdType, context: Context): Flow<AppAd>
}