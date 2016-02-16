package com.chenantao.playtogether.mvc.view.viewholder;

import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.PersonalCenterHomeBean;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.Constant;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public class PersonalCenterHeaderVH extends CommonViewHolder
{
	@Bind(R.id.tvDetailDesc)
	public TextView mTvDetailDesc;
	@Bind(R.id.favorite_activity)
	public TextView mTvFavoriteActivity;
	@Bind(R.id.gender_trend)
	public TextView mTvGenderTrend;
	@Bind(R.id.btnChat)
	public ButtonRectangle mBtnChat;

	public PersonalCenterHeaderVH(View itemView)
	{
		super(itemView);
	}

	@Override
	public void bindData(Object object)
	{
		User user = ((PersonalCenterHomeBean) object).getUser();
		mTvDetailDesc.setText(user.getDetailDesc());
		String genderTrend = Constant.GENDER_ALL;
		if (!"".equals(user.getGenderTrend()))
			genderTrend = user.getGenderTrend();
		mTvGenderTrend.setText(genderTrend);
		mTvFavoriteActivity.setText(user.getFavoriteActivity());
		//如果当前展示用户为本人，不显示聊天按钮
		if (user.getObjectId().equals(AVUser.getCurrentUser().getObjectId()))
		{
			mBtnChat.setVisibility(View.GONE);

		} else
		{
			mBtnChat.setVisibility(View.VISIBLE);
		}
	}
}
