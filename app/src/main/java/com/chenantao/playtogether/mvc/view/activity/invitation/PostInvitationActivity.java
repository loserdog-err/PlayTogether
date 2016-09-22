package com.chenantao.playtogether.mvc.view.activity.invitation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;

import com.chenantao.autolayout.AutoLinearLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.invitation.PostInvitationController;
import com.chenantao.playtogether.mvc.model.bean.event.EventRefreshData;
import com.chenantao.playtogether.mvc.model.bean.event.EventToolboxPopup;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.mvc.view.fragment.invitation.InviteConditionFragment;
import com.chenantao.playtogether.mvc.view.fragment.invitation.WriteMessageFragment;
import com.chenantao.playtogether.utils.DialogUtils;
import com.chenantao.playtogether.utils.ScreenUtils;
import com.gc.materialdesign.views.ButtonFloat;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/20.
 * 发布邀请的activity
 * <p/>
 * 这个界面最主要的逻辑就是对fab的控制
 * fab存在于activity的布局中，在所有fragment中都可以控制它，所以为了fragment与activity的通信，使用eventbus。
 * fab需要响应以下几种情况
 * 1.当fragment中的任一选择框弹起的时候，fab必须跟着弹起，当任一选择框隐藏的时候，fab必须跟着下降。每次操作都会发送
 * 一个event到activity中，然后fab就会执行相应的操作。
 * 2.当滑动tab的时候，tab需要根据当前页面以及当前页面选择框的状态来抬起或者下降fab。
 * 3.为了避免发送多余的event，比如当emoji选择框弹起的时候，在没有隐藏emoji选择框的前提下，弹起选择图片选择框，
 * 这种时候就需要进行判断，从而达到只发送一个event。
 */
