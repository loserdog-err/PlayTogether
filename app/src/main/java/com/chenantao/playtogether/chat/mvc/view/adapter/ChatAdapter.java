package com.chenantao.playtogether.chat.mvc.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.view.viewholder.ChatCommonViewHolder;
import com.chenantao.playtogether.chat.mvc.view.viewholder.ChatFromHolderChat;
import com.chenantao.playtogether.chat.mvc.view.viewholder.ChatToHolderChat;
import com.chenantao.playtogether.chat.utils.ChatConstant;

import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/29.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

	public static final int TYPE_FROM_MESSAGE = 0;
	public static final int TYPE_TO_MESSAGE = 1;

	private Context mContext;
	private List<AVIMMessage> mDatas;

	private AVIMConversation mConversation;

	public ChatAdapter(Context context, List<AVIMMessage> datas, AVIMConversation conversation)
	{
		this.mContext = context;
		this.mDatas = datas;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		switch (viewType)
		{
			case TYPE_FROM_MESSAGE:
				return new ChatFromHolderChat(LayoutInflater.from(mContext).inflate(R.layout
								.item_chat_from_message, parent, false), mContext, mConversation);
			case TYPE_TO_MESSAGE:
				return new ChatToHolderChat(LayoutInflater.from(mContext).inflate(R.layout
								.item_chat_to_message, parent, false), mContext, mConversation);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		AutoUtils.autoSize(holder.itemView);
		AVIMMessage message = mDatas.get(position);
		((ChatCommonViewHolder) holder).bindData(message, shouldShowTime(position));
	}

	@Override
	public int getItemViewType(int position)
	{
		AVIMMessage message = mDatas.get(position);
		if (message.getFrom() != null && message.getFrom().equals(AVImClientManager.getInstance()
						.getClientId()))//是发送出去的消息
		{
			return TYPE_TO_MESSAGE;
		} else
		{
			return TYPE_FROM_MESSAGE;
		}
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}


	public void addData(AVIMMessage message)
	{
		mDatas.add(message);
		notifyItemInserted(mDatas.size());
	}

	public void addDatasToBottom(List<AVIMMessage> messages)
	{
		int startIndex = mDatas.size();
		mDatas.addAll(messages);
		notifyItemRangeInserted(startIndex, messages.size());
	}

	public void addDatasToHeader(List<AVIMMessage> messages)
	{
		mDatas.addAll(0, messages);
		notifyItemRangeInserted(0, messages.size());
	}

	private boolean shouldShowTime(int position)
	{
		if (position == 0) return true;
		long lastTime = mDatas.get(position - 1).getTimestamp();
		long now = mDatas.get(position).getTimestamp();
		return now - lastTime > ChatConstant.CHAT_SHOW_TIME_INTERVAL;
	}
}
