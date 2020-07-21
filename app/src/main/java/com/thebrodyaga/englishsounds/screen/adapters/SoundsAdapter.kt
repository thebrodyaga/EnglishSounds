package com.thebrodyaga.englishsounds.screen.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AdBox
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundHeader
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoListItem
import com.thebrodyaga.englishsounds.screen.adapters.delegates.*
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader
import com.thebrodyaga.englishsounds.utils.NativeAdLoader

class SoundsAdapter constructor(
    positionList: MutableMap<Int, Pair<Int, Int>>,
    onCardSoundClick: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    onSoundClick: (transcription: String) -> Unit,
    onShowAllClick: (videoListItem: VideoListItem) -> Unit,
    lifecycle: Lifecycle,
    context: Context
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
                compositeAdLoader
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

    fun setData(newData: List<SoundsListItem>) {
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
                oldItem is AdBox && newItem is AdBox -> oldItem.ad == newItem.ad
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is AmericanSoundDto && newItem is AmericanSoundDto -> oldItem == newItem
                oldItem is SoundHeader && newItem is SoundHeader -> oldItem == newItem
                oldItem is VideoListItem && newItem is VideoListItem -> oldItem == newItem
                oldItem is AdBox && newItem is AdBox -> oldItem.ad == newItem.ad
                else -> false
            }
        }

    }
}