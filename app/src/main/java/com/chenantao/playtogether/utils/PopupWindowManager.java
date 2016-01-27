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

	public static PopupWindow getDefaultPopupWindow(View contentView)
	{
		final PopupWindow mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams
				.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
		return mPopupWindow;
	}

}
