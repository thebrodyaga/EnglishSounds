package com.thebrodyaga.core.uiUtils.recycler.pool

import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

typealias ViewHolderFactory = (viewType: Int, itemView: View) -> RecyclerView.ViewHolder

abstract class PrefetchRecycledViewPool(
    private val activity: AppCompatActivity,
    private val viewHolderPool: ViewHolderPool,
) : RecyclerView.RecycledViewPool(), PrefetchViewHolderPool {

    companion object {
        const val TAG = "PrefetchViewPool"
    }

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        var holder = super.getRecycledView(viewType)
        Log.d(TAG, "super.getRecycledView holder is ${holder?.let { "NOT " } ?: ""}null")
        if (holder == null) {
            holder = viewHolderPool.pop(viewType)
            Log.d(TAG, "local pool holder is ${holder?.let { "NOT " } ?: ""}null")
        }
        return holder
    }

    fun setPrefetchedViewType(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        prefetchSize: Int,
        maxRecycledViews: Int = prefetchSize,
        viewHolderFactory: ViewHolderFactory,
    ) {
        val maxRecycled = if (maxRecycledViews > 5) maxRecycledViews else 5
        setMaxRecycledViews(viewType, maxRecycled)
        viewHolderPool.create(viewType, itemLayout, prefetchSize, viewHolderFactory)
    }
}