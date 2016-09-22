package com.chenantao.playtogether.mvc.view.fragment.user;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVUser;
import com.chenantao.autolayout.AutoCardView;
import com.chenantao.autolayout.AutoRelativeLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.user.PersonalCenterDetailController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.adapter.SelectRecyclerViewAdapter;
import com.chenantao.playtogether.mvc.view.common.BaseFragment;
import com.chenantao.playtogether.mvc.view.common.WriteDataActivity;
import com.chenantao.playtogether.mvc.view.widget.SelectListPopupWindow;
import com.chenantao.playtogether.mvc.view.widget.SelectRecyclerView;
import com.chenantao.playtogether.mvc.view.widget.SnappingLinearLayoutManager;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.DateUtils;
import com.chenantao.playtogether.utils.PopupWindowManager;
import com.gc.materialdesign.views.ButtonFlat;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/10.
 * 个人中心的详细信息
 */
public class PersonalCenterDetailFragment extends BaseFragment implements View.OnClickListener
{

	@Bind(R.id.tvGender)
	TextView mTvGender;
	@Bind(R.id.rlGender)
	AutoRelativeLayout mRlGender;
	@Bind(R.id.tvAge)
	TextView mTvAge;
	@Bind(R.id.rlAge)
	AutoRelativeLayout mRlAge;
	@Bind(R.id.tvConstellation)
	TextView mTvConstellation;
	@Bind(R.id.rlConstellation)
	AutoRelativeLayout mRlConstellation;
	@Bind(R.id.tvBirthday)
	TextView mTvBirthday;
	@Bind(R.id.rlBirthday)
	AutoRelativeLayout mRlBirthday;
	@Bind(R.id.tvEmail)
	TextView mTvEmail;
	@Bind(R.id.rlEmail)
	AutoRelativeLayout mRlEmail;
	@Bind(R.id.tvPhone)
	TextView mTvPhone;
	@Bind(R.id.rlPhone)
	AutoRelativeLayout mRlPhone;
	@Bind(R.id.tvSimpleDesc)
	TextView mTvSimpleDesc;
	@Bind(R.id.rlSimpleDesc)
	AutoRelativeLayout mRlSimpleDesc;
	@Bind(R.id.tvDetailDesc)
	TextView mTvDetailDesc;
	@Bind(R.id.rlDetailDesc)
	AutoRelativeLayout mRlDetailDesc;
	@Bind(R.id.cardViewDesc)
	AutoCardView mCardViewDesc;
	@Bind(R.id.tvFavoriteActivity)
	TextView mTvFavoriteActivity;
	@Bind(R.id.rlFavoriteActivity)
	AutoRelativeLayout mRlFavoriteActivity;
	@Bind(R.id.tvGenderTrend)
	TextView mTvGenderTrend;
	@Bind(R.id.rlGenderTrend)
	AutoRelativeLayout mRlGenderTrend;
	@Bind(R.id.root)
	FrameLayout mRoot;


	//使屏幕变暗的遮罩层
	@Bind(R.id.viewDim)
	View mViewDim;


	private PopupWindow mSelectGenderPW;
	private PopupWindow mSelectConstellationPW;
	private PopupWindow mSelectGenderTrendPW;
	private PopupWindow mSelectFavoriteActivityPW;


	//选择生日的dialog
	DatePickerDialog mDateDialog;
	public static final String EXTRA_USER = "user";

	public User mUser;//当前页面展示的 user

	@Inject
	public PersonalCenterDetailController mController;

	//打开 WriteDataActivity 的 requestCode
	public static final int REQ_WRITE_EMAIL = 1;
	public static final int REQ_WRITE_PHONE = 2;
	public static final int REQ_WRITE_SIMPLE_DESC = 3;
	public static final int REQ_WRITE_DETAIL_DESC = 4;
	public static final int REQ_WRITE_AGE = 5;


	public static PersonalCenterDetailFragment newInstance(User user)
	{
		PersonalCenterDetailFragment fragment = new PersonalCenterDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_USER, user.toString());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View inflateView(LayoutInflater inflater)
	{
		return inflater.inflate(R.layout.fragment_personal_center_detail, mRoot, false);
	}

