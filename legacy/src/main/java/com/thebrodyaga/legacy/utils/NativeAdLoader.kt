package com.thebrodyaga.legacy.utils

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.coroutineScope
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.NativeAdOptions.NativeMediaAspectRatio
import com.thebrodyaga.legacy.data.AdBox
import com.thebrodyaga.legacy.data.AdTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.concurrent.TimeUnit

class NativeAdLoader constructor(
    context: Context,
    private val lifecycle: Lifecycle,
    private val adTag: AdTag,
    @NativeMediaAspectRatio private val mediaAspectRatio: Int = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE
) : LifecycleObserver {

    private val _adBoxFlow = MutableStateFlow<AdBox>(AdBox(null, adTag))
    val adBoxFlow = _adBoxFlow.asStateFlow()
    private val builder = AdLoader.Builder(context, context.getString(adTag.adUnitIdRes()))
    private var workJob: Job? = null

    private val videoOptions: VideoOptions = VideoOptions.Builder().build()

    private val adOptions: NativeAdOptions

    init {
        adOptions = NativeAdOptions.Builder()
            .setMediaAspectRatio(mediaAspectRatio)
            .setVideoOptions(videoOptions)
            .build()

        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        Timber.i("start load ad work adTag = $adTag")
        workJob?.cancel()

        workJob = interval(timeInMillis = 1, timeUnit = TimeUnit.MINUTES)
            .flatMapLatest { loadAdObservable(adTag) }
            .flowOn(Dispatchers.IO)
            .onEach {
                adBoxFlow.value.ad?.destroy()
                _adBoxFlow.value = (it)
            }
            .launchIn(lifecycle.coroutineScope)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        Timber.i("stop load ad work adTag = $adTag")
        adBoxFlow.value.ad?.destroy()
        workJob?.cancel()
        workJob = null
    }

    private fun interval(timeInMillis: Long, timeUnit: TimeUnit): Flow<Long> = flow {

        var counter: Long = 0

        val delayTime = when (timeUnit) {
            TimeUnit.MICROSECONDS -> timeInMillis / 1000
            TimeUnit.NANOSECONDS -> timeInMillis / 1_000_000
            TimeUnit.SECONDS -> timeInMillis * 1000
            TimeUnit.MINUTES -> 60 * timeInMillis * 1000
            TimeUnit.HOURS -> 60 * 60 * timeInMillis * 1000
            TimeUnit.DAYS -> 24 * 60 * 60 * timeInMillis * 1000
            else -> timeInMillis
        }

        while (true) {
            delay(delayTime)
            emit(counter++)
        }

    }

    private fun loadAdObservable(adTag: AdTag) = callbackFlow<AdBox> {

        /*lateinit var adLoader: AdLoader

        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd ->
            if (it.isDisposed) {
                Timber.i("loadAdObservable isDisposed = true adTag = $adTag")
                unifiedNativeAd.destroy()
            } else {
                Timber.i("loadAdObservable onNext adTag = $adTag")
                it.onNext(AdBox(unifiedNativeAd, adTag))
            }
            Timber.i("loadAdObservable onComplete adTag = $adTag")
            it.onComplete()
        }
            .withNativeAdOptions(adOptions)
            .build()

        Timber.i("loadAdObservable loadAds adTag = $adTag")
        adLoader.loadAd(AdRequest.Builder().build())*/
    }
}