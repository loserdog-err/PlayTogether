package com.chenantao.playtogether.chat.handler;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.event.EventReceiveMessage;

import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/30.
 * 处理聊天界面接收到的信息
 */
public class ChatHandler extends AVIMMessageHandler
{

	public ChatHandler()
	{
	}

	@Override
	public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client)
	{
		String clientId;
		clientId = AVImClientManager.getInstance().getClientId();
		if (client.getClientId().equals(clientId))
		{
			if (!message.getFrom().equals(clientId))
			{
//				mChatAdapter.addData(message);
				EventBus.getDefault().post(new EventReceiveMessage(message, conversation));
			}

		}

	}

}
