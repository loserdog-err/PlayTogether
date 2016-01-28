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

	public static String TABLE_NAME = "_invitation";

	//性别常量
//	public static final int GENDER_MAN = 0;
//	public static final int GENDER_WOMEN = 1;
//	public static final int GENDER_ALL = 2;
	public static final int MAX_AGE = 99;
	public static final int MIN_AGE = 10;
	//类别常量
//	public static final int CATEGORY_MOVIE = 0;
//	public static final int CATEGORY_EXERCISE = 1;
//	public static final int CATEGORY_FOOD = 2;
//	private String title;//邀请函的标题
//	private String content;//邀请函的内容
	//邀请函的筛选条件
//	private int gender;
//	private int minAge;
//	private int maxAge;
//	private String constellation;
//	private Date expire;
//	private int category;
//
//	private User author;
//	private List<User> acceptInviteUsers;//受约用户


	public List<String> uploadPicsPath = new ArrayList<>();
	//	public static final String FIELD_TITLE = "title";
//	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_GENDER = "gender";
	public static final String FIELD_MIN_AGE = "minAge";
	public static final String FIELD_MAX_AGE = "maxAge";
	//	public static final String FIELD_CONSTELLATION = "constellation";
//	public static final String FIELD_EXPIRE = "expire";
	public static final String FIELD_CATEGORY = "category";
	public static final String FIELD_AUTHOR = "author";
	public static final String FIELD_PIC = "pics";//上传的照片
	public static final String FIELD_LOCATION = "location";//位置
	public static final String FIELD_ACCEPT_INVITE_USERS = "acceptInviteUsers";

	public Invitation()
	{
	}

	public User getAuthor()
	{
		return getAVObject("author");
	}

	public void setAuthor(User author)
	{
		put("author", author);
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
		return getString("title");
	}

	public void setTitle(String title)
	{
		put("title", title);
	}

	public String getContent()
	{
		return getString("content");
	}

	public void setContent(String content)
	{
		put("content", content);
	}

	public String getGender()
	{
		int gender = getInt("gender");
		if (gender == Constant.GENDER_MAN)
		{
			return "男";
		} else if (gender == Constant.GENDER_WOMEN)
		{
			return "女";
		} else
		{
			return "不限";
		}

	}

	public void setGender(int gender)
	{
		put("gender", gender);
	}

	public int getMinAge()
	{
		return getInt("minAge");
	}

	public void setMinAge(int minAge)
	{
		put("minAge", minAge);
	}

	public int getMaxAge()
	{
		return getInt("maxAge");
	}

	public void setMaxAge(int maxAge)
	{
		put("maxAge", maxAge);
	}

	public String getConstellation()
	{
		return getString("constellation");
	}

	public void setConstellation(String constellation)
	{
		if ("".equals(constellation))
		{
			put("constellation", "不限");
		} else
		{
			put("constellation", constellation);
		}
	}

	public String getExpire()
	{
		Date date = getDate("expire");
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
			put("expire", null);
			return;
		}
		put("expire", calendar.getTime());
	}

	public String getCategory()
	{
		int category = getInt("category");
		if (category == Constant.CATEGORY_MOVIE) return "电影";
		else if (category == Constant.CATEGORY_EXERCISE) return "运动";
		else if (category == Constant.CATEGORY_FOOD) return "美食";
		else return "美食";
	}

	public void setCategory(String category)
	{
		if ("美食".equals(category)) put("category", Constant.CATEGORY_FOOD);
		else if ("运动".equals(category)) put("category", Constant.CATEGORY_EXERCISE);
		else if ("电影".equals(category)) put("category", Constant.CATEGORY_MOVIE);
		else put("category", Constant.CATEGORY_MOVIE);
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
//		ArrayList list = (ArrayList) get(Invitation.FIELD_ACCEPT_INVITE_USERS);
//		User user = (User) list.get(0);
//		Logger.e("user:" + user.getUsername());
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
//		getAcceptInviteUsers();
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
