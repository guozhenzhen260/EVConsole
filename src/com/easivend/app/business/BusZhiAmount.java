package com.easivend.app.business;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.AddInaccount.COMReceiver;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.http.EVServerhttp;
import com.easivend.http.Zhifubaohttp;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.COMService;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class BusZhiAmount  extends Activity
{
	private final int SPLASH_DISPLAY_LENGHT = 1500; // 延迟1.5秒
	//进度对话框
	ProgressDialog dialog= null;
	public static BusZhiAmount BusZhiAmountAct=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	TextView txtbuszhiamountcount=null,txtbuszhiamountAmount=null,txtbuszhiamountbillAmount=null,txtbuszhiamounttime=null,
			txtbuszhiamounttsxx=null;
	ImageButton imgbtnbuszhiamountqxzf=null,imgbtnbuszhiamountqtzf=null;
	float amount=0;//商品需要支付金额
	float billmoney=0,coinmoney=0,money=0;//投币金额
	float RealNote=0,RealCoin=0,RealAmount=0;//退币金额
	private int recLen = 180; 
	private int queryLen = 0; 
    private TextView txtView; 
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    private int iszhienable=0;//1发送打开指令,0还没发送打开指令
    private boolean isempcoin=false;//false还未发送关纸币器指令，true因为缺币，已经发送关纸币器指令
    private int dispenser=0;//0无,1hopper,2mdb
    private boolean ischuhuo=false;//true已经出货过了，可以上报日志
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private String zhifutype = "0";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
//    private String id="";
    private String out_trade_no=null;
    private int iszhiamount=0;//1成功投入钱,0没有成功投入钱    
    //COM服务相关
  	LocalBroadcastManager comBroadreceiver;
  	COMReceiver comreceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhiamount);
		BusZhiAmountAct = this;
		//从商品页面中取得锁选中的商品
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
//		proID=bundle.getString("proID");
//		productID=bundle.getString("productID");
//		proType=bundle.getString("proType");
//		cabID=bundle.getString("cabID");
//		huoID=bundle.getString("huoID");
//		prosales=bundle.getString("prosales");
//		count=bundle.getString("count");
//		reamin_amount=bundle.getString("reamin_amount");
		out_trade_no=ToolClass.out_trade_no(BusZhiAmount.this);
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		OrderDetail.setOrdereID(out_trade_no);
    	OrderDetail.setPayType(Integer.parseInt(zhifutype));
		txtbuszhiamountcount= (TextView) findViewById(R.id.txtbuszhiamountcount);
		txtbuszhiamountcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamountAmount= (TextView) findViewById(R.id.txtbuszhiamountAmount);
		txtbuszhiamountAmount.setText(String.valueOf(amount));
		txtbuszhiamountbillAmount= (TextView) findViewById(R.id.txtbuszhiamountbillAmount);		
		txtbuszhiamounttime = (TextView) findViewById(R.id.txtbuszhiamounttime);
		txtbuszhiamounttsxx = (TextView) findViewById(R.id.txtbuszhiamounttsxx);
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		imgbtnbuszhiamountqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhiamountqxzf);
		imgbtnbuszhiamountqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		imgbtnbuszhiamountqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhiamountqtzf);
		imgbtnbuszhiamountqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		//*****************
		//注册投币找零监听器
		//*****************
		EVprotocolAPI.setCallBack(new jniInterfaceImp());

		//打开纸币硬币器
    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
    	Intent intent=new Intent();
    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
		intent.putExtra("bill", 1);	
		intent.putExtra("coin", 1);	
		intent.putExtra("opt", 1);	
		intent.setAction("android.intent.action.comsend");//action与接收器相同
		comBroadreceiver.sendBroadcast(intent);
	}
	//2.创建COMReceiver的接收器广播，用来接收服务器同步的内容
	public class COMReceiver extends BroadcastReceiver 
	{
		int con=0;
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//操作返回	
			case COMService.EV_OPTMAIN: 
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusAmount 现金设备操作="+Set,"com.txt");
				int EV_TYPE=Set.get("EV_TYPE");
				switch(EV_TYPE)
				{
					case COMService.EV_MDB_ENABLE:	
						//打开失败,等待重新打开
						if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==0) )
						{
							txtbuszhiamounttsxx.setText("提示信息：重试"+con);
							if((Integer)Set.get("bill_result")==0)
								ToolClass.setBill_err(2);
							if((Integer)Set.get("coin_result")==0)
								ToolClass.setCoin_err(2);
							con++;
						}
						//打开成功
						else
						{
							//第一次打开才发送coninfo，以后就不再操作这个了
							if(iszhienable==0)
							{
								//EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
								//硬币器查询接口
								Intent intent3=new Intent();
						    	intent3.putExtra("EVWhat", COMService.EV_MDB_C_INFO);	
								intent3.setAction("android.intent.action.comsend");//action与接收器相同
								comBroadreceiver.sendBroadcast(intent3);
							}
							ToolClass.setBill_err(0);
							ToolClass.setCoin_err(0);
						}
						break;
