/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        java调用JNI接口封装类                   
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.evprotocol;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class EVprotocol {
	
	
	public EVprotocol(){} //构造函数
	public EVprotocol(Handler handler){this.handler = handler;}//构造函数
	
	
	static{
		System.loadLibrary("EVprotocol");//加载JNI动态链接库
		
	}
	
	
	/*********************************************************************************************************
	** Function name:     	EV_callBack
	** Descriptions:	    VMC回调入口（所有较长的结果回应都会通过该函数回调显示）
	** input parameters:    json_msg:回应结果的JSON包。 
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public void EV_callBack(String json_msg)
	{
		Log.i("JSON", json_msg);
		//这里是JSON包 该处是通过handler发送消息的方式发送到主线程处理结果的，开发者可以选择自己的方式进行处理。
		//但是千万不要在此函数里做过多的处理。以免jni调用出错 或者 拖慢速度
		if(handler != null)
		{
			Message msg = Message.obtain();
			msg.what = 1;
			msg.obj = json_msg;
			handler.sendMessage(msg);
		}
	}

	
	/*********************************************************************************************************
	** Function name:     	vmcStart
	** Descriptions:	    VMC主控板开启接口
	** input parameters:    portName 串口号 例如/dev/tty0
	** output parameters:   无
	** Returned value:      1:开启成功      -1:开启失败  （直接返回 不进行回调）
	*********************************************************************************************************/
	public native int vmcStart(String portName);
	
	
	
	
	
	/*********************************************************************************************************
	** Function name:     	vmcStop
	** Descriptions:	    VMC主控板断开接口
	** input parameters:    无
	** output parameters:   无
	** Returned value:      无（直接返回 不进行回调）
	*********************************************************************************************************/
	public native void vmcStop();
	
	
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
	public native int trade(int cabinet,int column,int type,int cost);
	
	
	
	/*********************************************************************************************************
	** Function name:     	payout
	** Descriptions:	    VMC出币接口
	**						PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	** input parameters:    value:要出币的金额(单位:分)
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public native int payout(long value);
	
	
	
	/*********************************************************************************************************
	** Function name:     	getStatus
	** Descriptions:	             获取VMC状态接口  PC发送该指令后，首先判断返回值为1则请求发送成功。然后通过回调函数返回出货的结果进行解析
	** input parameters:    无
	** output parameters:   无
	** Returned value:      1请求发送成功   0:请求发送失败
	*********************************************************************************************************/
	public native int getStatus();
	
	
	/*********************************************************************************************************
	** Function name:     	getRemainAmount
	** Descriptions:	             获取VMC投币余额  不进行回调 直接返回当前余额 
	** input parameters:    无
	** output parameters:   无
	** Returned value:      返回当前余额 单位:分
	*********************************************************************************************************/
	public native long getRemainAmount();
	
		
	
	/*********************************************************************************************************
	** Function name:     	bentoRegister
	** Descriptions:	             便利柜初始化接口
	** input parameters:    portName 串口号 例如/dev/tty0
	** output parameters:   无
	** Returned value:      1:初始化成功      -1:初始化失败 (失败原因为串口打开失败)
	*********************************************************************************************************/
	public native int bentoRegister(String portName);
	
	
	
	/*********************************************************************************************************
	** Function name:     	bentoRelease
	** Descriptions:	             便利柜释放资源接口（与bentoRegister配套调用）
	** input parameters:    无
	** output parameters:   无
	** Returned value:      1:成功      （调用必定成功）
	*********************************************************************************************************/
	public native int bentoRelease();
	

	/*********************************************************************************************************
	** Function name:     	bentoOpen
	** Descriptions:	             便利柜开格子接口
	** input parameters:    cabinet:柜号     box:格子号
	** output parameters:   无
	** Returned value:      1:打开成功      0:打开失败
	*********************************************************************************************************/
	public native int bentoOpen(int cabinet,int box);
	
	
	
	/*********************************************************************************************************
	** Function name:     	bentoLight
	** Descriptions:	             便利柜照明控制接口
	** input parameters:    cabinet:柜号     flag:1 表示照明开    0:表示照明关
	** output parameters:   无
	** Returned value:      1:打开成功      0:打开失败
	*********************************************************************************************************/
	public native int bentoLight(int cabinet,int flag);//flag 1开灯  0关灯
	
	
	/*********************************************************************************************************
	** Function name:     	bentoCheck
	** Descriptions:	             便利柜状态查询接口
	** input parameters:    cabinet:柜号     
	** output parameters:   无
	** Returned value:      返回的是一个JSON包 例如{{}}
	*********************************************************************************************************/
	public native String bentoCheck(int cabinet);
	
	
	
	
		
	
	
	
	
	
	
	public Handler handler = null;
	

	//JNI 静态回调函数 示例代码 没有用
	public static void EV_callBackStatic(int i) 
	{
		Log.i("Java------------->","" +  i);
	}
	
}