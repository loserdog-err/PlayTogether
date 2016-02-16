package com.chenantao.playtogether.chat.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.avos.avospush.notification.NotificationCompat;
import com.chenantao.playtogether.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wli on 15/8/26.
 */
public class NotificationUtils
{
	private static Notification mNotification;
	private static NotificationCompat.Builder mBuilder;
	private static NotificationManager mManager;

	private static final int NOTIFICATION_ID = 0X0107;

	/**
	 * tag list，用来标记是否应该展示 Notification
	 * 比如已经在聊天页面了，实际就不应该再弹出 notification
	 */
	private static List<String> notificationTagList = new LinkedList<String>();

	/**
	 * 添加 tag 到 tag list，在 MessageHandler 弹出 notification 前会判断是否与此 tag 相等
	 * 若相等，则不弹，反之，则弹出
	 *
	 * @param tag
	 */
	public static void addTag(String tag)
	{
		if (!notificationTagList.contains(tag))
		{
			notificationTagList.add(tag);
		}
	}

	/**
	 * 在 tag list 中 remove 该 tag
	 *
	 * @param tag
	 */
	public static void removeTag(String tag)
	{
		notificationTagList.remove(tag);
	}

	/**
	 * 判断是否应该弹出 notification
	 * 判断标准是该 tag 是否包含在 tag list 中
	 *
	 * @param tag
	 * @return
	 */
	public static boolean isShowNotification(String tag)
	{
		return !notificationTagList.contains(tag);
	}

	public static void showNotification(Context context, String title, String content, String sound,
																			Intent intent, Bitmap bitmap)
	{
		if (mBuilder == null)
		{
			intent.setFlags(0);
			PendingIntent contentIntent = PendingIntent
							.getBroadcast(context, NOTIFICATION_ID, intent, 0);
			mBuilder = new NotificationCompat.Builder(context)
							.setSmallIcon(R.mipmap.avatar)
							.setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent)
							.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
							.setContentText(content);
		} else
		{
			mBuilder.setContentText(content);
		}
		NotificationManager manager =
						(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = mBuilder.build();
//		if (sound != null && sound.trim().length() > 0)
//		{
//			notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound);
//		}
		manager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
