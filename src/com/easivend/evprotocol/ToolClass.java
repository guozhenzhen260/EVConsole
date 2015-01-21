/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           ToolClass.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        工具类，这里面存放的主要是static函数，static成员，统一作为全局变量和全局函数用       
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/


package com.easivend.evprotocol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.R.integer;
import android.os.Handler;
import android.util.Log;

public class ToolClass 
{
	public final static int VERBOSE=0;
	public final static int DEBUG=1;
	public final static int INFO=2;
	public final static int WARN=3;
	public final static int ERROR=4;
	
	//解析Map对象<String,Object>的数据集合
	public static Map<String, Object> getMapListgson(String jsonStr)
	{
		Map<String, Object> list=new HashMap<String,Object>();
		try {
			JSONObject object=new JSONObject(jsonStr);//{"EV_json":{"EV_type":"EV_STATE_RPT","state":2}}
			JSONObject perobj=object.getJSONObject("EV_json");//{"EV_type":"EV_STATE_RPT","state":2}
			Gson gson=new Gson();
			list=gson.fromJson(perobj.toString(), new TypeToken<Map<String, Object>>(){}.getType());
			//Log.i("EV_JNI",perobj.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	
	//Log方法，用于打印log，和在文本文件中打印操作日志
	public static void Log(int info,String tag,String str)
	{
		switch(info)
		{
			case VERBOSE:
				Log.v(tag,str);
				break;
			case DEBUG:
				Log.d(tag,str);
				break;
			case INFO:
				Log.i(tag,str);
				break;
			case WARN:
				Log.w(tag,str);
				break;
			case ERROR:
				Log.e(tag,str);
				break;	
		}		
	}
	
	//发送金额函数，浮点的元,转为以分为单位发送到底下
	public static long MoneySend(float sendMoney)
	{
		long values=(long)(sendMoney*100);
		return values;
	}
	
	//接收金额转换函数，接收的分,转为浮点的元
	public static float MoneyRec(long Money)
	{
		float amount = (float)(Money/100);
		return amount;
	}
	
}
