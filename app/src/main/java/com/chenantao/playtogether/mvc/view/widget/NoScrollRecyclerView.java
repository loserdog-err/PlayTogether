package com.chenantao.playtogether.mvc.view.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class NoScrollRecyclerView extends RecyclerView
{
	public NoScrollRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		switch (action)
		{
			case MotionEvent.ACTION_MOVE:
				return false;
		}
		return super.dispatchTouchEvent(ev);
	}
}
