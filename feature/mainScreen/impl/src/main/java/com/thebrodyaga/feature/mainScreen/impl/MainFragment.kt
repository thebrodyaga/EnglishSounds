package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.core.uiUtils.insets.appleInsetPadding
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.ime
import com.thebrodyaga.core.uiUtils.insets.systemBars
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.app.FlowFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.databinding.FragmentMainBinding
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenComponent
import javax.inject.Inject

class MainFragment : FlowFragment() {

//    private var micBehavior: FloatingActionButton.Behavior? = null

    override fun getContainerId(): Int = R.id.fragment_container

    override fun getContainerName(): String = mainScreenFactory.mainScreen().screenKey

    @Inject
    lateinit var recordVoice: RecordVoice
    private val binding by viewBinding(FragmentMainBinding::bind)

    @Inject
    lateinit var mainScreenFactory: MainScreenFactory

    override fun getLayoutId(): Int = R.layout.fragment_main

    override val currentFragment: BaseFragment?
        get() = childFragmentManager.fragments.firstOrNull { !it.isHidden } as? BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        MainScreenComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        val tv = TypedValue()
        if (result.context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val layoutParams = result.findViewById<View>(R.id.mic_button).layoutParams as ViewGroup.MarginLayoutParams
            val actionBarHeight =
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            val baseOffset = resources.getDimensionPixelSize(R.dimen.base_offset)
            layoutParams.rightMargin = baseOffset
            layoutParams.bottomMargin = actionBarHeight + baseOffset
        }

//        micBehavior = (result.mic_button.layoutParams as CoordinatorLayout.LayoutParams).behavior
//            as FloatingActionButton.Behavior

        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        micBehavior = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
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
        binding.bottomNavigation.setOnNavigationItemReselectedListener {
            (currentFragment as? TabContainerFragment)
                ?.localRouter?.backTo(null)
        }
        if (currentFragment == null) onBottomBarClick(FIRST_MAIN_PAGE.first)
        binding.micButton.setRecordVoice(recordVoice)
    }

    override fun applyWindowInsets(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation, null)
        rootView.doOnApplyWindowInsets { view, insets, initialPadding ->
            val systemBars = insets.systemBars()
            val ime = insets.ime()
            val bottomNavigationHeight = binding.bottomNavigation.height
            binding.bottomNavigation.appleInsetPadding(left = 0, top = 0, right = 0, bottom = systemBars.bottom)

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
            }
            newFragment?.let {
                show(it)
            }
        }.commit()
    }

    private fun createTabFragment(tag: String): Fragment =
        TabContainerFragment.getNewInstance(tag)

    fun toggleFabMic(isShow: Boolean?, autoHide: Boolean?) {
        when (isShow) {
            true -> binding.micButton?.show()
            false -> binding.micButton?.hide()
            null -> {}
        }
//        when (autoHide) {
//            false, true -> micBehavior?.isAutoHideEnabled = autoHide
//            null -> {}
//        }
    }

    companion object {
        val FIRST_MAIN_PAGE = Pair(0, "FIRST_TAB_MAIN_PAGE")
        val SECOND_MAIN_PAGE = Pair(1, "SECOND_TAB_MAIN_PAGE")
        val THIRD_MAIN_PAGE = Pair(2, "THIRD_MAIN_PAGE")
    }
}