package com.chenantao.playtogether.mvc.view.common;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.utils.FileUtils;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.chenantao.playtogether.utils.ScreenUtils;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Chenantao_gg on 2016/1/26.
 * 没啥逻辑，就是展示图片而已
 */
public class ShowImageActivity extends BaseActivity
{
	//内容中图片的宽度最大能占屏幕宽度的多少，高同理
	public static final double CONTENT_PIC_WIDTH_MAX_RATIO = 1;
	public static final double CONTENT_PIC_HEIGHT_MAX_RATIO = 0.9;

	//需要接收的两个extra
	public static final String EXTRA_URI = "uri";
	public static final String EXTRA_WIDTH = "width";
	public static final String EXTRA_HEIGHT = "height";
	@Bind(R.id.ivPic)
	ImageView mIvPic;
	@Bind(R.id.progressBar)
	ProgressBar mProgressBar;

	private Uri mUri;
	private int mWidth;
	private int mHeight;

	PhotoViewAttacher mAttacher;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_show_image;
	}

	@Override
	public void injectActivity()
	{
	}

	@Override
	public void afterCreate()
	{
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setTitle("大图");
		}
		mUri = getIntent().getParcelableExtra(EXTRA_URI);
		mWidth = getIntent().getIntExtra(EXTRA_WIDTH, 0);
		mHeight = getIntent().getIntExtra(EXTRA_HEIGHT, 0);
		if (mUri == null || mWidth == 0 || mHeight == 0)
		{
			Toast.makeText(this, "有点问题，试一下其他图片吧", Toast.LENGTH_SHORT).show();
			return;
		}
		//根据图片的大小进行压缩，使其高度宽度都在屏幕内且不失真
		double[] ratio = FileUtils.compressIfMoreThanDesireHeightWidth(mWidth, mHeight,
				CONTENT_PIC_WIDTH_MAX_RATIO, CONTENT_PIC_HEIGHT_MAX_RATIO, this);
		mProgressBar.setVisibility(View.VISIBLE);
		int screenWidth = ScreenUtils.getScreenWidth(this);
		int screenHeight = ScreenUtils.getScreenHeight(this);
		int imageWidth = (int) (screenWidth * ratio[0]);
		int imageHeight = (int) (screenHeight * ratio[1]);
		PicassoUtils.displaySpecSizeImage(this, mUri, mIvPic, imageWidth, imageHeight, new
				PicassoUtils.OnLoadPicListener()
				{
					@Override
					public void onSuccess()
					{
						mProgressBar.setVisibility(View.GONE);
						mAttacher = new PhotoViewAttacher(mIvPic);
						mAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					}

					@Override
					public void onError()
					{
						Toast.makeText(ShowImageActivity.this, "啊哦，没加载到，囧，换个图片试试吧", Toast
								.LENGTH_SHORT).show();
					}
				});
	}

}

