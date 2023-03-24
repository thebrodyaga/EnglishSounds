package com.thebrodyaga.core.uiUtils.view.pool

import android.view.View
import javax.inject.Inject

class ViewPoolHolderImpl @Inject constructor() : ViewPoolHolder {

    private val layoutMap = mutableMapOf<Int, ViewPool>()

    override fun <V : View> pop(layoutRes: Int): V? {
        return layoutMap[layoutRes]?.pop()
    }

    override fun <V : View> push(layoutRes: Int, view: V) {
        layoutMap[layoutRes]?.push(view)
    }

    override fun addViewPool(layoutRes: Int, viewPool: ViewPool) {
        layoutMap[layoutRes] = viewPool
    }
}