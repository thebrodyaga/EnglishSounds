package com.thebrodyaga.englishsounds.activity

import android.os.Bundle
import com.thebrodyaga.base.navigation.impl.navigator.AppNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.englishsounds.activity.di.DiAppActivityComponent
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import com.thebrodyaga.feature.appActivity.impl.R

class DiAppActivity : AppActivity() {

    override val navigator: Navigator = AppNavigator(this, R.id.appFragmentContainer, supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        component = DiAppActivityComponent.factory(this)
            .also { it.inject(this) }
        super.onCreate(savedInstanceState)
    }
}