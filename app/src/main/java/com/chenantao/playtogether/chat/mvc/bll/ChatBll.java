package com.chenantao.playtogether.chat.mvc.bll;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.utils.FileUtils;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/30.
 */
public class ChatBll
{
	/**
	 * 使用im前必须登录，传递一个唯一标识符的clientId
	 * 因为内部是异步方式，所以在主线程调用即可
	 *
	 * @param clientId
	 * @return
	 */
	public Observable<AVIMClient> login(final String clientId)
	{
		return Observable.create(new Observable.OnSubscribe<AVIMClient>()
		{
			@Override
			public void call(final Subscriber<? super AVIMClient> subscriber)
			{
				AVImClientManager.getInstance().open(clientId, new AVIMClientCallback()
				{
					@Override
					public void done(AVIMClient avimClient, AVIMException e)
					{
						if (e == null)
						{
							subscriber.onNext(avimClient);
						} else
						{
							e.printStackTrace();
							Logger.e("error:" + e);
							subscriber.onError(e);
						}
					}
				});
			}
		});
	}

	public Observable<Void> sendMessage(final AVIMMessage message, final AVIMConversation
					conversation)
	{
		return Observable.create(new Observable.OnSubscribe<Void>()
		{
			@Override
			public void call(final Subscriber<? super Void> subscriber)
			{
				conversation.sendMessage(message, AVIMConversation.NONTRANSIENT_MESSAGE_FLAG, new
								AVIMConversationCallback()//发送消息，当用户不在时会进行推送
								{
									@Override
									public void done(AVIMException e)
									{
										if (e == null)
										{
											subscriber.onCompleted();
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
	 * 获取最新的指定数量聊天记录
	 *
	 * @return
	 */
	public Observable<List<AVIMMessage>> getNewlyMessages(final int pageSize, final AVIMConversation
					conversation)
	{
		return Observable.create(new Observable.OnSubscribe<List<AVIMMessage>>()
		{
			@Override
			public void call(final Subscriber<? super List<AVIMMessage>> subscriber)
			{
				conversation.queryMessages(pageSize, new AVIMMessagesQueryCallback()
				{
					@Override
					public void done(List<AVIMMessage> list, AVIMException e)
					{
						if (e == null)
						{
							subscriber.onNext(list);
						} else
						{
							subscriber.onError(e);
						}
					}
				});
			}
		});
	}


	public Observable<List<AVIMMessage>> getHistoryMessages(final int pageSize, final AVIMMessage
					oldestMessage, final AVIMConversation conversation)
	{
		return Observable.create(new Observable.OnSubscribe<List<AVIMMessage>>()
		{
			@Override
			public void call(final Subscriber<? super List<AVIMMessage>> subscriber)
			{
				conversation.queryMessages(oldestMessage.getMessageId(), oldestMessage.getTimestamp(),
								pageSize, new AVIMMessagesQueryCallback()
								{
									@Override
									public void done(List<AVIMMessage> list, AVIMException e)
									{
										if (e == null)
										{
											subscriber.onNext(list);
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
	 * 压缩上传
	 *
	 * @return
	 */
	public Observable<AVFile> compressImageAndUpload(String path, int maxWidth, int maxHeight)
	{
		return compressImage(path, maxWidth, maxHeight)
						.observeOn(Schedulers.io())
						.subscribeOn(Schedulers.computation())
						.flatMap(new Func1<AVFile, Observable<AVFile>>()
						{
							@Override
							public Observable<AVFile> call(AVFile avFile)
							{
								return uploadFile(avFile);
							}
						});


	}

	public Observable<AVFile> uploadFile(final AVFile file)
	{
		return Observable.create(new Observable.OnSubscribe<AVFile>()
		{
			@Override
			public void call(Subscriber<? super AVFile> subscriber)
			{
				try
				{
					file.save();
					subscriber.onNext(file);
				} catch (AVException e)
				{
					subscriber.onError(e);
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 压缩聊天中的图片
	 */
	public Observable<AVFile> compressImage(String path, int maxWidth, int maxHeight)
	{
		Bitmap bitmap = FileUtils.compressImage(path, maxWidth, maxHeight);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 60, os);
		//构造 message
		AVFile file = new AVFile(System.currentTimeMillis() + "", os.toByteArray());
		file.addMetaData("width", bitmap.getWidth());
		file.addMetaData("height", bitmap.getHeight());
		return Observable.just(file);
	}
}
