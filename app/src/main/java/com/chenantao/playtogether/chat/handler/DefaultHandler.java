package com.chenantao.playtogether.chat.handler;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.chat.utils.NotificationBroadcastReceiver;
import com.chenantao.playtogether.chat.utils.NotificationUtils;
import com.chenantao.playtogether.utils.SpUtils;

/**
 * Created by Chenantao_gg on 2016/1/31.
 * 主要是处理当不在聊天界面的时候处理未读数量的handler
 * <p/>
 * 为了用户退出应用后仍能保持住客户端client，防止打开应用时重新登录耗时，
 * 把client保存在default handler中，如果手机强行杀死service，那就只能
 * 重新登录了。暂时无更优雅的解决办法，以后有的话再说
 */
public class DefaultHandler extends AVIMMessageHandler
{
	private Context mContext;
	//im的client
	public static AVIMClient mClient;

	public DefaultHandler(Context context)
	{
		mContext = context;
	}

	@Override
	public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client)
	{
		//将conversation id作为key，将未读数量从sp中取出，增加1
		SpUtils.increment(mContext, conversation.getConversationId(), 1);
		sendNotification(message, conversation);
	}

	private void sendNotification(AVIMMessage message, AVIMConversation conversation)
	{
		if (message instanceof AVIMTextMessage)
		{
			String notificationContent = ((AVIMTextMessage) message).getText();
			Intent intent = new Intent(mContext, NotificationBroadcastReceiver.class);
			intent.putExtra(ChatConstant.CONVERSATION_ID, conversation.getConversationId());
			int chatType = (int) conversation.getAttribute(ChatConstant.KEY_CHAT_TYPE);
			String conversationName ;
			if (chatType == ChatConstant.TYPE_SINGLE_CHAT)
			{
				conversationName = message.getFrom();
				intent.putExtra(ChatConstant.CONVERSATION_NAME, conversationName);
			} else
			{
				//如果是群聊
				conversationName = conversation.getName();
				intent.putExtra(ChatConstant.CONVERSATION_NAME, conversationName);
			}
//			intent.putExtra(ChatConstant.MEMBER_ID, message.getFrom());
			// TODO: 2016/2/1 获取		来信人的头像bitmap
			NotificationUtils.showNotification(mContext, conversationName, notificationContent, null,
							intent, null);

		}
	}
}
