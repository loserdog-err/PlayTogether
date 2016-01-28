package com.chenantao.playtogether.mvc.model.bll;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.Constant;

import java.io.File;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Chenantao_gg on 2016/1/17.
 */
public class UserBll
{
	public boolean checkUsername(String username)
	{
		if (username.length() >= Constant.USERNAME_MIN_LENGTH && username.length() <=
				Constant.PASSWORD_MAX_LENGTH)
		{
			//校验通过
			return true;
		} else
		{
			//校验失败
			return false;
		}
	}

	public boolean checkPassword(String password)
	{
		if (password.length() >= Constant.PASSWORD_MIN_LENGTH && password.length() <=
				Constant.PASSWORD_MAX_LENGTH)
		{
			//校验通过
			return true;
		} else
		{
			//校验失败
			return false;
		}
	}

	public Observable<User> registerUser(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<User>()
		{
			@Override
			public void call(Subscriber<? super User> subscriber)
			{
				try
				{
					AVUser avUser = new AVUser();
					avUser.setUsername(user.getUsername());
					avUser.setPassword(user.getPassword());
					avUser.put(User.FIELD_GENDER, user.getGender());
					avUser.signUp();
					//注册成功
					subscriber.onNext(user);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});

	}

	public Observable<AVUser> login(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<AVUser>()
		{
			@Override
			public void call(Subscriber<? super AVUser> subscriber)
			{
				try
				{
					AVUser avUser = AVUser.logIn(user.getUsername(), user.getPassword());
					subscriber.onNext(avUser);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	public Observable<String> uploadAvatar(final String path)
	{
		return Observable.create(new Observable.OnSubscribe<String>()
		{
			@Override
			public void call(Subscriber<? super String> subscriber)
			{
				File file = new File(path);
				try
				{
					AVFile avatar = AVFile.withFile(file.getName(), file);
					avatar.save();
					User user = AVUser.getCurrentUser(User.class);
					user.setAvatar(avatar);
					user.save();
					subscriber.onNext(path);
				} catch (Exception e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	public Observable<Void> updateLocation(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<Void>()
		{
			@Override
			public void call(Subscriber<? super Void> subscriber)
			{
				try
				{
					user.save();
					subscriber.onNext(null);
				} catch (AVException e)
				{
					subscriber.onError(e);
					e.printStackTrace();
				}
			}
		});
	}
}
