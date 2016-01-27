package com.chenantao.autolayout;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import com.chenantao.autolayout.utils.AutoLayoutHelper;

/**
 * Created by Chenantao_gg on 2016/1/20.
 */
public class AutoCardView extends CardView
{
	private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

	public AutoCardView(Context context)
	{
		super(context);
	}

	public AutoCardView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AutoCardView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if (!isInEditMode())
		{
			mHelper.adjustChildren();
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
	}

	public static class LayoutParams extends CardView.LayoutParams
			implements AutoLayoutHelper.AutoLayoutParams
	{
		private AutoLayoutInfo mAutoLayoutInfo;

		public LayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);
			mAutoLayoutInfo = AutoLayoutHelper.getAutoLayoutInfo(c, attrs);
		}

//		public LayoutParams(int width, int height)
//		{
//			super(width, height);
//		}
//
//		public LayoutParams(int width, int height, int gravity)
//		{
//			super(width, height, gravity);
//		}
//
//		public LayoutParams(ViewGroup.LayoutParams source)
//		{
//			super(source);
//		}
//
//		public LayoutParams(MarginLayoutParams source)
//		{
//			super(source);
//		}
//
//		public LayoutParams(FrameLayout.LayoutParams source)
//		{
//			super((MarginLayoutParams) source);
//			gravity = source.gravity;
//		}
//
//		public LayoutParams(LayoutParams source)
//		{
//			this((FrameLayout.LayoutParams) source);
//			mAutoLayoutInfo = source.mAutoLayoutInfo;
//		}

		@Override
		public AutoLayoutInfo getAutoLayoutInfo()
		{
			return mAutoLayoutInfo;
		}
	}
}
