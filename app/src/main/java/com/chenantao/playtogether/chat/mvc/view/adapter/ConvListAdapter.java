package com.chenantao.playtogether.chat.mvc.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.mvc.view.viewholder.ConversationViewHolder;
import com.chenantao.playtogether.utils.SpUtils;
import com.jauker.widget.BadgeView;

import java.util.List;

/**
 * Created by Chenantao_gg on 2016/2/1.
 */
public class ConvListAdapter extends RecyclerView.Adapter
{
	private List<AVIMConversation> mDatas;
	private Context mContext;
	private OnItemLongClickListener mListener;


	public ConvListAdapter(Context context, List<AVIMConversation> datas)
	{
		this.mDatas = datas;
		this.mContext = context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		return new ConversationViewHolder(LayoutInflater.from(mContext).inflate(R.layout
						.item_chat_conversation, parent, false), mContext);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
	{
		AutoUtils.autoSize(holder.itemView);
		String conversationId = mDatas.get(position).getConversationId();
		((ConversationViewHolder) holder).bindData(mDatas.get(position));
		//设置提示未读信息数量的小红点
		int unreadCount = SpUtils.getIntProperty(mContext, conversationId);
		ConversationViewHolder conversationViewHolder = (ConversationViewHolder) holder;
		if (unreadCount > 0)//没有关于这条conversation的未读消息或者未读消息为0，都不进行设置
		{
			if (conversationViewHolder.mBadgeView == null)
				conversationViewHolder.mBadgeView = new BadgeView(mContext);
			conversationViewHolder.mBadgeView.setTargetView(conversationViewHolder.mConversationIcon);
			conversationViewHolder.mBadgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
			conversationViewHolder.mBadgeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			conversationViewHolder.mBadgeView.setBadgeCount(unreadCount);
		} else
		{
			if (conversationViewHolder.mBadgeView != null)
				conversationViewHolder.mBadgeView.setText(null);
		}
		holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				//选项的长按事件
				if (mListener != null)
				{
					mListener.longClick(mDatas.get(position),holder.itemView);
				}
				return true;
			}
		});

	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	public void addConversations(List<AVIMConversation> conversations)
	{
		int startIndex = mDatas.size();
		mDatas.addAll(conversations);
		notifyItemRangeInserted(startIndex, conversations.size());
	}

	public void addConversation(AVIMConversation conversation)
	{
		int startIndex = mDatas.size();
		mDatas.add(conversation);
		notifyItemInserted(startIndex);

	}

	public void removeConversation(AVIMConversation conversation)
	{
		int index=mDatas.indexOf(conversation);
		if (index != -1)
		{
			mDatas.remove(conversation);
			notifyItemRemoved(index);
		}
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener)
	{
		mListener = listener;
	}

	public interface OnItemLongClickListener
	{
		void longClick(AVIMConversation conversation,View item);

	}
}
