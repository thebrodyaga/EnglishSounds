package com.thebrodyaga.infrastructure.featureToggle.api

interface FeatureToggleManager {

    fun featureOne(): FeatureToggle.Default

    fun featureTwo(): FeatureToggle.Loadable
}