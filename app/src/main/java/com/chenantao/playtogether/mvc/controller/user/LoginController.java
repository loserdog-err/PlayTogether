package com.chenantao.playtogether.mvc.controller.user;

import android.app.Activity;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.utils.Constant;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class LoginController
{
	private LoginActivity mActivity;

	@Inject
	public UserBll mUserBll;

	@Inject
	public LoginController(Activity activity)
	{
		mActivity = (LoginActivity) activity;
	}

	public boolean checkUsername(String username)
	{
		if (!mUserBll.checkUsername(username))
		{
			//校验不通过，设置错误信息
			mActivity.setUsernameError("用户名长度必须在" + Constant.USERNAME_MIN_LENGTH + "到" +
					Constant.USERNAME_MAX_LENGTH + "之间");
			return false;
		}
		return true;
	}

	public boolean checkPassword(String password)
	{
		if (!mUserBll.checkPassword(password))
		{
			mActivity.setPasswordError("密码长度必须在" + Constant.PASSWORD_MIN_LENGTH + "到" +
					Constant.PASSWORD_MAX_LENGTH + "之间");
			return false;
		}
		return true;

	}

	public void login(User user)
	{
		mUserBll.login(user)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<AVUser>()
				{
					@Override
					public void call(AVUser user)
					{
						mActivity.loginSuccess(user);
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						Logger.e("登录失败:" + throwable.getMessage());
						mActivity.loginError();
					}
				});
	}
}
