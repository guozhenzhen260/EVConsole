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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.easivend.common.ToolClass;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class COMThread implements Runnable 
{
	//=====================快递柜类型==============================================================================
	public static final int EV_BENTO_CHECKALLCHILD = 1;	//快递柜全部查询	
	public static final int EV_BENTO_CHECKCHILD = 3;	//快递柜查询
		
	//=====================弹簧柜类型==============================================================================
	public static final int EV_COLUMN_CHECKALLCHILD = 40;	//弹簧柜全部查询
	public static final int EV_COLUMN_CHECKCHILD = 41;	//弹簧柜查询
	public static final int EV_COLUMN_OPENCHILD 	= 42;	//弹簧柜出货
	
	//=====================升降机柜类型==============================================================================
	public static final int EV_ELEVATOR_CHECKALLCHILD = 43;	//升降机柜全部查询
	public static final int EV_ELEVATOR_CHECKCHILD = 44;	//升降机柜查询
	public static final int EV_ELEVATOR_OPENCHILD 	= 45;	//升降机柜出货
	//=====================冰山柜类型==============================================================================
	public static final int VBOX_HUODAO_SET_INDALLCHILD 	= 46;	//全部补货
	public static final int VBOX_HUODAO_SET_INDCHILD 	= 47;	//单道补货
	//=====================各种类型按键信息==============================================================================
	public static final int EV_BUTTONRPT_GAME = 50;		//游戏按键
	public static final int EV_BUTTONRPT_HUODAO = 51;	//上报货道id
	public static final int EV_BUTTONRPT_SP = 52;	    //上报商品id
	public static final int EV_BUTTONRPT_RETURN = 53;	//退币按键
	public static final int EV_BUTTONRPT_MAINTAIN = 54;	//维护模式
	
	//=====================基础返回值==================================
	public static final int EV_CHECKALLMAIN	= 2;//所有货道全部查询返回
	public static final int EV_CHECKMAIN	= 8;	//货道查询返回
	public static final int EV_OPTMAIN	= 9;	//所有设备操作返回
	public static final int EV_BUTTONMAIN	= 7;	//所有按钮返回
	
	
	private Handler mainhand=null,childhand=null;
	int cabinet=0,column=0,opt=0;
	int devopt=0;//操作方法
	
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
				
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				devopt=msg.what;
				switch (msg.what)
				{				
				//格子柜
				/*查询类，返回值都是
				 * COMThread.EV_CHECKALLMAIN=2
				 * COMThread.EV_CHECKMAIN=8*/
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
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec6.toString(),"com.txt");
					
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
	  				tomain6.what=EV_CHECKALLMAIN;							
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
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec.toString(),"com.txt");
					
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
	  				tomain.what=EV_CHECKMAIN;							
	  				tomain.obj=allSet;
	  				mainhand.sendMessage(tomain); // 发送消息
					break;
				/*设备操作返回值，都是
				 * COMThread.EV_OPTMAIN=9
				 * 二级返回信息：
				 * 格子柜开门EVprotocol.EV_BENTO_OPEN=11
				 * EVprotocol.EV_BENTO_LIGHT=13
				 * EVprotocol.EV_BENTO_COOL=14
				 * EVprotocol.EV_BENTO_HOT=15*/	
				case EVprotocol.EV_BENTO_OPEN://子线程接收主线程格子开门
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
					//重试5次
					for(int i=0;i<5;i++)
					{
						String rec2=EVprotocol.EVBentoOpen(ToolClass.getBentcom_id(), cabinet,column);
						ToolClass.Log(ToolClass.INFO,"EV_COM",i+"API<<"+rec2.toString(),"com.txt");
	
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
									
									break;
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_BENTO_OPEN);
									allSet.put("addr", 0);//柜子地址
									allSet.put("box", 0);//格子地址 
									allSet.put("result", 0);	 
									ToolClass.ResstartPort(2);
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						    	}
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					//3.向主线程返回信息
	  				Message tomain2=mainhand.obtainMessage();
	  				tomain2.what=EV_OPTMAIN;							
	  				tomain2.obj=allSet;
	  				mainhand.sendMessage(tomain2); // 发送消息
					
					break;
				case EVprotocol.EV_BENTO_LIGHT://子线程接收主线程照明
					//1.得到信息
					JSONObject ev3=null;
					try {
						ev3 = new JSONObject(msg.obj.toString());
						cabinet=ev3.getInt("cabinet");
						opt=ev3.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2light=cabinet="+cabinet+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec3=EVprotocol.EVBentoLight(ToolClass.getBentcom_id(), cabinet,opt);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec3.toString(),"com.txt");

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
	  				tomain3.what=EV_OPTMAIN;							
	  				tomain3.obj=allSet;
	  				mainhand.sendMessage(tomain3); // 发送消息
					
					break;
				case EVprotocol.EV_BENTO_COOL://子线程接收主线程制冷
					//1.得到信息
					JSONObject ev4=null;
					try {
						ev4 = new JSONObject(msg.obj.toString());
						cabinet=ev4.getInt("cabinet");
						opt=ev4.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2cool=cabinet="+cabinet+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec4=EVprotocol.EVBentoCool(ToolClass.getBentcom_id(), cabinet,opt);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec4.toString(),"com.txt");

					//2.重新组包
					try {
						JSONObject jsonObject4 = new JSONObject(rec4); 
						//根据key取出内容
						JSONObject ev_head4 = (JSONObject) jsonObject4.getJSONObject("EV_json");
						int str_evType4 =  ev_head4.getInt("EV_type");
						if(str_evType4==EVprotocol.EV_BENTO_COOL)
						{
							if(ev_head4.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_COOL);
								allSet.put("addr", ev_head4.getInt("addr"));//柜子地址
								allSet.put("opt", ev_head4.getInt("opt"));//开还是关
								allSet.put("result", ev_head4.getInt("result"));								
					    	}
					    	else 
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_COOL);
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
	  				tomain4.what=EV_OPTMAIN;							
	  				tomain4.obj=allSet;
	  				mainhand.sendMessage(tomain4); // 发送消息
					break;	
				case EVprotocol.EV_BENTO_HOT://子线程接收主线程加热	
					//1.得到信息
					JSONObject ev5=null;
					try {
						ev5 = new JSONObject(msg.obj.toString());
						cabinet=ev5.getInt("cabinet");
						opt=ev5.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2hot=cabinet="+cabinet+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec5=EVprotocol.EVBentoHot(ToolClass.getBentcom_id(), cabinet,opt);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec5.toString(),"com.txt");

					//2.重新组包
					try {
						JSONObject jsonObject5 = new JSONObject(rec5); 
						//根据key取出内容
						JSONObject ev_head5 = (JSONObject) jsonObject5.getJSONObject("EV_json");
						int str_evType5 =  ev_head5.getInt("EV_type");
						if(str_evType5==EVprotocol.EV_BENTO_HOT)
						{
							if(ev_head5.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_HOT);
								allSet.put("addr", ev_head5.getInt("addr"));//柜子地址
								allSet.put("opt", ev_head5.getInt("opt"));//开还是关
								allSet.put("result", ev_head5.getInt("result"));								
					    	}
					    	else 
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_BENTO_HOT);
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
	  				tomain5.what=EV_OPTMAIN;							
	  				tomain5.obj=allSet;
	  				mainhand.sendMessage(tomain5); // 发送消息
					break;	
				//弹簧柜	
				/*查询类，返回值都是
				 * COMThread.EV_CHECKALLMAIN=2
				 * COMThread.EV_CHECKMAIN=8*/	
				case EV_COLUMN_CHECKALLCHILD://子线程接收主线程弹簧全部查询消息	
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
	  				tomain7.what=EV_CHECKALLMAIN;							
	  				tomain7.obj=allSet;
	  				mainhand.sendMessage(tomain7); // 发送消息
					break;
				case EV_COLUMN_CHECKCHILD://子线程接收主线程弹簧查询消息	
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
	  				tomain8.what=EV_CHECKMAIN;							
	  				tomain8.obj=allSet;
	  				mainhand.sendMessage(tomain8); // 发送消息
					break;
				/*设备操作返回值，都是
				 * COMThread.EV_OPTMAIN=9
				 * 二级返回信息：
				 * 开门EVprotocol.EV_COLUMN_OPEN=16*/	
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
					//重试5次
					for(int i=0;i<5;i++)
					{
						String rec9=EVprotocol.EVtrade(ToolClass.getColumncom_id(),1,cabinet,column,ToolClass.getGoc());
						ToolClass.Log(ToolClass.INFO,"EV_COM",i+"API<<"+rec9.toString(),"com.txt");
	
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
									
									break;
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_OPEN);
									allSet.put("addr", 0);//柜子地址
									allSet.put("box", 0);//格子地址
									allSet.put("result", ToolClass.colChuhuorst(11));
									ToolClass.ResstartPort(3);
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						    	}
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					//3.向主线程返回信息
	  				Message tomain9=mainhand.obtainMessage();
	  				tomain9.what=EV_OPTMAIN;							
	  				tomain9.obj=allSet;
	  				mainhand.sendMessage(tomain9); // 发送消息
					
					break;
					//升降机
				/*查询类，返回值都是
				 * COMThread.EV_CHECKALLMAIN=2
				 * COMThread.EV_CHECKMAIN=8*/	
				case EV_ELEVATOR_CHECKALLCHILD://子线程接收主线程升降机全部查询消息	
					//1.得到信息
					JSONObject ev21=null;
					try {
						ev21 = new JSONObject(msg.obj.toString());
						cabinet=ev21.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bentid="+ToolClass.getBentcom_id()+" cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Map<String, Integer> list21=ToolClass.ReadElevatorFile();				
										
					//2.重新组包
					//往接口回调信息
					allSet.clear();
					allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_CHECK);
					allSet.put("cool", 0);
					allSet.put("hot", 0);
					allSet.put("light", 0);
					allSet.putAll(list21);
					
					//3.向主线程返回信息
	  				Message tomain21=mainhand.obtainMessage();
	  				tomain21.what=EV_CHECKALLMAIN;							
	  				tomain21.obj=allSet;
	  				mainhand.sendMessage(tomain21); // 发送消息
					break;	
				case EV_ELEVATOR_CHECKCHILD://子线程接收主线程升降机查询消息	
					//1.得到信息
					JSONObject ev20=null;
					try {
						ev20 = new JSONObject(msg.obj.toString());
						cabinet=ev20.getInt("cabinet");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bentid="+ToolClass.getBentcom_id()+" cabinet="+cabinet,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Map<String, Integer> list20=ToolClass.ReadElevatorFile();				
										
					//2.重新组包
					//往接口回调信息
					allSet.clear();
					allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_CHECK);
					allSet.put("cool", 0);
					allSet.put("hot", 0);
					allSet.put("light", 0);
					//作排序
					Map<Integer, Integer> tempSet20= new TreeMap<Integer,Integer>();
					Set<Entry<String, Integer>> allmap20=list20.entrySet();  //实例化
			        Iterator<Entry<String, Integer>> iter20=allmap20.iterator();
			        while(iter20.hasNext())
			        {
			            Entry<String, Integer> me=iter20.next();
			            if(
			               (me.getKey().equals("EV_TYPE")!=true)
			            )   
			            {
			            	tempSet20.put(Integer.parseInt(me.getKey()), (Integer)me.getValue());
			            }
			        } 
			        
			        //输出内容
			        Set<Entry<Integer, Integer>> zhuhemap20=tempSet20.entrySet();  //实例化
			        Iterator<Entry<Integer, Integer>> zhuheiter20=zhuhemap20.iterator();
			        while(zhuheiter20.hasNext())
			        {
			            Entry<Integer, Integer> me=zhuheiter20.next();
			            if(
			               (me.getKey().equals("EV_TYPE")!=true)
			            )   
			            {
			            	allSet.put(me.getKey().toString(), me.getValue());
			            }
			        } 
					
					//3.向主线程返回信息
	  				Message tomain20=mainhand.obtainMessage();
	  				tomain20.what=EV_CHECKMAIN;							
	  				tomain20.obj=allSet;
	  				mainhand.sendMessage(tomain20); // 发送消息
					break;	
				/*设备操作返回值，都是
				 * COMThread.EV_OPTMAIN=9
				 * 二级返回信息：
				 * 开门EVprotocol.EV_COLUMN_OPEN=16*/		
				case EV_ELEVATOR_OPENCHILD://子线程接收主线程升降机出货
					//1.得到信息
					JSONObject ev22=null;
					try {
						ev22 = new JSONObject(msg.obj.toString());
						cabinet=ev22.getInt("cabinet");
						column=ev22.getInt("column");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cabinet="+cabinet+"column="+column,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//重试5次
					for(int i=0;i<5;i++)
					{
						String rec22=EVprotocol.EVtrade(ToolClass.getColumncom_id(),3,cabinet,column,ToolClass.getGoc());
						ToolClass.Log(ToolClass.INFO,"EV_COM",i+"API<<"+rec22.toString(),"com.txt");
	
						//2.重新组包
						try {
							JSONObject jsonObject22 = new JSONObject(rec22); 
							//根据key取出内容
							JSONObject ev_head22 = (JSONObject) jsonObject22.getJSONObject("EV_json");
							int str_evType22 =  ev_head22.getInt("EV_type");
							if(str_evType22==EVprotocol.EV_COLUMN_OPEN)
							{
								if(ev_head22.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_OPEN);
									allSet.put("addr", ev_head22.getInt("addr"));//柜子地址
									allSet.put("box", ev_head22.getInt("box"));//格子地址
									allSet.put("result", ToolClass.elevatorChuhuorst(ev_head22.getInt("result")));								
									
									break;
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_COLUMN_OPEN);
									allSet.put("addr", 0);//柜子地址
									allSet.put("box", 0);//格子地址
									allSet.put("result", ToolClass.elevatorChuhuorst(0x1F));
									ToolClass.ResstartPort(3);
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						    	}
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					//3.向主线程返回信息
	  				Message tomain22=mainhand.obtainMessage();
	  				tomain22.what=EV_OPTMAIN;							
	  				tomain22.obj=allSet;
	  				mainhand.sendMessage(tomain22); // 发送消息
					
					break;	
				//*************************************	
				//现金设备模块，值是使用EVprotocol包中，范围21-31
				//*************************************		
				case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能
					int bill=0;
					int coin=0;
					int opt=0;
					//1.得到信息
					JSONObject ev10=null;
					try {
						ev10 = new JSONObject(msg.obj.toString());
						bill=ev10.getInt("bill");
						coin=ev10.getInt("coin");
						opt=ev10.getInt("opt");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace(); 
					}
					//重试5次
					for(int i=0;i<5;i++)
					{
						String rec10=EVprotocol.EVmdbEnable(ToolClass.getCom_id(),bill,coin,opt);
						ToolClass.Log(ToolClass.INFO,"EV_COM",i+"API<<"+rec10.toString(),"com.txt");
						
						//2.重新组包
						try {
							JSONObject jsonObject10 = new JSONObject(rec10); 
							//根据key取出内容
							JSONObject ev_head10 = (JSONObject) jsonObject10.getJSONObject("EV_json");
							int str_evType10 =  ev_head10.getInt("EV_type");
							if(str_evType10==EVprotocol.EV_MDB_ENABLE)
							{
								if(ev_head10.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_MDB_ENABLE);
									allSet.put("opt", ev_head10.getInt("opt"));
									allSet.put("bill_result", ev_head10.getInt("bill_result"));
									allSet.put("coin_result", ev_head10.getInt("coin_result"));								
									break;
						    	}
						    	else
						    	{
						    		//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_MDB_ENABLE);
									allSet.put("opt", ev_head10.getInt("opt"));
						    		allSet.put("bill_result", 0);
									allSet.put("coin_result", 0);
									ToolClass.ResstartPort(1);
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) { 
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					//3.向主线程返回信息
	  				Message tomain10=mainhand.obtainMessage();
	  				tomain10.what=EV_OPTMAIN;							
	  				tomain10.obj=allSet;
	  				mainhand.sendMessage(tomain10); // 发送消息
					
					break;
				case EVprotocol.EV_MDB_B_INFO://子线程接收主线程现金设备
					//1.得到信息					
					String rec11=EVprotocol.EVmdbBillInfoCheck(ToolClass.getCom_id());
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec11.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject11 = new JSONObject(rec11); 
						//根据key取出内容
						JSONObject ev_head11 = (JSONObject) jsonObject11.getJSONObject("EV_json");
						int str_evType11 =  ev_head11.getInt("EV_type");
						if(str_evType11==EVprotocol.EV_MDB_B_INFO)
						{
							if(ev_head11.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_B_INFO);
								allSet.put("acceptor", ev_head11.getInt("acceptor"));
								allSet.put("dispenser", ev_head11.getInt("dispenser"));
								allSet.put("code", ev_head11.getString("code"));
								allSet.put("sn", ev_head11.getString("sn"));
								allSet.put("model", ev_head11.getString("model"));
								allSet.put("ver", ev_head11.getString("ver"));
								allSet.put("capacity", ev_head11.getInt("capacity"));
								JSONArray arr1=ev_head11.getJSONArray("ch_r");//返回json数组
								for(int i=0;i<arr1.length();i++)
								{
									JSONObject object2=arr1.getJSONObject(i);
									allSet.put("ch_r"+object2.getInt("ch"), object2.getInt("value"));								
								}
								
								JSONArray arr2=ev_head11.getJSONArray("ch_d");//返回json数组
								for(int i=0;i<arr2.length();i++)
								{
									JSONObject object2=arr2.getJSONObject(i);
									allSet.put("ch_d"+object2.getInt("ch"), object2.getInt("value"));								
								}								
					    	}
					    	else
					    	{
					    		//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_B_INFO);
								allSet.put("acceptor", 0);
								allSet.put("dispenser", 0);
								allSet.put("code", 0);
								allSet.put("sn", 0);
								allSet.put("model", 0);
								allSet.put("ver", 0);
								allSet.put("capacity", 0);
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain11=mainhand.obtainMessage();
	  				tomain11.what=EV_OPTMAIN;							
	  				tomain11.obj=allSet;
	  				mainhand.sendMessage(tomain11); // 发送消息
					
					break;
					//纸币配置	
				case EVprotocol.EV_MDB_B_CON:
					//1.得到信息
					JSONObject ev14=null;
					try {
						ev14 = new JSONObject(msg.obj.toString());
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2="+ev14,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec14=EVprotocol.EVmdbBillConfig(ev14.toString());
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+ev14.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject14 = new JSONObject(rec14); 
						//根据key取出内容
						JSONObject ev_head14 = (JSONObject) jsonObject14.getJSONObject("EV_json");
						int str_evType14 =  ev_head14.getInt("EV_type");
						if(str_evType14==EVprotocol.EV_MDB_B_CON)
						{
							if(ev_head14.getInt("is_success")>0)
					    	{
					    		allSet.clear();
					    		allSet.put("EV_TYPE", EVprotocol.EV_MDB_B_CON);
					    		allSet.put("acceptor", ev_head14.getInt("acceptor"));
					    		allSet.put("dispenser", ev_head14.getInt("dispenser"));
					    	}
					    	else
					    	{
					    		allSet.clear();
					    		allSet.put("EV_TYPE", EVprotocol.EV_MDB_B_CON);
					    		allSet.put("acceptor", 0);
					    		allSet.put("dispenser", 0);
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain14=mainhand.obtainMessage();
	  				tomain14.what=EV_OPTMAIN;							
	  				tomain14.obj=allSet;
	  				mainhand.sendMessage(tomain14); // 发送消息
					
					break;
				case EVprotocol.EV_MDB_C_INFO://子线程接收主线程现金设备
					//1.得到信息					
					String rec12=EVprotocol.EVmdbCoinInfoCheck(ToolClass.getCom_id());
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec12.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject12 = new JSONObject(rec12); 
						//根据key取出内容
						JSONObject ev_head12 = (JSONObject) jsonObject12.getJSONObject("EV_json");
						int str_evType12 =  ev_head12.getInt("EV_type");
						if(str_evType12==EVprotocol.EV_MDB_C_INFO)
						{
							if(ev_head12.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_C_INFO);
								allSet.put("acceptor", ev_head12.getInt("acceptor"));
								allSet.put("dispenser", ev_head12.getInt("dispenser"));
								allSet.put("code", ev_head12.getString("code"));
								allSet.put("sn", ev_head12.getString("sn"));
								allSet.put("model", ev_head12.getString("model"));
								allSet.put("ver", ev_head12.getString("ver"));
								allSet.put("capacity", ev_head12.getInt("capacity"));
								JSONArray arr1=ev_head12.getJSONArray("ch_r");//返回json数组
								for(int i=0;i<arr1.length();i++)
								{
									JSONObject object2=arr1.getJSONObject(i);
									allSet.put("ch_r"+object2.getInt("ch"), object2.getInt("value"));								
								}
								
								JSONArray arr2=ev_head12.getJSONArray("ch_d");//返回json数组
								for(int i=0;i<arr2.length();i++)
								{
									JSONObject object2=arr2.getJSONObject(i);
									allSet.put("ch_d"+object2.getInt("ch"), object2.getInt("value"));								
								}								
					    	}
					    	else
					    	{
					    		
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain12=mainhand.obtainMessage();
	  				tomain12.what=EV_OPTMAIN;							
	  				tomain12.obj=allSet;
	  				mainhand.sendMessage(tomain12); // 发送消息
					
					break;
					//硬币配置	
				case EVprotocol.EV_MDB_C_CON:
					//1.得到信息
					JSONObject ev15=null;
					try {
						ev15 = new JSONObject(msg.obj.toString());
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2="+ev15,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec15=EVprotocol.EVmdbCoinConfig(ev15.toString());
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+ev15.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject15 = new JSONObject(rec15); 
						//根据key取出内容
						JSONObject ev_head15 = (JSONObject) jsonObject15.getJSONObject("EV_json");
						int str_evType15 =  ev_head15.getInt("EV_type");
						if(str_evType15==EVprotocol.EV_MDB_C_CON)
						{
							if(ev_head15.getInt("is_success")>0)
					    	{
					    		allSet.clear();
					    		allSet.put("EV_TYPE", EVprotocol.EV_MDB_C_CON);
					    		allSet.put("acceptor", ev_head15.getInt("acceptor"));
					    		allSet.put("dispenser", ev_head15.getInt("dispenser"));
					    	}
					    	else
					    	{
					    		allSet.clear();
					    		allSet.put("EV_TYPE", EVprotocol.EV_MDB_C_CON);
					    		allSet.put("acceptor", 0);
					    		allSet.put("dispenser", 0);
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain15=mainhand.obtainMessage();
	  				tomain15.what=EV_OPTMAIN;							
	  				tomain15.obj=allSet;
	  				mainhand.sendMessage(tomain15); // 发送消息
					
					break;	
				case EVprotocol.EV_MDB_PAYOUT:
					int bill16=0;
					int coin16=0;
					int billPay16=0;
					int coinPay16=0;
					//1.得到信息
					JSONObject ev16=null;
					try {
						ev16 = new JSONObject(msg.obj.toString());
						bill16=ev16.getInt("bill");
						coin16=ev16.getInt("coin");
						billPay16=ev16.getInt("billPay");
						coinPay16=ev16.getInt("coinPay");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bill="+bill16+"coin="+coin16+"billPay="+billPay16+"coinPay="+coinPay16,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec16=EVprotocol.EVmdbPayout(ToolClass.getCom_id(),bill16,coin16,billPay16,coinPay16);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec16.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject16 = new JSONObject(rec16); 
						//根据key取出内容
						JSONObject ev_head16 = (JSONObject) jsonObject16.getJSONObject("EV_json");
						int str_evType16 =  ev_head16.getInt("EV_type");
						if(str_evType16==EVprotocol.EV_MDB_PAYOUT)
						{
							if(ev_head16.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYOUT);
								allSet.put("result", ev_head16.getInt("result"));
								allSet.put("bill_changed", ev_head16.getInt("bill_changed"));
								allSet.put("coin_changed", ev_head16.getInt("coin_changed"));
							}	
					    	else
					    	{
					    		//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYOUT);
								allSet.put("result", 0);
					    		allSet.put("bill_changed", 0);
								allSet.put("coin_changed", 0);								
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain16=mainhand.obtainMessage();
	  				tomain16.what=EV_OPTMAIN;							
	  				tomain16.obj=allSet;
	  				mainhand.sendMessage(tomain16); // 发送消息
					
					break;
				case EVprotocol.EV_MDB_HP_PAYOUT://Hopper找币接口
					int no17=0;
					int nums17=0;
					//1.得到信息
					JSONObject ev17=null;
					try {
						ev17 = new JSONObject(msg.obj.toString());
						no17=ev17.getInt("no");
						nums17=ev17.getInt("nums");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=no="+no17+"nums="+nums17,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec17=EVprotocol.EVmdbHopperPayout(ToolClass.getCom_id(),no17,nums17);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec17.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject17 = new JSONObject(rec17); 
						//根据key取出内容
						JSONObject ev_head17 = (JSONObject) jsonObject17.getJSONObject("EV_json");
						int str_evType17 =  ev_head17.getInt("EV_type");
						if(str_evType17==EVprotocol.EV_MDB_HP_PAYOUT)
						{
							if(ev_head17.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_HP_PAYOUT);
								allSet.put("result", ev_head17.getInt("result"));
								allSet.put("changed", ev_head17.getInt("changed"));								
					    	}
					    	else
					    	{
					    		//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_HP_PAYOUT);
								allSet.put("result", 0);
					    		allSet.put("changed", 0);					    		
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain17=mainhand.obtainMessage();
	  				tomain17.what=EV_OPTMAIN;							
	  				tomain17.obj=allSet;
	  				mainhand.sendMessage(tomain17); // 发送消息
					
					break;
				//交易页面使用	
				case EVprotocol.EV_MDB_HEART://子线程接收主线程现金设备
					//1.得到信息					
					String rec13=EVprotocol.EVmdbHeart(ToolClass.getCom_id());
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec13.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject13 = new JSONObject(rec13); 
						//根据key取出内容
						JSONObject ev_head13 = (JSONObject) jsonObject13.getJSONObject("EV_json");
						int str_evType13 =  ev_head13.getInt("EV_type");
						if(str_evType13==EVprotocol.EV_MDB_HEART)
						{
							if(ev_head13.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_HEART);
								allSet.put("bill_enable", ev_head13.getInt("bill_enable"));
								allSet.put("bill_payback", ev_head13.getInt("bill_payback"));
								allSet.put("bill_err", ev_head13.getInt("bill_err"));
								allSet.put("bill_recv", ev_head13.getInt("bill_recv"));
								allSet.put("bill_remain", ev_head13.getInt("bill_remain"));
								allSet.put("coin_enable", ev_head13.getInt("coin_enable"));
								allSet.put("coin_payback", ev_head13.getInt("coin_payback"));
								allSet.put("coin_err", ev_head13.getInt("coin_err"));
								allSet.put("coin_recv", ev_head13.getInt("coin_recv"));
								allSet.put("coin_remain", ev_head13.getInt("coin_remain"));
								JSONObject object2=ev_head13.getJSONObject("hopper");//返回json数组
								allSet.put("hopper1", object2.getInt("hopper1"));
								allSet.put("hopper2", object2.getInt("hopper2"));
								allSet.put("hopper3", object2.getInt("hopper3"));
								allSet.put("hopper4", object2.getInt("hopper4"));
								allSet.put("hopper5", object2.getInt("hopper5"));
								allSet.put("hopper6", object2.getInt("hopper6"));
								allSet.put("hopper7", object2.getInt("hopper7"));
								allSet.put("hopper8", object2.getInt("hopper8"));								
					    	}
					    	else
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_HEART);
								allSet.put("bill_enable", 0);
								allSet.put("bill_payback", 0);
								allSet.put("bill_err", 1);
								allSet.put("bill_recv", 0);
								allSet.put("bill_remain", 0);
								allSet.put("coin_enable", 0);
								allSet.put("coin_payback", 0);
								allSet.put("coin_err", 1);
								allSet.put("coin_recv", 0);
								allSet.put("coin_remain", 0);
								allSet.put("hopper1", 3);
								allSet.put("hopper2", 3);
								allSet.put("hopper3", 3);
								allSet.put("hopper4", 3);
								allSet.put("hopper5", 3);
								allSet.put("hopper6", 3);
								allSet.put("hopper7", 3);
								allSet.put("hopper8", 3);
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain13=mainhand.obtainMessage();
	  				tomain13.what=EV_OPTMAIN;							
	  				tomain13.obj=allSet;
	  				mainhand.sendMessage(tomain13); // 发送消息
					
					break;
				case EVprotocol.EV_MDB_COST:
					int cost18=0;
					//1.得到信息
					JSONObject ev18=null;
					try {
						ev18 = new JSONObject(msg.obj.toString());
						cost18=ev18.getInt("cost");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=cost="+cost18,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec18=EVprotocol.EVmdbCost(ToolClass.getCom_id(),cost18);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec18.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject18 = new JSONObject(rec18); 
						//根据key取出内容
						JSONObject ev_head18 = (JSONObject) jsonObject18.getJSONObject("EV_json");
						int str_evType18 =  ev_head18.getInt("EV_type");
						if(str_evType18==EVprotocol.EV_MDB_COST)
						{
							if(ev_head18.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_COST);
								allSet.put("result", ev_head18.getInt("result"));
								allSet.put("cost", ev_head18.getInt("cost"));
								allSet.put("bill_recv", ev_head18.getInt("bill_recv"));
								allSet.put("coin_recv", ev_head18.getInt("coin_recv"));
							}	
					    	else
					    	{
					    		//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_COST);
								allSet.put("result", 0);
					    		allSet.put("bill_recv", 0);
								allSet.put("coin_recv", 0);								
							}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain18=mainhand.obtainMessage();
	  				tomain18.what=EV_OPTMAIN;							
	  				tomain18.obj=allSet;
	  				mainhand.sendMessage(tomain18); // 发送消息
					
					break;
				case EVprotocol.EV_MDB_PAYBACK:
					int bill19=0;
					int coin19=0;
					//1.得到信息
					JSONObject ev19=null;
					try {
						ev19 = new JSONObject(msg.obj.toString());
						bill19=ev19.getInt("bill");
						coin19=ev19.getInt("coin");
						ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSend0.2=bill="+bill19+"coin="+coin19,"com.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String rec19=EVprotocol.EVmdbPayback(ToolClass.getCom_id(),bill19,coin19);
					ToolClass.Log(ToolClass.INFO,"EV_COM","API<<"+rec19.toString(),"com.txt");
					
					//2.重新组包
					try {
						JSONObject jsonObject19 = new JSONObject(rec19); 
						//根据key取出内容
						JSONObject ev_head19 = (JSONObject) jsonObject19.getJSONObject("EV_json");
						int str_evType19 =  ev_head19.getInt("EV_type");
						if(str_evType19==EVprotocol.EV_MDB_PAYBACK)
						{
							if(ev_head19.getInt("is_success")>0)
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYBACK);
								allSet.put("result", ev_head19.getInt("result"));
								allSet.put("bill_changed", ev_head19.getInt("bill_changed"));
								allSet.put("coin_changed", ev_head19.getInt("coin_changed"));	
					    	}
					    	else
					    	{
								//往接口回调信息
								allSet.clear();
								allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYBACK);
								allSet.put("result", 0);
								allSet.put("bill_changed", 0);
								allSet.put("coin_changed", 0);
					    	}
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//3.向主线程返回信息
	  				Message tomain19=mainhand.obtainMessage();
	  				tomain19.what=EV_OPTMAIN;							
	  				tomain19.obj=allSet;
	  				mainhand.sendMessage(tomain19); // 发送消息
					
					break;	
				default:
					break;
				}
			}
			
		};
		
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
	
	

}
