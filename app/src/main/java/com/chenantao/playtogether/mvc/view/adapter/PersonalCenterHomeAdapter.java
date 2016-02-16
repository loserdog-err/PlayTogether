package com.chenantao.playtogether.mvc.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.PersonalCenterHomeBean;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.viewholder.CommonViewHolder;
import com.chenantao.playtogether.mvc.view.viewholder.PersonalCenterDynamicVH;
import com.chenantao.playtogether.mvc.view.viewholder.PersonalCenterHeaderVH;
import com.chenantao.playtogether.mvc.view.viewholder.PersonalCenterSubHeaderVH;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public class PersonalCenterHomeAdapter extends RecyclerView.Adapter
{

	public static final int TYPE_HEADER = 0;
	public static final int TYPE_SUB_HEADER = 1;
	public static final int TYPE_ITEM = 2;


	private Context mContext;
	private PersonalCenterHomeBean mData;

	private OnChatBtnClickListener mListener;

	public PersonalCenterHomeAdapter(Context mContext, PersonalCenterHomeBean mData)
	{
		this.mContext = mContext;
		this.mData = mData;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (viewType == TYPE_HEADER)
			return new PersonalCenterHeaderVH(LayoutInflater.from(mContext).inflate(R.layout
							.item_personal_center_header, parent, false));
		if (viewType == TYPE_SUB_HEADER)
			return new PersonalCenterSubHeaderVH(LayoutInflater.from(mContext).inflate(R.layout
							.item_personal_center_subheader, parent, false));
		return new PersonalCenterDynamicVH(LayoutInflater.from(mContext).inflate(R.layout
						.item_personal_center_dynamic, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		AutoUtils.autoSize(holder.itemView);
		if (position == 0 || position == 1)
			((CommonViewHolder) holder).bindData(mData);
		else
		{
			Invitation invitation = mData.getNewlyDynamic().get(position - 2);
			((CommonViewHolder) holder).bindData(invitation);
		}
		//聊天按钮的单击事件
		if (position == 0)
		{
			((PersonalCenterHeaderVH) holder).mBtnChat.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mListener.onClick(mData.getUser());
				}
			});
		}
	}

	@Override
	public int getItemCount()
	{
		return mData.getNewlyDynamic().size() + 2;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position == 0) return TYPE_HEADER;
		if (position == 1) return TYPE_SUB_HEADER;
		return TYPE_ITEM;
	}

	public void setOnChatBtnClickListener(OnChatBtnClickListener listener)
	{
		mListener = listener;
	}

	public interface OnChatBtnClickListener
	{
		void onClick(User author);
	}


}

