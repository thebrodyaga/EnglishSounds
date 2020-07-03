package com.thebrodyaga.englishsounds.utils

import android.content.Context
import androidx.annotation.StringRes
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class NativeAdLoader(
    context: Context,
    private @StringRes val adUnitIdRes: Int,
    private val adsCount: Int
) {

    val adsObservable = BehaviorRelay.create<List<UnifiedNativeAd>>()
    private val builder = AdLoader.Builder(context, context.getString(adUnitIdRes))
    private var adsList = listOf<UnifiedNativeAd>()
    private var workDisposable: Disposable? = null

    fun start() {
        if (workDisposable != null)
            return
        workDisposable = intervalObservable
            .subscribeOn(Schedulers.io())
            .subscribe { adsObservable.accept(it) }
    }

    fun stop() {
        workDisposable?.dispose()
    }

    private val intervalObservable = Observable.interval(10, TimeUnit.MINUTES)
        .flatMap { loadAdObservable() }
        .map { list ->
            adsList.forEach { it.destroy() }
            adsList = list
            adsList
        }
        .doOnDispose { adsList.forEach { it.destroy() } }

    private fun loadAdObservable() = Observable.create<List<UnifiedNativeAd>> {

        val result = mutableListOf<UnifiedNativeAd>()

        lateinit var adLoader: AdLoader

        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd ->
            if (it.isDisposed) {
                unifiedNativeAd.destroy()
                return@forUnifiedNativeAd
            }
            result.add(unifiedNativeAd)
            if (!adLoader.isLoading) {
                it.onNext(result)
                it.onComplete()
            }
        }
            .withNativeAdOptions(adOptions)
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), adsCount)
    }

    private val videoOptions = VideoOptions.Builder()
        .build()

    private val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()
}