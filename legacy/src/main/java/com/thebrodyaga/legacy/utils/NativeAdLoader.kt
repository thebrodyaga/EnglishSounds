package com.thebrodyaga.legacy.utils

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.NativeAdOptions.NativeMediaAspectRatio
import com.jakewharton.rxrelay2.BehaviorRelay
import com.thebrodyaga.legacy.data.AdBox
import com.thebrodyaga.legacy.data.AdTag
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class NativeAdLoader constructor(
    context: Context,
    lifecycle: Lifecycle,
    private val adTag: AdTag,
    @NativeMediaAspectRatio private val mediaAspectRatio: Int = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE
) : LifecycleObserver {

    val adsObservable = BehaviorRelay.createDefault<AdBox>(AdBox(null, adTag))
    private val builder = AdLoader.Builder(context, context.getString(adTag.adUnitIdRes()))
    private var workDisposable: Disposable? = null

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
        workDisposable?.dispose()
        workDisposable = Observable.interval(0, 1, TimeUnit.MINUTES)
            .flatMap { loadAdObservable(adTag) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adsObservable.value?.ad?.destroy()
                adsObservable.accept(it)
            }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        Timber.i("stop load ad work adTag = $adTag")
        adsObservable.value?.ad?.destroy()
        workDisposable?.dispose()
        workDisposable = null
    }

    private fun loadAdObservable(adTag: AdTag) = Observable.create<AdBox> {

        lateinit var adLoader: AdLoader

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
        adLoader.loadAd(AdRequest.Builder().build())
    }
}