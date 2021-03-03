package br.pucrio.inf.lac.mhub.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Application mApp;

    public ApplicationModule( Application app ) {
        mApp = app;
    }

    @Provides
    @Singleton
    public Context providesApplication(){
        return mApp;
    }
}