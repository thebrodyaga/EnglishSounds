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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class GoogleSingleAdLoader @Inject constructor(
    private val appAdManager: AppAdManager,
) : SingleAdLoader {

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            adFlow.value.google()?.ad?.destroy()
            owner.lifecycle.removeObserver(this)
        }
    }

    private var loadAdJob: Job? = null
    private val adFlow = MutableStateFlow<AppAd>(AppAd.Empty)

    override fun flowAd() = adFlow.asStateFlow()

    override fun loadAd(lifecycle: Lifecycle, adType: AdType, context: Context) {
        lifecycle.addObserver(lifecycleObserver)
        loadAdJob?.cancel()
        loadAdJob = appAdManager.loadAds
            .flatMapLatest {
                loadAd(appAdManager.getAdKey(adType), context)
            }
            .onEach { newAd ->
                val oldAd = adFlow.value
                when {
                    oldAd != newAd && oldAd is AppAd.Google && newAd is AppAd.Google -> {
                        oldAd.ad.destroy()
                        adFlow.value = newAd
                    }
                    // ignore any error/loading is oldAd exist
                    oldAd is AppAd.Google -> Unit
                    else -> adFlow.value = newAd
                }
            }
            .catch { Timber.e(it) }
            .launchIn(lifecycle.coroutineScope)
    }

    private fun loadAd(adId: String, context: Context): Flow<AppAd> = callbackFlow {
        loadNewAd(context, adId)
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
            val result = AppAd.Google(loadedTime = System.currentTimeMillis(), ad = nativeAd, adId = adId)
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