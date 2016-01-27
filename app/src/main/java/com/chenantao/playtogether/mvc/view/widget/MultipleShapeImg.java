package com.chenantao.playtogether.mvc.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.utils.BitmapUtils;

/**
 * Created by chenantao on 2015/10/15.
 * 多种形状的图片(目前包含圆形、圆角矩形、三角形、模糊矩形）
 */
public class MultipleShapeImg extends ImageView
{

	public static final int TYPE_ROUND = 0;

	public static final int TYPE_FILLET = 1;

	public static final int TYPE_TRIANGLE = 2;

	public static final int TYPE_ARC_RECTANGLE = 3;//下边为贝塞尔曲线的矩形

	private int width;


	private int type;

	private int borderRadius;//圆角矩形圆角半径

	private int radius;

	private Matrix matrix;

	private BitmapShader bitmapShader;

	private Paint paint;

	private Bitmap src;

	private Drawable mDrawable;

	public MultipleShapeImg(Context context)
	{
		this(context, null);
	}

	public MultipleShapeImg(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public MultipleShapeImg(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MultipleShapeImg);
		borderRadius = t.getDimensionPixelSize(R.styleable.MultipleShapeImg_borderRadius, (int)
				TypedValue
						.applyDimension
								(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics
										()));
		type = t.getInt(R.styleable.MultipleShapeImg_type, 0);
		t.recycle();
		paint = new Paint();
		paint.setAntiAlias(true);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		Log.e("cat", "measure width:" + width);
		width = Math.min(getMeasuredWidth(), getMeasuredHeight());
		if (type == TYPE_ROUND)
		{
			radius = width / 2;
			setMeasuredDimension(width, width);
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		setupShader();
		if (type == TYPE_ROUND)
		{
			canvas.drawCircle(radius, radius, radius, paint);
		} else if (type == TYPE_FILLET)
		{
			RectF rectF = new RectF(0, 0, getWidth(), getHeight());
			canvas.drawRoundRect(rectF, borderRadius, borderRadius, paint);
		} else if (type == TYPE_TRIANGLE)
		{
			Path path = new Path();
			path.moveTo(src.getWidth() / 2, 0);
			path.lineTo(src.getWidth(), src.getHeight());
			path.lineTo(0, src.getHeight());
			path.close();
			canvas.drawPath(path, paint);
		} else if (type == TYPE_ARC_RECTANGLE)
		{
			Path path = new Path();
			//贝塞尔曲线的操作点x y
			int deviation = 50;//圆弧突起的高度
			int controlY = getMeasuredHeight() + deviation;
			int controlX = getMeasuredWidth() / 2;
			path.moveTo(0, 0);
			path.lineTo(getMeasuredWidth(), 0);
			path.lineTo(getMeasuredWidth(), getMeasuredHeight() - deviation);
			path.quadTo(controlX, controlY, 0, getMeasuredHeight() - deviation);
			path.close();
			canvas.drawPath(path, paint);
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
		mDrawable = drawable;
	}

	@Override
	public void setImageBitmap(Bitmap bm)
	{
		super.setImageBitmap(bm);
	}

	public void setupShader()
	{
		float scale = 1.0f;
		mDrawable = getDrawable();
		src = drawableToBitmap(mDrawable);
		if (type == TYPE_ROUND)
		{
			int minSize = Math.min(src.getWidth(), src.getHeight());
			scale = width * 1.0f / minSize;
		} else //弧线矩形、三角形、圆角矩形
		{
			scale = Math.max(getWidth() * 1.0f / src.getWidth(), getHeight() * 1.0f / src
					.getHeight());
		}
		matrix = new Matrix();
		bitmapShader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		matrix.setScale(scale, scale);
		bitmapShader.setLocalMatrix(matrix);
		paint.setShader(bitmapShader);
	}

	public Bitmap drawableToBitmap(Drawable drawable)
	{
		Bitmap result = null;
		if (drawable instanceof BitmapDrawable)
		{
			result = ((BitmapDrawable) drawable).getBitmap();
		} else
		{
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(result);
			drawable.setBounds(0, 0, width, height);
			drawable.draw(canvas);
		}
		if (type == TYPE_ARC_RECTANGLE)//如果是弧线矩形，做模糊处理
		{
			result = BitmapUtils.blur(result, this);
		}
		return result;
	}


}
