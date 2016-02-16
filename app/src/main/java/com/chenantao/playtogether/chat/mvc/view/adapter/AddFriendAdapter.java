package com.chenantao.playtogether.chat.mvc.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/2/15.
 */
public class AddFriendAdapter extends RecyclerView.Adapter
{
	private List<User> mDatas;
	private Context mContext;

	private OnAddBtnClickListener mListener;

	public static final int TYPE_HEADER = 0;
	public static final int TYPE_ITEM = 1;

	public AddFriendAdapter(Context mContext, List<User> mDatas)
	{
		this.mContext = mContext;
		this.mDatas = mDatas;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (viewType == TYPE_HEADER)
			return new AddFriendHeader(LayoutInflater.from(mContext).inflate(R.layout
							.item_add_friend_header, parent, false));
		return new AddFriendItem(LayoutInflater.from(mContext).inflate(R.layout.item_friend_list,
						parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		if (position == 0)
		{
			final AddFriendHeader headerHolder = (AddFriendHeader) holder;
			headerHolder.mBtnAdd.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String name = headerHolder.mEtName.getText().toString();
					if (mListener != null && !TextUtils.isEmpty(name))
					{
						mListener.onClick(name, null);
					} else
					{
						Toast.makeText(mContext, "姓名不能为空", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return;
		}
		AddFriendItem itemHolder = (AddFriendItem) holder;
		final User user = mDatas.get(position - 1);
		itemHolder.mCb.setVisibility(View.GONE);
		itemHolder.mBtnAdd.setVisibility(View.VISIBLE);
		itemHolder.mMaterialRippleLayout.setClickable(false);
		String avatarUrl = user.getAvatarUrl();
		if (avatarUrl != null)
			PicassoUtils.displayFitImage(mContext, Uri.parse(avatarUrl), itemHolder.mIvAvatar, null);
		itemHolder.mTvSimpleDesc.setText(user.getSimpleDesc());
		itemHolder.mTvUsername.setText(user.getUsername());
		itemHolder.mBtnAdd.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onClick(null, user);
				}
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size() + 1;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position == 0)
			return TYPE_HEADER;
		return TYPE_ITEM;
	}

	public void setOnAddBtnClick(OnAddBtnClickListener listener)
	{
		mListener = listener;
	}

	public interface OnAddBtnClickListener
	{
		void onClick(String name, User user);
	}

	class AddFriendHeader extends RecyclerView.ViewHolder
	{
		@Bind(R.id.etName)
		EditText mEtName;
		@Bind(R.id.btnAddFriend)
		ButtonFlat mBtnAdd;
		public AddFriendHeader(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	class AddFriendItem extends RecyclerView.ViewHolder
	{
		@Bind(R.id.ivAvatar)
		RoundedImageView mIvAvatar;
		@Bind(R.id.tvUsername)
		TextView mTvUsername;
		@Bind(R.id.cb)
		CheckBox mCb;
		@Bind(R.id.tvSimpleDesc)
		TextView mTvSimpleDesc;
		@Bind(R.id.btnAdd)
		Button mBtnAdd;
		@Bind(R.id.materialRippleLayout)
		MaterialRippleLayout mMaterialRippleLayout;


		public AddFriendItem(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
