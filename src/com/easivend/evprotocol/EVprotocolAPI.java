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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocol.EV_listener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EVprotocolAPI 
{
	static EVprotocol ev=null;
	private static JNIInterface callBack=null;//与activity交互注册回调
	private static Call_json callJson = new Call_json();//与JNI交互注册回调	
	private static Map<String,Integer> allSet = new HashMap<String,Integer>() ;
	private static int EV_TYPE=0;	
	public static final int EV_INITING=1;//正在初始化
	public static final int EV_ONLINE=2;//成功连接
	public static final int EV_OFFLINE=3;//断开连接
	public static final int EV_RESTART=4;//主控板重启心动
	public static final int EV_TRADE_RPT=5;//出货返回	
	public static final int EV_PAYIN_RPT=6;//投入纸币
	public static final int EV_PAYOUT_RPT=7;//找零结果
	
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
					ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<"+msg.obj.toString());
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
						String str_evType =  ev_head.getString("EV_type");					
					    //ToolClass.Log(ToolClass.INFO,"EV_JNI",str_evType);				    
					    if(str_evType.equals("EV_INITING"))//正在初始化
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<正在初始化");
					    	EV_TYPE=EV_INITING;
						}
						else if(str_evType.equals("EV_ONLINE"))//str_evType.equals("EV_PAYOUT_RPT")
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<成功连接");
							EV_TYPE=EV_ONLINE;
							//往Activity线程发送信息
//							Message childmsg=mainHandler.obtainMessage();
//							childmsg.what=EV_ONLINE;
//							mainHandler.sendMessage(childmsg);
							//往接口回调信息
							allSet.clear();
							allSet.put("EV_TYPE", EV_ONLINE);
							callBack.jniCallback(allSet);
						}
						else if(str_evType.equals("EV_OFFLINE"))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<断开连接");
							EV_TYPE=EV_OFFLINE;
						}
						else if(str_evType.equals("EV_RESTART"))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<主控板重启心动");
							EV_TYPE=EV_RESTART;
						}
						else if(str_evType.equals("EV_STATE_RPT"))
						{
							int state = ev_head.getInt("state");
							if(state == 0)
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<断开连接");
							else if(state == 1)
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<正在初始化");
							else if(state == 2)
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<正常");
							else if(state == 3)
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<故障");
							else if(state == 4)
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<维护");
						}
						else if(str_evType.equals("EV_ENTER_MANTAIN"))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<维护");
						}
						else if(str_evType.equals("EV_EXIT_MANTAIN"))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<退出维护");
						}
					    //出货
						else if(str_evType.equals("EV_TRADE_RPT"))
						{
							EV_TYPE=EV_TRADE_RPT;
							//得到出货回传参数
							allSet.clear();	
							allSet.put("EV_TYPE", EV_TRADE_RPT);
							allSet.put("device", ev_head.getInt("cabinet"));//出货柜号							
							allSet.put("status", ev_head.getInt("result"));//出货结果
							allSet.put("hdid", ev_head.getInt("column"));//货道id
							allSet.put("type", ev_head.getInt("type"));//出货类型
							allSet.put("cost", ev_head.getInt("cost"));//扣钱
							allSet.put("totalvalue", ev_head.getInt("remainAmount"));//剩余金额
							allSet.put("huodao", ev_head.getInt("remainCount"));//剩余存货数量				
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<出货返回");	
							//往Activity线程发送信息
//							Message childmsg=mainHandler.obtainMessage();
//							childmsg.what=EV_TRADE_RPT;
//							childmsg.obj=allSet;
//							mainHandler.sendMessage(childmsg);		
							//往接口回调信息
							callBack.jniCallback(allSet);
						}
					    //投币上报
						else if(str_evType.equals("EV_PAYIN_RPT"))//投币上报
						{
							int amount = ev_head.getInt("remainAmount");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<投币:"+Integer.toString(amount));							
							//往Activity线程发送信息
//							Message childmsg=mainHandler.obtainMessage();
//							childmsg.what=EV_PAYIN_RPT;
//							childmsg.obj=amount;
//							mainHandler.sendMessage(childmsg);	
							//往接口回调信息
							allSet.clear();	
							allSet.put("EV_TYPE", EV_PAYIN_RPT);
							allSet.put("amount", amount);
							callBack.jniCallback(allSet);
						}
					    //找零金额上报
						else if(str_evType.equals("EV_PAYOUT_RPT"))
						{
							int amount = ev_head.getInt("remainAmount");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<找零:"+Integer.toString(amount));
							//往Activity线程发送信息
//							Message childmsg=mainHandler.obtainMessage();
//							childmsg.what=EV_PAYOUT_RPT;
//							childmsg.obj=amount;
//							mainHandler.sendMessage(childmsg);
							//往接口回调信息
							allSet.clear();	
							allSet.put("EV_TYPE", EV_PAYOUT_RPT);
							allSet.put("amount", amount);
							callBack.jniCallback(allSet);
						}
					}
					catch (Exception e) {
						// TODO: handle exception
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
	** Function name:     	vmcStart
	** Descriptions:	    VMC主控板开启接口
	** input parameters:    portName 串口号 例如/dev/tty0
	** output parameters:   无
	** Returned value:      1:开启成功      -1:开启失败  （直接返回 不进行回调）
	*********************************************************************************************************/
	public static int vmcStart(String portName)
	{
		ev= new EVprotocol();
		ev.addListener(callJson);//注册于jni的监听接口
		return ev.vmcStart(portName);
	}
	
			
	/*********************************************************************************************************
	** Function name:     	vmcStop
	** Descriptions:	    VMC主控板断开接口
	** input parameters:    无
	** output parameters:   无
	** Returned value:      无（直接返回 不进行回调）
	*********************************************************************************************************/
	public static void vmcStop()
	{	
		ev.removeListener(callJson);
		ev.vmcStop();
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
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIhuo>>]"+cost);
		return ev.trade(cabinet,column,type,(int)cost);
	}
	
	/*********************************************************************************************************
	** Function name:     	payout
	** Descriptions:	    VMC出币接口
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	** input parameters:    value:要出币的金额(单位:分)
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public static int payout(long value)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIpayout>>]"+value);
		return ev.payout((int)value);		
	}
	
	
	
			
}
