package com.chenantao.playtogether.mvc.controller.user;

import android.support.v4.app.Fragment;

import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.fragment.user.PersonalCenterDetailFragment;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/2/10.
 *
 */
public class PersonalCenterDetailController
{
	private PersonalCenterDetailFragment mFragment;

	@Inject
	UserBll mUserBll;

	@Inject
	public PersonalCenterDetailController(Fragment fragment)
	{
		this.mFragment = (PersonalCenterDetailFragment) fragment;
	}

	public void updateUser(User user)
	{
		mUserBll.updateUser(user)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Void>()
						{
							@Override
							public void onCompleted()
							{
								mFragment.updateUserSuccess();
							}

							@Override
							public void onError(Throwable e)
							{
								e.printStackTrace();
							}

							@Override
							public void onNext(Void aVoid)
							{
								mFragment.updateUserFail();
							}
						});
	}

}
