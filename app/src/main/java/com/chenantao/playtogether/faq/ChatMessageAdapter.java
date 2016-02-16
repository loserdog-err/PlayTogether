package com.chenantao.playtogether.faq;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.utils.DateUtils;
import com.chenantao.playtogether.utils.PicassoUtils;

import java.util.List;


public class ChatMessageAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private List<ChatMessage> mDatas;
	private Context mContext;

	public ChatMessageAdapter(Context context, List<ChatMessage> mDatas)
	{
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
		mContext = context;
	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		ChatMessage chatMessage = mDatas.get(position);
		if (chatMessage.getType() == ChatMessage.Type.INCOMING)
		{
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ChatMessage chatMessage = mDatas.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			if (getItemViewType(position) == 0)
			{
				convertView = mInflater.inflate(R.layout.item_faq_from, parent,
								false);
				viewHolder = new ViewHolder();
				viewHolder.mDate = (TextView) convertView
								.findViewById(R.id.tv_postTime);
				viewHolder.mMsg = (TextView) convertView
								.findViewById(R.id.tv_text);
				viewHolder.mAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
			} else
			{
				convertView = mInflater.inflate(R.layout.item_faq_to, parent,
								false);
				viewHolder = new ViewHolder();
				viewHolder.mDate = (TextView) convertView
								.findViewById(R.id.tv_postTime);
				viewHolder.mMsg = (TextView) convertView
								.findViewById(R.id.tv_text);
				viewHolder.mAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
			}
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (getItemViewType(position) == 1)
		{
			String avatarUrl = AVUser.getCurrentUser(User.class).getAvatarUrl();
			if (avatarUrl != null)
			{
				PicassoUtils.displayFitImage(mContext, Uri.parse(avatarUrl), viewHolder.mAvatar, null);
			}
		}
		viewHolder.mDate.setText(DateUtils.date2string(chatMessage.getDate(), "HH:mm"));
		viewHolder.mMsg.setText(chatMessage.getMsg());
		return convertView;
	}

	private final class ViewHolder
	{
		TextView mDate;
		TextView mMsg;
		ImageView mAvatar;
	}

}
