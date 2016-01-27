package com.chenantao.playtogether.mvc.view.fragment.invitation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chenantao.autolayout.AutoLinearLayout;
import com.chenantao.autolayout.AutoRelativeLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.InterestCategory;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.view.common.BaseFragment;
import com.chenantao.playtogether.mvc.view.adapter.SelectRecyclerViewAdapter;
import com.chenantao.playtogether.mvc.view.widget.SelectRecyclerView;
import com.chenantao.playtogether.mvc.view.widget.SnappingLinearLayoutManager;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.PopupWindowManager;
import com.chenantao.playtogether.utils.ScreenUtils;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
public class InviteConditionFragment extends BaseFragment implements CheckBox.OnCheckListener
{
	public static final int TYPE_CONSTELLATION = 0;
	public static final int TYPE_EXPIRE = 1;
	public static final int TYPE_SELECT_CATEGORY = 2;
	//选择性别的单选框
	@Bind(R.id.cbWomen)
	CheckBox mCbWomen;
	@Bind(R.id.cbMan)
	CheckBox mCbMan;

	//输入年龄的输入框
	@Bind(R.id.etMaxAge)
	AppCompatEditText mEtMaxAge;
	@Bind(R.id.tvTo)
	TextView mTvTo;
	@Bind(R.id.etMinAge)
	AppCompatEditText mEtMinAge;

	//显示星座，过期时间，兴趣类别的textview
	@Bind(R.id.tvConstellation)
	TextView mTvConstellation;
	@Bind(R.id.tvExpire)
	TextView mTvExpire;

	//三个需要单击事件的linearlayout
	@Bind(R.id.rlConstellation)
	AutoRelativeLayout mRlConstellation;
	@Bind(R.id.rlExpire)
	AutoRelativeLayout mRlExpire;
	@Bind(R.id.rlCategory)
	AutoRelativeLayout mRlCategory;

	//选择兴趣类型的view
	@Bind(R.id.llSelectCategoryUI)
	AutoLinearLayout mLlSelectCategory;//选择兴趣时弹出的界面
	@Bind(R.id.llFood)
	AutoLinearLayout mLlFood;
	@Bind(R.id.llMovie)
	AutoLinearLayout mLlMovie;
	@Bind(R.id.llExercise)
	AutoLinearLayout mLlExercise;
	@Bind(R.id.tvCategory)
	TextView mTvCategory;

	private View mRoot;

	private boolean isPopupWindowOpen = false;//标识popupwindow是否打开

	private boolean isSelectUIShow = false;//选择类型的ui是否显示

	/**
	 * 为避免多次加载，星座以及到期时间均使用同一个recyclerview，使用不同的adapter
	 */
	private View mSelectPopupWindowContentView;//popupWindow的contentView
	private PopupWindow mPopupWindow;

	private SelectRecyclerView mSelectRecyclerView;
	private SelectRecyclerViewAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	private int mCurrentType = TYPE_CONSTELLATION;//当前popupwindow显示的类型


	public static InviteConditionFragment newInstance()
	{
		return new InviteConditionFragment();
	}

	@Override
	public View inflateView(LayoutInflater inflater)
	{
		return inflater.inflate(R.layout.fragment_invite_condition, null);
	}


	@Override
	protected void getBundle(Bundle arguments)
	{
	}

	@Override
	protected void injectFragment()
	{
		mFragmentComponent.inject(this);
	}

	@Override
	public void afterViewCreated(View view)
	{
		mRoot = view;
		mCbWomen.setOncheckListener(this);
		mCbMan.setOncheckListener(this);
	}

	@OnClick({R.id.rlConstellation, R.id.rlExpire, R.id.rlCategory, R.id.llFood, R.id.llMovie,
			R.id.llExercise})
	public void onClick(View v)
	{
		ScreenUtils.hideKeyboard(getActivity());
		switch (v.getId())
		{
			case R.id.rlConstellation://星座要求的单击事件
				ScreenUtils.hideKeyboard(getActivity());
				showPopupWindow(TYPE_CONSTELLATION, Constant.CONSTELLATION);
				break;
			case R.id.rlExpire://到期时间的单击事件
				ScreenUtils.hideKeyboard(getActivity());
				showPopupWindow(TYPE_EXPIRE, Constant.EXPIRE_DATE);
				break;
			case R.id.rlCategory://弹出选择类型界面
				ScreenUtils.hideKeyboard(getActivity());
				if (isSelectUIShow) hideSelectCategoryUI();
				else showSelectCategoryUI();
				break;
			//类型各选项的单击事件
			case R.id.llMovie:
				setSelect(InterestCategory.MOVIE, TYPE_SELECT_CATEGORY);
				hideSelectCategoryUI();
				break;
			case R.id.llExercise:
				setSelect(InterestCategory.EXERCISE, TYPE_SELECT_CATEGORY);
				hideSelectCategoryUI();
				break;
			case R.id.llFood:
				setSelect(InterestCategory.FOOD, TYPE_SELECT_CATEGORY);
				hideSelectCategoryUI();
				break;

		}
	}


	@Override
	public void onCheck(CheckBox checkBox, boolean b)
	{
		switch (checkBox.getId())
		{
			//选择性别复选框的单击事件
			case R.id.cbMan:
				mCbWomen.setChecked(false);
				break;
			case R.id.cbWomen:
				mCbMan.setChecked(false);
				break;
		}
	}

