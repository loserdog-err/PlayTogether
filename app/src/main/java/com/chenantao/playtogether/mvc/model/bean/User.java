package com.chenantao.playtogether.mvc.model.bean;

import android.os.Parcel;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.utils.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class User extends AVUser
{
	private String password;
//	private String desc;//个人描述

	public static final int SIMPLE_DESC_MAX_LENGTH = 14;
	public static final int DETAIL_DESC_MAX_LENGTH = 140;


	public static final String FIELD_USERNAME = "username";
	public static final String FIELD_LOCATION = "location";
	public static final String FIELD_GENDER = "gender";
	public static final String FIELD_AGE = "age";
	public static final String FIELD_OBJECT_ID = "objectId";
	public static final String FIELD_AVATAR = "avatar";
	public static final String FIELD_FAVORITE_ACTIVITY = "favoriteActivity";
	public static final String FIELD_GENDER_TREND = "genderTrend";
	public static final String FIELD_DETAIL_DESC = "desc";
	public static final String FIELD_SIMPLE_DETAIL = "simpleDesc";
	public static final String FIELD_CONSTELLATION = "constellation";
	public static final String FIELD_BIRTHDAY = "birthday";

	//聊天系统需要的字段常量
	public static final String FIELD_FRIENDS = "friends";

	private AVFile avatar;

	public User(String username, String password)
	{
		setUsername(username);
		this.password = password;
	}

	public User()
	{
	}

	//此处为我们的默认实现，当然你也可以自行实现
	public static final Creator CREATOR = AVObjectCreator.instance;

	public User(Parcel in)
	{
		super(in);
	}

	public String getUsername()
	{
		return getString("username");
	}

	public void setUsername(String username)
	{
		put("username", username);
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public AVFile getAvatar()
	{
		return getAVFile(FIELD_AVATAR);
	}

	public String getAvatarUrl()
	{
		AVFile avFile = this.getAvatar();
		if (avFile != null)
			return avFile.getThumbnailUrl(true, Constant.AVATAR_WIDTH, Constant.AVATAR_HEIGHT);
		else return null;
	}

	public void setAvatar(AVFile avatar)
	{
		put(FIELD_AVATAR, avatar);
	}

	public String getDetailDesc()
	{
		return getString(FIELD_DETAIL_DESC);
	}

	public void setDetailDesc(String desc)
	{
		put(FIELD_DETAIL_DESC, desc);
	}

	public String getSimpleDesc()
	{
		return getString(FIELD_SIMPLE_DETAIL);
	}

	public void setSimpleDesc(String desc)
	{
		put(FIELD_SIMPLE_DETAIL, desc);
	}

	public void setLocation(AVGeoPoint point)
	{
		put(FIELD_LOCATION, point);
	}

	public AVGeoPoint getLocation()
	{
		AVGeoPoint point = (AVGeoPoint) get(FIELD_LOCATION);
		if (point != null) return point;
		return null;
	}

	public void setGender(String gender)
	{
		put(FIELD_GENDER, gender);
	}

	public String getGender()
	{
		return getString(FIELD_GENDER);
	}

	public void setAge(int age)
	{
		put(FIELD_AGE, age);
	}

	public int getAge()
	{
		return getInt(FIELD_AGE);
	}

	public void setFriends(List<User> friends)
	{
		put(FIELD_FRIENDS, friends);
	}

	public List<User> getFriends()
	{
		List<User> list = getList(FIELD_FRIENDS);
		if (list == null)
		{
			return new ArrayList<User>();
		}
		return list;
	}

	public void setConstellation(String constellation)
	{
		put(FIELD_CONSTELLATION, constellation);
	}

	public String getConstellation()
	{
		return getString(FIELD_CONSTELLATION);
	}

	public void setBirthday(Date birthday)
	{
		put(FIELD_BIRTHDAY, birthday);
	}

	public Date getBirthday()
	{
		return getDate(FIELD_BIRTHDAY);
	}

	public String getFavoriteActivity()
	{
		return getString(FIELD_FAVORITE_ACTIVITY);
	}

	public void setFavoriteActivity(String activity)
	{
		put(FIELD_FAVORITE_ACTIVITY, activity);
	}

	public String getGenderTrend()
	{
		return getString(FIELD_GENDER_TREND);
	}

	public void setGenderTrend(String strGender)
	{
		put(FIELD_GENDER_TREND, strGender);
	}

}
