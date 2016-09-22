package com.chenantao.playtogether.mvc.view.common;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.utils.ScreenUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/11.
 */
public class WriteDataActivity extends BaseActivity implements View.OnClickListener
{
	@Bind(R.id.tvOk)
	TextView mTvOk;
	@Bind(R.id.toolbar)
	Toolbar mToolbar;
	@Bind(R.id.etInput)
	EditText mEtInput;
	@Bind(R.id.tvLimitHint)
	TextView mTvLimitHint;
	@Bind(R.id.textInputLayout)
	TextInputLayout mTextInputLayout;
	public static final String EXTRA_HINT = "hint";
	public static final String EXTRA_LIMIT_HINT = "limitHint";
	public static final String EXTRA_INPUT_TYPE = "inputType";
	public static final String EXTRA_MAX_LENGTH = "maxLength";

	public static final String EXTRA_DATA = "data";

	private int mInputType;

	public static void startActivityForResult(Fragment fragment, String hint, String limitHint, int
					inputType, int maxLength, int reqCode)
	{
		Intent intent = new Intent(fragment.getActivity(), WriteDataActivity.class);
		intent.putExtra(EXTRA_HINT, hint);
		intent.putExtra(EXTRA_LIMIT_HINT, limitHint);
		intent.putExtra(EXTRA_INPUT_TYPE, inputType);
		intent.putExtra(EXTRA_MAX_LENGTH, maxLength);
		fragment.startActivityForResult(intent, reqCode);
	}

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_write_data;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		String hint = getIntent().getStringExtra(EXTRA_HINT);
		String limitHint = getIntent().getStringExtra(EXTRA_LIMIT_HINT);
		mInputType = getIntent().getIntExtra(EXTRA_INPUT_TYPE, InputType.TYPE_CLASS_TEXT);
		int maxLength = getIntent().getIntExtra(EXTRA_MAX_LENGTH, Integer.MAX_VALUE);
		mEtInput.setInputType(mInputType);
		mEtInput.setHint(hint);
		mTextInputLayout.setHint(hint);
		mEtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
		mTvLimitHint.setText(limitHint);
		mEtInput.setSingleLine(false);
		mTvOk.setOnClickListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		if (hasFocus)
		{
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					ScreenUtils.showKeyboard(WriteDataActivity.this);
				}
			}, 200);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tvOk:
				//如果是输入邮箱，利用正则表达式校验一下
				String data = mEtInput.getText().toString();
				if (mInputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
				{
					if (!validateEmail(data))
					{
						mEtInput.setError("邮箱格式有误");
						return;
					}
				}
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DATA, data);
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}

	public boolean validateEmail(String email)
	{
		Pattern pattern = Pattern
						.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
