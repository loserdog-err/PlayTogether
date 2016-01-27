package com.chenantao.playtogether.mvc.model.bean;

/**
 * Created by Chenantao_gg on 2016/1/27.
 * 用于设置筛选条件的bean
 * 这个bean只用于类跟类之间的传递，不用于存储以及接收服务器的数据
 */
public class InvitationCondition
{
	private int category;
	private int gender=-1;

	private int minAge;

	private int maxAge;


	private OrderBy orderBy;


	public enum OrderBy
	{
		NEAREST, NEWLY
	}

	public int getCategory()
	{
		return category;
	}

	public void setCategory(int category)
	{
		this.category = category;
	}

	public int getGender()
	{
		return gender;
	}

	public void setGender(int gender)
	{
		this.gender = gender;
	}

	public int getMinAge()
	{
		return minAge;
	}

	public void setMinAge(int minAge)
	{
		this.minAge = minAge;
	}

	public int getMaxAge()
	{
		return maxAge;
	}

	public void setMaxAge(int maxAge)
	{
		this.maxAge = maxAge;
	}

	public OrderBy getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy)
	{
		this.orderBy = orderBy;
	}

	@Override
	public String toString()
	{
		return "InvitationCondition{" +
				"category=" + category +
				", gender=" + gender +
				", minAge=" + minAge +
				", maxAge=" + maxAge +
				", orderBy=" + orderBy +
				'}';
	}
}
