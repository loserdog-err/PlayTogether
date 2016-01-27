package com.chenantao.playtogether;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.injector.component.ApplicationComponent;
import com.chenantao.playtogether.injector.component.DaggerApplicationComponent;
import com.chenantao.playtogether.injector.modules.ApiModule;
import com.chenantao.playtogether.injector.modules.ApplicationModule;
import com.chenantao.playtogether.injector.modules.BllModule;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Chenantao_gg on 2016/1/17.
 */
public class MyApplication extends Application
{
	private ApplicationComponent mApplicationComponent;

	@Override
	public void onCreate()
	{
		super.onCreate();
		mApplicationComponent = DaggerApplicationComponent.builder()
				.apiModule(new ApiModule())
				.bllModule(new BllModule())
				.applicationModule(new ApplicationModule(this))
				.build();
		//leancloud所需要的参数
		initBean();
		AVOSCloud.initialize(this, "Y16sd0KaVf7Lsw6aWoTFSGOg-gzGzoHsz",
				"ASphDiRHs5K7EPQCX2NadEdE");
//		AVOSCloud.setDebugLogEnabled(true);
		//初始化log
		Logger.init("cat")
				.methodCount(2)
				.hideThreadInfo();
		initPicasso();

	}

	private void initPicasso()
	{
		Picasso.Builder builder = new Picasso.Builder(this);
		builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
		Picasso built = builder.build();
		built.setIndicatorsEnabled(true);
		built.setLoggingEnabled(true);
		Picasso.setSingletonInstance(built);
	}

	private void initBean()
	{
		AVUser.alwaysUseSubUserClass(User.class);
		AVObject.registerSubclass(Invitation.class);
	}

	public ApplicationComponent getApplicationComponent()
	{
		return mApplicationComponent;
	}
}
