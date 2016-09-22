package com.chenantao.playtogether.gallery;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.chenantao.autolayout.utils.AutoUtils;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.utils.ScreenUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/22.
 */
public class MyGalleryAdapter extends RecyclerView.Adapter<MyGalleryAdapter.MyViewHolder>
{
	public static List<String> mSelectedImgs = new ArrayList<>();
	public static int mLimitCount = 0;
	private Context mContext;
	private List<String> mDatas;
	private String mParentDirPath;

	public MyGalleryAdapter(Context context, List<String> datas, String dir)
	{
		this.mContext = context;
		this.mDatas = datas;
		mParentDirPath = dir;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_gallery,
				parent,
				false);
//		view.setTag("auto");
		return new MyViewHolder(view);
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position)
	{
		AutoUtils.autoSize(holder.itemView);
		final String path = mParentDirPath + "/" + mDatas.get(position);
		int width = ScreenUtils.getScreenWidth(mContext) / MyGalleryActivity.SPAN_COUNT;
		int height = calcHeight(path, width);
		Uri uri = Uri.parse("file:///" + path);
		//先更新一下状态，防止recyclerview复用导致的错乱
		setState(holder, path);
		Picasso.with(mContext)
				.load(uri)
				.placeholder(R.mipmap.pictures_no)
				.resize(width, height)
				.into(holder.imageView);
		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mSelectedImgs.contains(path))//如果当前已选中，则取消选中
				{
					resetImgState(holder);
					mSelectedImgs.remove(path);
				} else
				{
					if (mSelectedImgs.size() >= mLimitCount)
					{
						//如果已超过了选中的数量，直接返回
						Toast.makeText(mContext, "你选择太多了少侠", Toast.LENGTH_SHORT).show();
						return;
					}
					addSelectedState(holder);
					mSelectedImgs.add(path);
				}
			}
		});
	}

	public int calcHeight(String path, int ivWidth)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int imgWidth = options.outWidth;
		int imgHeight = options.outHeight;
		int desireHeight = ivWidth * imgHeight / imgWidth;
//		Log.e("cat", "view width:" + ivWidth + ",image height:" + imgHeight + ",image width:" +
//				imgWidth);
		options.inJustDecodeBounds = false;
//		Log.e("cat", "desire height:" + desireHeight);
		return desireHeight > 640 ? 640 : desireHeight;

	}

	private void resetImgState(MyViewHolder viewHolder)
	{
		viewHolder.imageView.setColorFilter(null);
		viewHolder.imageButton.setBackgroundResource(R.mipmap.picture_unselected);
	}

	private void addSelectedState(MyViewHolder viewHolder)
	{
		viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
		viewHolder.imageButton.setBackgroundResource(R.mipmap.pictures_selected);
	}

	public void setState(MyViewHolder holder, String path)
	{
		if (mSelectedImgs.contains(path))
		{
			addSelectedState(holder);
		} else
		{
			resetImgState(holder);
		}
	}


	class MyViewHolder extends RecyclerView.ViewHolder
	{
		public ImageView imageView;
		public ImageButton imageButton;

		public MyViewHolder(View itemView)
		{
			super(itemView);
			imageView = (ImageView) itemView.findViewById(R.id.iv);
			imageButton = (ImageButton) itemView.findViewById(R.id.ibSelect);
		}
	}


}
