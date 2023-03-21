package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.base.navigation.api.container.TabsContainer
import com.thebrodyaga.core.uiUtils.insets.appleInsetPadding
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.ime
import com.thebrodyaga.core.uiUtils.insets.systemBars
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.databinding.FragmentMainBinding
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenComponent
import javax.inject.Inject

class MainFragment : ScreenFragment(R.layout.fragment_main), TabsContainer {

    @Inject
    lateinit var recordVoice: RecordVoice

    @Inject
    lateinit var mainScreenFactory: MainScreenFactory

    private val binding by viewBinding(FragmentMainBinding::bind)
    private val currentFragment: Fragment?
        get() = childFragmentManager.fragments.find { it.isVisible }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainScreenComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val position = when (item.itemId) {
                R.id.main_menu_first -> (0)
                R.id.main_menu_second -> (1)
                R.id.main_menu_third -> (2)
                else -> -1
            }
            if (position > -1)
                onBottomBarClick(position)
            position > -1
        }
        binding.bottomNavigation.setOnItemReselectedListener {
            (currentFragment as? TabContainerFragment)?.tabRouter?.backTo(null)
        }
        if (childFragmentManager.findFragmentById(R.id.fragment_container) == null)
            onBottomBarClick(FIRST_MAIN_PAGE.first)
        binding.micButton.setRecordVoice(recordVoice)
    }

    override fun applyWindowInsets(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation, null)
        rootView.doOnApplyWindowInsets { view, insets, initialPadding ->
            val systemBars = insets.systemBars()
            val ime = insets.ime()
            val bottomNavigationHeight = binding.bottomNavigation.height
            binding.bottomNavigation.appleInsetPadding(
                left = systemBars.left,
                top = 0,
                right = systemBars.right,
                bottom = systemBars.bottom
            )

            // check is keyboard overlay bottomNavigation
            val bottomConsume = if (ime.bottom > bottomNavigationHeight) bottomNavigationHeight else ime.bottom
            // remove from the bottom bottomNavigationHeight or low height ime
            insets.inset(0, 0, 0, systemBars.bottom + bottomConsume)
        }
    }

    private fun onBottomBarClick(position: Int): Boolean {
        showPage(
            when (position) {
                FIRST_MAIN_PAGE.first -> FIRST_MAIN_PAGE.second
                SECOND_MAIN_PAGE.first -> SECOND_MAIN_PAGE.second
                THIRD_MAIN_PAGE.first -> THIRD_MAIN_PAGE.second
                else -> throw IllegalArgumentException("че за тег?")
            }
        )
        return true
    }

    private fun showPage(fragmentTag: String) {
        val currentFragment = currentFragment
        val newFragment = childFragmentManager.findFragmentByTag(fragmentTag)

        if (currentFragment != null && newFragment != null
            && currentFragment.tag == newFragment.tag
        )
            return
        childFragmentManager.beginTransaction().apply {
            if (newFragment == null)
                add(R.id.fragment_container, createTabFragment(fragmentTag), fragmentTag)
            currentFragment?.let {
                hide(it)
//                detach(it)
            }
            newFragment?.let {
                show(it)
//                attach(it)
            }
        }.commit()
    }

    override fun onBackPressed() {
        val currentTag = currentFragment?.tag ?: super.onBackPressed()
        if (currentTag != FIRST_MAIN_PAGE.second)
            binding.bottomNavigation.selectedItemId = R.id.main_menu_first
        else super.onBackPressed()
    }

    private fun createTabFragment(tag: String): Fragment =
        TabContainerFragment.getNewInstance(tag)

    companion object {
        val FIRST_MAIN_PAGE = Pair(0, "FIRST_TAB_MAIN_PAGE")
        val SECOND_MAIN_PAGE = Pair(1, "SECOND_TAB_MAIN_PAGE")
        val THIRD_MAIN_PAGE = Pair(2, "THIRD_MAIN_PAGE")
    }
}