	@Override
	protected void injectFragment()
	{
		mFragmentComponent.inject(this);
	}

	@Override
	public void afterViewCreated(View view)
	{
		String strUser = getArguments().getString(EXTRA_USER);
		if (strUser == null)
		{
			Toast.makeText(getActivity(), "出了点问题..", Toast.LENGTH_SHORT).show();
			getActivity().finish();
		}
		mUser = JSON.parseObject(strUser, User.class);
		//初始化性别、年龄、星座、生日、城市
		mTvGender.setText(mUser.getGender());
		mTvAge.setText(mUser.getAge() + "");
		mTvConstellation.setText(mUser.getConstellation());
		mTvBirthday.setText(DateUtils.date2string(mUser.getBirthday()));
		//简单描述，详细描述
		mTvSimpleDesc.setText(mUser.getSimpleDesc());
		mTvDetailDesc.setText(mUser.getDetailDesc());
		//最喜欢的活动，同伴性别倾向
		mTvFavoriteActivity.setText(mUser.getFavoriteActivity());
		mTvGenderTrend.setText(mUser.getGenderTrend());
		//邮箱，手机
		mTvEmail.setText(mUser.getEmail());
		mTvPhone.setText(mUser.getMobilePhoneNumber());
		//如果当前展示的用户是当前登录的用户，即本人，初始化事件,否则不初始化
		if (mUser.getObjectId().equals(AVUser.getCurrentUser().getObjectId()))
			initEvent();
	}

	private void initEvent()
	{
		mRlGender.setOnClickListener(this);
		mRlAge.setOnClickListener(this);
		mRlConstellation.setOnClickListener(this);
		mRlBirthday.setOnClickListener(this);
		mRlEmail.setOnClickListener(this);
		mRlPhone.setOnClickListener(this);
		mRlSimpleDesc.setOnClickListener(this);
		mRlDetailDesc.setOnClickListener(this);
		mRlFavoriteActivity.setOnClickListener(this);
		mRlGenderTrend.setOnClickListener(this);
		mRlFavoriteActivity.setOnClickListener(this);
	}

