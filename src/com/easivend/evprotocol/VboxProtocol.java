/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           VboxProtocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        java调用友宝冰山协议JNI接口封装类                   
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/


package com.easivend.evprotocol;

/**
 * Created by yoc on 2016/6/29.
 */
public class VboxProtocol {
	public static final int VBOX_PROTOCOL=911;
	//vmc->pc
    public static final int VBOX_ACK_RPT    = 0x01;
    public static final int VBOX_NAK_RPT    = 0x02;
    public static final int VBOX_POLL    = 0x03;
    public static final int VBOX_VMC_SETUP    = 0x05;
    public static final int VBOX_PAYIN_RPT    = 0x06;
    public static final int VBOX_PAYOUT_RPT    = 0x07;
    public static final int VBOX_HUODAO_RPT    = 0x0E;
    public static final int VBOX_VENDOUT_RPT    = 0x08;
    public static final int VBOX_COST_RPT    = 0x10;
    public static final int VBOX_INFO_RPT    = 0x11;
    public static final int VBOX_ACTION_RPT    = 0x0B;
    public static final int VBOX_BUTTON_RPT    = 0x0C;
    public static final int VBOX_STATUS_RPT    = 0x0D;
    //pc->vmc
    public static final int VBOX_ACK    = 0x80;
    public static final int VBOX_NAK    = 0x81;
    public static final int VBOX_RESET_IND     = 0x84;
    public static final int VBOX_GET_SETUP    = 0x90;
    public static final int VBOX_GET_HUODAO    = 0x8A;
    public static final int VBOX_HUODAO_IND    = 0x87;
    public static final int VBOX_POSITION_IND    = 0x88;
    public static final int VBOX_SALEPRICE_IND    = 0x8E;
    public static final int VBOX_HUODAO_SET_IND    = 0x8F;
    public static final int VBOX_PRICE_SET_IND    = 0x8E;
    public static final int VBOX_VENDOUT_IND    = 0x83;
    public static final int VBOX_CONTROL_IND    = 0x85;
    public static final int VBOX_GET_INFO    = 0x8C;
    public static final int VBOX_GET_INFO_EXP    = 0x92;
    public static final int VBOX_SET_HUODAO    = 0x93;
    public static final int VBOX_GET_STATUS    = 0x86;
    public static final int VBOX_PAYOUT_IND    = 0x89;
    public static final int VBOX_COST_IND      = 0x8B;

    public static final int VBOX_TIMEOUT    = 0xFE;
    public static final int VBOX_DATA_ERROR    = 0xEE;


    public static final int VBOX_PC_REQ_IDLE       =  0;  //空闲可以接受指令
    public static final int VBOX_PC_REQ_SENDREADY   = 3; //准备发送
    public static final int VBOX_PC_REQ_SENDING     = 1;  //正在发送
    public static final int VBOX_PC_REQ_HANDLING   =  2;  //正在处理

    public static final int VBOX_TIMEROUT_VMC = 10;  //10秒超时
    public static final int VBOX_TIMEROUT_PC   =10;  //30秒超时
    public static final int VBOX_TIMEROUT_PC_LONG   =120;  //30秒超时



    //VMC当前状态
    public static final int VBOX_STATE_DISCONNECT		=0;    //断开连接
    public static final int VBOX_STATE_INITTING		    =1;    //正在初始化
    public static final int VBOX_STATE_NORMAL			=2;    //正常
    public static final int VBOX_STATE_FAULT			=3;    //故障


    public static final int VBOX_MODEL_MANTAIN		    =1;     //维护模式
    public static final int VBOX_MODEL_RUN            =0;     //运行模式


    //基础协议
    public  native static String VboxSendAck(int port);

    public  native static String VboxReadMsg(int port,int timeout);

    public native static String VboxSendMsg(String msg);

    //包协议
    public native static String VboxGetSetup(int port);
    public native static String VboxGetHuoDao(int port,int device);
    public native static String VboxGetInfo(int port,int type);
    public native static String VboxGetStatus(int port);
    public native static String VboxResetInd(int port,int dt);
    public native static String VboxHuodaolInd(int port,String reqStr);
    public native static String VboxVendoutInd(int port,int device,int method,int id,int type,int cost);
    public native static String VboxPositionInd(int port,String reqStr);
    public native static String VboxHuodaoSetInd(int port,String reqStr);
    public native static String VboxSalePriceInd(int port,String reqStr);
    public native static String VboxPayoutInd(int port,int device,int value,int type);
    public native static String VboxCostInd(int port,int device,int value,int type);
    public native static String VboxControlInd(int port,int type,int value);

}

