package com.chenantao.playtogether.chat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;

/**
 * Created by wli on 15/9/8.
 * 因为 notification 点击时，控制权不在 app，此时如果 app 被 kill 或者上下文改变后，
 * 有可能对 notification 的响应会做相应的变化，所以此处将所有 notification 都发送至此类，
 * 然后由此类做分发。
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		if (AVImClientManager.getInstance().getClient() == null)
		{
			AVImClientManager.getInstance().open(AVUser.getCurrentUser().getUsername(),
							new AVIMClientCallback()
							{
								@Override
								public void done(AVIMClient avimClient, AVIMException e)
								{
									gotoChatActivity(context, intent);
								}
							});
		} else
		{
			String conversationId = intent.getStringExtra(ChatConstant.CONVERSATION_ID);
			if (!TextUtils.isEmpty(conversationId))
			{
				gotoChatActivity(context, intent);
			}
		}
	}


	/**
	 * 如果 app 上下文已经缺失，则跳转到登陆页面，走重新登陆的流程，
	 * 这里im的登录界面为聊天的主界面 ChatHomeActivity
	 * <p/>
	 *
	 * @param context
	 */
	private void gotoLoginActivity(Context context)
	{
		Intent startActivityIntent = new Intent(context, LoginActivity.class);
		startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startActivityIntent);
	}


	/**
	 * 跳转至聊天页面
	 */
	private void gotoChatActivity(Context context, Intent intent)
	{
		Intent startActivityIntent = new Intent(context, ChatActivity.class);
		startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivityIntent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, intent.getStringExtra
						(ChatConstant.CONVERSATION_ID));
		startActivityIntent.putExtra(ChatActivity.EXTRA_CONVERSATION_NAME, intent.getStringExtra
						(ChatConstant.CONVERSATION_NAME));
		context.startActivity(startActivityIntent);
	}
}
