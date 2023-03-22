package com.thebrodyaga.core.navigation.api.cicerone

/**
 * Cicerone is the holder for other library components.
 *
 * To use it, instantiate it using one of the {@link #create()} methods.
 *
 * When you need a [NavigatorHolder] or router, get it here.
 *
 * @param router type of router. You can use the default [CiceroneRouter] or pass your own [BaseRouter] implementation.
 */
class Cicerone<T : BaseRouter> private constructor(val router: T) {

    fun getNavigatorHolder(): NavigatorHolder = router.commandBuffer

    companion object {
        /**
         * Creates the Cicerone instance with the default [CiceroneRouter]
         */
        @JvmStatic
        fun create() = create(CiceroneRouter())

        /**
         * Creates the Cicerone instance with the custom router.
         * @param customRouter the custom router extending [BaseRouter]
         */
        @JvmStatic
        fun <T : BaseRouter> create(customRouter: T) = Cicerone(customRouter)
    }
}
