package com.chenantao.playtogether.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	 * @param format 格式，如yyyy-MM-dd
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

	public static Date string2date(String date)
	{
		return string2date(date, "yyyy-MM-dd");
	}

	/**
	 * 将date对象转换 为特定格式的字符串
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
	 */
	public static String date2string(Date date)
	{
		return date2string(date, "yyyy-MM-dd");
	}

	/**
	 * 将一个date转换为语义化的字符串，例如是今天就显示时间，例如06:06 昨天就显示昨天
	 * 前天就显示日期 ,例如 01-07
	 */
	public static String date2desc(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (date.getTime() > calendar.getTime().getTime())
		{
			//今天
			return date2string(date, "HH:mm");
		} else
		{
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
			if (date.getTime() > calendar.getTime().getTime())
			{
				//昨天
				return "昨天";
			} else
			{
				//昨天以前，显示具体日期
				return date2string(date, "MM-dd");
			}
		}

	}

	/**
	 * 将long型的毫秒数转化为特定格式的String时间
	 */
	public static String long2date(String format, long mill)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(mill);
	}

	/**
	 * 将long型的毫秒数转化为默认(HH:mm:ss)格式的String时间
	 */
	public static String long2date(long mill)
	{
		return long2date("HH:mm:ss", mill);
	}


}
