package com.thebrodyaga.ad.google

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE
import com.thebrodyaga.ad.api.AppAd
import com.thebrodyaga.ad.api.AppAdLoader
import com.thebrodyaga.ad.api.google
import com.thebrodyaga.englishsounds.ad.google.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class GoogleAdLoader @Inject constructor(
    private val app: Application,
    private val googleMobileAdsConsentManager: GoogleMobileAdsConsentManager,
) : AppAdLoader {

    private var appActivity = WeakReference<AppCompatActivity>(null)
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private val soundListFirstAdKey = app.resources.getString(R.string.soundListFirstAdKey)
    private val soundListSecondAdKey = app.resources.getString(R.string.soundListSecondAdKey)
    private val soundDetailsAdKey = app.resources.getString(R.string.soundDetailsAdKey)
    private val trainingAdKey = app.resources.getString(R.string.trainingAdKey)
    private val videoListAdKey = app.resources.getString(R.string.videoListAdKey)

    private val ads = mutableMapOf<String, MutableStateFlow<AppAd>>(
        soundListFirstAdKey to MutableStateFlow(AppAd.Empty),
//        soundListSecondAdKey to MutableStateFlow(AppAd.Empty),
        soundDetailsAdKey to MutableStateFlow(AppAd.Empty),
        trainingAdKey to MutableStateFlow(AppAd.Empty),
        videoListAdKey to MutableStateFlow(AppAd.Empty),
    )

    private fun MutableMap<String, MutableStateFlow<AppAd>>.mustGet(adId: String) =
        this[adId] ?: throw IllegalArgumentException("init ads map")


    override val soundListFirstAd: StateFlow<AppAd>
        get() = returnOrLoadAd(soundListFirstAdKey)
    // not used
    override val soundListSecondAd: StateFlow<AppAd>
        get() = MutableStateFlow(AppAd.Empty)
    override val soundDetailsAd: StateFlow<AppAd>
        get() = returnOrLoadAd(soundDetailsAdKey)
    override val trainingAd: StateFlow<AppAd>
        get() = returnOrLoadAd(trainingAdKey)
    override val videoListAd: StateFlow<AppAd>
        get() = returnOrLoadAd(videoListAdKey)

    override fun refreshAds(activity: Activity) {
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk(activity)
        }
    }

    override fun onCreate(activity: AppCompatActivity) {
        this.appActivity = WeakReference<AppCompatActivity>(activity)
        googleMobileAdsConsentManager.gatherConsent(activity) { consentError ->
            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk(activity)
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk(activity)
        }
    }

    private fun initializeMobileAdsSdk(context: Context) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context) { initializationStatus ->
            // Load an ad.
            ads.forEach { returnOrLoadAd(it.key) }
        }
    }

    private fun returnOrLoadAd(adId: String): StateFlow<AppAd> {
        val adState = ads.mustGet(adId)
        val context = appActivity.get() ?: return adState
        return when (val ad = adState.value) {
            AppAd.Empty -> {
                loadAd(adId, context)
                adState
            }

            is AppAd.Google -> {
                val now = System.currentTimeMillis()
                val isExpired = TimeUnit.MILLISECONDS
                    .toMinutes(now - ad.loadedTime) >= 60
                if (isExpired) {
                    adState.update { AppAd.Loading }
                    loadAd(adId, context)
                }
                adState
            }

            AppAd.Loading -> adState
        }
    }

    private fun loadAd(adId: String, context: Context) {
        val adState = ads.mustGet(adId)
        val oldAdd = adState.value.google()
        // no needed
        adState.update { AppAd.Loading }
        oldAdd?.ad?.destroy()
        val adLoader = AdLoader.Builder(context, adId).forNativeAd { newAd ->
            val activity = this.appActivity.get()
            if (activity == null || activity.isDestroyed
            ) {
                newAd.destroy()
                return@forNativeAd
            }
            adState.update {
                AppAd.Google(
                    loadedTime = System.currentTimeMillis(),
                    ad = newAd
                )
            }

        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                adState.update { AppAd.Empty }
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder()
                .setMediaAspectRatio(NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                .build()
        ).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy(activity: AppCompatActivity) {
        if (activity.isFinishing) destroyAllAds()
    }

    private fun destroyAllAds() {
        ads.forEach {
            val (adId, adState) = it
            adState.update { AppAd.Empty }
            adState.value.google()?.ad?.destroy()
        }
    }

}