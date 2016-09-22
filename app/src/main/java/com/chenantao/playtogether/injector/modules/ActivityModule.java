package com.chenantao.playtogether.injector.modules;

import android.app.Activity;

import com.chenantao.playtogether.injector.MyScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2016/1/11.
 */
@Module
public class ActivityModule
{
	private Activity mActivity;

	public ActivityModule(Activity activity)
	{
		mActivity = activity;
	}

	@MyScope
	@Provides
	public Activity providesActivity()
	{
		return mActivity;
	}

}
