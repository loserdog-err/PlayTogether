package com.chenantao.playtogether.chat.handler;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.event.EventHomeConversationChange;
import com.chenantao.playtogether.chat.utils.NotificationUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/2/2.
 * 处理主界面接收消息的handler
 */
public class ChatHomeHandler extends AVIMMessageHandler
{

	@Override
	public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client)
	{
		String clientId;
		clientId = AVImClientManager.getInstance().getClientId();
		if (client.getClientId().equals(clientId))
		{
			if (message.getFrom().equals(clientId))
			{
				//如果是自己发送出去的消息，是不用更新未读数量的
				EventBus.getDefault().post(new EventHomeConversationChange(false, conversation));
			} else
			{
				//需要判断一下聊天界面是否可见，如果可见，也不用更新未读数量
				if (NotificationUtils.isShowNotification(conversation.getConversationId()))//会话不可见，更新未读数量
				{
					EventBus.getDefault().post(new EventHomeConversationChange(true, conversation));
				} else//会话可见
				{
					EventBus.getDefault().post(new EventHomeConversationChange(false, conversation));
				}
			}
		}

	}
}
