package com.chenantao.playtogether.injector.component;

import com.chenantao.playtogether.chat.mvc.bll.ChatBll;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.injector.modules.ApiModule;
import com.chenantao.playtogether.injector.modules.ApplicationModule;
import com.chenantao.playtogether.injector.modules.BllModule;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.model.bll.UserBll;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Administrator on 2016/1/11.
 */
@Singleton
@Component(modules = {ApiModule.class, BllModule.class,
				ApplicationModule.class})
public interface ApplicationComponent
{

	UserBll userBll();

	InviteBll inviteBll();

	ConversationBll conversationBll();

	ChatBll chatBll();


}
