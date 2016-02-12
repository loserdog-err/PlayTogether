package com.chenantao.playtogether.mvc.model.bean;

/**
 * Created by Chenantao_gg on 2016/1/27.
 * 用于设置筛选条件的bean
 * 这个bean只用于类跟类之间的传递，不用于存储以及接收服务器的数据
 */
public class InvitationCondition
{
	private int category;
	private String gender = "不限";

	private int minAge = Invitation.MIN_AGE;

	private int maxAge = Invitation.MAX_AGE;

	private int skip;//加载更多时用到，需要跳过多少条数
//	private boolean orderByNearest;//离我最近
//	private boolean orderByNewly;//最新
//	private boolean orderByMostMember;//同伙最多


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

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
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

	public int getSkip()
	{
		return skip;
	}

	public void setSkip(int skip)
	{
		this.skip = skip;
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
