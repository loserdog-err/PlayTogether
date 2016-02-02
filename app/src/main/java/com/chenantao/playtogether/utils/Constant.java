package com.chenantao.playtogether.utils;

import com.chenantao.playtogether.mvc.model.bean.InterestCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/18.
 */
public class Constant
{

	/*用户登录注册所需要的一些常量*/
	public static final int USERNAME_MIN_LENGTH = 2;
	public static final int USERNAME_MAX_LENGTH = 8;
	public static final int PASSWORD_MIN_LENGTH = 4;
	public static final int PASSWORD_MAX_LENGTH = 16;

	//兴趣类别常量
	public static final int CATEGORY_MOVIE = 0;
	public static final int CATEGORY_EXERCISE = 1;
	public static final int CATEGORY_FOOD = 2;

	//一些通用常量
	public static final int GENDER_MAN = 0;
	public static final int GENDER_WOMEN = 1;
	public static final int GENDER_ALL = 2;

	//分页显示的常量
	public static final int PAGE_SIZE=20;

	public static final List<InterestCategory> CATEGORIES = new ArrayList<>();

	public static final String[] CONSTELLATION = new String[]{"", "", "白羊座", "金牛座", "双子座", "巨蟹座",
			"狮子座",
			"处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座", "", ""};

	public static final String ONE_HOUR = "一小时";
	public static final String TWO_HOUR = "二小时";
	public static final String THREE_HOUR = "三小时";
	public static final String FOUR_HOUR = "四小时";
	public static final String FIVE_HOUR = "五小时";
	public static final String ONE_DAY = "一天";
	public static final String TWO_DAY = "二天";
	public static final String THREE_DAY = "三天";
	public static final String ONE_WEEK = "一星期";
	public static final String TWO_WEEK = "两星期";
	public static final String ONE_MONTH = "一个月";
	public static final String NEVER = "永不";


	public static final String[] EXPIRE_DATE = new String[]{"", "", ONE_HOUR, TWO_HOUR, THREE_HOUR,
			FOUR_HOUR,
			FIVE_HOUR, ONE_DAY, TWO_DAY, THREE_DAY, ONE_WEEK, TWO_WEEK,
			ONE_MONTH, NEVER, "", ""};


	public static final int MAX_UPLOAD_PIC = 10;//上传图片的最大数量

	public static final int AVATAR_WIDTH=150;
	public static final int AVATAR_HEIGHT=150;
}
