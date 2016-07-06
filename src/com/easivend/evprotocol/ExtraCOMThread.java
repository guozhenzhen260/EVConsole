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
	boolean onInit=false;
	boolean cmdSend=false;//true发送的命令，等待ACK确认
	int devopt=0;//命令状态值	
	int statusnum=0;//20发送一次get_status
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
				//格子柜
				case 0x01://子线程接收主线程格子查询消息
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMExtraThread="+msg.obj,"com.txt");	
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
					//交易页面使用	
				case EVprotocol.EV_MDB_HEART://子线程接收主线程现金设备
					devopt=EVprotocol.EV_MDB_HEART;	
					break;	
				}
			}
		};	
		//Init();
		timer.scheduleAtFixedRate(task, 5000, 100);       // timeTask
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop		 
	}
	
	private void Init()
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
						ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraAPI<<"+resjson.toString(),"com.txt");
						int F7=ev_head6.getInt("F7");
						if(mt==VboxProtocol.VBOX_POLL)
						{
							//1.获取setup
							VboxProtocol.VboxGetSetup(ToolClass.getExtracom_id());
							String resjson1 = VboxProtocol.VboxReadMsg(ToolClass.getExtracom_id(),100);
							ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraAPI<<"+resjson1.toString(),"com.txt");
							//2.获取gethuodao
							VboxProtocol.VboxGetHuoDao(ToolClass.getExtracom_id(),0);
							resjson1 = VboxProtocol.VboxReadMsg(ToolClass.getExtracom_id(),100);
							ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraAPI<<"+resjson1.toString(),"com.txt");
							
							onInit=true;
						}
					}
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	        	
    	}
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
											case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadExtraRec=bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_ENABLE);
												allSet.put("opt", opt);
												allSet.put("bill_result", bill);
												allSet.put("coin_result", coin);
												//消除变量值
												devopt=0;
												cmdSend=false;
												//3.向主线程返回信息
												Message tomain=mainhand.obtainMessage();
												tomain.what=EV_OPTMAIN;							
												tomain.obj=allSet;
												mainhand.sendMessage(tomain); // 发送消息
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
											case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadExtraRec=bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												//往接口回调信息
												allSet.clear();
												allSet.put("EV_TYPE", EVprotocol.EV_MDB_ENABLE);
												allSet.put("opt", opt);
												allSet.put("bill_result", 0);
												allSet.put("coin_result", 0);
												//消除变量值
												devopt=0;
												cmdSend=false;	
												//3.向主线程返回信息
												Message tomain=mainhand.obtainMessage();
												tomain.what=EV_OPTMAIN;							
												tomain.obj=allSet;
												mainhand.sendMessage(tomain); // 发送消息
												break;
										}
									}
									break;	
								case VboxProtocol.VBOX_POLL:								
									//ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraPOLL<<"+resjson.toString(),"com.txt");
									switch(devopt)
									{
										case EVprotocol.EV_MDB_ENABLE://子线程接收主线程现金设备使能禁能	
											if(cmdSend==false)
											{
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadExtraSend0.2=bill="+bill+"coin="+coin+"opt="+opt,"com.txt");
												VboxProtocol.VboxControlInd(ToolClass.getExtracom_id(),2,opt);
												cmdSend=true;
											}
											break;
										case EVprotocol.EV_MDB_HEART://心跳查询接口
											ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadExtraHeart","com.txt");
											if(++statusnum>2)
											{
												statusnum=0;
												VboxProtocol.VboxGetStatus(ToolClass.getExtracom_id());
												ToolClass.Log(ToolClass.INFO,"EV_COM","ThreadExtraGetStatus","com.txt");
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
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraSetupRpt<<hd_num="+hd_num,"com.txt");
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
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraPayinRpt<<dt="+dt+"value="+(value*10)+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									break;	
								case VboxProtocol.VBOX_PAYOUT_RPT://找币信息
									payback_value=ev_head6.getInt("value")*10; 
									g_holdValue = 0;//当前暂存纸币金额 以分为单位
									bill_recv=0;//纸币器当前收币金额	以分为单位
									coin_recv=ev_head6.getInt("total_value")*10; ;//硬币器当前收币金额	以分为单位
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraPayoutRpt<<payback_value="+payback_value+"GetAmountMoney="+GetAmountMoney(),"com.txt");
									break;
								case VboxProtocol.VBOX_ACTION_RPT://心跳不用处理
									break;	
								case VboxProtocol.VBOX_STATUS_RPT://整机状态
									int bv_st=ev_head6.getInt("bv_st");
									int cc_st=ev_head6.getInt("cc_st");
									int vmc_st=ev_head6.getInt("vmc_st");
									int change=ev_head6.getInt("change");
									coin_remain=change*10;
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraStatusRpt<<bv_st"+bv_st+"cc_st="+cc_st+"vmc_st="+vmc_st+"coin_remain="+coin_remain,"com.txt");
									break;	
								case VboxProtocol.VBOX_HUODAO_RPT://货道信息
									int[] huodao=new int[21];
									huodao[0]=ev_head6.getInt("huodao1");
									huodao[1]=ev_head6.getInt("huodao2");
									huodao[2]=ev_head6.getInt("huodao3");
									huodao[3]=ev_head6.getInt("huodao4");
									huodao[4]=ev_head6.getInt("huodao5");
									huodao[5]=ev_head6.getInt("huodao6");
									huodao[6]=ev_head6.getInt("huodao7");
									huodao[7]=ev_head6.getInt("huodao8");
									huodao[8]=ev_head6.getInt("huodao9");
									huodao[9]=ev_head6.getInt("huodao10");
									huodao[10]=ev_head6.getInt("huodao11");
									huodao[11]=ev_head6.getInt("huodao12");
									huodao[12]=ev_head6.getInt("huodao13");
									huodao[13]=ev_head6.getInt("huodao14");
									huodao[14]=ev_head6.getInt("huodao15");
									huodao[15]=ev_head6.getInt("huodao16");
									huodao[16]=ev_head6.getInt("huodao17");
									huodao[17]=ev_head6.getInt("huodao18");
									huodao[18]=ev_head6.getInt("huodao19");
									huodao[19]=ev_head6.getInt("huodao20");
									huodao[20]=ev_head6.getInt("huodao21");
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraHuodaoRpt<<"+huodao,"com.txt");
									break;								
								case VboxProtocol.VBOX_INFO_RPT:
									int infotype=ev_head6.getInt("type");
									if(infotype==3)//当前余额
									{
										int total_value=ev_head6.getInt("total_value");
										ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraInfototal<<total_value="+total_value,"com.txt");
									}									
									break;									
								default:								
									ToolClass.Log(ToolClass.INFO,"EV_COM","ExtraDefault<<"+resjson.toString(),"com.txt");
									break;
							}	
							
//							
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
