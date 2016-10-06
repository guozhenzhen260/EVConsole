package com.easivend.app.business;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.model.Tb_vmc_cabinet;
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
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	ImageView imgbtnbusgoodsback=null;
	float amount=0;//商品需要支付金额
	float billmoney=0,coinmoney=0,money=0;//投币金额
	float RealNote=0,RealCoin=0,RealAmount=0;//退币金额
	private int recLen = 180; 
	private int queryLen = 0; 
	private int billdev=1;//是否需要打开纸币器,1需要
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    private int iszhienable=0;//1发送打开指令,0还没发送打开指令,2本次投币已经结束
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
		this.imgbtnbusgoodsback=(ImageView)findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		
		//Heart操作
	    Intent intent2=new Intent();
    	intent2.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
		intent2.setAction("android.intent.action.comsend");//action与接收器相同
		comBroadreceiver.sendBroadcast(intent2);
		//打开纸币硬币器
		//延时1s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {            	
        		//打开纸币硬币器
            	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
            	BillEnable(1);	
            }

		}, SPLASH_DISPLAY_LENGHT);
    	
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
			case COMThread.EV_OPTMAIN: 
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusAmount 现金设备操作="+Set,"com.txt");
				int EV_TYPE=Set.get("EV_TYPE");
				switch(EV_TYPE)
				{
					case EVprotocol.EV_MDB_ENABLE:
						//打开
						if((Integer)Set.get("opt")==1)
						{
							//打开失败,等待重新打开
							if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==0) )
							{
								txtbuszhiamounttsxx.setText("提示信息：重试"+con);
								if((Integer)Set.get("bill_result")==0)
									ToolClass.setBill_err(2);
								if((Integer)Set.get("coin_result")==0)
									ToolClass.setCoin_err(2);
								con++;
								billdev=0;
							}
							//纸币器故障
							else if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==1) )
							{
								txtbuszhiamounttsxx.setText("提示信息：[纸币器]无法使用");
								//第一次打开才发送coninfo，以后就不再操作这个了
								if(iszhienable==0)
								{
									//EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
									//硬币器查询接口
									Intent intent3=new Intent();
							    	intent3.putExtra("EVWhat", EVprotocol.EV_MDB_C_INFO);	
									intent3.setAction("android.intent.action.comsend");//action与接收器相同
									comBroadreceiver.sendBroadcast(intent3);
								}
								ToolClass.setBill_err(2);
								ToolClass.setCoin_err(0);
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
							    	intent3.putExtra("EVWhat", EVprotocol.EV_MDB_C_INFO);	
									intent3.setAction("android.intent.action.comsend");//action与接收器相同
									comBroadreceiver.sendBroadcast(intent3);
								}
								ToolClass.setBill_err(0);
								ToolClass.setCoin_err(0);
							}
						}
						break;
					case EVprotocol.EV_MDB_B_INFO:
						break;
					case EVprotocol.EV_MDB_C_INFO:
						dispenser=(Integer)Set.get("dispenser");
					    //Heart操作
					    Intent intent4=new Intent();
				    	intent4.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
						intent4.setAction("android.intent.action.comsend");//action与接收器相同
						comBroadreceiver.sendBroadcast(intent4);
						iszhienable=1;
						break;	
					case EVprotocol.EV_MDB_HEART://心跳查询
						Map<String,Object> obj=new LinkedHashMap<String, Object>();
						obj.putAll(Set);										
						String bill_enable="";
						String coin_enable="";
						String hopperString="";
						int bill_err=ToolClass.getvmcStatus(obj,1);
						int coin_err=ToolClass.getvmcStatus(obj,2);
						//遇到纸币器故障时，不操作纸币器了
						if(bill_err>0)
						{
							billdev=0;
						}
						//纸币器页面
						if(iszhienable==1)
						{
							ToolClass.setBill_err(bill_err);
							ToolClass.setCoin_err(coin_err);
							if(bill_err>0)
								bill_enable="[纸币器]无法使用";
							if(coin_err>0)
								coin_enable="[硬币器]无法使用";
							int hopper1=0;
							if(dispenser==1)//hopper
						  	{
								hopper1=ToolClass.getvmcStatus(obj,3);
								if(hopper1>0)
									hopperString="[找零器]:"+ToolClass.gethopperstats(hopper1);
						  	}
							else if(dispenser==2)//mdb
						  	{
						  		//当前存币金额小于5元
						  		if(ToolClass.MoneyRec((Integer)Set.get("coin_remain"))<5)
						  		{
						  			hopperString="[找零器]:缺币";
						  		}
						  	}
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
							  			BillEnable(0);		
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
						  				BillEnable(0);	
							  			isempcoin=true;
							  		}
						  		}
						  	}
						  	
						  	txtbuszhiamountbillAmount.setText(String.valueOf(money));
					  		if(money>0)
						  	{					  		
						  		iszhiamount=1;
						  		recLen = 180;//有投币后倒计时不用计算了
						  		OrderDetail.setSmallNote(billmoney);
						  		OrderDetail.setSmallConi(coinmoney);
						  		OrderDetail.setSmallAmount(money);
						  		if(money>=amount)
						  		{
						  			iszhienable=2;
						  			timer.shutdown(); 
						  			tochuhuo();
						  		}
						  	}
						}
						break;
					case EVprotocol.EV_MDB_COST://扣款流程
						float cost=ToolClass.MoneyRec((Integer)Set.get("cost"));	
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusAmount 扣款="+cost,"com.txt");
						money-=amount;//扣款
						payback();
					case EVprotocol.EV_MDB_PAYOUT://找零			
						break;	
					case EVprotocol.EV_MDB_PAYBACK://退币
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
			  	    	dialog.dismiss();
			  	    	finish();
						break; 	
					default:break;	
				}
				break;	
				//按钮返回
			case COMThread.EV_BUTTONMAIN:
				SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set2=serializableMap2.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusAmount 按键操作="+Set2,"com.txt");
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
                    	finishActivity();
                    } 
                    //发送查询交易指令
                    if(iszhienable==1)
                    {
	                    queryLen++;
	                    if(queryLen>=5)
	                    {
	                    	queryLen=0;
	                    	//EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
	                    	//Heart操作
						    Intent intent2=new Intent();
					    	intent2.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
							intent2.setAction("android.intent.action.comsend");//action与接收器相同
							comBroadreceiver.sendBroadcast(intent2);
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
	                    	BillEnable(1);
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
    	BillEnable(0);    	   
    	//延时
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {   
            	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(BusZhiAmount.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
//            	intent.putExtra("out_trade_no", out_trade_no);
//            	intent.putExtra("proID", proID);
//            	intent.putExtra("productID", productID);
//            	intent.putExtra("proType", proType);
//            	intent.putExtra("cabID", cabID);
//            	intent.putExtra("huoID", huoID);
//            	intent.putExtra("prosales", prosales);
//            	intent.putExtra("count", count);
//            	intent.putExtra("reamin_amount", reamin_amount);
//            	intent.putExtra("zhifutype", zhifutype);
            	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
            }

		}, 2500);    	
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
  				int cabinetvar=bundle.getInt("cabinetvar");//出货柜号
  				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货返回status="+status+"cabinetvar="+cabinetvar,"log.txt");
  				//1.
  				//出货成功,扣钱
				if(status==1)
				{
					//查找货道类型
	        		vmc_cabinetDAO cabinetDAO3 = new vmc_cabinetDAO(BusZhiAmount.this);// 创建InaccountDAO对象
	        	    // 获取所有收入信息，并存储到List泛型集合中
	        	    Tb_vmc_cabinet listinfos3 = cabinetDAO3.findScrollData(String.valueOf(cabinetvar));
	        	    //针对冰山机器，不用扣钱
	        	    if((ToolClass.getExtraComType()==1)&&(listinfos3.getCabType()==4))
	        	    {
	        	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","COMBusAmount 扣款="+amount,"log.txt");
						money-=amount;//扣款
						payback();
	        	    }
	        	    else
	        	    {
						//扣钱
			  	    	//EVprotocolAPI.EV_mdbCost(ToolClass.getCom_id(),ToolClass.MoneySend(amount));
						Intent intent=new Intent();
				    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_COST);	
						intent.putExtra("cost", ToolClass.MoneySend((float)amount));	
						intent.setAction("android.intent.action.comsend");//action与接收器相同
						comBroadreceiver.sendBroadcast(intent);	
	        	    }
				}
				//出货失败,不扣钱
				else
				{	
					payback();
				}				
  			}			
  		}
  	}
  	
  	//出货完成后，进入退币流程
  	private void payback()
  	{
		//2.更新投币金额
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<退款money="+money,"log.txt");
		txtbuszhiamountbillAmount.setText(String.valueOf(money));
		  				
		//还有余额退币
		if(money>0)
		{
			dialog= ProgressDialog.show(BusZhiAmount.this,"正在退币中","请稍候...");
			new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {            	
	            	//退币
  	  	  	    	//EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
  					Intent intent=new Intent();
  			    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_PAYBACK);	
  					intent.putExtra("bill", 1);	
  					intent.putExtra("coin", 1);	
  					intent.setAction("android.intent.action.comsend");//action与接收器相同
  					comBroadreceiver.sendBroadcast(intent);
	            }

			}, 500);  					
		}
	    //没剩下余额了，不退币
		else
		{  					
	    	OrderDetail.addLog(BusZhiAmount.this);			    	
	    	//打开纸币硬币器					
		    new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {            	
	            	finish();
	            }

			}, 500);
		}
  	}
    //结束界面
  	private void finishActivity()
  	{  		
  		//如果本次投币已经结束，可以购买，则不进行退币操作
  		if(iszhienable==2)
  		{
  			ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusLand 退币按钮无效","com.txt");
  		}
  		else if((iszhiamount==1)&&(money>0))
  		{
  			timer.shutdown(); 
  			dialog= ProgressDialog.show(BusZhiAmount.this,"正在退币中","请稍候...");
  			OrderDetail.setPayStatus(2);//支付失败
  			//退币
  	    	//EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
  			Intent intent=new Intent();
	    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_PAYBACK);	
			intent.putExtra("bill", 1);	
			intent.putExtra("coin", 1);	
			intent.setAction("android.intent.action.comsend");//action与接收器相同
			comBroadreceiver.sendBroadcast(intent);
  		} 
  		else 
  		{	
  			finish();
		}  		
  	}
  	
    //1打开,0关闭关闭纸币硬币器   
  	private void BillEnable(int opt)
  	{  		 	
		Intent intent=new Intent();
		intent.putExtra("EVWhat", EVprotocol.EV_MDB_ENABLE);	
		intent.putExtra("bill", billdev);	
		intent.putExtra("coin", 1);	
		intent.putExtra("opt", opt);	
		intent.setAction("android.intent.action.comsend");//action与接收器相同
		comBroadreceiver.sendBroadcast(intent);	
  	}
  	
  	@Override
	protected void onDestroy() {
  		timer.shutdown(); 
		//=============
  		//COM服务相关
  		//=============
  		//关闭纸币硬币器
  		BillEnable(0);	
  		//5.解除注册接收器
  		comBroadreceiver.unregisterReceiver(comreceiver);
	    super.onDestroy();		
	}
}


