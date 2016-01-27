package com.chenantao.playtogether.injector.component;

import com.chenantao.playtogether.injector.MyScope;
import com.chenantao.playtogether.injector.modules.FragmentModule;
import com.chenantao.playtogether.mvc.view.fragment.invitation.InviteConditionFragment;

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
}
