package com.thebrodyaga.englishsounds.screen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItem
import com.thebrodyaga.englishsounds.domine.entities.ui.YoutubeVideoItem

class VideoListAdapter constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = listOf<VideoItem>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            YOUTUBE_VIEW -> YoutubeViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_sound,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("хуйня в onCreateViewHolder")
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {
            is YoutubeVideoItem -> (holder as YoutubeViewHolder).bind(item)
            else -> throw IllegalArgumentException("хуйня в onBindViewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is YoutubeVideoItem -> YOUTUBE_VIEW
            else -> throw IllegalArgumentException("хуйня в getItemViewType")
        }
    }

    fun setData(newData: List<VideoItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newData.toList()))
        list = newData
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class YoutubeViewHolder constructor(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        var item: YoutubeVideoItem? = null

        fun bind(item: YoutubeVideoItem) = with(itemView) {
            this@YoutubeViewHolder.item = item
        }
    }

    class DiffCallback constructor(
        private var oldList: List<VideoItem>,
        private var newList: List<VideoItem>
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
                oldItem is YoutubeVideoItem && newItem is YoutubeVideoItem ->
                    oldItem.videoId == newItem.videoId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is YoutubeVideoItem && newItem is YoutubeVideoItem ->
                    oldItem == newItem
                else -> false
            }
        }
    }

    companion object {
        const val YOUTUBE_VIEW = 0
    }
}