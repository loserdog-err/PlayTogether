package com.chenantao.playtogether;

import android.content.Intent;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chenantao.playtogether.mvc.controller.MainController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.invitation.HomeActivity;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{

	@Inject
	public MainController mController;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_main;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	public void query(View view)
	{
		User user = AVUser.getCurrentUser(User.class);
		AVQuery<Invitation> inviteQuery = AVQuery.getQuery(Invitation.class);
		AVQuery<Invitation> beInvitedQuery = AVQuery.getQuery(Invitation.class);
		inviteQuery.whereEqualTo(Invitation.FIELD_AUTHOR, user);
		beInvitedQuery.whereEqualTo(Invitation.FIELD_ACCEPT_INVITE_USERS, user);
		List<AVQuery<Invitation>> queries = new ArrayList<AVQuery<Invitation>>();
		queries.add(inviteQuery);
		queries.add(beInvitedQuery);
		AVQuery mainQuery = AVQuery.or(queries);
		mainQuery.setLimit(10);
		mainQuery.orderByDescending(Invitation.UPDATED_AT);
		mainQuery.findInBackground(new FindCallback<Invitation>()
		{
			@Override
			public void done(List<Invitation> list, AVException e)
			{
				Logger.e("list size:" + list.size());
			}
		});
	}
}
