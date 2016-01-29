package com.chenantao.playtogether.mvc.view.activity.invitation;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.invitation.InvitationCategoryController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.InvitationCondition;
import com.chenantao.playtogether.mvc.model.bean.event.EventLocate;
import com.chenantao.playtogether.mvc.view.adapter.InvitationCategoryAdapter;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.PopupWindowManager;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/26.
 */
public class InvitationCategoryActivity extends BaseActivity implements View.OnClickListener,
		CompoundButton.OnCheckedChangeListener
{

	public static final String EXTRA_CATEGORY = "category";

	public int mCategory;
	@Bind(R.id.appBarLayout)
	AppBarLayout mAppBarLayout;
	@Bind(R.id.viewDim)
	View viewDim;
	@Bind(R.id.collapsingToolbarLayout)
	CollapsingToolbarLayout mCollapsingToolbarLayout;
	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@Bind(R.id.coordinatorLayout)
	CoordinatorLayout mCoordinatorLayout;
	@Bind(R.id.toolbar)
	Toolbar mToolBar;
	@Bind(R.id.view_header)
	View mHeader;
	//recyclerview 四件套
	@Bind(R.id.rvInvitation)
	RecyclerView mRvInvitation;
	LinearLayoutManager mLayoutManager;
	List<Invitation> mDatas;
	InvitationCategoryAdapter mAdapter;

	//popupwindow的控件
	TextView mEtMinAge;
	TextView mEtMaxAge;
	android.widget.CheckBox mCbNearest;
	android.widget.CheckBox mCbNewly;
	CheckBox mCbMan;
	CheckBox mCbWomen;
	private PopupWindow mPopupWindow;//筛选弹出框

	private InvitationCondition mCondition;


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
		mCategory = getIntent().getIntExtra(EXTRA_CATEGORY, Constant.CATEGORY_FOOD);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
			setHeader();
		}
		EventBus.getDefault().register(this);
		//初始化SwipeRefreshLayout
		mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
		initEvent();
		//初始化recyclerview
		mRvInvitation.setLayoutManager(mLayoutManager = new LinearLayoutManager(this,
				LinearLayoutManager.VERTICAL, false));
		//加载数据并显示加载框
		InvitationCondition condition = new InvitationCondition();
		condition.setCategory(mCategory);
		mCondition = condition;
		showProgress();
		mController.loadData(condition);
	}

	/**
	 * 根据类型设置头部，图片，文字等
	 */
	private void setHeader()
	{
		if (mCategory == Constant.CATEGORY_EXERCISE)
		{
			mCollapsingToolbarLayout.setTitle("运动");
			mHeader.setBackgroundResource(R.mipmap.exerise_header);
		} else if (mCategory == Constant.CATEGORY_FOOD)
		{
			mCollapsingToolbarLayout.setTitle("美食");
			mHeader.setBackgroundResource(R.mipmap.food_header);
		} else if (mCategory == Constant.CATEGORY_MOVIE)
		{
			mCollapsingToolbarLayout.setTitle("电影");
			mHeader.setBackgroundResource(R.mipmap.movie_header);
		}

	}


	private void initEvent()
	{
		//监听recyclerview滑动到底部时加载更多
		mRvInvitation.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				if (isLastItemDisplaying())
				{
					if (mCondition != null)
					{
						mCondition.setSkip(mDatas.size());
						mController.loadData(mCondition);
						showProgress();
					}
				}
			}
		});
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				if (mCondition != null)
				{
					mController.loadData(mCondition);
				} else mSwipeRefreshLayout.setRefreshing(false);

			}
		});
	}

	public void addDataSuccess(List<Invitation> invitations)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		int count = invitations.size();
		if (count == 0)
		{
			return;
		}
