package com.thebrodyaga.feature.soundDetails.impl.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.dsl.CommonViewHolder
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchViewHolderPool
import com.thebrodyaga.core.uiUtils.recycler.pool.RecoverableViewHolderPool
import com.thebrodyaga.core.uiUtils.recycler.pool.ViewHolderFactory
import com.thebrodyaga.feature.soundDetails.impl.ui.adapter.*
import kotlinx.coroutines.delay

class SoundsDetailsViewPool constructor(
    private val activity: AppCompatActivity,
) : PrefetchRecycledViewPool(
    activity = activity,
    viewHolderPool = RecoverableViewHolderPool(activity)
), PrefetchViewHolderPool {

    override fun prefetch() {
        activity.lifecycleScope.launchWhenCreated {
            // 1 sec delay because not main screen
            delay(1000)
            val viewHolderFactory: ViewHolderFactory = { _: Int, itemView: View ->
                CommonViewHolder<UiModel, View>(itemView)
            }
            setPrefetchedViewType(
                SOUND_DETAILS_IMAGE_VIEW_TYPE, SOUND_DETAILS_IMAGE_LAYOUT_ID, 1,
                viewHolderFactory = viewHolderFactory
            )
            setPrefetchedViewType(
                SOUND_DETAILS_VIEW_TYPE, SOUND_DETAILS_LAYOUT_ID, 1,
                viewHolderFactory = viewHolderFactory
            )
            setPrefetchedViewType(
                SOUND_DETAILS_DESCRIPTION_VIEW_TYPE, SOUND_DETAILS_DESCRIPTION_LAYOUT_ID, 1,
                viewHolderFactory = viewHolderFactory
            )
        }
    }
}