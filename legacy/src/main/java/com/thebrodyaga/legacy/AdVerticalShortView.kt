package com.thebrodyaga.legacy

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnAttach
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.core.uiUtils.view.viewScope
import com.thebrodyaga.legacy.databinding.ViewAdVerticalShortBinding
import com.thebrodyaga.legacy.utils.CompositeAdLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AdVerticalShortView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val constraintSet = ConstraintSet()
    private var nativeAdLoaderJob: Job? = null
    val binding by viewBinding(ViewAdVerticalShortBinding::bind)

    init {

        View.inflate(context, R.layout.view_ad_vertical_short, this)

        setUpView()
    }

    private fun setUpView() {
        binding.adView.apply {
            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
//            mediaView = ad_media

            // Register the view used for each individual asset.
            headlineView = binding.adHeadline
            iconView = binding.adIcon
            callToActionView = binding.adCallToAction
            priceView = binding.adPrice
            starRatingView = binding.adStars
            storeView = binding.adGooglePlayIcon
            bodyView = binding.adAdvertiser
        }
    }

    fun setAd(
        ad: ShortAdItem,
        nativeAdLoader: CompositeAdLoader,
        adapterPosition: Int? = null
    ) {
        nativeAdLoaderJob?.cancel()
        doOnAttach {
            nativeAdLoaderJob = nativeAdLoader.getLoader(ad.adTag, adapterPosition, ad.customTag)
                .adBoxFlow
                .onEach { adBox ->
                    constraintSet.clone(binding.adContainer)
                    adBox.ad?.let {
                        setEmptyView(false)
                        /*if (it.mediaContent != null) {
                            constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.VISIBLE)
                        } else constraintSet.setVisibility(ad_view.mediaView.id, ConstraintSet.GONE)*/
                        populateNativeAdView(it, binding.adView)
                    } ?: kotlin.run {
                        setEmptyView(true)
                    }
                    TransitionManager.beginDelayedTransition(binding.adContainer)
                    constraintSet.applyTo(binding.adContainer)
                }
                .launchIn(viewScope)
        }
    }

    private fun setEmptyView(isEmpty: Boolean) {
        if (isEmpty)
            constraintSet.setVisibility(binding.adEmpty.id, ConstraintSet.VISIBLE)
        else constraintSet.setVisibility(binding.adEmpty.id, ConstraintSet.INVISIBLE)
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