//		int startIndex = mDatas.size()+1;
		Logger.e("size:" + count);
		for (int i = 0; i < count; i++)
		{
			mDatas.add(invitations.get(i));
		}
		mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), count);
	}


	public void refreshDataSuccess(List<Invitation> invitations)
	{
		mDatas = invitations;
		mSwipeRefreshLayout.setRefreshing(false);
		mRvInvitation.setAdapter(mAdapter = new InvitationCategoryAdapter(this, invitations));
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

	/**
	 * 更新用户位置
	 *
	 * @param event
	 */
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
		switch (item.getItemId())
		{
			case R.id.menu_item_btn:
				if (mPopupWindow == null)
				{
					/**
					 * 性别可以全选
					 * 年龄会过滤，小于10或者大于99都会进行过滤
					 * 排序只能选择一个
					 */
					View view = LayoutInflater.from(this).inflate(R.layout
									.popupwindow_invitation_filter,
							mCoordinatorLayout, false);
					//find view
					mEtMinAge = (TextView) view.findViewById(R.id.etMinAge);
					mEtMaxAge = (TextView) view.findViewById(R.id.etMaxAge);
					mCbNearest = (android.widget.CheckBox) view.findViewById(R.id.cbNearest);
					mCbNewly = (android.widget.CheckBox) view.findViewById(R.id.cbNewly);
					mCbMan = (CheckBox) view.findViewById(R.id.cbMan);
					mCbWomen = (CheckBox) view.findViewById(R.id.cbWomen);
					ButtonFlat btnReset = (ButtonFlat) view.findViewById(R.id.btnReset);
					ButtonFlat btnOk = (ButtonFlat) view.findViewById(R.id.btnOk);
					mPopupWindow = PopupWindowManager.getDefaultPopupWindow(view, viewDim);
					mPopupWindow.setAnimationStyle(R.style.category_filter_popupwindow_anim);
					//init event
					btnReset.setOnClickListener(this);
					btnOk.setOnClickListener(this);
					mCbNearest.setOnCheckedChangeListener(this);
					mCbNewly.setOnCheckedChangeListener(this);

				}
				mPopupWindow.showAsDropDown(mToolBar,
						0, 0);
				PopupWindowManager.toggleLight(true, viewDim);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnReset:
				resetPopupwindow();
				break;
			case R.id.btnOk:
				InvitationCondition condition = new InvitationCondition();
				//性别选择
				if (mCbMan.isCheck() && !mCbWomen.isCheck())
				{
					condition.setGender(Constant.GENDER_MAN);
				} else if (mCbWomen.isCheck() && !mCbMan.isCheck())
				{
					condition.setGender(Constant.GENDER_WOMEN);
				} else
				{
					condition.setGender(Constant.GENDER_ALL);
				}
				//年龄选择
				String strMinAge = mEtMinAge.getText().toString();
				String strMaxAge = mEtMaxAge.getText().toString();
				if (!"".equals(strMinAge))
				{
					condition.setMinAge(Integer.parseInt(strMinAge));
				}
				if (!"".equals(strMaxAge))
				{
					condition.setMaxAge(Integer.parseInt(strMaxAge));

				}
				//排序选择
				if (mCbNearest.isChecked())
				{
					condition.setOrderBy(InvitationCondition.OrderBy.NEAREST);
				} else if (mCbNewly.isChecked())
				{
					condition.setOrderBy(InvitationCondition.OrderBy.NEWLY);
				}
				condition.setCategory(mCategory);
				mCondition = condition;
				mController.loadData(condition);
				showProgress();
				mPopupWindow.dismiss();
				resetPopupwindow();
				break;
		}
	}

	public void resetPopupwindow()
	{
		mEtMinAge.setText("");
		mEtMaxAge.setText("");
		mCbNewly.setChecked(false);
		mCbNearest.setChecked(false);
		mCbMan.setChecked(false);
		mCbWomen.setChecked(false);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch (buttonView.getId())
		{
			case R.id.cbNearest:
				if (isChecked) mCbNewly.setChecked(false);
				break;
			case R.id.cbNewly:
				if (isChecked) mCbNearest.setChecked(false);
				break;
		}
	}

	private boolean isLastItemDisplaying()
	{
		if (mRvInvitation != null && mRvInvitation.getAdapter() != null)
		{
			if (mRvInvitation.getAdapter().getItemCount() != 0)
			{
				int lastVisibleItemPosition = ((LinearLayoutManager) mRvInvitation
						.getLayoutManager())
						.findLastCompletelyVisibleItemPosition();
				if (lastVisibleItemPosition != RecyclerView.NO_POSITION &&
						lastVisibleItemPosition ==
								mRvInvitation.getAdapter().getItemCount() - 1)
					return true;
			}
		}
		return false;
	}

}