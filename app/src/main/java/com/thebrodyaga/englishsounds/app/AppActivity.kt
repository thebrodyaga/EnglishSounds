package com.thebrodyaga.englishsounds.app

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.BuildConfig
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.navigation.RouterTransition
import com.thebrodyaga.englishsounds.navigation.Screens
import com.thebrodyaga.englishsounds.navigation.TransitionNavigator
import com.thebrodyaga.englishsounds.screen.base.BaseFragment
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import com.thebrodyaga.englishsounds.screen.dialogs.RateAppDialog
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.englishsounds.screen.isSystemDarkMode
import com.thebrodyaga.englishsounds.tools.AudioPlayer
import com.thebrodyaga.englishsounds.tools.RecordVoice
import com.thebrodyaga.englishsounds.tools.SettingManager
import moxy.InjectViewState
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject

class AppActivity : BaseActivity(), AppActivityView {

    @Inject
    lateinit var router: RouterTransition

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
    private val navigator: Navigator =
        TransitionNavigator(this, supportFragmentManager, R.id.fragment_container)

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
        if (settingManager.needShowRateRequest() &&
            supportFragmentManager.findFragmentByTag(RateAppDialog.TAG) == null
        ) {
            RateAppDialog().showNow(supportFragmentManager, RateAppDialog.TAG)
            settingManager.onRateRequestShow()
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
