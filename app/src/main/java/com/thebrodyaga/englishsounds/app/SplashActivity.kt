package com.thebrodyaga.englishsounds.app

import android.content.Intent
import android.os.Bundle
import com.thebrodyaga.englishsounds.repository.SoundsRepository
import com.thebrodyaga.englishsounds.screen.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import timber.log.Timber
import javax.inject.Inject


class SplashActivity : BaseActivity(), SplashView {
    override fun forward() {
        val intent = Intent(this, AppActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
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
    }
}

@InjectViewState
class SplashPresenter @Inject constructor(
    private val soundsRepository: SoundsRepository
) : BasePresenter<SplashView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        unSubscribeOnDestroy(
            soundsRepository.tryCopySounds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ viewState.forward() }, {
                    Timber.e(it)
                    viewState.error()
                })
        )
    }
}

interface SplashView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun forward()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun error()
}
