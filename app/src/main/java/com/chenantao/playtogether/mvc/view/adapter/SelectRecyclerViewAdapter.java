package com.chenantao.playtogether.mvc.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenantao.playtogether.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/1/21.
 */
public class SelectRecyclerViewAdapter extends RecyclerView.Adapter<SelectRecyclerViewAdapter
		.SelectViewHolder>
{
	private Context mContext;
	private String[] mDatas;

	public SelectRecyclerViewAdapter(Context context, String[] datas)
	{
		mContext = context;
		mDatas = datas;
	}

	@Override
	public SelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_select_view, parent,
				false);
		return new SelectViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SelectViewHolder holder, int position)
	{
		holder.tv.setText(mDatas[position]);
	}

	@Override
	public int getItemCount()
	{
		return mDatas.length;
	}

	class SelectViewHolder extends RecyclerView.ViewHolder
	{
		@Bind(R.id.tv)
		TextView tv;

		public SelectViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
