package com.chenantao.playtogether.gallery;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.event.EventChatPic;
import com.chenantao.playtogether.mvc.model.bean.event.EventSetAvatar;
import com.chenantao.playtogether.mvc.model.bean.event.EventUploadPic;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MyGalleryActivity extends BaseActivity implements LoaderManager
				.LoaderCallbacks<Cursor>, View.OnClickListener
{

	public static final int SPAN_COUNT = 2;

	//限制选取照片的数量
	public static final int mSelectLimit = 0;

	public static final String EXTRA_LIMIT_COUNT = "limitCount";
	public static final String EXTRA_SELECT_TYPE = "type";//选择照片是要干嘛用的,上传头像，还是发邀请,etc.
	public static final int TYPE_UPLOAD_PIC = 0;
	public static final int TYPE_UPLOAD_AVATAR = 1;
	public static final int TYPE_CHAT = 2;
	public int mType = -1;


	private RecyclerView mRv;
	private List<FolderBean> mFolders;
//	private List<String> mCurrentImgs;

	//底部的控件
	private RelativeLayout mRlBottom;
	private TextView mTvDirName;
	//	private TextView mTvImgCount;
	private ButtonFlat mBtnOk;
	private CheckBox mCbIsSendOriginal;//是否发送原图


	private File mCurrentFolder;
	private DirListPopupWindow mDirListPopupWindow;

	private boolean isPopupWindowOpen = false;//标识popupwindow是否打开

	private ProgressBarCircularIndeterminate mProgressBar;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_my_gallery;
	}

	@Override
	public void injectActivity()
	{
	}

	@Override
	public void afterCreate()
	{
		int limitCount = getIntent().getIntExtra(EXTRA_LIMIT_COUNT, 0);
		mType = getIntent().getIntExtra(EXTRA_SELECT_TYPE, -1);
		if (mType == -1) finish();
		mCbIsSendOriginal = (CheckBox) findViewById(R.id.cbIsOriginal);
		mRv = (RecyclerView) findViewById(R.id.rv);
		mRlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
		mTvDirName = (TextView) findViewById(R.id.tvDirName);
		mCbIsSendOriginal = (CheckBox) findViewById(R.id.cbIsOriginal);
//		mTvImgCount = (TextView) findViewById(R.id.tvImgCount);
		mBtnOk = (ButtonFlat) findViewById(R.id.btnOk);
		if (mType == TYPE_CHAT) mCbIsSendOriginal.setVisibility(View.VISIBLE);
		MyGalleryAdapter.mLimitCount = limitCount;
		mProgressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		getSupportLoaderManager().initLoader(1, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
		return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
						null,
						MediaStore.Images.Media.DATE_MODIFIED);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor)
	{
		//读取数据有点缓慢，开启一条线程去加载
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				mFolders = new ArrayList<>();
				if (cursor.moveToFirst())
				{
					List<String> dirNames = new ArrayList<>();
					do
					{
						if (cursor.isClosed()) return;
						String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images
										.Media
										.DATA));
						File parentFile = new File(path).getParentFile();
						if (parentFile != null && parentFile.list() != null)
						{
							mCurrentFolder = parentFile;
							if (dirNames.contains(mCurrentFolder.getName()))
							{
								continue;
							} else
							{
//						Logger.e("add folder");
								dirNames.add(mCurrentFolder.getName());
								FolderBean folderBean = new FolderBean(path, parentFile
												.getAbsolutePath(), getSubfiles(mCurrentFolder).size());
								mFolders.add(folderBean);
							}
						} else
						{
							continue;
						}
					}
					while (cursor.moveToNext());
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							data2View();
							mProgressBar.setVisibility(View.GONE);
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
	}

	private void data2View()
	{
		List<String> subFiles = getSubfiles(mCurrentFolder);
		mRv.setAdapter(new MyGalleryAdapter(this, subFiles, mCurrentFolder
						.getAbsolutePath()));
		mRv.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT,
						StaggeredGridLayoutManager
										.VERTICAL));
		mTvDirName.setText(mCurrentFolder.getName());
//		mTvImgCount.setText(subFiles.size() + "张");
		initPopupWindow();
		initEvent();
	}

	/**
	 * 使内容区域变亮或变暗
	 */

	private void lightOff()
	{
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = isPopupWindowOpen ? 0.3f : 1.0f;
		getWindow().setAttributes(lp);
	}

	private void initPopupWindow()
	{
		mDirListPopupWindow = new DirListPopupWindow(this, mFolders);
		mDirListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				//当popupwindow关闭的时候，使内容区域变暗
				isPopupWindowOpen = false;
				lightOff();
			}
		});
		//设置popupwindow的回调
		mDirListPopupWindow.setOnFolderSelectedListener(new DirListPopupWindow
						.OnFolderSelectedListener()
		{

			public void onSelected(FolderBean folderBean)
			{
				//如果点击的是当前的文件夹，直接返回
				if (mCurrentFolder.getName().equals(folderBean.getName()))
				{
					mDirListPopupWindow.dismiss();
					return;
				}
				mCurrentFolder = new File(folderBean.getDir());
//				mTvImgCount.setText(folderBean.getImageCount() + "张");
				mTvDirName.setText(folderBean.getName());
				mRv.setAdapter(new MyGalleryAdapter(MyGalleryActivity.this, getSubfiles
								(mCurrentFolder),
								folderBean.getDir()));
				mRv.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT,
								StaggeredGridLayoutManager
												.VERTICAL));
//				if (mLayoutManger != null) mLayoutManger.scrollToPosition(0);
				mDirListPopupWindow.dismiss();
			}
		});
	}

	private List<String> getSubfiles(File folder)
	{
		return Arrays.asList(mCurrentFolder.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
								|| filename.endsWith(".jpeg"))
				{
					return true;
				}
				return false;
			}
		}));
	}

	private void initEvent()
	{
		//初始化底部单击事件
		mRlBottom.setOnClickListener(this);
		mBtnOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rlBottom:
				if (mDirListPopupWindow != null)
				{
					mDirListPopupWindow.setAnimationStyle(R.style.selectPopupWindowAnim);
					mDirListPopupWindow.showAsDropDown(mRlBottom, 0, 0);
					isPopupWindowOpen = true;
					lightOff();

				} else
				{
					Toast.makeText(MyGalleryActivity.this, "骚等下,还没加载完毕..", Toast.LENGTH_SHORT)
									.show();
				}
				break;
			case R.id.btnOk:
				if (mType == TYPE_UPLOAD_AVATAR)//当前为上传头像
				{
					EventBus.getDefault().post(new EventSetAvatar(MyGalleryAdapter.mSelectedImgs));
					MyGalleryAdapter.mSelectedImgs = new ArrayList<>();
				} else if (mType == TYPE_UPLOAD_PIC)//当前为上传图片
				{
					EventBus.getDefault().post(new EventUploadPic(MyGalleryAdapter.mSelectedImgs));
					MyGalleryAdapter.mSelectedImgs = new ArrayList<>();
				} else if (mType == TYPE_CHAT)//发送聊天图片
				{
					EventBus.getDefault().post(new EventChatPic(MyGalleryAdapter.mSelectedImgs,
									mCbIsSendOriginal.isCheck()));
					MyGalleryAdapter.mSelectedImgs = new ArrayList<>();
				}
				finish();
				break;
		}
	}
}


