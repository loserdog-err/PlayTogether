package com.chenantao.playtogether.mvc.controller.invitation;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.view.activity.invitation.PostInvitationActivity;
import com.chenantao.playtogether.mvc.view.fragment.invitation.InviteConditionFragment;
import com.chenantao.playtogether.mvc.view.fragment.invitation.WriteMessageFragment;
import com.chenantao.playtogether.utils.DialogUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/22.
 */
public class PostInvitationController
{
	private PostInvitationActivity mActivity;

	@Inject
	public InviteBll mInviteBll;

	@Inject
	public PostInvitationController(Activity activity)
	{
		this.mActivity = (PostInvitationActivity) activity;
	}

	public void postInvite()
	{
		//从fragment中得到数据
		List<Fragment> fragments = mActivity.getFragments();
		if (fragments == null || fragments.size() < 3)
		{
			Toast.makeText(mActivity, "骚等，界面还没加载完毕,", Toast.LENGTH_SHORT).show();
			return;
		}
		WriteMessageFragment writeTitleFragment = (WriteMessageFragment) fragments.get
				(PostInvitationActivity.POS_WRITE_TITLE);
		WriteMessageFragment writeContentFragment = (WriteMessageFragment) fragments.get
				(PostInvitationActivity.POS_WRITE_CONTENT);
		InviteConditionFragment conditionFragment = (InviteConditionFragment) fragments
				.get(PostInvitationActivity
						.POS_CONDITION);
		//先从标题和内容获得数据
		String title = writeTitleFragment.getContent();
		String content = writeContentFragment.getContent();
		List<String> uploadFiles = writeContentFragment.getPic();
		if ("".equals(title))
		{
			DialogUtils.dismissProgressDialog();
			Toast.makeText(mActivity, "没标题你要问神马⊙△⊙？", Toast.LENGTH_SHORT).show();
			return;
		}
		Invitation invitation = new Invitation();
		invitation.setAuthor(AVUser.getCurrentUser(User.class));
		invitation.setTitle(title);
		invitation.setContent(content);
		invitation.setUploadPicsPath(uploadFiles);
		//由于条件fragment数据较多，这里创建一个对象供其自己set进去
		conditionFragment.getInputData(invitation);
//		int desireWidth = (int) (ScreenUtils.getScreenWidth(mActivity) * 0.84);
//		int desireHeight = ScreenUtils.getScreenHeight(mActivity) / 2;
		mInviteBll.postInvitation(invitation, mActivity)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<AVObject>()
				{
					@Override
					public void call(AVObject avObject)
					{
						mActivity.postInvitationSuccess();
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						mActivity.postInvitationFail("失败啦，不知道为神马，重试一遍吧");
						Logger.e(throwable, "发布失败");
					}
				});
	}
}
