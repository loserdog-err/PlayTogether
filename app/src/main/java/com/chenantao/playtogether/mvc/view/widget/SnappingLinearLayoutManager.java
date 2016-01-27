package com.chenantao.playtogether.mvc.view.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * 扩展linearlayout，提供缓慢滑动的功能
 */
public class SnappingLinearLayoutManager extends LinearLayoutManager
{
	private static final float MILLISECONDS_PER_INCH = 500f;
	private Context mContext;

	public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
	{
		super(context, orientation, reverseLayout);
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
	                                   int position)
	{
		RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView
				.getContext())
		{
			//This controls the direction in which smoothScroll looks for your view
			@Override
			public PointF computeScrollVectorForPosition(int targetPosition)
			{
				return new PointF(0, 1);
			}

			//This returns the milliseconds it takes to scroll one pixel.
			@Override
			protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
			{
				return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
			}
		};
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}


	private class TopSnappedSmoothScroller extends LinearSmoothScroller
	{
		public TopSnappedSmoothScroller(Context context)
		{
			super(context);
		}

		@Override
		public PointF computeScrollVectorForPosition(int targetPosition)
		{
			return SnappingLinearLayoutManager.this
					.computeScrollVectorForPosition(targetPosition);
		}

		@Override
		protected int getVerticalSnapPreference()
		{
			return SNAP_TO_START;
		}
	}
}  