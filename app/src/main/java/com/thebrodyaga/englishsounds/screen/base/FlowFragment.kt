package com.thebrodyaga.englishsounds.screen.base

import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import com.thebrodyaga.core.navigation.api.cicerone.Navigator
import com.thebrodyaga.core.navigation.impl.cicerone.AppNavigator
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.navigation.RouterTransition

/**
 * фрагмент контейнер для вложенной навигации, тут храниться локальный роутер,
 * тут скоупы открыать и закрывать
 */
abstract class FlowFragment : BaseFragment() {

    private val navigator: Navigator by lazy {
        object : AppNavigator(requireActivity(), getContainerId(), childFragmentManager) {
            /**
             * корректный выход из FlowFragment
             * если в текущем стеке пусто, то ищет любой роутер выше и там выходит
             */
            override fun activityBack() {
                this@FlowFragment.parentFragmentManager.popBackStack()
            }
        }
    }

    protected open val currentFragment: BaseFragment?
        get() = childFragmentManager.findFragmentById(getContainerId()) as? BaseFragment

    protected abstract fun getContainerId(): Int

    /**
     * ключ для хранения локальных роутеров, следить за уникальностью
     */
    protected abstract fun getContainerName(): String

    private fun getCiceroneHolder() = App.appComponent.getLocalCiceroneHolder()

    val localRouter: RouterTransition get() = getCicerone().router

    private fun getCicerone(): Cicerone<RouterTransition> =
        getCiceroneHolder().getCicerone(getContainerName())

    override fun onResume() {
        super.onResume()
        getCicerone().getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        getCicerone().getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: localRouter.exit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        currentFragment?.apply { onHiddenChanged(hidden) }
    }
}