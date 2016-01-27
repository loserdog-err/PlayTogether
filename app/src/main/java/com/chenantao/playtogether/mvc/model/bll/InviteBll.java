package com.chenantao.playtogether.mvc.model.bll;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.InvitationCondition;
import com.chenantao.playtogether.utils.FileUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
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
	 * 发布邀请
	 *
	 * @param invitation
	 * @return
	 */
	public Observable<AVObject> postInvitation(final Invitation invitation, final Context context)
	{
		return Observable.create(new Observable.OnSubscribe<AVObject>()
		{
			@Override
			public void call(Subscriber<? super AVObject> subscriber)
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

	public Observable<Invitation> acceptInvite(final Invitation invitation)
	{
		return Observable.create(new Observable.OnSubscribe<Invitation>()
		{
			@Override
			public void call(Subscriber<? super Invitation> subscriber)
			{
				try
				{
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
				int gender = condition.getGender();
				int minAge = condition.getMinAge();
				int maxAge = condition.getMaxAge();
				InvitationCondition.OrderBy orderBy = condition.getOrderBy();
				AVQuery<Invitation> query = AVQuery.getQuery(Invitation.class);
				query.whereEqualTo(Invitation.FIELD_CATEGORY, category);
				query.whereGreaterThanOrEqualTo(Invitation.FIELD_MIN_AGE, minAge);
				query.whereLessThanOrEqualTo(Invitation.FIELD_MAX_AGE, maxAge);
				query.include(Invitation.FIELD_AUTHOR);
				if (orderBy == InvitationCondition.OrderBy.NEAREST)//离我最近
				{
					// TODO: 2016/1/27 需要百度sdk提供的功能来实现
				} else//最新
				{
					query.orderByDescending(Invitation.CREATED_AT);
				}
				Logger.e("condition:" + condition);
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
