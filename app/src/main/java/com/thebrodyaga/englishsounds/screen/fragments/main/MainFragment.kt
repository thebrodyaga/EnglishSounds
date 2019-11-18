package com.thebrodyaga.englishsounds.screen.fragments.main

import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.navigation.LocalCiceroneHolder
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.screen.base.FlowFragment
import com.thebrodyaga.englishsounds.screen.view.BottomAppBarTopEdgeTreatment
import com.thebrodyaga.englishsounds.tools.RecordVoice
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : FlowFragment() {

    @Inject
    lateinit var cicHolder: LocalCiceroneHolder

    override fun getContainerId(): Int = R.id.fragment_container

    override fun getContainerName(): String = Screens.MainScreen.screenKey

    override fun getCiceroneHolder(): LocalCiceroneHolder = cicHolder

    @Inject
    lateinit var recordVoice: RecordVoice

    override fun getLayoutId(): Int = R.layout.fragment_main

    private val currentTabFragment: Fragment?
        get() = childFragmentManager.fragments.firstOrNull { !it.isHidden }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            val position = when (item.itemId) {
                R.id.main_menu_first -> (0)
                R.id.main_menu_third -> (1)
                else -> -1
            }
            if (position > -1)
                onBottomBarClick(position)
            position > -1
        }
        if (currentTabFragment == null) onBottomBarClick(FIRST_MAIN_PAGE.first)
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

    override fun onBackPressed() {
        (currentTabFragment as? TabContainerFragment)?.onBackPressed()
            ?: super.onBackPressed()
    }

    private fun showPage(fragmentTag: String) {
        val currentFragment = currentTabFragment
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

    companion object {
        val FIRST_MAIN_PAGE = Pair(0, "FIRST_TAB_MAIN_PAGE")
        val SECOND_MAIN_PAGE = Pair(1, "SECOND_TAB_MAIN_PAGE")
        val THIRD_MAIN_PAGE = Pair(2, "THIRD_MAIN_PAGE")
    }
}
