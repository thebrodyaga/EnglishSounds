package com.thebrodyaga.englishsounds.base.app

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

abstract class BasePresenter<View : MvpView> : MvpPresenter<View>(), CoroutineScope {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main

    protected fun unSubscribeOnDestroy(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    open fun onBackPressed() {}

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        coroutineContext.cancelChildren()
    }
}