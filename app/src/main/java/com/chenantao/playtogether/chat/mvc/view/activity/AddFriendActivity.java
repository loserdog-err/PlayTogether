package com.chenantao.playtogether.chat.mvc.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.mvc.controller.AddFriendController;
import com.chenantao.playtogether.chat.mvc.view.adapter.AddFriendAdapter;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/5.
 */
public class AddFriendActivity extends BaseActivity
{


	@Inject
	public AddFriendController mController;
	@Bind(R.id.recyclerView)
	RecyclerView mRecyclerView;

	private AddFriendAdapter mAdapter;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_add_friend;
	}

	private void initEvent()
	{
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		mController.getSimilarPeople(AVUser.getCurrentUser(User.class));
		initEvent();
	}


	public void getSimilarPeopleSuccess(List<User> users)
	{
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
						false));
		mRecyclerView.setAdapter(mAdapter = new AddFriendAdapter(this, users));
		mAdapter.setOnAddBtnClick(new AddFriendAdapter.OnAddBtnClickListener()
		{
			@Override
			public void onClick(String name, User user)
			{
				//这两个参数有一个肯定为空，判断是根据姓名添加好友还是直接使用user添加
				mController.addFriend(name, user);
			}
		});
	}

	public void addFriendSuccess()
	{
		Toast.makeText(this, "添加成功~", Toast.LENGTH_SHORT).show();
		finish();
	}

	public void addFriendError(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
