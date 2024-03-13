package com.thebrodyaga.feature.mainScreen.impl

import androidx.lifecycle.ViewModel
import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.router.TabRouter
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import javax.inject.Inject

class TabContainerViewModel @Inject constructor(
    private val ciceroneHolder: CiceroneHolder,
    private val containerName: String,
) : ViewModel() {

    val cicerone: Cicerone<TabRouter>
        get() = ciceroneHolder.getOrCreate(containerName) { Cicerone.create(TabRouter()) }

    val router: TabRouter
        get() = cicerone.router

    override fun onCleared() {
        super.onCleared()
        ciceroneHolder.clear(containerName)
    }
}