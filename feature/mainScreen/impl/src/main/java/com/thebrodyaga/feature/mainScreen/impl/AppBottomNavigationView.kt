package com.thebrodyaga.feature.mainScreen.impl

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarMenuView

class AppBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BottomNavigationView(context, attrs) {

    override fun createNavigationBarMenuView(context: Context): NavigationBarMenuView {
        return AppBottomNavigationMenuView(context)

    }
}