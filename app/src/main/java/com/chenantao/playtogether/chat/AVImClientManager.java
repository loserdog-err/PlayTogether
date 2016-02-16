package com.chenantao.playtogether.chat;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.chenantao.playtogether.chat.handler.DefaultHandler;

/**
 * Created by wli on 15/8/13.
 * 使用leancloud的im必须有一个client。
 * 这是管理im client的单例类
 */
public class AVImClientManager
{

	private static AVImClientManager imClientManager;

	private AVIMClient avimClient;
	private String clientId;

	public synchronized static AVImClientManager getInstance()
	{
		if (null == imClientManager)
		{
			imClientManager = new AVImClientManager();
		}
		return imClientManager;
	}

	private AVImClientManager()
	{
	}

	public void open(String clientId, AVIMClientCallback callback)
	{
		this.clientId = clientId;
		avimClient = AVIMClient.getInstance(clientId);
		DefaultHandler.mClient = avimClient;
		avimClient.open(callback);
	}

	public AVIMClient getClient()
	{
		return avimClient;
	}

	public String getClientId()
	{
		if (TextUtils.isEmpty(clientId))
		{
			return null;
		}
		return clientId;
	}
}
