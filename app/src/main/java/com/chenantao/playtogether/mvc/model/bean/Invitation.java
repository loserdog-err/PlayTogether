package com.chenantao.playtogether.mvc.model.bean;

import android.os.Parcel;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/19.
 */
@AVClassName("invitation")
public class Invitation extends AVObject
{

	//上传图片的最大宽高
	public static final int UPLOAD_PIC_WIDTH = 1000;
	public static final int UPLOAD_PIC_HEIGHT = 1000;

	public static final int MAX_AGE = 99;
	public static final int MIN_AGE = 10;


	public List<String> uploadPicsPath = new ArrayList<>();
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_GENDER = "gender";
	public static final String FIELD_MIN_AGE = "minAge";
	public static final String FIELD_MAX_AGE = "maxAge";
	public static final String FIELD_CONSTELLATION = "constellation";
	public static final String FIELD_EXPIRE = "expire";
	public static final String FIELD_CATEGORY = "category";
	public static final String FIELD_AUTHOR = "author";
	public static final String FIELD_PIC = "pics";//上传的照片
	public static final String FIELD_LOCATION = "location";//位置
	public static final String FIELD_ACCEPT_INVITE_USERS = "acceptInviteUsers";
	public static final String FIELD_OBJECT_ID = "objectId";
	public static final String FIELD_IS_EXPIRE = "isExpire";//邀请是否过期

	public Invitation()
	{
	}

	public User getAuthor()
	{
		return getAVObject(FIELD_AUTHOR);
	}

	public void setAuthor(User author)
	{
		put(FIELD_AUTHOR, author);
	}

	//此处为我们的默认实现，当然你也可以自行实现
	public static final Creator CREATOR = AVObjectCreator.instance;

	public Invitation(Parcel in)
	{
		super(in);
	}

	public List<String> getUploadPicsPath()
	{
		return uploadPicsPath;
	}

	public void setUploadPicsPath(List<String> uploadPicsPath)
	{
		this.uploadPicsPath = uploadPicsPath;
	}

	public List<AVFile> getPics()
	{
		return getList(FIELD_PIC);
	}

	public void setPics(List<AVFile> pics)
	{
		addAll(FIELD_PIC, pics);
	}

	public String getTitle()
	{
		return getString(FIELD_TITLE);
	}

	public void setTitle(String title)
	{
		put(FIELD_TITLE, title);
	}

	public String getContent()
	{
		return getString(FIELD_CONTENT);
	}

	public void setContent(String content)
	{
		put(FIELD_CONTENT, content);
	}

	public String getGender()
	{
		return getString(FIELD_GENDER);
	}

	public void setGender(String gender)
	{
		put(FIELD_GENDER, gender);
	}

	public int getMinAge()
	{
		return getInt(FIELD_MIN_AGE);
	}

	public void setMinAge(int minAge)
	{
		put(FIELD_MIN_AGE, minAge);
	}

	public int getMaxAge()
	{
		return getInt(FIELD_MAX_AGE);
	}

	public void setMaxAge(int maxAge)
	{
		put(FIELD_MAX_AGE, maxAge);
	}

	public String getConstellation()
	{
		return getString(FIELD_CONSTELLATION);
	}

	public void setConstellation(String constellation)
	{
		if ("".equals(constellation))
		{
			put(FIELD_CONSTELLATION, "不限");
		} else
		{
			put(FIELD_CONSTELLATION, constellation);
		}
	}

	public boolean getIsExpire()
	{
		return getBoolean(FIELD_IS_EXPIRE);
	}

	public void setIsExpire(boolean isExpire)
	{
		put(FIELD_IS_EXPIRE, isExpire);
	}

	public String getExpire()
	{
		Date date = getDate(FIELD_EXPIRE);
		String expire = DateUtils.date2string(date, "MM月dd日HH时mm分");
		if ("".equals(expire))
		{
			return "不限";
		}
		return expire;
	}

	public void setExpire(String strExpire)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (Constant.ONE_HOUR.equals(strExpire))
		{
			calendar.add(Calendar.HOUR, 1);
		} else if (Constant.TWO_HOUR.equals(strExpire))
		{
			calendar.add(Calendar.HOUR, 2);
		} else if (Constant.THREE_HOUR.equals(strExpire))
		{
			calendar.add(Calendar.HOUR, 3);
		} else if (Constant.FOUR_HOUR.equals(strExpire))
		{
			calendar.add(Calendar.HOUR, 4);
		} else if (Constant.FIVE_HOUR.equals(strExpire))
		{
			calendar.add(Calendar.HOUR, 5);
		} else if (Constant.ONE_DAY.equals(strExpire))
		{
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		} else if (Constant.TWO_DAY.equals(strExpire))
		{
			calendar.add(Calendar.DAY_OF_YEAR, 2);
		} else if (Constant.THREE_DAY.equals(strExpire))
		{
			calendar.add(Calendar.DAY_OF_YEAR, 3);
		} else if (Constant.ONE_WEEK.equals(strExpire))
		{
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		} else if (Constant.TWO_WEEK.equals(strExpire))
		{
			calendar.add(Calendar.WEEK_OF_YEAR, 2);
		} else if (Constant.ONE_MONTH.equals(strExpire))
		{
			calendar.add(Calendar.MONTH, 1);
		} else
		{
			put(FIELD_EXPIRE, null);
			return;
		}
		put(FIELD_EXPIRE, calendar.getTime());
	}

	public String getCategory()
	{
		return getString(FIELD_CATEGORY);
	}

	public void setCategory(String category)
	{
		put(FIELD_CATEGORY, category);
	}

	public static String convertConstellation(String constellation)
	{
		if ("".equals(constellation))
		{
			return "不限";
		}
		return constellation;

	}


	public List<User> getAcceptInviteUsers()
	{
		List<User> list = getList(FIELD_ACCEPT_INVITE_USERS);
		if (list == null)
		{
			return new ArrayList<User>();
		}
		return list;
	}

	public void setAcceptInviteUsers(List<User> acceptInviteUsers)
	{
		put(FIELD_ACCEPT_INVITE_USERS, acceptInviteUsers);

	}

	public void setAcceptInviteUser(User user)
	{
		addUnique(FIELD_ACCEPT_INVITE_USERS, user);
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
}
