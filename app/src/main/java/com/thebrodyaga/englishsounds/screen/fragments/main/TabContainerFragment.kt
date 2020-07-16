package com.thebrodyaga.englishsounds.screen.fragments.main

import android.os.Bundle

import javax.inject.Inject

import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.navigation.LocalCiceroneHolder
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.screen.base.FlowFragment

/**
 * Created by terrakok 25.11.16
 */
class TabContainerFragment : FlowFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (currentFragment == null) {
            val containerName = getContainerName()
            if (containerName == MainFragment.FIRST_MAIN_PAGE.second)
                localRouter.newRootScreen(Screens.SoundsListScreen)
            if (containerName == MainFragment.SECOND_MAIN_PAGE.second)
                localRouter.newRootScreen(Screens.AllVideoScreen())
            if (containerName == MainFragment.THIRD_MAIN_PAGE.second)
                localRouter.newRootScreen(Screens.SoundsTrainingScreen)
        }
    }

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