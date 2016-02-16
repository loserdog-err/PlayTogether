package com.chenantao.playtogether.chat.utils;

import android.media.MediaRecorder;
import android.os.Environment;

import com.chenantao.playtogether.utils.FileUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by chenantao on 2015/7/9.
 */
public class AudioManager
{

	private static AudioManager mAudioManager;

	private String mDir = "";
	File mFile;

	private MediaRecorder mMediaRecorder;

	private boolean isPrepare = false;


	public synchronized static AudioManager getInstance()
	{
		if (mAudioManager == null)
		{
			return new AudioManager(Environment.getExternalStorageDirectory() +
							FileUtils.VOICE_PATH);
		} else
		{
			return mAudioManager;
		}
	}

	public interface OnAudioPrepare
	{
		void hadPrepare();
	}

	public OnAudioPrepare mAudioStateListener;

	public void setAudioStateListener(OnAudioPrepare listener)
	{
		mAudioStateListener = listener;
	}


	private AudioManager(String dir)
	{
		mDir = dir;
	}

	/**
	 * 准备录音
	 */
	public void prepareAudio()
	{
		try
		{
			isPrepare = false;
			File dir = new File(mDir);
			if (!dir.exists())
				dir.mkdirs();
			String fileName = UUID.randomUUID() + ".amr";
			mFile = new File(dir, fileName);
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFile(mFile.getAbsolutePath());
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			isPrepare = true;
			if (mAudioStateListener != null)
			{
				mAudioStateListener.hadPrepare();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			isPrepare = false;
		}
	}

	/**
	 * 释放资源
	 */
	public void release()
	{
		if (mMediaRecorder != null)
		{
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			mFile = null;
		}
	}

	/**
	 * 取消录音
	 */
	public void cancel()
	{
		release();
		File file = new File(mDir);
		if (file.exists())
		{
			file.delete();
		}
	}


	/**
	 * 得到音量大小范围为1到7的整数
	 *
	 * @return
	 */
	public int getVoiceLevel()
	{
		try
		{
			int level = 0;
			if (isPrepare)
			{
				level = (7 * mMediaRecorder.getMaxAmplitude()) / 32768 + 1;
				return level;
			}
		} catch (Exception e)
		{
			return 1;
		}
		return 1;
	}

	/**
	 * 得到录制文件的路径
	 */
	public String getFilePath()
	{
		return mFile.getAbsolutePath();
	}

}
