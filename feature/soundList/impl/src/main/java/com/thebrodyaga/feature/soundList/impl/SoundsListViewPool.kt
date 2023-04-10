package com.thebrodyaga.feature.soundList.impl

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.brandbook.component.sound.SoundCardView
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.dsl.CommonViewHolder
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
            SoundCardView.VIEW_TYPE, SoundCardView.LAYOUT_ID, 28
        ) { _: Int, itemView: View ->
            CommonViewHolder<UiModel, SoundCardView>(itemView)
        }
    }
}