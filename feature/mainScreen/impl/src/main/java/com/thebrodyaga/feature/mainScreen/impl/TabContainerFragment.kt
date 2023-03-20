package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.view.View
import com.thebrodyaga.englishsounds.base.app.FlowFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenComponent
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import javax.inject.Inject

/**
 * Created by terrakok 25.11.16
 */
class TabContainerFragment : FlowFragment() {

    @Inject
    lateinit var soundListScreenFactory: SoundListScreenFactory

    @Inject
    lateinit var videoListScreenFactory: VideoScreenFactory

    @Inject
    lateinit var trainingScreenFactory: TrainingScreenFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        MainScreenComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (currentFragment == null) {
            val containerName = getContainerName()
            if (containerName == MainFragment.FIRST_MAIN_PAGE.second)
                localRouter.newRootScreen(soundListScreenFactory.soundListFactory())
            if (containerName == MainFragment.SECOND_MAIN_PAGE.second)
                localRouter.newRootScreen(videoListScreenFactory.videoListScreen())
            if (containerName == MainFragment.THIRD_MAIN_PAGE.second)
                localRouter.newRootScreen(trainingScreenFactory.trainingScreen())
        }
    }

    override fun applyWindowInsets(rootView: View) = Unit

    override fun getLayoutId(): Int {
        return R.layout.layout_fragemnt_container
    }

    override fun getContainerId(): Int {
        return R.id.fragment_container
    }

    override fun getContainerName(): String {
        return arguments?.getString(EXTRA_NAME) ?: throw RuntimeException("need put key")
    }

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