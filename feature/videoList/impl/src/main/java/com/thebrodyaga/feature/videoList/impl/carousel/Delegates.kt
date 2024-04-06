package com.thebrodyaga.feature.videoList.impl.carousel

import android.view.View
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.component.sound.mini.SoundCardMiniUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.brandbook.recycler.dsl.DslRowAdapterDelegate
import com.thebrodyaga.brandbook.recycler.dsl.rowDelegate
import com.thebrodyaga.feature.videoList.impl.R
import com.thebrodyaga.feature.videoList.impl.databinding.ItemVideoCarouselBinding
import com.thebrodyaga.feature.videoList.impl.databinding.ItemVideoCarouselItemBinding
import com.thebrodyaga.feature.videoList.impl.databinding.ItemYoutubeVideoBinding
import kotlin.math.absoluteValue

internal val VIDEO_CAROUSEL_LAYOUT_ID = R.layout.item_video_carousel
internal val VIDEO_CAROUSEL_VIEW_TYPE = VIDEO_CAROUSEL_LAYOUT_ID

fun videoCarouselDelegate(
    pool: VideoCarouselViewPool,
    carouselItemBindListener: ((binding: ItemVideoCarouselItemBinding, item: VideoCarouselItemUiModel) -> Unit)? = null,
): DslRowAdapterDelegate<VideoCarouselUiModel, View> =
    rowDelegate(VIDEO_CAROUSEL_LAYOUT_ID, VIDEO_CAROUSEL_VIEW_TYPE) {

        onInflate {
            val binding = ItemVideoCarouselBinding.bind(it)
            val layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = CommonAdapter {
                row(videoCarouselItemDelegate(carouselItemBindListener))
            }
            binding.itemVideoCarouselRecycler.layoutManager = layoutManager
            binding.itemVideoCarouselRecycler.setRecycledViewPool(pool)
            binding.itemVideoCarouselRecycler.swapAdapter(adapter, true)
        }

        onBind { holder, _ ->
            val item = holder.item
            val binding = ItemVideoCarouselBinding.bind(holder.view)
            (binding.itemVideoCarouselRecycler.adapter as CommonAdapter).items = item.list
        }
    }


data class VideoCarouselUiModel(
    val list: List<VideoCarouselItemUiModel>,
) : UiModel

data class VideoCarouselItemUiModel(
    val videoId: String,
    val title: String,
    val firstSound: SoundCardMiniUiModel? = null,
    val secondSound: SoundCardMiniUiModel? = null,
) : UiModel

internal val VIDEO_CAROUSEL_ITEM_LAYOUT_ID = R.layout.item_video_carousel_item
internal val VIDEO_CAROUSEL_ITEM_VIEW_TYPE = VIDEO_CAROUSEL_ITEM_LAYOUT_ID

fun videoCarouselItemDelegate(
    bindListener: ((binding: ItemVideoCarouselItemBinding, item: VideoCarouselItemUiModel) -> Unit)? = null,
): DslRowAdapterDelegate<VideoCarouselItemUiModel, View> =
    rowDelegate(VIDEO_CAROUSEL_ITEM_LAYOUT_ID, VIDEO_CAROUSEL_ITEM_VIEW_TYPE) {

        onBind { holder, _ ->
            val item = holder.item
            val itemView = holder.view
            val binding = ItemVideoCarouselItemBinding.bind(holder.view)
            /*binding.root.setOnMaskChangedListener { maskRect ->
                val visiblePercent = ((maskRect.right - maskRect.left) / itemView.width).absoluteValue
                val alphaPercent = 0.4f + (0.6f * visiblePercent)
                itemView.alpha = alphaPercent
                binding.carouselItemText.translationX = maskRect.left
                binding.carouselItemFirstSound.translationX = maskRect.left
                binding.carouselItemSecondSound.translationX = maskRect.left
            }*/
            binding.carouselItemVideoView.loadYoutubeThumbnail(item.videoId)

            val firstSound = item.firstSound
            binding.carouselItemFirstSound.isInvisible = firstSound == null
            firstSound?.let { binding.carouselItemFirstSound.bind(firstSound) }

            val secondSound = item.secondSound
            binding.carouselItemSecondSound.isInvisible = secondSound == null
            secondSound?.let { binding.carouselItemSecondSound.bind(secondSound) }

            binding.carouselItemText.text = item.title

            bindListener?.invoke(binding, item)
        }
    }

fun videoItemDelegate(
    onBind: ((binding: ItemYoutubeVideoBinding, item: VideoCarouselItemUiModel) -> Unit)? = null,
) = adapterDelegateViewBinding<VideoCarouselItemUiModel, UiModel, ItemYoutubeVideoBinding>(
    viewBinding = { inflater, parent -> ItemYoutubeVideoBinding.inflate(inflater, parent, false) }) {

    bind {

        binding.youtubeVideoItemVideoView.loadYoutubeThumbnail(item.videoId)

        val leftSound = item.secondSound
        binding.youtubeVideoItemFirstSound.isInvisible = leftSound == null
        leftSound?.let { binding.youtubeVideoItemFirstSound.bind(leftSound) }

        val rightSound = item.firstSound
        binding.youtubeVideoItemSecondSound.isInvisible = rightSound == null
        rightSound?.let { binding.youtubeVideoItemSecondSound.bind(rightSound) }

        binding.youtubeVideoItemTitle.text = item.title
        onBind?.invoke(binding, item)
    }
}