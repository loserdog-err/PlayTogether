package com.chenantao.playtogether.injector.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2016/1/11.
 */
@Module
public class ApplicationModule
{
	private Application mApplication;

	public ApplicationModule(Application application)
	{
		this.mApplication = application;
	}

	@Singleton
	@Provides
	public Application providesApplication()
	{
		return mApplication;
	}
}
