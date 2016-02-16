package com.chenantao.playtogether.chat.mvc.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.gc.materialdesign.views.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/2/6.
 */
public class SelectMemberAdapter extends RecyclerView.Adapter<SelectMemberAdapter.SelectMemberViewHolder>
{
	private List<User> mDatas;
	private Context mContext;

	private List<User> mSelectedUser;

	public SelectMemberAdapter(Context mContext, List<User> mDatas)
	{
		this.mDatas = mDatas;
		this.mContext = mContext;
		this.mSelectedUser = new ArrayList<>();
	}

	@Override
	public SelectMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		return new SelectMemberViewHolder(LayoutInflater.from(mContext).inflate(R.layout
						.item_friend_list, parent, false));
	}

	@Override
	public void onBindViewHolder(SelectMemberViewHolder holder, int position)
	{
		AutoUtils.autoSize(holder.itemView);
		final User user = mDatas.get(position);
		if (user.getAvatarUrl() != null)
		{
			PicassoUtils.displayFitImage(mContext, Uri.parse(user.getAvatarUrl()), holder.mIvAvatar,
							null);
		}
		holder.mTvUsername.setText(user.getUsername());
		holder.mCb.setVisibility(View.VISIBLE);
		holder.mCb.setOncheckListener(new CheckBox.OnCheckListener()
		{
			@Override
			public void onCheck(CheckBox checkBox, boolean checked)
			{
				if (checked)
					mSelectedUser.add(user);
				else mSelectedUser.remove(user);
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	public List<User> getSelectMember()
	{
		return mSelectedUser;
	}

	class SelectMemberViewHolder extends RecyclerView.ViewHolder
	{
		@Bind(R.id.ivAvatar)
		ImageView mIvAvatar;
		@Bind(R.id.tvUsername)
		TextView mTvUsername;
		@Bind(R.id.cb)
		CheckBox mCb;

		public SelectMemberViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
