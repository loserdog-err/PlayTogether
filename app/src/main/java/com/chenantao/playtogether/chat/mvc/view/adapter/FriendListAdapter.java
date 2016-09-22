package com.chenantao.playtogether.chat.mvc.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.gc.materialdesign.views.CheckBox;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/2/6.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>
{
	private Context mContext;
	private List<User> mDatas;
	private OnItemClickListener mListener;

	public FriendListAdapter(Context mContext, List<User> mDatas)
	{
		this.mContext = mContext;
		this.mDatas = mDatas;
	}

	@Override
	public FriendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		return new FriendListViewHolder(LayoutInflater.from(mContext).inflate(R.layout
						.item_friend_list, parent, false));
	}

	@Override
	public void onBindViewHolder(final FriendListViewHolder holder, final int position)
	{
		AutoUtils.autoSize(holder.itemView);
		User user = mDatas.get(position);
		holder.mCb.setVisibility(View.GONE);
		holder.mTvUsername.setText(user.getUsername());
		holder.mTvSimpleDesc.setText(user.getSimpleDesc());
		AVFile avatar = user.getAvatar();
		if (avatar != null)
			PicassoUtils.displayFitImage(mContext, Uri.parse(avatar.getThumbnailUrl(true, Constant
							.AVATAR_WIDTH, Constant.AVATAR_HEIGHT)), holder.mIvAvatar, null);
		else holder.mIvAvatar.setImageResource(R.mipmap.avatar);
		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mListener != null)
					mListener.onClick(holder, position);
			}
		});
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mListener = listener;
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	public interface OnItemClickListener
	{
		void onClick(FriendListViewHolder holder, int position);
	}

	public class FriendListViewHolder extends RecyclerView.ViewHolder
	{

		@Bind(R.id.ivAvatar)
		ImageView mIvAvatar;
		@Bind(R.id.tvUsername)
		TextView mTvUsername;
		@Bind(R.id.cb)
		CheckBox mCb;
		@Bind(R.id.tvSimpleDesc)
		TextView mTvSimpleDesc;

		public FriendListViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
