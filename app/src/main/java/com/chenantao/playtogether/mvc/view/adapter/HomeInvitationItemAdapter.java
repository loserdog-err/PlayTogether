package com.chenantao.playtogether.mvc.view.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationCategoryActivity;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.PicassoUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class HomeInvitationItemAdapter extends RecyclerView.Adapter implements View.OnClickListener
{

	private static final int TYPE_HEADER_VIEW = 0;
	private static final int TYPE_ITEM_VIEW = 1;
	private static final int POSITION_HEADER = 0;
	private List<Invitation> mDatas;
	private Context mContext;
	private int mCount;//因为包含一个header，所以实际数量要比item多1
	private int offsetIndex = 1;

	public HomeInvitationItemAdapter(Context context, List<Invitation> datas)
	{
		this.mDatas = datas;
		this.mContext = context;
		mCount = datas.size() + 1;
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
			return new HomeInvitationHeaderHolder(LayoutInflater.from(mContext).inflate(R.layout
					.item_home_invitation_header, null));
		}
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_invitation, parent,
				false);
		return new HomeInvitationItemViewHolder(view);
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		AutoUtils.auto(holder.itemView);
		if (isHeader(position))
		{
			HomeInvitationHeaderHolder headerHolder = (HomeInvitationHeaderHolder) holder;
			headerHolder.mLlExercise.setOnClickListener(this);
			headerHolder.mLlFood.setOnClickListener(this);
			headerHolder.mLlMovie.setOnClickListener(this);
			return;
		}
		//因为包含一个header，所以取数据的时候要从position减掉1的偏移量
		int dataPos = position - offsetIndex;
		final Invitation invitation = mDatas.get(dataPos);
		final HomeInvitationItemViewHolder itemHolder = (HomeInvitationItemViewHolder) holder;
		itemHolder.mIvAvatar.setImageResource(R.mipmap.avatar);
		itemHolder.mTvTitle.setText(invitation.getTitle());
		itemHolder.mTvContent.setText(invitation.getContent());
		itemHolder.mTvCategory.setText(mContext.getResources().getString(R.string.from_category,
				invitation.getCategory()));
		itemHolder.mTvUsername.setText(invitation.getAuthor().getUsername());
		//设置用户头像
		AVFile avFile = invitation.getAuthor().getAvatar();
		if (avFile != null)
		{
			Uri avatarUri = Uri.parse(avFile.getThumbnailUrl(true, Constant.AVATAR_WIDTH,
					Constant.AVATAR_HEIGHT));
			PicassoUtils.displayFitImage(mContext, avatarUri, itemHolder.mIvAvatar, null);
		} else
		{
			itemHolder.mIvAvatar.setImageResource(R.mipmap.avatar);
		}
		itemHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, InvitationDetailActivity.class);
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_ID, invitation
						.getObjectId());
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_USERNAME, invitation
						.getAuthor().getUsername());
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_TITLE, invitation
						.getTitle());
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_AVATAR, (
						(BitmapDrawable) itemHolder.mIvAvatar.getDrawable()).getBitmap());
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
				{
					ActivityOptions options =
							ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
									Pair.create((View) itemHolder.mIvAvatar, mContext
											.getResources().getString(R.string
													.share_element_avatar)),
									Pair.create((View) itemHolder.mTvTitle, mContext
											.getResources().getString(R.string
													.share_element_title)),
									Pair.create((View) itemHolder.mTvUsername, mContext
											.getResources().getString(R.string
													.share_element_username)));
					mContext.startActivity(intent, options.toBundle());

				} else
				{
					mContext.startActivity(intent);
				}
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return mCount;
	}

	/**
	 * 主要类型选项的单击事件
	 *
	 * @param v
	 */
	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		switch (v.getId())
		{
			case R.id.llFood:
				intent = new Intent(mContext, InvitationCategoryActivity.class);
				intent.putExtra(InvitationCategoryActivity.EXTRA_CATEGORY, Constant.CATEGORY_FOOD);
				mContext.startActivity(intent);
				break;
			case R.id.llMovie:
				intent = new Intent(mContext, InvitationCategoryActivity.class);
				intent.putExtra(InvitationCategoryActivity.EXTRA_CATEGORY, Constant
						.CATEGORY_MOVIE);
				mContext.startActivity(intent);
				break;
			case R.id.llExercise:
				intent = new Intent(mContext, InvitationCategoryActivity.class);
				intent.putExtra(InvitationCategoryActivity.EXTRA_CATEGORY, Constant
						.CATEGORY_EXERCISE);
				mContext.startActivity(intent);
				break;
		}
	}

	public class HomeInvitationHeaderHolder extends RecyclerView.ViewHolder
	{

		@Bind(R.id.llMovie)
		LinearLayout mLlMovie;
		@Bind(R.id.llExercise)
		LinearLayout mLlExercise;
		@Bind(R.id.llFood)
		LinearLayout mLlFood;

		public HomeInvitationHeaderHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public class HomeInvitationItemViewHolder extends RecyclerView.ViewHolder
	{

		@Bind(R.id.ivAuthorAvatar)
		ImageView mIvAvatar;
		@Bind(R.id.tvTitle)
		TextView mTvTitle;
		@Bind(R.id.tvUsername)
		TextView mTvUsername;
		@Bind(R.id.tvContent)
		TextView mTvContent;
		@Bind(R.id.tvFlag)
		TextView mTvCategory;

		public HomeInvitationItemViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
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
}
