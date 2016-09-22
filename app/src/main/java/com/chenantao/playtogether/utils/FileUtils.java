package com.chenantao.playtogether.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Created by chenantao on 2015/6/29.
 */
public class FileUtils
{
	private static File imageCacheDir;//图片缓存的目录
	private static File voiceCacheDir;//音频缓存的目录
	public static final String VOICE_PATH = Environment.getExternalStorageDirectory()
					.getPath() + "/cat_voice";//音频文件存储路径
	public static final String IMAGE_PATH = Environment.getExternalStorageDirectory()
					.getPath() + "/cat_image"; //图片地址缓存路径

	static
	{
		imageCacheDir = new File(IMAGE_PATH);
		if (!imageCacheDir.exists())
		{
			imageCacheDir.mkdirs();
		}
		voiceCacheDir = new File(VOICE_PATH);
		if (!voiceCacheDir.exists())
		{
			voiceCacheDir.mkdirs();
		}

	}

	/**
	 * 将bitmap文件保存到sd卡
	 */
	public static String saveBitmapInSdCard(Bitmap bitmap, String fileName)
	{
		FileOutputStream fos = null;
		File file = null;
		String path;
		try
		{
			file = new File(createFile(IMAGE_PATH, fileName));
			fos = new FileOutputStream(file);
			if (fos != null)
			{
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return file.getAbsolutePath();
	}

	public static String createFile(String parent, String fileName)
	{
		File dir = new File(parent);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		File file = new File(dir, fileName);
		return file.getAbsolutePath();
	}

	/**
	 * 在默认的语音路径下创建一个语音文件
	 *
	 * @return
	 */
	public static String createVoiceFile()
	{
		return createFile(VOICE_PATH, UUID.randomUUID() + ".amr");
	}

	/**
	 * 在默认的图片目录下创建一个图片文件
	 */
	public static String createImageFile()
	{
		return createFile(IMAGE_PATH, System.currentTimeMillis() + ".jpg");
	}


	/**
	 * 得到图片缓存目录
	 *
	 * @return
	 */
	public static File getImageCacheDir()
	{
		if (imageCacheDir != null && imageCacheDir.exists())
		{
			return imageCacheDir;
		} else
		{
			imageCacheDir = new File(IMAGE_PATH);
			if (!imageCacheDir.exists())
			{
				imageCacheDir.mkdirs();
			}
			return imageCacheDir;
		}
	}

	/**
	 * 得到音频缓存目录
	 *
	 * @return
	 */
	public static File getVoiceCacheDir()
	{
		if (voiceCacheDir != null && voiceCacheDir.exists())
		{
			return voiceCacheDir;
		} else
		{
			voiceCacheDir = new File(VOICE_PATH);
			if (!voiceCacheDir.exists())
			{
				voiceCacheDir.mkdirs();
			}
			return voiceCacheDir;
		}
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 *
	 * @param dir 将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful.
	 * If a deletion fails, the method stops attempting to
	 * delete and returns "false".
	 */
	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			//递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 得到文件的大小
	 *
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f)
	{
		long size = 0;
		File files[] = f.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isDirectory())
			{
				size = size + getFileSize(files[i]);
			} else
			{
				size = size + files[i].length();
			}
		}
		return size;
	}


	/**
	 * 当图片超过给定的宽度占屏比和高度占屏比时，返回压缩后的占屏比
	 * 返回的数组下标为0为宽度，下边1为高度.
	 * 注意，这个方法并不对图片进行压缩，只是返回大小。
	 * 一般是用于上传图片或者下载需要指定大小的图片时用到
	 *
	 * @param maxWidthRatio  最大的占宽比
	 * @param maxHeightRatio 最大的占高比
	 * @param imageWidth     真实图片的宽度
	 * @param imageHeight    真实图片的高度
	 * @return 为了适配不同分辨率，返回的是屏占比，下标0为占屏幕宽度百分比，下标1为占屏幕高度百分比
	 */
	public static double[] compressIfMoreThanDesireHeightWidth(
					int imageWidth, int imageHeight, double maxWidthRatio,
					double maxHeightRatio, Context context)
	{
		int screenWidth = ScreenUtils.getScreenWidth(context);
		int screenHeight = ScreenUtils.getScreenHeight(context);
		int desireWidth = (int) (screenWidth * maxWidthRatio);
		int desireHeight = (int) (screenHeight * maxHeightRatio);
		float resultWidthRatio, resultHeightRatio;
		//如果宽或者高超过了限制,则对图片进行压缩，以适应最大的宽高比
		if (imageWidth > desireWidth || imageHeight > desireHeight)
		{
			float widthScale = imageWidth * 1.0f / desireWidth;
			float heightScale = imageHeight * 1.0f / desireHeight;
			float scale = Math.max(widthScale, heightScale);
			int resultWidth = (int) (imageWidth / scale);
			int resultHeight = (int) (imageHeight / scale);
			resultWidthRatio = resultWidth * 1.0f / screenWidth;
			resultHeightRatio = resultHeight * 1.0f / screenHeight;
			return new double[]{resultWidthRatio, resultHeightRatio};
		} else//否则，根据图片的实际尺寸返回占屏比
		{
			resultWidthRatio = imageWidth * 1.0f / screenWidth;
			resultHeightRatio = imageHeight * 1.0f / screenHeight;
		}
		return new double[]{resultWidthRatio, resultHeightRatio};
	}

	public static double[] getImageWidthAndHeight(String path)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		double imageWidth = options.outWidth;
		double imageHeight = options.outHeight;
		return new double[]{imageWidth, imageHeight};
	}


	public static Bitmap compressImage(String path, int maxWidth, int maxHeight)
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, option);
		int imageWidth = option.outWidth;
		int imageHeight = option.outHeight;
		int sampleSize = 1;
		if (imageWidth > maxWidth || imageHeight > maxHeight)
		{
			float widthScale = imageWidth * 1.0f / maxWidth;
			float heightScale = imageHeight * 1.0f / maxHeight;
			sampleSize = Math.round(Math.max(widthScale, heightScale));
		}
		option.inSampleSize = sampleSize;
		option.inJustDecodeBounds = false;
		int resultWidth = imageWidth / sampleSize;
		int resultHeight = imageHeight / sampleSize;
		Logger.e("inSampleSize:" + sampleSize + ",resultWidth:" + resultWidth + ",resultHeight:" +
						resultHeight);
		Bitmap bitmap = BitmapFactory.decodeFile(path, option);
//		Logger.e("bitmap width:"+bitmap.getWidth()+",bitmap height:"+bitmap.getHeight());
		return bitmap;
	}

}
