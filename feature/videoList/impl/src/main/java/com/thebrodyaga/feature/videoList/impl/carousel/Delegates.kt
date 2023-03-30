package com.thebrodyaga.feature.videoList.impl.carousel

import android.util.SparseIntArray
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.feature.videoList.impl.databinding.ItemVideoCarouselBinding
import com.thebrodyaga.feature.videoList.impl.databinding.ItemVideoCarouselItemBinding
import kotlin.math.absoluteValue

fun videoCarouselDelegate(
    inflateListener: ((view: RecyclerView) -> Unit)? = null,
    bindListener: ((view: RecyclerView, item: VideoCarouselUiModel) -> Unit)? = null,
    listPosition: SparseIntArray,
    shadowRecyclerHeight: () -> Int,
) = adapterDelegateViewBinding<VideoCarouselUiModel, UiModel, ItemVideoCarouselBinding>(
    { layoutInflater, root -> ItemVideoCarouselBinding.inflate(layoutInflater, root, false) }
) {

    val layoutManager = CarouselLayoutManager()
    val adapter = CommonAdapter(
        delegates = listOf(videoCarouselItemDelegate())
    )
    binding.itemVideoCarouselRecycler.updateLayoutParams { height = shadowRecyclerHeight() }
    binding.itemVideoCarouselRecycler.layoutManager = layoutManager
    binding.itemVideoCarouselRecycler.adapter = adapter

    bind {
        adapter.items = item.list
    }
}


data class VideoCarouselUiModel(
    val list: List<VideoCarouselItemUiModel>
) : UiModel

data class VideoCarouselItemUiModel(
    val videoId: String,
    val title: String,
) : UiModel

fun videoCarouselItemDelegate(
    inflateListener: ((view: RecyclerView) -> Unit)? = null,
    bindListener: ((view: RecyclerView, item: VideoCarouselItemUiModel) -> Unit)? = null,
) = adapterDelegateViewBinding<VideoCarouselItemUiModel, UiModel, ItemVideoCarouselItemBinding>(
    { layoutInflater, root -> ItemVideoCarouselItemBinding.inflate(layoutInflater, root, false) }
) {

    bind {
        binding.root.setOnMaskChangedListener { maskRect ->
            val visiblePercent = ((maskRect.right - maskRect.left) / itemView.width).absoluteValue
            val alphaPercent = 0.5f + (0.5f * visiblePercent)
            itemView.alpha = alphaPercent
            binding.carouselItemText.translationX = maskRect.left
        }
        binding.carouselItemVideoView.loadYoutubeThumbnail(item.videoId)
        binding.carouselItemText.text = item.title
    }
}