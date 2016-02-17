package com.chenantao.playtogether.chat.mvc.view.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.utils.AudioManager;
import com.chenantao.playtogether.chat.utils.RecorderDialogManager;
import com.orhanobut.logger.Logger;

import java.io.IOException;


/**
 * Created by chenantao on 2015/7/9.
 */
public class RecorderButton extends Button implements AudioManager.OnAudioPrepare
{

	public final int RECORDING = 1;
	public final int WANT_TO_CANCEL = 2;
	public final int NORMAL = 3;

	public int currentState;//标志当前按钮处于何种状态

	private boolean isRecording = false;
	private boolean isCancel;
	RecorderDialogManager mDialogManager;
	AudioManager mAudioManager;

	public float mRecordTime = 0;//秒

	public final int MSG_MEDIA_RECODER_PREPARED = 0x1;//代表media recoder准备完毕
	public final int MSG_UPDATE_VOICE_LEVEL = 0x2;
	public final int MSG_DIALOG_DISMISS = 0x3;

	private Animator mBtnAnim;


	//回调接口，代表录音已经完成
	public interface OnAudioRecordFinishListener
	{
		void onFinish(float seconds, String path) throws IOException;
	}

	public OnAudioRecordFinishListener mAudioRecordFinishListener;

	public void setOnAudioRecordFinishListener(OnAudioRecordFinishListener listener)
	{
		mAudioRecordFinishListener = listener;
	}


	public RecorderButton(Context context)
	{
		this(context, null);
	}

	public RecorderButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mBtnAnim = AnimatorInflater.loadAnimator(getContext(), R.animator.recorder_btn_press_anim);
		mBtnAnim.setTarget(this);
		mDialogManager = new RecorderDialogManager(context);
		mAudioManager = AudioManager.getInstance();
		this.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
//				Log.e("TAG", "long click");
				mBtnAnim.start();
				isRecording = true;
				mAudioManager.prepareAudio();
				return false;
			}
		});
		mAudioManager.setAudioStateListener(this);
	}

	/**
	 * 回调方法，代表MediaRecorder准备完毕
	 */
	@Override
	public void hadPrepare()
	{
		//开始录音
		setCurrentState(RECORDING);
		isRecording = true;
		//开启一条线程记录录音时间并更新ui
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (isRecording)
				{
					try
					{
						Thread.sleep(100);
						mRecordTime += 0.1f;
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					((Activity) getContext()).runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							int level = mAudioManager.getVoiceLevel();
							mDialogManager.updateVoiceLevel(level);
						}
					});
				}
			}
		}).start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				//如果超出了按钮的范围，设置状态为want to cancel
				if (isRecording)
				{
					if ((x > getWidth() || x < 0) || (y > getHeight() || y < 0))
					{
						setCurrentState(WANT_TO_CANCEL);
					} else
					{
						setCurrentState(RECORDING);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (currentState == RECORDING)
				{
					if (isRecording && mRecordTime > 1f)//代表正常录制结束
					{
						setCurrentState(NORMAL);
						if (mAudioRecordFinishListener != null)
						{
							try
							{
								mAudioRecordFinishListener.onFinish(mRecordTime, mAudioManager
												.getFilePath());
							} catch (Exception e)
							{
								e.printStackTrace();
								Logger.e("file not found");
							}
						}
						mAudioManager.release();
						mDialogManager.dismissDialog();
					} else
					{
						mDialogManager.showTooShortDialog();
						delayDismissDialog();
						mAudioManager.cancel();
					}
				} else if (currentState == WANT_TO_CANCEL)
				{
					setCurrentState(NORMAL);
					mAudioManager.cancel();
					mDialogManager.showCancelDialog();
					delayDismissDialog();
				}
				//提升逼格，再次播放一次动画
				mBtnAnim.start();
				reset();
				break;
		}
		return super.onTouchEvent(event);

	}

	public void delayDismissDialog()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				mDialogManager.dismissDialog();
			}
		}, 1000);
	}

	private void reset()
	{
		isRecording = false;
		setCurrentState(NORMAL);
	}

	public void setCurrentState(int state)
	{
		switch (state)
		{
			case NORMAL:
				setBackgroundResource(R.mipmap.microphone);
				currentState = NORMAL;
//				this.setText("按住说话");
				break;
			case WANT_TO_CANCEL:
				currentState = WANT_TO_CANCEL;
				setBackgroundResource(R.mipmap.microphone);
//				setText("松开手指，取消发送");
				mDialogManager.showWantToCancelDialog();
				break;
			case RECORDING:
				currentState = RECORDING;
				setBackgroundResource(R.mipmap.microphone);
//				setText("松开完成录音");
				mDialogManager.showRecordingDialog();
				break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(event);
	}
}

