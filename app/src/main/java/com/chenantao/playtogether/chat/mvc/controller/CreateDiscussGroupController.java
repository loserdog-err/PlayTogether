package com.chenantao.playtogether.chat.mvc.controller;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.chat.mvc.view.activity.CreateDiscussGroupActivity;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/2/6.
 */
public class CreateDiscussGroupController
{
	private CreateDiscussGroupActivity mActivity;

	@Inject
	public UserBll mUserBll;

	@Inject
	public ConversationBll mConversationBll;

	@Inject
	public CreateDiscussGroupController(Activity mActivity)
	{
		this.mActivity = (CreateDiscussGroupActivity) mActivity;
	}

	public void createDiscussGroup(List<User> members, String discussName)
	{
		mConversationBll.createGroupChat(members, AVImClientManager.getInstance().getClient(),
						discussName)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Action1<AVIMConversation>()
						{
							@Override
							public void call(AVIMConversation conversation)
							{
								//显示成功并跳转到群聊界面
								mActivity.createDiscussGroupSuccess();
								Intent intent = new Intent(mActivity, ChatActivity.class);
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation
												.getConversationId());
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_NAME, conversation.getName());
								mActivity.startActivity(intent);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								mActivity.createDiscussGroupFail("群组创建失败");
								throwable.printStackTrace();
							}
						});
	}

}
