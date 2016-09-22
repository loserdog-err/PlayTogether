package com.chenantao.playtogether.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by chenantao on 2015/12/12.
 */
public class HttpUtils
{
	public static String get(String url)
	{
		try
		{
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Charset", "UTF-8");
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String response = "";
			String readLine = "";
			while ((readLine = br.readLine()) != null)
			{
				response += readLine;
			}
			br.close();
			return response;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 得到字节数组
	 *
	 * @param url
	 * @return
	 */
	public static byte[] getBytes(String url)
	{
		try
		{
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Charset", "UTF-8");
			InputStream is = connection.getInputStream();
			int contentLength = connection.getContentLength();
			byte[] data = new byte[contentLength];
			int alreadyRead = 0;
			int len = 0;
			while (alreadyRead < contentLength)
			{
				len = is.read(data, alreadyRead, contentLength - alreadyRead);
				alreadyRead += len;
			}
			is.close();
			return data;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * post请求
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, String> params)
	{
		try
		{
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			PrintWriter pw = new PrintWriter(connection.getOutputStream());
			StringBuilder sbParams = new StringBuilder();
			if (params != null)
			{
				for (String key : params.keySet())
				{
					sbParams.append(key + "=" + params.get(key) + "&");
				}
			}
			if (sbParams.length() > 0)
			{
				String strParams = sbParams.substring(0, sbParams.length() - 1);
				Log.e("cat", "strParams:" + strParams);
				pw.write(strParams);
				pw.flush();
				pw.close();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			StringBuffer response = new StringBuffer();
			String readLine = "";
			while ((readLine = br.readLine()) != null)
			{
				response.append(readLine);
			}
			br.close();
			return response.toString();
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
