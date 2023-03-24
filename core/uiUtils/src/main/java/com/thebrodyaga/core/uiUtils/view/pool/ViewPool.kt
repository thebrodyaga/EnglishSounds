package com.thebrodyaga.core.uiUtils.view.pool

import android.view.View
import androidx.annotation.LayoutRes

interface ViewPool {

    suspend fun inflate(@LayoutRes resId: Int, size: Int, waitingSize: Int = 0): List<View>
    fun <V : View> push(view: V)
    fun <V : View> pop(): V?
}