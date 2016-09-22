package com.chenantao.playtogether.mvc.controller.user;

import android.app.Activity;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.activity.user.PersonalCenterActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public class PersonalCenterController
{
	private PersonalCenterActivity mActivity;

	@Inject
	UserBll mUserBll;

	@Inject
	public PersonalCenterController(Activity activity)
	{
		this.mActivity = (PersonalCenterActivity) activity;
	}

	public void getUserInfo(String userId)
	{
		User currentUser = AVUser.getCurrentUser(User.class);
		if (userId.equals(currentUser.getObjectId()))
		{
			mActivity.getUserInfoSuccess(currentUser);
			return;
		}
		mUserBll.getUserById(userId)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Action1<User>()
						{
							@Override
							public void call(User user)
							{
								mActivity.getUserInfoSuccess(user);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
								mActivity.getUserInfoFail("获取用户信息失败");
							}
						});
	}
}
