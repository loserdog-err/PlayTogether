package com.chenantao.playtogether;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chenantao.playtogether.mvc.controller.MainController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
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
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000);

	}

	public void query(View view)
	{
		User user = AVUser.getCurrentUser(User.class);
		AVQuery<User> query = AVUser.getQuery(User.class);
		List<String> friends = new ArrayList<String>();
		for (User friend : user.getFriends())
		{
			friends.add(friend.getUsername());
		}
		query.whereNotContainedIn(User.FIELD_USERNAME, friends);//排除掉已经是好友的人
		query.whereEqualTo(User.FIELD_GENDER_TREND, user.getGenderTrend());
		query.whereEqualTo(User.FIELD_FAVORITE_ACTIVITY, user.getFavoriteActivity());
		query.findInBackground(new FindCallback<User>()
		{
			@Override
			public void done(List<User> list, AVException e)
			{
				Logger.e("list size:" + list.size());
			}
		});
	}
}
