package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.thebrodyaga.base.navigation.api.CiceroneHolder
import com.thebrodyaga.base.navigation.api.container.TabContainer
import com.thebrodyaga.base.navigation.api.router.TabRouter
import com.thebrodyaga.base.navigation.impl.navigator.FlowNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenComponent
import com.thebrodyaga.feature.setting.api.SettingsScreenFactory
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import javax.inject.Inject

class TabContainerFragment : ScreenFragment(R.layout.fragemnt_tab_container), TabContainer {

    @Inject
    lateinit var soundListScreenFactory: SoundListScreenFactory

    @Inject
    lateinit var videoListScreenFactory: VideoScreenFactory

    @Inject
    lateinit var trainingScreenFactory: TrainingScreenFactory

    @Inject
    lateinit var settingsScreenFactory: SettingsScreenFactory

    @Inject
    lateinit var ciceroneHolder: CiceroneHolder

    private val viewModel: TabContainerViewModel by viewModels()

    private val containerName: String by lazy {
        arguments?.getString(EXTRA_NAME) ?: throw RuntimeException("need put key")
    }
    private val containerId = R.id.tabContainer
    private val navigator: Navigator by lazy {
        FlowNavigator(this, containerId, routerProvider)
    }
    private val cicerone: Cicerone<TabRouter>
        get() = viewModel.cicerone

    override val tabRouter: TabRouter
        get() = viewModel.router

    override fun onCreate(savedInstanceState: Bundle?) {
        MainScreenComponent.factory(this).inject(this)
        super.onCreate(savedInstanceState)
        viewModel.containerName = containerName
        viewModel.ciceroneHolder = ciceroneHolder
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFragment = childFragmentManager.findFragmentById(containerId)
        if (currentFragment == null) {
            when (containerName) {
                MainFragment.BottomTabPosition.FIRST.screenTag -> tabRouter.newRootScreen(soundListScreenFactory.soundListFactory())
                MainFragment.BottomTabPosition.SECOND.screenTag -> tabRouter.newRootScreen(videoListScreenFactory.videoCarouselScreen())
                MainFragment.BottomTabPosition.THIRD.screenTag -> tabRouter.newRootScreen(trainingScreenFactory.trainingScreen())
                MainFragment.BottomTabPosition.FOURTH.screenTag -> tabRouter.newRootScreen(settingsScreenFactory.settingScreen())
            }
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

    override fun applyWindowInsets(rootView: View) = Unit

    companion object {
        private const val EXTRA_NAME = "tcf_extra_name"

        fun getNewInstance(name: String): TabContainerFragment {
            val fragment = TabContainerFragment()

            val arguments = Bundle()
            arguments.putString(EXTRA_NAME, name)
            fragment.arguments = arguments

            return fragment
        }
    }
}