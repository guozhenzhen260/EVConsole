package com.easivend.evprotocol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ExtraCOMThread implements Runnable {

	private Handler mainhand=null,childhand=null;
	public static final int EV_OPTMAIN	= 9;	//所有设备操作返回
	Timer timer = new Timer(); 
	private static Map<String,Object> allSet = new LinkedHashMap<String,Object>() ;
	int onInit=0;//0表示没有初始化，其他值表示正在初始化的阶段
	boolean cmdSend=false;//true发送的命令，等待ACK确认,这个值只用于需要回复ACK的命令
	int devopt=0;//命令状态值	
	int statusnum=0;//达到一个值时发送一次get_status
	//现金设备使能禁能
	int bill=0;
	int coin=0;
	int opt=0;
	//现金设备金额	
	int coin_remain=0;//硬币器当前储币金额	以分为单位
	int payback_value=0;//找零金额
	
	int g_holdValue = 0;//当前暂存纸币金额 以分为单位
	int coin_recv=0;//硬币器当前收币金额	以分为单位
	int bill_recv=0;//纸币器当前收币金额	以分为单位
	
	int cost_value=0;//现金设备扣款金额
	
	int billPay=0;//纸币退币金额
	int coinPay=0;//硬币退币金额
	
	
	/*********************************************************************************************************
	** Function name:     	GetAmountMoney
	** Descriptions:	    投币总金额
	** input parameters:    无
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	int GetAmountMoney()
	{	
		return coin_recv + bill_recv + g_holdValue;
	}
	
	public ExtraCOMThread(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraThread start["+Thread.currentThread().getId()+"]","com.txt");
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{	
				//冰山柜	
				case COMThread.EV_BENTO_CHECKALLCHILD://子线程接收主线程冰山全部查询消息		
					//1.得到信息
					JSONObject ev6=null;
					try {
						ev6 = new JSONObject(msg.obj.toString());
						devopt=COMThread.EV_BENTO_CHECKALLCHILD;						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case COMThread.EV_BENTO_CHECKCHILD://子线程接收主线程冰山柜查询消息	
					//1.得到信息
					JSONObject ev7=null;
					try {
						ev7 = new JSONObject(msg.obj.toString());
						devopt=COMThread.EV_BENTO_CHECKCHILD;						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能					
					//1.得到信息
					JSONObject ev=null;
					try {
						ev = new JSONObject(msg.obj.toString());
						bill=ev.getInt("bill");
						coin=ev.getInt("coin");
						opt=ev.getInt("opt");
						devopt=EVprotocol.EV_MDB_ENABLE;						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace(); 
					}										
					break;
				case EVprotocol.EV_MDB_B_INFO://子线程接收主线程现金设备
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
					for(int i=1;i<9;i++)
					{
						allSet.put("ch_r"+i, 0);								
					}
					
					for(int i=1;i<9;i++)
					{
						allSet.put("ch_d"+i, 0);								
					}
					//3.向主线程返回信息
	  				Message tomain11=mainhand.obtainMessage();
	  				tomain11.what=EV_OPTMAIN;							
	  				tomain11.obj=allSet;
	  				mainhand.sendMessage(tomain11); // 发送消息
					
					break;	
				case EVprotocol.EV_MDB_C_INFO://子线程接收主线程现金设备
					//往接口回调信息
					allSet.clear();
					allSet.put("EV_TYPE", EVprotocol.EV_MDB_C_INFO);
					allSet.put("acceptor", 0);
					allSet.put("dispenser", 0);
					allSet.put("code", 0);
					allSet.put("sn", 0);
					allSet.put("model", 0);
					allSet.put("ver", 0);
					allSet.put("capacity", 0);
					for(int i=1;i<17;i++)
					{
						allSet.put("ch_r"+i, 0);								
					}
					
					for(int i=1;i<9;i++)
					{
						allSet.put("ch_d"+i, 0);								
					}
					//3.向主线程返回信息
	  				Message tomain12=mainhand.obtainMessage();
	  				tomain12.what=EV_OPTMAIN;							
	  				tomain12.obj=allSet;
	  				mainhand.sendMessage(tomain12); // 发送消息
					
					break;	
				case EVprotocol.EV_MDB_PAYOUT://MDB设备找币
					//1.得到信息
					JSONObject ev16=null;
					try {
						ev16 = new JSONObject(msg.obj.toString());
						bill=ev16.getInt("bill");
						coin=ev16.getInt("coin");
						billPay=ev16.getInt("billPay");
						coinPay=ev16.getInt("coinPay");	
						devopt=EVprotocol.EV_MDB_PAYOUT;
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}				
					break;	
					//交易页面使用	
				case EVprotocol.EV_MDB_HEART://子线程接收主线程现金设备
					devopt=EVprotocol.EV_MDB_HEART;	
					break;
				case EVprotocol.EV_MDB_COST:					
					//1.得到信息
					JSONObject ev18=null;
					try {
						ev18 = new JSONObject(msg.obj.toString());
						cost_value=ev18.getInt("cost");
						devopt=EVprotocol.EV_MDB_COST;						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;	
				case EVprotocol.EV_MDB_PAYBACK:
					//1.得到信息
					JSONObject ev19=null;
					try {
						ev19 = new JSONObject(msg.obj.toString());
						bill=ev19.getInt("bill");
						coin=ev19.getInt("coin");
						devopt=EVprotocol.EV_MDB_PAYBACK;						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
					break;	
				}
			}
		};	
		timer.scheduleAtFixedRate(task, 5000, 100);       // timeTask
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop		 
	}
	
	TimerTask task = new TimerTask() 
	{ 
        @Override 
        public void run() 
        { 
        	if(ToolClass.getExtraComType()>0)
        	{
	        	//ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraThread Timer["+Thread.currentThread().getId()+"]","com.txt");
	        	String resjson = VboxProtocol.VboxReadMsg(ToolClass.getExtracom_id(),100);
	        	
	        	//2.重新组包
				try {
					JSONObject jsonObject6 = new JSONObject(resjson); 
					//根据key取出内容
					JSONObject ev_head6 = (JSONObject) jsonObject6.getJSONObject("EV_json");
					int str_evType6 =  ev_head6.getInt("EV_type");
					if(str_evType6==VboxProtocol.VBOX_PROTOCOL)
					{
						int mt= ev_head6.getInt("mt");
						if(mt == VboxProtocol.VBOX_TIMEOUT || mt == VboxProtocol.VBOX_DATA_ERROR)
						{
							//ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraAPI<<ERROR="+mt,"com.txt");
				        }
						else
						{
							//1.发送ACK
							int F7=ev_head6.getInt("F7");
							if(F7 == 1 && mt != VboxProtocol.VBOX_POLL){
			                    VboxProtocol.VboxSendAck(ToolClass.getExtracom_id());
			                }
							
							switch(mt)
							{
								case VboxProtocol.VBOX_ACK_RPT:	
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraACK<<ACK","com.txt");
									if(cmdSend)
									{
										switch(devopt)
										{
											case VboxProtocol.VBOX_HUODAO_IND:
												if(onInit==2)
												{
													devopt=VboxProtocol.VBOX_SALEPRICE_IND;
													cmdSend=false;
													onInit=3;//saleprice_ind阶段
												}
												break;
											case VboxProtocol.VBOX_SALEPRICE_IND:	
												if(onInit==3)
												{
													devopt=COMThread.EV_BENTO_CHECKCHILD;
													cmdSend=false;
													onInit=4;//get_huodao阶段
												}
												break;
											case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadENABLERec<<bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_ENABLE);
												allSet.put("opt", opt);
												allSet.put("bill_result", bill);
												allSet.put("coin_result", coin);												
												//3.向主线程返回信息
												Message tomain=mainhand.obtainMessage();
												tomain.what=EV_OPTMAIN;							
												tomain.obj=allSet;
												mainhand.sendMessage(tomain); // 发送消息
												break;	
											case EVprotocol.EV_MDB_PAYOUT://MDB设备找币
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYOUTRec<<coinPay="+coinPay,"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;	
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYOUT);
												allSet.put("result", 1);
												allSet.put("bill_changed", 0);
												allSet.put("coin_changed", coinPay);
												//3.向主线程返回信息
								  				Message tomain16=mainhand.obtainMessage();
								  				tomain16.what=EV_OPTMAIN;							
								  				tomain16.obj=allSet;
								  				mainhand.sendMessage(tomain16); // 发送消息
												break;	
											case EVprotocol.EV_MDB_COST://扣款
												//消除变量值
												devopt=0;
												cmdSend=false;
												break;
											case EVprotocol.EV_MDB_PAYBACK://退币
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYBACKRec<<GetAmountMoney="+GetAmountMoney(),"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYBACK);
												allSet.put("result", 1);
												allSet.put("bill_changed", 0);
												allSet.put("coin_changed", GetAmountMoney());
												//3.向主线程返回信息
								  				Message tomain19=mainhand.obtainMessage();
								  				tomain19.what=EV_OPTMAIN;							
								  				tomain19.obj=allSet;
								  				mainhand.sendMessage(tomain19); // 发送消息
												break;
										}
									}
									break;
								case VboxProtocol.VBOX_NAK_RPT:	
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraNAK<<NAK","com.txt");
									if(cmdSend)
									{
										switch(devopt)
										{
											case VboxProtocol.VBOX_HUODAO_IND:
												if(onInit==2)
												{
													devopt=VboxProtocol.VBOX_SALEPRICE_IND;
													cmdSend=false;
													onInit=3;//saleprice_ind阶段
												}
												break;
											case VboxProtocol.VBOX_SALEPRICE_IND:	
												if(onInit==3)
												{
													devopt=COMThread.EV_BENTO_CHECKCHILD;
													cmdSend=false;
													onInit=4;//get_huodao阶段
												}
												break;
											case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadENABLERec<<bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_ENABLE);
												allSet.put("opt", opt);
												allSet.put("bill_result", bill);
												allSet.put("coin_result", coin);												
												//3.向主线程返回信息
												Message tomain=mainhand.obtainMessage();
												tomain.what=EV_OPTMAIN;							
												tomain.obj=allSet;
												mainhand.sendMessage(tomain); // 发送消息
												break;	
											case EVprotocol.EV_MDB_PAYOUT://MDB设备找币
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYOUTRec<<coinPay="+coinPay,"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;	
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYOUT);
												allSet.put("result", 1);
												allSet.put("bill_changed", 0);
												allSet.put("coin_changed", coinPay);
												//3.向主线程返回信息
								  				Message tomain16=mainhand.obtainMessage();
								  				tomain16.what=EV_OPTMAIN;							
								  				tomain16.obj=allSet;
								  				mainhand.sendMessage(tomain16); // 发送消息
												break;	
											case EVprotocol.EV_MDB_COST://扣款
												//消除变量值
												devopt=0;
												cmdSend=false;
												break;
											case EVprotocol.EV_MDB_PAYBACK://退币
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYBACKRec<<GetAmountMoney="+GetAmountMoney(),"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYBACK);
												allSet.put("result", 1);
												allSet.put("bill_changed", 0);
												allSet.put("coin_changed", GetAmountMoney());
												//3.向主线程返回信息
								  				Message tomain19=mainhand.obtainMessage();
								  				tomain19.what=EV_OPTMAIN;							
								  				tomain19.obj=allSet;
								  				mainhand.sendMessage(tomain19); // 发送消息
												break;
										}
									}
									break;	
								case VboxProtocol.VBOX_POLL:								
									//ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraPOLL<<"+resjson.toString(),"com.txt");
									switch(devopt)
									{
										//准备初始化阶段
										case COMThread.EV_BENTO_CHECKALLCHILD://子线程接收主线程冰山柜全部查询消息
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCHECKALLCHILDSend0.2>>","com.txt");
												VboxProtocol.VboxGetSetup(ToolClass.getExtracom_id());
												onInit=1;//setup阶段
												cmdSend=true;
											}
											break;
										case VboxProtocol.VBOX_HUODAO_IND:
											if((onInit==2)&&(cmdSend==false))
											{
												JSONArray arr=new JSONArray();
												for(int i=1;i<22;i++)
												{
													JSONObject obj=new JSONObject();
													obj.put("id", i);
													arr.put(obj);
												}
												JSONObject zhuheobj=new JSONObject();
												zhuheobj.put("port", ToolClass.getExtracom_id());
												zhuheobj.put("sp_id", arr);										
												zhuheobj.put("device", 0);
												zhuheobj.put("EV_type", VboxProtocol.VBOX_PROTOCOL);
												JSONObject reqStr=new JSONObject();
												reqStr.put("EV_json", zhuheobj);
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHuodaoind<<"+reqStr,"com.txt");
												VboxProtocol.VboxHuodaolInd(ToolClass.getExtracom_id(),reqStr.toString());
												cmdSend=true;
											}
											break;
										case VboxProtocol.VBOX_SALEPRICE_IND:
											if((onInit==3)&&(cmdSend==false))
											{
												JSONArray arr=new JSONArray();
												for(int i=1;i<22;i++)
												{
													JSONObject obj=new JSONObject();
													obj.put("id", 10);
													arr.put(obj);
												}
												JSONObject zhuheobj=new JSONObject();
												zhuheobj.put("port", ToolClass.getExtracom_id());
												zhuheobj.put("sp_id", arr);										
												zhuheobj.put("device", 0);
												zhuheobj.put("EV_type", VboxProtocol.VBOX_PROTOCOL);
												JSONObject reqStr=new JSONObject();
												reqStr.put("EV_json", zhuheobj);
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSalepriceind<<"+reqStr,"com.txt");
												VboxProtocol.VboxSalePriceInd(ToolClass.getExtracom_id(),reqStr.toString());
												cmdSend=true;
											}
											break;
										case COMThread.EV_BENTO_CHECKCHILD://子线程接收主线程冰山柜查询消息	
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCHECKCHILDSend0.2>>","com.txt");
												VboxProtocol.VboxGetHuoDao(ToolClass.getExtracom_id(),0);
												cmdSend=true;
											}
											break;										
										case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadENABLESend0.2>>bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												VboxProtocol.VboxControlInd(ToolClass.getExtracom_id(),2,opt);
												cmdSend=true;
											}
											break;
										case EVprotocol.EV_MDB_PAYOUT://MDB设备找币
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYOUTSend0.2>>bill="+bill+"coin="+coin+"billPay="+billPay+"coinPay="+coinPay,"com.txt");
												VboxProtocol.VboxPayoutInd(ToolClass.getExtracom_id(),0,coinPay/10,2);
												cmdSend=true;
											}											
											break;
										case EVprotocol.EV_MDB_HEART://心跳查询接口
											ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHEARTSend0.2>>","com.txt");
											if(++statusnum>2)
											{
												statusnum=0;
												VboxProtocol.VboxGetStatus(ToolClass.getExtracom_id());
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadGetStatus>>","com.txt");
											}
											//往接口回调信息
											allSet.clear();
											allSet.put("EV_TYPE", EVprotocol.EV_MDB_HEART);
											allSet.put("bill_enable", 1);
											allSet.put("bill_payback", 0);
											allSet.put("bill_err", 0);
											allSet.put("bill_recv", bill_recv+g_holdValue);
											allSet.put("bill_remain", 0);
											allSet.put("coin_enable", 1);
											allSet.put("coin_payback", 0);
											allSet.put("coin_err", 0);
											allSet.put("coin_recv", coin_recv);
											allSet.put("coin_remain", coin_remain);
											allSet.put("hopper1", 0);
											allSet.put("hopper2", 0);
											allSet.put("hopper3", 0);
											allSet.put("hopper4", 0);
											allSet.put("hopper5", 0);
											allSet.put("hopper6", 0);
											allSet.put("hopper7", 0);
											allSet.put("hopper8", 0);
											devopt=0;
											//3.向主线程返回信息
							  				Message tomain13=mainhand.obtainMessage();
							  				tomain13.what=EV_OPTMAIN;							
							  				tomain13.obj=allSet;
							  				mainhand.sendMessage(tomain13); // 发送消息
											break;
										case EVprotocol.EV_MDB_COST://扣款
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCOSTSend0.2>>cost="+cost_value,"com.txt");
												VboxProtocol.VboxCostInd(ToolClass.getExtracom_id(),0,cost_value/10,2);
												cmdSend=true;
											}
											break;	
										case EVprotocol.EV_MDB_PAYBACK://退币
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYBACKSend0.2>>bill="+bill+"coin="+coin,"com.txt");
												VboxProtocol.VboxControlInd(ToolClass.getExtracom_id(),6,0);
												cmdSend=true;
											}
											break;
										default:
											if(F7==1)
											{
												VboxProtocol.VboxSendAck(ToolClass.getExtracom_id());
											}
											break;
									}
									break;
								case VboxProtocol.VBOX_VMC_SETUP://开机setup的信息
									int hd_num=ev_head6.getInt("hd_num");
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSetupRpt<<hd_num="+hd_num,"com.txt");
									//初始化1.Get_Setup
									if((onInit==1)&&(cmdSend))
									{
										devopt=VboxProtocol.VBOX_HUODAO_IND;
										cmdSend=false;
										onInit=2;//huodao_ind阶段
									}
									break;
								case VboxProtocol.VBOX_PAYIN_RPT://投币信息
									int dt=ev_head6.getInt("dt");
									int value=ev_head6.getInt("value");
									if(dt==0)
									{
										coin_recv+=value*10;
									}
									else if(dt==100)
									{
										g_holdValue=value*10;
									}
									else if(dt==101)
									{
										g_holdValue=0;
									}
									else if(dt==1)
									{
										bill_recv+=value*10;
										g_holdValue=0;
									}
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayinRpt<<dt="+dt+"value="+(value*10)+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									break;	
								case VboxProtocol.VBOX_PAYOUT_RPT://找币信息
									payback_value=ev_head6.getInt("value")*10; 
									g_holdValue = 0;//当前暂存纸币金额 以分为单位
									bill_recv=0;//纸币器当前收币金额	以分为单位
									coin_recv=ev_head6.getInt("total_value")*10; ;//硬币器当前收币金额	以分为单位
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayoutRpt<<payback_value="+payback_value+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									break;
								case VboxProtocol.VBOX_COST_RPT://扣款信息
									cost_value=ev_head6.getInt("value")*10; 
									g_holdValue = 0;//当前暂存纸币金额 以分为单位
									bill_recv=0;//纸币器当前收币金额	以分为单位
									coin_recv=ev_head6.getInt("total_value")*10; ;//硬币器当前收币金额	以分为单位
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCostRpt<<cost_value="+cost_value+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_MDB_COST);
									allSet.put("result", 1);
									allSet.put("bill_recv", bill_recv);
									allSet.put("coin_recv", coin_recv);
									//3.向主线程返回信息
					  				Message tomain18=mainhand.obtainMessage();
					  				tomain18.what=EV_OPTMAIN;							
					  				tomain18.obj=allSet;
					  				mainhand.sendMessage(tomain18); // 发送消息
									
									break;	
								case VboxProtocol.VBOX_ACTION_RPT://心跳不用处理
									break;
								case VboxProtocol.VBOX_REQUEST://数据请求不用处理
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadRequest<<","com.txt");
									break;	
								case VboxProtocol.VBOX_BUTTON_RPT://按键消息
									int type=ev_head6.getInt("type");
									if(type==0)
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<Game","com.txt");
									else if(type==1)
									{
										int btntype=ev_head6.getInt("type");
										int btnvalue=ev_head6.getInt("value");
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<btntype="+btntype+"btnvalue="+btnvalue,"com.txt");
									}
									else if(type==2)
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<sp","com.txt");
									else if(type==4)
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<return","com.txt");
									break;
								case VboxProtocol.VBOX_STATUS_RPT://整机状态
									int bv_st=ev_head6.getInt("bv_st");
									int cc_st=ev_head6.getInt("cc_st");
									int vmc_st=ev_head6.getInt("vmc_st");
									int change=ev_head6.getInt("change");
									coin_remain=change*10;
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadStatusRpt<<bv_st"+bv_st+"cc_st="+cc_st+"vmc_st="+vmc_st+"coin_remain="+coin_remain,"com.txt");
									break;	
								case VboxProtocol.VBOX_HUODAO_RPT://货道信息
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHuodao<<"+resjson.toString(),"com.txt");
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", VboxProtocol.VBOX_HUODAO_RPT);
									allSet.put("cool", 0);
									allSet.put("hot", 0);
									allSet.put("light", 0);
									for(int i=1;i<22;i++)
									{
										allSet.put(String.valueOf(i), 1);								
									}		
									//表示是第一次初始化完成
									if(onInit==4)
									{
										//消除变量值
										devopt=0;
										cmdSend=false;
										onInit=0;
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHuodaoInit<<"+allSet,"com.txt");
										//3.向主线程返回信息
										Message tomain=mainhand.obtainMessage();
						  				tomain.what=COMThread.EV_CHECKALLMAIN;							
						  				tomain.obj=allSet;
						  				mainhand.sendMessage(tomain); // 发送消息
									}
									else if(cmdSend)
									{
										//消除变量值
										devopt=0;
										cmdSend=false;										
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHuodaoRpt<<"+allSet,"com.txt");
										//3.向主线程返回信息
										Message tomain=mainhand.obtainMessage();
						  				tomain.what=COMThread.EV_CHECKMAIN;							
						  				tomain.obj=allSet;
						  				mainhand.sendMessage(tomain); // 发送消息										
									}
									break;								
								case VboxProtocol.VBOX_INFO_RPT:
									int infotype=ev_head6.getInt("type");
									if(infotype==3)//当前余额
									{
										int total_value=ev_head6.getInt("total_value");
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadInfototal<<total_value="+total_value,"com.txt");
									}									
									break;									
								default:								
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadDefault<<"+resjson.toString(),"com.txt");
									break;
							}	
							
						}
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	        	
        	}
        } 
    };

}
