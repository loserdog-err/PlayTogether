package com.chenantao.playtogether.mvc.view.activity.invitation;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.invitation.InvitationCategoryController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.InvitationCondition;
import com.chenantao.playtogether.mvc.model.bean.event.EventLocate;
import com.chenantao.playtogether.mvc.view.adapter.InvitationCategoryAdapter;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.Constant;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/26.
 */
public class InvitationCategoryActivity extends BaseActivity
{

	public static final String EXTRA_CATEGORY = "category";

	public int mCategory;
	@Bind(R.id.collapsingToolbarLayout)
	CollapsingToolbarLayout mCollapsingToolbarLayout;
	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	//recyclerview 四件套
	@Bind(R.id.rvInvitation)
	RecyclerView mRvInvitation;
	LinearLayoutManager mLayoutManager;
	List<Invitation> mDatas;


	@Inject
	public InvitationCategoryController mController;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_invitation_category;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
			mCollapsingToolbarLayout.setTitle("运动");
			setHeader();
		}
		EventBus.getDefault().register(this);
		mCategory = getIntent().getIntExtra(EXTRA_CATEGORY, Constant.CATEGORY_FOOD);
		//初始化SwipeRefreshLayout
		mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
		initEvent();
		//初始化recyclerview
		mRvInvitation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
				false));
		InvitationCondition condition = new InvitationCondition();
		condition.setCategory(mCategory);
		//加载数据并显示加载框
		mController.loadData(condition);
		showProgress();
	}

	/**
	 * 根据类型设置头部，图片，文字等
	 */
	private void setHeader()
	{
	}

	private void initEvent()
	{
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}

	public void refreshDataSuccess(List<Invitation> invitations)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		mRvInvitation.setAdapter(new InvitationCategoryAdapter(this, invitations));
	}

	public void refreshDataFail(String msg)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void showProgress()
	{
		mSwipeRefreshLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				//刷新动画
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});
	}

	public void onEvent(EventLocate event)
	{
		mController.updateLocation(event.longitude, event.latitude);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		MenuItem item = menu.findItem(R.id.menu_item_btn);
		item.setTitle("筛选");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);
	}
}
