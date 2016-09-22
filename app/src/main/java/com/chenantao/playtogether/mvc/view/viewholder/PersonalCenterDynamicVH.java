package com.chenantao.playtogether.mvc.view.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.invitation.InvitationDetailActivity;
import com.chenantao.playtogether.utils.Constant;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public class PersonalCenterDynamicVH extends CommonViewHolder
{
	@Bind(R.id.tvAction)
	TextView mTvAction;
	@Bind(R.id.tvTitle)
	TextView mTvTitle;
	@Bind(R.id.tvAcceptInvitedNum)
	TextView mTvAcceptInvitedNum;
	@Bind(R.id.ivCategory)
	RoundedImageView mIvCategory;

	public PersonalCenterDynamicVH(View itemView)
	{
		super(itemView);
	}

	@Override
	public void bindData(Object object)
	{
		final Invitation invitation = (Invitation) object;
		User author = invitation.getAuthor();
		if (author.getUsername().equals(AVUser.getCurrentUser(User.class).getUsername()))
		{
			mTvAction.setText("发起了邀请");
		} else
		{
			mTvAction.setText("接受了邀请");
		}
		mTvTitle.setText(invitation.getTitle());
		mTvAcceptInvitedNum.setText(mIvCategory.getResources().getString(R.string.join_count,
						invitation.getAcceptInviteUsers().size()));
		String category = invitation.getCategory();
		if (category.equals(Constant.CATEGORY_MOVIE))
		{
			mIvCategory.setImageResource(R.mipmap.category_movie);

		} else if (category.equals(Constant.CATEGORY_EXERCISE))
		{
			mIvCategory.setImageResource(R.mipmap.category_exercise);

		} else if (category.equals(Constant.CATEGORY_FOOD))
		{
			mIvCategory.setImageResource(R.mipmap.category_food);
		}
		//设置item的单击事件
		itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), InvitationDetailActivity.class);
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_ID, invitation.getObjectId());
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_TITLE, invitation.getTitle());
				intent.putExtra(InvitationDetailActivity.EXTRA_INVITATION_USERNAME, invitation.getAuthor()
								.getUsername());
				v.getContext().startActivity(intent);
			}
		});
	}
}
