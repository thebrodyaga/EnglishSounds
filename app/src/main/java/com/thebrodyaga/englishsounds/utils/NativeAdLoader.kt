package com.thebrodyaga.englishsounds.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.jakewharton.rxrelay2.BehaviorRelay
import com.thebrodyaga.englishsounds.domine.entities.data.AdBox
import com.thebrodyaga.englishsounds.domine.entities.data.AdListBox
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class NativeAdLoader constructor(
    context: Context,
    lifecycle: Lifecycle,
    @StringRes adUnitIdRes: Int,
    private val adsCount: Int
) : LifecycleObserver {

    val adsObservable = BehaviorRelay.create<AdListBox>()
    private val builder = AdLoader.Builder(context, context.getString(adUnitIdRes))
    private var adsList = listOf<AdBox>()
    private var workDisposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        Timber.i("start load ad work")
        workDisposable?.dispose()
        workDisposable = intervalObservable
            .subscribeOn(Schedulers.io())
            .subscribe { adsObservable.accept(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        Timber.i("stop load ad work")
        workDisposable?.dispose()
        workDisposable = null
    }

    private val intervalObservable = Observable.interval(0, 10, TimeUnit.MINUTES)
        .flatMap { loadAdObservable(adsCount) }
        .map { list ->
            adsList.forEach { it.ad?.destroy() }
            adsList = list.ads
            list
        }
        .doOnDispose { adsList.forEach { it.ad?.destroy() } }

    private fun loadAdObservable(adsCount: Int) = Observable.create<AdListBox> {

        val result = mutableListOf<UnifiedNativeAd>()

        lateinit var adLoader: AdLoader

        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd ->
            if (it.isDisposed) {
                Timber.i("loadAdObservable isDisposed = true")
                unifiedNativeAd.destroy()
                return@forUnifiedNativeAd
            }
            Timber.i("loadAdObservable add unifiedNativeAd")
            result.add(unifiedNativeAd)
            if (!adLoader.isLoading) {
                Timber.i("loadAdObservable onComplete")
                it.onNext(AdListBox(adsCount, result.map { AdBox(it) }))
                it.onComplete()
            }
        }
            .withNativeAdOptions(adOptions)
            .build()

        Timber.i("loadAdObservable loadAds adsCount = $adsCount")
        adLoader.loadAds(AdRequest.Builder().build(), adsCount)
    }

    private val videoOptions = VideoOptions.Builder()
        .build()

    private val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()
}