/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           COMSerial.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        与串口设备连接的线程
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.evprotocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.http.EVServerhttp;
import com.google.gson.Gson;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class COMThread implements Runnable 
{
	//=====================快递柜类型==============================================================================
	public static final int EV_BENTO_CHECKALLCHILD = 1;	//快递柜全部查询
	public static final int EV_BENTO_CHECKALLMAIN	= 2;//快递柜全部查询返回
	public static final int EV_BENTO_CHECKCHILD = 3;	//快递柜查询
	public static final int EV_BENTO_OPENCHILD 	= 4;	//快递柜开门
	public static final int EV_BENTO_LIGHTCHILD = 5;	//快递柜照明
	public static final int EV_BENTO_COOLCHILD 	= 6;	//快递柜制冷
	public static final int EV_BENTO_HOTCHILD 	= 7;	//快递柜加热
	
	public static final int EV_BENTO_CHECKMAIN	= 8;	//快递柜查询返回
	public static final int EV_BENTO_OPTMAIN	= 9;	//快递柜操作返回
	
	private Handler mainhand=null,childhand=null;
	int cabinet=0,column=0,opt=0;
	FileOutputStream bentOutputStream;  
    FileInputStream bentInputStream;      
	int devopt=0;//操作方法
	int retry=0;
	boolean comrecv=false;//true表示串口已经接收完成
	boolean issend=false;
    
	public COMThread(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		ToolClass.Log(ToolClass.INFO,"EV_COM","Thread start["+Thread.currentThread().getId()+"]","com.txt");
//		//打开格子柜串口
//		if(ToolClass.getBentcom()!=null)
//		{
//			try {  
//				sp=new SerialPort(new File(ToolClass.getBentcom()),9600,0); 
//				bentInputStream=(FileInputStream) sp.getInputStream();
//				bentOutputStream=(FileOutputStream) sp.getOutputStream();
//	        } catch (SecurityException e) {  
//	            // TODO Auto-generated catch block  
//	            e.printStackTrace();  
//	        } catch (IOException e) {  
//	            // TODO Auto-generated catch block  
//	            e.printStackTrace();  
//	        }
//		}
				
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				devopt=msg.what;
				switch (msg.what)
				{
				case EV_BENTO_CHECKALLCHILD://子线程接收主线程格子查询消息		
//					//1.得到信息
//					JSONObject ev6=null;
//					try {
//						ev6 = new JSONObject(msg.obj.toString());
//						cabinet=ev6.getInt("cabinet");
//						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet,"com.txt");
//					} catch (JSONException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend=CHECKALL","com.txt");
					//向主线程返回信息
	  				Message tomain=mainhand.obtainMessage();
	  				tomain.what=EV_BENTO_CHECKALLMAIN;							
	  				tomain.obj="";
	  				mainhand.sendMessage(tomain); // 发送消息
					
					break;
				case EV_BENTO_CHECKCHILD://子线程接收主线程格子查询消息		
					//1.得到信息
					JSONObject ev=null;
					try {
						ev = new JSONObject(msg.obj.toString());
						cabinet=ev.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					break;
				case EV_BENTO_OPENCHILD://子线程接收主线程格子开门
					//1.得到信息
					JSONObject ev2=null;
					try {
						ev2 = new JSONObject(msg.obj.toString());
						cabinet=ev2.getInt("cabinet");
						column=ev2.getInt("column");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet+"column="+column,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					break;
				case EV_BENTO_LIGHTCHILD://子线程接收主线程照明
					//1.得到信息
					JSONObject ev3=null;
					try {
						ev3 = new JSONObject(msg.obj.toString());
						cabinet=ev3.getInt("cabinet");
						opt=ev3.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					break;
				case EV_BENTO_COOLCHILD://子线程接收主线程制冷
					//1.得到信息
					JSONObject ev4=null;
					try {
						ev4 = new JSONObject(msg.obj.toString());
						cabinet=ev4.getInt("cabinet");
						opt=ev4.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					break;	
				case EV_BENTO_HOTCHILD://子线程接收主线程加热	
					//1.得到信息
					JSONObject ev5=null;
					try {
						ev5 = new JSONObject(msg.obj.toString());
						cabinet=ev5.getInt("cabinet");
						opt=ev5.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					break;		
				default:
					break;
				}
			}
			
		};
		
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
	
	

}
