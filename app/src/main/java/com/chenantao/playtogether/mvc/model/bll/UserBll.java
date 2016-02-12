package com.chenantao.playtogether.mvc.model.bll;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Chenantao_gg on 2016/1/17.
 */
public class UserBll
{
	public boolean checkUsername(String username)
	{
		if (username.length() >= Constant.USERNAME_MIN_LENGTH && username.length() <=
						Constant.PASSWORD_MAX_LENGTH)
		{
			//校验通过
			return true;
		} else
		{
			//校验失败
			return false;
		}
	}

	public boolean checkPassword(String password)
	{
		if (password.length() >= Constant.PASSWORD_MIN_LENGTH && password.length() <=
						Constant.PASSWORD_MAX_LENGTH)
		{
			//校验通过
			return true;
		} else
		{
			//校验失败
			return false;
		}
	}

	public Observable<User> registerUser(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<User>()
		{
			@Override
			public void call(Subscriber<? super User> subscriber)
			{
				try
				{
					AVUser avUser = new AVUser();
					avUser.setUsername(user.getUsername());
					avUser.setPassword(user.getPassword());
					avUser.put(User.FIELD_GENDER, user.getGender());
					avUser.signUp();
					//注册成功
					subscriber.onNext(user);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});

	}

	public Observable<Void> updateUser(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<Void>()
		{
			@Override
			public void call(Subscriber<? super Void> subscriber)
			{
				try
				{
					user.save();
					subscriber.onCompleted();
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	public Observable<AVUser> login(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<AVUser>()
		{
			@Override
			public void call(Subscriber<? super AVUser> subscriber)
			{
				try
				{
					AVUser avUser = AVUser.logIn(user.getUsername(), user.getPassword());
					subscriber.onNext(avUser);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	public Observable<String> uploadAvatar(final String path)
	{
		return Observable.create(new Observable.OnSubscribe<String>()
		{
			@Override
			public void call(Subscriber<? super String> subscriber)
			{
				File file = new File(path);
				try
				{
					AVFile avatar = AVFile.withFile(file.getName(), file);
					double[] widthAndHeight = FileUtils.getImageWidthAndHeight(path);
					avatar.addMetaData("width", widthAndHeight[0]);
					avatar.addMetaData("height", widthAndHeight[1]);
					avatar.save();
					User user = AVUser.getCurrentUser(User.class);
					user.setAvatar(avatar);
					user.save();
					subscriber.onNext(path);
				} catch (Exception e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	public Observable<Void> updateLocation(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<Void>()
		{
			@Override
			public void call(Subscriber<? super Void> subscriber)
			{
				try
				{
					user.save();
					subscriber.onNext(null);
				} catch (AVException e)
				{
					subscriber.onError(e);
					e.printStackTrace();
				}
			}
		});

	}

	public Observable<User> getUserByUserName(final String username)
	{
		return Observable.create(new Observable.OnSubscribe<User>()
		{
			@Override
			public void call(Subscriber<? super User> subscriber)
			{
				AVQuery<User> query = AVQuery.getQuery(User.class);
				query.whereEqualTo(User.FIELD_USERNAME, username);
				try
				{
					List<User> userList = query.find();
					if (!userList.isEmpty()) subscriber.onNext(userList.get(0));
					else subscriber.onError(new Exception("查无此人"));
				} catch (AVException e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	public Observable<User> getUserById(final String id)
	{
		return Observable.create(new Observable.OnSubscribe<User>()
		{
			@Override
			public void call(Subscriber<? super User> subscriber)
			{
				AVQuery<User> query = AVQuery.getQuery(User.class);
				try
				{
					User user = query.get(id);
					subscriber.onNext(user);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}
	/*---------------------------个人中心需要获取数据的方法------------------*/

	/**
	 * 获得发出邀请的数量
	 */
	public Observable<Integer> getInviteCount(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<Integer>()
		{
			@Override
			public void call(Subscriber<? super Integer> subscriber)
			{
				AVQuery<Invitation> query = AVQuery.getQuery(Invitation.class);
				query.whereEqualTo(Invitation.FIELD_AUTHOR, user);
				try
				{
					int count = query.count();
					subscriber.onNext(count);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});

	}

	/**
	 * 获得接受邀请的数量
	 */
	public Observable<Integer> getAcceptInvitedCount(final User user)
	{
		return Observable.create(new Observable.OnSubscribe<Integer>()
		{
			@Override
			public void call(Subscriber<? super Integer> subscriber)
			{
				AVQuery<Invitation> query = AVQuery.getQuery(Invitation.class);
				query.whereEqualTo(Invitation.FIELD_ACCEPT_INVITE_USERS, user);
				try
				{
					int count = query.count();
					subscriber.onNext(count);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	/**
	 * 获得用户的最新动态
	 */
	public Observable<List<Invitation>> getNewlyDynamic(final User user, final int count)
	{
		return Observable.create(new Observable.OnSubscribe<List<Invitation>>()
		{
			@Override
			public void call(Subscriber<? super List<Invitation>> subscriber)
			{
				AVQuery<Invitation> inviteQuery = AVQuery.getQuery(Invitation.class);
				AVQuery<Invitation> beInvitedQuery = AVQuery.getQuery(Invitation.class);
				inviteQuery.whereEqualTo(Invitation.FIELD_AUTHOR, user);
				beInvitedQuery.whereEqualTo(Invitation.FIELD_ACCEPT_INVITE_USERS, user);
				List<AVQuery<Invitation>> queries = new ArrayList<AVQuery<Invitation>>();
				queries.add(inviteQuery);
				queries.add(beInvitedQuery);
				AVQuery<Invitation> mainQuery = AVQuery.or(queries);
				mainQuery.setLimit(count);
				mainQuery.include(Invitation.FIELD_ACCEPT_INVITE_USERS);
				mainQuery.include(Invitation.FIELD_AUTHOR);
				mainQuery.orderByDescending(Invitation.UPDATED_AT);
				try
				{
					List<Invitation> list = mainQuery.find();
					subscriber.onNext(list);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});

	}


	/*---------------------------以下这些方法为接入聊天系统必须实现的方法-------------------*/


	/**
	 * 得到指定 username 的好友列表
	 *
	 * @param username
	 * @return
	 */
	public Observable<List<User>> getFriends(final String username)
	{
		return Observable.create(new Observable.OnSubscribe<List<User>>()
		{
			@Override
			public void call(Subscriber<? super List<User>> subscriber)
			{
				AVQuery<User> query = AVQuery.getQuery(User.class);
				query.whereEqualTo(User.FIELD_USERNAME, username);
				query.include(User.FIELD_FRIENDS);
				try
				{
					/**
					 * 获得 User 数组后，将其转换为 ChatUser的list
					 */
					List<User> users = query.find();
					List list = users.get(0).getList(User.FIELD_FRIENDS);
					if (list != null && !list.isEmpty())
					{
						List<User> friends = list;
						subscriber.onNext(friends);
					} else
					{
						subscriber.onCompleted();
					}
				} catch (AVException e)
				{
					subscriber.onError(e);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 为当前用户添加好友，先把用户名转换为 User 对象，然后再添加到好友数组里面
	 *
	 * @param friendName
	 * @return
	 */
	public Observable<User> addFriend(final String friendName)
	{
		return checkIfIsFriend(friendName)
						.flatMap(new Func1<String, Observable<User>>()
						{
							@Override
							public Observable<User> call(String friend)
							{
								return getUserByUserName(friendName);
							}
						})
						.flatMap(new Func1<User, Observable<User>>()
						{
							@Override
							public Observable<User> call(final User friend)
							{
								return Observable.create(new Observable.OnSubscribe<User>()
								{
									@Override
									public void call(Subscriber<? super User> subscriber)
									{
										User me = AVUser.getCurrentUser(User.class);
										List<User> friendList = me.getFriends();
										friendList.addAll(Arrays.asList(friend));
										me.setFriends(friendList);
										try
										{
											me.save();
											subscriber.onNext(friend);
										} catch (AVException e)
										{
											subscriber.onError(e);
											e.printStackTrace();
										}
									}
								});
							}
						});
	}

	/**
	 * 检查 指定friend 是否已经是我的好友
	 *
	 * @param friend
	 * @return
	 */
	public Observable<String> checkIfIsFriend(final String friend)
	{
		return Observable.create(new Observable.OnSubscribe<String>()
		{
			@Override
			public void call(Subscriber<? super String> subscriber)
			{
				List<User> friends = AVUser.getCurrentUser(User.class).getFriends();
				if (!friends.isEmpty())
				{
					for (User user : friends)
					{
						if (user.getUsername().equals(friend))
						{
							subscriber.onError(new Exception(friend + " 已是你的好友"));
							break;
						}
					}
				}
				subscriber.onNext(friend);
			}


		});


	}
}
