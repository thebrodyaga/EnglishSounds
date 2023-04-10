package com.thebrodyaga.feature.videoList.impl.carousel

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.dsl.CommonViewHolder
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.core.uiUtils.recycler.pool.RecoverableViewHolderPool
import com.thebrodyaga.core.uiUtils.recycler.pool.ViewHolderFactory
import kotlinx.coroutines.delay

class VideoCarouselViewPool(
    private val activity: AppCompatActivity
) : PrefetchRecycledViewPool(
    activity = activity,
    viewHolderPool = RecoverableViewHolderPool(activity)
) {
    override fun prefetch() {
        activity.lifecycleScope.launchWhenCreated {
            // 1 sec delay because not main screen
            delay(1000)
            val viewHolderFactory: ViewHolderFactory = { _: Int, itemView: View ->
                CommonViewHolder<UiModel, View>(itemView)
            }
            setPrefetchedViewType(
                VIDEO_CAROUSEL_ITEM_VIEW_TYPE, VIDEO_CAROUSEL_ITEM_LAYOUT_ID, 45,
                maxRecycledViews = 65,
                viewHolderFactory = viewHolderFactory
            )
        }
    }
}