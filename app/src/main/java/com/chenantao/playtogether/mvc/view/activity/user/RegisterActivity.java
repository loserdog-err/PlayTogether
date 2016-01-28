package com.chenantao.playtogether.mvc.view.activity.user;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.view.activity.invitation.HomeActivity;
import com.chenantao.playtogether.mvc.controller.user.RegisterController;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.DialogUtils;
import com.gc.materialdesign.views.ButtonRectangle;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/1/18.
 */
public class RegisterActivity extends BaseActivity implements View.OnFocusChangeListener
{
	@Bind(R.id.etUsername)
	EditText mEtUsername;
	@Bind(R.id.etPassword)
	EditText mEtPassword;
	@Bind(R.id.btnRegister)
	ButtonRectangle mBtnRegister;
	@Inject
	public RegisterController mController;

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_register;
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
			toolbar.setTitle("登录");
		}
		mBtnRegister.setRippleSpeed(25);
		initEvent();
	}

	private void initEvent()
	{
		mEtUsername.setOnFocusChangeListener(this);
		mEtPassword.setOnFocusChangeListener(this);
		mBtnRegister.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String username = mEtUsername.getText().toString();
				String password = mEtPassword.getText().toString();
				if (mController.checkUsername(username) && mController.checkPassword(password))
				{
					//符合注册条件
					User user = new User(username, password);
					user.setGender(0);
					DialogUtils.showProgressDialog("注册中，请骚等哦`(*∩_∩*)′", RegisterActivity.this);
					mController.registerUser(user);
				}

			}
		});
	}


	/**
	 * 文本框的失去焦点事件
	 * 主要用户输入校验
	 *
	 * @param v
	 * @param hasFocus
	 */
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

	/**
	 * 设置用户名输入有错误
	 */
	public void setUsernameError(String msg)
	{
		mEtUsername.setError(msg);
	}

	public void setPasswordError(String msg)
	{
		mEtPassword.setError(msg);
	}

	/**
	 * 注册成功后要进行登录
	 *
	 * @param user
	 */
	public void registerSuccess(User user)
	{
		DialogUtils.dismissProgressDialog();
		Logger.e("注册成功，撸一发吧骚年");
		Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
		mController.login(user);
		DialogUtils.showProgressDialog("登录中，请骚等", this);
	}

	public void registerError()
	{
		DialogUtils.dismissProgressDialog();
		Toast.makeText(this, "注册失败，用户名已存在", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 登录成功后完成跳转
	 *
	 * @param user
	 */
	public void loginSuccess(AVUser user)
	{
		Logger.e("登录成功，狂撸吧少年");
		Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
		DialogUtils.dismissProgressDialog();
		//跳转到主页面
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

	public void loginError()
	{
		DialogUtils.dismissProgressDialog();
		Toast.makeText(this, "登录失败，待会在登录吧", Toast.LENGTH_SHORT).show();
	}
}
