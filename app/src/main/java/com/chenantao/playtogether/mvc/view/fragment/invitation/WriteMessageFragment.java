package com.chenantao.playtogether.mvc.view.fragment.invitation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenantao.autolayout.AutoFrameLayout;
import com.chenantao.autolayout.AutoLinearLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.event.EventUploadPic;
import com.chenantao.playtogether.mvc.model.bean.event.EventToolboxPopup;
import com.chenantao.playtogether.mvc.view.adapter.SelectPicAdapter;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/1/21.
 * 这个界面比逻辑判断比较乱，简单记录一下。
 * 1.这是一个输入信息的fragment，所以首先得判断当前是输入什么玩意，
 * 如果是输入标题，则需要隐藏上传图片栏，如果是输入内容，则需要显示上传标题栏
 * 2.当用户选择emoji以及上传图片栏的时候，需要隐藏键盘。
 * 3.当选择某个(emoji、上传图片)栏时，需要隐藏另一个栏。
 * 4.当键盘弹起的时候，需要隐藏所有栏，监听键盘弹起并且设置隐藏会引起错位，
 * 所以改用文本框的点击事件时，隐藏所有栏，暴力解决方法。
 * 5.可能由于设置了第一次进入时文本框不允许获得焦点，所以第一次点击事件会失效，
 * 所以需要和setFocusChangeListener一起使用，当得到焦点时隐藏。
 * 6.为什么不能只用setFocusChangeListener
 * 就好呢？因为当你文本框获得焦点的时候，点击emoji栏或者图片上传栏并不会使文本框失去焦点，
 * 所以，当你继续点击任意栏的时候，并不会触发焦点改变的方法。
 * 7.关于后退键隐藏的逻辑，放到宿主activity中处理
 */
