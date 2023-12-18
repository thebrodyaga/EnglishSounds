package com.thebrodyaga.feature.appActivity.impl

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.material.color.DynamicColors
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.thebrodyaga.ad.api.AppAdLoader
import com.thebrodyaga.base.navigation.api.router.AppRouter
import com.thebrodyaga.base.navigation.impl.navigator.AppNavigator
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.englishsounds.base.app.BaseActivity
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.HasActivityDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.soundList.impl.SoundsListViewPool
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

open class AppActivity : BaseActivity(), HasActivityDependencies {

    protected lateinit var component: ActivityDependencies

    override val dependencies: ActivityDependencies
        get() = component

    @Inject
    lateinit var router: AppRouter

    @Inject
    lateinit var recordVoice: RecordVoice

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var soundsListViewPool: SoundsListViewPool

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var viewPools: Set<@JvmSuppressWildcards PrefetchRecycledViewPool>

    @Inject
    lateinit var mainScreenFactory: MainScreenFactory

    @Inject
    lateinit var adLoader: AppAdLoader

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AppViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: Task<ReviewInfo>? = null
    private val navigator: Navigator =
        AppNavigator(this, R.id.appFragmentContainer, supportFragmentManager)

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.appFragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        reviewManager = ReviewManagerFactory.create(this)
        adLoader.onCreate(this)
        setContentView(R.layout.layout_fragemnt_container)

        splashScreen.setOnExitAnimationListener { splashProvider ->
            lifecycleScope.launch {
                viewPools.forEach { it.prefetch() }
                waitOnLoaded(splashProvider)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adLoader.onDestroy(this)
    }

    private suspend fun waitOnLoaded(splashProvider: SplashScreenViewProvider) {
        while (!viewModel.isReady) {
            delay(50)
        }
        if (currentFragment == null) newRootScreen()
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

    fun showRateDialog() {
        if (settingManager.needShowRateRequest() && reviewInfo == null) {
            reviewInfo = reviewManager.requestReviewFlow().apply {
                addOnCompleteListener { request ->
                    Timber.i("requestReviewFlow isSuccessful = ${request.isSuccessful}")
                    val result = request.result
                    if (request.isSuccessful && result != null)
                        showReviewDialog(result)
                    else {
                        Timber
                            .e("requestReviewFlow error = ${request.exception?.message ?: "null"}")
                        reviewInfo = null
                    }
                }
            }
        }
    }

    private fun showReviewDialog(reviewInfo: ReviewInfo) {
        val flow = reviewManager.launchReviewFlow(this@AppActivity, reviewInfo)
        flow.addOnCompleteListener {
            Timber.i("launchReviewFlow isSuccessful = ${it.isSuccessful}")
            if (it.isSuccessful)
                settingManager.onRateRequestShow()
            else
                Timber.e("launchReviewFlow error = ${it.exception?.message ?: "null"}")
            this.reviewInfo = null
        }
    }
}
