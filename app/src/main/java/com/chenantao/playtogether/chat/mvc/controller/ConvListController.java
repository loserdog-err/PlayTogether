package com.chenantao.playtogether.chat.mvc.controller;

import android.support.v4.app.Fragment;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.bll.ChatBll;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.chat.mvc.view.fragment.ConvListFragment;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Chenantao_gg on 2016/2/1.
 */
public class ConvListController
{
//	private ChatHomeActivity mActivity;

	private ConvListFragment mFragment;
	@Inject
	ChatBll mChatBll;
	@Inject
	ConversationBll mConversationBll;

	@Inject
	public ConvListController(Fragment fragment)
	{
		this.mFragment = (ConvListFragment) fragment;
	}


	/**
	 * 根据用户名拉去会话，需要先判断用户是否已经登录
	 */
	public void getConversations(final String username)
	{
		String clientId = AVImClientManager.getInstance().getClientId();
		Observable.just(clientId)
						.subscribeOn(AndroidSchedulers.mainThread())
						.observeOn(AndroidSchedulers.mainThread())
						.flatMap(new Func1<String, Observable<AVIMClient>>()//获得 client
						{
							@Override
							public Observable<AVIMClient> call(String clientId)
							{
//								Logger.e("client:"+clientId);
								if (clientId == null || !username.equals(clientId))
								{
									//如果全局存储的clientId为空或者跟你传递进来的用户名不同，即认为未登录
									return mChatBll.login(username);
								} else
								{
									return Observable.just(AVImClientManager.getInstance().getClient());
								}
							}
						})
						.flatMap(new Func1<AVIMClient, Observable<List<AVIMConversation>>>()//获取会话
						{
							@Override
							public Observable<List<AVIMConversation>> call(AVIMClient client)
							{
								return mConversationBll.getConversations(client, ChatConstant
												.CHAT_HOME_CONVERSATION_COUNT);
							}
						})
						.subscribe(new Action1<List<AVIMConversation>>()
						{
							@Override
							public void call(List<AVIMConversation> conversations)
							{
								mFragment.getConversationsSuccess(conversations);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
								mFragment.getConversationsFail("获取会话失败");
							}
						});

	}

	/**
	 * 删除会话
	 * leancloud 暂时没有提供删除会话的接口，
	 * 这里退出会话即为删除会话
	 */
	public void delConversation(AVIMConversation conversation)
	{
		mConversationBll.quitConversation(conversation)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<AVIMConversation>()
						{
							@Override
							public void call(AVIMConversation conversation)
							{
								Logger.e("删除成功");
								//delete success,do sth?
							}
						});
	}
}
