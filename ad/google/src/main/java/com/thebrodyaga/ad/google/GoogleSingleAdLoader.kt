package com.thebrodyaga.ad.google

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE
import com.thebrodyaga.ad.api.AdType
import com.thebrodyaga.ad.api.AppAd
import com.thebrodyaga.ad.api.AppAdManager
import com.thebrodyaga.ad.api.SingleAdLoader
import com.thebrodyaga.ad.api.google
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GoogleSingleAdLoader @Inject constructor(
    private val appAdManager: AppAdManager,
) : SingleAdLoader {

    private var accumulatorAd: AppAd.Google? = null
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            accumulatorAd?.ad?.destroy()
            accumulatorAd = null
            owner.lifecycle.removeObserver(this)
        }
    }

    override fun getAd(
        lifecycle: Lifecycle,
        adType: AdType,
        context: Context,
    ): Flow<AppAd> {
        lifecycle.addObserver(lifecycleObserver)
        return appAdManager.loadAds
            .flatMapConcat {
                loadAd(appAdManager.getAdKey(adType), context)
            }
            .stateIn(lifecycle.coroutineScope, SharingStarted.Eagerly, AppAd.Empty)
            .filter {
                val newAd = it.google() ?: return@filter true
                val accumulatorAd = accumulatorAd ?: return@filter true
                accumulatorAd.loadedTime != newAd.loadedTime
            }
            .map { value ->
                if (value is AppAd.Google) {
                    accumulatorAd?.ad?.destroy()
                    accumulatorAd = value
                }
                accumulatorAd ?: value
            }
            .catch { Timber.e(it) }
    }

    private fun loadAd(adId: String, context: Context): Flow<AppAd> = callbackFlow {
        val now = System.currentTimeMillis()
        val oldAd = accumulatorAd
        val isExpired = oldAd != null && TimeUnit.MILLISECONDS
            .toMinutes(now - oldAd.loadedTime) >= 5
        if (oldAd != null && !isExpired) {
            send(oldAd)
        } else {
            loadNewAd(context, adId)
        }
        awaitClose()
    }

    private suspend fun ProducerScope<AppAd>.loadNewAd(context: Context, adId: String) {
        send(AppAd.Loading)
        val adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                trySend(AppAd.Empty)
            }
        }
        val adLoader = AdLoader.Builder(context, adId).forNativeAd { nativeAd ->
            val result = AppAd.Google(loadedTime = System.currentTimeMillis(), ad = nativeAd)
            trySend(result)
                .onFailure { nativeAd.destroy() }
        }.withAdListener(adListener)
            .withNativeAdOptions(nativeAdOptions())
            .build()
        adLoader.loadAd(adRequest())
    }

    private fun nativeAdOptions() = NativeAdOptions
        .Builder()
        .setMediaAspectRatio(NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
        .build()

    private fun adRequest() = AdRequest
        .Builder()
        .build()
}