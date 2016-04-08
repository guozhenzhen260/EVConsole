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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
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
	//=====================弹簧柜类型==============================================================================
	public static final int EV_COLUMN_CHECKALLCHILD = 10;	//弹簧柜全部查询
	public static final int EV_COLUMN_CHECKCHILD = 11;	//弹簧柜查询
	public static final int EV_COLUMN_OPENCHILD 	= 12;	//弹簧柜出货
	
	//=====================现金设备==================================
	public static final int EV_MDB_ENABLE 	= 22;	//MDB设备使能
	public static final int EV_MDB_HEART 	= 23;	//MDB设备心跳
	public static final int EV_MDB_B_INFO 	= 24;	//MDB纸币器信息
	public static final int EV_MDB_C_INFO 	= 25;	//MDB硬币器信息
	public static final int EV_MDB_COST 	= 26;	//MDB设备扣款
	public static final int EV_MDB_PAYBACK = 27;	//MDB设备退币
	public static final int EV_MDB_PAYOUT 	= 28;	//MDB设备找币
	public static final int EV_MDB_B_CON 	= 29;	//MDB纸币器配置
	public static final int EV_MDB_C_CON 	= 30;	//MDB硬币器配置
	public static final int EV_MDB_HP_PAYOUT = 31;	//hopper硬币器找零
	
	
	private Handler mainhand=null,childhand=null;
	int cabinet=0,column=0,opt=0;
	int devopt=0;//操作方法
	int retry=0;
	private static Map<String,Object> allSet = new LinkedHashMap<String,Object>() ;
	
	
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
		//打开格子柜串口
		if(ToolClass.getBentcom().equals("")==false)
		{
			String bentcom = EVprotocol.EVPortRegister(ToolClass.getBentcom());
			ToolClass.Log(ToolClass.INFO,"EV_COM","Threadbentcom="+bentcom,"com.txt");
			ToolClass.setBentcom_id(Resetportid(bentcom));
		}
		//打开弹簧柜串口
		if(ToolClass.getColumncom().equals("")==false)
		{
			String columncom = EVprotocol.EVPortRegister(ToolClass.getColumncom());
			ToolClass.Log(ToolClass.INFO,"EV_COM","Threadcolumncom="+columncom,"com.txt");
			ToolClass.setColumncom_id(Resetportid(columncom));
		}
		
				
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				devopt=msg.what;
				switch (msg.what)
				{
				//格子柜
				case EV_BENTO_CHECKALLCHILD://子线程接收主线程格子查询消息		
					//1.得到信息
					JSONObject ev6=null;
					try {
						ev6 = new JSONObject(msg.obj.toString());
						cabinet=ev6.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bentid="+ToolClass.getBentcom_id()+" cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec6=EVprotocol.EVBentoCheck(ToolClass.getBentcom_id(), cabinet);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec6.toString(),"log.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject6 = new JSONObject(rec6); 
						//根据key取出内容
						JSONObject ev_head6 = (JSONObject) jsonObject6.getJSONObject("EV_json");
						int str_evType6 =  ev_head6.getInt("EV_type");
						if(str_evType6==EVprotocol.EV_BENTO_CHECK)
						{
							if(ev_head6.getInt("is_success")>0)
							{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_CHECK);
								allSet.put("cool", ev_head6.getInt("cool"));
								allSet.put("hot", ev_head6.getInt("hot"));
								allSet.put("light", ev_head6.getInt("light"));
								JSONArray arr6=ev_head6.getJSONArray("column");//返回json数组
								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
								for(int i=0;i<arr6.length();i++)
								{
									JSONObject object2=arr6.getJSONObject(i);
									allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("state"));								
								}
								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道3:"+allSet.toString());								
							}
							else
							{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_CHECK);
								allSet.put("cool", 0);
								allSet.put("hot", 0);
								allSet.put("light", 0);
//								JSONArray arr=ev_head.getJSONArray("column");//返回json数组
//								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
//								for(int i=0;i<arr.length();i++)
//								{
//									JSONObject object2=arr.getJSONObject(i);
//									allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("state"));								
//								}
								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道3:"+allSet.toString());								
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain6=mainhand.obtainMessage();
	  				tomain6.what=EV_BENTO_CHECKALLMAIN;							
	  				tomain6.obj=allSet;
	  				mainhand.sendMessage(tomain6); // 发送消息
					break;
				case EV_BENTO_CHECKCHILD://子线程接收主线程格子查询消息		
					//1.得到信息
					JSONObject ev=null;
					try {
						ev = new JSONObject(msg.obj.toString());
						cabinet=ev.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bentid="+ToolClass.getBentcom_id()+" cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec=EVprotocol.EVBentoCheck(ToolClass.getBentcom_id(), cabinet);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec.toString(),"log.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject = new JSONObject(rec); 
						//根据key取出内容
						JSONObject ev_head = (JSONObject) jsonObject.getJSONObject("EV_json");
						int str_evType =  ev_head.getInt("EV_type");
						if(str_evType==EVprotocol.EV_BENTO_CHECK)
						{
							if(ev_head.getInt("is_success")>0)
							{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_CHECK);
								allSet.put("cool", ev_head.getInt("cool"));
								allSet.put("hot", ev_head.getInt("hot"));
								allSet.put("light", ev_head.getInt("light"));
								JSONArray arr=ev_head.getJSONArray("column");//返回json数组
								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
								for(int i=0;i<arr.length();i++)
								{
									JSONObject object2=arr.getJSONObject(i);
									allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("state"));								
								}
								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道3:"+allSet.toString());								
							}
							else
							{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_CHECK);
								allSet.put("cool", 0);
								allSet.put("hot", 0);
								allSet.put("light", 0);
//								JSONArray arr=ev_head.getJSONArray("column");//返回json数组
//								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
//								for(int i=0;i<arr.length();i++)
//								{
//									JSONObject object2=arr.getJSONObject(i);
//									allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("state"));								
//								}
								//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道3:"+allSet.toString());								
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain=mainhand.obtainMessage();
	  				tomain.what=EV_BENTO_CHECKMAIN;							
	  				tomain.obj=allSet;
	  				mainhand.sendMessage(tomain); // 发送消息
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
					String rec2=EVprotocol.EVBentoOpen(ToolClass.getBentcom_id(), cabinet,column);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec2.toString(),"log.txt");

					//2.重新组包
					try {
						JSONObject jsonObject2 = new JSONObject(rec2); 
						//根据key取出内容
						JSONObject ev_head2 = (JSONObject) jsonObject2.getJSONObject("EV_json");
						int str_evType2 =  ev_head2.getInt("EV_type");
						if(str_evType2==EVprotocol.EV_BENTO_OPEN)
						{
							if(ev_head2.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_OPEN);
								allSet.put("addr", ev_head2.getInt("addr"));//柜子地址
								allSet.put("box", ev_head2.getInt("box"));//格子地址
								allSet.put("result", ev_head2.getInt("result"));								
					    	}
					    	else
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_OPEN);
								allSet.put("addr", 0);//柜子地址
								allSet.put("box", 0);//格子地址
								allSet.put("result", 0);								
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain2=mainhand.obtainMessage();
	  				tomain2.what=EV_BENTO_OPTMAIN;							
	  				tomain2.obj=allSet;
	  				mainhand.sendMessage(tomain2); // 发送消息
					
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
					String rec3=EVprotocol.EVBentoLight(ToolClass.getBentcom_id(), cabinet,opt);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec3.toString(),"log.txt");

					//2.重新组包
					try {
						JSONObject jsonObject3 = new JSONObject(rec3); 
						//根据key取出内容
						JSONObject ev_head3 = (JSONObject) jsonObject3.getJSONObject("EV_json");
						int str_evType3 =  ev_head3.getInt("EV_type");
						if(str_evType3==EVprotocol.EV_BENTO_LIGHT)
						{
							if(ev_head3.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_LIGHT);
								allSet.put("addr", ev_head3.getInt("addr"));//柜子地址
								allSet.put("opt", ev_head3.getInt("opt"));//开还是关
								allSet.put("result", ev_head3.getInt("result"));								
					    	}
					    	else 
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_LIGHT);
								allSet.put("addr", 0);//柜子地址
								allSet.put("opt", 0);//开还是关
								allSet.put("result", 0);								
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain3=mainhand.obtainMessage();
	  				tomain3.what=EV_BENTO_OPTMAIN;							
	  				tomain3.obj=allSet;
	  				mainhand.sendMessage(tomain3); // 发送消息
					
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
					String rec4=EVprotocol.EVBentoLight(ToolClass.getBentcom_id(), cabinet,opt);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec4.toString(),"log.txt");

					//2.重新组包
					try {
						JSONObject jsonObject4 = new JSONObject(rec4); 
						//根据key取出内容
						JSONObject ev_head4 = (JSONObject) jsonObject4.getJSONObject("EV_json");
						int str_evType4 =  ev_head4.getInt("EV_type");
						if(str_evType4==EVprotocol.EV_BENTO_LIGHT)
						{
							if(ev_head4.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_LIGHT);
								allSet.put("addr", ev_head4.getInt("addr"));//柜子地址
								allSet.put("opt", ev_head4.getInt("opt"));//开还是关
								allSet.put("result", ev_head4.getInt("result"));								
					    	}
					    	else 
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_LIGHT);
								allSet.put("addr", 0);//柜子地址
								allSet.put("opt", 0);//开还是关
								allSet.put("result", 0);								
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain4=mainhand.obtainMessage();
	  				tomain4.what=EV_BENTO_OPTMAIN;							
	  				tomain4.obj=allSet;
	  				mainhand.sendMessage(tomain4); // 发送消息
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
					String rec5=EVprotocol.EVBentoLight(ToolClass.getBentcom_id(), cabinet,opt);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec5.toString(),"log.txt");

					//2.重新组包
					try {
						JSONObject jsonObject5 = new JSONObject(rec5); 
						//根据key取出内容
						JSONObject ev_head5 = (JSONObject) jsonObject5.getJSONObject("EV_json");
						int str_evType5 =  ev_head5.getInt("EV_type");
						if(str_evType5==EVprotocol.EV_BENTO_LIGHT)
						{
							if(ev_head5.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_LIGHT);
								allSet.put("addr", ev_head5.getInt("addr"));//柜子地址
								allSet.put("opt", ev_head5.getInt("opt"));//开还是关
								allSet.put("result", ev_head5.getInt("result"));								
					    	}
					    	else 
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_LIGHT);
								allSet.put("addr", 0);//柜子地址
								allSet.put("opt", 0);//开还是关
								allSet.put("result", 0);								
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain5=mainhand.obtainMessage();
	  				tomain5.what=EV_BENTO_OPTMAIN;							
	  				tomain5.obj=allSet;
	  				mainhand.sendMessage(tomain5); // 发送消息
					break;	
				//弹簧柜	
				case EV_COLUMN_CHECKALLCHILD://子线程接收主线程格子查询消息	
					//1.得到信息
					JSONObject ev7=null;
					try {
						ev7 = new JSONObject(msg.obj.toString());
						cabinet=ev7.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bentid="+ToolClass.getBentcom_id()+" cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Map<String, Integer> list=ToolClass.ReadColumnFile();				
										
					//2.重新组包
					//往接口回调信息
					allSet.clear();
					allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_CHECK);
					allSet.put("cool", 0);
					allSet.put("hot", 0);
					allSet.put("light", 0);
					allSet.putAll(list);
					
					//3.向主线程返回信息
	  				Message tomain7=mainhand.obtainMessage();
	  				tomain7.what=EV_BENTO_CHECKALLMAIN;							
	  				tomain7.obj=allSet;
	  				mainhand.sendMessage(tomain7); // 发送消息
					break;
				case EV_COLUMN_CHECKCHILD://子线程接收主线程格子查询消息	
					//1.得到信息
					JSONObject ev8=null;
					try {
						ev8 = new JSONObject(msg.obj.toString());
						cabinet=ev8.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bentid="+ToolClass.getBentcom_id()+" cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Map<String, Integer> list8=ToolClass.ReadColumnFile();				
										
					//2.重新组包
					//往接口回调信息
					allSet.clear();
					allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_CHECK);
					allSet.put("cool", 0);
					allSet.put("hot", 0);
					allSet.put("light", 0);
					//作排序
					Map<Integer, Integer> tempSet8= new TreeMap<Integer,Integer>();
					Set<Entry<String, Integer>> allmap8=list8.entrySet();  //实例化
			        Iterator<Entry<String, Integer>> iter8=allmap8.iterator();
			        while(iter8.hasNext())
			        {
			            Entry<String, Integer> me=iter8.next();
			            if(
			               (me.getKey().equals("EV_TYPE")!=true)
			            )   
			            {
			            	tempSet8.put(Integer.parseInt(me.getKey()), (Integer)me.getValue());
			            }
			        } 
			        
			        //输出内容
			        Set<Entry<Integer, Integer>> zhuhemap8=tempSet8.entrySet();  //实例化
			        Iterator<Entry<Integer, Integer>> zhuheiter8=zhuhemap8.iterator();
			        while(zhuheiter8.hasNext())
			        {
			            Entry<Integer, Integer> me=zhuheiter8.next();
			            if(
			               (me.getKey().equals("EV_TYPE")!=true)
			            )   
			            {
			            	allSet.put(me.getKey().toString(), me.getValue());
			            }
			        } 
					
					//3.向主线程返回信息
	  				Message tomain8=mainhand.obtainMessage();
	  				tomain8.what=EV_BENTO_CHECKMAIN;							
	  				tomain8.obj=allSet;
	  				mainhand.sendMessage(tomain8); // 发送消息
					break;
				case EV_COLUMN_OPENCHILD://子线程接收主线程弹簧出货
					//1.得到信息
					JSONObject ev9=null;
					try {
						ev9 = new JSONObject(msg.obj.toString());
						cabinet=ev9.getInt("cabinet");
						column=ev9.getInt("column");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet+"column="+column,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec9=EVprotocol.EVtrade(ToolClass.getColumncom_id(),1,cabinet,column,ToolClass.getGoc());
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec9.toString(),"log.txt");

					//2.重新组包
					try {
						JSONObject jsonObject9 = new JSONObject(rec9); 
						//根据key取出内容
						JSONObject ev_head9 = (JSONObject) jsonObject9.getJSONObject("EV_json");
						int str_evType9 =  ev_head9.getInt("EV_type");
						if(str_evType9==EVprotocol.EV_COLUMN_OPEN)
						{
							if(ev_head9.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_OPEN);
								allSet.put("addr", ev_head9.getInt("addr"));//柜子地址
								allSet.put("box", ev_head9.getInt("box"));//格子地址
								allSet.put("result", ToolClass.colChuhuorst(ev_head9.getInt("result")));								
					    	}
					    	else
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_OPEN);
								allSet.put("addr", 0);//柜子地址
								allSet.put("box", 0);//格子地址
								allSet.put("result", ToolClass.colChuhuorst(11));							
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain9=mainhand.obtainMessage();
	  				tomain9.what=EV_BENTO_OPTMAIN;							
	  				tomain9.obj=allSet;
	  				mainhand.sendMessage(tomain9); // 发送消息
					
					break;	
				default:
					break;
				}
			}
			
		};
		
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
	
	//提取出portid
	private int Resetportid(String bentcom)
	{
		int bentcom_id=0;
		//2.重新组包
		try {
			JSONObject jsonObject = new JSONObject(bentcom); 
			//根据key取出内容
			JSONObject ev_head = (JSONObject) jsonObject.getJSONObject("EV_json");
			int str_evType =  ev_head.getInt("EV_type");
			if(str_evType==EVprotocol.EV_REGISTER)
			{
				bentcom_id=ev_head.getInt("port_id");				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bentcom_id;
	}

}
