package com.chenantao.playtogether.mvc.view.activity.invitation;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.chenantao.autolayout.AutoRecyclerView;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.ChatHomeActivity;
import com.chenantao.playtogether.gallery.MyGalleryActivity;
import com.chenantao.playtogether.mvc.controller.invitation.HomeController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.model.bean.event.EventRefreshData;
import com.chenantao.playtogether.mvc.model.bean.event.EventSetAvatar;
import com.chenantao.playtogether.mvc.view.adapter.HomeInvitationItemAdapter;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.DialogUtils;
import com.chenantao.playtogether.utils.FileUtils;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.chenantao.playtogether.utils.PopupWindowManager;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class HomeActivity extends BaseActivity implements View.OnClickListener
{

	private static final int REQUEST_CODE_CAPTURE_CAMERA = 0;
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
	public HomeController mControllr;

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
		View headerView = mNavigationView.getHeaderView(0);
		mIvAvatar = (ImageView) headerView.findViewById(R.id.ivAuthorAvatar);
		TextView tvUsername = (TextView) headerView.findViewById(R.id.tvUsername);
		tvUsername.setText(AVUser.getCurrentUser().getUsername());
		mRvInvitation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
						false));
		initEvent();
		//loadData();
		//显示调用下拉组件
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
			Intent intent = null;

			@Override
			public boolean onNavigationItemSelected(MenuItem item)
			{
				switch (item.getItemId())
				{
					case R.id.home:
						break;
					case R.id.personCenter:
						break;
					case R.id.invite:
						Intent intent = new Intent(HomeActivity.this, PostInvitationActivity
										.class);
						startActivity(intent);
						break;
					case R.id.friend:
						ChatHomeActivity.startActivity(HomeActivity.this, AVUser.getCurrentUser().getUsername
										());
						break;
					case R.id.faq:
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
		Intent intent = null;
		switch (v.getId())
		{
			case R.id.tvFromGallery:
				intent = new Intent(this, MyGalleryActivity.class);
				intent.putExtra(MyGalleryActivity.EXTRA_LIMIT_COUNT, 1);
				intent.putExtra(MyGalleryActivity.EXTRA_SELECT_TYPE, MyGalleryActivity.TYPE_UPLOAD_AVATAR);
				startActivity(intent);
				break;
			case R.id.tvTakePhoto:
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED))
				{
					mCameraFile = new File(FileUtils.createImageFile());
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
					startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
				} else
				{
					Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
									Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.ivAuthorAvatar:
				mPopupWindow = getPopupWindow();
//				mPopupWindow.showAtLocation(mNavigationView, Gravity
//						.CENTER, 0, 0);
				mPopupWindow.showAsDropDown(mIvAvatar, 0, 0);
				toggleLight(true);
				break;
		}
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
	 *
	 * @param event
	 */
	public void onEvent(EventSetAvatar event)
	{
		mPopupWindow.dismiss();
		mControllr.uploadAvatar(event.path.get(0));
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
		mControllr.getNewlyInvitationDatas();

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
			View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_select_avatar,
							mDrawerLayout, false);
			mPopupWindow = PopupWindowManager.getDefaultPopupWindow(view, viewDim);
			mPopupWindow.setAnimationStyle(R.style.select_avatar_popupwindow_anim);
			TextView tvFromGallery = (TextView) view.findViewById(R.id.tvFromGallery);
			TextView tvTakePhoto = (TextView) view.findViewById(R.id.tvTakePhoto);
			tvFromGallery.setOnClickListener(this);
			tvTakePhoto.setOnClickListener(this);
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
				mControllr.uploadAvatar(path);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
