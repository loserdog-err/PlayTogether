package com.chenantao.playtogether.mvc.controller.invitation;

import android.app.Activity;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.InvitationCondition;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationCategoryActivity;
import com.chenantao.playtogether.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/27.
 */
public class InvitationCategoryController
{
	private InvitationCategoryActivity mActivity;

	@Inject
	public InviteBll mInviteBll;
	@Inject
	public UserBll mUserBll;

	@Inject
	public InvitationCategoryController(Activity activity)
	{
		mActivity = (InvitationCategoryActivity) activity;
	}


	public void loadData(InvitationCondition condition)
	{
		//对一些没赋值的条件赋默认值以及一些输入校验
		int minAge = condition.getMinAge();
		int maxAge = condition.getMaxAge();
		int gender = condition.getGender();
		if (minAge < Invitation.MIN_AGE || minAge > Invitation.MAX_AGE) minAge = Invitation
				.MIN_AGE;
		if (maxAge > Invitation.MAX_AGE || maxAge < Invitation.MIN_AGE) maxAge = Invitation
				.MAX_AGE;
		if (minAge > maxAge) minAge = maxAge;
		if (gender == -1) condition.setGender(Constant.GENDER_ALL);
		condition.setMinAge(minAge);
		condition.setMaxAge(maxAge);
		mInviteBll.getInvitationsByCondition(condition)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<Invitation>>()
				{
					@Override
					public void call(List<Invitation> invitations)
					{
						mActivity.refreshDataSuccess(invitations);
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						mActivity.refreshDataFail("加载数据失败：" + throwable.getMessage());
					}
				});
	}

	/**
	 * 保存用户的位置
	 *
	 * @param longitude
	 * @param latitude
	 */
	public void updateLocation(double longitude, double latitude)
	{
		User user = AVUser.getCurrentUser(User.class);
//		Logger.e(user.getLocation()+"");
		user.setLocation(new AVGeoPoint(latitude, longitude));
		mUserBll.updateLocation(user)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Void>()
				{
					@Override
					public void call(Void aVoid)
					{
//						Logger.e("更新定位成功");
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
//						Logger.e("更新定位失败：" + throwable.getMessage());
					}
				});
	}
}
