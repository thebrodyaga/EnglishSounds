package com.thebrodyaga.feature.appActivity.impl.di

import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.englishsounds.base.di.BaseAppComponent
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import dagger.Component

@[ActivityScope Component(
    dependencies = [BaseAppComponent::class]
)]
interface AppActivityComponent {

    @Component.Builder
    interface Builder {
        fun baseComponent(component: BaseAppComponent): Builder
        fun build(): AppActivityComponent
    }

    fun inject(appActivity: AppActivity)

    companion object {

        fun build(
            baseComponent: BaseAppComponent,
        ): AppActivityComponent {
            return DaggerAppActivityComponent.builder()
                .baseComponent(baseComponent)
                .build()
        }
    }
}