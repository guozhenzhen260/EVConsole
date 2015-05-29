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
import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocol.EV_listener;

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
						int str_evType =  ev_head.getInt("EV_type");					
					    ToolClass.Log(ToolClass.INFO,"EV_JNI",String.valueOf(str_evType));
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
						    	break;
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
						    	break;
						    case EV_BENTO_CHECK://快递柜查询
								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道状态上报");
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
						    	break;
					    }
//					    if(str_evType.equals("EV_INITING"))//正在初始化
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<正在初始化");
//					    	EV_TYPE=EV_INITING;
//						}
//						else if(str_evType.equals("EV_ONLINE"))//str_evType.equals("EV_PAYOUT_RPT")
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<成功连接");
//							EV_TYPE=EV_ONLINE;
//							//往Activity线程发送信息
////							Message childmsg=mainHandler.obtainMessage();
////							childmsg.what=EV_ONLINE;
////							mainHandler.sendMessage(childmsg);
//							//往接口回调信息
//							allSet.clear();
//							allSet.put("EV_TYPE", EV_ONLINE);
//							callBack.jniCallback(allSet);
//						}
//						else if(str_evType.equals("EV_OFFLINE"))
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<断开连接");
//							EV_TYPE=EV_OFFLINE;
//						}
//						else if(str_evType.equals("EV_RESTART"))
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<主控板重启心动");
//							EV_TYPE=EV_RESTART;
//						}
//						else if(str_evType.equals("EV_STATE_RPT"))
//						{
//							int state = ev_head.getInt("vmcState");
//							if(state == 0)
//								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<断开连接");
//							else if(state == 1)
//								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<正在初始化");
//							else if(state == 2)		
//							{
//								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<正常");	
//								//往接口回调信息
//								allSet.clear();
//								allSet.put("EV_TYPE", EV_ONLINE);
//								callBack.jniCallback(allSet);
//							}
//							else if(state == 3)
//								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<故障");
//							else if(state == 4)
//								ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<维护");
//						}
//						else if(str_evType.equals("EV_ENTER_MANTAIN"))
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<维护");
//						}
//						else if(str_evType.equals("EV_EXIT_MANTAIN"))
//						{
//							ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<退出维护");
//						}
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
	** Function name:     	bentoRegister
	** Descriptions		:		串口注册接口 	[异步]
	** input parameters	:       portName 串口号 例如"COM1"
	** output parameters:		无
	** Returned value	:		1：指令发送成功  0：指令发送失败
	*  具体的结果通过回调返回json包 		 例如： EV_JSON={"EV_json":{"EV_type":1,"port":"/dev/ttymxc2","port_id":0}}
	*  							"EV_type" = EV_REGISTER = 1;表示串口注册包类型
	*                           "port"    表示返回的串口号,就是portName的值
	*							"port_id":表示返回的串口编号，如果失败则返回 -1
	*********************************************************************************************************/
	public  static int bentoRegister(String portName)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIbenopen>>]"+portName);		
		return EVprotocol.EV_portRegister(portName);		
	}
	
	
	
	/*********************************************************************************************************
	** Function name:     	bentoRelease
	** Descriptions		:		串口释放接口  [异步]
	** input parameters	:       port_id 串口编号
	** output parameters:		无
	** Returned value	:		1：指令发送成功  0：指令发送失败
	*	具体的结果通过回调返回json包	  例如： EV_JSON={"EV_json":{"EV_type":2,"result":1}}
	*							"EV_type"= EV_RELEASE = 2; 表示串口释放包类型
	*							"result":表示操作结果    1:表示成功释放   0:表示释放失败
	*********************************************************************************************************/
	public  static int bentoRelease(int port_id)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIbenclose>>]"+port_id);		
		return EVprotocol.EV_portRelease(port_id);		
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
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIgetColumn>>]"+cabinet);
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
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIhuo>>]"+cost);
		//return ev.trade(cabinet,column,type,(int)cost);
		return 0;
	}
	
		

	
	//快递柜
	/*********************************************************************************************************
	** Function name:     	bentoOpen
	** Descriptions		:		快递柜开门接口  [异步]
	** input parameters	:       port_id:串口编号, addr:柜子地址 00-15,box:开门的格子号 1-88
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*	返回json包     例如： EV_JSON={"EV_json":{"EV_type":11,"port_id":0,"addr":0,"box":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_OPEN = 11; 表开门结果回应包类型
	*							"port_id":原样返回,
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 	表示处理结果	1:开门成功   0:开门失败
	*********************************************************************************************************/
	public static int bentoOpen(int port_id,int addr,int box)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[bentoCheck>>port_id=]"+port_id+"[addr]="+addr+"[box]="+box);
		return EVprotocol.EV_bentoOpen(port_id,addr,box);
	}
	
	
	/*********************************************************************************************************
	** Function name:     	bentoLight
	** Descriptions		:		快递柜照明控制接口  [异步]
	** input parameters	:       port_id:串口编号,addr:柜子地址 01-16,opt:开照明控制 1:开  0:关
	** output parameters:		无
	** Returned value	:		1：发送成功  0：发送失败
	*  通过回调返回json包     例如： EV_JSON={"EV_json":{"EV_type":13,"port_id":0,"addr":0,"opt":1,"is_success":1,"result":1}}
	*							"EV_type"= EV_BENTO_LIGHT = 13: 表照明控制结果回应包类型
	*							"port_id":原样返回,"addr":原样返回柜子地址,"opt":原样返回操作.
	*							"is_success":表示指令是否发送成功,1:发送成功。 0:发送失败（通信超时）
	*							"result": 表示处理结果	1:成功   0:失败
	*********************************************************************************************************/
	public  static int bentoLight(int port_id,int addr,int opt)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[bentoCheck>>port_id=]"+port_id+"[addr]="+addr+"[opt]="+opt);
		return EVprotocol.EV_bentoLight(port_id,addr,opt);
	}
	
	/*********************************************************************************************************
	** Function name:     	bentoCheck
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
	*							"sum":货道总数	例如货道总数88 则默认 货道编号 1-88
	*********************************************************************************************************/
	public  static int bentoCheck(int port_id,int addr)
	{				
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[bentoCheck>>port_id=]"+port_id+"[addr]="+addr);
		return EVprotocol.EV_bentoCheck(port_id,addr);
	}
	
	
	//现金支付设备			
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
		//return ev.payout((int)value);		
		return 0;
	}
	
	/*********************************************************************************************************
	** Function name:     	payback
	** Descriptions:	    VMC退币接口
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回的结果进行解析
	** input parameters:    无
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public  static int payback()
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIback>>]");
		//return ev.payback();	
		return 0;
	}
	
	/*********************************************************************************************************
	** Function name:     	cashControl
	** Descriptions:	             控制现金设备 直接返回 不进行回调
	** input parameters:    flag 1:开启现金设备  0关闭现金设备
	** output parameters:   无
	** Returned value:      1成功  0失败
	*********************************************************************************************************/
	public  static int cashControl(int flag)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIcashControl>>]"+flag);
		//return ev.cashControl(flag);		
		return 0;
	}
			
}
