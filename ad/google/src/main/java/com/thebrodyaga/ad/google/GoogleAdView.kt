package com.thebrodyaga.ad.google

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.ad.api.GoogleAdUiModel
import com.thebrodyaga.ad.google.GoogleAdView.Companion.GOOGLE_AD_LAYOUT
import com.thebrodyaga.ad.google.GoogleAdView.Companion.GOOGLE_AD_SMALL_VIEW_TYPE
import com.thebrodyaga.brandbook.component.icon.IconWrapperUiModel
import com.thebrodyaga.brandbook.recycler.dsl.rowDelegate
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageViewUiModel
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.shape.shapeRoundedAll
import com.thebrodyaga.englishsounds.ad.google.R
import com.thebrodyaga.englishsounds.ad.google.databinding.ViewGoogleAdBinding

class GoogleAdView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : MaterialCardView(context, attrs, R.attr.materialCardViewElevatedStyle) {

    companion object {
        val GOOGLE_AD_LAYOUT = R.layout.item_google_ad
        val GOOGLE_AD_SMALL_VIEW_TYPE = GOOGLE_AD_LAYOUT - 1

    }

    private val binding by viewBinding(ViewGoogleAdBinding::bind)

    init {
        inflate(context, R.layout.view_google_ad, this)

        val nativeAdView = binding.adBigAdView

        // Set the media view.
//        nativeAdView.mediaView = binding.adBigMedia

        // Set other ad assets.
        nativeAdView.headlineView = binding.adBigTitle
        nativeAdView.bodyView = binding.adBigText
        nativeAdView.callToActionView = binding.adBigAction
        nativeAdView.iconView = binding.adBigImg
        nativeAdView.priceView = binding.adBigPrice
        nativeAdView.starRatingView = binding.adBigRating
    }

    fun populate(model: GoogleAdUiModel) {
        val nativeAd = model.ad

        if (model.isWithMedia) showMedia() else hideMedia()

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        binding.adBigTitle.text = nativeAd.headline
//        nativeAd.mediaContent?.let { binding.adBigMedia.setImageDrawable(nativeAd.images.firstOrNull()?.drawable) }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            binding.adBigText.visibility = View.GONE
        } else {
            binding.adBigText.visibility = View.VISIBLE
            binding.adBigText.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            binding.adBigAction.visibility = View.GONE
        } else {
            binding.adBigAction.visibility = View.VISIBLE
            binding.adBigAction.text = nativeAd.callToAction
        }

        val drawable = nativeAd.icon?.drawable
        if (drawable == null) {
            binding.adBigImg.visibility = View.GONE
        } else {
            val iconModel = IconWrapperUiModel.SingleIcon(
                icon = ImageViewUiModel(
                    icon = IconContainer.Draw(drawable),
                    scaleType = ImageView.ScaleType.FIT_CENTER,
                    clippingShape = shapeRoundedAll(16f.px),
                )
            )
            binding.adBigImg.bind(iconModel)
            binding.adBigImg.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            binding.adBigPrice.visibility = View.GONE
        } else {
            binding.adBigPrice.visibility = View.VISIBLE
            binding.adBigPrice.text = nativeAd.price
        }

        if (nativeAd.starRating == null) {
            binding.adBigRating.visibility = View.GONE
        } else {
            binding.adBigRating.rating = nativeAd.starRating!!.toFloat()
            binding.adBigRating.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        binding.adBigAdView.setNativeAd(nativeAd)

    }

    private fun hideMedia() {
        binding.adBigMedia.isVisible = false
    }

    private fun showMedia() {
        binding.adBigMedia.isVisible = true
    }
}

fun googleAdDelegate() = rowDelegate<GoogleAdUiModel, GoogleAdView>(
    GOOGLE_AD_LAYOUT, GOOGLE_AD_SMALL_VIEW_TYPE, block = {

        onBind { holder, _ ->
            holder.view.populate(holder.item)
        }
    }
)