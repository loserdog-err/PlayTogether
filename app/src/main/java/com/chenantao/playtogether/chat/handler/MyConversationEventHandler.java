package com.chenantao.playtogether.chat.handler;

import android.content.Context;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.chenantao.playtogether.chat.event.EventMemberLeft;
import com.chenantao.playtogether.utils.SpUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/31.
 *
 */
public class MyConversationEventHandler extends AVIMConversationEventHandler
{
	private Context mContext;

	public MyConversationEventHandler(Context context)
	{
		mContext = context;
	}

	@Override
	public void onMemberLeft(AVIMClient avimClient, AVIMConversation avimConversation,
													 List<String> list, String s)
	{
		Logger.e("member left");
		EventBus.getDefault().post(new EventMemberLeft(avimConversation));
	}

	@Override
	public void onMemberJoined(AVIMClient avimClient, AVIMConversation avimConversation, List
					<String> list, String s)
	{
	}

	@Override
	public void onKicked(AVIMClient avimClient, AVIMConversation avimConversation, String s)
	{
	}

	@Override
	public void onInvited(AVIMClient avimClient, AVIMConversation avimConversation, String s)
	{
	}

	@Override
	public void onOfflineMessagesUnread(AVIMClient client, AVIMConversation conversation, int
					unreadCount)
	{
		super.onOfflineMessagesUnread(client, conversation, unreadCount);
		Logger.e("unreadCount:" + unreadCount);
		//将未读消息数量累加进存储在sp中的未读数量
		SpUtils.increment(mContext, conversation.getConversationId(), unreadCount);
	}
}
