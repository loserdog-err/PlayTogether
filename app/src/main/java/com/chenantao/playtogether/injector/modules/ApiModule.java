package com.chenantao.playtogether.injector.modules;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2016/1/11.
 */
@Module
public class ApiModule
{

	@Singleton
	@Provides
	public Gson prividesGson()
	{
		return new Gson();
	}
}
