package com.easivend.evprotocol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	private static Map<String,Object> allSet = new LinkedHashMap<String,Object>() ;
	int onInit=0;//0表示没有初始化，其他值表示正在初始化的阶段
	boolean cmdSend=false;//true发送的命令，等待ACK确认,这个值只用于需要回复ACK的命令
	int devopt=0;//命令状态值	
	//货道出货
	int cabinet=0;
	int column=0;
	//现金设备使能禁能
	int bill=0;
	int coin=0;
	int opt=0;
	//现金设备金额		
	int cost_value=0;//现金设备扣款金额
	
	int billPay=0;//纸币退币金额
	int coinPay=0;//硬币退币金额
	
		
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
				case EVprotocol.EV_BENTO_OPEN://子线程接收主线程格子开门
					//1.得到信息
					JSONObject ev2=null;
					try {
						ev2 = new JSONObject(msg.obj.toString());
						cabinet=ev2.getInt("cabinet");
						column=ev2.getInt("column");
						devopt=EVprotocol.EV_BENTO_OPEN;						
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
				case VboxProtocol.VBOX_PROTOCOL:
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMThread 冰山柜关闭","com.txt");
					timer.shutdown();
					break;
				}
			}
		};	
		timer.scheduleWithFixedDelay(task, 5000, 100,TimeUnit.MILLISECONDS);       // timeTask
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop		 
	}
	
	TimerTask task = new TimerTask() 
	{ 
		int onInit=0;//0表示没有初始化，其他值表示正在初始化的阶段
		boolean cmdSend=false;//true发送的命令，等待ACK确认,这个值只用于需要回复ACK的命令
		int statusnum=0;//达到一个值时发送一次get_status
		int bill_err=0;//纸币器故障状态
		int coin_err=0;//硬币器故障状态
		//现金设备金额	
		int coin_remain=0;//硬币器当前储币金额	以分为单位
		int payback_value=0;//找零金额
		
		int g_holdValue = 0;//当前暂存纸币金额 以分为单位
		int coin_recv=0;//硬币器当前收币金额	以分为单位
		int bill_recv=0;//纸币器当前收币金额	以分为单位
			
		
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
		
		int hd_num=0;//货道数量
		int  decimalPlaces=1;//基本单位1角,2分,0元
		//设置decimalPlaces比例因子
		void setDecimal(int decimal_places)
		{
			if(decimal_places==0)
			{
				 decimalPlaces=1;
			}
		}
		/***********************************************************
		vmc内部处理都是以分为单位，但是在上传给pc时需要转换

		decimalPlaces=1以角为单位
			例如，需要上传给pc的是200分,
			200*10=2000
			2000/100=20角，即上传20角
			
		decimalPlaces=2以分为单位
			例如，需要上传给pc的是200分,
			200*100=20000
			20000/100=200分，即上传200分	

		decimalPlaces=0以元为单位
			例如，需要上传给pc的是200分,
			200=200
			200/100=2元，即上传2元	
		***************************************************************/
		int   MoneySend(int sendMoney)
		{
			int tempMoney=0;
			
			//公式2: 上传ScaledValue = ActualValue元/(10-DecimalPlaces次方)
			if(decimalPlaces==1)
			{
				//tempMoney = sendMoney*10;
				//tempMoney = tempMoney/100;
				tempMoney = sendMoney/10;
			}
			else if(decimalPlaces==2)
			{
				tempMoney = sendMoney;
			}
			else if(decimalPlaces==0)
			{
				//tempMoney = sendMoney;
				//tempMoney = tempMoney/100;
				tempMoney = sendMoney/100;
			}
			//例如:600分=6元
			return tempMoney;
		}
		
		/***********************************************************
		vmc内部处理都是以分为单位，所以接收pc传下来的数据时需要转换

		decimalPlaces=1以角为单位
			例如，pc下传是20角,
			20*100=2000
			2000/10=200分，即接收200分
			
		decimalPlaces=2以分为单位
			例如，pc下传是200分,
			200*100=20000
			20000/100=200分，即接收200分	

		decimalPlaces=0以元为单位
			例如，pc下传是2元,
			2*100=200
			200=200分，即接收200分
		***************************************************************/
		int   MoneyRec(int recMoney)
		{
			int tempMoney;
			
			tempMoney = recMoney;
			//公式1:  ActualValue元 =下载ScaledValue*(10-DecimalPlaces次方)
			if(decimalPlaces==1)
			{
				//tempMoney = tempMoney*100;
				//tempMoney = tempMoney/10;
				tempMoney = tempMoney*10;
			}
			else if(decimalPlaces==2)
			{
				tempMoney = tempMoney;
			}
			else if(decimalPlaces==0)
			{
				tempMoney = tempMoney*100;
				//tempMoney = tempMoney;
			}
			//例如:6元=600分	
			return tempMoney;
		}
		
        @Override 
        public void run() 
        { 
        	if(ToolClass.getExtraComType()>0)
        	{
	        	//ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraThread Timer["+Thread.currentThread().getId()+"]","com.txt");
	        	String resjson = VboxProtocol.VboxReadMsg(ToolClass.getExtracom_id(),100);
	        	//ToolClass.Log(ToolClass.INFO,"EV_COM","Threadresjson<<"+resjson.toString(),"com.txt");
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
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraACK<<ACK,cmd="+cmdSend+"devopt="+devopt+"onInit="+onInit,"com.txt");
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
											case EVprotocol.EV_BENTO_OPEN://子线程接收主线程格子开门
												//消除变量值
												devopt=0;
												cmdSend=false;
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
												//消除变量值
												//devopt=0;//在pyout_rpt处理
												//cmdSend=false;													
												break;	
											case EVprotocol.EV_MDB_COST://扣款
												//消除变量值
												devopt=0;
												cmdSend=false;
												break;
											case EVprotocol.EV_MDB_PAYBACK://退币
												//消除变量值
												//devopt=0;//在pyout_rpt处理
												//cmdSend=false;												
												break;
										}
									}
									break;
								case VboxProtocol.VBOX_NAK_RPT:	
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraNAK<<NAK,cmd="+cmdSend+"devopt="+devopt+"onInit="+onInit,"com.txt");
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
											case EVprotocol.EV_BENTO_OPEN://子线程接收主线程格子开门
												//消除变量值
												devopt=0;
												cmdSend=false;
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadVendoutRpt<<column="+column+"status="+2,"com.txt");
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_BENTO_OPEN);
												allSet.put("addr", 0);//柜子地址
												allSet.put("box", 0);//格子地址
												allSet.put("result", 0);
												//3.向主线程返回信息
								  				Message tomain2=mainhand.obtainMessage();
								  				tomain2.what=EV_OPTMAIN;							
								  				tomain2.obj=allSet;
								  				mainhand.sendMessage(tomain2); // 发送消息
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
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayoutRpt<<EV_MDB_PAYOUT="+0,"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;	
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYOUT);
												allSet.put("result", 1);
												allSet.put("bill_changed", 0);
												allSet.put("coin_changed", 0);
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
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCostRpt<<cost_value="+0+"GetAmountMoney="+GetAmountMoney(),"com.txt");
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_COST);
												allSet.put("result", 1);
												allSet.put("cost", 0);
												allSet.put("bill_recv", 0);
												allSet.put("coin_recv", GetAmountMoney());
												//3.向主线程返回信息
								  				Message tomain18=mainhand.obtainMessage();
								  				tomain18.what=EV_OPTMAIN;							
								  				tomain18.obj=allSet;
								  				mainhand.sendMessage(tomain18); // 发送消息
												break;
											case EVprotocol.EV_MDB_PAYBACK://退币
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayoutRpt<<EV_MDB_PAYBACK="+0,"com.txt");
												//消除变量值
												devopt=0;
												cmdSend=false;
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYBACK);
												allSet.put("result", 1);
												allSet.put("bill_changed", 0);
												allSet.put("coin_changed", 0);
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
												if(++statusnum>2)
												{
													statusnum=0;
													ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadGetSetup>>","com.txt");
													VboxProtocol.VboxGetSetup(ToolClass.getExtracom_id());
													onInit=1;//setup阶段
													cmdSend=true;
												}
												else
												{
													ToolClass.Log(ToolClass.INFO,"EV_COM","Threadstatusnumwait="+statusnum,"com.txt");
													if(F7==1)
													{
														VboxProtocol.VboxSendAck(ToolClass.getExtracom_id());
													}
												}
											}
											break;
										case VboxProtocol.VBOX_HUODAO_IND:
											if((onInit==2)&&(cmdSend==false))
											{
												if(++statusnum>2)
												{
													statusnum=0;
													JSONArray arr=new JSONArray();
													for(int i=1;i<(hd_num+1);i++)
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
													ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHuodaoind>>"+reqStr,"com.txt");
													VboxProtocol.VboxHuodaolInd(ToolClass.getExtracom_id(),reqStr.toString());
													cmdSend=true;
												}
												else
												{
													ToolClass.Log(ToolClass.INFO,"EV_COM","Threadstatusnumwait="+statusnum,"com.txt");
													if(F7==1)
													{
														VboxProtocol.VboxSendAck(ToolClass.getExtracom_id());
													}
												}
											}
											break;
										case VboxProtocol.VBOX_SALEPRICE_IND:
											if((onInit==3)&&(cmdSend==false))
											{
												if(++statusnum>2)
												{
													statusnum=0;
													JSONArray arr=new JSONArray();
													for(int i=1;i<(hd_num+1);i++)
													{
														JSONObject obj=new JSONObject();
														obj.put("id", MoneySend(56800));//
														arr.put(obj);
													}
													JSONObject zhuheobj=new JSONObject();
													zhuheobj.put("port", ToolClass.getExtracom_id());
													zhuheobj.put("sp_id", arr);										
													zhuheobj.put("device", 0);
													zhuheobj.put("EV_type", VboxProtocol.VBOX_PROTOCOL);
													JSONObject reqStr=new JSONObject();
													reqStr.put("EV_json", zhuheobj);
													ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSalepriceind>>"+reqStr,"com.txt");
													VboxProtocol.VboxSalePriceInd(ToolClass.getExtracom_id(),reqStr.toString());
													cmdSend=true;
												}
												else
												{
													ToolClass.Log(ToolClass.INFO,"EV_COM","Threadstatusnumwait="+statusnum,"com.txt");
													if(F7==1)
													{
														VboxProtocol.VboxSendAck(ToolClass.getExtracom_id());
													}
												}	
											}
											break;
										case COMThread.EV_BENTO_CHECKCHILD://子线程接收主线程冰山柜查询消息	
											if(cmdSend==false)
											{
												//初始化
												if(onInit==4)
												{
													if(++statusnum>2)
													{
														statusnum=0;
														ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadGetHuodao>>","com.txt");
														VboxProtocol.VboxGetHuoDao(ToolClass.getExtracom_id(),0);
														cmdSend=true;
													}
													else
													{
														ToolClass.Log(ToolClass.INFO,"EV_COM","Threadstatusnumwait="+statusnum,"com.txt");
														if(F7==1)
														{
															VboxProtocol.VboxSendAck(ToolClass.getExtracom_id());
														}
													}
												}
												//正常GetHuodao
												else
												{
													ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadGetHuodao>>","com.txt");
													VboxProtocol.VboxGetHuoDao(ToolClass.getExtracom_id(),0);
													cmdSend=true;
												}
											}
											break;	
										case EVprotocol.EV_BENTO_OPEN://子线程接收主线程格子开门
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadVendoutind>>cabinet="+cabinet+"column="+column,"com.txt");
												VboxProtocol.VboxVendoutInd(ToolClass.getExtracom_id(), 0, 2, column, 2, 0);
												cmdSend=true;
											}
											break;
										case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadENABLE>>bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												VboxProtocol.VboxControlInd(ToolClass.getExtracom_id(),2,opt);
												cmdSend=true;
											}
											break;
										case EVprotocol.EV_MDB_PAYOUT://MDB设备找币
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYOUTind>>bill="+bill+"coin="+coin+"billPay="+billPay+"coinPay="+coinPay,"com.txt");
												VboxProtocol.VboxPayoutInd(ToolClass.getExtracom_id(),0,MoneySend(coinPay),2);
												cmdSend=true;
											}											
											break;
										case EVprotocol.EV_MDB_HEART://心跳查询接口
											ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHEART>>","com.txt");
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
											allSet.put("bill_err", bill_err);
											allSet.put("bill_recv", bill_recv+g_holdValue);
											allSet.put("bill_remain", 0);
											allSet.put("coin_enable", 1);
											allSet.put("coin_payback", 0);
											allSet.put("coin_err", coin_err);
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
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCOSTind>>cost="+cost_value,"com.txt");
												VboxProtocol.VboxCostInd(ToolClass.getExtracom_id(),0,MoneySend(cost_value),2);
												cmdSend=true;
											}
											break;	
										case EVprotocol.EV_MDB_PAYBACK://退币
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPAYBACK>>bill="+bill+"coin="+coin,"com.txt");
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
									//初始化1.Get_Setup
									if((onInit==1)&&(cmdSend))
									{
										hd_num=ev_head6.getInt("hd_num");
										int decimal_places=ev_head6.getInt("decimal_places");											
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadSetupRpt<<"+ev_head6,"com.txt");
										setDecimal(decimal_places);
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
										coin_recv+=MoneyRec(value);
									}
									else if(dt==100)
									{
										g_holdValue=MoneyRec(value);
									}
									else if(dt==101)
									{
										g_holdValue=0;
									}
									else if(dt==1)
									{
										bill_recv+=MoneyRec(value);
										g_holdValue=0;
									}
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayinRpt<<dt="+dt+"value="+MoneyRec(value)+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									break;	
								case VboxProtocol.VBOX_PAYOUT_RPT://找币信息
									payback_value=MoneyRec(ev_head6.getInt("value")); 
									g_holdValue = 0;//当前暂存纸币金额 以分为单位
									bill_recv=0;//纸币器当前收币金额	以分为单位
									coin_recv=MoneyRec(ev_head6.getInt("total_value"));//硬币器当前收币金额	以分为单位
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayoutRpt<<payback_value="+payback_value+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									//MDB设备找币
									if(devopt==EVprotocol.EV_MDB_PAYOUT)
									{
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayoutRpt<<EV_MDB_PAYOUT="+payback_value,"com.txt");
										//消除变量值
										devopt=0;
										cmdSend=false;
										//往接口回调信息
										allSet.clear();
										allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYOUT);
										allSet.put("result", 1);
										allSet.put("bill_changed", 0);
										allSet.put("coin_changed", payback_value);
										//3.向主线程返回信息
										Message tomain16=mainhand.obtainMessage();
										tomain16.what=EV_OPTMAIN;							
										tomain16.obj=allSet;
										mainhand.sendMessage(tomain16); // 发送消息										
									}
									//退币命令
									else if(devopt==EVprotocol.EV_MDB_PAYBACK)
									{
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadPayoutRpt<<EV_MDB_PAYBACK="+payback_value,"com.txt");
										//消除变量值
										devopt=0;
										cmdSend=false;
										//往接口回调信息
										allSet.clear();
										allSet.put("EV_TYPE", EVprotocol.EV_MDB_PAYBACK);
										allSet.put("result", 1);
										allSet.put("bill_changed", 0);
										allSet.put("coin_changed", payback_value);
										//3.向主线程返回信息
										Message tomain19=mainhand.obtainMessage();
										tomain19.what=EV_OPTMAIN;							
										tomain19.obj=allSet;
										mainhand.sendMessage(tomain19); // 发送消息
									}
									break;
								case VboxProtocol.VBOX_VENDOUT_RPT://出货结果
									int status=ev_head6.getInt("status");
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadVendoutRpt<<column="+column+"status="+status,"com.txt");
									//出货成功
									if(status==0)
									{
										//往接口回调信息
										allSet.clear();
										allSet.put("EV_TYPE", EVprotocol.EV_BENTO_OPEN);
										allSet.put("addr", 1);//柜子地址
										allSet.put("box", column);//格子地址
										allSet.put("result", 1);
									}
									//出货失败
									else
									{
										//往接口回调信息
										allSet.clear();
										allSet.put("EV_TYPE", EVprotocol.EV_BENTO_OPEN);
										allSet.put("addr", 0);//柜子地址
										allSet.put("box", 0);//格子地址
										allSet.put("result", 0);
									}
									//3.向主线程返回信息
					  				Message tomain2=mainhand.obtainMessage();
					  				tomain2.what=EV_OPTMAIN;							
					  				tomain2.obj=allSet;
					  				mainhand.sendMessage(tomain2); // 发送消息
									
									break;
								case VboxProtocol.VBOX_COST_RPT://扣款信息
									cost_value=MoneyRec(ev_head6.getInt("value")); 
									g_holdValue = 0;//当前暂存纸币金额 以分为单位
									bill_recv=0;//纸币器当前收币金额	以分为单位
									coin_recv=MoneyRec(ev_head6.getInt("total_value"));//硬币器当前收币金额	以分为单位
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadCostRpt<<cost_value="+cost_value+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", EVprotocol.EV_MDB_COST);
									allSet.put("result", 1);
									allSet.put("cost", cost_value);
									allSet.put("bill_recv", bill_recv);
									allSet.put("coin_recv", coin_recv);
									//3.向主线程返回信息
					  				Message tomain18=mainhand.obtainMessage();
					  				tomain18.what=EV_OPTMAIN;							
					  				tomain18.obj=allSet;
					  				mainhand.sendMessage(tomain18); // 发送消息
									
									break;	
								case VboxProtocol.VBOX_ACTION_RPT://心跳不用处理
									//往接口回调信息
									allSet.clear();
									int action=ev_head6.getInt("action");									
									//  心跳                     出货开始                 出币开始
									if((action==0)||(action==1)||(action==2))
									{										
									}
									else if(action==5)//维护
									{
										int actvalue=ev_head6.getInt("value");
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadAction<<维护模式","com.txt");
										allSet.put("EV_TYPE", COMThread.EV_BUTTONRPT_MAINTAIN);
										allSet.put("btnvalue", actvalue);
										//3.向主线程返回信息
						  				Message tomain19=mainhand.obtainMessage();
						  				tomain19.what=COMThread.EV_BUTTONMAIN;							
						  				tomain19.obj=allSet;
						  				mainhand.sendMessage(tomain19); // 发送消息
									}
									
									break;
								case VboxProtocol.VBOX_REQUEST://数据请求不用处理
									//ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadRequest<<","com.txt");
									break;	
								case VboxProtocol.VBOX_BUTTON_RPT://按键消息
									//往接口回调信息
									allSet.clear();
									int type=ev_head6.getInt("type");									
									if(type==0)//游戏按键
									{
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<Game","com.txt");
										allSet.put("EV_TYPE", COMThread.EV_BUTTONRPT_GAME);
									}
									else if(type==1)//货道按键
									{
										int btntype=ev_head6.getInt("type");
										int btnvalue=ev_head6.getInt("value");
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<Huodaobtntype="+btntype+"btnvalue="+btnvalue,"com.txt");
										allSet.put("EV_TYPE", COMThread.EV_BUTTONRPT_HUODAO);
										allSet.put("btnvalue", btnvalue);
									}
									else if(type==2)//商品按键
									{
										int btntype=ev_head6.getInt("type");
										int btnvalue=ev_head6.getInt("value");
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<Spbtntype="+btntype+"btnvalue="+btnvalue,"com.txt");
										allSet.put("EV_TYPE", COMThread.EV_BUTTONRPT_SP);
										allSet.put("btnvalue", btnvalue);
									}
									else if(type==4)//退币按键
									{
										ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadButtonRpt<<Return","com.txt");
										allSet.put("EV_TYPE", COMThread.EV_BUTTONRPT_RETURN);
									}
									
									//3.向主线程返回信息
					  				Message tomain19=mainhand.obtainMessage();
					  				tomain19.what=COMThread.EV_BUTTONMAIN;							
					  				tomain19.obj=allSet;
					  				mainhand.sendMessage(tomain19); // 发送消息
									break;
								case VboxProtocol.VBOX_STATUS_RPT://整机状态
									bill_err=ev_head6.getInt("bv_st");
									coin_err=ev_head6.getInt("cc_st");
									int vmc_st=ev_head6.getInt("vmc_st");
									int change=ev_head6.getInt("change");
									coin_remain=MoneyRec(change);
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadStatusRpt<<bv_st="+bill_err+"cc_st="+coin_err+"vmc_st="+vmc_st+"coin_remain="+coin_remain,"com.txt");
									break;	
								case VboxProtocol.VBOX_HUODAO_RPT://货道信息
									ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadHuodao<<"+resjson.toString(),"com.txt");
									//往接口回调信息
									allSet.clear();
									allSet.put("EV_TYPE", VboxProtocol.VBOX_HUODAO_RPT);
									allSet.put("cool", 0);
									allSet.put("hot", 0);
									allSet.put("light", 0);
									JSONArray arr=ev_head6.getJSONArray("huodao");//返回json数组
									//ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道2:"+arr.toString());
									for(int i=0;i<arr.length();i++)
									{
										JSONObject object2=arr.getJSONObject(i);
										allSet.put(String.valueOf(object2.getInt("no")), object2.getInt("remain"));								
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
