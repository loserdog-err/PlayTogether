package com.chenantao.playtogether;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chenantao.playtogether.mvc.controller.MainController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
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
		mIv = (ImageView) findViewById(R.id.iv);
		mBtn = (Button) findViewById(R.id.btn);
		mBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AVQuery query = AVQuery.getQuery(User.class);
				String[] genders = new String[]{"0"};
				query.whereContainedIn("gender", Arrays.asList(genders));
//				AVQuery<AVObject> query = new AVQuery<AVObject>("invitation");
				query.findInBackground(new FindCallback<User>()
				{
					@Override
					public void done(List<User> list, AVException e)
					{
						if (e == null)
						{
							Logger.e("success");
							for (int i = 0; i < list.size(); i++)
							{
								Logger.e("gender:" + list.get(i).getGender());
							}
						} else
						{
							e.printStackTrace();
						}
					}
				});
			}

		});
	}
}
