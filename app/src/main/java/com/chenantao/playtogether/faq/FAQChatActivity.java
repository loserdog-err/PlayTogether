package com.chenantao.playtogether.faq;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.chenantao.playtogether.R;
import com.chenantao.playtogether.utils.HttpUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chean_antao on 2015/8/16.
 * 常见问题的activity
 */
public class FAQChatActivity extends Activity
{
	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String API_KEY = "dd763102b6013f48bb47ec2533e0f4ac";


	private ListView mMsgs;
	private ChatMessageAdapter mAdapter;
	private List<ChatMessage> mDatas;

	private EditText mInputMsg;
	private Button mSendMsg;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			ChatMessage fromMessage = (ChatMessage) msg.obj;
			mDatas.add(fromMessage);
			mAdapter.notifyDataSetChanged();
			mMsgs.setSelection(mDatas.size() - 1);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_faq_chat_ui);
		initView();
		initDatas();
		initListener();
	}

	private void initListener()
	{
		mSendMsg.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final String toMsg = mInputMsg.getText().toString();
				if (TextUtils.isEmpty(toMsg))
				{
					Toast.makeText(FAQChatActivity.this, "信息不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				ChatMessage toMessage = new ChatMessage();
				toMessage.setDate(new Date());
				toMessage.setMsg(toMsg);
				toMessage.setType(ChatMessage.Type.OUTCOMING);
				mDatas.add(toMessage);
				mAdapter.notifyDataSetChanged();
				mMsgs.setSelection(mDatas.size() - 1);
				mInputMsg.setText("");
				new Thread()
				{
					public void run()
					{
						ChatMessage fromMessage = sendMessage(toMsg);
						Message m = Message.obtain();
						m.obj = fromMessage;
						mHandler.sendMessage(m);
					}

				}.start();

			}
		});
	}

	private void initDatas()
	{
		mDatas = new ArrayList<ChatMessage>();
		mDatas.add(new ChatMessage("您好，我是问问，您可以尽情的调戏我哦。", ChatMessage.Type.INCOMING, new Date()));
		mAdapter = new ChatMessageAdapter(this, mDatas);
		mMsgs.setAdapter(mAdapter);
	}

	private void initView()
	{
		mMsgs = (ListView) findViewById(R.id.lv_content);
		mInputMsg = (EditText) findViewById(R.id.et_send_content);
		mSendMsg = (Button) findViewById(R.id.btn_send);
	}

	private static String setParams(String msg)
	{
		String url = "";
		try
		{
			url = URL + "?key=" + API_KEY + "&info="
					+ URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return url;
	}

	public static ChatMessage sendMessage(String msg)
	{
		ChatMessage chatMessage = new ChatMessage();
		String jsonRes = null;
		try
		{
			jsonRes = HttpUtils.get(setParams(msg));
			Gson gson = new Gson();
			Result result = gson.fromJson(jsonRes, Result.class);
			chatMessage.setMsg(result.getText());

		} catch (Exception e)
		{
			e.printStackTrace();
			chatMessage.setMsg("发送失败");
		}
		chatMessage.setDate(new Date());
		chatMessage.setType(ChatMessage.Type.INCOMING);
		return chatMessage;
	}

}
