package com.chenantao.playtogether.chat.utils;

/**
 * Created by Chenantao_gg on 2016/1/30.
 */
public class ChatConstant
{
	public static final String KEY_CHAT_TYPE = "chatType";//聊天类型，是单聊还是群聊
	public static final int TYPE_SINGLE_CHAT = 0;//单聊
	public static final int TYPE_GROUP_CHAT = 1;//群聊


	public static final String MSG_ATTR_AVATAR = "avatar";

	public static final long CHAT_SHOW_TIME_INTERVAL = 1000 * 60 * 5;//超过5分钟就得显示时间了

	public static final int CHAT_PAGE_SIZE = 20;//聊天页面每页显示的消息数量


	public static final String CONVERSATION_ID = "conversationId";
	public static final String MEMBER_ID = "memberId";
	public static final String CONVERSATION_NAME = "conversationName";

	public static final int CHAT_HOME_CONVERSATION_COUNT = 15;//聊天主页显示的conversation的最大数量

	public static final int CHAT_MAX_PIC_COUNT = 5;//聊天界面每次最多只能发送5张图片

	//聊天消息的图片所能占屏幕最多的宽或者高的比率
	public static final double MESSAGE_PIC_WIDTH_MAX_RATIO = 0.4;
	public static final double MESSAGE_PIC_HEIGHT_MAX_RATIO = 0.4;

	public static final String KEY_IS_ORIGINAL = "isOriginal";//聊天的时候是否发送原图

}
