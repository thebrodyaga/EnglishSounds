package com.thebrodyaga.englishsounds.di.modules;

import com.thebrodyaga.core.navigation.api.cicerone.Cicerone;
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder;
import com.thebrodyaga.core.navigation.api.cicerone.Router;
import com.thebrodyaga.englishsounds.navigation.LocalCiceroneHolder;
import com.thebrodyaga.englishsounds.navigation.RouterTransition;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class
NavigationModule {
    private Cicerone<RouterTransition> cicerone;

    public NavigationModule() {
        cicerone = Cicerone.create(new RouterTransition());
    }

    @Provides
    @Singleton
    RouterTransition provideTransitionRouter() {
        return cicerone.getRouter();
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
