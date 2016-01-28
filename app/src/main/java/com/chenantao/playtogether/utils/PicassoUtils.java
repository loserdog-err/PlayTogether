package com.chenantao.playtogether.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.chenantao.playtogether.R;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Chenantao_gg on 2016/1/25.
 */
public class PicassoUtils
{

	/**
	 * 展示自适应imageview的图片
	 *
	 * @param context
	 * @param uri
	 * @param imageView
	 * @param callback
	 */
	public static void displayFitImage(final Context context, final Uri uri, final ImageView
			imageView, final Callback callback)
	{
		Picasso.with(context)
				.load(uri)
				.fit()
				.placeholder(R.mipmap.pictures_no)
				.networkPolicy(NetworkPolicy.OFFLINE)
				.into(imageView, new Callback()
				{
					@Override
					public void onSuccess()
					{
					}

					@Override
					public void onError()
					{
//						Logger.e("down load..");
						//Try again online if cache failed
						Picasso.with(context)
								.load(uri)
								.fit()
								.placeholder(R.mipmap.pictures_no)
								.error(R.mipmap.pictures_no)
								.into(imageView, callback == null ? new Callback()
								{
									@Override
									public void onSuccess()
									{
									}

									@Override
									public void onError()
									{
										Logger.e("can not find image");
									}
								} : callback);
					}
				});
	}

	/**
	 * 展示指定大小的图片
	 *
	 * @param context
	 * @param uri
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public static void displaySpecSizeImage(final Context context, final Uri uri, final ImageView
			imageView, final int width, final int height, final
	OnLoadPicListener listener)
	{
		Picasso.with(context)
				.load(uri)
				.centerInside()
				.resize(width, height)
				.placeholder(R.mipmap.pictures_no)
				.networkPolicy(NetworkPolicy.OFFLINE)
				.into(imageView, new Callback()
				{
					@Override
					public void onSuccess()
					{
						if (listener != null)
						{
							listener.onSuccess();
						}

					}

					@Override
					public void onError()
					{
						//Try again online if cache failed
						Picasso.with(context)
								.load(uri)
								.centerInside()
								.resize(width, height)
								.placeholder(R.mipmap.pictures_no)
								.error(R.mipmap.pictures_no)
								.into(imageView, new Callback()
								{
									@Override
									public void onSuccess()
									{
										if (listener != null)
										{
											listener.onSuccess();
										}
									}

									@Override
									public void onError()
									{
										listener.onError();
										Logger.e("can not find image");
									}
								});
					}
				});
	}

	public interface OnLoadPicListener
	{
		void onSuccess();

		void onError();
	}
}
