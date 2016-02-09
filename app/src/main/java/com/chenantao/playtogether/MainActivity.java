package com.chenantao.playtogether;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.controller.MainController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{

	@Inject
	public MainController mController;
	private Button mBtn;

	ImageView mIv;

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
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		mBtn = (Button) findViewById(R.id.btn);
		mBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				User currentUser = AVUser.getCurrentUser(User.class);
				Logger.e("curUser:" + currentUser);
			}
		});
//		Logger.e("xixi");
	}
}
