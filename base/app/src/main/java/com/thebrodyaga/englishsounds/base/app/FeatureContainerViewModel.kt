package com.thebrodyaga.englishsounds.base.app

import androidx.lifecycle.ViewModel
import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.router.FeatureRouter
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone

class FeatureContainerViewModel : ViewModel() {

    lateinit var ciceroneHolder: CiceroneHolder
    lateinit var containerName: String

    val cicerone: Cicerone<FeatureRouter>
        get() = ciceroneHolder.getOrCreate(containerName) { Cicerone.create(FeatureRouter()) }

    val router: FeatureRouter
        get() = cicerone.router

    override fun onCleared() {
        super.onCleared()
        ciceroneHolder.clear(containerName)
    }
}