package com.thebrodyaga.englishsounds.screen.adapters.delegates

import android.content.Context
import android.content.res.ColorStateList
import android.os.Parcelable
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.domine.entities.data.AmericanSoundDto
import com.thebrodyaga.englishsounds.domine.entities.ui.SoundHeader
import com.thebrodyaga.englishsounds.domine.entities.ui.VideoListItem
import com.thebrodyaga.englishsounds.screen.adapters.VideoListAdapter
import com.thebrodyaga.englishsounds.screen.adapters.decorator.OffsetItemDecoration
import kotlinx.android.synthetic.main.item_sound.*
import kotlinx.android.synthetic.main.item_sound_header.*
import kotlinx.android.synthetic.main.item_video_list.*

fun videoListItemDelegate(
    positionList: MutableMap<Int, Parcelable?>,
    itemClickedListener: (VideoListItem) -> Unit
) = adapterDelegateLayoutContainer<VideoListItem, Any>(R.layout.item_video_list) {

    val adapter = VideoListAdapter()
    val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

    video_list_recycler_view.apply {
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
        positionList[adapterPosition] = layoutManager.onSaveInstanceState()
    }

    bind {
        adapter.setData(item.list)
        // Retrieve and set the saved position
        positionList[adapterPosition]
            ?.let { layoutManager.onRestoreInstanceState(it) }
        video_list_title.setText(item.title)
    }
}

fun soundItemDelegate(
    itemClickedListener: (soundDto: AmericanSoundDto, sharedElements: Array<Pair<View, String>>) -> Unit,
    getColor: (context: Context, colorRes: Int) -> ColorStateList?,
    singleCharMaxWight: () -> Float?
) = adapterDelegateLayoutContainer<AmericanSoundDto, Any>(R.layout.item_sound) {

    sound_root_view.setOnClickListener {
        val pair = Pair(itemView, ViewCompat.getTransitionName(sound_root_view) ?: "")
        item.also { itemClickedListener(it, arrayOf(pair)) }
    }

    bind {
        ViewCompat.setTransitionName(sound_root_view, item.transcription)
        val maxWordLength = word.width / (singleCharMaxWight()?.toInt() ?: 1)
        sound.text = item.transcription
        word.text = item.spellingWordList.filter { it.name.length <= maxWordLength }.let {
            if (it.isNotEmpty()) it.random()
            else item.spellingWordList.minBy { word -> word.name.length }
        }?.name
        sound.backgroundTintList = getColor(context, item.soundType.color())
    }
}


fun soundHeaderItemDelegate() =
    adapterDelegateLayoutContainer<SoundHeader, Any>(R.layout.item_sound_header) {

        bind {
            title.setText(item.soundType.humanName())
        }
    }