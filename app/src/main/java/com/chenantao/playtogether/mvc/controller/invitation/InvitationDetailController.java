package com.chenantao.playtogether.mvc.controller.invitation;

import android.app.Activity;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/25.
 */
public class InvitationDetailController
{
	public InvitationDetailActivity mActivity;
	@Inject
	public InviteBll mInviteBll;

	@Inject
	public InvitationDetailController(Activity activity)
	{
		mActivity = (InvitationDetailActivity) activity;
	}

	public void loadData(String invitationId)
	{
		mInviteBll
						.getInvitationById(invitationId)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<Invitation>()
						{
							@Override
							public void call(Invitation invitation)
							{
								mActivity.loadTextDataSuccess(invitation);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
								mActivity.loadDataFail("似乎出了点问题，请检查您的网络是否通畅");
							}
						});

	}

	/**
	 * 接受邀请
	 */
	public void acceptInvite(final Invitation invitation)
	{
//		acceptInviteUsers.add(AVUser.getCurrentUser(ChatUser.class));
//		invitation.setAcceptInviteUsers(acceptInviteUsers);
		invitation.setAcceptInviteUser(AVUser.getCurrentUser(User.class));
		mInviteBll.hadAcceptInvite(invitation)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.flatMap(new Func1<Boolean, Observable<Invitation>>()
						{
							@Override
							public Observable<Invitation> call(Boolean hasInvited)
							{
								return mInviteBll.acceptInvite(invitation, hasInvited);
							}
						})
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<Invitation>()
						{
							@Override
							public void call(Invitation invitation)
							{
								mActivity.acceptInviteSuccess(invitation);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
								mActivity.acceptInviteFail(throwable.getMessage());
							}
						});
	}

	/**
	 * 将邀请设置为已过期
	 */
	public void setExpire(Invitation invitation)
	{
		mInviteBll.setExpire(invitation)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Subscriber<Void>()
						{
							@Override
							public void onCompleted()
							{
								mActivity.setExpireSuccess();
							}

							@Override
							public void onError(Throwable e)
							{
								e.printStackTrace();
							}

							@Override
							public void onNext(Void aVoid)
							{
							}
						});
	}

}





