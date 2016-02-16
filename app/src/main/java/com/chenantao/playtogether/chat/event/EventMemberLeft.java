package com.chenantao.playtogether.chat.event;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by Chenantao_gg on 2016/2/13.
 */
public class EventMemberLeft
{
	public AVIMConversation conversation;

	public EventMemberLeft(AVIMConversation conversation)
	{
		this.conversation = conversation;
	}
}
