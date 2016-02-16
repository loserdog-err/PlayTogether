package com.chenantao.playtogether.chat.mvc.controller;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.chat.mvc.view.fragment.FriendListFragment;
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
public class FriendListController
{
	private FriendListFragment mFragment;

	@Inject
	UserBll mUserBll;
	@Inject
	ConversationBll mConversationBll;

	@Inject
	public FriendListController(Fragment fragment)
	{
		this.mFragment = (FriendListFragment) fragment;
	}

	public void getFriends(String username)
	{
		mUserBll.getFriends(username)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Action1<List<User>>()
						{
							@Override
							public void call(List<User> chatUsers)
							{
								mFragment.getFriendsSuccess(chatUsers);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								mFragment.getFriendsFail("获取好友失败");
								throwable.printStackTrace();
							}
						});

	}

	public void createConversation(final User friend)
	{
		mConversationBll.createSingleChat(friend, AVImClientManager.getInstance().getClient())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Action1<AVIMConversation>()
						{
							@Override
							public void call(AVIMConversation conversation)
							{
								Intent intent = new Intent(mFragment.getActivity(), ChatActivity.class);
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_NAME, friend.getUsername());
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation
												.getConversationId());
								mFragment.getActivity().startActivity(intent);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
							}
						});
	}
}
