package com.thebrodyaga.core.uiUtils.recycler.pool

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

typealias ViewHolderFactory = (viewType: Int, itemView: View) -> RecyclerView.ViewHolder

open class PrefetchRecycledViewPool(
    private val activity: AppCompatActivity,
    private val viewHolderPool: ViewHolderPool = AsyncViewHolderPool(activity)
) : RecyclerView.RecycledViewPool() {

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        var holder = super.getRecycledView(viewType)
        if (holder == null) {
            viewHolderPool.pop(viewType)
            holder = viewHolderPool.pop(viewType)
        }
        return holder
    }

    fun setPrefetchedViewType(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        maxCount: Int,
        viewHolderFactory: ViewHolderFactory,
    ) {
        setMaxRecycledViews(viewType, maxCount)
        viewHolderPool.create(viewType, itemLayout, maxCount, viewHolderFactory)
    }
}