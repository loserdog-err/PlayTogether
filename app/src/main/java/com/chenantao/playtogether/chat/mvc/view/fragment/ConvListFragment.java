package com.chenantao.playtogether.chat.mvc.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.event.EventHadRead;
import com.chenantao.playtogether.chat.event.EventHomeConversationChange;
import com.chenantao.playtogether.chat.event.EventMemberLeft;
import com.chenantao.playtogether.chat.mvc.controller.ConvListController;
import com.chenantao.playtogether.chat.mvc.view.adapter.ConvListAdapter;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseFragment;
import com.chenantao.playtogether.utils.PopupWindowManager;
import com.chenantao.playtogether.utils.SpUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * Created by Chenantao_gg on 2016/2/5.
 * 会话列表的fragment
 */
public class ConvListFragment extends BaseFragment
{
	public static final String EXTRA_USERNAME = "username";

	//recyclerview四件套
	@Bind(R.id.rvConversationList)
	RecyclerView mRvConversationList;
	ConvListAdapter mAdapter;
	List<AVIMConversation> mDatas;

	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	@Inject
	ConvListController mController;

	private String mName;//我的名字

	PopupWindow mOperatePW;//操作会话的 popupwindow


	public static ConvListFragment newInstance()
	{
		return new ConvListFragment();
	}

	@Override
	public View inflateView(LayoutInflater inflater)
	{
		return inflater.inflate(R.layout.fragment_conv_list, null);
	}


	@Override
	protected void injectFragment()
	{
		mFragmentComponent.inject(this);
	}


	@Override
	public void afterViewCreated(View view)
	{
		EventBus.getDefault().register(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
		//初始化recyclerview
		mDatas = new ArrayList<>();
		mAdapter = new ConvListAdapter(getActivity(), mDatas);
		mRvConversationList.setAdapter(mAdapter);
		mRvConversationList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager
						.VERTICAL, false));
		//拉取用户的会话
		showProgress();
		mController.getConversations(AVUser.getCurrentUser(User.class).getUsername());
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				mController.getConversations(AVUser.getCurrentUser(User.class).getUsername());
			}
		});
	}


	/**
	 * 收到了消息,根据情况判断是否需要更新未读数量。
	 * 如果是在聊天的界面发送的event，则不需要增加未读数量
	 */
	public void onEvent(EventHomeConversationChange event)
	{
		String conversationId = event.conversation.getConversationId();
		if (event.isUpdateUnreadCount)
			SpUtils.increment(getActivity(), conversationId, 1);
		int pos = getConvPos(conversationId);
		if (pos != -1)
		{
			mAdapter.notifyItemChanged(pos);
		} else//证明还没有这条会话，手动从服务器拉取数据
		{
			Logger.e("refresh conversations");
			// TODO: 2016/2/5 第一次接收这条会话的时候 conversation 的 attr 没有值，只能强制重新拉取 conversation了
//			mAdapter.addConversation(event.conversation);
			mController.getConversations(AVUser.getCurrentUser(User.class).getUsername());
		}
	}


	/**
	 * 消息已读
	 */
	public void onEvent(EventHadRead event)
	{
		int pos = getConvPos(event.conversationId);
		if (pos != -1)
			mAdapter.notifyItemChanged(pos);
	}

	public void onEvent(EventMemberLeft event)
	{
		//member left
		AVIMConversation conversation = event.conversation;
		//如果成员数小于2，则删除该会话
		if (conversation.getMembers().size() < 2)
		{
			Logger.e("del conv");
			mAdapter.removeConversation(conversation);
			mController.delConversation(conversation);
		}
	}

	public void getConversationsSuccess(List<AVIMConversation> conversations)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		if (!conversations.isEmpty())
		{
			mDatas = conversations;
			mRvConversationList.setAdapter(mAdapter = new ConvListAdapter(getActivity(), mDatas));
		}
		//设置长按事件
		mAdapter.setOnItemLongClickListener(new ConvListAdapter.OnItemLongClickListener()
		{
			@Override
			public void longClick(AVIMConversation conversation, View item)
			{
				showOperatePW(conversation, item);
			}
		});
//			mAdapter.addConversations(conversations);
	}

	private void showOperatePW(final AVIMConversation conversation, View item)
	{
		if (mOperatePW == null)
		{
			View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_operate,
							null);
			contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
							View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			mOperatePW = PopupWindowManager.getDefaultPopupWindow(contentView, null);
			TextView tvDelConv = (TextView) contentView.findViewById(R.id.delConv);
			tvDelConv.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mAdapter.removeConversation(conversation);
					mController.delConversation(conversation);
					mOperatePW.dismiss();
				}
			});
		}
		int itemWidth = item.getMeasuredWidth();
		int itemHeight = item.getMeasuredHeight();
		int PWWidth = mOperatePW.getContentView().getMeasuredWidth();
		int PWHeight = mOperatePW.getContentView().getMeasuredHeight();
		mOperatePW.showAsDropDown(item, itemWidth / 2 - PWWidth / 2, -itemHeight - PWHeight);
	}


	public void getConversationsFail(String msg)
	{
		mSwipeRefreshLayout.setRefreshing(false);
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获得conversation的下标
	 *
	 * @return conversation的下标
	 */
	private int getConvPos(String conversationId)
	{
		int pos = -1;
		for (int i = 0; i < mDatas.size(); i++)
		{
			AVIMConversation conversation = mDatas.get(i);
			if (conversation.getConversationId().equals(conversationId))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}

	public void showProgress()
	{
		mSwipeRefreshLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				//刷新动画
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});
	}

}
