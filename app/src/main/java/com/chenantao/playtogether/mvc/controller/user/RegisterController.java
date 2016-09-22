package com.chenantao.playtogether.mvc.controller.user;


import android.app.Activity;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.activity.user.RegisterActivity;
import com.chenantao.playtogether.utils.Constant;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/18.
 */
public class RegisterController
{
	@Inject
	public UserBll mUserBll;
	private RegisterActivity mActivity;

	@Inject
	public RegisterController(Activity mActivity)
	{
		this.mActivity = (RegisterActivity) mActivity;
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

	/**
	 * 注册用户
	 * 注册前显式ProgressDialog，注册完成隐藏ProgressDialog并跳转
	 *
	 * @param user
	 */
	public void registerUser(User user)
	{
		mUserBll.registerUser(user)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<User>()
				{
					@Override
					public void call(User user)
					{
						//注册成功
						mActivity.registerSuccess(user);
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						//注册失败
						mActivity.registerError();
					}
				});
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
						//登录成功
						mActivity.loginSuccess(user);
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						//登录失败
						mActivity.loginError();
					}
				});
	}
}
