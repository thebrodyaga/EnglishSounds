package com.thebrodyaga.englishsounds.screen.adapters

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter
import com.thebrodyaga.englishsounds.domine.entities.ui.AdItem
import com.thebrodyaga.englishsounds.domine.entities.ui.ShortAdItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItemInList
import com.thebrodyaga.englishsounds.screen.adapters.delegates.videoItemDelegate
import com.thebrodyaga.englishsounds.screen.adapters.delegates.videoNativeAdDelegate
import com.thebrodyaga.englishsounds.screen.adapters.utils.SoundItemViewCache
import com.thebrodyaga.englishsounds.utils.CompositeAdLoader

class VideoListAdapter constructor(
    onSoundClick: (transcription: String) -> Unit,
    compositeAdLoader: CompositeAdLoader,
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL
) : AbsDelegationAdapter<List<Any>>() {

    private var viewCache: SoundItemViewCache? = null

    init {
        items = listOf()
        delegatesManager.addDelegate(
            videoItemDelegate(
                orientation, { viewCache },
                { context, colorRes -> getColor(context, colorRes) },
                onSoundClick
            )
        )
        delegatesManager.addDelegate(
            videoNativeAdDelegate(
                compositeAdLoader,
                orientation
            )
        )
    }

    fun setData(newData: List<VideoItemInList>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(items, newData.toList()))
        items = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = items.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        viewCache = SoundItemViewCache(recyclerView.context)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        viewCache = null
    }

    private val soundsBackgroundColors = mutableMapOf<@ColorRes Int, ColorStateList>()

    private fun getColor(context: Context, @ColorRes colorRes: Int): ColorStateList? {
        return soundsBackgroundColors[colorRes] ?: ContextCompat.getColorStateList(
            context,
            colorRes
        )?.also { soundsBackgroundColors[colorRes] = it }
    }

    class DiffCallback constructor(
        private var oldList: List<Any>,
        private var newList: List<Any>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is VideoItem && newItem is VideoItem -> oldItem.videoId == newItem.videoId
                oldItem is AdItem && newItem is AdItem -> oldItem == newItem
                oldItem is ShortAdItem && newItem is ShortAdItem -> oldItem == newItem
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is VideoItem && newItem is VideoItem -> oldItem == newItem
                oldItem is AdItem && newItem is AdItem -> oldItem == newItem
                oldItem is ShortAdItem && newItem is ShortAdItem -> oldItem == newItem
                else -> false
            }
        }
    }
}