package com.chenantao.playtogether.chat.event;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

/**
 * Created by Chenantao_gg on 2016/1/31.
 */
public class EventReceiveMessage
{
	public AVIMMessage message;

	public AVIMConversation conversation;

	public EventReceiveMessage(AVIMMessage message, AVIMConversation conversation)
	{
		this.message = message;
		this.conversation = conversation;
	}
}
