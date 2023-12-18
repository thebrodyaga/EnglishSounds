package com.thebrodyaga.ad.api

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.doOnLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.ad.api.AdLoadingView.Companion.AD_LOADING_LAYOUT
import com.thebrodyaga.ad.api.AdLoadingView.Companion.AD_LOADING_SMALL_VIEW_TYPE
import com.thebrodyaga.brandbook.recycler.dsl.rowDelegate
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.skeleton.SkeletonUiModel
import com.thebrodyaga.core.uiUtils.skeleton.bindSkeleton
import com.thebrodyaga.englishsounds.ad.api.R
import com.thebrodyaga.englishsounds.ad.api.databinding.ViewLoadingAdBinding

class AdLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : MaterialCardView(context, attrs, R.attr.materialCardViewElevatedStyle) {

    companion object {
        val AD_LOADING_LAYOUT = R.layout.item_ad_loading
        val AD_LOADING_BIG_VIEW_TYPE = AD_LOADING_LAYOUT + 1
        val AD_LOADING_SMALL_VIEW_TYPE = AD_LOADING_LAYOUT - 1

    }

    private val binding by viewBinding(ViewLoadingAdBinding::bind)

    init {
        inflate(context, R.layout.view_loading_ad, this)
    }

    fun smallLoadingAd() = doOnLayout {
        val roundedRadius = 16f.px
        binding.adLoadingTitle.apply {
            bindSkeleton(SkeletonUiModel(height, width, roundedRadius))
        }
        binding.adLoadingText.apply {
            bindSkeleton(SkeletonUiModel(height, width, roundedRadius))
        }
        binding.adLoadingImg.apply {
            bindSkeleton(SkeletonUiModel(height, width, roundedRadius))
        }
        binding.adLoadingAction.apply {
            bindSkeleton(SkeletonUiModel(height, width, roundedRadius))
        }
        binding.adLoadingRating.apply {
            bindSkeleton(SkeletonUiModel(height, width, roundedRadius))
        }
    }
}



fun adSmallLoadingDelegate() = rowDelegate<AdLoadingSmallUiModel, AdLoadingView>(
    AD_LOADING_LAYOUT, AD_LOADING_SMALL_VIEW_TYPE, block = {

        onInflate {
            it.smallLoadingAd()
        }
    }
)