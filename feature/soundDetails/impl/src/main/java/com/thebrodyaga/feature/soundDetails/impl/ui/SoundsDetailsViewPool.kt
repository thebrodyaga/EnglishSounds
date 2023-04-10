package com.thebrodyaga.feature.soundDetails.impl.ui

import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchViewHolderPool
import com.thebrodyaga.core.uiUtils.recycler.pool.RecoverableViewHolderPool

// todo pool for details
class SoundsDetailsViewPool constructor(
    activity: AppCompatActivity,
) : PrefetchRecycledViewPool(
    activity = activity,
    viewHolderPool = RecoverableViewHolderPool(activity)
), PrefetchViewHolderPool {

    override fun prefetch() {
//        setPrefetchedViewType(
//            SoundCardViewHolder.VIEW_TYPE, SoundCardViewHolder.VIEW_TYPE, 28
//        ) { _: Int, itemView: View ->
//            SoundCardViewHolder(itemView)
//        }
    }
}