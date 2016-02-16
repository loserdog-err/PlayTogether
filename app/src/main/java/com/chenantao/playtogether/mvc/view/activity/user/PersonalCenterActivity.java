package com.chenantao.playtogether.mvc.view.activity.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.chenantao.autolayout.AutoFrameLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.user.PersonalCenterController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.mvc.view.common.ShowImageActivity;
import com.chenantao.playtogether.mvc.view.fragment.user.PersonalCenterDetailFragment;
import com.chenantao.playtogether.mvc.view.fragment.user.PersonalCenterHomeFragment;
import com.chenantao.playtogether.mvc.view.widget.SplashView;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/9.
 * 个人中心
 */
public class PersonalCenterActivity extends BaseActivity
{
	public static final int POS_PERSONAL_HOME = 0;
	public static final int POS_PERSONAL_DETAIL = 1;

	@Bind(R.id.viewPager)
	ViewPager mViewPager;
	@Bind(R.id.ivAvatar)
	RoundedImageView mIvAvatar;
	@Bind(R.id.tvUsername)
	TextView mTvUsername;
	@Bind(R.id.toolbar)
	Toolbar mToolbar;
	@Bind(R.id.tabLayout)
	TabLayout mTabLayout;
	@Bind(R.id.collapsingToolbarLayout)
	CollapsingToolbarLayout mCollapsingToolbarLayout;
	@Bind(R.id.appBarLayout)
	AppBarLayout mAppBarLayout;
	@Bind(R.id.coordinatorLayout)
	CoordinatorLayout mCoordinatorLayout;
	@Bind(R.id.mFlRoot)
	AutoFrameLayout mMFlRoot;
	@Bind(R.id.splashView)
	SplashView mSplashView;

	private User mCurrentUser;//当前页面所展示的user

	public static final String EXTRA_USER_ID = "userId";

	@Inject
	PersonalCenterController mController;


	private ArrayList<Fragment> mFragments;

	public static void startActivity(Context context, String userId)
	{
		Intent intent = new Intent(context, PersonalCenterActivity.class);
		intent.putExtra(EXTRA_USER_ID, userId);
		context.startActivity(intent);
	}

	@Override
	public int getLayoutId()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
							| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);
		}
		return R.layout.activity_personal_center;
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
		if (actionBar != null) actionBar.setTitle("");
		String userId = getIntent().getStringExtra(EXTRA_USER_ID);
		if (userId == null)
		{
			Toast.makeText(this, "无效id", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		//初始化用户信息
		mController.getUserInfo(userId);
	}

	private void initViewPagerAndTab()
	{
		final List<String> mTitles = new ArrayList<>();
		mTitles.add("个人主页");
		mTitles.add("详细信息");
		//初始化fragment
		mFragments = new ArrayList<>();
		//个人中心首页
		Fragment personalHome = PersonalCenterHomeFragment.newInstance(mCurrentUser);
		//个人详情页
		Fragment personalDetail = PersonalCenterDetailFragment.newInstance(mCurrentUser);
		mFragments.add(POS_PERSONAL_HOME, personalHome);
		mFragments.add(POS_PERSONAL_DETAIL, personalDetail);
		mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public Fragment getItem(int position)
			{
				return mFragments.get(position);
			}

			@Override
			public int getCount()
			{
				return mFragments.size();
			}

			@Override
			public CharSequence getPageTitle(int position)
			{
				return mTitles.get(position);
			}
		});
		mTabLayout.setupWithViewPager(mViewPager);
	}

	public void getUserInfoSuccess(User user)
	{
		mCurrentUser = user;
		mTvUsername.setText(mCurrentUser.getUsername());
		final String avatarUrl = user.getAvatarUrl();
		if (avatarUrl != null)
		{
			PicassoUtils.displayFitImage(this, Uri.parse(avatarUrl), mIvAvatar, null);
		}
		final AVFile avFile = user.getAvatar();
		mIvAvatar.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (avFile != null)
				{
					int width = (int) (avFile.getMetaData("width") == null ? 500 : avFile.getMetaData
									("width"));
					int height = (int) (avFile.getMetaData("height") == null ? 500 : avFile.getMetaData
									("height"));
					Intent intent = new Intent(PersonalCenterActivity.this, ShowImageActivity.class);
					intent.putExtra(ShowImageActivity.EXTRA_HEIGHT, height);
					intent.putExtra(ShowImageActivity.EXTRA_WIDTH, width);
					intent.putExtra(ShowImageActivity.EXTRA_URI, Uri.parse(avFile.getUrl()));
					startActivity(intent);
				}
			}
		});
		initViewPagerAndTab();
		mSplashView.loadFinish();
	}

	public void getUserInfoFail(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
