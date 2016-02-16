package com.chenantao.playtogether.mvc.view.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.user.LoginController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.invitation.HomeActivity;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.DialogUtils;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import javax.inject.Inject;

import butterknife.Bind;

public class LoginActivity extends BaseActivity implements View.OnFocusChangeListener, View
				.OnClickListener
{

	@Bind(R.id.etUsername)
	EditText mEtUsername;
	@Bind(R.id.etPassword)
	EditText mEtPassword;
	@Bind(R.id.btnRegister)
	ButtonFlat mBtnRegister;
	@Bind(R.id.btnLogin)
	ButtonRectangle mBtnLogin;

	@Inject
	public LoginController mController;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_login;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		if (AVUser.getCurrentUser() != null)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			finish();
		}
		ActionBar toolbar = getSupportActionBar();
		if (toolbar != null)
		{
			toolbar.setTitle("登录");
		}
		mBtnLogin.setRippleSpeed(50);

		initEvent();
	}

	private void initEvent()
	{
		mEtUsername.setOnFocusChangeListener(this);
		mEtPassword.setOnFocusChangeListener(this);
		mBtnLogin.setOnClickListener(this);
		mBtnRegister.setOnClickListener(this);


	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		String username = mEtUsername.getText().toString();
		String password = mEtPassword.getText().toString();
		switch (v.getId())
		{
			case R.id.etUsername:
				if (!hasFocus)//如果失去了焦点
				{
					if (username.length() > 0)
					{
						mController.checkUsername(username);
					}
				}
				break;
			case R.id.etPassword:
				if (!hasFocus)//如果失去了焦点
				{
					if (password.length() > 0)
					{
						mController.checkPassword(password);
					}
				}
				break;
		}
	}

	@Override
	public void onClick(View v)
	{
		String username = mEtUsername.getText().toString();
		String password = mEtPassword.getText().toString();
		switch (v.getId())
		{
			case R.id.btnLogin:
				if (mController.checkUsername(username) && mController.checkPassword(password))
				{
					User user = new User(username, password);
					mController.login(user);
					DialogUtils.showProgressDialog("登录中，请骚等`(*∩_∩*)′", this);
				}
				break;
			case R.id.btnRegister:
				Intent intent = new Intent(this, RegisterActivity.class);
				startActivity(intent);
				break;
		}
	}

	public void setUsernameError(String msg)
	{
		mEtUsername.setError(msg);
	}

	public void setPasswordError(String msg)
	{
		mEtPassword.setError(msg);
	}

	public void loginSuccess(AVUser user)
	{
		DialogUtils.dismissProgressDialog();
		Intent intent = new Intent(this, HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("user", user);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	public void loginError()
	{
		DialogUtils.dismissProgressDialog();
		Toast.makeText(this, "Sorry~!用户名或密码错误", Toast.LENGTH_SHORT).show();
	}


}
