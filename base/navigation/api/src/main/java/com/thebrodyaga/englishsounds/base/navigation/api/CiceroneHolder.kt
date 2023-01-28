package com.thebrodyaga.englishsounds.base.navigation.api

import com.thebrodyaga.englishsounds.base.navigation.api.cicerone.Cicerone

interface CiceroneHolder {

    fun getCicerone(
        containerTag: String,
        defaultValue: Cicerone<AppRouter> = Cicerone.create(AppRouter())
    ): Cicerone<AppRouter>

    fun clearCicerone(containerTag: String)
}