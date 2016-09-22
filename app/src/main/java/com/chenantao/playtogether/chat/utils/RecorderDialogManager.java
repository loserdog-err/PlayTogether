package com.chenantao.playtogether.chat.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenantao.playtogether.R;


/**
 * Created by chenantao on 2015/7/9.
 * 录音时管理弹出来的音量对话框的类
 */
public class RecorderDialogManager
{

	private Context mContext;
	private Dialog mDialog;
	private LayoutInflater mInflater;
	private ImageView mIvRecorder;
	private ImageView mIvVoiceLevel;
	private TextView mTvText;
	private LinearLayout mLlContainer;

	public RecorderDialogManager(Context context)
	{
		mContext = context;
		mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
		mInflater = LayoutInflater.from(mContext);
		mLlContainer = (LinearLayout) mInflater.inflate(R.layout.dialog_recorder, null);
		mDialog.setContentView(mLlContainer);
		mIvRecorder = (ImageView) mLlContainer.findViewById(R.id.iv_recoder);
		mIvVoiceLevel = (ImageView) mLlContainer.findViewById(R.id.iv_voice_level);
		mTvText = (TextView) mLlContainer.findViewById(R.id.tv_text);
	}

	public void showRecordingDialog()
	{
		mIvRecorder.setVisibility(View.VISIBLE);
		mIvVoiceLevel.setVisibility(View.VISIBLE);
		mTvText.setVisibility(View.VISIBLE);
		mIvRecorder.setBackgroundResource(R.mipmap.recorder);
		mTvText.setText("手指上滑,取消录音");
		mDialog.show();
//	Log.e("TAG","show");
	}

	public void showWantToCancelDialog()
	{
		mIvRecorder.setVisibility(View.VISIBLE);
		mIvVoiceLevel.setVisibility(View.GONE);
		mTvText.setVisibility(View.VISIBLE);
		mIvRecorder.setBackgroundResource(R.mipmap.cancel);
		mTvText.setText("松开手指,取消发送");
	}

	public void showCancelDialog()
	{
		mIvRecorder.setVisibility(View.VISIBLE);
		mIvVoiceLevel.setVisibility(View.GONE);
		mTvText.setVisibility(View.VISIBLE);
		mIvRecorder.setBackgroundResource(R.mipmap.voice_to_short);
		mTvText.setText("录音已取消");
	}

	public void showTooShortDialog()
	{
		mIvRecorder.setVisibility(View.VISIBLE);
		mIvVoiceLevel.setVisibility(View.GONE);
		mTvText.setVisibility(View.VISIBLE);
		mIvRecorder.setBackgroundResource(R.mipmap.voice_to_short);
		mTvText.setText("录制时间过短");
	}

	public void dismissDialog()
	{
		if (mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
		}
	}

	public void updateVoiceLevel(int level)
	{
		if (mDialog != null && mDialog.isShowing())
		{
			int resId = mContext.getResources().getIdentifier("v" + level, "mipmap", mContext
							.getPackageName());
			mIvVoiceLevel.setBackgroundResource(resId);
		}
	}


}
