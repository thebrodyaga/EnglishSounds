package com.thebrodyaga.feature.videoList.impl.carousel

import android.util.SparseIntArray
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.component.sound.mini.SoundCardMiniUiModel
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
    val firstSound: SoundCardMiniUiModel? = null,
    val secondSound: SoundCardMiniUiModel? = null,
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
            val alphaPercent = 0.4f + (0.6f * visiblePercent)
            itemView.alpha = alphaPercent
            binding.carouselItemText.translationX = maskRect.left
            binding.carouselItemFirstSound.translationX = maskRect.left
            binding.carouselItemSecondSound.translationX = maskRect.left
        }
        binding.carouselItemVideoView.loadYoutubeThumbnail(item.videoId)

        val firstSound = item.firstSound
        binding.carouselItemFirstSound.isInvisible = firstSound == null
        firstSound?.let { binding.carouselItemFirstSound.bind(firstSound) }

        val secondSound = item.secondSound
        binding.carouselItemSecondSound.isInvisible = secondSound == null
        secondSound?.let { binding.carouselItemSecondSound.bind(secondSound) }

        binding.carouselItemText.text = item.title
    }
}