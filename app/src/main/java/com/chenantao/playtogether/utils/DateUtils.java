package com.chenantao.playtogether.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 操作的date的工具类
 *
 * @author chenantao
 */
public class DateUtils
{


	/**
	 * 将特定格式的字符串转换为date对象
	 *
	 * @param date
	 * @param format 格式，如yyyy-MM-dd
	 * @return
	 */
	public static Date string2date(String date, String format)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date result = sdf.parse(date);
			sdf = null;
			return result;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 将date对象转换 为特定格式的字符串
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2string(Date date, String format)
	{
		if (date == null)
		{
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String result = sdf.format(date);
		sdf = null;
		return result;
	}

	/**
	 * 将date对象转换 为特定默认格式的字符串
	 *
	 * @param date
	 * @return
	 */
	public static String date2string(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = sdf.format(date);
		sdf = null;
		return result;
	}

	/**
	 * 将long型的毫秒数转化为特定格式的String时间
	 *
	 * @param format
	 * @param mill
	 * @return
	 */
	public static String long2date(String format, long mill)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date(mill);
		return sdf.format(date);
	}
	/**
	 * 将long型的毫秒数转化为默认(HH:mm:ss)格式的String时间
	 *
	 * @param mill
	 * @return
	 */
	public static String long2date(long mill)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(mill);
		return sdf.format(date);
	}


}
