package com.chenantao.playtogether.mvc.view.fragment.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.user.PersonalCenterHomeController;
import com.chenantao.playtogether.mvc.model.bean.PersonalCenterHomeBean;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.adapter.PersonalCenterHomeAdapter;
import com.chenantao.playtogether.mvc.view.common.BaseFragment;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/9.
 */
public class PersonalCenterHomeFragment extends BaseFragment
{
	public static final String TAG = PersonalCenterHomeFragment.class.getSimpleName();

	public static final String EXTRA_USER = "user";

	@Bind(R.id.recyclerView)
	RecyclerView mRecyclerView;
	private PersonalCenterHomeAdapter mAdapter;
	private LinearLayoutManager mLayoutManger;
	private PersonalCenterHomeBean mData;

	@Inject
	public PersonalCenterHomeController mController;

	@Override
	public View inflateView(LayoutInflater inflater)
	{
		return inflater.inflate(R.layout.fragment_personal_home, null);
	}

	public static PersonalCenterHomeFragment newInstance(User user)
	{
		PersonalCenterHomeFragment fragment = new PersonalCenterHomeFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_USER, user.toString());
//		bundle.putString(EXTRA_USER, userId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void injectFragment()
	{
		mFragmentComponent.inject(this);
	}

	@Override
	public void afterViewCreated(View view)
	{
		String strUser = getArguments().getString(EXTRA_USER);
		if (strUser == null)
		{
			Toast.makeText(getActivity(), "出了点问题..", Toast.LENGTH_SHORT).show();
			getActivity().finish();
		}
		User user = JSON.parseObject(strUser, User.class);
		mRecyclerView.setLayoutManager(mLayoutManger = new LinearLayoutManager(getActivity(),
						LinearLayoutManager.VERTICAL, false));
		mController.getHomeData(user);
	}

	public void getDataSuccess(PersonalCenterHomeBean data)
	{
		mData = data;
		mRecyclerView.setAdapter(mAdapter = new PersonalCenterHomeAdapter(getActivity(), mData));
		mAdapter.setOnChatBtnClickListener(new PersonalCenterHomeAdapter.OnChatBtnClickListener()
		{
			@Override
			public void onClick(User author)
			{
				mController.chatWithAuthor(author);
			}
		});
	}

	public void getDataFail(String msg)
	{
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
}
