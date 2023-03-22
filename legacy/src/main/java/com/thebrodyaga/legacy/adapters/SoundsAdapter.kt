package com.thebrodyaga.legacy.adapters

import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import com.google.android.gms.ads.formats.NativeAdOptions
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.core.navigation.api.cicerone.CiceroneRouter
import com.thebrodyaga.data.sounds.api.model.AmericanSoundDto
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.AdItem
import com.thebrodyaga.legacy.R
import com.thebrodyaga.legacy.ShortAdItem
import com.thebrodyaga.legacy.SoundHeader
import com.thebrodyaga.legacy.VideoListItem
import com.thebrodyaga.legacy.delegates.ConsonantSoundsDelegate
import com.thebrodyaga.legacy.delegates.RControlledSoundsDelegate
import com.thebrodyaga.legacy.delegates.VowelSoundsDelegate
import com.thebrodyaga.legacy.delegates.soundHeaderItemDelegate
import com.thebrodyaga.legacy.delegates.videoListItemDelegate
import com.thebrodyaga.legacy.delegates.videoNativeAdDelegate
import com.thebrodyaga.legacy.delegates.videoNativeShortAdDelegate
import com.thebrodyaga.legacy.utils.CompositeAdLoader

class SoundsAdapter constructor(
    positionList: MutableMap<Int, Pair<Int, Int>>,
    onCardSoundClick: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    onSoundClick: (transcription: String) -> Unit,
    onShowAllClick: (videoListItem: VideoListItem) -> Unit,
    lifecycle: Lifecycle,
    context: Context,
    youtubeScreenFactory: YoutubeScreenFactory,
    router: CiceroneRouter,
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback()) {


    init {
        val compositeAdLoader = CompositeAdLoader(context, lifecycle)
        delegatesManager.addDelegate(
            videoNativeAdDelegate(
                compositeAdLoader,
                RecyclerView.VERTICAL
            )
        )
        delegatesManager.addDelegate(
            videoNativeShortAdDelegate(
                CompositeAdLoader(
                    context,
                    lifecycle,
                    NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE
                )
            )
        )
        delegatesManager.addDelegate(soundHeaderItemDelegate())
        delegatesManager.addDelegate(
            videoListItemDelegate(
                positionList,
                onSoundClick,
                onShowAllClick,
                lifecycle,
                compositeAdLoader,
                youtubeScreenFactory,
                router,
            )
        )
        delegatesManager.addDelegate(
            ConsonantSoundsDelegate(onCardSoundClick,
                { singleCharMaxWight })
        )
        delegatesManager.addDelegate(
            RControlledSoundsDelegate(onCardSoundClick,
                { singleCharMaxWight })
        )
        delegatesManager.addDelegate(
            VowelSoundsDelegate(onCardSoundClick,
                { singleCharMaxWight })
        )
    }

    fun setData(newData: List<Any>) {
        items = newData
    }

    private var singleCharMaxWight: Float? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val mPaint: Paint = Paint()
        var font: Typeface? = null
        fun font(context: Context): Typeface {
            return font ?: ResourcesCompat.getFont(context, R.font.font_regular).also { font = it }
            ?: throw Resources.NotFoundException()

        }
        mPaint.isAntiAlias = true
        mPaint.textSize = recyclerView.context.resources.getDimension(R.dimen.bodySize)
        mPaint.typeface = font(recyclerView.context)
        singleCharMaxWight = mPaint.measureText("a")
    }

    private class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is AmericanSoundDto && newItem is AmericanSoundDto -> oldItem.transcription == newItem.transcription
                oldItem is SoundHeader && newItem is SoundHeader -> oldItem.soundType == newItem.soundType
                oldItem is VideoListItem && newItem is VideoListItem -> oldItem.title == newItem.title
                oldItem is AdItem && newItem is AdItem -> oldItem == newItem
                oldItem is ShortAdItem && newItem is ShortAdItem -> oldItem == newItem
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is AmericanSoundDto && newItem is AmericanSoundDto -> oldItem == newItem
                oldItem is SoundHeader && newItem is SoundHeader -> oldItem == newItem
                oldItem is VideoListItem && newItem is VideoListItem -> oldItem == newItem
                oldItem is AdItem && newItem is AdItem -> oldItem == newItem
                oldItem is ShortAdItem && newItem is ShortAdItem -> oldItem == newItem
                else -> false
            }
        }

    }
}