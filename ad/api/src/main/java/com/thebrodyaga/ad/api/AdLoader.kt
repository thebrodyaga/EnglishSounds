package com.thebrodyaga.ad.api

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.StateFlow

interface AppAdLoader {
    val soundListFirstAd: StateFlow<AppAd>
    val soundListSecondAd: StateFlow<AppAd>
    val soundDetailsAd: StateFlow<AppAd>
    val trainingAd: StateFlow<AppAd>
    val videoListAd: StateFlow<AppAd>
    fun onDestroy(activity: AppCompatActivity)
    fun onCreate(activity: AppCompatActivity)
}