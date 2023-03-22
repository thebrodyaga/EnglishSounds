package com.thebrodyaga.base.navigation.api.router

class TabRouter : AppRouter() {

    fun resetTabStack() = backTo(null)
}