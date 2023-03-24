package com.thebrodyaga.core.uiUtils.view.pool

import android.view.View
import androidx.annotation.LayoutRes

interface ViewPoolHolder {

    fun <V : View> pop(@LayoutRes layoutRes: Int): V?
    fun <V : View> push(@LayoutRes layoutRes: Int, view: V)

    fun addViewPool(@LayoutRes layoutRes: Int, viewPool: ViewPool)
}