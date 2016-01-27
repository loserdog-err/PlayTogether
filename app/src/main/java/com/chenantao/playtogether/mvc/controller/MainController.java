package com.chenantao.playtogether.mvc.controller;

import android.app.Activity;

import com.chenantao.playtogether.MainActivity;
import com.chenantao.playtogether.mvc.model.bll.UserBll;

import javax.inject.Inject;

/**
 * Created by Chenantao_gg on 2016/1/17.
 */
public class MainController
{
	@Inject
	UserBll mUserBll;

	private MainActivity mActivity;

	@Inject
	public MainController(Activity activity)
	{
		mActivity = (MainActivity) activity;
	}

	public void getUsername()
	{
	}
}
