package com.chenantao.playtogether.mvc.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/2/10.
 */
public abstract class CommonViewHolder extends RecyclerView.ViewHolder
{
	public CommonViewHolder(View itemView)
	{
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public abstract void bindData(Object object);
}
