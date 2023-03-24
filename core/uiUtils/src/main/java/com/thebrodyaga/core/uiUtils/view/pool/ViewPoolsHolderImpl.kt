package com.thebrodyaga.core.uiUtils.view.pool

import android.view.View

class ViewPoolsHolderImpl : ViewPoolsHolder {

    private val layoutMap = mutableMapOf<Int, ViewPool>()

    override fun <V : View> pop(layoutRes: Int): V? {
        return layoutMap[layoutRes]?.pop()
    }

    override fun <V : View> push(layoutRes: Int, view: V) {
        layoutMap[layoutRes]?.push(view)
    }

    override fun addViewPoolInflater(layoutRes: Int, inflater: ViewPool) {
        layoutMap[layoutRes] = inflater
    }
}