package com.thebrodyaga.core.uiUtils.recycler.pool

import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

typealias ViewHolderFactory = (viewType: Int, itemView: View) -> RecyclerView.ViewHolder

abstract class PrefetchRecycledViewPool(
    private val activity: AppCompatActivity,
    private val viewHolderPool: ViewHolderPool = AsyncViewHolderPool(activity)
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
        maxCount: Int,
        viewHolderFactory: ViewHolderFactory,
    ) {
        setMaxRecycledViews(viewType, maxCount)
        viewHolderPool.create(viewType, itemLayout, maxCount, viewHolderFactory)
    }
}