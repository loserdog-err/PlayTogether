package com.chenantao.playtogether.utils;

import android.content.Context;
import android.location.Location;

import com.avos.avoscloud.AVGeoPoint;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by Chenantao_gg on 2016/1/27.
 * 使用了百度sdk的定位
 */
public class LocationUtils
{

	public static LocationClient mClient;

	public static LocationClient getLocationClient(Context context)
	{
		if (mClient == null)
		{
			synchronized (LocationUtils.class)
			{
				mClient = new LocationClient(context.getApplicationContext());
				initLocation();
				return mClient;
			}
		} else
		{
			return mClient;
		}
	}

	private static void initLocation()
	{
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
		);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span = 0;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
		// .getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);
		//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mClient.setLocOption(option);
	}

	public static void stopClient()
	{
		if (mClient != null && mClient.isStarted())
		{
			mClient.stop();
		}
	}

	public static double getDistance(AVGeoPoint p1, AVGeoPoint p2)
	{
		float[] results = new float[1];
		Location.distanceBetween(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2
				.getLongitude(), results);
		return results[0];
	}
}
