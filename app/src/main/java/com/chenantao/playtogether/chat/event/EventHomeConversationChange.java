package com.chenantao.playtogether.chat.event;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by Chenantao_gg on 2016/2/2.
 */
public class EventHomeConversationChange
{
	public EventHomeConversationChange(boolean isUpdateUnreadCount, AVIMConversation conversation)
	{
		this.isUpdateUnreadCount = isUpdateUnreadCount;
		this.conversation = conversation;
	}

	public boolean isUpdateUnreadCount = false;

	public AVIMConversation conversation;
}
