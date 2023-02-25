package com.thebrodyaga.legacy.delegates

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.legacy.R
import com.thebrodyaga.legacy.SoundHeader
import com.thebrodyaga.legacy.VideoListItem
import com.thebrodyaga.legacy.adapters.VideoListAdapter
import com.thebrodyaga.legacy.adapters.decorator.OffsetItemDecoration
import com.thebrodyaga.legacy.databinding.ItemSoundHeaderBinding
import com.thebrodyaga.legacy.databinding.ItemVideoListBinding
import com.thebrodyaga.legacy.humanName
import com.thebrodyaga.legacy.utils.CompositeAdLoader
import kotlinx.android.synthetic.main.item_sound_header.*
import kotlinx.android.synthetic.main.item_video_list.*

fun videoListItemDelegate(
    positionList: MutableMap<Int, Pair<Int, Int>>,
    onSoundClick: (transcription: String) -> Unit,
    onShowAllClick: (videoListItem: VideoListItem) -> Unit,
    lifecycle: Lifecycle,
    compositeAdLoader: CompositeAdLoader,
    youtubeScreenFactory: YoutubeScreenFactory,
    router: Router,
) = adapterDelegateViewBinding<VideoListItem, Any, ItemVideoListBinding>(
    { layoutInflater, root -> ItemVideoListBinding.inflate(layoutInflater, root, false) }
) {

    val adapter = VideoListAdapter(
        onSoundClick, compositeAdLoader,
        youtubeScreenFactory = youtubeScreenFactory, router = router
    )
    val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

    val lifecycleObserver = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun saveState() {
            val positionIndex = layoutManager.findFirstVisibleItemPosition()
            val startView = binding.videoListRecyclerView.getChildAt(0)
            val topView =
                if (startView == null) 0 else startView.left - binding.videoListRecyclerView.paddingLeft
            positionList[adapterPosition] = Pair(positionIndex, topView)
        }
    }

    binding.videoListShowAll.setOnClickListener { onShowAllClick(item) }

    binding.videoListRecyclerView.apply {
        lifecycle.addObserver(lifecycleObserver)
        this.layoutManager = layoutManager
        this.adapter = adapter
        this.addItemDecoration(
            OffsetItemDecoration(
                context,
                RecyclerView.HORIZONTAL,
                R.dimen.base_offset_small
            )
        )
    }

    onViewRecycled {
        lifecycleObserver.saveState()
    }

    bind {
        adapter.setData(item.list)
        // Retrieve and set the saved position
        positionList[adapterPosition]
            ?.let { layoutManager.scrollToPositionWithOffset(it.first, it.second) }
        binding.videoListTitle.setText(item.title)
    }
}

fun soundHeaderItemDelegate() =
    adapterDelegateViewBinding<SoundHeader, Any, ItemSoundHeaderBinding>(
        { layoutInflater, root -> ItemSoundHeaderBinding.inflate(layoutInflater, root, false) }
    ) {

        bind {
            binding.headerTitle.setText(item.soundType.humanName())
        }
    }