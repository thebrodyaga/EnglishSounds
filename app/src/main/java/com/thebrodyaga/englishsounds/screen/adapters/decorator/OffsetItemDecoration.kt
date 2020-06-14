package com.thebrodyaga.englishsounds.screen.adapters.decorator

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import timber.log.Timber

class OffsetItemDecoration constructor(
    context: Context,
    @Orientation private val orientation: Int,
    @DimenRes horizontalOffsetRes: Int? = null,
    @DimenRes verticalOffsetRes: Int? = null,
    private val includeEdges: Boolean = true
) : ItemDecoration() {

    private val horizontalOffset: Int
    private val verticalOffset: Int

    init {
        val resources = context.resources
        horizontalOffset = horizontalOffsetRes?.let { resources.getDimensionPixelOffset(it) } ?: 0
        verticalOffset = verticalOffsetRes?.let { resources.getDimensionPixelOffset(it) } ?: 0
        checkOffsets()
    }

    private fun checkOffsets() {
        if (horizontalOffset <= 0 && verticalOffset <= 0) {
            Timber.w("No offsets applied.")
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        val position = parent.getChildAdapterPosition(view)
        val count = position + parent.childCount
        when (orientation) {
            HORIZONTAL -> offsetHorizontal(outRect, position, count)
            VERTICAL -> offsetVertical(outRect, position, count)
        }
    }

    private fun offsetHorizontal(outRect: Rect, position: Int, count: Int) {
        val edgeOffset = if (includeEdges) horizontalOffset else 0
        when (position) {
            0 -> outRect.set(edgeOffset, verticalOffset, horizontalOffset / 2, verticalOffset)
            in 1 until count - 1 -> outRect.set(
                horizontalOffset / 2,
                verticalOffset,
                horizontalOffset / 2,
                verticalOffset
            )
            count - 1 -> outRect.set(
                horizontalOffset / 2,
                verticalOffset,
                edgeOffset,
                verticalOffset
            )
            else -> outRect.set(0, 0, 0, 0)
        }
    }

    private fun offsetVertical(outRect: Rect, position: Int, count: Int) {
        val edgeOffset = if (includeEdges) verticalOffset else 0
        when (position) {
            0 -> outRect.set(horizontalOffset, edgeOffset, horizontalOffset, verticalOffset / 2)
            in 1 until count - 1 -> outRect.set(
                horizontalOffset,
                verticalOffset / 2,
                horizontalOffset,
                verticalOffset / 2
            )
            count - 1 -> outRect.set(
                horizontalOffset,
                verticalOffset / 2,
                horizontalOffset,
                edgeOffset
            )
            else -> outRect.set(0, 0, 0, 0)
        }
    }
}
