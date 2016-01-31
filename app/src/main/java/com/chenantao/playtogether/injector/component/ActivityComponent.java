package com.chenantao.playtogether.injector.component;

import com.chenantao.playtogether.MainActivity;
import com.chenantao.playtogether.chat.ChatActivity;
import com.chenantao.playtogether.injector.MyScope;
import com.chenantao.playtogether.injector.modules.ActivityModule;
import com.chenantao.playtogether.mvc.view.activity.invitation.HomeActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationCategoryActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.PostInvitationActivity;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.mvc.view.activity.user.RegisterActivity;

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

	void inject(ChatActivity chatActivity);
}
