package com.thebrodyaga.infrastructure.featureToggle.impl

import com.thebrodyaga.infrastructure.featureToggle.api.*

class FeatureToggleManagerImpl : FeatureToggleManager, FeatureToggleStorage {

    private val toggles: Map<String, MutableFeatureToggle> by lazy {
        val result = mutableMapOf<String, MutableFeatureToggle>()
        featureToggleKeys.forEach { key ->
            result[key.key] = key.createMutable()
        }
        result
    }

    override fun featureOne(): FeatureToggle.Default {
        return FeatureToggleKey.FeatureOne.getFT()
    }

    override fun featureTwo(): FeatureToggle.Loadable {
        return FeatureToggleKey.FeatureTwo.getFT()
    }

    override fun updateValue(value: Boolean, key: FeatureToggleKey) {
        val featureToggle: MutableFeatureToggle = key.getFT()
        featureToggle.mutableFlow.tryEmit(value)
    }

    private inline fun <reified FT> FeatureToggleKey.getFT(): FT where FT : FeatureToggle, FT : MutableFeatureToggle {
        return toggles[this.key] as FT
    }
}

private fun FeatureToggleKey.createMutable(): MutableFeatureToggle {
    return when (this) {
        is FeatureToggleKey.Default -> MutableDefaultFT(this.defaultValues, this)
        is FeatureToggleKey.Loadable -> MutableLoadableFT(this)
    }
}