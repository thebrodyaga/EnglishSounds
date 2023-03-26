package com.thebrodyaga.core.uiUtils.recycler

import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field

class PrefetchRecycledViewPool() : RecyclerView.RecycledViewPool() {

    private val prefetchedViewHolder = mutableMapOf<Int, (viewType: Int) -> RecyclerView.ViewHolder?>()

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        var holder = super.getRecycledView(viewType)
        if (holder == null) {
            holder = prefetchedViewHolder[viewType]?.invoke(viewType)
            if (holder != null) {
                val viewTypeField: Field = holder::class.java.superclass
                    .getDeclaredField("mItemViewType")
                viewTypeField.isAccessible = true
                viewTypeField.setInt(holder, viewType)
            }
        }
        return holder
    }

    fun setPrefetchedViewType(
        viewType: Int,
        maxCount: Int,
        viewHolder: (viewType: Int) -> RecyclerView.ViewHolder?
    ) {
        setMaxRecycledViews(viewType, maxCount)
        prefetchedViewHolder[viewType] = viewHolder
    }
}