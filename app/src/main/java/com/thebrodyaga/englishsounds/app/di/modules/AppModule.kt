package com.thebrodyaga.englishsounds.app.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thebrodyaga.core.utils.coroutines.AppDispatchers
import com.thebrodyaga.core.utils.coroutines.AppScope
import com.thebrodyaga.core.utils.coroutines.DefaultDispatchers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun appScope(): AppScope = AppScope()

    @Provides
    @Singleton
    fun appDispatchers(): AppDispatchers = DefaultDispatchers()

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun provideAppSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE)
}