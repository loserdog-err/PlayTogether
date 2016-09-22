package com.chenantao.playtogether.injector.component;

import com.chenantao.playtogether.chat.mvc.view.fragment.ConvListFragment;
import com.chenantao.playtogether.chat.mvc.view.fragment.FriendListFragment;
import com.chenantao.playtogether.injector.MyScope;
import com.chenantao.playtogether.injector.modules.FragmentModule;
import com.chenantao.playtogether.mvc.view.fragment.invitation.InviteConditionFragment;
import com.chenantao.playtogether.mvc.view.fragment.user.PersonalCenterDetailFragment;
import com.chenantao.playtogether.mvc.view.fragment.user.PersonalCenterHomeFragment;

import dagger.Component;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
@MyScope
@Component(dependencies = {ApplicationComponent.class}, modules = {
				FragmentModule.class})
public interface FragmentComponent
{
	void inject(InviteConditionFragment fragment);

	void inject(ConvListFragment convListFragment);

	void inject(FriendListFragment friendListFragment);

	void inject(PersonalCenterHomeFragment personalCenterHomeFragment);

	void inject(PersonalCenterDetailFragment personalCenterDetailFragment);
}
