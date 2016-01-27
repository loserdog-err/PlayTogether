package com.chenantao.playtogether.mvc.view.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenantao.playtogether.MyApplication;
import com.chenantao.playtogether.injector.component.DaggerFragmentComponent;
import com.chenantao.playtogether.injector.component.FragmentComponent;
import com.chenantao.playtogether.injector.modules.FragmentModule;

import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
public abstract class BaseFragment extends Fragment
{
	public FragmentComponent mFragmentComponent;

	/**
	 * 初始化UI
	 *
	 * @param inflater
	 * @return
	 */
	public abstract View inflateView(LayoutInflater inflater);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState)
	{
		View v = inflateView(inflater);
		ButterKnife.bind(this, v);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		mFragmentComponent = DaggerFragmentComponent.builder()
				.fragmentModule(new FragmentModule(this))
				.applicationComponent(((MyApplication) getActivity().getApplication())
						.getApplicationComponent())
				.build();
		injectFragment();
		getBundle(getArguments());
		afterViewCreated(view);
		super.onViewCreated(view, savedInstanceState);
	}

	protected abstract void getBundle(Bundle arguments);

	protected abstract void injectFragment();

	public abstract void afterViewCreated(View view);

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		ButterKnife.unbind(this);
	}
}
