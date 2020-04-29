package com.thebrodyaga.englishsounds.screen.base

import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.navigation.RouterTransition
import com.thebrodyaga.englishsounds.navigation.TransitionNavigator
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Navigator

/**
 * фрагмент контейнер для вложенной навигации, тут храниться локальный роутер,
 * тут скоупы открыать и закрывать
 */
abstract class FlowFragment : BaseFragment() {

    private val navigator: Navigator by lazy {
        object : TransitionNavigator(requireActivity(), childFragmentManager, getContainerId()) {
            /**
             * корректный выход из FlowFragment
             * если в текущем стеке пусто, то ищет любой роутер выше и там выходит
             */
            override fun fragmentBack() {
                if (childFragmentManager.backStackEntryCount > 0)
                    super.fragmentBack()
                else getAnyRouter().exit()
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
        getCicerone().navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        getCicerone().navigatorHolder.removeNavigator()
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