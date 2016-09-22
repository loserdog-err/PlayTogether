package com.chenantao.playtogether.gallery;

/**
 * Created by chenantao on 2015/12/11.
 */
public class FolderBean
{
	private String firstImage;//第一张图片的路径
	private String dir;
	private String name;
	private int imageCount;

	public FolderBean()
	{
	}

	public FolderBean(String firstImage, String dir, int imageCount)
	{
		this.firstImage = firstImage;
		this.dir = dir;
		this.name = dir.substring(dir.lastIndexOf("/") + 1);
		this.imageCount = imageCount;
	}

	public String getFirstImage()
	{
		return firstImage;
	}

	public void setFirstImage(String firstImage)
	{
		this.firstImage = firstImage;
	}

	public String getDir()
	{
		return dir;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
	}

	public String getName()
	{
		return name;
	}


	public int getImageCount()
	{
		return imageCount;
	}

	public void setImageCount(int imageCount)
	{
		this.imageCount = imageCount;
	}
}
