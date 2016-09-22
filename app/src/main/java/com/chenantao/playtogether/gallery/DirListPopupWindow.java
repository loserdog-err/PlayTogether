package com.chenantao.playtogether.gallery;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chenantao.playtogether.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chenantao on 2015/12/11.
 */
public class DirListPopupWindow extends PopupWindow
{
	private Context mContext;
	private int mWidth;
	private int mHeight;
	//内容区域
	private View mContentView;
	private ListView mLvDir;
	private List<FolderBean> mDatas;
	private PopupWindowAdapter mAdapter;

	private OnFolderSelectedListener mListener;


	public DirListPopupWindow(Context context, List<FolderBean> datas)
	{
		mContext = context;
		mDatas = datas;
		mContentView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_dir_list, null);
		calcWidthAndHeight();
		setContentView(mContentView);
		setWidth(mWidth);
		setHeight(mHeight);
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		initView();
		initEvent();
	}

	private void initView()
	{
		mLvDir = (ListView) mContentView.findViewById(R.id.lvDir);
		mLvDir.setAdapter(mAdapter = new PopupWindowAdapter(mContext, mDatas));

	}


	private void initEvent()
	{
		setTouchInterceptor(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					dismiss();
					return true;
				}
				return false;
			}
		});
		mLvDir.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (mListener != null)
				{
					FolderBean folderBean = mDatas.get(position);
					mListener.onSelected(folderBean);
				}
			}
		});
	}

	private void calcWidthAndHeight()
	{
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context
				.WINDOW_SERVICE);
		Display defaultDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		defaultDisplay.getMetrics(metrics);
		//将popup的高度设置为屏幕的7分之一
		mWidth = metrics.widthPixels;
		mHeight = (int) (metrics.heightPixels * 0.7);
	}

	//回调接口，用于切换文件夹的时候回调activity的方法
	interface OnFolderSelectedListener
	{
		void onSelected(FolderBean folderBean);
	}

	public void setOnFolderSelectedListener(OnFolderSelectedListener listener)
	{
		mListener = listener;
	}
}

class PopupWindowAdapter extends ArrayAdapter<FolderBean>
{
	private Context mContext;

	public PopupWindowAdapter(Context context, List<FolderBean> datas)
	{
		super(context, 0, datas);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_popupwindow_dir_list,
					null);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ivDirImg);
			viewHolder.tvDirName = (TextView) convertView.findViewById(R.id.tvDirName);
			viewHolder.tvImgCount = (TextView) convertView.findViewById(R.id.tvImgCount);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//重置状态
		viewHolder.imageView.setImageResource(R.mipmap.pictures_no);
		viewHolder.tvImgCount.setText("");
		viewHolder.tvDirName.setText("");
		//设置属性
		FolderBean folderBean = getItem(position);
		Uri uri = Uri.parse("file:///" + folderBean.getFirstImage());
		Picasso.with(mContext)
				.load(uri)
				.resize(100,100)
				.placeholder(R.mipmap.pictures_no)
				.into(viewHolder.imageView);
		viewHolder.tvDirName.setText(folderBean.getName());
		viewHolder.tvImgCount.setText(folderBean.getImageCount() + "张");
		return convertView;
	}

	class ViewHolder
	{
		ImageView imageView;
		TextView tvDirName;
		TextView tvImgCount;
	}

}

