package com.chenantao.playtogether.injector.component;

import com.chenantao.playtogether.MainActivity;
import com.chenantao.playtogether.chat.mvc.view.activity.AddFriendActivity;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatHomeActivity;
import com.chenantao.playtogether.chat.mvc.view.activity.CreateDiscussGroupActivity;
import com.chenantao.playtogether.injector.MyScope;
import com.chenantao.playtogether.injector.modules.ActivityModule;
import com.chenantao.playtogether.mvc.view.activity.invitation.HomeActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationCategoryActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.PostInvitationActivity;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.mvc.view.activity.user.PersonalCenterActivity;
import com.chenantao.playtogether.mvc.view.activity.user.RegisterActivity;
import com.chenantao.playtogether.mvc.view.common.WriteDataActivity;

import dagger.Component;

/**
 * Created by Administrator on 2016/1/11.
 */
@MyScope
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent
{
	void inject(MainActivity mainActivity);

	void inject(RegisterActivity registerActivity);

	void inject(LoginActivity loginActivity);

	void inject(HomeActivity homeActivity);

	void inject(PostInvitationActivity postInvitationActivity);

	void inject(InvitationDetailActivity invitationDetailActivity);

	void inject(InvitationCategoryActivity invitationCategoryActivity);

	void inject(PersonalCenterActivity personalCenterActivity);

	void inject(WriteDataActivity writeDataActivity);

	/*聊天模块*/
	void inject(AddFriendActivity addFriendActivity);

	void inject(ChatActivity chatActivity);

	void inject(ChatHomeActivity chatHomeActivity);

	void inject(CreateDiscussGroupActivity createDiscussGroupActivity);

}
