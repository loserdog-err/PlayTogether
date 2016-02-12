package com.chenantao.playtogether.mvc.controller.invitation;

import android.app.Activity;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.chat.mvc.bll.ChatBll;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;

import javax.inject.Inject;

import rx.Observable;
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
	private ChatBll mChatBll;
	private ConversationBll mConversationBll;

	@Inject
	public InvitationDetailController(Activity activity)
	{
		mActivity = (InvitationDetailActivity) activity;
		mChatBll = new ChatBll();
		mConversationBll = new ConversationBll();
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
								mActivity.loadDataFail("失败啦：" + throwable.getMessage());
							}
						});

	}

	/**
	 * 接受邀请
	 *
	 * @param invitation
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
								mActivity.acceptInviteFail("受约失败啦:" + throwable.getMessage());
							}
						});
	}

}