//					case COMService.EV_MDB_B_INFO:
//						break;
//					case COMService.EV_MDB_C_INFO:
//						String acceptor2="";
//						if((Integer)Set.get("acceptor")==3)
//							acceptor2="串行脉冲";
//						else if((Integer)Set.get("acceptor")==2)
//							acceptor2="MDB";
//						else if((Integer)Set.get("acceptor")==1)
//							acceptor2="并行脉冲";
//						else if((Integer)Set.get("acceptor")==0)
//							acceptor2="无";
//						String dispenser2="";
//						if((Integer)Set.get("dispenser")==2)
//							dispenser2="MDB";
//						else if((Integer)Set.get("dispenser")==1)
//							dispenser2="hopper";
//						else if((Integer)Set.get("dispenser")==0)
//							dispenser2="无";
//						String code2=String.valueOf(Set.get("code"));
//						String sn2=String.valueOf(Set.get("sn"));
//						String model2=String.valueOf(Set.get("model"));
//						String ver2=String.valueOf(Set.get("ver"));
//						int capacity2=(Integer)Set.get("capacity");
//						String str2="硬币接收器:"+acceptor2+"硬币找零器:"+dispenser2+"厂商:"+code2
//								+"序列号"+sn2;
//						txtcoinmanagerpar.setText(str2);
//						str2=" 型号:"+model2+"版本号:"+ver2+"储币量:"+capacity2;
//						txtcoinmanagerpar2.setText(str2);
//						spincoinmanagercoin.setSelection((Integer)Set.get("acceptor"));
//						
//						
//						str2="";
//						Map<String,Integer> allSet3=new LinkedHashMap<String, Integer>();
//						allSet3.put("ch_r1", Set.get("ch_r1"));
//						allSet3.put("ch_r2", Set.get("ch_r2"));
//						allSet3.put("ch_r3", Set.get("ch_r3"));
//						allSet3.put("ch_r4", Set.get("ch_r4"));
//						allSet3.put("ch_r5", Set.get("ch_r5"));
//						allSet3.put("ch_r6", Set.get("ch_r6"));
//						allSet3.put("ch_r7", Set.get("ch_r7"));
//						allSet3.put("ch_r8", Set.get("ch_r8"));
//						//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet3.toString());
//						double all[]=new double[allSet3.size()];	
//						int i=0;
//						Set<Map.Entry<String,Integer>> allset3=allSet3.entrySet();  //实例化
//						Iterator<Map.Entry<String,Integer>> iter3=allset3.iterator();
//					    while(iter3.hasNext())
//					    {
//					        Map.Entry<String,Integer> me=iter3.next();
//					        all[i++]=ToolClass.MoneyRec(me.getValue());
//					        //str+="[通道"+me.getKey() + "]=" + ToolClass.MoneyRec(me.getValue()) + ",";
//					    }
//					    txtcoinmanagercoinin1.setText(String.valueOf(all[0]));
//					    txtcoinmanagercoinin2.setText(String.valueOf(all[1]));
//					    txtcoinmanagercoinin3.setText(String.valueOf(all[2]));
//					    txtcoinmanagercoinin4.setText(String.valueOf(all[3]));
//					    txtcoinmanagercoinin5.setText(String.valueOf(all[4]));
//					    txtcoinmanagercoinin6.setText(String.valueOf(all[5]));
//					    txtcoinmanagercoinin7.setText(String.valueOf(all[6]));
//					    txtcoinmanagercoinin8.setText(String.valueOf(all[7]));
//					    txtcoinmanagercoinin9.setText("0");
//					    txtcoinmanagercoinin10.setText("0");
//					    txtcoinmanagercoinin11.setText("0");
//					    txtcoinmanagercoinin12.setText("0");
//					    txtcoinmanagercoinin13.setText("0");
//					    txtcoinmanagercoinin14.setText("0");
//					    txtcoinmanagercoinin15.setText("0");
//					    txtcoinmanagercoinin16.setText("0");
//					    //找零通道面值
//					    spinhopper.setSelection((Integer)Set.get("dispenser"));
//					    str2="";
//					    Map<String,Integer> allSet4=new LinkedHashMap<String, Integer>();
//						allSet4.put("ch_d1", Set.get("ch_d1"));
//						allSet4.put("ch_d2", Set.get("ch_d2"));
//						allSet4.put("ch_d3", Set.get("ch_d3"));
//						allSet4.put("ch_d4", Set.get("ch_d4"));
//						allSet4.put("ch_d5", Set.get("ch_d5"));
//						allSet4.put("ch_d6", Set.get("ch_d6"));
//						allSet4.put("ch_d7", Set.get("ch_d7"));
//						allSet4.put("ch_d8", Set.get("ch_d8"));
//					    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet4.toString(),"log.txt");
//					    double all2[]=new double[allSet4.size()];	
//						i=0;
//						Set<Map.Entry<String,Integer>> allset4=allSet4.entrySet();  //实例化
//					    Iterator<Map.Entry<String,Integer>> iter4=allset4.iterator();
//					    while(iter4.hasNext())
//					    {
//					        Map.Entry<String,Integer> me=iter4.next();
//					        all2[i++]=ToolClass.MoneyRec(me.getValue());
//					        //str2+="[通道"+me.getKey() + "]=" + ToolClass.MoneyRec(me.getValue()) + ",";
//					    }
//					    txthopperin1.setText(String.valueOf(all2[0]));
//					    txthopperin2.setText(String.valueOf(all2[1]));
//					    txthopperin3.setText(String.valueOf(all2[2]));
//					    txthopperin4.setText(String.valueOf(all2[3]));
//					    txthopperin5.setText(String.valueOf(all2[4]));
//					    txthopperin6.setText(String.valueOf(all2[5]));
//					    txthopperin7.setText(String.valueOf(all2[6]));
//					    txthopperin8.setText(String.valueOf(all2[7]));
//					    //Heart操作
//					    Intent intent4=new Intent();
//				    	intent4.putExtra("EVWhat", COMService.EV_MDB_HEART);
//						intent4.setAction("android.intent.action.comsend");//action与接收器相同
//						comBroadreceiver.sendBroadcast(intent4);
//						devopt=COMService.EV_MDB_HEART;//Heart操作
//						break;	
//					case COMService.EV_MDB_HEART://心跳查询
//						Map<String,Object> obj=new LinkedHashMap<String, Object>();
//						obj.putAll(Set);
//						String bill_enable=((Integer)Set.get("bill_enable")==1)?"使能":"禁能";
//						txtbillmanagerstate.setText(bill_enable);
//						String bill_payback=((Integer)Set.get("bill_payback")==1)?"触发":"没触发";
//					  	txtbillpayback.setText(bill_payback);
//					  	String bill_err=((Integer)Set.get("bill_err")==0)?"正常":"故障码:"+(Integer)Set.get("bill_err");
//					  	txtbillerr.setText(bill_err);
//					  	double money=ToolClass.MoneyRec((Integer)Set.get("bill_recv"));					  	
//					  	txtbillpayin.setText(String.valueOf(money));
//					  	amount=money;//当前纸币投入
//					  	money=ToolClass.MoneyRec((Integer)Set.get("bill_remain"));
//					  	txtbillmanagerbillpayamount.setText(String.valueOf(money));
//					  	int bill_errstatus=ToolClass.getvmcStatus(obj,1);
//					  	ToolClass.setBill_err(bill_errstatus);
//					  	
//					  	String coin_enable=((Integer)Set.get("coin_enable")==1)?"使能":"禁能";
//					  	txtcoinmanagerstate.setText(coin_enable);
//						String coin_payback=((Integer)Set.get("coin_payback")==1)?"触发":"没触发";
//						txtcoinpayback.setText(coin_payback);
//					  	String coin_err=((Integer)Set.get("coin_err")==0)?"正常":"故障码:"+(Integer)Set.get("coin_err");
//					  	txtcoinerr.setText(coin_err);
//					  	money=ToolClass.MoneyRec((Integer)Set.get("coin_recv"));					  	
//					  	txtcoinpayin.setText(String.valueOf(money));
//					  	amount+=money;//当前硬币投入
//					  	money=ToolClass.MoneyRec((Integer)Set.get("coin_remain"));
//					  	txtcoinmanagercoininamount.setText(String.valueOf(money));
//					  	int coin_errstatus=ToolClass.getvmcStatus(obj,2);
//					  	ToolClass.setCoin_err(coin_errstatus);
//					  	
//					  	String hopperString=null;
//					  	if(Set.containsKey("hopper1"))
//					  	{
//						  	hopperString="[1]:"+ToolClass.gethopperstats((Integer)Set.get("hopper1"))+"[2]:"+ToolClass.gethopperstats((Integer)Set.get("hopper2"))
//						  				+"[[3]:"+ToolClass.gethopperstats((Integer)Set.get("hopper3"))+"[4]:"+ToolClass.gethopperstats((Integer)Set.get("hopper4"))
//							  			+"[5]:"+ToolClass.gethopperstats((Integer)Set.get("hopper5"))+"[6]:"+ToolClass.gethopperstats((Integer)Set.get("hopper6"))
//							  			+"[7]:"+ToolClass.gethopperstats((Integer)Set.get("hopper7"))+"[8]:"+ToolClass.gethopperstats((Integer)Set.get("hopper8"));
//					  	}
//					  	txthopperincount.setText(hopperString);
//						break;
//					case EVprotocolAPI.EV_MDB_PAYOUT://找零
//						money=ToolClass.MoneyRec((Integer)Set.get("bill_changed"));					  	
//						txtpaymoney.setText(String.valueOf(money));	
//						money=ToolClass.MoneyRec((Integer)Set.get("coin_changed"));					  	
//						txtcoinpaymoney.setText(String.valueOf(money));	
//						txthopperpaymoney.setText(String.valueOf(money));	
//						break;
//					case EVprotocolAPI.EV_MDB_HP_PAYOUT://找零
//						txthopperpaynum.setText(String.valueOf((Integer)Set.get("changed")));
//						
//						break;	
					default:break;	
				}
				break;			
			}			
		}

	}
	//创建一个专门处理单击接口的子类
	private class jniInterfaceImp implements JNIInterface
	{
		int con=0;
		@Override
		public void jniCallback(Map<String, Object> allSet) {
			float payin_amount=0,reamin_amount=0,payout_amount=0;
			// TODO Auto-generated method stub	
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<mdb设备结果","log.txt");
			Map<String, Object> Set= allSet;
			int jnirst=(Integer)Set.get("EV_TYPE");
			switch (jnirst)
			{
				case EVprotocolAPI.EV_MDB_ENABLE://接收子线程投币金额消息	
					//打开失败,等待重新打开
					if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==0) )
					{
						txtbuszhiamounttsxx.setText("提示信息：重试"+con);
						if((Integer)Set.get("bill_result")==0)
							ToolClass.setBill_err(2);
						if((Integer)Set.get("coin_result")==0)
							ToolClass.setCoin_err(2);
						con++;
					}
					//打开成功
					else
					{
						//第一次打开才发送coninfo，以后就不再操作这个了
						if(iszhienable==0)
							EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
						ToolClass.setBill_err(0);
						ToolClass.setCoin_err(0);
					}										
					break;
				case EVprotocolAPI.EV_MDB_B_INFO:	
					break;
				case EVprotocolAPI.EV_MDB_C_INFO:
					dispenser=(Integer)Set.get("dispenser");
					EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
					break;	
				case EVprotocolAPI.EV_MDB_HEART://心跳查询
					iszhienable=1;					
					String bill_enable="";
					String coin_enable="";
					String hopperString="";
					int bill_err=ToolClass.getvmcStatus(Set,1);
					int coin_err=ToolClass.getvmcStatus(Set,2);
					ToolClass.setBill_err(bill_err);
					ToolClass.setCoin_err(coin_err);
					int hopper1=ToolClass.getvmcStatus(Set,3);
					if(bill_err>0)
						bill_enable="[纸币器]无法使用";
					if(coin_err>0)
						coin_enable="[硬币器]无法使用";					
					if(hopper1>0)
						hopperString="[找零器]:"+ToolClass.gethopperstats(hopper1);
				  	txtbuszhiamounttsxx.setText("提示信息："+bill_enable+coin_enable+hopperString);
				  	billmoney=ToolClass.MoneyRec((Integer)Set.get("bill_recv"));	
				  	coinmoney=ToolClass.MoneyRec((Integer)Set.get("coin_recv"));
				  	money=billmoney+coinmoney;
				  	//如果缺币,就把纸币硬币器关闭
				  	if(dispenser==1)//hopper
				  	{
				  		if(hopper1>0)//hopper缺币
				  		{
					  		if(isempcoin==false)//第一次关闭纸币硬币器
					  		{
					  			//关闭纸币硬币器
					  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);
					  			Intent intent=new Intent();
						    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
								intent.putExtra("bill", 1);	
								intent.putExtra("coin", 1);	
								intent.putExtra("opt", 0);	
								intent.setAction("android.intent.action.comsend");//action与接收器相同
								comBroadreceiver.sendBroadcast(intent);	
					  			isempcoin=true;
					  		}
				  		}
				  	}
				  	else if(dispenser==2)//mdb
				  	{
				  		//当前存币金额小于5元
				  		if(ToolClass.MoneyRec((Integer)Set.get("coin_remain"))<5)
				  		{
				  			if(isempcoin==false)//第一次关闭纸币硬币器
					  		{
					  			//关闭纸币硬币器
					  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);
				  				Intent intent=new Intent();
						    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
								intent.putExtra("bill", 1);	
								intent.putExtra("coin", 1);	
								intent.putExtra("opt", 0);	
								intent.setAction("android.intent.action.comsend");//action与接收器相同
								comBroadreceiver.sendBroadcast(intent);	
					  			isempcoin=true;
					  		}
				  		}
				  	}
				  	
				  	if(money>0)
				  	{
				  		iszhiamount=1;
				  		recLen = 180;//有投币后倒计时不用计算了
				  		txtbuszhiamountbillAmount.setText(String.valueOf(money));
				  		OrderDetail.setSmallNote(billmoney);
				  		OrderDetail.setSmallConi(coinmoney);
				  		OrderDetail.setSmallAmount(money);
				  		if(money>=amount)
				  		{
				  			timer.shutdown(); 
				  			tochuhuo();
				  		}
				  	}
					break;
				case EVprotocolAPI.EV_MDB_PAYOUT://找零
					break;
				case EVprotocolAPI.EV_MDB_PAYBACK://退币
					RealNote=ToolClass.MoneyRec((Integer)Set.get("bill_changed"));	
					RealCoin=ToolClass.MoneyRec((Integer)Set.get("coin_changed"));	
					RealAmount=RealNote+RealCoin;						
					OrderDetail.setRealNote(RealNote);
			    	OrderDetail.setRealCoin(RealCoin);
			    	OrderDetail.setRealAmount(RealAmount);
			    	//退币完成
			    	if(RealAmount==money)
			    	{
			    		OrderDetail.setRealStatus(1);//退款完成				    		
			    	}
			    	//欠款
			    	else
			    	{
			    		OrderDetail.setRealStatus(3);//退款失败
			    		OrderDetail.setDebtAmount(money-RealAmount);//欠款金额
			    	}
			    	if(ischuhuo==true)
			    	{
			    		OrderDetail.addLog(BusZhiAmount.this);
			    	}
			    	else
			    	{
			    		OrderDetail.cleardata();
					}
					//关闭纸币硬币器
		  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);
			    	Intent intent=new Intent();
			    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
					intent.putExtra("bill", 1);	
					intent.putExtra("coin", 1);	
					intent.putExtra("opt", 0);	
					intent.setAction("android.intent.action.comsend");//action与接收器相同
					comBroadreceiver.sendBroadcast(intent);	
		  	    	dialog.dismiss();
		  	    	finish();
					break; 	
			}				
		}
		
	}
	
	//调用倒计时定时器
	TimerTask task = new TimerTask() { 
        @Override 
        public void run() { 
  
            runOnUiThread(new Runnable() {      // UI thread 
                @Override 
                public void run() { 
                    recLen--; 
                    txtbuszhiamounttime.setText("倒计时:"+recLen); 
                    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
                    if(recLen <= 0)
                    { 
                    	timer.shutdown(); 
                        finishActivity();
                    } 
                    //发送查询交易指令
                    if(iszhienable==1)
                    {
	                    queryLen++;
	                    if(queryLen>=1)
	                    {
	                    	queryLen=0;
	                    	EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
	                    }
                    }
                    //发送打开纸币硬币器指令
                    else if(iszhienable==0)
                    {
                    	queryLen++;
	                    if(queryLen>=10)
	                    {
	                    	queryLen=0;
	                    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
	                    	Intent intent=new Intent();
	        		    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
	        				intent.putExtra("bill", 1);	
	        				intent.putExtra("coin", 1);	
	        				intent.putExtra("opt", 1);	
	        				intent.setAction("android.intent.action.comsend");//action与接收器相同
	        				comBroadreceiver.sendBroadcast(intent);	
	                    }
                    }
                } 
            }); 
        } 
    };
    //出货界面
    private void tochuhuo()
    {        
    	ischuhuo=true;
    	Intent intent = null;// 创建Intent对象                
    	intent = new Intent(BusZhiAmount.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
//    	intent.putExtra("out_trade_no", out_trade_no);
//    	intent.putExtra("proID", proID);
//    	intent.putExtra("productID", productID);
//    	intent.putExtra("proType", proType);
//    	intent.putExtra("cabID", cabID);
//    	intent.putExtra("huoID", huoID);
//    	intent.putExtra("prosales", prosales);
//    	intent.putExtra("count", count);
//    	intent.putExtra("reamin_amount", reamin_amount);
//    	intent.putExtra("zhifutype", zhifutype);    	
    	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
    }
    //接收BusHuo返回信息
  	@Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		// TODO Auto-generated method stub
  		if(requestCode==REQUEST_CODE)
  		{
  			if(resultCode==BusZhiAmount.RESULT_CANCELED)
  			{
  				Bundle bundle=data.getExtras();
  				int status=bundle.getInt("status");//出货结果1成功,0失败
  				//1.
  				//出货成功,扣钱
				if(status==1)
				{
					//扣钱
		  	    	EVprotocolAPI.EV_mdbCost(ToolClass.getCom_id(),ToolClass.MoneySend(amount));
		  	    	money-=amount;
				}
				//出货失败,不扣钱
				else
				{					
				}
				//2.更新投币金额
  				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<退款money="+money,"log.txt");
  				txtbuszhiamountbillAmount.setText(String.valueOf(money));
  				//没剩下余额了，不退币
  				if(money==0)
  				{  					
			    	OrderDetail.addLog(BusZhiAmount.this);
					//关闭纸币硬币器
		  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);
			    	Intent intent=new Intent();
			    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
					intent.putExtra("bill", 1);	
					intent.putExtra("coin", 1);	
					intent.putExtra("opt", 0);	
					intent.setAction("android.intent.action.comsend");//action与接收器相同
					comBroadreceiver.sendBroadcast(intent);	
		  	    	finish();
  				}
  				//退币
  				else 
  				{
  					dialog= ProgressDialog.show(BusZhiAmount.this,"正在退币中","请稍候...");
  					EVprotocolAPI.setCallBack(new jniInterfaceImp());
  					//退币
  	  	  	    	EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
				}  				
  			}			
  		}
  	}
    //结束界面
  	private void finishActivity()
  	{
  		timer.shutdown(); 
  		if(iszhiamount==1)
  		{
  			dialog= ProgressDialog.show(BusZhiAmount.this,"正在退币中","请稍候...");
  			OrderDetail.setPayStatus(2);//支付失败
  			//退币
  	    	EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
  		} 
  		else 
  		{
  			//关闭纸币硬币器
  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);  
  			Intent intent=new Intent();
	    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
			intent.putExtra("bill", 1);	
			intent.putExtra("coin", 1);	
			intent.putExtra("opt", 0);	
			intent.setAction("android.intent.action.comsend");//action与接收器相同
			comBroadreceiver.sendBroadcast(intent);	
  			finish();
		}  		
  	}
  	
  	@Override
	protected void onDestroy() {
		//=============
  		//COM服务相关
  		//=============
  		//5.解除注册接收器
  		comBroadreceiver.unregisterReceiver(comreceiver);
	    super.onDestroy();		
	}
}


