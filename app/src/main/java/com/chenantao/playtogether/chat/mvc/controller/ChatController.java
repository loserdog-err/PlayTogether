package com.chenantao.playtogether.chat.mvc.controller;

import android.app.Activity;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.chat.mvc.bll.ChatBll;
import com.chenantao.playtogether.chat.mvc.bll.ConversationBll;
import com.chenantao.playtogether.chat.event.EventHadRead;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.utils.ScreenUtils;
import com.chenantao.playtogether.utils.SpUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenantao_gg on 2016/1/30.
 */
public class ChatController
{
	private ChatActivity mActivity;
	@Inject
	ChatBll mChatBll;


	@Inject
	ConversationBll mConversationBll;

	private AVIMConversation mConversation;
	private AVIMMessage mOldestMessage;

	@Inject
	public ChatController(Activity chatActivity)
	{
		mActivity = (ChatActivity) chatActivity;
	}

	public void sendMessage(AVIMMessage message)
	{
		Observable.just(message)
						.observeOn(Schedulers.computation())
						.flatMap(new Func1<AVIMMessage, Observable<AVIMMessage>>()
						{
							@Override
							public Observable<AVIMMessage> call(AVIMMessage message)
							{
								if (message instanceof AVIMImageMessage)//如果是图片，查看是否发送原图，不是的话进行压缩
								{
									boolean isOriginal = (boolean) ((AVIMImageMessage) message).getAttrs().get
													(ChatConstant.KEY_IS_ORIGINAL);
									Logger.e("isOriginal:" + isOriginal);
									if (!isOriginal)
										return compressImage((AVIMImageMessage) message);
								}
								return Observable.just(message);
							}
						})
						.observeOn(AndroidSchedulers.mainThread())
						.flatMap(new Func1<AVIMMessage, Observable<Void>>()
						{
							@Override
							public Observable<Void> call(AVIMMessage message)
							{
								return mChatBll.sendMessage(message, mConversation);
							}
						})
						.subscribeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<Void>()
						{
							@Override
							public void call(Void aVoid)
							{
								mActivity.sendMessageSuccess();
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								mActivity.sendMessageFail("发送失败：" + throwable.getMessage());
								throwable.printStackTrace();
							}
						});
	}

	/**
	 * 获取最新的消息记录
	 */
	public void getNewlyMessageData(final String conversationId)
	{
		mConversationBll.getConversationById(conversationId,
						AVImClientManager.getInstance().getClient())
						.subscribeOn(AndroidSchedulers.mainThread())
						.observeOn(AndroidSchedulers.mainThread())
						.doOnNext(new Action1<AVIMConversation>()
						{
							@Override
							public void call(AVIMConversation conversation)
							{
								//将未读的消息数量置0
								int count = SpUtils.getIntProperty(mActivity, conversation
												.getConversationId());
								if (count != -1)
								{
									count = 0;
									SpUtils.setIntProperty(mActivity, conversation.getConversationId(), count);
								}
								mConversation = conversation;
								EventBus.getDefault().post(new EventHadRead(conversation.getConversationId()));
							}
						})
						.flatMap(new Func1<AVIMConversation, Observable<List<AVIMMessage>>>()
						{
							@Override
							public Observable<List<AVIMMessage>> call(AVIMConversation conversation)
							{
								return mChatBll.getNewlyMessages(ChatConstant.CHAT_PAGE_SIZE, conversation);
							}
						})
						.subscribe(new Action1<List<AVIMMessage>>()
						{
							@Override
							public void call(List<AVIMMessage> messages)
							{
//								Logger.e("newly data:" + messages.size());
								mActivity.getMessageSuccess(messages, mConversation, true);
								if (!messages.isEmpty()) mOldestMessage = messages.get(0);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								mActivity.getMessageDataFail("获取消息失败:" + throwable.getMessage());
								throwable.printStackTrace();
							}
						});
	}

	public void getHistoryMessageData()
	{
		if (mOldestMessage == null)
		{
			Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT).show();
			mActivity.getMessageDataFail("没有更多数据了");
			return;
		}
		mChatBll.getHistoryMessages(ChatConstant.CHAT_PAGE_SIZE, mOldestMessage, mConversation)
						.subscribeOn(AndroidSchedulers.mainThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<List<AVIMMessage>>()
						{
							@Override
							public void call(List<AVIMMessage> messages)
							{
								Logger.e("history data:" + messages.size());
								mActivity.getMessageSuccess(messages, mConversation, false);
								if (messages.size() < ChatConstant.CHAT_PAGE_SIZE) mOldestMessage = null;
								else mOldestMessage = messages.get(0);
								Logger.e("oldest msg:" + mOldestMessage);
							}
						}, new Action1<Throwable>()
						{
							@Override
							public void call(Throwable throwable)
							{
								mActivity.getMessageDataFail("获取消息失败:" + throwable.getMessage());
								throwable.printStackTrace();
							}
						});
	}

	/**
	 * 压缩图片
	 */
	public Observable<AVIMMessage> compressImage(AVIMImageMessage message)
	{
		//压缩上传的图片并重新构造 message
		int maxWidth = (int) (ScreenUtils.getScreenWidth(mActivity) * ChatConstant
						.MESSAGE_PIC_WIDTH_MAX_RATIO);
		int maxHeight = (int) (ScreenUtils.getScreenHeight(mActivity) * ChatConstant
						.MESSAGE_PIC_HEIGHT_MAX_RATIO);
		return mChatBll.compressImageAndUpload(message.getLocalFilePath(), maxWidth, maxHeight)
						.observeOn(AndroidSchedulers.mainThread())
						.flatMap(new Func1<AVFile, Observable<AVIMMessage>>()
						{
							@Override
							public Observable<AVIMMessage> call(AVFile file)
							{
								AVIMMessage msg = new AVIMImageMessage(file);
								return Observable.just(msg);
							}
						});
	}
}
