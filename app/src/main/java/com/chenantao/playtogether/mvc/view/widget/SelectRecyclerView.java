package com.chenantao.playtogether.mvc.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.chenantao.playtogether.R;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
public class SelectRecyclerView extends RecyclerView
{
	public static final int DISPLAY_ITEM_COUNT = 5;//只能同时展示5条数据，注意，只能展示奇数条数据

	public static final int OFFSET_ITEM = 2;//由于业务需求，位置需要与原位置偏差

	private double mMaxTextSize = 30, mMinTextSize = 10;//sp，textview字体大小最大以及最小限制

	private double mMaxTextAlpha = 1, mMinTextAlpha = 0.2;//textview透明度最大以及最小限制

	private Paint mSelectedBorderPaint;//绘制选择框的paint

	private SnappingLinearLayoutManager mLayoutManager;//自定义的布局管理器，可以缓慢滑动到指定位置

	private double mTextSizeScale, mTextAlphaScale;//字体大小以及透明度的梯度值

	public SelectRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//将dp转换为px
//		mMaxTextSize = (int) DimensionUtils.sp2px(mMaxTextSize, getResources().getDisplayMetrics
//				());
//		mMinTextSize = (int) DimensionUtils.sp2px(mMinTextSize, getResources().getDisplayMetrics
//				());
		mSelectedBorderPaint = new Paint();
		mSelectedBorderPaint.setColor(getResources().getColor(R.color.primary_color));
		mSelectedBorderPaint.setAntiAlias(true);
		mSelectedBorderPaint.setStyle(Paint.Style.STROKE);
		mSelectedBorderPaint.setStrokeWidth(3);
	}


	@Override
	protected void onMeasure(int widthSpec, int heightSpec)
	{
		super.onMeasure(widthSpec, heightSpec);
		int childCount = getChildCount();
		int itemHeight = getMeasuredHeight() / DISPLAY_ITEM_COUNT;
		for (int i = 0; i < childCount; i++)
		{
			View child = getChildAt(i);
			child.getLayoutParams().height = itemHeight;
		}
	}

	@Override
	public void setLayoutManager(LayoutManager layout)
	{
		super.setLayoutManager(layout);
		mLayoutManager = (SnappingLinearLayoutManager) layout;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (changed)
		{
			int height = getMeasuredHeight();
			//距离中点一半高度的时候textSize为最小
			mTextSizeScale = (mMaxTextSize - mMinTextSize) / (height / 2);
			//距离中点一半高度的时候透明度为最小
			mTextAlphaScale = (mMaxTextAlpha - mMinTextAlpha) / (height / 2);
		}
		if (mLayoutManager != null)
		{
			setItemStyle();
		}
	}

	/**
	 * 在滑动停止的时候设置选中的条目
	 *
	 * @param state
	 */
	@Override
	public void onScrollStateChanged(int state)
	{
		super.onScrollStateChanged(state);
		if (state == SCROLL_STATE_IDLE)
		{
			scrollToSelectedView();
		}
	}

	/**
	 * 滑动到目前选择框选中的item
	 */
	private void scrollToSelectedView()
	{
		//取得中点对应的item，类似于listview的pointToPosition
		View selectView = findChildViewUnder(getMeasuredWidth() / 2, getMeasuredHeight
				() / 2);
		int pos = getChildAdapterPosition(selectView);
		mLayoutManager.smoothScrollToPosition(this, null, pos - ((DISPLAY_ITEM_COUNT - 1) / 2));
	}


	/**
	 * 画出选择框
	 *
	 * @param c
	 */
	@Override
	public void onDraw(Canvas c)
	{
		super.onDraw(c);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int itemHeight = height / DISPLAY_ITEM_COUNT;
//		Logger.e("height:" + height+",itemheight:"+itemHeight);
		int t = (height - itemHeight) / 2;
		Rect rect = new Rect(0, t, width, t + itemHeight);
		c.drawRect(rect, mSelectedBorderPaint);
	}

	/**
	 * 设置可视recyclerView可视item的属性(字体大小、透明度)
	 */
	public void setItemStyle()
	{
		int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();
		int lastVisiblePos = mLayoutManager.findLastVisibleItemPosition();
		int itemHeight = getMeasuredHeight() / DISPLAY_ITEM_COUNT;
		for (int i = firstVisiblePos; i <= lastVisiblePos; i++)
		{
			View view = mLayoutManager.findViewByPosition(i);
			int x = (int) view.getX();
			int y = (int) view.getY();
			//itemCenter:item的中点，相对于parent的y距离
			int itemCenter = y + itemHeight / 2;
			if (itemCenter < 0) itemCenter = 0;
			//distanceToCenter：item的中点距离parent的y距离
			int distanceToCenter = Math.abs(getMeasuredHeight() / 2 - itemCenter);
//			Logger.e("toCenter:" + distanceToCenter + ",testSize:" + (float) (mMaxTextSize -
//					mTextSizeScale * distanceToCenter));
			TextView textview = (TextView) view.findViewById(R.id.tv);
			//距离中点最近的时候透明度最大，字体也最大
			//设置textview字体大小随着距离中点距离的改变而改变
			textview.setTextSize((float) (mMaxTextSize - mTextSizeScale * distanceToCenter));
			//设置textview透明度随着距离中点距离的改变而改变
			textview.setAlpha((float) (mMaxTextAlpha - mTextAlphaScale * distanceToCenter));
		}
	}

	@Override
	public void onScrolled(int dx, int dy)
	{
		super.onScrolled(dx, dy);
		if (mLayoutManager != null)
		{
			setItemStyle();
		}
	}

	public String getSelectItemText()
	{
		View selectView = findChildViewUnder(getMeasuredWidth() / 2, getMeasuredHeight
				() / 2);
		TextView tv = (TextView) selectView.findViewById(R.id.tv);
		return tv.getText().toString();
	}

}
