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

    override fun getAdKey(adType: AdType): String {
        return when (adType) {
            AdType.SOUND_LIST -> app.resources.getString(R.string.soundListFirstAdKey)
            AdType.SOUND_DETAILS -> app.resources.getString(R.string.soundDetailsAdKey)
            AdType.TRAINING -> app.resources.getString(R.string.trainingAdKey)
            AdType.VIDEO_LIST -> app.resources.getString(R.string.videoListAdKey)
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
}