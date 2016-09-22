package com.chenantao.playtogether.mvc.model.bean.event;

/**
 * Created by Chenantao_gg on 2016/1/27.
 */
public class EventLocate
{
	public double latitude;//纬度
	public double longitude;//经度

	public EventLocate(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
