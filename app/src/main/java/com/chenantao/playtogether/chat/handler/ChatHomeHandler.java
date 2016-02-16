package com.chenantao.playtogether.chat.handler;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.event.EventHomeConversationChange;
import com.orhanobut.logger.Logger;

import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/2/2.
 * 处理主界面接收消息的handler
 */
public class ChatHomeHandler extends AVIMMessageHandler
{
	public ChatHomeHandler()
	{
	}

	@Override
	public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client)
	{
		Logger.e("on Message");
		String clientId;
		clientId = AVImClientManager.getInstance().getClientId();
		if (client.getClientId().equals(clientId))
		{
			if (!message.getFrom().equals(clientId))
			{
				EventBus.getDefault().post(new EventHomeConversationChange(true, conversation));
			}
		}

	}
}
