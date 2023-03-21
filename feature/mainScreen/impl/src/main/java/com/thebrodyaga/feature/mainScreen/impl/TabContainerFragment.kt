package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.view.View
import com.thebrodyaga.base.navigation.api.AppRouter
import com.thebrodyaga.base.navigation.api.container.TabContainer
import com.thebrodyaga.base.navigation.impl.FlowNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenComponent
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import javax.inject.Inject

class TabContainerFragment : ScreenFragment(R.layout.layout_fragemnt_container), TabContainer {

    @Inject
    lateinit var soundListScreenFactory: SoundListScreenFactory

    @Inject
    lateinit var videoListScreenFactory: VideoScreenFactory

    @Inject
    lateinit var trainingScreenFactory: TrainingScreenFactory

    private val containerName: String by lazy {
        arguments?.getString(EXTRA_NAME) ?: throw RuntimeException("need put key")
    }
    private val containerId = R.id.fragment_container
    private val navigator: Navigator by lazy { FlowNavigator(this, containerId, routerProvider) }
    private val cicerone = Cicerone.create(AppRouter())

    override val tabRouter: AppRouter = cicerone.router

    override fun onCreate(savedInstanceState: Bundle?) {
        MainScreenComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFragment = childFragmentManager.findFragmentById(containerId)
        if (currentFragment == null) {
            if (containerName == MainFragment.FIRST_MAIN_PAGE.second)
                tabRouter.newRootScreen(soundListScreenFactory.soundListFactory())
            if (containerName == MainFragment.SECOND_MAIN_PAGE.second)
                tabRouter.newRootScreen(videoListScreenFactory.videoListScreen())
            if (containerName == MainFragment.THIRD_MAIN_PAGE.second)
                tabRouter.newRootScreen(trainingScreenFactory.trainingScreen())
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