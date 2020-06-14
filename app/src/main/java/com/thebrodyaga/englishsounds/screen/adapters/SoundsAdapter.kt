package com.thebrodyaga.englishsounds.screen.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Parcelable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundHeader
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundsListItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoListItem
import com.thebrodyaga.englishsounds.screen.adapters.delegates.soundHeaderItemDelegate
import com.thebrodyaga.englishsounds.screen.adapters.delegates.soundItemDelegate
import com.thebrodyaga.englishsounds.screen.adapters.delegates.videoListItemDelegate

class SoundsAdapter constructor(
    onCardSoundClick: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback()) {

    private val positionList = mutableMapOf<Int, Parcelable?>()

    init {
        delegatesManager.addDelegate(soundHeaderItemDelegate())
        delegatesManager.addDelegate(videoListItemDelegate(positionList, {}))
        delegatesManager.addDelegate(
            soundItemDelegate(
                onCardSoundClick,
                { context, colorRes -> getColor(context, colorRes) },
                { singleCharMaxWight })
        )
    }

    fun setData(newData: List<SoundsListItem>) {
        items = newData
    }

    private val soundsBackgroundColors = mutableMapOf<@ColorRes Int, ColorStateList>()

    private fun getColor(context: Context, @ColorRes colorRes: Int): ColorStateList? {
        return soundsBackgroundColors[colorRes] ?: ContextCompat.getColorStateList(
            context,
            colorRes
        )?.also { soundsBackgroundColors[colorRes] = it }
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
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is AmericanSoundDto && newItem is AmericanSoundDto -> oldItem == newItem
                oldItem is SoundHeader && newItem is SoundHeader -> oldItem == newItem
                oldItem is VideoListItem && newItem is VideoListItem -> oldItem == newItem
                else -> false
            }
        }

    }
}