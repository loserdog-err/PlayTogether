package com.chenantao.playtogether.chat.mvc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.handler.ChatHomeHandler;
import com.chenantao.playtogether.chat.mvc.view.fragment.ConvListFragment;
import com.chenantao.playtogether.chat.mvc.view.fragment.FriendListFragment;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.chenantao.playtogether.utils.PopupWindowManager;
import com.chenantao.playtogether.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/1/29.
 * <p/>
 * * 聊天的首页，即平常我们看到的qq的那一排排的跟好友的会话信息
 * 这个界面需要先检查用户是已经登录，这里的登录跟结伴而行这个app的登录是不一样的，
 * 即，你即使已经登录了app，你想要聊天，还得登录聊天这个系统，不过为了方便，一般都会
 * 使用结伴而行app的登录用户名作为聊天的登录用户名
 * <p/>
 * 注意，我把这个聊天功能跟本app分离出来，以方便其他app直接使用。你只需要提供
 * 给这个聊天功能一个登录系统就可以了，这里我使用的是我这个app的登录系统
 * <p/>
 * <p/>
 * <p/>
 * 我这里接入我这个app的登录系统，有需要实现自己登录系统的，也是可以的，
 * 因为，你只需要提供一个用户名，你就可以在这个聊天系统中玩耍了，但是，
 * 你想要其他功能，例如头像，就需要一个比较完善的用户系统了。
 * <p/>
 * 这里我定义了一个bll接口，需要使用这个聊天功能的，只需要实现这个接口，同时传递一个用户名过来就可以了。
 */
public class ChatHomeActivity extends BaseActivity implements View.OnClickListener
{

	public static int POS_CONV_LIST_FRAGMENT = 0;
	public static int POS_FRIEND_LIST_FRAGMENT = 1;

	@Bind(R.id.tvUsername)
	TextView mTvUsername;
	@Bind(R.id.ivAvatar)
	ImageView mIvAvatar;
	@Bind(R.id.btnMenu)
	FloatingActionButton mBtnMenu;
	@Bind(R.id.mFlRoot)
	FrameLayout mFlRoot;
	@Bind(R.id.viewDim)
	View mViewDim;//主要是一个黑色透明的view，制造一个关灯的效果
	@Bind(R.id.collapsingToolbarLayout)
	CollapsingToolbarLayout mCollapsingToolbarLayout;
	RelativeLayout mRlAddFriend;
	RelativeLayout mRlCreateDiscussGroup;
	String mName;

	@Bind(R.id.tabLayout)
	TabLayout mTabLayout;
	@Bind(R.id.viewPager)
	ViewPager mViewPager;


	PopupWindow mPopupWindow;

	ChatHomeHandler mHandler;
	private ArrayList<Fragment> mFragments;

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
		return R.layout.activity_chat_home;
	}

	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	public static void startActivity(Context context, String username)
	{
		Intent intent = new Intent(context, ChatHomeActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void afterCreate()
	{
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.setTitle("");
		User me = AVUser.getCurrentUser(User.class);
		if (me == null)
		{
			Toast.makeText(this, "请先进行登录,", Toast.LENGTH_SHORT).show();
			finish();
		}
		//初始化用户信息
		mTvUsername.setText(me.getUsername());
		AVFile avAvatar = me.getAvatar();
		if (avAvatar != null)
			PicassoUtils.displayFitImage(this, Uri.parse(avAvatar.getThumbnailUrl(true, Constant
							.AVATAR_WIDTH, Constant.AVATAR_HEIGHT)), mIvAvatar, null);
		//初始化事件
		initEvent();
		//初始化viewpager
		initViewPagerAndTabLayout();
	}

	private void initViewPagerAndTabLayout()
	{
		final List<String> mTitles = new ArrayList<>();
		mTitles.add("信息");
		mTitles.add("好友");
		//初始化fragment
		mFragments = new ArrayList<>();
		//会话的fragment
		Fragment convListFragment = ConvListFragment.newInstance();
		//好友fragment
		Fragment friendListFragment = FriendListFragment.newInstance();
		mFragments.add(POS_CONV_LIST_FRAGMENT, convListFragment);
		mFragments.add(POS_FRIEND_LIST_FRAGMENT, friendListFragment);
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

	private void initEvent()
	{
		mBtnMenu.setOnClickListener(this);
	}

	/**
	 * 菜单 popupWindow
	 */
	private void initPopupWindow()
	{
		View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_chat_home_menu, mFlRoot,
						false);
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
						View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mRlAddFriend = (RelativeLayout) view.findViewById(R.id.rlAddFriend);
		mRlCreateDiscussGroup = (RelativeLayout) view.findViewById(R.id.rlCreateDiscussGroup);
		mRlAddFriend.setOnClickListener(this);
		mRlCreateDiscussGroup.setOnClickListener(this);
		mPopupWindow = PopupWindowManager.getDefaultPopupWindow(view, mViewDim, new PopupWindowManager
						.onCloseListener()
		{
			@Override
			public void onClose()
			{
				//提升逼格,给 fab 弄一个动画效果
				mBtnMenu.animate()
								.rotation(0)
								.setDuration(200);
			}
		});
		mPopupWindow.setAnimationStyle(R.style.chat_home_menu_popupwindow_anim);
	}

	/**
	 * 由于种种原因，fab 的高度要手动调整
	 *
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		float marginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources()
						.getDisplayMetrics()) + ScreenUtils.getVirtualBarHeight(this);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBtnMenu.getLayoutParams();
		params.bottomMargin = (int) marginBottom;
		mBtnMenu.setLayoutParams(params);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (mHandler == null)
		{
			mHandler = new ChatHomeHandler();
		}
		AVIMMessageManager.registerMessageHandler(AVIMMessage.class, mHandler);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, mHandler);
	}

	@Override
	public void onClick(View v)
	{
		Intent intent;
		switch (v.getId())
		{
			case R.id.btnMenu:
				if (mPopupWindow == null)
					initPopupWindow();
				mBtnMenu.animate()
								.rotation(135)
								.setDuration(200);
				int width = mPopupWindow.getContentView().getMeasuredWidth();
				int height = mPopupWindow.getContentView().getMeasuredHeight();
				int offsetHeight = height + mBtnMenu.getMeasuredHeight();
				int offsetWidth = width - mBtnMenu.getMeasuredWidth();
				mPopupWindow.showAsDropDown(mBtnMenu, -offsetWidth, -offsetHeight - 10);
				PopupWindowManager.toggleLight(true, mViewDim);
				break;
			case R.id.rlCreateDiscussGroup:
				if (FriendListFragment.friendList == null)
				{
					Toast.makeText(this, "骚等下，好友列表还在加载中..", Toast.LENGTH_SHORT).show();
					return;
				}
				intent = new Intent(this, CreateDiscussGroupActivity.class);
				startActivity(intent);
				mPopupWindow.dismiss();
				break;
			case R.id.rlAddFriend:
				intent = new Intent(this, AddFriendActivity.class);
				startActivity(intent);
				mPopupWindow.dismiss();
				break;
		}
	}
}
