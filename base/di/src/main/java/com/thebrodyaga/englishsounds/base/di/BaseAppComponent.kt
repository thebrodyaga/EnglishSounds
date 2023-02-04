package com.thebrodyaga.englishsounds.base.di

import androidx.fragment.app.Fragment

fun Fragment.findComponent(): BaseAppComponent =
    (requireContext().applicationContext as ComponentHolder).component()

interface ComponentHolder {
    fun component(): BaseAppComponent
}

interface BaseAppComponent {

}