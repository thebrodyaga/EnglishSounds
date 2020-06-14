package com.thebrodyaga.englishsounds.screen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.ContrastingSoundVideoItem
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItem
import kotlinx.android.synthetic.main.item_youtube_video.view.*
import kotlinx.android.synthetic.main.view_youtube_thumbnail.view.*

class VideoListAdapter constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = listOf<VideoItem>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return YoutubeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_youtube_video,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as YoutubeViewHolder).bind(list[position])
    }

    fun setData(newData: List<VideoItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newData.toList()))
        list = newData
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class YoutubeViewHolder constructor(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        var item: VideoItem? = null

        init {
            itemView.setOnClickListener { }
            itemView.item_youtube_video_thumbnail.youtube_play_icon.isVisible = false
        }

        fun bind(item: VideoItem) = with(itemView) {
            this@YoutubeViewHolder.item = item
            item_youtube_video_thumbnail.loadYoutubeThumbnail(item.videoId)
            item_youtube_video_title.text = item.title
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
}