/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           BluetoothMsg.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        蓝牙类        
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.view;

public class BluetoothMsg {
	 /** 
     * 蓝牙连接类型 
     * @author Andy 
     * 
     */  
    public enum ServerOrCilent{  
        NONE,  
        SERVICE,  
        CILENT  
    };  
    //蓝牙连接方式  
    public static ServerOrCilent serviceOrCilent = ServerOrCilent.NONE;  
    //连接蓝牙地址  
    public static String BlueToothAddress = null,lastblueToothAddress=null;  
    //通信线程是否开启  
    public static boolean isOpen = false;  
}
