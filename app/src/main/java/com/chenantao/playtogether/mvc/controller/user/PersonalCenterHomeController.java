package com.chenantao.playtogether.mvc.controller.user;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.chat.mvc.bll.ChatBll;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.PersonalCenterHomeBean;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bll.UserBll;
import com.chenantao.playtogether.mvc.view.fragment.user.PersonalCenterHomeFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public class PersonalCenterHomeController
{
	private PersonalCenterHomeFragment mFragment;
	public static final int DYNAMIC_COUNT = 10;

	@Inject
	UserBll mUserBll;

	@Inject
	ChatBll mChatBll;
	@Inject
	ConversationBll mConversationBll;

	@Inject
	public PersonalCenterHomeController(Fragment fragment)
	{
		this.mFragment = (PersonalCenterHomeFragment) fragment;
	}


	public void getHomeData(final User user)
	{
		Observable.zip(
						mUserBll.getInviteCount(user),
						mUserBll.getAcceptInvitedCount(user),
						mUserBll.getNewlyDynamic(user, DYNAMIC_COUNT),
						new Func3<Integer, Integer, List<Invitation>, PersonalCenterHomeBean>()
						{
							@Override
							public PersonalCenterHomeBean call(Integer inviteCount, Integer
											acceptInviteCount, List<Invitation> invitations)
							{
								PersonalCenterHomeBean data = new PersonalCenterHomeBean();
								data.setUser(user);
								data.setBeInvitedCount(acceptInviteCount);
								data.setInviteCount(inviteCount);
								data.setNewlyDynamic(invitations);
								return data;
							}
						})
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<PersonalCenterHomeBean>()
						{
							@Override
							public void call(PersonalCenterHomeBean personalCenterHomeBean)
							{
								mFragment.getDataSuccess(personalCenterHomeBean);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								throwable.printStackTrace();
								mFragment.getDataFail("数据请求失败");
							}
						});
	}

	/**
	 * 与作者聊天，需要先登录，然后创建会话
	 */
	public void chatWithAuthor(final User author)
	{
		final List<User> members = new ArrayList<>();
		User user = AVUser.getCurrentUser(User.class);
		final String myName = user.getUsername();
		members.add(author);
		members.add(user);
		mChatBll.login(myName)//登录
						.subscribeOn(AndroidSchedulers.mainThread())
						.observeOn(AndroidSchedulers.mainThread())
						.flatMap(new Func1<AVIMClient, Observable<AVIMConversation>>()//创建会话
						{
							@Override
							public Observable<AVIMConversation> call(AVIMClient client)
							{
								return mConversationBll.createConversation(members, client,
												ChatConstant.TYPE_SINGLE_CHAT, myName + author.getUsername());

							}
						})
						.subscribe(new Action1<AVIMConversation>()
						{
							@Override
							public void call(AVIMConversation conversation)
							{
								Intent intent = new Intent(mFragment.getActivity(), ChatActivity.class);
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation
												.getConversationId());
								intent.putExtra(ChatActivity.EXTRA_CONVERSATION_NAME, author.getUsername());
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
