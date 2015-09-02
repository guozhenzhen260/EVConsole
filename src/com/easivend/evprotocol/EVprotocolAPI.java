/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocolAPI.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        与JNI接口通信API层          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.evprotocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocol.EV_listener;
import com.easivend.evprotocol.EVprotocol.RequestObject;
import com.easivend.http.EVServerhttp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EVprotocolAPI 
{
	//static EVprotocol ev=null;
	private static JNIInterface callBack=null;//与activity交互注册回调
	private static Call_json callJson = new Call_json();//与JNI交互注册回调	
	private static Map<String,Object> allSet = new LinkedHashMap<String,Object>() ;
	private static int EV_TYPE=0;	
	public static final int EV_INITING=1;//正在初始化
	public static final int EV_ONLINE=2;//成功连接
	public static final int EV_OFFLINE=3;//断开连接
	public static final int EV_RESTART=4;//主控板重启心动
	public static final int EV_TRADE_RPT=5;//出货返回
	public static final int EV_COLUMN_RPT=6;//货道状态上报
	public static final int EV_PAYIN_RPT=7;//投入纸币
	public static final int EV_PAYOUT_RPT=8;//找零结果
	public static final int EV_STATE_RPT=9;//初始化完成
	
	public static final int EV_NONE 		= 0;	//保留类型
	public static final int EV_REGISTER 	= 1;	//串口注册
	public static final int EV_RELEASE 	= 2;	//串口释放
	
	//=====================快递柜类型==============================================================================
	public static final int EV_BENTO_OPEN 	= 11;	//快递柜开门
	public static final int EV_BENTO_CHECK = 12;	//快递柜查询
	public static final int EV_BENTO_LIGHT = 13;	//快递柜照明
	public static final int EV_BENTO_COOL 	= 14;	//快递柜制冷
	public static final int EV_BENTO_HOT 	= 15;	//快递柜加热
	
	//=====================MDB现金模组类型==============================================================================
	public static final int EV_MDB_INIT 	= 21;	//MDB设备初始化
	public static final int EV_MDB_ENABLE 	= 22;	//MDB设备使能
	public static final int EV_MDB_HEART 	= 23;	//MDB设备心跳
	public static final int EV_MDB_B_INFO 	= 24;	//MDB纸币器信息
	public static final int EV_MDB_C_INFO 	= 25;	//MDB硬币器信息
	public static final int EV_MDB_COST 	= 26;	//MDB设备扣款
	public static final int EV_MDB_PAYBACK = 27;	//MDB设备退币
	public static final int EV_MDB_PAYOUT 	= 28;	//MDB设备找币
	public static final int EV_MDB_B_CON 	= 29;	//MDB纸币器配置
	public static final int EV_MDB_C_CON 	= 30;	//MDB硬币器配置
	public static final int EV_MDB_HP_PAYOUT 	= 31;	//hopper硬币器找零
	
	//实例化hand邮箱，并且进行pend
	private static Handler EVProhand=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			//ToolClass.Log(ToolClass.INFO,"EV_JNI",msg.obj.toString());
			// TODO Auto-generated method stub
			switch (msg.what)
			{
				case 1://接收JNI返回的消息
					ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<"+msg.obj.toString(),"log.txt");
//					Map<String, Object> map=JsonToolUnpack.getMapListgson(msg.obj.toString());
//					//ToolClass.Log(ToolClass.INFO,"EV_JNI",list.toString());
//					/*
//					 //遍历Map输出
//					Set<Entry<String, Object>> allset=map.entrySet();  //实例化
//			        Iterator<Entry<String, Object>> iter=allset.iterator();
//			        while(iter.hasNext())
//			        {
//			            Entry<String, Object> me=iter.next();
//			            ToolClass.Log(ToolClass.INFO,"EV_JNI",me.getKey()+"-->"+me.getValue());
//			        } 
//			        */
					try {
						String text = msg.obj.toString();
						JSONObject jsonObject = new JSONObject(text); 
						//根据key取出内容
						JSONObject ev_head = (JSONObject) jsonObject.getJSONObject("EV_json");
						int str_evType =  ev_head.getInt("EV_type");					
					    //ToolClass.Log(ToolClass.INFO,"EV_JNI",String.valueOf(str_evType));
					    switch(str_evType)
					    {
						    case EV_REGISTER://串口注册
						    	if(ev_head.getInt("port_id")>=0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_REGISTER);
									allSet.put("port_com", ev_head.getString("port"));
									allSet.put("port_id", ev_head.getInt("port_id"));
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_REGISTER);
									allSet.put("port_com", ev_head.getString("port"));
									allSet.put("port_id", ev_head.getInt("port_id"));
									callBack.jniCallback(allSet);
						    	}
						    	break;
						    	
						    //快递柜设备	
						    case EV_BENTO_OPEN://快递柜开门
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_BENTO_OPEN);
									allSet.put("addr", ev_head.getInt("addr"));//柜子地址
									allSet.put("box", ev_head.getInt("box"));//格子地址
									allSet.put("result", ev_head.getInt("result"));
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_BENTO_OPEN);
									allSet.put("addr", 0);//柜子地址
									allSet.put("box", 0);//格子地址
									allSet.put("result", 0);
									callBack.jniCallback(allSet);
						    	}
						    	break;
						    case EV_BENTO_CHECK://快递柜查询
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道状态上报","log.txt");
								if(ev_head.getInt("is_success")>0)
								{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_BENTO_CHECK);
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
									callBack.jniCallback(allSet);
								}
								else
								{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_BENTO_CHECK);
									allSet.put("cool", 0);
									allSet.put("hot", 0);
									allSet.put("light", 0);
