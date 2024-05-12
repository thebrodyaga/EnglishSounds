package com.thebrodyaga.feature.videoList.impl.carousel

import android.os.Parcelable
import android.view.View
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.lang.ref.WeakReference

internal val VIDEO_CAROUSEL_LAYOUT_ID = R.layout.item_video_carousel
internal val VIDEO_CAROUSEL_VIEW_TYPE = VIDEO_CAROUSEL_LAYOUT_ID

fun interface CarouselItemBindListener {
    fun onBind(binding: ItemVideoCarouselItemBinding, item: VideoCarouselItemUiModel)
}

fun videoCarouselDelegate(
    pool: RecyclerView.RecycledViewPool,
    carouselItemBindListener: (CarouselItemBindListener)? = null,
): DslRowAdapterDelegate<VideoCarouselUiModel, View> =
    rowDelegate(VIDEO_CAROUSEL_LAYOUT_ID, VIDEO_CAROUSEL_VIEW_TYPE) {

        val carouselRow = videoCarouselItemDelegate(carouselItemBindListener)
        val scrollStates = hashMapOf<String, Parcelable?>()

        onViewRecycled {
            val rv = it.view as RecyclerView
            scrollStates[it.item.id] = rv.layoutManager?.onSaveInstanceState()
        }

        onInflate {
            val binding = ItemVideoCarouselBinding.bind(it)
            val layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)

            binding.itemVideoCarouselRecycler.layoutManager = layoutManager
            val adapter = CommonAdapter {
                row(carouselRow)
            }
            binding.itemVideoCarouselRecycler.adapter = adapter
//            binding.itemVideoCarouselRecycler.setRecycledViewPool(pool)
//            binding.itemVideoCarouselRecycler.swapAdapter(adapter, true)
        }

        onBind { holder, _ ->
            val rv = holder.view as RecyclerView
            (rv.adapter as CommonAdapter).setItems(holder.item.list) {
                scrollStates[holder.item.id]?.let { rv.layoutManager?.onRestoreInstanceState(it) }
            }
        }
    }


data class VideoCarouselUiModel(
    val id: String,
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
    bindListener: (CarouselItemBindListener)? = null,
): DslRowAdapterDelegate<VideoCarouselItemUiModel, View> =
    rowDelegate(VIDEO_CAROUSEL_ITEM_LAYOUT_ID, VIDEO_CAROUSEL_ITEM_VIEW_TYPE) {

        val weakBindListener = WeakReference(bindListener)

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

            weakBindListener.get()?.onBind(binding, item)
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