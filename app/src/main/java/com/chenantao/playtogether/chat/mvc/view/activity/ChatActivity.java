package com.chenantao.playtogether.chat.mvc.view.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.chenantao.autolayout.AutoFrameLayout;
import com.chenantao.autolayout.AutoLinearLayout;
import com.chenantao.autolayout.AutoRelativeLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.chat.AVImClientManager;
import com.chenantao.playtogether.chat.event.EventChatPic;
import com.chenantao.playtogether.chat.event.EventReceiveMessage;
import com.chenantao.playtogether.chat.handler.ChatHandler;
import com.chenantao.playtogether.chat.mvc.controller.ChatController;
import com.chenantao.playtogether.chat.mvc.view.adapter.ChatAdapter;
import com.chenantao.playtogether.chat.mvc.view.widget.RecorderButton;
import com.chenantao.playtogether.chat.utils.AudioManager;
import com.chenantao.playtogether.chat.utils.ChatConstant;
import com.chenantao.playtogether.chat.utils.NotificationUtils;
import com.chenantao.playtogether.gallery.MyGalleryActivity;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.utils.Constant;
import com.chenantao.playtogether.utils.ScreenUtils;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Chenantao_gg on 2016/1/29.
 * 聊天的主界面
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener,
				CompoundButton.OnCheckedChangeListener, EmojiconGridFragment.OnEmojiconClickedListener,
				EmojiconsFragment.OnEmojiconBackspaceClickedListener, View.OnFocusChangeListener
{
	public static final String TAG = ChatActivity.class.getSimpleName();

	@Bind(R.id.btnSend)
	Button mBtnSend;

	@Bind(R.id.cbSelectEmoji)
	CheckBox mCbSelectEmoji;
	@Bind(R.id.cbSelectPic)
	CheckBox mCbSelectPic;
	@Bind(R.id.cbVoice)
	CheckBox mCbVoice;
	@Bind(R.id.messageToolBox)
	AutoLinearLayout mMessageToolBox;
	@Bind(R.id.flEmojicons)
	AutoFrameLayout mFlEmojicons;
	@Bind(R.id.tvSelectNumHint)
	TextView mTvSelectNumHint;
	@Bind(R.id.rlSelectPic)
	AutoRelativeLayout mRlSelectPic;
	@Bind(R.id.btnRecorder)
	RecorderButton mBtnRecorder;
	@Bind(R.id.rlVoice)
	AutoRelativeLayout mRlVoice;
	@Bind(R.id.etContent)
	EditText mEtContent;
	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@Bind(R.id.tvTitle)
	TextView mTvTitle;


	public static final String EXTRA_CONVERSATION_ID = "conversationId";
	public static final String EXTRA_CONVERSATION_NAME = "conversationName";

	private ChatHandler mHandler;

	@Inject
	ChatController mController;

	private String mConversationId;

	//recyclerview 四件套
	@Bind(R.id.rvChatMessage)
	RecyclerView mRvChatMessage;
	LinearLayoutManager mLayoutManager;
	List<AVIMMessage> mDatas;
	ChatAdapter mAdapter;

	//选择相片的recyclerview四件套
	@Bind(R.id.rvSelectPic)
	RecyclerView mRvSelectPic;
	List<String> mSelectPaths;

	private boolean mIsPrepare = false;//页面是否准备完毕，这里界面数据初始化完毕后就算准备完毕了

	@Override
	public int getLayoutId()
	{
		return R.layout.activity_chat;
	}

	@Override
	public void injectActivity()
	{
		mActivityComponent.inject(this);
	}

	@Override
	public void afterCreate()
	{
		mConversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
		String conversationName = getIntent().getStringExtra(EXTRA_CONVERSATION_NAME);
		if (mConversationId == null)
		{
			Toast.makeText(this, "无效会话", Toast.LENGTH_SHORT).show();
			finish();
		}
		//用于查看当前会话是否可见，如果可见，不更新首页的未读数量
		NotificationUtils.addTag(mConversationId);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.setTitle("");
		if (conversationName == null)
			mTvTitle.setText("陌生人");
		else mTvTitle.setText(conversationName);
		EventBus.getDefault().register(this);
		mHandler = new ChatHandler();
		//隐藏图片展示框,这里不需要，因为在 gallery 一点击确定直接发送出去即可
		mRlSelectPic.setVisibility(View.GONE);
		//获取消息列表并显示加载中
		mController.getNewlyMessageData(mConversationId);
		showProgress();
		initEvent();
		//初始化recyclerview
		mDatas = new ArrayList<>();
//		mAdapter = new ChatAdapter(this, mDatas, mChatUserBll);
//		mRvChatMessage.setAdapter(mAdapter);
		mRvChatMessage.setLayoutManager(mLayoutManager = new LinearLayoutManager(this,
						LinearLayoutManager
										.VERTICAL, false));
		mRvChatMessage.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1.0f)));
	}

	private void initEvent()
	{
		mBtnSend.setOnClickListener(this);
		mCbSelectPic.setOnCheckedChangeListener(this);
		mCbSelectEmoji.setOnCheckedChangeListener(this);
		mCbVoice.setOnCheckedChangeListener(this);
		mEtContent.setOnClickListener(this);
		mEtContent.setOnFocusChangeListener(this);
//		mEtContent.addTextChangedListener(new);
		//下拉加载以前的记录
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				if (mIsPrepare) mController.getHistoryMessageData();
			}
		});
		//语音按钮的长按事件
		mBtnRecorder.setOnAudioRecordFinishListener(new RecorderButton.OnAudioRecordFinishListener()
		{
			@Override
			public void onFinish(float seconds, String path)
			{
				if (!mIsPrepare)
				{
					Toast.makeText(ChatActivity.this, "骚等，还没加载完毕", Toast.LENGTH_SHORT).show();
					return;
				}
				File file = new File(path);
				try
				{
					AVIMAudioMessage message = new AVIMAudioMessage(file);
					message.setAttrs(initMessage(message));
					mAdapter.addData(message);
					mController.sendMessage(message);
					mLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param messages 获取到的message
	 * @param isNewly  是否是获取最新消息，主要是判断recyclerview要滑动到哪以及数据插入到哪
	 */
	public void getMessageSuccess(List<AVIMMessage> messages, AVIMConversation conversation, boolean
					isNewly)
	{
		mIsPrepare = true;
		if (isNewly)
		{
			mAdapter = new ChatAdapter(this, mDatas, conversation);
			mRvChatMessage.setAdapter(mAdapter);
			//获取最新消息的话数据插入到尾部并滑动到底部
			mAdapter.addDatasToBottom(messages);
			mLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);

		} else
		{
			//获取历史消息将消息插入到头部
			mAdapter.addDatasToHeader(messages);
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}

	public void getMessageDataFail(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		mSwipeRefreshLayout.setRefreshing(false);

	}

	public void sendMessageSuccess()
	{
		Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
	}

	public void sendMessageFail(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onClick(View v)
	{
		if (!mIsPrepare)
		{
			Toast.makeText(this, "骚等，还没加载完毕", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (v.getId())
		{
			case R.id.btnSend://发送按钮
				String content = mEtContent.getText().toString();
				AVIMTextMessage msg = new AVIMTextMessage();
				msg.setText(content);
				msg.setAttrs(initMessage(msg));
				mAdapter.addData(msg);
				mController.sendMessage(msg);
				mEtContent.setText("");
				mLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
				break;
			case R.id.etContent:
				hideAllBox();
				break;

		}
	}

	/**
	 * 接收到信息
	 */
	public void onEvent(EventReceiveMessage event)
	{
		mAdapter.addData(event.message);
		//当没添加数据前最后一项数据在可视范围内时才移动
		if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mDatas.size() - 2)
		{
			mLayoutManager.scrollToPosition(mDatas.size() - 1);
		} else
		{
			// TODO: 2016/1/31 显示未读消息、
		}
//		//同时更新一下 chatHomeActivity 的状态
//		EventBus.getDefault().post(new EventHomeConversationChange(false, event.conversation));
	}

	/**
	 * 选择照片后的回调
	 */
	public void onEvent(EventChatPic event)
	{
		mSelectPaths = event.paths;
		int count = mSelectPaths.size();
		for (int i = 0; i < count; i++)
		{
			try
			{
				AVIMImageMessage pic = new AVIMImageMessage(mSelectPaths.get(i));
				Map<String, Object> attrs = initMessage(pic);
				attrs.put(ChatConstant.KEY_IS_ORIGINAL, event.isSendOriginal);
				pic.setAttrs(attrs);
				mAdapter.addData(pic);
				mController.sendMessage(pic);
				mLayoutManager.scrollToPosition(mDatas.size() - 1);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton checkbox, boolean isChecked)
	{
		switch (checkbox.getId())
		{
			case R.id.cbSelectEmoji:
				if (isChecked)
				{
					hideOtherBox(mCbSelectEmoji);
					mFlEmojicons.setVisibility(View.VISIBLE);
					ScreenUtils.hideKeyboard(this);

				} else
				{
					mFlEmojicons.setVisibility(View.GONE);
				}
				break;
			case R.id.cbSelectPic:
				Intent intent = new Intent(this, MyGalleryActivity.class);
				intent.putExtra(MyGalleryActivity.EXTRA_LIMIT_COUNT, ChatConstant.CHAT_MAX_PIC_COUNT);
				intent.putExtra(MyGalleryActivity.EXTRA_SELECT_TYPE, MyGalleryActivity.TYPE_CHAT);
				startActivity(intent);
				break;
			case R.id.cbVoice:
				if (isChecked)
				{
					hideOtherBox(mCbVoice);
					ScreenUtils.hideKeyboard(this);
					mRlVoice.setVisibility(View.VISIBLE);

				} else
				{
					mRlVoice.setVisibility(View.GONE);
				}
				break;
		}
	}

	/**
	 * 其实 leancloud 发送消息的时候会自动为 message 赋这些值。
	 * 为什么我还要自己来赋值呢？因为拓麻的 observable 有延迟啊。
	 * <p/>
	 * 初始化一些信息，并返回属性map
	 */
	public Map<String, Object> initMessage(AVIMMessage message)
	{
		message.setTimestamp(System.currentTimeMillis());
		message.setFrom(AVImClientManager.getInstance().getClientId());
		Map<String, Object> attrs = new HashMap<>();
		AVFile avatar = AVUser.getCurrentUser(User.class).getAvatar();
		attrs.put(ChatConstant.MSG_ATTR_AVATAR, avatar != null ? avatar.getThumbnailUrl(true,
						Constant.AVATAR_WIDTH, Constant.AVATAR_HEIGHT) : null);
		return attrs;
	}

	/**
	 * 隐藏除指定之外的其他弹出框弹出框
	 */
	private void hideOtherBox(CompoundButton checkbox)
	{
		if (checkbox != mCbVoice)
			mCbVoice.setChecked(false);
		if (checkbox != mCbSelectEmoji)
			mCbSelectEmoji.setChecked(false);
	}

	/**
	 * 隐藏所有弹出框
	 */
	private void hideAllBox()
	{
		mCbVoice.setChecked(false);
		mCbSelectEmoji.setChecked(false);
	}

	private boolean isBoxShowing()
	{
		return mCbSelectEmoji.isChecked() || mCbVoice.isChecked();
	}

	/**
	 * 输入的文本内容改变的监听，
	 * 设置发送按钮是否可用
	 */
	@OnTextChanged(R.id.etContent)
	public void onContentChange()
	{
		if (mEtContent.getText().length() > 0)
			mBtnSend.setEnabled(true);
		else
			mBtnSend.setEnabled(false);
	}

	@Override
	public void onEmojiconBackspaceClicked(View view)
	{
		mCbSelectEmoji.setChecked(false);
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon)
	{
		String currContent = mEtContent.getText().toString();
		mEtContent.setText(currContent + emojicon.getEmoji());
		mEtContent.setSelection(mEtContent.getText().toString().length());
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if (v.getId() == R.id.etContent && hasFocus) hideAllBox();
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

	@Override
	protected void onPause()
	{
		super.onPause();
		AudioManager.getInstance().cancel();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, mHandler);
		NotificationUtils.removeTag(mConversationId);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (mHandler == null)
		{
			mHandler = new ChatHandler();
		}
		AVIMMessageManager.registerMessageHandler(AVIMMessage.class, mHandler);
	}


	@Override
	public void onBackPressed()
	{
		if (isBoxShowing())
			hideAllBox();
		else
			super.onBackPressed();
	}
}