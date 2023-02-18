package com.thebrodyaga.englishsounds.app

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.core.navigation.impl.cicerone.AppNavigator
import com.thebrodyaga.data.sounds.impl.setting.SettingManager
import com.thebrodyaga.englishsounds.BuildConfig
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.englishsounds.screen.isSystemDarkMode
import com.thebrodyaga.feature.audioPlayer.impl.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.impl.RecordVoice
import moxy.InjectViewState
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import timber.log.Timber
import javax.inject.Inject

class AppActivity : BaseActivity(), AppActivityView {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var recordVoice: RecordVoice

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var settingManager: SettingManager

    @Inject
    @InjectPresenter
    lateinit var presenter: AppActivityPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: Task<ReviewInfo>? = null
    private val navigator: Navigator =
        AppNavigator(this, R.id.fragment_container, supportFragmentManager)

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)

    private var navigationBar: Int = 0
    private var primaryDark: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        navigationBar = ContextCompat.getColor(this, R.color.navigation_bar)
        primaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        App.appComponent.inject(this)
        isLightSystem(isSystemDarkMode())
        super.onCreate(savedInstanceState)
        reviewManager = ReviewManagerFactory.create(this)
        if (BuildConfig.DEBUG)
            supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifecycle(), true)
        setContentView(R.layout.layout_fragemnt_container)
        if (currentFragment == null)
            router.newRootScreen(Screens.MainScreen)
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
        window.statusBarColor = primaryDark
        window.navigationBarColor = if (!isDarkTheme) navigationBar else primaryDark

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

    override fun showRateDialog() {
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

    fun onSoundScreenClose() {
        presenter.onSoundScreenClose()
    }

    fun toggleFabMic(isShow: Boolean?, autoHide: Boolean?) {
        (supportFragmentManager.fragments.find { it is MainFragment } as? MainFragment)
            ?.toggleFabMic(isShow, autoHide)
    }
}

interface AppActivityView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showRateDialog()
}

@InjectViewState
class AppActivityPresenter @Inject constructor() : BasePresenter<AppActivityView>() {

    fun onSoundScreenClose() {
        viewState.showRateDialog()
    }
}
