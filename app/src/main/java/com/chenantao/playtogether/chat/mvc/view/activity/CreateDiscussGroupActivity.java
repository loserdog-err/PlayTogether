package com.chenantao.playtogether.chat.mvc.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.mvc.controller.CreateDiscussGroupController;
import com.chenantao.playtogether.chat.mvc.view.adapter.SelectMemberAdapter;
import com.chenantao.playtogether.chat.mvc.view.fragment.FriendListFragment;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/6.
 * 创建讨论组选择会员的activity
 */
public class CreateDiscussGroupActivity extends BaseActivity
{

	@Bind(R.id.etGroupName)
	EditText mEtGroupName;


	@Bind(R.id.rvFriendList)
	RecyclerView mRvFriendList;
	SelectMemberAdapter mAdapter;
	LinearLayoutManager mLayoutManager;
	List<User> mDatas;

	@Inject
	public CreateDiscussGroupController mController;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_create_discuss_group;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		mDatas = FriendListFragment.friendList;
		//初始化 recyclerview
		mRvFriendList.setAdapter(mAdapter = new SelectMemberAdapter(this, mDatas));
		mRvFriendList.setLayoutManager(mLayoutManager = new LinearLayoutManager(this,
						LinearLayoutManager.VERTICAL, false));
	}

	public void createDiscussGroupSuccess()
	{
		Toast.makeText(this, "创建群组成功", Toast.LENGTH_SHORT).show();
		finish();
	}

	public void createDiscussGroupFail(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		MenuItem item = menu.findItem(R.id.menu_item_btn);
		item.setTitle("创建");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_item_btn:
				String discussName = mEtGroupName.getText().toString();
				if (TextUtils.isEmpty(discussName))
				{
					Toast.makeText(this, "团队名称不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				List<User> selectedUser = mAdapter.getSelectMember();
				if (selectedUser.size() < 1)
				{
					Toast.makeText(this, "选取几个小伙伴吧", Toast.LENGTH_SHORT).show();
					break;
				}
				mController.createDiscussGroup(selectedUser, discussName);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
