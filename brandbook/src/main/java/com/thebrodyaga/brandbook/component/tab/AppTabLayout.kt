package com.thebrodyaga.brandbook.component.tab

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout

class AppTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TabLayout(context, attrs) {

    override fun addView(child: View?) {
        if (child is TabItem) super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        if (child is TabItem) super.addView(child, index)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is TabItem) super.addView(child, index, params)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (child is TabItem) super.addView(child, params)
    }
}