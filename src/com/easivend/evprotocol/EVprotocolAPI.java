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
	static EVprotocol ev=null;
	private static int EV_TYPE=0;
	private static final int EV_INITING=1;//正在初始化
	private static final int EV_ONLINE=2;//成功连接
	private static final int EV_OFFLINE=3;//断开连接
	private static final int EV_RESTART=4;//主控板重启心动
	
	
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
		ev=new EVprotocol(EVProhand);
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
		return ev.trade(cabinet,column,type,cost);
	}
	
}
