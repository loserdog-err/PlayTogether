package com.chenantao.playtogether.mvc.model.bll;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.InvitationCondition;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.FileUtils;
import com.chenantao.playtogether.utils.LocationUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
public class InviteBll
{
	/**
	 * 获得发邀请人的地址
	 *
	 * @param context
	 * @return
	 */
	public Observable<AVGeoPoint> getLocation(final Context context)
	{
		return Observable.create(new Observable.OnSubscribe<AVGeoPoint>()
		{
			@Override
			public void call(final Subscriber<? super AVGeoPoint> subscriber)
			{
				LocationClient client = LocationUtils.getLocationClient(context);
				client.registerLocationListener(new BDLocationListener()
				{
					@Override
					public void onReceiveLocation(BDLocation location)
					{
						if (location.getLocType() == BDLocation.TypeGpsLocation || location
										.getLocType() == BDLocation.TypeNetWorkLocation || location
										.getLocType() == BDLocation.TypeOffLineLocation)
						{
							subscriber.onNext(new AVGeoPoint(location.getLatitude(), location
											.getLongitude()));
						} else
						{
							subscriber.onNext(null);
						}
						LocationUtils.stopClient();
					}
				});
				client.start();
			}
		});
	}

	/**
	 * 发布邀请
	 *
	 * @param invitation
	 * @param point
	 * @return
	 */
	public Observable<Invitation> postInvitation(final Invitation invitation, final AVGeoPoint
					point, final Context context)
	{
		return Observable.create(new Observable.OnSubscribe<Invitation>()
		{
			@Override
			public void call(Subscriber<? super Invitation> subscriber)
			{
				try
				{
					List<String> filePaths = invitation.getUploadPicsPath();
					List<AVFile> uploadFiles = new LinkedList<AVFile>();
					for (int i = 0; i < filePaths.size(); i++)
					{
						String path = filePaths.get(i);
						double[] metaData = FileUtils.getImageWidthAndHeight(path);
						File file = new File(path);
						AVFile avFile = AVFile.withFile(file.getName(), file);
						avFile.addMetaData("width", metaData[0]);
						avFile.addMetaData("height", metaData[1]);
						avFile.save();
						uploadFiles.add(avFile);
					}
					invitation.setPics(uploadFiles);
					if (point == null)
					{
						AVGeoPoint defaultPoint = new AVGeoPoint(0, 0);
						invitation.setLocation(defaultPoint);
					} else
					{
						invitation.setLocation(point);
					}
					invitation.save();
					subscriber.onNext(invitation);
				} catch (Exception e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	/**
	 * 获得最新的邀请数据
	 *
	 * @return
	 */
	public Observable<List<Invitation>> getNewlyInvitationData()
	{
		return Observable.create(new Observable.OnSubscribe<List<Invitation>>()
		{
			@Override
			public void call(Subscriber<? super List<Invitation>> subscriber)
			{
				AVQuery<Invitation> query = AVObject.getQuery(Invitation.class);
				query.orderByDescending(AVObject.CREATED_AT);
				query.include(Invitation.FIELD_AUTHOR);
				try
				{
					List<Invitation> invitations = query.find();
					subscriber.onNext(invitations);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}


	/**
	 * 通过一个objectid获得一个invitation
	 *
	 * @param invitationId
	 * @return
	 */
	public Observable<Invitation> getInvitationById(final String invitationId)
	{
		return Observable.create(new Observable.OnSubscribe<Invitation>()
		{
			@Override
			public void call(Subscriber<? super Invitation> subscriber)
			{
				AVQuery<Invitation> query = AVObject.getQuery(Invitation.class);
				query.include(Invitation.FIELD_ACCEPT_INVITE_USERS);
				query.include(Invitation.FIELD_PIC);
				query.include(Invitation.FIELD_AUTHOR);
				try
				{
					Invitation invitation = query.get(invitationId);
					subscriber.onNext(invitation);
				} catch (AVException e)
				{
					Logger.e(e, "失败");
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}

	/**
	 * 查询用户是否已经接受了邀请
	 *
	 * @return true，代表已经接收过邀请
	 */
	public Observable<Boolean> hadAcceptInvite(final Invitation invitation)
	{
		return Observable.create(new Observable.OnSubscribe<Boolean>()
		{
			@Override
			public void call(Subscriber<? super Boolean> subscriber)
			{
				AVQuery<Invitation> query = AVQuery.getQuery(Invitation.class);
				AVQuery<User> innerQuery = AVQuery.getQuery(User.class);
				User localUser = AVUser.getCurrentUser(User.class);
				innerQuery.whereEqualTo(User.FIELD_OBJECT_ID, localUser.getObjectId());
				query.whereMatchesQuery(Invitation.FIELD_ACCEPT_INVITE_USERS, innerQuery);
				query.whereEqualTo(Invitation.FIELD_OBJECT_ID, invitation.getObjectId());
				try
				{
					List<Invitation> invitations = query.find();
					if (invitations != null && invitations.size() > 0)
					{
						subscriber.onNext(true);
					} else
					{
						subscriber.onNext(false);
					}
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onNext(true);
				}
			}
		});
	}

	/**
	 * 接受邀请
	 *
	 * @param invitation
	 * @param hasInvited
	 * @return
	 */
	public Observable<Invitation> acceptInvite(final Invitation invitation, final Boolean
					hasInvited)
	{
		return Observable.create(new Observable.OnSubscribe<Invitation>()
		{
			@Override
			public void call(Subscriber<? super Invitation> subscriber)
			{
				try
				{
					if (hasInvited)
					{
						subscriber.onError(new Exception("别这么热情啊，你都已经接受邀请了"));
						return;
					}
//					Logger.json(invitation.toString());
					invitation.setFetchWhenSave(true);
					invitation.save();
					subscriber.onNext(invitation);
				} catch (AVException e)
				{
					subscriber.onError(e);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 通过条件获取invitations
	 *
	 * @param condition
	 */
	public Observable<List<Invitation>> getInvitationsByCondition(final InvitationCondition
																																				condition)
	{
		return Observable.create(new Observable.OnSubscribe<List<Invitation>>()
		{
			@Override
			public void call(Subscriber<? super List<Invitation>> subscriber)
			{
				int category = condition.getCategory();
				String gender = condition.getGender();
				int minAge = condition.getMinAge();
				int maxAge = condition.getMaxAge();
				InvitationCondition.OrderBy orderBy = condition.getOrderBy();
				AVQuery<Invitation> query = AVQuery.getQuery(Invitation.class);
				AVQuery<User> innerQuery = AVQuery.getQuery(User.class);
				//指定类别
				query.whereEqualTo(Invitation.FIELD_CATEGORY, category);
				//嵌套查询，查询受邀用户是指定性别的的invitation
				if (gender.equals(Constant.GENDER_ALL))
				{
					String[] genders = new String[]{Constant.GENDER_MAN, Constant.GENDER_WOMEN};
					innerQuery.whereContainedIn(User.FIELD_GENDER, Arrays.asList(genders));
				} else
				{
					innerQuery.whereEqualTo(User.FIELD_GENDER, gender);
				}
				//指定年龄
				innerQuery.whereGreaterThanOrEqualTo(User.FIELD_AGE, minAge);
				innerQuery.whereLessThanOrEqualTo(User.FIELD_AGE, maxAge);
				//指定排序方式
				if (orderBy == InvitationCondition.OrderBy.NEAREST)//离我最近
				{
					AVGeoPoint point = AVUser.getCurrentUser(User.class).getLocation();
					if (point != null)
					{
						query.whereNear(Invitation.FIELD_LOCATION, point);

					}

				} else//最新
				{
					query.orderByDescending(Invitation.CREATED_AT);
				}
				//指定分页显示的数据
				query.setLimit(Constant.PAGE_SIZE);
				query.setSkip(condition.getSkip());
				query.include(Invitation.FIELD_AUTHOR);
				query.whereMatchesQuery(Invitation.FIELD_AUTHOR, innerQuery);
				try
				{
					List<Invitation> invitations = query.find();
					subscriber.onNext(invitations);
				} catch (AVException e)
				{
					e.printStackTrace();
					subscriber.onError(e);
				}
			}
		});
	}
}
