package com.chenantao.playtogether.chat.mvc.controller;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.event.EventFriendListChange;
import com.chenantao.playtogether.chat.event.EventHomeConversationChange;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.chat.mvc.view.activity.AddFriendActivity;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/2/5.
 */
public class AddFriendController
{
	private AddFriendActivity mActivity;

	@Inject
	ConversationBll mConversationBll;

	@Inject
	UserBll mUserBll;

	@Inject
	public AddFriendController(Activity activity)
	{
		mActivity = (AddFriendActivity) activity;
	}

	/**
	 * 添加好友后打开相应的会话
	 */
	public void addFriend(String strFriend, User friend)
	{
		String clientId = AVImClientManager.getInstance().getClientId();
		String friendName = strFriend == null ? friend.getUsername() : strFriend;
		if (clientId.equals(friendName))
		{
			mActivity.addFriendError("不能添加自己为好友");
			return;
		}
		mUserBll.addFriend(strFriend, friend)//添加好友
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.doOnNext(new Action1<User>()
						{
							@Override
							public void call(User friend)
							{
								//通知刷新好友列表
								EventBus.getDefault().post(new EventFriendListChange());
							}
						})
						.flatMap(new Func1<User, Observable<AVIMConversation>>()//创建会话
						{
							@Override
							public Observable<AVIMConversation> call(User friend)
							{
								return mConversationBll.createSingleChat(friend, AVImClientManager.getInstance
												().getClient());
							}
						})
						.subscribe(new Action1<AVIMConversation>()
						{
							@Override
							public void call(AVIMConversation conversation)
							{
								mActivity.addFriendSuccess();
								String conversationName = "";
								List<String> members = conversation.getMembers();
								for (String member : members)
								{
									if (!member.equals(AVImClientManager.getInstance().getClientId()))
									{
										conversationName = member;
										break;
									}
								}
								//通知会话列表刷新会话
								EventBus.getDefault().post(new EventHomeConversationChange(false, conversation));
								//跳转到聊天界面
								Intent intent = new Intent(mActivity, ChatActivity.class);
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_NAME, conversationName);
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.getConversationId
												());
								mActivity.startActivity(intent);
								mActivity.finish();
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								mActivity.addFriendError("添加失败:" + throwable.getMessage());
							}
						});
	}


	/**
	 * 获得臭味相投的人
	 */
	public void getSimilarPeople(final User user)
	{
		mUserBll.getSimilarPeople(user)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Action1<List<User>>()
						{
							@Override
							public void call(List<User> users)
							{
								mActivity.getSimilarPeopleSuccess(users);
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
