package com.chenantao.playtogether.mvc.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.widget.MultipleShapeImg;
import com.chenantao.playtogether.utils.PicassoUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/1/27.
 */
public class InvitationCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private static final int TYPE_HEADER_VIEW = 0;
	private static final int TYPE_ITEM_VIEW = 1;
	private static final int POSITION_HEADER = 0;

	private Context mContext;
	private List<Invitation> mDatas;
	private int mCount;//真实item的数量，要比mDatas对1，因为还有个header

	public InvitationCategoryAdapter(Context context, List<Invitation> datas)
	{
		mContext = context;
		mDatas = datas;
		mCount = mDatas.size() + 1;
	}

	private boolean isHeader(int position)
	{
		return position == POSITION_HEADER;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (viewType == TYPE_HEADER_VIEW)
		{
			return new InvitationCategoryHeaderViewHolder(LayoutInflater.from(mContext).inflate(
					R.layout.item_invitation_category_header, parent, false));
		} else
		{
			return new InvitationCategoryItemViewHolder(LayoutInflater.from(mContext).inflate(
					R.layout.item_home_invitation, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		AutoUtils.autoSize(holder.itemView);
		if (isHeader(position))
		{
			return;
		}
		Invitation invitation = mDatas.get(position - 1);//还有个header。要减1
		User author = invitation.getAuthor();
		AVFile avatar = author.getAvatar();
		InvitationCategoryItemViewHolder itemViewHolder = (InvitationCategoryItemViewHolder)
				holder;
		itemViewHolder.mTvCategory.setVisibility(View.GONE);
		itemViewHolder.mTvContent.setText(invitation.getContent());
		itemViewHolder.mTvTitle.setText(invitation.getTitle());
		itemViewHolder.mTvUsername.setText(author.getUsername());
		if (avatar != null)
		{
			Uri uri = Uri.parse(avatar.getThumbnailUrl(true, 150, 150));
			PicassoUtils.displayFitImage(mContext, uri, itemViewHolder.mIvAuthorAvatar, null);
		}else
		{
			itemViewHolder.mIvAuthorAvatar.setImageResource(R.mipmap.avatar);
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		if (isHeader(position))
		{
			return TYPE_HEADER_VIEW;
		} else
		{
			return TYPE_ITEM_VIEW;
		}
	}

	@Override
	public int getItemCount()
	{
		return mCount;
	}

	class InvitationCategoryItemViewHolder extends RecyclerView.ViewHolder
	{
		@Bind(R.id.ivAuthorAvatar)
		MultipleShapeImg mIvAuthorAvatar;
		@Bind(R.id.tvUsername)
		TextView mTvUsername;
		@Bind(R.id.tvCategory)
		TextView mTvCategory;
		@Bind(R.id.tvTitle)
		TextView mTvTitle;
		@Bind(R.id.tvContent)
		TextView mTvContent;

		public InvitationCategoryItemViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	class InvitationCategoryHeaderViewHolder extends RecyclerView.ViewHolder
	{

		@Bind(R.id.tvAddress)
		TextView mTvAddress;

		public InvitationCategoryHeaderViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}