	/**
	 * 选择星座的 popupwindow
	 */
	private void showSelectConstellationPW()
	{
		if (mSelectConstellationPW == null)
		{
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_select_view,
							mRoot, false);
			mSelectConstellationPW = PopupWindowManager.getPopupWindow(view, ViewGroup
							.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, mViewDim);
			final SelectRecyclerView selectRecyclerView = (SelectRecyclerView) view.findViewById
							(R.id.rvSelect);
			selectRecyclerView.setLayoutManager(new SnappingLinearLayoutManager
							(getActivity(), LinearLayoutManager.VERTICAL, false));
			selectRecyclerView.setAdapter(new SelectRecyclerViewAdapter
							(getActivity(), Constant.CONSTELLATIONS));
			//popupwindow上面的完成选择按钮
			ButtonFlat btnFinish = (ButtonFlat) view.findViewById(R.id.btnFinish);
			btnFinish.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String constellation = selectRecyclerView.getSelectItemText();
					mTvConstellation.setText(constellation);
					mUser.setConstellation(constellation);
					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							mSelectConstellationPW.dismiss();
						}
					}, 20);
					mController.updateUser(mUser);
				}
			});
		}
		mSelectConstellationPW.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
		PopupWindowManager.toggleLight(true, mViewDim);
	}

	/**
	 * 选择性别的 popupwindow
	 */
	private void showSelectGenderPW()
	{
		if (mSelectGenderPW == null)
		{
			String[] items = new String[]{"男", "女"};
			mSelectGenderPW = new SelectListPopupWindow(getActivity(), "选择性别", items, mViewDim);
			((SelectListPopupWindow)
							mSelectGenderPW).setOnItemClickListener(new SelectListPopupWindow
							.OnItemClickListener()
			{
				@Override
				public void onClick(String item)
				{
					if (item.equals(Constant.GENDER_MAN) || item.equals(Constant.GENDER_WOMEN))
					{
						mTvGender.setText(item);
						mSelectGenderPW.dismiss();
						mUser.setGender(item);
						mController.updateUser(mUser);
					}
				}
			});
		}
		mSelectGenderPW.showAsDropDown(mRlGender);
		PopupWindowManager.toggleLight(true, mViewDim);
	}

	/**
	 * 选择同伴性别倾向的 PW(popupwindow)
	 */
	private void showSelectGenderTrendPW()
	{
		if (mSelectGenderTrendPW == null)
		{
			String[] items = new String[]{"男", "女", "不限"};
			mSelectGenderTrendPW = new SelectListPopupWindow(getActivity(), "期望同伴性别", items, mViewDim);
			((SelectListPopupWindow)
							mSelectGenderTrendPW).setOnItemClickListener(new SelectListPopupWindow
							.OnItemClickListener()
			{
				@Override
				public void onClick(String item)
				{
					if (item.equals(Constant.GENDER_MAN) || item.equals(Constant.GENDER_WOMEN) || item
									.equals(Constant.GENDER_ALL))
					{
						mTvGenderTrend.setText(item);
						mSelectGenderTrendPW.dismiss();
						mUser.setGenderTrend(item);
						mController.updateUser(mUser);
					}
				}
			});
		}
		mSelectGenderTrendPW.showAsDropDown(mRlGenderTrend);
		PopupWindowManager.toggleLight(true, mViewDim);
	}


	/**
	 * 显示选择最喜欢的活动的 PW
	 */
	private void showSelectFavoriteActivityPW()
	{
		if (mSelectFavoriteActivityPW == null)
		{
			String[] items = new String[]{"运动", "美食", "电影"};
			mSelectFavoriteActivityPW = new SelectListPopupWindow(getActivity(), "最喜欢的活动", items,
							mViewDim);
			((SelectListPopupWindow)
							mSelectFavoriteActivityPW).setOnItemClickListener(new SelectListPopupWindow
							.OnItemClickListener()
			{
				@Override
				public void onClick(String item)
				{
					if (item.equals("运动") || item.equals("美食") || item.equals("电影"))
					{
						mTvFavoriteActivity.setText(item);
						mSelectFavoriteActivityPW.dismiss();
						mUser.setFavoriteActivity(item);
						mController.updateUser(mUser);
					}
				}
			});
		}
		mSelectFavoriteActivityPW.showAsDropDown(mRlFavoriteActivity);
		PopupWindowManager.toggleLight(true, mViewDim);
	}

	private void showSelectBirthdayDialog()
	{
		if (mDateDialog == null)
		{
			Calendar calendar = Calendar.getInstance();
			mDateDialog = new DatePickerDialog(
							getActivity(), null,
							calendar.get(Calendar.YEAR), // 传入年份
							calendar.get(Calendar.MONTH), // 传入月份
							calendar.get(Calendar.DAY_OF_MONTH) // 传入天数
			);
			mDateDialog.setButton(DialogInterface.BUTTON_POSITIVE,
							"完成", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
																		int which)
								{
									DatePicker datePicker = mDateDialog.getDatePicker();
									// 将年月日转换为字符串，判断是否为个位数，若为个位数，在前面补上0
									String year = datePicker.getYear() + "";
									String month = datePicker.getMonth() + 1
													+ "";
									String day = datePicker.getDayOfMonth()
													+ "";
									if (month.length() == 1)
									{
										month = "0" + month;
									}
									if (day.length() == 1)
									{
										day = "0" + day;
									}
									String date = year + "-" + month + "-" + day;
									mTvBirthday.setText(date);
									mUser.setBirthday(DateUtils.string2date(date));
									mController.updateUser(mUser);
								}
							});
			mDateDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
							"取消", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
																		int which)
								{
									dialog.cancel();
								}
							});
		}
		mDateDialog.show();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rlGender://性别
				showSelectGenderPW();
				break;
			case R.id.rlAge://年龄
				WriteDataActivity.startActivityForResult(this, "输入你的年龄", "正确填写你的年龄有助于别人了解你哦", InputType
								.TYPE_CLASS_NUMBER, 2, REQ_WRITE_AGE);
				break;
			case R.id.rlConstellation://星座
				showSelectConstellationPW();
				break;
			case R.id.rlBirthday://生日
				showSelectBirthdayDialog();
				break;
			case R.id.rlEmail://email
				WriteDataActivity.startActivityForResult(this, "请输入邮箱", "注意正确的邮箱格式", InputType
								.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, 100, REQ_WRITE_EMAIL);
				break;
			case R.id.rlPhone://手机
				WriteDataActivity.startActivityForResult(this, "请输入电话号码", "提供正确的号码有利于好友的联系哦",
								InputType.TYPE_CLASS_PHONE, 100, REQ_WRITE_PHONE);
				break;
			case R.id.rlSimpleDesc://简单描述
				WriteDataActivity.startActivityForResult(this, "简单描述一下自己", "长度必须在" + User
								.SIMPLE_DESC_MAX_LENGTH + "个字以内", InputType.TYPE_TEXT_FLAG_MULTI_LINE, User
								.SIMPLE_DESC_MAX_LENGTH, REQ_WRITE_SIMPLE_DESC);
				break;
			case R.id.rlDetailDesc://详细描述
				WriteDataActivity.startActivityForResult(this, "详细描述一下自己", "长度必须在" + User
												.DETAIL_DESC_MAX_LENGTH + "个字以内", InputType.TYPE_TEXT_FLAG_MULTI_LINE, User
												.DETAIL_DESC_MAX_LENGTH,
								REQ_WRITE_DETAIL_DESC);
				break;
			case R.id.rlGenderTrend://同伴性别倾向
				showSelectGenderTrendPW();
				break;
			case R.id.rlFavoriteActivity://最喜欢的活动
				showSelectFavoriteActivityPW();
				break;
		}
	}

	public void updateUserSuccess()
	{
		if (getActivity() != null)
		{
			Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
		}
	}

	public void updateUserFail()
	{
		Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case REQ_WRITE_SIMPLE_DESC:
				if (resultCode == Activity.RESULT_OK)
				{
					String simpleDesc = data.getStringExtra(WriteDataActivity.EXTRA_DATA);
					if (simpleDesc != null)
					{
						mTvSimpleDesc.setText(simpleDesc);
						mUser.setSimpleDesc(simpleDesc);
					}
				}
				break;
			case REQ_WRITE_DETAIL_DESC:
				if (resultCode == Activity.RESULT_OK)
				{
					String detailDesc = data.getStringExtra(WriteDataActivity.EXTRA_DATA);
					if (detailDesc != null)
					{
						mTvDetailDesc.setText(detailDesc);
						mUser.setDetailDesc(detailDesc);
					}
				}
				break;
			case REQ_WRITE_EMAIL:
				if (resultCode == Activity.RESULT_OK)
				{
					String email = data.getStringExtra(WriteDataActivity.EXTRA_DATA);
					if (email != null)
					{
						mTvEmail.setText(email);
						mUser.setEmail(email);
					}
				}
				break;
			case REQ_WRITE_PHONE:
				if (resultCode == Activity.RESULT_OK)
				{
					String phone = data.getStringExtra(WriteDataActivity.EXTRA_DATA);
					if (phone != null)
					{
						mTvPhone.setText(phone);
						mUser.setMobilePhoneNumber(phone);
					}
				}
				break;
			case REQ_WRITE_AGE:
				if (resultCode == Activity.RESULT_OK)
				{
					String age = data.getStringExtra(WriteDataActivity.EXTRA_DATA);
					if (age != null)
					{
						mTvAge.setText(age);
						mUser.setAge(Integer.parseInt(age));
					}
				}
				break;
		}
		if (resultCode == Activity.RESULT_OK)//代表输入数据成功，那么更新user
		{
			mController.updateUser(mUser);
		}
	}
}
