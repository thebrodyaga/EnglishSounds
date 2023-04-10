package com.thebrodyaga.englishsounds.base.app

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.container.FeatureContainer
import com.thebrodyaga.base.navigation.api.router.FeatureRouter
import com.thebrodyaga.base.navigation.impl.navigator.FlowNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies

abstract class FeatureContainerFragment(layoutId: Int) : ScreenFragment(layoutId), FeatureContainer {

    private val navigator: Navigator by lazy { FlowNavigator(this, containerId, routerProvider) }
    private val viewModel: FeatureContainerViewModel by viewModels()

    private val containerName: String
        get() = this.tag ?: javaClass.simpleName

    private val ciceroneHolder: CiceroneHolder
        get() = findDependencies<AppDependencies>().ciceroneHolder()

    override val featureRouter: FeatureRouter
        get() = viewModel.router

    abstract val containerId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.containerName = containerName
        viewModel.ciceroneHolder = ciceroneHolder
    }

    override fun onResume() {
        super.onResume()
        viewModel.cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        viewModel.cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }
}