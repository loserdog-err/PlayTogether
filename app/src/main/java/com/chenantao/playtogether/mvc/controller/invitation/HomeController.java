package com.chenantao.playtogether.mvc.controller.invitation;

import android.app.Activity;

import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.activity.invitation.HomeActivity;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class HomeController
{
	private HomeActivity mActivity;

	@Inject
	public InviteBll mInviteBll;

	@Inject
	public UserBll mUserBll;

	@Inject
	public HomeController(Activity activity)
	{
		mActivity = (HomeActivity) activity;
	}

	/**
	 * 获得最新的邀请信息
	 */
	public void getNewlyInvitationDatas()
	{
		mInviteBll.getNewlyInvitationData()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<Invitation>>()
				{
					@Override
					public void call(List<Invitation> invitations)
					{
						mActivity.loadDataSuccess(invitations);
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						throwable.printStackTrace();
						mActivity.loadDataError("啊哦，加载失败");
					}
				});
//		AVQuery<Invitation> query = AVObject.getQuery(Invitation.class);
//		query.orderByDescending(AVObject.CREATED_AT);
//		query.whereNotEqualTo(Invitation.FIELD_IS_EXPIRE, true);
//		query.include(Invitation.FIELD_AUTHOR);
//		query.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
//		query.findInBackground(new FindCallback<Invitation>()
//		{
//			@Override
//			public void done(List<Invitation> invitations, AVException e)
//			{
//				if (e == null)
//					mActivity.loadDataSuccess(invitations);
//			}
//		});

	}

	public void uploadAvatar(String path)
	{
		mUserBll.uploadAvatar(path)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<String>()
						{
							@Override
							public void call(String path)
							{
								mActivity.uploadAvatarSuccess(path);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
								mActivity.uploadAvatarFail("失败啦:" + throwable.getMessage());
							}
						});
	}
}
