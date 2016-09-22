package com.chenantao.playtogether.injector.modules;

import com.chenantao.playtogether.chat.mvc.bll.ChatBll;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
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
	private static ConversationBll mConversationBll;
	private static ChatBll mChatBll;

	static
	{
		mUserBll = new UserBll();
		mInviteBll = new InviteBll();
		mConversationBll = new ConversationBll();
		mChatBll = new ChatBll();
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

	@Singleton
	@Provides
	public ConversationBll providesConvetsationBll()
	{
		return mConversationBll;
	}

	@Singleton
	@Provides
	public ChatBll providesChatBll()
	{
		return mChatBll;
	}
}