//									JSONArray arr=ev_head.getJSONArray("column");//返回json数组
//									//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
//									for(int i=0;i<arr.length();i++)
//									{
//										JSONObject object2=arr.getJSONObject(i);
//										allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("state"));								
//									}
									//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道3:"+allSet.toString());
									callBack.jniCallback(allSet);
								}
						    	break;
						    case EV_BENTO_LIGHT://快递柜照明
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_BENTO_LIGHT);
									allSet.put("addr", ev_head.getInt("addr"));//柜子地址
									allSet.put("opt", ev_head.getInt("opt"));//开还是关
									allSet.put("result", ev_head.getInt("result"));
									callBack.jniCallback(allSet);
						    	}
						    	else 
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_BENTO_LIGHT);
									allSet.put("addr", 0);//柜子地址
									allSet.put("opt", 0);//开还是关
									allSet.put("result", 0);
									callBack.jniCallback(allSet);
						    	}
						    	break;
						    	
						    //现金设备	
						    case EV_MDB_ENABLE:	//使能接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_ENABLE);
									allSet.put("bill_result", ev_head.getInt("bill_result"));
									allSet.put("coin_result", ev_head.getInt("coin_result"));
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
						    		//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_ENABLE);
						    		allSet.put("bill_result", 0);
									allSet.put("coin_result", 0);
									callBack.jniCallback(allSet);
								}
						    	break;
						    case EV_MDB_B_INFO://纸币器查询接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_B_INFO);
									allSet.put("acceptor", ev_head.getInt("acceptor"));
									allSet.put("dispenser", ev_head.getInt("dispenser"));
									allSet.put("code", ev_head.getString("code"));
									allSet.put("sn", ev_head.getString("sn"));
									allSet.put("model", ev_head.getString("model"));
									allSet.put("ver", ev_head.getString("ver"));
									allSet.put("capacity", ev_head.getInt("capacity"));
									JSONArray arr1=ev_head.getJSONArray("ch_r");//返回json数组
									Map<String,Integer> allSet1 = new LinkedHashMap<String,Integer>() ;
									for(int i=0;i<arr1.length();i++)
									{
										JSONObject object2=arr1.getJSONObject(i);
										allSet1.put(String.valueOf(object2.getInt("ch")), object2.getInt("value"));								
									}
									allSet.put("ch_r", allSet1);
									
									JSONArray arr2=ev_head.getJSONArray("ch_d");//返回json数组
									Map<String,Integer> allSet2 = new LinkedHashMap<String,Integer>() ;
									for(int i=0;i<arr2.length();i++)
									{
										JSONObject object2=arr2.getJSONObject(i);
										allSet2.put(String.valueOf(object2.getInt("ch")), object2.getInt("value"));								
									}
									allSet.put("ch_d", allSet2);
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
						    		
								}
						    	break;
						    case EV_MDB_B_CON://纸币器配置接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
						    		allSet.clear();
						    	}
						    	else
						    	{
						    		
								}
						    	break;
						    case EV_MDB_C_INFO://硬币器查询接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_C_INFO);
									allSet.put("acceptor", ev_head.getInt("acceptor"));
									allSet.put("dispenser", ev_head.getInt("dispenser"));
									allSet.put("code", ev_head.getString("code"));
									allSet.put("sn", ev_head.getString("sn"));
									allSet.put("model", ev_head.getString("model"));
									allSet.put("ver", ev_head.getString("ver"));
									allSet.put("capacity", ev_head.getInt("capacity"));
									JSONArray arr1=ev_head.getJSONArray("ch_r");//返回json数组
									Map<String,Integer> allSet1 = new LinkedHashMap<String,Integer>() ;
									for(int i=0;i<arr1.length();i++)
									{
										JSONObject object2=arr1.getJSONObject(i);
										allSet1.put(String.valueOf(object2.getInt("ch")), object2.getInt("value"));								
									}
									allSet.put("ch_r", allSet1);
									
									JSONArray arr2=ev_head.getJSONArray("ch_d");//返回json数组
									Map<String,Integer> allSet2 = new LinkedHashMap<String,Integer>() ;
									for(int i=0;i<arr2.length();i++)
									{
										JSONObject object2=arr2.getJSONObject(i);
										allSet2.put(String.valueOf(object2.getInt("ch")), object2.getInt("value"));								
									}
									allSet.put("ch_d", allSet2);
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
						    		
								}
						    	break;
						    case EV_MDB_C_CON://硬币器配置接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
						    		allSet.clear();
						    	}
						    	else
						    	{
						    		
								}
						    	break;	
						    case EV_MDB_PAYOUT://找币接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_PAYOUT);
									allSet.put("result", ev_head.getInt("result"));
									allSet.put("bill_changed", ev_head.getInt("bill_changed"));
									allSet.put("coin_changed", ev_head.getInt("coin_changed"));
									callBack.jniCallback(allSet);
						    	}	
						    	else
						    	{
						    		//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_PAYOUT);
									allSet.put("result", 0);
						    		allSet.put("bill_changed", 0);
									allSet.put("coin_changed", 0);
									callBack.jniCallback(allSet);
								}
						    	break;
						    case EV_MDB_HP_PAYOUT://Hopper找币接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_HP_PAYOUT);
									allSet.put("result", ev_head.getInt("result"));
									allSet.put("changed", ev_head.getInt("changed"));
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
						    		//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_HP_PAYOUT);
									allSet.put("result", 0);
						    		allSet.put("changed", 0);
						    		callBack.jniCallback(allSet);
								}
						    	break;	
						    //交易页面使用	
						    case EV_MDB_HEART://现金心跳查询接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_HEART);
									allSet.put("bill_enable", ev_head.getInt("bill_enable"));
									allSet.put("bill_payback", ev_head.getInt("bill_payback"));
									allSet.put("bill_err", ev_head.getInt("bill_err"));
									allSet.put("bill_recv", ev_head.getInt("bill_recv"));
									allSet.put("bill_remain", ev_head.getInt("bill_remain"));
									allSet.put("coin_enable", ev_head.getInt("coin_enable"));
									allSet.put("coin_payback", ev_head.getInt("coin_payback"));
									allSet.put("coin_err", ev_head.getInt("coin_err"));
									allSet.put("coin_recv", ev_head.getInt("coin_recv"));
									allSet.put("coin_remain", ev_head.getInt("coin_remain"));
									JSONObject object2=ev_head.getJSONObject("hopper");//返回json数组
									allSet.put("hopper1", object2.getInt("hopper1"));
									allSet.put("hopper2", object2.getInt("hopper2"));
									allSet.put("hopper3", object2.getInt("hopper3"));
									allSet.put("hopper4", object2.getInt("hopper4"));
									allSet.put("hopper5", object2.getInt("hopper5"));
									allSet.put("hopper6", object2.getInt("hopper6"));
									allSet.put("hopper7", object2.getInt("hopper7"));
									allSet.put("hopper8", object2.getInt("hopper8"));
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_HEART);
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
									callBack.jniCallback(allSet);
						    	}	
						    	break;
						    case EV_MDB_COST:
