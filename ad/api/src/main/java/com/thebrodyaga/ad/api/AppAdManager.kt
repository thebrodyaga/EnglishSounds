package com.thebrodyaga.ad.api

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.Flow

interface AppAdManager {
    fun onCreate(activity: AppCompatActivity)
    fun refreshAds()
    fun getAdKey(adType: AdType):String
    val loadAds: Flow<Unit>
}