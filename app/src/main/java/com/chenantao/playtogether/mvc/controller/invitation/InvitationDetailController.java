package com.chenantao.playtogether.mvc.controller.invitation;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.chat.ChatActivity;
import com.chenantao.playtogether.chat.bll.ChatBll;
import com.chenantao.playtogether.chat.bll.ConversationBll;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.InviteBll;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

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
//		acceptInviteUsers.add(AVUser.getCurrentUser(User.class));
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

	/**
	 * 与作者聊天，需要先登录，然后创建会话
	 *
	 * @param authorName
	 */
	public void chatWithAuthor(final String authorName)
	{
		final List<String> members = new ArrayList<>();
		final String myName = AVUser.getCurrentUser(User.class).getUsername();
		members.add(authorName);
		members.add(myName);
		Logger.e("authorname:" + authorName + ",myName:" + myName);
		mChatBll.login(myName)//登录
				.subscribeOn(AndroidSchedulers.mainThread())
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap(new Func1<AVIMClient, Observable<AVIMConversation>>()//创建会话
				{
					@Override
					public Observable<AVIMConversation> call(AVIMClient client)
					{
						return mConversationBll.createConversationIfNotExists(members, client,
								ChatConstant.TYPE_SINGLE_CHAT, myName + authorName);

					}
				})
				.subscribe(new Action1<AVIMConversation>()
				{
					@Override
					public void call(AVIMConversation conversation)
					{
						Intent intent = new Intent(mActivity, ChatActivity.class);
						intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation
								.getConversationId());
						mActivity.startActivity(intent);
					}
				}, new Action1<Throwable>()
				{
					@Override
					public void call(Throwable throwable)
					{
						mActivity.chatWithAuthorFail("聊天失败:" + throwable.getMessage());
						throwable.printStackTrace();
					}
				});
	}
}





