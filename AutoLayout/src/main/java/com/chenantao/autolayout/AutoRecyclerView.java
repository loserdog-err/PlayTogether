package com.chenantao.autolayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chenantao.autolayout.utils.AutoLayoutHelper;

/**
 * Created by Chenantao_gg on 2016/1/20.
 */
public class AutoRecyclerView extends RecyclerView
{
	private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);
	public AutoRecyclerView(Context context)
	{
		super(context);
	}

	public AutoRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AutoRecyclerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if (!isInEditMode())
			mHelper.adjustChildren();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public static class LayoutParams extends RecyclerView.LayoutParams
			implements AutoLayoutHelper.AutoLayoutParams
	{
		private AutoLayoutInfo mAutoLayoutInfo;

		public LayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);
			mAutoLayoutInfo = AutoLayoutHelper.getAutoLayoutInfo(c, attrs);
		}


		@Override
		public AutoLayoutInfo getAutoLayoutInfo()
		{
			return mAutoLayoutInfo;
		}


	}
}
