package com.chenantao.playtogether;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.orhanobut.logger.Logger;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application>
{
	public ApplicationTest()
	{
		super(Application.class);
	}

	public void test()
	{
		AVQuery<Invitation> query = AVObject.getQuery(Invitation.class);
		try
		{
			Invitation invitation = query.get("56a4ad21a633bd025780ab94");
			Logger.e("length:" + invitation.getPics().size());
		} catch (AVException e)
		{
			e.printStackTrace();
		}
	}

}