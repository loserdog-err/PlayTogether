package com.chenantao.playtogether;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.chenantao.playtogether.mvc.controller.MainController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.List;

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
		mIv = (ImageView)  findViewById(R.id.iv);
		mBtn = (Button) findViewById(R.id.btn);
		mBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AVQuery query = AVQuery.getQuery(Invitation.class);
//				AVQuery<AVObject> query = new AVQuery<AVObject>("invitation");
				query.include(Invitation.FIELD_ACCEPT_INVITE_USERS);
//				query.orderByDescending("createdAt");
				query.getInBackground("56a7114dd342d3005414c898", new GetCallback<Invitation>()
				{
					@Override
					public void done(Invitation avObject, AVException e)
					{
//						ArrayList acceptInviteUsers = (ArrayList) avObject.get
//								(Invitation.FIELD_ACCEPT_INVITE_USERS);
						List<User> list = avObject.getAcceptInviteUsers();
						User user = list.get(0);
						Logger.e("user:" + user.getUsername());
					}
				});
//				query.findInBackground(new FindCallback<AVObject>()
//				{
//					@Override
//					public void done(List<AVObject> list, AVException e)
//					{
//						AVObject avObject = list.get(0);
//						ArrayList arrayList = (ArrayList) avObject.get("acceptInviteUsers");
//						AVUser avUser = (AVUser) arrayList.get(0);
//						Logger.e("user:" + avUser.getUsername());
//					}
//				});
			}

		});
	}
}
