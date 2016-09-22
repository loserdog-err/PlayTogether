package com.chenantao.playtogether.chat.mvc.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.gallery.MyGalleryAdapter;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Chenantao_gg on 2016/1/23.
 */
public class SelectPicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	public static final int TYPE_ITEM_PIC = 0;
	public static final int TYPE_ITEM_FOOTER = 1;
	private Context mContext;
	private List<String> mDatas;//datas不包含footer，所以要记得做相应的加减
	private int mCount = 0;//有包含footer的数量

	public SelectPicAdapter(Context context, List<String> pathDatas)
	{
		mContext = context;
		mDatas = pathDatas;
		mCount = mDatas.size() + 1;
	}

	private boolean isFooter(int position)
	{
		return position == mCount - 1;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (viewType == TYPE_ITEM_FOOTER)
		{
			return new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout
							.item_select_pic_footer, parent, false));
		} else
		{
			return new SelectPicViewHolder(LayoutInflater.from(mContext).inflate(R.layout
											.item_select_pic, parent, false
			));
		}
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
	{
		AutoUtils.autoSize(holder.itemView);
		if (isFooter(position))
		{
			return;
		}
		final String path = mDatas.get(position);
		SelectPicViewHolder selectViewHolder = (SelectPicViewHolder) holder;
		ImageView ivPic = selectViewHolder.ivPic;
		int width = ivPic.getLayoutParams().width;
		int height = ivPic.getLayoutParams().height;
		Uri uri = Uri.parse("file:///" + path);
		Picasso.with(mContext)
						.load(uri)
						.placeholder(R.mipmap.pictures_no)
						.resize(width, height)
						.into(ivPic);
		ImageView ivDel = selectViewHolder.ivDel;
		//防止重复点击
		RxView.clicks(ivDel)
						.throttleFirst(500, TimeUnit.SECONDS)
						.subscribe(new Action1<Void>()
						{
							@Override
							public void call(Void aVoid)
							{
								notifyItemRemoved(holder.getAdapterPosition());
								MyGalleryAdapter.mSelectedImgs.remove(path);
								mCount = mCount - 1;
							}
						});
	}


	@Override
	public int getItemCount()
	{
		return mCount;
	}

	@Override
	public int getItemViewType(int position)
	{
		return position == mCount - 1 ? TYPE_ITEM_FOOTER : TYPE_ITEM_PIC;
	}

	class FooterViewHolder extends RecyclerView.ViewHolder
	{

		public FooterViewHolder(View itemView)
		{
			super(itemView);

		}
	}

	class SelectPicViewHolder extends RecyclerView.ViewHolder
	{
		@Bind(R.id.ivPic)
		public ImageView ivPic;
		@Bind(R.id.ivDel)
		public ImageView ivDel;

		public SelectPicViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
