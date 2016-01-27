package com.chenantao.playtogether.utils;

import android.app.Activity;
import android.content.Intent;

import com.chenantao.playtogether.mvc.view.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class ActivityCollector
{
	private static List<BaseActivity> activities = new ArrayList<>();

	public static void addActivity(BaseActivity activity)
	{
		activities.add(activity);
	}

	public static void removeActivity(BaseActivity activity)
	{
		activities.remove(activity);
	}

	/**
	 * 移除所有activity
	 */
	public static void removeActivitys()
	{
		for (int i = 0; i < activities.size(); i++)
		{
			Activity activity = activities.get(i);
			activity.finish();
		}
		activities = null;

	}

	public static void reStart(BaseActivity fromActivity, Class<?>... toClass)
	{
		if (toClass != null)
		{
			for (int i = 0; i < toClass.length; i++)
			{
				fromActivity.startActivity(new Intent(fromActivity, toClass[i]));
			}
		}
		for (BaseActivity baseActivity : activities)
		{
			if (!baseActivity.isFinishing())
			{
				baseActivity.finish();
			}
		}
	}
}
