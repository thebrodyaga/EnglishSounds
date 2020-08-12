package com.thebrodyaga.englishsounds.screen.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.ShortAdItem
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_ad_vertical_short.view.*

class AdVerticalShortView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val constraintSet = ConstraintSet()
    private var disposable: Disposable? = null

    init {

        View.inflate(context, R.layout.view_ad_vertical_short, this)

        setUpView()
    }

    private fun setUpView() {
        ad_view.apply {
            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
//            mediaView = ad_media

            // Register the view used for each individual asset.
            headlineView = ad_headline
            iconView = ad_icon
            callToActionView = ad_call_to_action
            priceView = ad_price
            starRatingView = ad_stars
            storeView = ad_google_play_icon
            bodyView = ad_advertiser
        }
    }

    fun dispose() {
        disposable?.dispose()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dispose()
    }

    fun setAd(
        ad: ShortAdItem,
        nativeAdLoader: CompositeAdLoader,
        adapterPosition: Int? = null
    ) {
        disposable?.dispose()
        disposable = nativeAdLoader.getLoader(ad.adTag, adapterPosition, ad.customTag)
            .adsObservable
            .subscribe { adBox ->
                constraintSet.clone(ad_container)
                adBox.ad?.let {
                    setEmptyView(false)
                    /*if (it.mediaContent != null) {
                        constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.VISIBLE)
                    } else constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.GONE)*/
                    populateNativeAdView(it, ad_view)
                } ?: kotlin.run {
                    setEmptyView(true)
                }
                TransitionManager.beginDelayedTransition(ad_container)
                constraintSet.applyTo(ad_container)
            }
    }

    private fun setEmptyView(isEmpty: Boolean) {
        if (isEmpty)
            constraintSet.setVisibility(ad_empty.id, ConstraintSet.VISIBLE)
        else constraintSet.setVisibility(ad_empty.id, ConstraintSet.INVISIBLE)
    }

    private fun populateNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them
        setIcon(nativeAd, adView)
        setPrice(nativeAd, adView)
        setStore(nativeAd, adView)
        setStartRating(nativeAd, adView)
        setBody(nativeAd, adView)
//        setAdvertiser(nativeAd, adView)

        adView.setNativeAd(nativeAd)
    }

    private fun setIcon(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        val icon = nativeAd.icon
        if (icon == null) {
            constraintSet.setVisibility(adView.iconView.id, ConstraintSet.GONE)
        } else {
            constraintSet.setVisibility(adView.iconView.id, ConstraintSet.VISIBLE)
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
        }
    }

    private fun setPrice(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        val price = nativeAd.price
        if (price == null) {
            constraintSet.setVisibility(adView.priceView.id, ConstraintSet.GONE)
        } else {
            constraintSet.setVisibility(adView.priceView.id, ConstraintSet.VISIBLE)
            (adView.priceView as TextView).text = price
        }
    }

    private fun setStore(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        val store = nativeAd.store
        if (store == null) {
            constraintSet.setVisibility(adView.storeView.id, ConstraintSet.GONE)
        } else {
            constraintSet.setVisibility(adView.storeView.id, ConstraintSet.VISIBLE)
            (adView.storeView as? TextView)?.text = store
        }
    }

    private fun setStartRating(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        val starRating = nativeAd.starRating
        if (starRating == null) {
            constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.GONE)
        } else {
            constraintSet.setVisibility(adView.starRatingView.id, ConstraintSet.VISIBLE)
            (adView.starRatingView as RatingBar).rating = starRating.toFloat()
        }
    }

    private fun setAdvertiser(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        val advertiser = nativeAd.advertiser
        if (advertiser == null) {
            constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.GONE)
        } else {
            constraintSet.setVisibility(adView.advertiserView.id, ConstraintSet.VISIBLE)
            (adView.advertiserView as TextView).text = advertiser
        }
    }

    private fun setBody(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        val body = nativeAd.body
        if (body == null) {
            constraintSet.setVisibility(adView.bodyView.id, ConstraintSet.GONE)
        } else {
            constraintSet.setVisibility(adView.bodyView.id, ConstraintSet.VISIBLE)
            (adView.bodyView as TextView).text = body
        }
    }
}