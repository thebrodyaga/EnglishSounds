package com.thebrodyaga.base.navigation.api

import com.thebrodyaga.core.navigation.api.cicerone.Cicerone
import com.thebrodyaga.core.navigation.api.cicerone.CiceroneRouter

class CiceroneHolder {

    private val map = mutableMapOf<String, Cicerone<out CiceroneRouter>>()

    fun <R : CiceroneRouter> getOrCreate(key: String, defaultValue: () -> Cicerone<R>): Cicerone<R> {
        @Suppress("UNCHECKED_CAST")
        return map.getOrPut(key, defaultValue) as Cicerone<R>
    }

    fun clear(key: String) {
        map.remove(key)
    }
}