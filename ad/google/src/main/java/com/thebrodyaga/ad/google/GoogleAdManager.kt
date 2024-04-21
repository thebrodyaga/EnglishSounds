package com.thebrodyaga.ad.google

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.thebrodyaga.ad.api.AdType
import com.thebrodyaga.ad.api.AppAdManager
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.englishsounds.ad.google.R
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class GoogleAdManager @Inject constructor(
    private val app: Application,
    private val googleMobileAdsConsentManager: GoogleMobileAdsConsentManager,
    private val settingManager: SettingManager,
) : AppAdManager {

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun refreshAds() {
        if (googleMobileAdsConsentManager.canRequestAds) {
            loadAds.tryEmit(Unit)
        }
    }

    private val soundListKeys =  AdKeysStack(listOf(
        app.resources.getString(R.string.soundListFirstAdKey),
        app.resources.getString(R.string.soundListSecondAdKey),
        app.resources.getString(R.string.soundListThirdAdKey),
    ))

    private val soundDetailsKeys =  AdKeysStack(listOf(
        app.resources.getString(R.string.soundDetailsAdKey),
        app.resources.getString(R.string.soundDetailsSecondAdKey),
        app.resources.getString(R.string.soundDetailsThirdAdKey),
    ))

    private val videoListKeys =  AdKeysStack(listOf(
        app.resources.getString(R.string.videoListAdKey),
        app.resources.getString(R.string.videoListSecondAdKey),
        app.resources.getString(R.string.videoListThirdAdKey),
    ))

    override fun getAdKey(adType: AdType): String {
        return when (adType) {
            AdType.SOUND_LIST -> soundListKeys.getKey()
            AdType.SOUND_DETAILS -> soundDetailsKeys.getKey()
            AdType.TRAINING -> app.resources.getString(R.string.trainingAdKey)
            AdType.VIDEO_LIST -> videoListKeys.getKey()
        }
    }

    override val loadAds: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onCreate(activity: AppCompatActivity) {
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk(activity)
        } else {
            fun gatherConsent() {
                googleMobileAdsConsentManager.gatherConsent(activity) { consentError ->
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        initializeMobileAdsSdk(activity)
                    }
                }
            }
            if (settingManager.isFirstAppStart()) {
                activity.lifecycleScope.launchWhenCreated {
                    delay(3000)
                    gatherConsent()
                }
            } else gatherConsent()
        }
    }

    private fun initializeMobileAdsSdk(context: Context) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context) { initializationStatus ->
            loadAds.tryEmit(Unit)
        }
    }

    private class AdKeysStack(private val ads: List<String>) {

        private var cursor = 0

        init {
            if (ads.isEmpty()) throw IllegalArgumentException("ads must be not empty")
        }


        fun getKey(): String {
            var key = ads.getOrNull(cursor)
            if (key == null) {
                cursor = 0
                key = ads.first()
            }
            cursor += 1
            return key
        }

    }
}