package com.thebrodyaga.englishsounds.screen.fragments.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.base.FlowFragment
import com.thebrodyaga.englishsounds.tools.RecordVoice
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import javax.inject.Inject

class MainFragment : FlowFragment() {

    private var micBehavior: FloatingActionButton.Behavior? = null

    override fun getContainerId(): Int = R.id.fragment_container

    override fun getContainerName(): String = Screens.MainScreen.screenKey

    @Inject
    lateinit var recordVoice: RecordVoice

    override fun getLayoutId(): Int = R.layout.fragment_main

    override val currentFragment: BaseFragment?
        get() = childFragmentManager.fragments.firstOrNull { !it.isHidden } as? BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
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
            val layoutParams = result.mic_button.layoutParams as ViewGroup.MarginLayoutParams
            val actionBarHeight =
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            val baseOffset = resources.getDimensionPixelSize(R.dimen.base_offset)
            layoutParams.rightMargin = baseOffset
            layoutParams.bottomMargin = actionBarHeight + baseOffset
        }

        micBehavior = (result.mic_button.layoutParams as CoordinatorLayout.LayoutParams).behavior
                as FloatingActionButton.Behavior

        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        micBehavior = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
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
        bottom_navigation.setOnNavigationItemReselectedListener {
            (currentFragment as? TabContainerFragment)
                ?.localRouter?.backTo(null)
        }
        if (currentFragment == null) onBottomBarClick(FIRST_MAIN_PAGE.first)
        mic_button.setRecordVoice(recordVoice)
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
                it.userVisibleHint = false
            }
            newFragment?.let {
                show(it)
                it.userVisibleHint = true
            }
        }.commitNow()
    }

    private fun createTabFragment(tag: String): Fragment =
        TabContainerFragment.getNewInstance(tag)

    fun toggleFabMic(isShow: Boolean?, autoHide: Boolean?) {
        when (isShow) {
            true -> mic_button?.show()
            false -> mic_button?.hide()
        }
        when (autoHide) {
            false, true -> micBehavior?.isAutoHideEnabled = autoHide
        }
    }

    companion object {
        val FIRST_MAIN_PAGE = Pair(0, "FIRST_TAB_MAIN_PAGE")
        val SECOND_MAIN_PAGE = Pair(1, "SECOND_TAB_MAIN_PAGE")
        val THIRD_MAIN_PAGE = Pair(2, "THIRD_MAIN_PAGE")
    }
}