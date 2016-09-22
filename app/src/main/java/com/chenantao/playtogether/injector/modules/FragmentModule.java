package com.chenantao.playtogether.injector.modules;

import android.support.v4.app.Fragment;

import com.chenantao.playtogether.injector.MyScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
@Module
public class FragmentModule
{
	private Fragment mFragment;

	public FragmentModule(Fragment fragment)
	{
		mFragment = fragment;
	}

	@MyScope
	@Provides
	public Fragment providesFragment()
	{
		return mFragment;
	}
}
