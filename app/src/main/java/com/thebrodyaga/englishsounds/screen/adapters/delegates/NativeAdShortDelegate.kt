package com.thebrodyaga.englishsounds.screen.adapters.delegates

import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.ShortAdItem
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.itme_ad_vertical_short.*

fun videoNativeShortAdDelegate(
    nativeAdLoader: CompositeAdLoader
) = adapterDelegateLayoutContainer<ShortAdItem, Any>(
    R.layout.itme_ad_vertical_short
) {

    var disposable: Disposable? = null
    val constraintSet = ConstraintSet()
    ad_view.apply {
        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        mediaView = ad_media

        // Register the view used for each individual asset.
        headlineView = ad_headline
        callToActionView = ad_call_to_action
        priceView = ad_price
        starRatingView = ad_stars
        storeView = ad_google_play_icon
        advertiserView = ad_advertiser
    }

    bind {
        fun setEmptyView(isEmpty: Boolean) {
            if (isEmpty)
                constraintSet.setVisibility(ad_empty.id, ConstraintSet.VISIBLE)
            else constraintSet.setVisibility(ad_empty.id, ConstraintSet.INVISIBLE)
        }

        fun populateNativeAdView(
            nativeAd: UnifiedNativeAd,
            adView: UnifiedNativeAdView
        ) {
            // Some assets are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.callToActionView as Button).text = nativeAd.callToAction

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.price == null) {
                constraintSet.setVisibility(adView.priceView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.priceView.id, ConstraintSet.VISIBLE)
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                constraintSet.setVisibility(adView.storeView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.storeView.id, ConstraintSet.VISIBLE)
                (adView.storeView as? TextView)?.text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.VISIBLE)
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            }
            if (nativeAd.advertiser == null) {
                constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.VISIBLE)
                (adView.advertiserView as TextView).text = nativeAd.advertiser
            }
            // Assign native ad object to the native view.
            adView.setNativeAd(nativeAd)
        }

        disposable?.dispose()
        disposable = nativeAdLoader.getLoader(item.adTag, adapterPosition, item.customTag)
            .adsObservable
            .subscribe { adBox ->
                constraintSet.clone(ad_container)
                adBox.ad?.let {
                    setEmptyView(false)
                    if (it.mediaContent != null) {
                        constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.VISIBLE)
                    } else constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.GONE)
                    populateNativeAdView(it, ad_view)
                } ?: kotlin.run {
                    setEmptyView(true)
                }
                TransitionManager.beginDelayedTransition(ad_container)
                constraintSet.applyTo(ad_container)
            }
    }

    onViewRecycled { disposable?.dispose() }
}