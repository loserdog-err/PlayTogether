package com.chenantao.playtogether.mvc.view.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chenantao.playtogether.R;

/**
 * Created by Chenantao_gg on 2016/2/13.
 */
public class SelectListPopupWindow extends PopupWindow
{
	private Context mContext;
	private View mContentView;
	private OnItemClickListener mListener;
	private View mViewDim;//用于将屏幕变暗的遮罩层

	public SelectListPopupWindow(Context context, String title, final String[] items, View viewDim)
	{
		super(context);
		mContext = context;
		mViewDim = viewDim;
		mContentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_select_list,
						null);
		TextView tvHead = (TextView) mContentView.findViewById(R.id.tvHeader);
		tvHead.setText(title);
		setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.select_avatar_popupwindow_anim);
		//初始化列表项
		ViewGroup container = (ViewGroup) mContentView.findViewById(R.id.llContainer);
		int count = items.length;
		for (int i = 0; i < count; i++)
		{
			final String item = items[i];
			getItemView(context, item, container);
		}
		setContentView(mContentView);
		initEvent();
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
		setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				mViewDim.setVisibility(View.GONE);
			}
		});
	}

	public View getItemView(Context context, final String item, ViewGroup parent)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.popupwindow_select_list_item,
						parent, false);
		TextView tvItem = (TextView) view.findViewById(R.id.tvItem);
		tvItem.setText(item);
		parent.addView(view);
		view.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mListener != null)
				{
					mListener.onClick(item);
					dismiss();
					mViewDim.setVisibility(View.GONE);
				}
			}
		});
		return view;
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mListener = listener;
	}

	public interface OnItemClickListener
	{
		void onClick(String item);
	}

}
