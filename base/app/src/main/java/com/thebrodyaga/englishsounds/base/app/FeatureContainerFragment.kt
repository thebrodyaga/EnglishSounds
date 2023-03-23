package com.thebrodyaga.englishsounds.base.app

import com.thebrodyaga.base.navigation.api.container.FeatureContainer
import com.thebrodyaga.base.navigation.api.router.FeatureRouter
import com.thebrodyaga.base.navigation.impl.navigator.FlowNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import com.thebrodyaga.core.navigation.api.cicerone.Navigator

abstract class FeatureContainerFragment(layoutId: Int) : ScreenFragment(layoutId), FeatureContainer {

    private val navigator: Navigator by lazy { FlowNavigator(this, containerId, routerProvider) }
    private val cicerone = Cicerone.create(FeatureRouter())

    override val featureRouter: FeatureRouter = cicerone.router
    abstract val containerId: Int

    override fun onResume() {
        super.onResume()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }
}