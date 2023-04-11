package com.thebrodyaga.infrastructure.featureToggle.impl

import com.thebrodyaga.infrastructure.featureToggle.api.FeatureToggle
import com.thebrodyaga.infrastructure.featureToggle.api.FeatureToggleKey
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface MutableFeatureToggle {
    val mutableFlow: MutableSharedFlow<Boolean>
}

internal class MutableDefaultFT(
    defaultValue: Boolean,
    override val key: FeatureToggleKey.Default,
) : FeatureToggle.Default, MutableFeatureToggle {
    override val mutableFlow: MutableStateFlow<Boolean> = MutableStateFlow(defaultValue)
    override val flow: StateFlow<Boolean> = mutableFlow
}

internal class MutableLoadableFT(
    override val key: FeatureToggleKey.Loadable,
) : FeatureToggle.Loadable, MutableFeatureToggle {
    override val mutableFlow: MutableSharedFlow<Boolean> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val flow: SharedFlow<Boolean> = mutableFlow
}