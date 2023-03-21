package com.thebrodyaga.englishsounds.base.app

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import com.thebrodyaga.base.navigation.api.AppRouter
import com.thebrodyaga.base.navigation.impl.routerProvider
import com.thebrodyaga.core.uiUtils.insets.appleInsetPadding
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatFragment

abstract class ScreenFragment(layoutId: Int) : MvpAppCompatFragment(layoutId) {

    val appRouter: AppRouter by lazy {
        findDependencies<AppDependencies>().appRouter()
    }

    protected val routerProvider by lazy { routerProvider(appRouter) }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Deprecated("")
    fun getAnyRouter() = routerProvider.anyRouter

    @Deprecated("")
    protected fun unSubscribeOnDestroy(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyWindowInsets(view)
        registerBackPressedDispatcher()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        // in case of show/hide on main tab
        if (!hidden) registerBackPressedDispatcher()
    }

    protected open fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { view, insets, _ ->
            insets.systemAndIme().consume {
                view.appleInsetPadding(left = left, top = top, right = right, bottom = bottom)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun registerBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback { onBackPressed() }
    }

    open fun onBackPressed() {
        routerProvider.anyRouter.exit()
    }
}