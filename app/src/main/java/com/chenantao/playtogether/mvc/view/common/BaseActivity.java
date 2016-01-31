package com.chenantao.playtogether.mvc.view.common;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.chenantao.autolayout.AutoLayoutActivity;
import com.chenantao.playtogether.MyApplication;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.injector.component.ActivityComponent;
import com.chenantao.playtogether.injector.component.DaggerActivityComponent;
import com.chenantao.playtogether.injector.modules.ActivityModule;
import com.chenantao.playtogether.utils.ActivityCollector;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/17.
 */
public abstract class BaseActivity extends AutoLayoutActivity
{
	public Toolbar mToolbar;
	protected ActivityComponent mActivityComponent;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		//将当前activity添加到activity集合
		ActivityCollector.addActivity(this);
		ButterKnife.bind(this);
		//dagger2注入
		mActivityComponent = DaggerActivityComponent.builder()
				.activityModule(new ActivityModule(this))
				.applicationComponent(((MyApplication) getApplication()).getApplicationComponent())
				.build();
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar != null)
		{
			setSupportActionBar(mToolbar);
		}
		injectActivity();
		afterCreate();
	}


	public abstract int getLayoutId();

	public abstract void injectActivity();//为activity添加注入依赖

	public abstract void afterCreate();
//	public abstract void initEvent();


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityCollector.removeActivity(this);
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
	}
}
