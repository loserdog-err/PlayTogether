package com.chenantao.playtogether.chat.mvc.view.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.mvc.view.activity.ChatActivity;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.mvc.view.widget.MultipleShapeImg;
import com.chenantao.playtogether.utils.DateUtils;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.jauker.widget.BadgeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Chenantao_gg on 2016/2/1.
 */
public class ConversationViewHolder extends RecyclerView.ViewHolder
{

	@Bind(R.id.conversationIcon)
	public MultipleShapeImg mConversationIcon;
	@Bind(R.id.tvConversationName)
	public TextView mTvConversationName;
	@Bind(R.id.tvLastMsg)
	public TextView mTvLastMsg;
	@Bind(R.id.tvLastUpdate)
	public TextView mTvLastUpdate;
	public BadgeView mBadgeView;

	private Context mContext;


	public ConversationViewHolder(View itemView, Context context)
	{
		super(itemView);
		ButterKnife.bind(this, itemView);
		mContext = context;
	}

	public void bindData(final AVIMConversation conversation)
	{
		List<String> members = conversation.getMembers();
		//设置炮友的头像
		mConversationIcon.setImageResource(R.mipmap.avatar);
		//对于单聊的会话名，为你的好友名，取出conversation member，跟 client id比较，不同的即为好友名
		int chatType = (int) conversation.getAttribute(ChatConstant.KEY_CHAT_TYPE);
		if (chatType == ChatConstant.TYPE_SINGLE_CHAT)
		{
			String clientId = AVImClientManager.getInstance().getClientId();
			//遍历群会员，把会话名设置为你好友的名字，并且把会话的图标改成你好友的头像
			for (String member : members)
			{
				if (!member.equals(clientId))
				{
					//你的好友名
					mTvConversationName.setText(member);
					//取出创建去会话时保存的头像路径，以用户名为key，头像路径为value的值
					Object avatar = conversation.getAttribute(member);
					if (avatar != null)
					{
						PicassoUtils.displayFitImage(mContext, Uri.parse((String) avatar), mConversationIcon,
										null);
					}
					break;
				}
			}
		} else
		{
			//否则把名字设置成会话名就好
			mTvConversationName.setText(conversation.getName());
		}
		mTvLastUpdate.setText(DateUtils.date2desc(conversation.getLastMessageAt()));
		conversation.getLastMessage(new AVIMSingleMessageQueryCallback()
		{
			@Override
			public void done(AVIMMessage avimMessage, AVIMException e)
			{
				if (e == null)
				{
					if (avimMessage instanceof AVIMTextMessage)
						mTvLastMsg.setText(((AVIMTextMessage) avimMessage).getText());
					else if (avimMessage instanceof AVIMAudioMessage)
						mTvLastMsg.setText("[语音]");
					else if (avimMessage instanceof AVIMImageMessage)
						mTvLastMsg.setText("[图片]");
					else
						mTvLastMsg.setText("不知道发的什么鬼");
				} else
				{
					e.printStackTrace();
				}
			}
		});
		itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (conversation.getMembers().size() < 2)
				{
					Toast.makeText(mContext, "会话已过期", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(mContext, ChatActivity.class);
				intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation
								.getConversationId());
				intent.putExtra(ChatActivity.EXTRA_CONVERSATION_NAME, mTvConversationName.getText()
								.toString());
				mContext.startActivity(intent);
			}
		});
	}
}
