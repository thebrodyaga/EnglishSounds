package com.thebrodyaga.englishsounds.screen.adapters.delegates

import androidx.constraintlayout.widget.ConstraintSet
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AdBox
import com.thebrodyaga.englishsounds.utils.populateNativeAdView
import kotlinx.android.synthetic.main.itme_ad_sounds_list.*

fun videoNativeAdDelegate() = adapterDelegateLayoutContainer<AdBox, Any>(R.layout.itme_ad_sounds_list) {

    val constraintSet = ConstraintSet()
    ad_view.apply {
        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        mediaView = ad_media

        // Register the view used for each individual asset.
        headlineView = ad_headline
        bodyView = ad_body
        callToActionView = ad_call_to_action
        iconView = ad_icon
        priceView = ad_price
        starRatingView = ad_stars
        storeView = ad_google_play_icon
        advertiserView = ad_advertiser
    }

    bind {
        fun setDimensionRatio(ratio: String) {
            constraintSet.apply {
                clone(ad_container)
                setDimensionRatio(ad_media.id, ratio)
                applyTo(ad_container)
            }
        }

        item.ad?.let {
            setDimensionRatio(if (it.mediaContent != null) it.mediaContent.aspectRatio.toString() else "16:9")
            populateNativeAdView(it, ad_view)
        } ?: setDimensionRatio("16:9")
    }
}