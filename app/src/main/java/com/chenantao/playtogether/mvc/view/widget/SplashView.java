package com.chenantao.playtogether.mvc.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.chenantao.playtogether.R;

/**
 * Created by chenantao on 2015/8/1.
 */
public class SplashView extends View {
    //小圆半径
    private int mCircleRadius;
    //旋转半径
    private float mRotateRadius;
    //小圆的颜色数组
    private int[] mCircleColor;
    //view的中心点
    private float mCenterX;
    private float mCenterY;
    //小圆旋转了多少角度
    private float mCircleRotation;
    //对角线的一半
    private int mHalfDiagonal;
    //空心圆的半径
    private float mHollowCircleRadius;
    //画笔的一半
    Paint mCirclePaint;
    //背景画笔
    Paint mBgPaint;

    AnimationState state;
    private ValueAnimator loadAnimator;

    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化变量，以及动画
     */
    private void init() {
        mCircleRadius = 20;
        mRotateRadius = 80;
        mCircleColor = getResources().getIntArray(R.array.splash_circle_color);
//		Log.e("TAG", "size:" + mCircleColor.length);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setColor(Color.WHITE);
    }

    /**
     * 加载结束，结束第一个动画，开启第二个动画
     */
    public void loadFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                state = new ContractionAnimation();
                invalidate();
            }
        }, 1000);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
        mHalfDiagonal = (int) Math.sqrt(w * w / 4 + h * h / 4);
//		Log.e("TAG", "mHalfDiagonal:" + mHalfDiagonal + "");
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == null) {
            state = new RotationAnimation(canvas);
        }
        state.draw(canvas);
    }

    /**
     * 小圆的旋转动画
     */
    public class RotationAnimation extends AnimationState {

        /**
         * 开启动画，每隔一定时间为小圆的旋转角度赋值
         */
        public RotationAnimation(Canvas canvas) {
            loadAnimator = ValueAnimator.ofFloat(0, (float) (2 * Math.PI));
            loadAnimator.setDuration(750);
            loadAnimator.setInterpolator(new LinearInterpolator());
            loadAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCircleRotation = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            loadAnimator.setRepeatCount(ValueAnimator.INFINITE);
            loadAnimator.start();
        }

        /**
         * 根据旋转角度以及旋转半径,计算出小圆的x,y坐标
         * 旋转角度angle等于小圆的初始角度加上旋转角度
         * 1：根据余弦定理，x坐标等于 旋转半径*cos(angle)+view的centerX；
         * 2：根据正弦定理，y坐标等于 旋转半径*sin(angle)+view的centerY；
         */
        @Override
        public void draw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            drawCircle(canvas);
        }
    }

    /**
     * 收缩动画
     */
    public class ContractionAnimation extends AnimationState {
        ValueAnimator animator;

        public ContractionAnimation() {
            animator = ValueAnimator.ofFloat(mRotateRadius, 0);
            animator.setInterpolator(new AccelerateInterpolator(5f));
            animator.setDuration(700);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRotateRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    state = new ExpandAnimation();
                    invalidate();
                    super.onAnimationEnd(animation);
                }
            });
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            drawCircle(canvas);
        }
    }

    /**
     * 扩散动画（绘制空心圆）
     */
    public class ExpandAnimation extends AnimationState {
        private ValueAnimator animator;

        public ExpandAnimation() {
            animator = ValueAnimator.ofFloat(0, mHalfDiagonal);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHollowCircleRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            //画笔的宽度
            float stokeWidth = mHalfDiagonal - mHollowCircleRadius;
            mBgPaint.setStrokeWidth(stokeWidth);
            //圆的半径
            float radius = mHollowCircleRadius + stokeWidth / 2;
            if (mHollowCircleRadius >= mHalfDiagonal) {
                if (loadAnimator != null && loadAnimator.isStarted()) {
                    loadAnimator.end();
                }
            }
            canvas.drawCircle(mCenterX, mCenterY, radius, mBgPaint);
        }
    }

    public void drawCircle(Canvas canvas) {
        //初始角度
        float originalAngle = (float) (2 * Math.PI / mCircleColor.length);
        for (int i = 0; i < mCircleColor.length; i++) {
            float angle = originalAngle * i + mCircleRotation;
            float circleX = (float) (mRotateRadius * Math.cos(angle) + mCenterX);
            float circleY = (float) (mRotateRadius * Math.sin(angle) + mCenterY);
            mCirclePaint.setColor(mCircleColor[i]);
            canvas.drawCircle(circleX, circleY, mCircleRadius, mCirclePaint);
        }
    }

    public abstract class AnimationState {
        public abstract void draw(Canvas canvas);
    }
}
