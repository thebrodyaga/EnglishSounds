package com.thebrodyaga.englishsounds.di.modules;

import com.thebrodyaga.base.navigation.api.LocalCiceroneHolder;
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone;
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder;
import com.thebrodyaga.core.navigation.api.cicerone.Router;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class
NavigationModule {
    private Cicerone<Router> cicerone;

    public NavigationModule() {
        cicerone = Cicerone.create(new Router());
    }

    @Provides
    @Singleton
    Router provideRouter() {
        return cicerone.getRouter();
    }

    @Provides
    @Singleton
    NavigatorHolder provideNavigatorHolder() {
        return cicerone.getNavigatorHolder();
    }

    @Provides
    @Singleton
    LocalCiceroneHolder provideLocalNavigationHolder() {
        return new LocalCiceroneHolder();
    }
}
