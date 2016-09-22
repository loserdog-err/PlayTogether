package com.chenantao.playtogether.chat.mvc.bll;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.Constant;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Chenantao_gg on 2016/1/30.
 */
public class ConversationBll
{
	/**
	 * 创建一个会话，不管存不存在
	 * 可在主线程调用
	 */
	public Observable<AVIMConversation> createConversation(final List<User> members, final
	AVIMClient client, final int chatType, final String conversationName)
	{
		return Observable.create(new Observable.OnSubscribe<AVIMConversation>()
		{
			@Override
			public void call(final Subscriber<? super AVIMConversation> subscriber)
			{
				HashMap<String, Object> attr = new HashMap<String, Object>();
				attr.put(ChatConstant.KEY_CHAT_TYPE, chatType);
				if (chatType == ChatConstant.TYPE_SINGLE_CHAT)
				{
					//如果是单聊的话，以用户名为key，以头像路径为value，存储到对话里面
					AVFile meAvatar = AVUser.getCurrentUser(User.class).getAvatar();
					AVFile friendAvatar = members.get(0).getAvatar();
					attr.put(AVUser.getCurrentUser(User.class).getUsername(), meAvatar != null ? meAvatar
									.getThumbnailUrl(true, Constant.AVATAR_WIDTH, Constant.AVATAR_HEIGHT) : null);
					attr.put(members.get(0).getUsername(), friendAvatar != null ? friendAvatar
									.getThumbnailUrl(true, Constant.AVATAR_WIDTH, Constant.AVATAR_HEIGHT) : null);
				}
				List<String> strMembers = new ArrayList<String>();
				for (int i = 0; i < members.size(); i++)
				{
					strMembers.add(members.get(i).getUsername());
				}
				client.createConversation(strMembers, conversationName, attr, false, true, new
								AVIMConversationCreatedCallback()
								{
									@Override
									public void done(AVIMConversation avimConversation, AVIMException e)
									{
										if (e == null)
										{
											Logger.e("conversation create success");
											subscriber.onNext(avimConversation);
										} else
										{
											e.printStackTrace();
											subscriber.onError(e);
										}
									}
								});
			}
		});
	}

	/**
	 * 创建群聊
	 */
	public Observable<AVIMConversation> createGroupChat(final List<User> members, final
	AVIMClient client, final String conversationName)
	{
		return this.createConversation(members, client, ChatConstant.TYPE_GROUP_CHAT,
						conversationName);
	}

	/**
	 * 创建单聊
	 */
	public Observable<AVIMConversation> createSingleChat(User friend, final
	AVIMClient client)
	{
		List<User> members = Arrays.asList(friend);
		String myName = AVUser.getCurrentUser(User.class).getUsername();
		return this.createConversation(members, client, ChatConstant.TYPE_SINGLE_CHAT, myName + friend
						.getUsername());
	}

	/**
	 * 退出会话
	 */
	public Observable<AVIMConversation> quitConversation(final AVIMConversation conversation)
	{
		return Observable.create(new Observable.OnSubscribe<AVIMConversation>()
		{
			@Override
			public void call(final Subscriber<? super AVIMConversation> subscriber)
			{
				conversation.quit(new AVIMConversationCallback()
				{
					@Override
					public void done(AVIMException e)
					{
						if (e == null)
						{
							subscriber.onNext(conversation);
						} else
						{
							e.printStackTrace();
							subscriber.onError(e);
						}
					}
				});
			}
		});
	}

	/**
	 * 根据一个会话是否存在，创建一个会话
	 * 可在主线程调用
	 * 已废弃，新版本的leancloud创建会话可以指定一个unique标识，创建唯一的会话
	 */
	@Deprecated
	public Observable<AVIMConversation> createConversationIfNotExists(
					final List<User> members,
					final AVIMClient client,
					final int chatType,
					final String conversationName)
	{
		List<String> strMembers = new ArrayList<>();
		for (int i = 0; i < members.size(); i++)
		{
			strMembers.add(members.get(i).getUsername());
		}
		Observable<AVIMConversation> existsConversation = getConversationByMembers(strMembers,
						client);
		Observable<AVIMConversation> newConversation = createConversation
						(members, client, chatType, conversationName);
		return Observable.concat(existsConversation, newConversation)
						.first();

	}

	/**
	 * 通过会话成员得到会话
	 * can in main thread
	 */
	public Observable<AVIMConversation> getConversationByMembers(final List<String> members, final
	AVIMClient client)
	{
		return Observable.create(new Observable.OnSubscribe<AVIMConversation>()
		{
			@Override
			public void call(final Subscriber<? super AVIMConversation> subscriber)
			{
				AVIMConversationQuery query = client.getQuery();
				query.withMembers(members, true);
				query.findInBackground(new AVIMConversationQueryCallback()
				{
					@Override
					public void done(List<AVIMConversation> list, AVIMException e)
					{
						if (e == null)
						{
							if (list.size() > 0)
							{
								Logger.e("conversation exists");
								subscriber.onNext(list.get(0));
							} else
							{
								Logger.e("conversation not exists");
								subscriber.onCompleted();
							}

						} else
						{
							e.printStackTrace();
							subscriber.onError(e);
						}
					}
				});
			}
		});
	}

	/**
	 * 根据会话的id查询
	 * 先查询本地是否有缓存，否在到服务器中寻找
	 * can in main thread
	 */
	public Observable<AVIMConversation> getConversationById(final String conversationId, final
	AVIMClient client)
	{
		return Observable.create(new Observable.OnSubscribe<AVIMConversation>()
		{
			@Override
			public void call(final Subscriber<? super AVIMConversation> subscriber)
			{
				AVIMConversation conversation = client.getConversation(conversationId);
				if (conversation != null)
				{
					subscriber.onNext(conversation);
					subscriber.onCompleted();
					return;
				}
				AVIMConversationQuery query = client.getQuery();
				query.whereEqualTo("objectId", conversationId);
				query.findInBackground(new AVIMConversationQueryCallback()
				{
					@Override
					public void done(List<AVIMConversation> list, AVIMException e)
					{
						if (e == null)
						{
							if (!list.isEmpty())
								subscriber.onNext(list.get(0));
							else subscriber.onCompleted();
						} else
						{
							subscriber.onError(e);
						}
					}
				});
			}
		});
	}

	/**
	 * 获取用户的会话
	 *
	 * @param client client
	 * @param count  获取会话的数量
	 */
	public Observable<List<AVIMConversation>> getConversations(final AVIMClient client, final int
					count)
	{
		return Observable.create(new Observable.OnSubscribe<List<AVIMConversation>>()
		{
			@Override
			public void call(final Subscriber<? super List<AVIMConversation>> subscriber)
			{
				AVIMConversationQuery query = client.getQuery();
				query.setLimit(count);
				query.orderByDescending("lm");
				query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
				query.findInBackground(new AVIMConversationQueryCallback()
				{
					@Override
					public void done(List<AVIMConversation> list, AVIMException e)
					{
						if (e == null)
						{
							//过滤掉那些单人的会话
							List<AVIMConversation> newList = new ArrayList<>();
							for (AVIMConversation conversation : list)
							{
								if (conversation.getMembers().size() >= 2)
								{
									newList.add(conversation);
								}
							}
							subscriber.onNext(newList);
						} else
						{
							e.printStackTrace();
							subscriber.onError(e);
						}
					}
				});
			}
		});
	}
}