package com.chenantao.playtogether.mvc.model.bean;

import android.os.Parcel;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
public class User extends AVUser
{
	private String username;
	private String password;
	private String desc;//个人描述


	public static final String FIELD_LOCATION = "location";
	public static final String FIELD_GENDER = "gender";
	public static final String FIELD_AGE = "age";
	public static final String FIELD_OBJECT_ID = "objectId";

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
		return getAVFile("avatar");
	}

	public void setAvatar(AVFile avatar)
	{
		put("avatar", avatar);
	}

	public String getDesc()
	{
		return getString("desc");
	}

	public void setDesc(String desc)
	{
		put("desc", desc);
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

	public void setGender(int gender)
	{
		put(FIELD_GENDER,gender);
	}
	public int getGender()
	{
		return getInt(FIELD_GENDER);
	}

	public void setAge(int age)
	{
		put(FIELD_AGE,age);
	}

	public int getAge()
	{
		return getInt(FIELD_AGE);
	}
}
