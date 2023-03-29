package com.thebrodyaga.feature.videoList.impl.carousel

import android.util.SparseIntArray
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.feature.videoList.impl.databinding.ItemVideoCarouselBinding
import com.thebrodyaga.feature.videoList.impl.databinding.ItemVideoCarouselItemBinding

fun videoCarouselDelegate(
    inflateListener: ((view: RecyclerView) -> Unit)? = null,
    bindListener: ((view: RecyclerView, item: VideoCarouselUiModel) -> Unit)? = null,
    listPosition: SparseIntArray,
) = adapterDelegateViewBinding<VideoCarouselUiModel, UiModel, ItemVideoCarouselBinding>(
    { layoutInflater, root -> ItemVideoCarouselBinding.inflate(layoutInflater, root, false) }
) {

    val layoutManager = CarouselLayoutManager()
    val adapter = CommonAdapter(
        delegates = listOf(videoCarouselItemDelegate())
    )
    binding.itemVideoCarouselRecycler.layoutManager = layoutManager
    binding.itemVideoCarouselRecycler.adapter = adapter

    onViewRecycled {
        // Store position
//        val position: Int = bindingAdapterPosition
//        val firstVisiblePosition: Int = binding.itemVideoCarouselRecycler.computeHorizontalScrollRange()
//        listPosition.put(position, firstVisiblePosition)
    }

    bind {
        adapter.items = item.list
        val lastSeenFirstPosition: Int = listPosition.get(bindingAdapterPosition, 0)
//        if (lastSeenFirstPosition >= 0) {
//            layoutManager.scrollHorizontallyBy()
//            binding.itemVideoCarouselRecycler.scrollTo()
//            layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, 0)
//        }
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
            binding.carouselItemText.translationX = maskRect.left
        }
        binding.carouselItemVideoView.loadYoutubeThumbnail(item.videoId)
        binding.carouselItemText.text = item.title
    }
}