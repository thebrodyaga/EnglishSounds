package com.thebrodyaga.base.navigation.impl

import androidx.fragment.app.Fragment
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.englishsounds.base.di.findComponent

interface GetRouter {

    val fragment: Fragment

    /**
     * Ищет первый роутер FlowFragment
     * Идет по родительским фрагментам пока не дойдет до первого FlowFragment
     * Иначе null значит не вложен во FlowFragment
     */
    fun getFlowRouter(): Router? {
        /*var parentFragment: Fragment = fragment.parentFragment ?: return null
        while (true) {
            if (parentFragment is FlowFragment) {
                return parentFragment.localRouter
            }
            parentFragment = parentFragment.parentFragment ?: return null
        }*/
        return null
    }

    /**
     * Ищет первый роутер и так до глобального
     */
    fun getAnyRouter(): Router {
        return getFlowRouter() ?: getTabRouter() ?: fragment.findComponent().getRouter()
    }

    /**
     * Глобальный роутер, активити роутер
     */
    fun getGlobalRouter(): Router {
        return fragment.findComponent().getRouter()
    }

    /**
     * Ищет роутер TabContainerFragment
     * Идет по родительским фрагментам пока не дойдет до первого TabContainerFragment
     * Иначе null значит не вложен в TabContainerFragment
     */
    fun getTabRouter(): Router? {
        /*var parentFragment: Fragment = fragment.parentFragment ?: return null
        while (true) {
            if (parentFragment is TabContainerFragment) {
                return parentFragment.localRouter
            }
            parentFragment = parentFragment.parentFragment ?: return null
        }*/
        return null
    }
}