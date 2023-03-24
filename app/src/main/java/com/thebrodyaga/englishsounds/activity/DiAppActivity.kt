package com.thebrodyaga.englishsounds.activity

import android.os.Bundle
import com.thebrodyaga.englishsounds.activity.di.DiAppActivityComponent
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.appActivity.impl.AppActivity

class DiAppActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        component = DiAppActivityComponent.factory(findDependencies())
            .also { it.inject(this) }
        super.onCreate(savedInstanceState)
    }
}