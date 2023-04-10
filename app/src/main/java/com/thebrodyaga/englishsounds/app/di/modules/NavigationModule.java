package com.thebrodyaga.englishsounds.app.di.modules;

import com.thebrodyaga.base.navigation.api.CiceroneHolder;
import com.thebrodyaga.base.navigation.api.router.AppRouter;
import com.thebrodyaga.core.navigation.api.cicerone.Cicerone;
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigationModule {
    private Cicerone<AppRouter> cicerone;

    public NavigationModule() {
        cicerone = Cicerone.create(new AppRouter());
    }

    @Provides
    @Singleton
    AppRouter appRouter() {
        return cicerone.getRouter();
    }

    @Provides
    @Singleton
    NavigatorHolder provideNavigatorHolder() {
        return cicerone.getNavigatorHolder();
    }

    @Provides
    @Singleton
    CiceroneHolder ciceroneHolder() {
        return new CiceroneHolder();
    }
}
