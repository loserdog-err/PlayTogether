package com.chenantao.playtogether.chat.mvc.view.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.chat.utils.MediaManager;
import com.chenantao.playtogether.mvc.view.common.ShowImageActivity;
import com.chenantao.playtogether.mvc.view.widget.MultipleShapeImg;
import com.chenantao.playtogether.utils.DateUtils;
import com.chenantao.playtogether.utils.FileUtils;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.chenantao.playtogether.utils.ScreenUtils;

import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/1/30.
 * 由于聊天的viewholder具有极其相似的逻辑，所以抽取一个公共类出来
 */
public abstract class ChatCommonViewHolder extends RecyclerView.ViewHolder
{
	@Bind(R.id.tvPostTime)
	TextView mTvPostTime;
	@Bind(R.id.ivAvatar)
	MultipleShapeImg mIvAvatar;
	@Bind(R.id.tvContent)
	TextView mTvContent;
	@Bind(R.id.rlVoice)
	RelativeLayout mRlVoice;
	@Bind(R.id.ivVoice)
	ImageView mIvVoice;
	@Bind(R.id.ivImage)
	ImageView mIvImage;
	@Bind(R.id.length)
	TextView mTvLength;

	private Context mContext;


	public ChatCommonViewHolder(View itemView, Context context)
	{
		super(itemView);
		ButterKnife.bind(this, itemView);
		mContext = context;
	}


	public void bindData(AVIMMessage message, boolean shouldShowTime)
	{
		if (shouldShowTime)
			mTvPostTime.setText(DateUtils.date2desc(new Date(message.getTimestamp())));
		else
		{
			mTvPostTime.setText("");
		}
		String avatarUrl = null;
		if (message instanceof AVIMTextMessage)
		{
			AVIMTextMessage textMessage = (AVIMTextMessage) message;
			Map<String, Object> attrs = textMessage.getAttrs();
			if (attrs != null && attrs.get(ChatConstant.MSG_ATTR_AVATAR) != null)
			{
				avatarUrl = (String) attrs.get(ChatConstant.MSG_ATTR_AVATAR);
			}
			handleTextMessage(textMessage);

		} else if (message instanceof AVIMAudioMessage)
		{
			AVIMAudioMessage audioMessage = (AVIMAudioMessage) message;
			Map<String, Object> attrs = audioMessage.getAttrs();
			if (attrs != null && attrs.get(ChatConstant.MSG_ATTR_AVATAR) != null)
			{
				avatarUrl = (String) attrs.get(ChatConstant.MSG_ATTR_AVATAR);
			}
			handleAudioMessage(audioMessage);
		} else if (message instanceof AVIMImageMessage)
		{
			AVIMImageMessage imageMessage = (AVIMImageMessage) message;
			Map<String, Object> attrs = imageMessage.getAttrs();
			if (attrs != null && attrs.get(ChatConstant.MSG_ATTR_AVATAR) != null)
			{
				avatarUrl = (String) attrs.get(ChatConstant.MSG_ATTR_AVATAR);
			}
			handleImageMessage(imageMessage);
		}
		if (avatarUrl != null)
		{
			PicassoUtils.displayFitImage(mContext, Uri.parse(avatarUrl), mIvAvatar, null);
		} else
		{
			mIvAvatar.setImageResource(R.mipmap.avatar);
		}
	}

	/**
	 * 处理图片需要动态改变聊天框的长宽高，依赖上传到服务器的图片元数据
	 */
	private void handleImageMessage(final AVIMImageMessage message)
	{
		mTvContent.setVisibility(View.GONE);
		mRlVoice.setVisibility(View.GONE);
		mIvImage.setVisibility(View.VISIBLE);
		Map<String, Object> metaData = message.getFileMetaData();
		final int imageWidth = (int) metaData.get("width");
		final int imageHeight = (int) metaData.get("height");
		double[] ratio = FileUtils.compressIfMoreThanDesireHeightWidth(imageWidth, imageHeight,
						ChatConstant.MESSAGE_PIC_WIDTH_MAX_RATIO, ChatConstant.MESSAGE_PIC_HEIGHT_MAX_RATIO,
						mContext);
		int resultWidth = (int) (ScreenUtils.getScreenWidth(mContext) * ratio[0]);
		int resultHeight = (int) (ScreenUtils.getScreenHeight(mContext) * ratio[1]);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(resultWidth, resultHeight);
		mIvImage.setLayoutParams(params);
		//下载图片
		//如果是发送出去的，先看本地有没有路径，没有的话从服务器拿图片地址
		final Uri thumbUri;//用户在聊天界面展示图片的uri
		final Uri originalUri;//用户查看大图时候的uri
		String path = message.getLocalFilePath();
		if (path == null)
		{
			path = message.getAVFile().getThumbnailUrl(true, resultWidth, resultHeight);
			originalUri = Uri.parse(message.getFileUrl());
			thumbUri = Uri.parse(path);

		} else
		{
			thumbUri = originalUri = Uri.parse("file:///" + path);
		}
		PicassoUtils.displaySpecSizeImage(mContext, thumbUri, mIvImage, resultWidth, resultHeight,
						null);
		mIvImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)//点击查看大图
			{
				Intent intent = new Intent(mContext, ShowImageActivity.class);
				intent.putExtra(ShowImageActivity.EXTRA_WIDTH, imageWidth);
				intent.putExtra(ShowImageActivity.EXTRA_HEIGHT, imageHeight);
				intent.putExtra(ShowImageActivity.EXTRA_URI, originalUri);
				mContext.startActivity(intent);
			}
		});
	}

	private void handleTextMessage(AVIMTextMessage message)
	{
		//显示文本内容，隐藏语音，图片，视频内容
		mTvContent.setVisibility(View.VISIBLE);
		mIvImage.setVisibility(View.GONE);
		mRlVoice.setVisibility(View.GONE);
		AVIMTextMessage textMessage = message;
		mTvContent.setText(textMessage.getText());
//			mTvUsername.setText(textMessage.getFrom());
	}

	private void handleAudioMessage(final AVIMAudioMessage message)
	{
		mIvImage.setVisibility(View.GONE);
		mTvContent.setVisibility(View.GONE);
		mRlVoice.setVisibility(View.VISIBLE);
		final AVIMAudioMessage audioMessage = (AVIMAudioMessage) message;
		mTvLength.setText(Math.round(audioMessage.getDuration()) + "\"");
		mIvVoice.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//设置语音文件的背景图片并开始播放背景动画
				String path = audioMessage.getLocalFilePath();
				if (path == null)
					path = audioMessage.getFileUrl();
				MediaManager.playSound(path, new MediaManager.onMediaChangeListener()
				{
					@Override
					public void onStop()
					{
						if (message.getFrom().equals(AVImClientManager.getInstance().getClientId()))//发送出去的
							mIvVoice.setBackgroundResource(R.mipmap.volume_to);
						else mIvVoice.setBackgroundResource(R.mipmap.volume_from);
					}

					@Override
					public void onStart()
					{
						if (message.getFrom().equals(AVImClientManager.getInstance().getClientId()))//发送出去的
							mIvVoice.setBackgroundResource(R.drawable.play_recorder_anim);
						else
							mIvVoice.setBackgroundResource(R.drawable.play2_recorder_anim);
						AnimationDrawable drawable = (AnimationDrawable) mIvVoice
										.getBackground();
						drawable.start();
					}
				});
			}
		});
	}
}
