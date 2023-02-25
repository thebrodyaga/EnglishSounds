package com.thebrodyaga.legacy.delegates

import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.legacy.AdItem
import com.thebrodyaga.legacy.databinding.ItemAdHorizontalListBinding
import com.thebrodyaga.legacy.databinding.ItemAdVerticalListBinding
import com.thebrodyaga.legacy.utils.CompositeAdLoader
import io.reactivex.disposables.Disposable

fun videoNativeAdDelegate(
    compositeAdLoader: CompositeAdLoader,
    @RecyclerView.Orientation orientation: Int
) = if (orientation == RecyclerView.VERTICAL)
    videoVerticalNativeAdDelegate(compositeAdLoader, orientation)
else videoHorizontalNativeAdDelegate(compositeAdLoader, orientation)

private fun videoHorizontalNativeAdDelegate(
    compositeAdLoader: CompositeAdLoader,
    @RecyclerView.Orientation orientation: Int
) = adapterDelegateViewBinding<AdItem, Any, ItemAdHorizontalListBinding>(
    { layoutInflater, root -> ItemAdHorizontalListBinding.inflate(layoutInflater, root, false) }
) {

    var disposable: Disposable? = null
    val constraintSet = ConstraintSet()
    binding.adView.apply {
        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        mediaView = binding.adMedia

        // Register the view used for each individual asset.
        headlineView = binding.adHeadline
        bodyView = binding.adBody
        callToActionView = binding.adCallToAction
        iconView = binding.adIcon
        priceView = binding.adPrice
        starRatingView = binding.adStars
        storeView = binding.adGooglePlayIcon
        advertiserView = binding.adAdvertiser
    }

    bind {
        fun setDimensionRatio(ratio: String) {
            if (orientation == RecyclerView.VERTICAL)
                constraintSet.setDimensionRatio(binding.adMedia.id, ratio)
        }

        fun setEmptyView(isEmpty: Boolean) {
            if (isEmpty)
                constraintSet.setVisibility(binding.adEmpty.id, ConstraintSet.VISIBLE)
            else constraintSet.setVisibility(binding.adEmpty.id, ConstraintSet.INVISIBLE)
        }

        fun populateNativeAdView(
            nativeAd: UnifiedNativeAd,
            adView: UnifiedNativeAdView
        ) {
            // Some assets are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body
            (adView.callToActionView as Button).text = nativeAd.callToAction

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            val icon = nativeAd.icon
            if (icon == null) {
                constraintSet.setVisibility(adView.iconView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.iconView.id, ConstraintSet.VISIBLE)
                (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            }
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
        disposable = compositeAdLoader.getLoader(item.adTag, adapterPosition, item.customTag)
            .adsObservable
            .subscribe { adBox ->
                constraintSet.clone(binding.adContainer)
                adBox.ad?.let {
                    setEmptyView(false)
                    if (it.mediaContent != null) {
                        constraintSet.setVisibility(binding.adView.mediaView.id, ConstraintSet.VISIBLE)
                        setDimensionRatio(it.mediaContent.aspectRatio.toString())
                    } else constraintSet.setVisibility(binding.adView.mediaView.id, ConstraintSet.GONE)
                    populateNativeAdView(it, binding.adView)
                } ?: kotlin.run {
                    setEmptyView(true)
                }
                TransitionManager.beginDelayedTransition(binding.adContainer)
                constraintSet.applyTo(binding.adContainer)
            }
    }

    onViewRecycled { disposable?.dispose() }
}

private fun videoVerticalNativeAdDelegate(
    compositeAdLoader: CompositeAdLoader,
    @RecyclerView.Orientation orientation: Int
) = adapterDelegateViewBinding<AdItem, Any, ItemAdVerticalListBinding>(
    { layoutInflater, root -> ItemAdVerticalListBinding.inflate(layoutInflater, root, false) }
) {

    var disposable: Disposable? = null
    val constraintSet = ConstraintSet()
    binding.adView.apply {
        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        mediaView = binding.adMedia

        // Register the view used for each individual asset.
        headlineView = binding.adHeadline
        bodyView = binding.adBody
        callToActionView = binding.adCallToAction
        iconView = binding.adIcon
        priceView = binding.adPrice
        starRatingView = binding.adStars
        storeView = binding.adGooglePlayIcon
        advertiserView = binding.adAdvertiser
    }

    bind {
        fun setDimensionRatio(ratio: String) {
            if (orientation == RecyclerView.VERTICAL)
                constraintSet.setDimensionRatio(binding.adMedia.id, ratio)
        }

        fun setEmptyView(isEmpty: Boolean) {
            if (isEmpty)
                constraintSet.setVisibility(binding.adEmpty.id, ConstraintSet.VISIBLE)
            else constraintSet.setVisibility(binding.adEmpty.id, ConstraintSet.INVISIBLE)
        }

        fun populateNativeAdView(
            nativeAd: UnifiedNativeAd,
            adView: UnifiedNativeAdView
        ) {
            // Some assets are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body
            (adView.callToActionView as Button).text = nativeAd.callToAction

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            val icon = nativeAd.icon
            if (icon == null) {
                constraintSet.setVisibility(adView.iconView.id, ConstraintSet.GONE)
            } else {
                constraintSet.setVisibility(adView.iconView.id, ConstraintSet.VISIBLE)
                (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            }
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
        disposable = compositeAdLoader.getLoader(item.adTag, adapterPosition, item.customTag)
            .adsObservable
            .subscribe { adBox ->
                constraintSet.clone(binding.adContainer)
                adBox.ad?.let {
                    setEmptyView(false)
                    if (it.mediaContent != null) {
                        constraintSet.setVisibility(binding.adView.mediaView.id, ConstraintSet.VISIBLE)
                        setDimensionRatio(it.mediaContent.aspectRatio.toString())
                    } else constraintSet.setVisibility(binding.adView.mediaView.id, ConstraintSet.GONE)
                    populateNativeAdView(it, binding.adView)
                } ?: kotlin.run {
                    setEmptyView(true)
                }
                TransitionManager.beginDelayedTransition(binding.adContainer)
                constraintSet.applyTo(binding.adContainer)
            }
    }

    onViewRecycled { disposable?.dispose() }
}