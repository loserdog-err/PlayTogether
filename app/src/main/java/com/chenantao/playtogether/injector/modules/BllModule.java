package com.chenantao.playtogether.injector.modules;

import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.model.bll.UserBll;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2016/1/11.
 */
@Module(includes = {ApiModule.class})
public class BllModule
{

	private static UserBll mUserBll;
	private static InviteBll mInviteBll;

	static
	{
		mUserBll = new UserBll();
		mInviteBll = new InviteBll();
	}

	@Singleton
	@Provides
	public UserBll providesUserBll()
	{
		return mUserBll;
	}

	@Singleton
	@Provides
	public InviteBll providesInviteBll()
	{
		return mInviteBll;
	}
}
