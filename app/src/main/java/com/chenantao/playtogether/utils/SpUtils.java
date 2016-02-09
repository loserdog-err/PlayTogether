package com.chenantao.playtogether.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chenantao on 2015/6/29.
 */
public class SpUtils
{
	//存储当前登录用户的id的文件名
	public static final String DEFAULT_FILE_NAME = "playTogetherAttr";

	/**
	 * 设置属性根据指定的文件名
	 *
	 * @param context
	 * @param key
	 * @param value
	 * @param fileName
	 */
	public static void setStringProperty(Context context, String key, String value, String
					fileName)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context
						.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 设置字符串属性根据默认的文件名
	 *
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setStringProperty(Context context, String key, String value)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void setIntProperty(Context context, String key, int value)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 计数器，增加指定数值的幅度
	 *
	 * @param context
	 * @param key
	 * @param amount
	 */
	public static void increment(Context context, String key, int amount)
	{
//		Logger.e("increment");
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE);
		int count = sp.getInt(key, -1);
		if (count == -1) count = 0;
		count++;
		SharedPreferences.Editor editor = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE).edit();
		editor.putInt(key, count);
		editor.commit();
	}

	/**
	 * 设置字符串的set集合根据默认的文件名
	 *
	 * @param context
	 * @param key
	 * @param set
	 */
	public static void setStringSetProperty(Context context, String key, Set<String> set)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE).edit();
		editor.putStringSet(key, set);
		editor.commit();
	}

	/**
	 * 设置字符串的set集合根据指定的文件名
	 *
	 * @param context
	 * @param key
	 * @param set
	 */
	public static void setStringSetProperty(Context context, String key, Set<String> set, String
					fileName)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context
						.MODE_PRIVATE).edit();
		editor.putStringSet(key, set);
		editor.commit();
	}

	/**
	 * 根据key从指定的文件中获取字符串值
	 *
	 * @param context
	 * @param key
	 * @param fileName
	 * @return
	 */
	public static String getStringProperty(Context context, String key, String fileName)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	/**
	 * 根据key从默认的文件中获取字符串值
	 *
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getStringProperty(Context context, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	public static int getIntProperty(Context context, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}

	/**
	 * 根据key从默认的文件中获取字符串集合
	 *
	 * @param context
	 * @param key
	 * @return
	 */
	public static Set<String> getStringSetProperty(Context context, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(DEFAULT_FILE_NAME, Context
						.MODE_PRIVATE);
		return sp.getStringSet(key, null);
	}

	/**
	 * 根据key从指定的文件中获取字符串集合
	 *
	 * @param context
	 * @param key
	 * @return
	 */
	public static Set<String> getStringSetProperty(Context context, String key, String fileName)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getStringSet(key, new HashSet<String>());
	}


}