	/**
	 * 得到条件筛选fragment的数据
	 * 需要对输入进行检查
	 * 1.文本框最小年龄如果超过
	 */
	public Invitation getInputData(Invitation invitation)
	{
		int minAge, maxAge;
		if ("".equals(mEtMinAge.getText().toString()))
		{
			minAge = Invitation.MIN_AGE;
		} else
		{
			minAge = Integer.parseInt(mEtMinAge.getText().toString());
		}
		if ("".equals(mEtMaxAge.getText().toString()))
		{
			maxAge = Invitation.MAX_AGE;
		} else
		{
			maxAge = Integer.parseInt(mEtMaxAge.getText().toString());
		}
		if (minAge < Invitation.MIN_AGE || minAge > Invitation.MAX_AGE) minAge = Invitation
				.MIN_AGE;
		if (maxAge > Invitation.MAX_AGE || maxAge < Invitation.MIN_AGE) maxAge = Invitation
				.MAX_AGE;
		invitation.setGender(mCbMan.isCheck() ? 0 : 1);
		invitation.setMinAge(minAge);
		invitation.setMaxAge(maxAge);
		invitation.setConstellation(Invitation.convertConstellation
				(mTvConstellation.getText().toString()));
		invitation.setExpire(mTvExpire.getText().toString());
		invitation.setCategory(mTvCategory.getText().toString());
		return invitation;

	}

	/**
	 * 设置popupwindow选中的选项
	 *
	 * @param text 需要设置的文本
	 * @param type 需要设置的类型
	 */
	public void setSelect(String text, int type)
	{
		if ("".equals(text)) text = Constant.NEVER;
		if (type == TYPE_CONSTELLATION)
		{
			mTvConstellation.setText(text);
		} else if (type == TYPE_EXPIRE)
		{
			mTvExpire.setText(text);
		} else
		{
			mTvCategory.setText(text);
		}
	}

	/**
	 * 显示选择类型的ui
	 */
	private void showSelectCategoryUI()
	{
		mLlSelectCategory.setVisibility(View.VISIBLE);
		mLlSelectCategory.setAlpha(0);
		mLlSelectCategory
				.animate()
				.alpha((float) 0.8)
				.scaleX((float) 1.2)
				.scaleY((float) 1.2)
				.setDuration(300)
				.setListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						mLlSelectCategory.animate()
								.alpha((float) 1)
								.scaleX((float) 1)
								.scaleY((float) 1)
								.setDuration(300)
								.setListener(new AnimatorListenerAdapter()
								{
									@Override
									public void onAnimationEnd(Animator animation)
									{
										isSelectUIShow = true;
									}
								});
					}
				});
	}

	/**
	 * 隐藏选择类型的ui
	 */
	public void hideSelectCategoryUI()
	{
		if (isSelectUIShow)
		{
			mLlSelectCategory.animate()
					.alpha(0)
					.setDuration(300)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLlSelectCategory.setVisibility(View.GONE);
							isSelectUIShow = false;
						}
					});
		}
	}

	public boolean isSelectCategoryUIShowing()
	{
		return isSelectUIShow;
	}

	/**
	 * 弹出popupwindow
	 * 因为如果每次弹出popupwindow会造成资源浪费，
	 * 所以判断，如果显示的类型跟上次显示类型不一致时，或者第一次show的时候
	 * 才重新设置adapter。
	 * 默认类型是星座
	 *
	 * @param type 是什么类型的popupwindow（星座、到期时间）
	 */
	private void showPopupWindow(final int type, String[] datas)
	{
		if (mSelectPopupWindowContentView == null)
		{
			//init popupwindow
			mSelectPopupWindowContentView = LayoutInflater.from(getActivity()).inflate(R.layout
					.popupwindow_select_view, null);
			mPopupWindow = PopupWindowManager.getDefaultPopupWindow(mSelectPopupWindowContentView);
			mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
			{
				@Override
				public void onDismiss()
				{
					//当popupwindow关闭的时候，使内容区域变暗
					isPopupWindowOpen = false;
					toggleLight();
				}
			});
			//初始化recyclerview
			mSelectRecyclerView = (SelectRecyclerView) mSelectPopupWindowContentView.findViewById
					(R.id
							.rvSelect);
			mSelectRecyclerView.setLayoutManager(mLayoutManager = new SnappingLinearLayoutManager
					(getActivity(),
							LinearLayoutManager.VERTICAL, false));
			mSelectRecyclerView.setAdapter(mAdapter == null ? new SelectRecyclerViewAdapter
					(getActivity(),
							datas) : mAdapter);
			//popupwindow上面的完成选择按钮
			ButtonFlat btnFinish = (ButtonFlat) mSelectPopupWindowContentView.findViewById(R.id
					.btnFinish);
			btnFinish.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mPopupWindow.dismiss();
					String text = mSelectRecyclerView.getSelectItemText();
					setSelect(text, mCurrentType);
				}
			});
		}
		//第一次show以及将要显式的类型跟上一次类型不一致时，均要更新adapter
		if (mAdapter == null || mCurrentType != type)
		{
			mAdapter = new SelectRecyclerViewAdapter(getActivity(),
					datas);
			mSelectRecyclerView.setAdapter(mAdapter);
		}
		mCurrentType = type;
		mPopupWindow.showAtLocation(mRoot, Gravity
				.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		isPopupWindowOpen = true;
		toggleLight();
	}

	/**
	 * popupWindow弹出或隐藏时
	 * 使内容区域变亮或变暗
	 */
	private void toggleLight()
	{
		WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
		lp.alpha = isPopupWindowOpen ? 0.3f : 1.0f;
		getActivity().getWindow().setAttributes(lp);
	}

}