public class WriteMessageFragment extends Fragment implements
		CompoundButton.OnCheckedChangeListener
{
	//为了复用，输入标题以及内容都由这个fragment提供
	public static final int TYPE_WRITE_TITLE = 0;
	public static final int TYPE_WRITE_CONTENT = 1;

	public static final String MESSAGE_TYPE = "messageType";

	private int mType = TYPE_WRITE_TITLE;

	private View mRoot;

	@Bind(R.id.etMessage)
	EditText mEtMessage;

	//底边工具栏需要的
	@Bind(R.id.cbSelectEmoji)
	CheckBox mCbSelectEmoji;
	@Bind(R.id.cbSelectPic)
	CheckBox mCbSelectPic;
	@Bind(R.id.messageToolBox)
	AutoLinearLayout mMessageToolBox;
	@Bind(R.id.flEmojicons)
	AutoFrameLayout mFlEmojicons;
	@Bind(R.id.rlSelectPic)
	RelativeLayout mRlSelectPic;
	@Bind(R.id.tvSelectNumHint)
	TextView mTvSelectNumHint;
	//上传图片栏的recyclerview四件套
	@Bind(R.id.rvSelectPic)
	RecyclerView mRvSelectPic;
	private RecyclerView.LayoutManager mHorizLayoutManager;
	private List<String> mPathDatas;
	private SelectPicAdapter mAdapter;


	public static WriteMessageFragment newInstance()
	{
		return new WriteMessageFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState)
	{
		mRoot = inflater.inflate(R.layout.fragment_write_message, container, false);
		ButterKnife.bind(this, mRoot);
		return mRoot;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		Bundle bundle = getArguments();
		if (bundle != null)
		{
			mType = getArguments().getInt(MESSAGE_TYPE, TYPE_WRITE_TITLE);
		}
		mEtMessage.setHint(mType == TYPE_WRITE_CONTENT ? "写下你的内容..." : "写下你的标题...");
		//如果是输入标题页面，隐藏选择图片
		if (mType == TYPE_WRITE_TITLE)
		{
			mCbSelectPic.setVisibility(View.GONE);

		} else//如果是输入内容页面，显示pic box，并且初始化recyclerview
		{
			EventBus.getDefault().register(this);
			mCbSelectPic.setVisibility(View.VISIBLE);
			mPathDatas = new ArrayList<>();
			mRvSelectPic.setLayoutManager(mHorizLayoutManager = new LinearLayoutManager
					(getActivity(), LinearLayoutManager.HORIZONTAL, false));
			mRvSelectPic.setAdapter(mAdapter = new SelectPicAdapter(getActivity(), mPathDatas));
		}
		initEvent();
	}

	private void initEvent()
	{
		mCbSelectEmoji.setOnCheckedChangeListener(this);
		mCbSelectPic.setOnCheckedChangeListener(this);
		//点击输入框时隐藏选择框
		mEtMessage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//监听键盘弹起隐藏选择框不起作用，我也不知道为什么，只能用这种暴力处理方法了。
				hideEmojiSelectBox(false);
				hidePicSelectBox(false);
			}
		});
		//可能是设置了进入的时候不允许获得焦点，第一次单击事件不会触发，所以改用这个
		mEtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{
					hideEmojiSelectBox(false);
					hidePicSelectBox(false);
				}
			}
		});
	}


	/**
	 * 因为我使用的emoji库需要实现一个回调，但是居然不能在fragment设置回调，
	 * 真的是好想吃了一口屎，所以需要在依赖的activity设置回调，这个方法，
	 * 主要是供activity设置文本用的。
	 */
	public void setContent(String content)
	{
		String currentContent = mEtMessage.getText().toString();
		mEtMessage.setText(currentContent + content);
		mEtMessage.setSelection(mEtMessage.getText().toString().length());
	}


	/**
	 * 选择图片以及emoji表情实际上是两个checkbox，在这个方法处理之
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch (buttonView.getId())
		{
			case R.id.cbSelectEmoji:
				if (isChecked)//打开emoji选框，同时隐藏键盘
				{
					showEmojiSelectBox();
				} else//隐藏emoji选框
				{
					hideEmojiSelectBox(false);
				}
				break;
			case R.id.cbSelectPic:
				if (isChecked)//打开图片选框，同时隐藏键盘
				{
					showPicSelectBox();

				} else//隐藏图片选框
				{
					hidePicSelectBox(false);
				}
				break;
		}
	}


	// 选择相片后的回调
	public void onEvent(EventUploadPic event)
	{
		if (event.paths.size() > 10)
		{
			mPathDatas = new ArrayList<>();
			List<String> tempList = event.paths.subList(0, Constant.MAX_UPLOAD_PIC);
			for (int i = 0; i < 10; i++)
			{
				mPathDatas.add(tempList.get(i));
			}
		} else
		{
			mPathDatas = event.paths;
		}
		mRvSelectPic.setAdapter(mAdapter = new SelectPicAdapter(getActivity(), mPathDatas));
		int selectedNum = mPathDatas.size();
		int remainSelect = Constant.MAX_UPLOAD_PIC - selectedNum;
		mTvSelectNumHint.setText(getResources().getString(R.string.select_num_hint,
				selectedNum, remainSelect));
	}

	/**
	 * 显示emoji选择框
	 */
	public void showEmojiSelectBox()
	{
		hidePicSelectBox(true);
		EventBus.getDefault().post(new EventToolboxPopup(true));
		mFlEmojicons.setVisibility(View.VISIBLE);
		ScreenUtils.hideKeyboard(getActivity());
	}

	/**
	 * 显示图片选择框
	 */
	public void showPicSelectBox()
	{
		hideEmojiSelectBox(true);
		EventBus.getDefault().post(new EventToolboxPopup(true));
		int selectedNum = mPathDatas.size();
		int remainSelect = Constant.MAX_UPLOAD_PIC - selectedNum;
		mTvSelectNumHint.setText(getResources().getString(R.string.select_num_hint,
				selectedNum, remainSelect));
		mRlSelectPic.setVisibility(View.VISIBLE);
		ScreenUtils.hideKeyboard(getActivity());
	}


	/**
	 * 隐藏emoji选择框
	 *
	 * @param goon 为了防止发送无用的event，判断一下hide之后是否还要show另一个弹出框
	 *             举一个例子：比如我在显示pic box之前需要hide一下emoji box，那么hide
	 *             emoji这个方法是不用发送event的。
	 */
	public void hideEmojiSelectBox(boolean goon)
	{
		if (!goon)
		{
			EventBus.getDefault().post(new EventToolboxPopup(false));
		}
		mFlEmojicons.setVisibility(View.GONE);
		if (mCbSelectEmoji.isChecked())
			mCbSelectEmoji.setChecked(false);
		//// TODO: 2016/1/24 这里的setChecked会导致触发checkListener方法，会再次调用隐藏
	}

	/**
	 * 隐藏图片选择框
	 */
	public void hidePicSelectBox(boolean goon)
	{
		if (!goon)
		{
			EventBus.getDefault().post(new EventToolboxPopup(false));
		}
		mRlSelectPic.setVisibility(View.GONE);
		if (mCbSelectPic.isChecked())
			mCbSelectPic.setChecked(false);
	}

	/**
	 * 得到输入的内容
	 *
	 * @return
	 */
	public String getContent()
	{
		return mEtMessage.getText().toString();
	}

	public List<String> getPic()
	{
		return mPathDatas;
	}

	public boolean isSelectBoxShowing()
	{
		return mCbSelectEmoji.isChecked() || mCbSelectPic.isChecked();
	}

	public boolean isSelectPicBoxShowing()
	{
		return mCbSelectPic.isChecked();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
}
