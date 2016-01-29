package com.chenantao.playtogether.mvc.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.balysv.materialripple.MaterialRippleLayout;
import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bean.event.EventLocate;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;
import com.chenantao.playtogether.mvc.view.widget.MultipleShapeImg;
import com.chenantao.playtogether.utils.LocationUtils;
import com.chenantao.playtogether.utils.PicassoUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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
		//处理header的逻辑，主要是定位
		if (isHeader(position))
		{
			final InvitationCategoryHeaderViewHolder headerViewHolder =
					(InvitationCategoryHeaderViewHolder) holder;
			headerViewHolder.mIvLocate.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					//将经纬度保存到服务器
					LocationUtils.getLocationClient(mContext.getApplicationContext()).start();
				}
			});
			if ("".equals(headerViewHolder.mTvAddress.getText().toString()))
			{
				//先取上次的地址设置进去,如果没有，在从服务器请求
				LocationClient client = LocationUtils.getLocationClient(mContext);
				BDLocation location = client.getLastKnownLocation();
				if (location == null)
				{
					client.registerLocationListener(new LocationListener(headerViewHolder));
					client.start();
				} else
				{
					headerViewHolder.mTvAddress.setText(location.getAddrStr());
				}
			}
			return;
		}
		//处理item
		final Invitation invitation = mDatas.get(position - 1);//还有个header。要减1
		final User author = invitation.getAuthor();
		AVFile avatar = author.getAvatar();
		final InvitationCategoryItemViewHolder itemViewHolder = (InvitationCategoryItemViewHolder)
				holder;
		//据算距离本屌的距离
		User user = AVUser.getCurrentUser(User.class);
		AVGeoPoint localPoint = user.getLocation();
		AVGeoPoint authorPoint = invitation.getLocation();
		//当item的作者不是本人，有位置坐标，并且还没有
		if (localPoint != null && authorPoint != null && !user.getUsername().equals(author
				.getUsername()))
		{
			int distance = (int) LocationUtils.getDistance(localPoint, authorPoint);//米
			itemViewHolder.mTvDistance.setText(mContext.getResources().getString(R.string
					.distance_from_me, distance));
		} else
		{
			itemViewHolder.mTvDistance.setText("");
		}
		final String title = invitation.getTitle();
		final String authorName = author.getUsername();
		itemViewHolder.mTvContent.setText(invitation.getContent());
		itemViewHolder.mTvTitle.setText(title);
		itemViewHolder.mTvUsername.setText(authorName);
		if (avatar != null)
		{
			Uri uri = Uri.parse(avatar.getThumbnailUrl(true, 150, 150));
			PicassoUtils.displayFitImage(mContext, uri, itemViewHolder.mIvAuthorAvatar, null);
		} else
		{
			itemViewHolder.mIvAuthorAvatar.setImageResource(R.mipmap.avatar);
		}
		itemViewHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, InvitationDetailActivity.class);
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_ID, invitation
						.getObjectId());
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_USERNAME, authorName);
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_TITLE, title);
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_AVATAR, (
						(BitmapDrawable) itemViewHolder.mIvAuthorAvatar.getDrawable()).getBitmap
						());
				mContext.startActivity(intent);

			}
		});
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
		return mDatas.size()+1;
	}


	class InvitationCategoryItemViewHolder extends RecyclerView.ViewHolder
	{
		@Bind(R.id.ivAuthorAvatar)
		MultipleShapeImg mIvAuthorAvatar;
		@Bind(R.id.tvUsername)
		TextView mTvUsername;
		@Bind(R.id.tvFlag)
		TextView mTvDistance;
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
		@Bind(R.id.ivLocate)
		MaterialRippleLayout mIvLocate;

		public InvitationCategoryHeaderViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	class LocationListener implements BDLocationListener
	{
		private TextView mTv;

		public LocationListener(InvitationCategoryHeaderViewHolder holder)
		{
			mTv = holder.mTvAddress;
		}

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location.getLocType() == BDLocation.TypeGpsLocation || location
					.getLocType() == BDLocation.TypeNetWorkLocation || location
					.getLocType() == BDLocation.TypeOffLineLocation)
			{
				mTv.setText(location.getAddrStr());
				//将经纬度保存到服务器
				EventBus.getDefault().post(new EventLocate(location.getLatitude(),
						location.getLongitude()));
			} else
			{
				Toast.makeText(mContext, "定位失败，检查网络是否通畅", Toast.LENGTH_SHORT).show();
			}
			LocationUtils.stopClient();
		}
	}
}
