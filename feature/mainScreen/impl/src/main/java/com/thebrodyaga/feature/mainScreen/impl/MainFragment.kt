package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.base.navigation.api.container.TabsContainer
import com.thebrodyaga.core.uiUtils.insets.*
import com.thebrodyaga.core.uiUtils.outline.shapeOutline
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.shape.shapeTopRounded
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.databinding.FragmentMainBinding
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenComponent
import dev.shreyaspatil.permissionFlow.utils.registerForPermissionFlowRequestsResult
import javax.inject.Inject


class MainFragment : ScreenFragment(R.layout.fragment_main), TabsContainer {

    @Inject
    lateinit var mainScreenFactory: MainScreenFactory

    @Inject
    lateinit var recordVoice: RecordVoice
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val tabs = listOf(
        BottomTab.Sounds,
        BottomTab.Videos,
        BottomTab.Words,
        BottomTab.Settings,
    )

    private val binding by viewBinding(FragmentMainBinding::bind)
    private val currentFragment: Fragment?
        get() = childFragmentManager.fragments.find { it.isVisible }

    override fun onCreate(savedInstanceState: Bundle?) {
        permissionLauncher = registerForPermissionFlowRequestsResult()
        MainScreenComponent.factory(this).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainBottomNavigation.setOnItemSelectedListener { item ->
            val position = when (item.itemId) {
                R.id.main_menu_first -> (0)
                R.id.main_menu_second -> (1)
                R.id.main_menu_third -> (2)
                R.id.main_menu_fourth -> (3)
                else -> -1
            }
            if (position > -1) onBottomBarClick(position)
            position > -1
        }
        binding.mainBottomNavigation.setOnItemReselectedListener {
            (currentFragment as? TabContainerFragment)?.tabRouter?.resetTabStack()
        }
        binding.mainBottomAppBar.shapeOutline(shapeTopRounded(16f.px))
        if (childFragmentManager.findFragmentById(R.id.mainFragmentContainer) == null) {
            onBottomBarClick(BottomTab.Sounds.position.index)
        }
//        binding.mainMicButton.setOnClickListener {
//            TestBottomSheetDialog().show(childFragmentManager, "TestBottomSheetDialog")
//        }
        MicButtonDelegate().bind(this, binding.mainMicButton, recordVoice, permissionLauncher)
    }

    override fun applyWindowInsets(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainBottomNavigation, null)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainBottomAppBar, null)
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            val navigationBars = insets.navigationBars()
            val ime = insets.ime()

            binding.mainBottomNavigation.appleInsetPadding(
                oldInsets = navigationBars, bottom = navigationBars.bottom
            )

            val newInsets = WindowInsetsCompat.Builder(insets)

            val fabExtendHeight = binding.mainBottomAppBar.y - binding.mainMicButton.y
            val appBarHeight = fabExtendHeight + binding.mainBottomAppBar.height

            if (appBarHeight == 0f) {
                rootView.requestApplyInsets()
                return@doOnApplyWindowInsets insets
            }

            val newNavigationInsets =
                with(navigationBars) { Insets.of(left, top, right, appBarHeight.toInt()) }
            newInsets.setInsets(navigationInsetType, newNavigationInsets)

            if (insets.isVisible(imeInsetType)) {
                val isKeyboardOverlay = ime.bottom > appBarHeight

                val newImeBottom = if (isKeyboardOverlay) ime.bottom else 0
                val newImeInsets = with(ime) { Insets.of(left, top, right, newImeBottom) }

                newInsets.setInsets(imeInsetType, newImeInsets)
            }

            newInsets.build()
        }
    }

    private fun onBottomBarClick(position: Int): Boolean {
        val screenTag = tabs.first { it.position.ordinal == position }.position.screenTag
        showPage(screenTag)
        return true
    }

    private fun showPage(fragmentTag: String) {
        val currentFragment = currentFragment
        val newFragment = childFragmentManager.findFragmentByTag(fragmentTag)

        if (currentFragment != null && newFragment != null && currentFragment.tag == newFragment.tag) return
        childFragmentManager.beginTransaction().apply {
            if (newFragment == null) add(
                R.id.mainFragmentContainer,
                createTabFragment(fragmentTag),
                fragmentTag
            )
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
        if (currentTag != BottomTabPosition.FIRST.screenTag) {
            binding.mainBottomNavigation.selectedItemId = R.id.main_menu_first
        } else super.onBackPressed()
    }

    private fun createTabFragment(tag: String): Fragment = TabContainerFragment.getNewInstance(tag)

    private sealed class BottomTab constructor(
        val position: BottomTabPosition,
    ) {

        data object Sounds : BottomTab(BottomTabPosition.FIRST)
        data object Videos : BottomTab(BottomTabPosition.SECOND)
        data object Words : BottomTab(BottomTabPosition.THIRD)
        data object Settings : BottomTab(BottomTabPosition.FOURTH)
    }

    enum class BottomTabPosition {
        FIRST, SECOND, THIRD, FOURTH;

        val screenTag: String
            get() = name

        val index: Int
            get() = ordinal
    }
}