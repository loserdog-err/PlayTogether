package com.chenantao.playtogether.chat.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by chenantao on 2015/7/10.
 */
public class MediaManager
{
	private static MediaPlayer mMediaPlayer;
	private static boolean isPause;

	private static onMediaChangeListener mLastListener;//上一个监听，主要是为了重置时通知上一个调用播放的对象的状态

	public static void playSound(String path, final onMediaChangeListener listener)
	{
		if (mMediaPlayer == null)
		{
			mMediaPlayer = new MediaPlayer();
		} else
		{
			mMediaPlayer.reset();
			if (mLastListener != null)
				mLastListener.onStop();
		}
		mLastListener = listener;
		try
		{
			//报错监听
			mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
			{
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					mMediaPlayer.reset();
					if (listener != null)
						listener.onStop();
					return false;
				}
			});
			mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{
				@Override
				public void onCompletion(MediaPlayer mp)
				{
					if (listener != null)
						listener.onStop();
				}
			});
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			if (listener != null)
				listener.onStart();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//停止函数
	public static void pause()
	{
		if (mMediaPlayer != null && mMediaPlayer.isPlaying())
		{
			mMediaPlayer.pause();
			isPause = true;
		}
	}

	//继续
	public static void resume()
	{
		if (mMediaPlayer != null && isPause)
		{
			mMediaPlayer.start();
			isPause = false;
		}
	}


	public static void release()
	{
		if (mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	/**
	 * 播放停止的监听方法
	 * media play 的reset、stop、complete都需要回调stop这个方法
	 */
	public interface onMediaChangeListener
	{
		void onStop();

		void onStart();
	}

}
