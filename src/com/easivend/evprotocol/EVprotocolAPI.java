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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EVprotocolAPI 
{
	EVprotocol ev=null;
	//实例化hand邮箱，并且进行pend
	private Handler EVProhand=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			Log.i("EV_thread",msg.obj.toString());
			// TODO Auto-generated method stub
			switch (msg.what)
			{
				case 1://子线程接收主线程消息
					Log.i("EV_thread",msg.obj.toString());
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
	public  int vmcStart(String portName)
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
	public void vmcStop()
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
	public int trade(int cabinet,int column,int type,int cost)
	{
		return ev.trade(cabinet,column,type,cost);
	}
	
}
