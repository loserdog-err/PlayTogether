package com.chenantao.playtogether.chat.mvc.view.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

/**
 * Created by Chenantao_gg on 2016/1/29.
 * 发送信息的viewholder
 */
public class ChatToHolderChat extends ChatCommonViewHolder
{

	private Bitmap mAvatar;
	private Context mContext;

	public ChatToHolderChat(View itemView, Context context, AVIMConversation mConversation)
	{
		super(itemView, context);
		mContext = context;
	}
//	@Bind(R.id.tvUsername)
//	TextView mTvUsername;


	@Override
	public void bindData(AVIMMessage message, boolean shouldShowTime)
	{
		super.bindData(message, shouldShowTime);
//		if (mAvatar == null)
//		{
//			mChatUserBll.getAvatarByUsername(message.getFrom())
//							.observeOn(AndroidSchedulers.mainThread())
//							.subscribeOn(Schedulers.io())
//							.subscribe(new Action1<String>()
//							{
//								@Override
//								public void call(String url)
//								{
//									PicassoUtils.displayFitImage(mContext, Uri.parse(url), mIvAvatar, null);
//								}
//							});
//		} else
//		{
//			mIvAvatar.setImageBitmap(mAvatar);
//		}
	}
}
