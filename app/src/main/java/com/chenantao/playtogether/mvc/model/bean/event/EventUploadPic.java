package com.chenantao.playtogether.mvc.model.bean.event;

import java.util.List;

/**
 * Created by Chenantao_gg on 2016/1/23.
 */
public class EventUploadPic
{
	public List<String> paths;

	public EventUploadPic(List<String> paths)
	{
		this.paths = paths;
	}
}
