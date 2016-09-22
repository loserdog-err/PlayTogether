package com.chenantao.playtogether.mvc.model.bean;

import java.util.List;

/**
 * Created by Chenantao_gg on 2016/2/10.
 * 这不是一个保存到服务器的bean，仅仅是封装服务器数据然后用于在个人中心展示数据的bean
 */
public class PersonalCenterHomeBean
{
	private User user;


	private int inviteCount;

	private int beInvitedCount;

	public List<Invitation> newlyDynamic;//最新动态

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}


	public int getInviteCount()
	{
		return inviteCount;
	}

	public void setInviteCount(int inviteCount)
	{
		this.inviteCount = inviteCount;
	}

	public int getBeInvitedCount()
	{
		return beInvitedCount;
	}

	public void setBeInvitedCount(int beInvitedCount)
	{
		this.beInvitedCount = beInvitedCount;
	}

	public List<Invitation> getNewlyDynamic()
	{
		return newlyDynamic;
	}

	public void setNewlyDynamic(List<Invitation> newlyDynamic)
	{
		this.newlyDynamic = newlyDynamic;
	}
}
