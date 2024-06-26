package com.thebrodyaga.feature.appActivity.impl

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.thebrodyaga.ad.api.AppAdManager
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.core.uiUtils.launchWithLifecycle
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.englishsounds.base.app.BaseActivity
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.HasActivityDependencies
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class AppActivity : BaseActivity(), HasActivityDependencies {

    protected lateinit var component: ActivityDependencies

    override val dependencies: ActivityDependencies
        get() = component

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var viewPools: Set<@JvmSuppressWildcards PrefetchRecycledViewPool>

    @Inject
    lateinit var mainScreenFactory: MainScreenFactory

    @Inject
    lateinit var adLoader: AppAdManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AppViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private lateinit var inAppReviewDelegate: InAppReviewDelegate
    abstract val navigator: Navigator

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.appFragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        inAppReviewDelegate = InAppReviewDelegate(this) { settingManager.onRateRequestShow() }
        setContentView(R.layout.layout_fragemnt_container)
        if (viewModel.keepSystemSplashVisible) {
            adLoader.onCreate(this)
            splashScreen.setKeepOnScreenCondition {
                !viewModel.isReady
            }
        } else {
            val progress = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                createProgressView()
            else null
            splashScreen.setOnExitAnimationListener { splashProvider ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                    (splashProvider.view as ViewGroup).addView(progress)
                lifecycleScope.launch {
                    waitOnLoaded(splashProvider)
                    adLoader.onCreate(this@AppActivity)
                }
            }
        }
        if (currentFragment == null) newRootScreen()
        viewPools.forEach { it.prefetch() }
        settingManager.needShowRateRequest()
            .onEach { showRateDialog(it) }
            .launchWithLifecycle(lifecycle)
    }

    private suspend fun waitOnLoaded(splashProvider: SplashScreenViewProvider) {
        while (!viewModel.isReady) {
            delay(50)
        }
        splashProvider.remove()
    }

    private fun newRootScreen() {
        val mainScreen = mainScreenFactory.mainScreen() as FragmentScreen
        val fragment = mainScreen.createFragment(supportFragmentManager.fragmentFactory)
        supportFragmentManager.beginTransaction()
            .replace(R.id.appFragmentContainer, fragment, mainScreen.screenKey)
            .commitNow()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun showRateDialog(needShowRate: Boolean) {
        if (needShowRate) {
            inAppReviewDelegate.showRateDialog()
        }
    }

    private fun createProgressView(): View {
        val progress = ConstraintLayout(this)
        progress.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val indicator = CircularProgressIndicator(this).apply {
            isIndeterminate = true
            id = View.generateViewId()
            val color = ContextCompat.getColor(this@AppActivity, R.color.app_icon_background)
            setIndicatorColor(color)
            progress.addView(this)
        }
        ConstraintSet().apply {
            val id = indicator.id
            val parentId = ConstraintSet.PARENT_ID
            constrainHeight(id, ConstraintSet.WRAP_CONTENT)
            constrainWidth(id, ConstraintSet.WRAP_CONTENT)
            connect(id, ConstraintSet.START, parentId, ConstraintSet.START)
            connect(id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
            connect(id, ConstraintSet.END, parentId, ConstraintSet.END)
            connect(id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)
            setVerticalBias(id, 0.8f)
            applyTo(progress)
        }
        return progress
    }
}
