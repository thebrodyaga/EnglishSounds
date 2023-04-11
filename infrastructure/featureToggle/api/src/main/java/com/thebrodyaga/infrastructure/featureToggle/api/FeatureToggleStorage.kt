package com.thebrodyaga.infrastructure.featureToggle.api

interface FeatureToggleStorage {

    fun updateValue(value: Boolean, key: FeatureToggleKey)
}