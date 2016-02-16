package com.chenantao.playtogether.utils;

import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.chenantao.playtogether.R;

/**
 * Created by Chenantao_gg on 2016/1/22.
 */
public class PopupWindowManager
{

	public static PopupWindow getDefaultPopupWindow(View contentView, final View dimView)
	{
		return getDefaultPopupWindow(contentView, dimView, null);
	}

	public static PopupWindow getPopupWindow(View contentView, int width, int height, final View
					dimView)
	{
		return getPopupWindow(contentView, width, height, dimView, null);
	}

	public static PopupWindow getPopupWindow(View contentView, int width, int height, final View
					dimView, final onCloseListener listener)
	{
		final PopupWindow mPopupWindow = new PopupWindow(contentView, width, height, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.selectPopupWindowAnim);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setTouchInterceptor(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					mPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				if (dimView != null)
					toggleLight(false, dimView);
				if (listener != null)
					listener.onClose();
			}
		});
		return mPopupWindow;
	}

	public static PopupWindow getDefaultPopupWindow(View contentView, final View dimView,
																									final onCloseListener listener)
	{
		return getPopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
						.WRAP_CONTENT, dimView, listener);
	}

	/**
	 * popupwindow 默认关闭的时候是亮屏效果，这里添加一个回调，供开发者设置
	 */
	public interface onCloseListener
	{
		void onClose();
	}

	/**
	 * popupWindow弹出或隐藏时
	 * 使内容区域变亮或变暗
	 */
	public static void toggleLight(boolean isOpen, View dimView)
	{
		if (isOpen)
		{
			dimView.setVisibility(View.VISIBLE);
		} else
		{
			dimView.setVisibility(View.GONE);
		}
	}


}
