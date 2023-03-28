package com.thebrodyaga.feature.soundList.impl

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.brandbook.component.sound.SoundCardViewHolder
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchViewHolderPool
import com.thebrodyaga.core.uiUtils.recycler.pool.RecoverableViewHolderPool

class SoundsListViewPool constructor(
    activity: AppCompatActivity,
) : PrefetchRecycledViewPool(
    activity = activity,
    viewHolderPool = RecoverableViewHolderPool(activity)
), PrefetchViewHolderPool {

    override fun prefetch() {
        setPrefetchedViewType(
            SoundCardViewHolder.VIEW_TYPE, SoundCardViewHolder.VIEW_TYPE, 28
        ) { _: Int, itemView: View ->
            SoundCardViewHolder(itemView)
        }
    }
}