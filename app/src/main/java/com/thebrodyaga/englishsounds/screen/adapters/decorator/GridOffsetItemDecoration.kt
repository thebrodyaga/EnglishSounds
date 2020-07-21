package com.thebrodyaga.englishsounds.screen.adapters.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class GridOffsetItemDecoration constructor(private val offset: Int, private val offsetLast: Boolean = true) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val gridLayoutManager = parent.layoutManager as? GridLayoutManager
        gridLayoutManager?.let {
            val position = parent.getChildAdapterPosition(view)
            val spanSize = it.spanSizeLookup.getSpanSize(position)
            val spanCount = it.spanCount
            val column = position % spanCount

            if (spanSize < spanCount) {
                val left = offset - column * offset / spanCount
                val top = if (position < spanCount) offset else 0
                val right = (column + 1) * offset / spanCount
                val bottom = offset

                outRect.set(left, top, right, bottom)
            } else {
                if (offsetLast) {
                    outRect.set(offset, offset, offset, offset)
                } else {
                    outRect.set(0, 0, 0, 0)
                }
            }
        }
    }
}