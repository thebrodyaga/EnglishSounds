package com.thebrodyaga.englishsounds.screen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.ContrastingSoundVideoItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItem
import kotlinx.android.synthetic.main.item_youtube_video.view.*

class VideoListAdapter constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = listOf<VideoItem>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            YOUTUBE_VIEW -> YoutubeViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_youtube_video,
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
            is ContrastingSoundVideoItem -> (holder as YoutubeViewHolder).bind(item)
            else -> throw IllegalArgumentException("хуйня в onBindViewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is ContrastingSoundVideoItem -> YOUTUBE_VIEW
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

        var item: ContrastingSoundVideoItem? = null

        fun bind(item: ContrastingSoundVideoItem) = with(itemView) {
            this@YoutubeViewHolder.item = item
            item_youtube_video_thumbnail.loadYoutubeThumbnail(item.videoId)
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
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }

    companion object {
        const val YOUTUBE_VIEW = 0
    }
}