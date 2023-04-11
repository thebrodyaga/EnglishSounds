package com.thebrodyaga.infrastructure.featureToggle.api

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface FeatureToggle {

    /**
     * [flow] with initial value [FeatureToggleKey.Default.key]
     */
    interface Default : FeatureToggle {
        val key: FeatureToggleKey.Default
        val flow: StateFlow<Boolean>
    }

    /**
     * [flow] without initial value
     * Need to put [FeatureToggleStorage.updateValue]
     * RemoteConfig, LocalSetting or someone else
     */
    interface Loadable : FeatureToggle {
        val key: FeatureToggleKey.Loadable
        val flow: SharedFlow<Boolean>
    }
}