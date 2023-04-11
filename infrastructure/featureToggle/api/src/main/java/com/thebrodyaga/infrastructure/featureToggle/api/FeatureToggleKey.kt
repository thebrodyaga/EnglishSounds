package com.thebrodyaga.infrastructure.featureToggle.api

// di maybe
val featureToggleKeys = listOf(
    FeatureToggleKey.FeatureOne,
    FeatureToggleKey.FeatureTwo,
)

sealed interface FeatureToggleKey {
    val key: String
    val defaultValues: Boolean

    sealed class Default(
        override val key: String,
        override val defaultValues: Boolean,
    ) : FeatureToggleKey

    object FeatureOne : Default("awesome_feature_one", false)

    sealed class Loadable(
        override val key: String,
        override val defaultValues: Boolean,
    ) : FeatureToggleKey

    object FeatureTwo : Loadable("awesome_feature_two", false)
}