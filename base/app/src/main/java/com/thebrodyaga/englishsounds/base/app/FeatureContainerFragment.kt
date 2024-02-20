package com.thebrodyaga.englishsounds.base.app

import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.container.FeatureContainer
import com.thebrodyaga.base.navigation.api.router.FeatureRouter
import com.thebrodyaga.base.navigation.impl.navigator.FlowNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies

abstract class FeatureContainerFragment(layoutId: Int) : ScreenFragment(layoutId), FeatureContainer {

    private val navigator: Navigator by lazy { FlowNavigator(this, containerId, routerProvider) }

    private val containerName: String
        get() = this.tag ?: javaClass.simpleName

    private val ciceroneHolder: CiceroneHolder
        get() = findDependencies<AppDependencies>().ciceroneHolder()

    private val cicerone: Cicerone<FeatureRouter>
        get() = ciceroneHolder.getOrCreate(containerName) { Cicerone.create(FeatureRouter()) }

    override val featureRouter: FeatureRouter
        get() = cicerone.router

    abstract val containerId: Int

    override fun onDestroy() {
        super.onDestroy()
        if (activity?.isChangingConfigurations == false) {
            ciceroneHolder.clear(containerName)
        }
    }

    override fun onResume() {
        super.onResume()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }
}