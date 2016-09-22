package com.chenantao.playtogether.chat.mvc.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.event.EventFriendListChange;
import com.chenantao.playtogether.chat.mvc.controller.FriendListController;
import com.chenantao.playtogether.chat.mvc.view.adapter.FriendListAdapter;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/2/5.
 */
public class FriendListFragment extends BaseFragment
{
	public static final String EXTRA_USERNAME = "username";


	//recyclerview 四件套
	@Bind(R.id.rvFriendList)
	RecyclerView mRvFriendList;
	LinearLayoutManager mLayoutManager;
	FriendListAdapter mAdapter;
	ArrayList<User> mDatas;

	public static List<User> friendList;

	@Inject
	FriendListController mController;


	public static FriendListFragment newInstance()
	{
		return new FriendListFragment();
	}

	@Override
	public View inflateView(LayoutInflater inflater)
	{
		return inflater.inflate(R.layout.fragment_friend_list, null);
	}

	@Override
	protected void injectFragment()
	{
		mFragmentComponent.inject(this);
	}

	@Override
	public void afterViewCreated(View view)
	{
		EventBus.getDefault().register(this);
		//初始化 recyclerview
		mRvFriendList.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity(),
						LinearLayoutManager.VERTICAL, false));
		mController.getFriends(AVUser.getCurrentUser(User.class).getUsername());
	}

	public void getFriendsSuccess(List<User> friends)
	{
		friendList = friends;
		mDatas = (ArrayList<User>) friends;
		mRvFriendList.setAdapter(mAdapter = new FriendListAdapter(getActivity(), friends));
		mAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener()
		{
			@Override
			public void onClick(FriendListAdapter.FriendListViewHolder holder, int position)
			{
				User user = mDatas.get(position);
				mController.createConversation(user);
			}
		});
	}

	public void getFriendsFail(String msg)
	{
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}


	/**
	 * 好友列表发生变化
	 */
	public void onEvent(EventFriendListChange event)
	{
		mController.getFriends(AVUser.getCurrentUser().getUsername());
	}

}