//						    	if(ev_head.getInt("is_success")>0)
//						    	{
//									//往接口回调信息
//									allSet.clear();
//									allSet.put("EV_TYPE", EV_MDB_COST);
//									allSet.put("result", ev_head.getInt("result"));
//									allSet.put("bill_recv", ev_head.getInt("bill_recv"));
//									allSet.put("coin_recv", ev_head.getInt("coin_recv"));									
//									callBack.jniCallback(allSet);
//						    	}
						    	break;
						    case EV_MDB_PAYBACK://退币接口
						    	if(ev_head.getInt("is_success")>0)
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_PAYBACK);
									allSet.put("result", ev_head.getInt("result"));
									allSet.put("bill_changed", ev_head.getInt("bill_changed"));
									allSet.put("coin_changed", ev_head.getInt("coin_changed"));									
									callBack.jniCallback(allSet);
						    	}
						    	else
						    	{
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EV_MDB_PAYBACK);
									allSet.put("result", 0);
									allSet.put("bill_changed", 0);
									allSet.put("coin_changed", 0);									
									callBack.jniCallback(allSet);
						    	}
						    	break;	
					    }

//					    //出货
//						else if(str_evType.equals("EV_TRADE_RPT"))
//						{
//							EV_TYPE=EV_TRADE_RPT;
//							//得到出货回传参数
//							allSet.clear();	
//							allSet.put("EV_TYPE", EV_TRADE_RPT);
//							allSet.put("device", ev_head.getInt("cabinet"));//出货柜号							
//							allSet.put("status", ev_head.getInt("result"));//出货结果
//							allSet.put("hdid", ev_head.getInt("column"));//货道id
//							allSet.put("type", ev_head.getInt("type"));//出货类型
//							allSet.put("cost", ev_head.getInt("cost"));//扣钱
//							allSet.put("totalvalue", ev_head.getInt("remainAmount"));//剩余金额
//							allSet.put("huodao", ev_head.getInt("remainCount"));//剩余存货数量				
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<出货返回");	
//							//往Activity线程发送信息
////							Message childmsg=mainHandler.obtainMessage();
////							childmsg.what=EV_TRADE_RPT;
////							childmsg.obj=allSet;
////							mainHandler.sendMessage(childmsg);		
//							//往接口回调信息
//							callBack.jniCallback(allSet);
//						}
//					    //货道状态上报
//						else if(str_evType.equals("EV_COLUMN_RPT"))
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道状态上报");
//							EV_TYPE=EV_COLUMN_RPT;
//							JSONArray arr=ev_head.getJSONArray("column");//返回json数组
//							//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
//							//往接口回调信息
//							allSet.clear();	
//							allSet.put("EV_TYPE", EV_COLUMN_RPT);
//							for(int i=0;i<arr.length();i++)
//							{
//								JSONObject object2=arr.getJSONObject(i);
//								allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("state"));								
//							}
//							//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道3:"+allSet.toString());
//							callBack.jniCallback(allSet);
//						}
//					    //投币上报
//						else if(str_evType.equals("EV_PAYIN_RPT"))//投币上报
//						{
//							int payin_amount= ev_head.getInt("payin_amount");
//							int reamin_amount = ev_head.getInt("reamin_amount");
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<投币:"+Integer.toString(payin_amount)
//									+"总共:"+Integer.toString(reamin_amount));							
//							
//							//往Activity线程发送信息
////							Message childmsg=mainHandler.obtainMessage();
////							childmsg.what=EV_PAYIN_RPT;
////							childmsg.obj=amount;
////							mainHandler.sendMessage(childmsg);	
//							//往接口回调信息
//							allSet.clear();	
//							allSet.put("EV_TYPE", EV_PAYIN_RPT);
//							allSet.put("payin_amount", payin_amount);
//							allSet.put("reamin_amount", reamin_amount);
//							callBack.jniCallback(allSet);
//						}
//					    //找零金额上报
//						else if(str_evType.equals("EV_PAYOUT_RPT"))
//						{
//							int payout_amount= ev_head.getInt("payout_amount");
//							int reamin_amount = ev_head.getInt("reamin_amount");
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<找零:"+Integer.toString(payout_amount)
//									+"剩余:"+Integer.toString(reamin_amount));	//往Activity线程发送信息
//							
////							Message childmsg=mainHandler.obtainMessage();
////							childmsg.what=EV_PAYOUT_RPT;
////							childmsg.obj=amount;
////							mainHandler.sendMessage(childmsg);
//							//往接口回调信息
//							allSet.clear();	
//							allSet.put("EV_TYPE", EV_PAYOUT_RPT);
//							allSet.put("payout_amount", payout_amount);
//							allSet.put("reamin_amount", reamin_amount);
//							callBack.jniCallback(allSet);
//						}
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					break;
			}	
		}
		
	};	
	
		
	//与activity之间接口的交互
	public static void setCallBack(JNIInterface call){ 
        callBack = call;
    } 
	//与jni接口的交互
	public static class Call_json implements EV_listener
	{
		public void do_json(String json){
			Message msg = Message.obtain();
			msg.what = 1;
			msg.obj = json;
			EVProhand.sendMessage(msg);
		}
	}
	/*********************************************************************************************************
	** Function name:     	vmcEVStart
	** Descriptions:	    监听开启接口
	** input parameters:    
	** output parameters:   无
	** Returned value:      
	*********************************************************************************************************/
	public static void vmcEVStart()
	{
		EVprotocol.addListener(callJson);//注册于jni的监听接口
	}
	/*********************************************************************************************************
	** Function name:     	vmcEVStop
	** Descriptions:	    监听关闭
	** input parameters:    
	** output parameters:   无
	** Returned value:      
	*********************************************************************************************************/
	public static void vmcEVStop()
	{
		EVprotocol.removeListener(callJson);
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_portRegister
	** Descriptions		:		串口注册接口 	[异步]
	** input parameters	:       portName 串口号 例如"COM1"
	** output parameters:		无
	** Returned value	:		1：指令发送成功  0：指令发送失败
	*  具体的结果通过回调返回json包 		 例如： EV_JSON={"EV_json":{"EV_type":1,"port":"/dev/ttymxc2","port_id":0}}
	*  							"EV_type" = EV_REGISTER = 1;表示串口注册包类型
	*                           "port"    表示返回的串口号,就是portName的值
	*							"port_id":表示返回的串口编号，如果失败则返回 -1
	*********************************************************************************************************/
	public static int EV_portRegister(String portName)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIbenopen>>]"+portName,"log.txt");		
		return EVprotocol.EV_portRegister(portName);		
	}
	
	
	
	/*********************************************************************************************************
	** Function name	:		EV_portRelease
	** Descriptions		:		串口释放接口  [异步]
	** input parameters	:       port_id 串口编号
	** output parameters:		无
	** Returned value	:		1：指令发送成功  0：指令发送失败
	*	具体的结果通过回调返回json包	  例如： EV_JSON={"EV_json":{"EV_type":2,"result":1}}
	*							"EV_type"= EV_RELEASE = 2; 表示串口释放包类型
	*							"result":表示操作结果    1:表示成功释放   0:表示释放失败
	*********************************************************************************************************/
	public  static int EV_portRelease(int port_id)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIbenclose>>]"+port_id,"log.txt");		
		return EVprotocol.EV_portRelease(port_id);		
	}
	
	
	
	//快递柜
	/*********************************************************************************************************
	** Function name	:		EV_bentoOpen
	** Descriptions		:		快递柜开门接口  [异步]
	** input parameters	:       port_id:串口编号, addr:柜子地址 01-16,box:开门的格子号 1-88
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":11,"port_id":0,"addr":1,"box":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_OPEN = 11; 表开门结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 	表示处理结果	1:开门成功   0:开门失败
	*********************************************************************************************************/
	public  static int EV_bentoOpen(int port_id,int addr,int box)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[bentoCheck>>port_id=]"+port_id+"[addr]="+addr+"[box]="+box,"log.txt");
		return EVprotocol.EV_bentoOpen(port_id,addr,box);
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_bentoCheck
	** Descriptions		:		快递柜查询接口  [异步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 01-16
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":12,"port_id":0,"addr":0,"is_success":1,"ID":"xxxxxxxxx1",
	*								"cool":0,"hot":0,"light":1,"sum":88,[]}}
	*							"EV_type"= EV_BENTO_CHECK = 12: 表查询结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"ID":驱动板的ID号
	*							"cool":是否支持制冷 	 	1:支持 0:不支持
	*							"hot":是否支持加热  		1:支持 0:不支持
	*							"light":是否支持照明  	1:支持 0:不支持
	*							"sum":货道总数	例如货道总数88 则默认 货道编号 1-88,状态1正常,0故障
	*********************************************************************************************************/
	public  static int EV_bentoCheck(int port_id,int addr)
	{				
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[bentoCheck>>port_id=]"+port_id+"[addr]="+addr,"log.txt");
		return EVprotocol.EV_bentoCheck(port_id,addr);
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_bentoLight
	** Descriptions		:		快递柜照明控制接口  [异步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 01-16,opt:开照明控制 1:开  0:关
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":13,"port_id":0,"addr":1,"opt":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_LIGHT = 13: 表照明控制结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址,"opt":原样返回操作.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 表示处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  static int EV_bentoLight(int port_id,int addr,int opt)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[bentoCheck>>port_id=]"+port_id+"[addr]="+addr+"[opt]="+opt,"log.txt");
		return EVprotocol.EV_bentoLight(port_id,addr,opt);
	}
		
		
	//普通柜子
	/*********************************************************************************************************
	** Function name:     	getColumn
	** Descriptions:	    VMC获取货道接口
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回的结果进行解析
	** input parameters:    cabinet:1主柜,2副柜
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public  static int getColumn(int cabinet)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIgetColumn>>]"+cabinet,"log.txt");
		//return ev.getColumn(cabinet);		
		return 0;
	}
	
	/*********************************************************************************************************
	** Function name:     	trade
	** Descriptions:	    VMC出货接口  
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	**						回调JSON包 格式 
	** input parameters:    cabinet:柜号  column:货道号   type:支付方式  type= 0 :现金  type = 1  非现金    
	**					    cost:扣款金额(单位:分 ;如果type=1则该值必须为0)
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败  
	*********************************************************************************************************/
	public static int trade(int cabinet,int column,int type,long cost)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIhuo>>]"+cost,"log.txt");
		//return ev.trade(cabinet,column,type,(int)cost);
		return 0;
	}
	
		
	
	//现金支付设备		
	/*********************************************************************************************************
	** Function name	:		EV_mdbEnable
	** Descriptions		:		收币设备使能禁能接口  [异步]
	** input parameters	:       port_id:串口编号; bill:操作纸币器  1:操作 0:不操作;coin:操作硬币器  1:操作,0:不操作 ;opt:操作 1:使能 0:禁能
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":22,"port_id":0,"bill":1,"coin":1,"opt":1,"is_success":1,"bill_result":1,"coin_result":1}}
	*							"EV_type"= EV_MDB_ENABLE = 22: 表MDB使能结果回应包类型
	*							"port_id":原样返回,"bill":原样返回柜子地址,"coin":原样返回操作;"opt":原样返回
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_result": 纸币器处理结果	1:成功   0:失败
	*							"coin_result": 纸币器处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  static int EV_mdbEnable(int port_id,int bill,int coin,int opt)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbEnable>>port_id=]"+port_id+"[bill]="+bill+"[coin]="+coin+"[opt]="+opt,"log.txt");
		return EVprotocol.EV_mdbEnable(port_id,bill,coin,opt);
	}
	/*********************************************************************************************************
	** Function name	:		EVmdbBillInfoCheck
	** Descriptions		:		MDB纸币器查询接口  [异步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":24,"port_id":0,"is_success":1,
	*							"acceptor":2,"dispenser":2,"code":"ITL","sn":"12312....","model":"NV9",
	*							"ver":"1212","capacity":500,"ch_r":[],"ch_d":[]}}
	*							"EV_type"= EV_MDB_B_INFO = 24: 表示MDB纸币器信息查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"acceptor": 纸币接收器协议类型	0:接收器关闭	2:MDB协议接收器
	*							"dispenser": 纸币找零器协议类型	0:找零器关闭	2:MDB协议找零器
	*							"code": 纸币器厂商代码			例如：ITL
	*							"sn":纸币器序列号
	*							"model":纸币器型号				例如:NV9
	*							ver:纸币器软件版本号
	*							capacity:纸币器储币量	
	*							ch_r:纸币器接收器通道面值 		以分为单位
	*							ch_d:纸币器找零器通道面值		以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbBillInfoCheck(int port_id)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbbillInfo>>port_id=]"+port_id,"log.txt");
		return EVprotocol.EV_mdbBillInfoCheck(port_id);
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbBillConfig
	** Descriptions		:		MDB纸币器配置  [异步]
	** input parameters	:       port_id:串口编号;req:配置参数 json包
 								EV_JSON={"EV_json":{"EV_type":29,"port_id":0,"acceptor":2,"dispenser":2,
 								"ch_r":[{"ch":1,"value":100},{"ch":2,"value":500},{"ch":3,"value":1000},{"ch":4,"value":2000},
 										{"ch":5,"value":0},{"ch":6,"value":0},{"ch":7,"value":0},{"ch":8,"value":0},
 										{"ch":9,"value":0},{"ch":10,"value":0},{"ch":11,"value":0},{"ch":12,"value":0},
 										{"ch":13,"value":0},{"ch":14,"value":0},{"ch":15,"value":0},{"ch":16,"value":0}],
 								"ch_d":[{"ch":1,"value":0},{"ch":2,"value":0},{"ch":3,"value":0},{"ch":4,"value":0},
 										{"ch":5,"value":0},{"ch":6,"value":0},{"ch":7,"value":0},{"ch":8,"value":0},
 										{"ch":9,"value":0},{"ch":10,"value":0},{"ch":11,"value":0},{"ch":12,"value":0},
 										{"ch":13,"value":0},{"ch":14,"value":0},{"ch":15,"value":0},{"ch":16,"value":0}]}}
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":29,"port_id":0,"acceptor":0,"dispenser":1,
	*							"is_success":1,"result":1}}
	*							"EV_type"= EV_MDB_B_CON = 29: 表示MDB纸币配置结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":返回结果	1:成功     0:失败			
	*********************************************************************************************************/
	public  static int EV_mdbBillConfig(int port_id,int billtype)
	{
		JSONObject jsonObject = new JSONObject(); 
		JSONObject EV_json = new JSONObject(); 
		JSONArray ch_r=new JSONArray();
		JSONArray ch_d=new JSONArray();
		try {
			jsonObject.put("EV_type", EV_MDB_B_CON);
			jsonObject.put("port_id", port_id);
			jsonObject.put("acceptor", billtype);
			jsonObject.put("dispenser", billtype);
			for(int i=1;i<=16;i++)
			{
				JSONObject ch = new JSONObject(); 
				ch.put("ch", i);
				ch.put("value", 0);
				ch_r.put(ch);
			}
			jsonObject.put("ch_r", ch_r);
			for(int i=1;i<=16;i++)
			{
				JSONObject ch = new JSONObject(); 
				ch.put("ch", i);
				ch.put("value", 0);
				ch_d.put(ch);
			}
			jsonObject.put("ch_d", ch_d);
			EV_json.put("EV_json", jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[BillConfig>>]"+EV_json.toString(),"log.txt");		
		return EVprotocol.EV_mdbBillConfig(EV_json.toString());
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbCoinInfoCheck
	** Descriptions		:		MDB硬币器信息查询接口  [异步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":25,"port_id":0,"is_success":1,
	*							"acceptor":2,"dispenser":2,"code":"MEI","sn":"12312....","model":"***",
	*							"ver":"1212","capacity":500,"ch_r":[],"ch_d":[]}}
	*							"EV_type"= EV_MDB_C_INFO = 25: 表示MDB硬币器信息查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"acceptor": 硬币接收器协议类型	0:接收器关闭	1:并行硬币脉冲协议  2:MDB协议接收器 3:串行硬币脉冲协议
	*							"dispenser": 硬币找零器协议类型	0:找零器关闭	1:hopper 串口232协议  2:MDB协议找零器
	*							"code": 硬币器厂商代码			例如：MEI
	*							"sn":硬币器序列号
	*							"model":硬币器型号			
	*							ver:硬币器软件版本号
	*							capacity:硬币器储币量	
	*							ch_r:硬币器接收器通道面值 		以分为单位
	*							ch_d:硬币器找零器通道面值		以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbCoinInfoCheck(int port_id)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbcoinInfo>>port_id=]"+port_id,"log.txt");
		return EVprotocol.EV_mdbCoinInfoCheck(port_id);
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbCoinConfig
	** Descriptions		:		硬币器找零器配置  [异步]
	** input parameters	:       port_id:串口编号;req:配置参数 json包
 								EV_JSON={"EV_json":{"EV_type":30,"port_id":0,"acceptor":2,"dispenser":2,"hight_en":0,
 								"ch_r":[{"ch":1,"value":100},{"ch":2,"value":500},{"ch":3,"value":1000},{"ch":4,"value":2000},
 										{"ch":5,"value":0},{"ch":6,"value":0},{"ch":7,"value":0},{"ch":8,"value":0},
 										{"ch":9,"value":0},{"ch":10,"value":0},{"ch":11,"value":0},{"ch":12,"value":0},
 										{"ch":13,"value":0},{"ch":14,"value":0},{"ch":15,"value":0},{"ch":16,"value":0}],
 								"ch_d":[{"ch":1,"value":0},{"ch":2,"value":0},{"ch":3,"value":0},{"ch":4,"value":0},
 										{"ch":5,"value":0},{"ch":6,"value":0},{"ch":7,"value":0},{"ch":8,"value":0},
 										{"ch":9,"value":0},{"ch":10,"value":0},{"ch":11,"value":0},{"ch":12,"value":0},
 										{"ch":13,"value":0},{"ch":14,"value":0},{"ch":15,"value":0},{"ch":16,"value":0}]}}
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":30,"port_id":0,"acceptor":0,"dispenser":1,
	*							"is_success":1,"result":1}}
	*							"EV_type"= EV_MDB_C_CON = 30: 表示MDB硬币配置结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:成功     0:失败			
	*********************************************************************************************************/
	public  static int EV_mdbCoinConfig(int port_id,int cointype,int payouttype,Map<Integer, Integer>c_r,Map<Integer, Integer>c_d)
	{
		JSONObject jsonObject = new JSONObject(); 
		JSONObject EV_json = new JSONObject(); 
		JSONArray ch_r=new JSONArray();
		JSONArray ch_d=new JSONArray();
		try {
			jsonObject.put("EV_type", EV_MDB_C_CON);
			jsonObject.put("port_id", port_id);
			jsonObject.put("acceptor", cointype);
			jsonObject.put("dispenser", payouttype);
			jsonObject.put("hight_en", 0);
			//输出内容
	        Set<Map.Entry<Integer,Integer>> allset=c_r.entrySet();  //实例化
	        Iterator<Map.Entry<Integer,Integer>> iter=allset.iterator();
	        while(iter.hasNext())
	        {
	            Map.Entry<Integer,Integer> me=iter.next();
	            //System.out.println(me.getKey()+"--"+me.getValue());
	            JSONObject ch = new JSONObject(); 
				ch.put("ch", me.getKey());
				ch.put("value",me.getValue());
				ch_r.put(ch);
	        } 	        
			jsonObject.put("ch_r", ch_r);
			
			//输出内容
	        Set<Map.Entry<Integer,Integer>> allset2=c_d.entrySet();  //实例化
	        Iterator<Map.Entry<Integer,Integer>> iter2=allset2.iterator();
	        while(iter2.hasNext())
	        {
	            Map.Entry<Integer,Integer> me2=iter2.next();
	            //System.out.println(me.getKey()+"--"+me.getValue());
	            JSONObject ch = new JSONObject(); 
				ch.put("ch", me2.getKey());
				ch.put("value",me2.getValue());
				ch_d.put(ch);
	        } 	   
			jsonObject.put("ch_d", ch_d);
			EV_json.put("EV_json", jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[CoinConfig>>]"+EV_json.toString(),"log.txt");	
		return EVprotocol.EV_mdbCoinConfig(EV_json.toString());		
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbPayout
	** Descriptions		:		现金设备找币接口  [异步]
	** input parameters	:       port_id:串口编号;bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作;
	*							billPayout:纸币器下发找币金额 分为单位 ;coinPayout:硬币器下发找币金额 分为单位 
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":28,"port_id":0,"bill":0,"coin":1,
	*							"billPayout":0,"coinPayout":100,"is_success":1,
	*							"result":1,"bill_changed":0,"coin_changed":100}}
	*							"EV_type"= EV_MDB_PAYOUT = 28: 表示MDB找币结果回应包类型
	*							"port_id":原样返回,"bill":原样返回,"coin":原样返回,"billPayout":原样返回,"coinPayout":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:找币成功     0:找币失败
	*							"bill_changed":纸币器当前找币金额	  以分为单位
	*							"coin_changed":硬币器当前找币金额	  以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbPayout(int port_id,int bill,int coin,int billPay,int coinPay)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbPayout>>port_id=]"+port_id+"[bill]="+bill+"[coin]="+coin+"[billPay]="+billPay+"[coinPay]="+coinPay,"log.txt");
		return EVprotocol.EV_mdbPayout(port_id,bill,coin,billPay,coinPay);
	}
	
	/*********************************************************************************************************
	** Function name	:		EV_mdbHopperPayout
	** Descriptions		:		Hopper找币接口  [同步]
	** input parameters	:       port_id:串口编号;no:hopper编号 1-8  nums:需要找币的枚数 ;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":31,"port_id":0,"no":1,"nums":5,
	*							"is_success":1,"result":1,"changed":5}}
	*							"EV_type"= EV_MDB_HP_PAYOUT = 31: 表示hopper找币结果回应包类型
	*							"port_id":原样返回,"no":原样返回,"nums":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:找币成功     0:找币失败
	*							"changed":实际找零枚数
	*********************************************************************************************************/
	public static int EV_mdbHopperPayout(int port_id,int no,int nums)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[HopperPayout>>no=]"+no+" nums="+nums,"log.txt");	
		return EVprotocol.EV_mdbHopperPayout(port_id,no,nums);
	}
		
	
	//交易页面使用
	/*********************************************************************************************************
	** Function name	:		EV_mdbHeart
	** Descriptions		:		现金设备心跳查询接口  [异步]
	** input parameters	:       port_id:串口编号;
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":23,"port_id":0,"is_success":1,
	*							"bill_enable":1,"bill_payback":0,"bill_err":0,"bill_recv":0,"bill_remain":0,
	*							"coin_enable":1,"coin_payback":0,"coin_err":0,"coin_recv":0,"coin_remain":0,
	*                           "hopper":{"hopper1":1,"hopper2":1,"hopper3":0,"hopper4":0,"hopper5":0,"hopper6":0,
	*                           "hopper7":0,"hopper8":0}
	*                           }}
	*							"EV_type"= EV_MDB_HEART = 23: 表示MDB心跳查询结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"bill_enable": 纸币器使能状态		1:使能   0:禁能
	*							"bill_payback": 纸币器退币按钮触发	1:触发   0:非触发
	*							"bill_err":纸币器故障状态			0:正常   非0 为故障码
	*							"bill_recv":纸币器当前收币金额	以分为单位
	*							"bill_remain":纸币器当前储币金额	以分为单位
	*	*						"coin_enable": 硬币器使能状态		1:使能   0:禁能
	*							"coin_payback": 硬币器退币按钮触发	1:触发   0:非触发
	*							"coin_err":硬币器故障状态			0:正常   非0 为故障码
	*							"coin_recv":硬币器当前收币金额	以分为单位
	*							"coin_remain":硬币器当前储币金额	以分为单位
	*		                    "hopper":8个hopper的状态,0正常,1缺币,2故障,3通讯故障
	*********************************************************************************************************/
	public  static int EV_mdbHeart(int port_id)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbHeart>>port_id=]"+port_id,"log.txt");
		return EVprotocol.EV_mdbHeart(port_id);
	}
	/*********************************************************************************************************
	** Function name	:		EV_mdbCost
	** Descriptions		:		MDB扣款接口  [异步]
	** input parameters	:       port_id:串口编号;cost:扣款金额  以分为单位
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"cost":100,"is_success":1,
	*							"result":1,"bill_recv":0,"coin_recv":0}}
	*							"EV_type"= EV_MDB_COST = 26: 表示MDB扣款结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:扣款成功     0:扣款失败
	*							"bill_recv":纸币器当前收币金额	  以分为单位
	*							"coin_recv":硬币器当前收币金额	  以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbCost(int port_id,int cost)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbmdbCost>>port_id=]"+port_id+"[cost]="+cost,"log.txt");
		return EVprotocol.EV_mdbCost(port_id,cost);
	}
	/*********************************************************************************************************
	** Function name	:		EV_mdbPayback
	** Descriptions		:		MDB退币接口  [异步]
	** input parameters	:       port_id:串口编号;bill:操作纸币器  1:操作,0:不操作,coin:操作硬币器  1:操作,0:不操作
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":26,"port_id":0,"bill":1,"coin":1,"is_success":1,
	*							"result":1,"bill_changed":0,"coin_changed":100}}
	*							"EV_type"= EV_MDB_PAYBACK = 27: 表示MDB退币结果回应包类型
	*							"port_id":原样返回,"bill":原样返回,"coin":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result":扣款结果	1:退币成功     0:退币失败
	*							"bill_changed":纸币器当前找币金额	  以分为单位
	*							"coin_changed":硬币器当前找币金额	  以分为单位
	*********************************************************************************************************/
	public  static int EV_mdbPayback(int port_id,int bill,int coin)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[mdbPayback>>port_id=]"+port_id+"[bill]="+bill+"[coin]="+coin,"log.txt");
		return EVprotocol.EV_mdbPayback(port_id,bill,coin);
	}
					
}
