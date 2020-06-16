package com.thebrodyaga.englishsounds.screen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.ui.PlayVideoExtra
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoItem
import com.thebrodyaga.englishsounds.youtube.YoutubePlayerActivity
import kotlinx.android.synthetic.main.item_youtube_video.view.*
import kotlinx.android.synthetic.main.view_youtube_thumbnail.view.*


class VideoListAdapter constructor(
    @RecyclerView.Orientation val orientation: Int = RecyclerView.HORIZONTAL
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = listOf<VideoItem>()
        private set
    private var constraintSet = ConstraintSet()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_youtube_video, parent, false) as ConstraintLayout
        if (orientation == RecyclerView.VERTICAL) {
            itemView.layoutParams = itemView.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            constraintSet.clone(itemView.root_view)
            constraintSet.constrainHeight(
                itemView.item_youtube_video_thumbnail.id,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            constraintSet.applyTo(itemView.root_view)
        }
        return YoutubeViewHolder(itemView)
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

    private inner class YoutubeViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        var item: VideoItem? = null

        init {
            itemView.setOnClickListener { v ->
                item?.let {
                    YoutubePlayerActivity.startActivity(
                        v.context,
                        PlayVideoExtra(it.videoId, it.title)
                    )
                }
            }
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