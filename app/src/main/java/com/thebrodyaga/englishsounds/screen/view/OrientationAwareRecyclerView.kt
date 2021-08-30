package com.thebrodyaga.englishsounds.screen.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class OrientationAwareRecyclerView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var lastX = 0f
    private var lastY = 0f
    private var scrolling = false

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        scrolling = state != SCROLL_STATE_IDLE
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val lm: LayoutManager = layoutManager ?: return super.onInterceptTouchEvent(e)
        var allowScroll = true
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.x
                lastY = e.y
                if (scrolling) {
                    return super.onInterceptTouchEvent(createActionUpEvent(e))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                allowScroll = checkAllowScroll(e, lm)
            }
        }
        return if (!allowScroll) {
            false
        } else {
            super.onInterceptTouchEvent(e)
        }
    }

    private fun createActionUpEvent(e: MotionEvent): MotionEvent {
        val newEvent: MotionEvent = MotionEvent.obtain(e)
        newEvent.action = MotionEvent.ACTION_UP
        return newEvent
    }

    private fun checkAllowScroll(e: MotionEvent, lm: LayoutManager): Boolean {
        val dx = abs(e.x - lastX)
        val dy = abs(e.y - lastY)
        return if (dy > dx) {
            lm.canScrollVertically()
        } else {
            lm.canScrollHorizontally()
        }
    }
}