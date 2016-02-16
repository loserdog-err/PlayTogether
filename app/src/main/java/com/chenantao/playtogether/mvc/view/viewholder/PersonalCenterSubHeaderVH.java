package com.chenantao.playtogether.mvc.view.viewholder;

import android.view.View;
import android.widget.TextView;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.PersonalCenterHomeBean;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public class PersonalCenterSubHeaderVH extends CommonViewHolder
{
	@Bind(R.id.tvInviteCount)
	TextView mTvInviteCount;
	@Bind(R.id.tvBeInvitedCount)
	TextView mTvBeInvitedCount;

	public PersonalCenterSubHeaderVH(View itemView)
	{
		super(itemView);
	}

	@Override
	public void bindData(Object object)
	{
		int inviteCount = ((PersonalCenterHomeBean) object).getInviteCount();
		int beInvitedCount = ((PersonalCenterHomeBean) object).getBeInvitedCount();
		mTvInviteCount.setText(inviteCount + "");
		mTvBeInvitedCount.setText(beInvitedCount + "");
	}
}
