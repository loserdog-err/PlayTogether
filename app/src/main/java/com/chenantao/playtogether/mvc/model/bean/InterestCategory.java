package com.chenantao.playtogether.mvc.model.bean;

/**
 * Created by Chenantao_gg on 2016/1/19.
 * 邀请属于什么类别的(电影、运动、美食.etc)
 */
public class InterestCategory
{
	public static final String MOVIE = "电影";
	public static final String FOOD = "美食";
	public static final String EXERCISE = "运动";

	private String categoryName;
	private int categoryIcon;

	public InterestCategory()
	{
	}

	public InterestCategory(String categoryName, int categoryIcon)
	{
		this.categoryName = categoryName;
		this.categoryIcon = categoryIcon;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public int getCategoryIcon()
	{
		return categoryIcon;
	}

	public void setCategoryIcon(int categoryIcon)
	{
		this.categoryIcon = categoryIcon;
	}
}
