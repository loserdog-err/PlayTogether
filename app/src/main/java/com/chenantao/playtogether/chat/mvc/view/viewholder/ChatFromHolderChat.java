package com.chenantao.playtogether.chat.mvc.view.viewholder;

import android.content.Context;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

/**
 * Created by Chenantao_gg on 2016/1/29.
 * 接收信息的viewholder
 */
public class ChatFromHolderChat extends ChatCommonViewHolder
{

	private AVIMConversation mConversation;

	public ChatFromHolderChat(View itemView, Context context, AVIMConversation conversation)
	{
		super(itemView, context);
		mConversation = conversation;
	}

	@Override
	public void bindData(AVIMMessage message, boolean shouldShowTime)
	{
		super.bindData(message, shouldShowTime);
//		if (mConversation.getAttribute(ChatConstant.KEY_CHAT_TYPE).equals(ChatConstant.TYPE_SINGLE_CHAT))
//		{
//			//如果是单聊,则把头像保存起来，省的每次都去查找
//		}
	}
}
