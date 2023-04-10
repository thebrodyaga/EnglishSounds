package com.thebrodyaga.feature.mainScreen.impl

import androidx.lifecycle.ViewModel
import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.router.TabRouter
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone

class TabContainerViewModel : ViewModel() {
    lateinit var ciceroneHolder: CiceroneHolder
    lateinit var containerName: String

    val cicerone: Cicerone<TabRouter>
        get() = ciceroneHolder.getOrCreate(containerName) { Cicerone.create(TabRouter()) }

    val router: TabRouter
        get() = cicerone.router

    override fun onCleared() {
        super.onCleared()
        ciceroneHolder.clear(containerName)
    }
}