public class PostInvitationActivity extends BaseActivity implements View.OnClickListener,
		EmojiconGridFragment
				.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener
{

	public static final int POS_WRITE_TITLE = 0;
	public static final int POS_WRITE_CONTENT = 1;
	public static final int POS_CONDITION = 2;
	@Bind(R.id.tabLayout)
	TabLayout mTabLayout;
	@Bind(R.id.viewPager)
	ViewPager mViewPager;
	@Bind(R.id.btnInvite)
	ButtonFloat mBtnInvite;
	@Bind(R.id.root)
	AutoLinearLayout mRoot;

	//判断布局变化的高度是否超过这个数，超过的话，认为键盘弹起
	int mKeyHeight;


	private List<Fragment> mFragments;
	private List<String> mTitles;

	private boolean mIsFabLift = false;//fab是否被抬起了
	private ViewPropertyAnimator mFabCurrentAnim;

	@Inject
	public PostInvitationController mController;

	public List<Fragment> getFragments()
	{
		return mFragments;
	}

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_post_invitation;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		EventBus.getDefault().register(this);
		mKeyHeight = ScreenUtils.getScreenHeight(this) / 3;
		ActionBar toolbar = getSupportActionBar();
		if (toolbar != null)
		{
			toolbar.setTitle("邀约");
		}
		initViewPager();
		initTabLayout();
		initEvent();

	}

	private void initEvent()
	{
		mBtnInvite.setOnClickListener(this);
	}


	private void initTabLayout()
	{
		mTabLayout.setupWithViewPager(mViewPager);
	}

	private void initViewPager()
	{
		//初始化标题
		mTitles = new ArrayList<>();
		mTitles.add("标题");
		mTitles.add("内容");
		mTitles.add("条件");
		//初始化fragment
		mFragments = new ArrayList<>();
		//写标题的fragment
		Fragment titleFragment = WriteMessageFragment.newInstance();
		Bundle titleBundle = new Bundle();
		titleBundle.putInt(WriteMessageFragment.MESSAGE_TYPE, WriteMessageFragment
				.TYPE_WRITE_TITLE);
		titleFragment.setArguments(titleBundle);
		//写内容的fragment
		Fragment contentFragment = WriteMessageFragment.newInstance();
		Bundle contentBundle = new Bundle();
		contentBundle.putInt(WriteMessageFragment.MESSAGE_TYPE, WriteMessageFragment
				.TYPE_WRITE_CONTENT);
		contentFragment.setArguments(contentBundle);
		//添加到fragment list中
		mFragments.add(POS_WRITE_TITLE, titleFragment);
		mFragments.add(POS_WRITE_CONTENT, contentFragment);
		mFragments.add(POS_CONDITION, InviteConditionFragment.newInstance());
		//设置adapter
		mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public Fragment getItem(int position)
			{
				return mFragments.get(position);
			}

			@Override
			public CharSequence getPageTitle(int position)
			{
				return mTitles.get(position);
			}

			@Override
			public int getCount()
			{
				return mFragments.size();
			}


		});
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int
					positionOffsetPixels)
			{
			}

			@Override
			public void onPageSelected(int position)
			{
				//根据页面设置相应fab的位置
				switch (position)
				{
					case POS_WRITE_TITLE:
					case POS_WRITE_CONTENT:
						if (((WriteMessageFragment) mFragments.get(position)).isSelectBoxShowing())
						{
							liftFab();
						} else
						{
							resetFab();
						}
						break;
					case POS_CONDITION:
						resetFab();
				}
				if (position == POS_CONDITION) resetFab();
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
		//防止fragment被销毁
		mViewPager.setOffscreenPageLimit(2);

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnInvite:
				Snackbar.make(mRoot, "约吗？", Snackbar.LENGTH_LONG).setAction("约", new View
						.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						DialogUtils.showProgressDialog("发布中，请耐心等待", PostInvitationActivity
								.this);
						mController.postInvite();
					}
				}).show();
				break;
		}
	}

	/**
	 * 发布邀请成功
	 */
	public void postInvitationSuccess()
	{
		DialogUtils.dismissProgressDialog();
		Toast.makeText(this, "发布成功!", Toast.LENGTH_SHORT).show();
		EventBus.getDefault().post(new EventRefreshData());
		finish();

	}

	/**
	 * 发布问题失败
	 */
	public void postInvitationFail(String error)
	{
		DialogUtils.dismissProgressDialog();
		Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
	}

	/**
	 * fragment的工具栏弹出或者缩回的回调，需要调整一下 约 按钮的位置
	 *
	 */
	public void onEvent(EventToolboxPopup event)
	{
		if (event.isPopup)
		{
			liftFab();
		} else
		{
			resetFab();
		}
	}

	/**
	 * 为了提升逼格，fab的抬起以及下落来个动画效果
	 */
	public void liftFab()
	{
		if (mFabCurrentAnim != null)
		{
			mFabCurrentAnim.cancel();
		}
		int screenHeight = ScreenUtils.getScreenHeight(this);
		mFabCurrentAnim = mBtnInvite.animate()
				.translationY(-screenHeight / 3)
				.rotation(360)
				.setDuration(400)
				.setListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationCancel(Animator animation)
					{
						mIsFabLift = true;
					}

					@Override
					public void onAnimationEnd(Animator animation)
					{
						mIsFabLift = true;
					}
				});
	}

	public void resetFab()
	{
		if (mFabCurrentAnim != null)
		{
			mFabCurrentAnim.cancel();
		}
		mFabCurrentAnim = mBtnInvite.animate()
				.translationY(0)
				.rotation(-360)
				.setDuration(400)
				.setListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationCancel(Animator animation)
					{
						mIsFabLift = false;
					}

					@Override
					public void onAnimationEnd(Animator animation)
					{
						mIsFabLift = false;
					}
				});
	}

	@Override
	public void onBackPressed()
	{
		/**
		 *  判断当前处于哪个tab,判断一些支持后退键关闭的框是否有打开，有的话就关闭。
		 *  没有的话则询问是否退出编辑
		 */
		int mCurrentPos = mTabLayout.getSelectedTabPosition();
		switch (mCurrentPos)
		{
			case POS_WRITE_TITLE:
			case POS_WRITE_CONTENT:
				WriteMessageFragment fragment = (WriteMessageFragment) mFragments.get(mCurrentPos);
				if (fragment.isSelectBoxShowing())
				{
					fragment.hideEmojiSelectBox(false);
					fragment.hidePicSelectBox(false);
					return;
				} else exitEdit();
			case POS_CONDITION:
				//如果选择兴趣类型的选框可见，隐藏它
				InviteConditionFragment conditionFragment = (InviteConditionFragment) mFragments
						.get(POS_CONDITION);
				if (conditionFragment.isSelectCategoryUIShowing())
				{
					conditionFragment.hideSelectCategoryUI();
					return;
				} else exitEdit();
				break;
			default:
				exitEdit();

		}

	}

	public void exitEdit()
	{
		Snackbar.make(mRoot, "你确定要放弃编辑吗？", Snackbar.LENGTH_SHORT)
				.setAction("确定", new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						finish();
					}
				})
				.show();
	}

	@Override
	public void onEmojiconBackspaceClicked(View view)
	{
//		Logger.e("back click");
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon)
	{
		int currentPos = mTabLayout.getSelectedTabPosition();
		if (currentPos == POS_CONDITION) return;
		WriteMessageFragment fragment = (WriteMessageFragment) mFragments.get(currentPos);
		fragment.setContent(emojicon.getEmoji());
	}

}
