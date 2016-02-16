package com.chenantao.playtogether.chat.event;

import java.util.List;

/**
 * Created by Chenantao_gg on 2016/2/3.
 */
public class EventChatPic
{
	public List<String> paths;

	public boolean isSendOriginal;

	public EventChatPic(List<String> paths,boolean isSendOriginal)
	{
		this.paths = paths;
		this.isSendOriginal = isSendOriginal;
	}
}
