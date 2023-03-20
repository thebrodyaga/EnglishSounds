package com.thebrodyaga.feature.appActivity.impl

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.color.DynamicColors
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.core.navigation.impl.cicerone.AppNavigator
import com.thebrodyaga.core.uiUtils.isSystemDarkMode
import com.thebrodyaga.englishsounds.base.app.BaseActivity
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.appActivity.impl.di.AppActivityComponent
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenAction
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.setting.api.SettingManager
import timber.log.Timber
import javax.inject.Inject

class AppActivity : BaseActivity() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var recordVoice: RecordVoice

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    lateinit var mainScreenFactory: MainScreenFactory

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: Task<ReviewInfo>? = null
    private val navigator: Navigator =
        AppNavigator(this, R.id.fragment_container, supportFragmentManager)

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this);
        AppActivityComponent.factory(findDependencies()).inject(this)
        isLightSystem(isSystemDarkMode())
        super.onCreate(savedInstanceState)
        reviewManager = ReviewManagerFactory.create(this)
        setContentView(R.layout.layout_fragemnt_container)
        if (currentFragment == null)
            router.newRootScreen(mainScreenFactory.mainScreen())
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
        recordVoice.onAppShow()
        audioPlayer.onAppShow()
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
        recordVoice.onAppHide()
        audioPlayer.onAppHide()
    }

    override fun onBackPressed() {
        val currentFragment = this.currentFragment

        if (currentFragment is BaseFragment)
            currentFragment.onBackPressed()
        else
            super.onBackPressed()
    }

    private fun isLightSystem(isDarkTheme: Boolean?) {
        if (isDarkTheme == null)
            return
//        window.statusBarColor = primaryDark
//        window.navigationBarColor = if (!isDarkTheme) navigationBar else primaryDark

        val view = window.decorView

        var result = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result =
                if (!isDarkTheme)
                    view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else
                    view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result =
                if (!isDarkTheme)
                    result or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                else
                    result and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        view.systemUiVisibility = result
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

    fun toggleFabMic(isShow: Boolean?, autoHide: Boolean?) {
        val mainScreen = mainScreenFactory.mainScreen()
        (supportFragmentManager.fragments.find { it.tag == mainScreen.screenKey } as? MainScreenAction)
            ?.toggleFabMic(isShow, autoHide)
    }
}
