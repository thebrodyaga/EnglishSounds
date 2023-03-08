package com.thebrodyaga.englishsounds.app

import android.content.Intent
import android.os.Bundle
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.app.BaseActivity
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion

class SplashActivity : BaseActivity(), SplashView {
    override fun forward() {
        val intent = Intent(this, AppActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun error() {
        finish()
    }

    @Inject
    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}

@InjectViewState
class SplashPresenter @Inject constructor(
    private val soundsRepository: SoundsRepository
) : MvpPresenter<SplashView>(), CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        soundsRepository.tryCopySounds()
            .flowOn(Dispatchers.IO)
            .onCompletion { error ->
                if (error == null)
                    viewState.forward()
                else {
                    Timber.e(error)
                    viewState.error()
                }
            }
            .launchIn(this)
    }
}

interface SplashView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun forward()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun error()
}
