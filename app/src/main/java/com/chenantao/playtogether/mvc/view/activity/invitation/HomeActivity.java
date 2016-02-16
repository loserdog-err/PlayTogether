package com.chenantao.playtogether.mvc.view.activity.invitation;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.chenantao.autolayout.AutoRecyclerView;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatHomeActivity;
import com.chenantao.playtogether.faq.FAQChatActivity;
import com.chenantao.playtogether.gallery.MyGalleryActivity;
import com.chenantao.playtogether.mvc.controller.invitation.HomeController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bean.event.EventRefreshData;
import com.chenantao.playtogether.mvc.model.bean.event.EventSetAvatar;
import com.chenantao.playtogether.mvc.view.activity.user.LoginActivity;
import com.chenantao.playtogether.mvc.view.activity.user.PersonalCenterActivity;
import com.chenantao.playtogether.mvc.view.adapter.HomeInvitationItemAdapter;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.mvc.view.widget.SelectListPopupWindow;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.DialogUtils;
import com.chenantao.playtogether.utils.FileUtils;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.chenantao.playtogether.utils.SpUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class HomeActivity extends BaseActivity implements View.OnClickListener
{

	private static final int REQUEST_CODE_CAPTURE_CAMERA = 0;

	//第一次打开应用时的引导层
	@Bind(R.id.guideView)
	View mGuideView;
	@Bind(R.id.tvLogout)
	TextView mTvLogout;
	@Bind(R.id.tvExit)
	TextView mTvExit;
	@Bind(R.id.viewDim)
	View viewDim;
	@Bind(R.id.rvInvitation)
	AutoRecyclerView mRvInvitation;
	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@Bind(R.id.navigationView)
	NavigationView mNavigationView;
	@Bind(R.id.drawerLayout)
	DrawerLayout mDrawerLayout;
	ImageView mIvAvatar;

	private RecyclerView.Adapter mRvInvitationAdapter;

	private PopupWindow mPopupWindow;

	private File mCameraFile;//拍照上传的图片


	@Inject
	public HomeController mController;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_home;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		ActionBar toolbar = getSupportActionBar();
		if (toolbar != null)
		{
			toolbar.setTitle("首页");
		}
		EventBus.getDefault().register(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
		mNavigationView.setItemIconTintList(null);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			mNavigationView.setElevation(0);
		}
		//设置 headerView 的信息
		View headerView = mNavigationView.getHeaderView(0);
		mIvAvatar = (ImageView) headerView.findViewById(R.id.ivAuthorAvatar);
		TextView tvUsername = (TextView) headerView.findViewById(R.id.tvUsername);
		tvUsername.setText(AVUser.getCurrentUser().getUsername());
		//初始化 recyclerview
		mRvInvitation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
						false));
		initEvent();
		//显式调用下拉组件
		mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
						.OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				mDrawerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mSwipeRefreshLayout.setRefreshing(true);
				loadData();
			}
		});
	}

	private void initEvent()
	{
		//判断是否为第一次登录，如果是，显示引导层
		if (SpUtils.getIntProperty(this, Constant.SP_KEY_IS_FIRST_LOGIN) == -1)
		{
			mGuideView.setVisibility(View.VISIBLE);
			SpUtils.setIntProperty(this, Constant.SP_KEY_IS_FIRST_LOGIN, 1);
			mGuideView.setOnClickListener(this);
		}
		mTvExit.setOnClickListener(this);
		mTvLogout.setOnClickListener(this);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				loadData();
			}
		});
		mNavigationView.setNavigationItemSelectedListener(new NavigationView
						.OnNavigationItemSelectedListener()
		{
			@Override
			public boolean onNavigationItemSelected(MenuItem item)
			{
				Intent intent;
				switch (item.getItemId())
				{
					case R.id.home:
						break;
					case R.id.personCenter:
						PersonalCenterActivity.startActivity(HomeActivity.this, AVUser.getCurrentUser()
										.getObjectId());
						break;
					case R.id.invite:
						intent = new Intent(HomeActivity.this, PostInvitationActivity
										.class);
						startActivity(intent);
						break;
					case R.id.friend:
						ChatHomeActivity.startActivity(HomeActivity.this, AVUser.getCurrentUser().getUsername
										());
						break;
					case R.id.faq:
						intent = new Intent(HomeActivity.this, FAQChatActivity.class);
						startActivity(intent);
						break;
				}
				return true;
			}
		});
		mIvAvatar.setOnClickListener(this);
	}


	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.ivAuthorAvatar:
				mPopupWindow = getPopupWindow();
				mPopupWindow.showAsDropDown(mIvAvatar, 0, 0);
				toggleLight(true);
				break;
			case R.id.guideView:
				mGuideView.setVisibility(View.GONE);
				break;
			case R.id.tvExit:
				exit(false);
				break;
			case R.id.tvLogout:
				//清空 User信息以及退出 IM
				exit(true);
				break;
		}
	}

	/**
	 * 退出应用
	 *
	 * @param needClearUserInfo 是否需要清空用户信息
	 */
	public void exit(final boolean needClearUserInfo)
	{
		String hint = needClearUserInfo ? "确定要清空信息并退出吗？" : "确定要退出应用吗";
		Snackbar.make(mDrawerLayout, hint, Snackbar.LENGTH_LONG)
						.setAction("拜拜", new View.OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												if (needClearUserInfo)
												{
													AVUser.logOut();
													AVIMClient client = AVImClientManager.getInstance().getClient();
													if (client != null)
													{
														client.close(new AVIMClientCallback()
														{
															@Override
															public void done(AVIMClient avimClient, AVIMException e)
															{
																e.printStackTrace();
																Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
																startActivity(intent);
																finish();
																return;
															}
														});
													}
												}
												finish();
											}
										}
						)
						.show();

	}

	/**
	 * 需要刷新数据
	 */

	public void onEvent(EventRefreshData event)
	{
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		loadData();
	}

	/**
	 * 设置头像
	 */
	public void onEvent(EventSetAvatar event)
	{
		mPopupWindow.dismiss();
		mController.uploadAvatar(event.path.get(0));
		DialogUtils.showProgressDialog("上传头像中..", this);
	}

	private void loadData()
	{
		AVFile avatar = AVUser.getCurrentUser(User.class).getAvatar();
		if (avatar != null)
		{
			avatar.getThumbnailUrl(false, 100, 100);
			String url = avatar.getUrl();
			PicassoUtils.displayFitImage(this, Uri.parse(url), mIvAvatar, null);
		}
		mSwipeRefreshLayout.setRefreshing(true);
		List<Invitation> datas = new ArrayList<>();
		mRvInvitation.setAdapter(mRvInvitationAdapter = new HomeInvitationItemAdapter(this,
						datas));
		mController.getNewlyInvitationDatas();

	}

	public void loadDataSuccess(List<Invitation> datas)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		mRvInvitation.setAdapter(mRvInvitationAdapter = new HomeInvitationItemAdapter(this,
						datas));

	}

	public void loadDataError(String msg)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void uploadAvatarSuccess(String path)
	{
		Toast.makeText(this, "上传成功:" + path, Toast.LENGTH_SHORT).show();
		int width = mIvAvatar.getMeasuredWidth();
		int height = mIvAvatar.getMeasuredHeight();
		Picasso.with(this).
						load(new File(path))
						.resize(width, height)
						.placeholder(R.mipmap.pictures_no)
						.into(mIvAvatar);
		DialogUtils.dismissProgressDialog();
	}

	public void uploadAvatarFail(String msg)
	{
		DialogUtils.dismissProgressDialog();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * popupWindow弹出或隐藏时
	 * 使内容区域变亮或变暗
	 */
	private void toggleLight(boolean isOpen)
	{
		if (isOpen)
		{
			viewDim.setVisibility(View.VISIBLE);
		} else
		{
			viewDim.setVisibility(View.GONE);
		}
	}

	private PopupWindow getPopupWindow()
	{
		if (mPopupWindow == null)
		{
			String[] items = new String[]{"相册", "拍照"};
			mPopupWindow = new SelectListPopupWindow(this, "选择头像", items, viewDim);
			((SelectListPopupWindow) mPopupWindow).setOnItemClickListener(new SelectListPopupWindow
							.OnItemClickListener()
			{
				@Override
				public void onClick(String item)
				{
					if (item.equals("相册"))
					{
						Intent intent = new Intent(HomeActivity.this, MyGalleryActivity.class);
						intent.putExtra(MyGalleryActivity.EXTRA_LIMIT_COUNT, 1);
						intent.putExtra(MyGalleryActivity.EXTRA_SELECT_TYPE, MyGalleryActivity
										.TYPE_UPLOAD_AVATAR);
						startActivity(intent);
					} else if (item.equals("拍照"))
					{
						String state = Environment.getExternalStorageState();
						if (state.equals(Environment.MEDIA_MOUNTED))
						{
							mCameraFile = new File(FileUtils.createImageFile());
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
							startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
						} else
						{
							Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
											Toast.LENGTH_LONG).show();
						}
					}
				}
			});

		}
		return mPopupWindow;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (mNavigationView != null) mNavigationView.setCheckedItem(R.id.home);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE_CAPTURE_CAMERA)//拍照完成
		{
			if (resultCode == RESULT_OK)
			{
				mPopupWindow.dismiss();
				String path = mCameraFile.getAbsolutePath();
				Logger.e("path:" + mCameraFile.getAbsolutePath());
				DialogUtils.showProgressDialog("上传图片中", this);
				mController.uploadAvatar(path);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed()
	{
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
		{
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			return;
		}
		exitBy2Click();
	}

	/**
	 *  * 双击退出函数
	 *  
	 */
	private static Boolean isExit = false;

	private void exitBy2Click()
	{
		Timer tExit = null;
		if (!isExit)
		{
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					isExit = false; // 取消退出
				}

			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else
		{
			finish();
		}
	}
}
