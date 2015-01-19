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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EVprotocolAPI 
{
	static int EVproTimer=0;//超时定时器
	static EVprotocol ev=null;
	private static int EV_TYPE=0;
	private static final int EV_INITING=1;//正在初始化
	private static final int EV_ONLINE=2;//成功连接
	private static final int EV_OFFLINE=3;//断开连接
	private static final int EV_RESTART=4;//主控板重启心动
	private static final int EV_TRADE_RPT=5;//出货返回
		private static int device=0;//出货柜号		
		private static int status=0;//出货结果
		private static int hdid=0;//货道id
		private static int type=0;//出货类型
		private static int cost=0;//扣钱
		private static int totalvalue=0;//剩余金额
		private static int huodao=0;//剩余存货数量
	
	
	//实例化hand邮箱，并且进行pend
	private static Handler EVProhand=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			//Log.i("EV_JNI",msg.obj.toString());
			// TODO Auto-generated method stub
			switch (msg.what)
			{
				case 1://接收JNI返回的消息
					Log.i("EV_JNI",msg.obj.toString());
					Map<String, Object> map=JsonToolUnpack.getMapListgson(msg.obj.toString());
					//Log.i("EV_JNI",list.toString());
					/*
					 //遍历Map输出
					Set<Entry<String, Object>> allset=map.entrySet();  //实例化
			        Iterator<Entry<String, Object>> iter=allset.iterator();
			        while(iter.hasNext())
			        {
			            Entry<String, Object> me=iter.next();
			            Log.i("EV_JNI",me.getKey()+"-->"+me.getValue());
			        } 
			        */
					//根据key取出内容
				    String str_evType=map.get("EV_type").toString(); 
					//Log.i("EV_JNI",str_evType);				    
				    if(str_evType.equals("EV_INITING"))//正在初始化
					{
						//textView_VMCState.setText("正在初始化");
				    	Log.i("EV_JNI","正在初始化");
				    	EV_TYPE=EV_INITING;
					}
					else if(str_evType.equals("EV_ONLINE"))//str_evType.equals("EV_PAYOUT_RPT")
					{
						//textView_VMCState.setText("成功连接");
						Log.i("EV_JNI","成功连接");
						EV_TYPE=EV_ONLINE;
					}
					else if(str_evType.equals("EV_OFFLINE"))
					{
						//textView_VMCState.setText("断开连接");
						Log.i("EV_JNI","断开连接");
						EV_TYPE=EV_OFFLINE;
					}
					else if(str_evType.equals("EV_RESTART"))
					{
						//textView_VMCState.setText("主控板重启心动");
						Log.i("EV_JNI","主控板重启心动");
						EV_TYPE=EV_RESTART;
					}
					else if(str_evType.equals("EV_TRADE_RPT"))
					{
						//textView_VMCState.setText("主控板重启心动");
						Log.i("EV_JNI","出货返回");
						EV_TYPE=EV_TRADE_RPT;
						//得到出货回传参数
						device=Integer.parseInt(map.get("cabinet").toString());//出货柜号
						status=Integer.parseInt(map.get("result").toString());//出货结果
						hdid=Integer.parseInt(map.get("column").toString());//货道id
						type=Integer.parseInt(map.get("type").toString());//出货类型
						cost=Integer.parseInt(map.get("cost").toString());//扣钱
						totalvalue=Integer.parseInt(map.get("remainAmount").toString());//剩余金额
						huodao=Integer.parseInt(map.get("remainCount").toString());//剩余存货数量
					}
				    
					break;
			}	
		}
		
	};	
	
	
	/*********************************************************************************************************
	** Function name:     	vmcStart
	** Descriptions:	    VMC主控板开启接口
	** input parameters:    portName 串口号 例如/dev/tty0
	** output parameters:   无
	** Returned value:      1:开启成功      -1:开启失败  （直接返回 不进行回调）
	*********************************************************************************************************/
	public static int vmcStart(String portName)
	{
		int result=0;	
		ev=new EVprotocol(EVProhand);
		result=ev.vmcStart(portName);
		//开启成功
		if(result==1)
		{
			result=2;	
			EVproTimer=20;//设为20s延时
			while(EVproTimer>0)
			{
				if(EV_TYPE==EV_ONLINE)
				{
					result=1;
					EVproTimer=0;
				}
			}
		}	
		//开启失败
		
		
		return result; 
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
	public static int trade(int cabinet,int column,int type,int cost)
	{
		int result=0;	
		result=ev.trade(cabinet,column,type,cost);
		//请求发送成功
		if(result==1)
		{
			result=0;
			EVproTimer=60;//设为60s延时
			while(EVproTimer>0)
			{
				if(EV_TYPE==EV_TRADE_RPT)
				{
					result=1;
					EVproTimer=0;
					//情空回传参数
					device=0;//出货柜号		
					status=0;//出货结果
					hdid=0;//货道id
					type=0;//出货类型
					cost=0;//扣钱
					totalvalue=0;//剩余金额
					huodao=0;//剩余存货数量
				}
			}
		}
		//请求发送失败
		return result;
	}
	
}
