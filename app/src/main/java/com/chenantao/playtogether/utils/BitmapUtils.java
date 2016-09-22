package com.chenantao.playtogether.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by chenantao on 2015/12/26.
 */
public class BitmapUtils
{
	/**
	 * 高斯模糊bitmap
	 *
	 * @param src
	 * @return
	 */
	public static Bitmap blur(Bitmap src, View view)
	{
		float scaleFactor = 8;
		float radius = 5;
		Bitmap overlay = Bitmap.createBitmap(
				(int) (view.getMeasuredWidth() / scaleFactor),
				(int) (view.getMeasuredHeight() / scaleFactor),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-view.getLeft() / scaleFactor, view.getTop()
				/ scaleFactor);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(src, 0, 0, paint);
		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		return overlay;
	}
}
