/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           AddInaccount.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        纸币器硬币器测试页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.view.COMService;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class AddInaccount extends TabActivity
{
	private TabHost mytabhost = null;
	private int[] layres=new int[]{R.id.tab_billmanager,R.id.tab_coinmanager,R.id.tab_payoutmanager};//内嵌布局文件的id
	private double amount=0;//总投币金额
	//纸币器
	private Spinner spinbillmanagerbill=null;
	private String [] billStringArray; 
	private ArrayAdapter<String> billAdapter ;
	private EditText edtpayout=null;
	private TextView txtbillmanagerpar=null,txtbillmanagerpar2=null,txtbillmanagerstate=null,txtbillmanagerbillin=null,txtbillmanagerbillin2=null,txtbillmanagerbillincount=null
			,txtbillmanagerbillpay=null,txtbillmanagerbillpay2=null,txtbillmanagerbillpaycount=null,txtbillmanagerbillpayamount=null,txtbillpayin=null,
			txtpaymoney=null,txtbillpayback=null,txtbillerr=null;
	private Button btnbillon=null,btnbilloff=null,btnbillquery=null,btnbillset=null,btnbillexit=null,btnbillpayout=null;// 创建Button对象“退出”
	//硬币器
	private Spinner spincoinmanagercoin=null;
	private String [] coinStringArray; 
	private ArrayAdapter<String> coinAdapter ;
	private TextView txtcoinmanagerpar=null,txtcoinmanagerpar2=null,txtcoinmanagerstate=null,txtcoinpayback=null,txtcoinerr=null,
			txtcoinmanagercoinincount=null,txtcoinmanagercoininamount=null,txtcoinpayin=null,txtcoinpaymoney=null;
	private EditText txtcoinmanagercoinin1=null,txtcoinmanagercoinin2=null,txtcoinmanagercoinin3=null,txtcoinmanagercoinin4=null,
			txtcoinmanagercoinin5=null,txtcoinmanagercoinin6=null,txtcoinmanagercoinin7=null,txtcoinmanagercoinin8=null,
			txtcoinmanagercoinin9=null,txtcoinmanagercoinin10=null,txtcoinmanagercoinin11=null,txtcoinmanagercoinin12=null,
			txtcoinmanagercoinin13=null,txtcoinmanagercoinin14=null,txtcoinmanagercoinin15=null,txtcoinmanagercoinin16=null,
			edtcoinpayout=null;		
	private Button btncoinon=null,btncoinoff=null,btncoinquery=null,btncoinset=null,btncoinpayout=null,btncoinexit=null;
	//hopper找零器
	private Spinner spinhopper=null;
	private String [] hopperStringArray; 
	private ArrayAdapter<String> hopperAdapter ;
	private TextView txthopperincount=null,txthopperpaymoney=null,txthopperpaynum=null;
	private EditText txthopperin1=null,txthopperin2=null,txthopperin3=null,txthopperin4=null,
			txthopperin5=null,txthopperin6=null,txthopperin7=null,txthopperin8=null,edthopperpayout=null,
			edthopperpayno=null,edthopperpaynum=null;		
	private Button btnhopperquery=null,btnhopperpay=null,btnhopperpaymoney=null,btnhopperset=null,btnhopperexit=null,
			btnhopperpaynum=null;
	private int devopt=0;//操作类型	
	private boolean cashopt=false;//true表示有打开纸币器或者硬币器
	private boolean ercheck=false;//true正在退币操作中，请稍后。false没有退币的线程操作
	private int queryLen = 0; //查询时间
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	//COM服务相关
	LocalBroadcastManager comBroadreceiver;
	COMReceiver comreceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.addinaccount);// 设置布局文件
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.addinaccount, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabbill=this.mytabhost.newTabSpec("tab0");
        myTabbill.setIndicator("纸币器测试");
        myTabbill.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabbill); 
    	
    	TabSpec myTabcoin=this.mytabhost.newTabSpec("tab1");
    	myTabcoin.setIndicator("硬币器测试");
    	myTabcoin.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabcoin); 
    	
    	TabSpec myTabpay=this.mytabhost.newTabSpec("tab2");
    	myTabpay.setIndicator("Hopper找零器测试");
    	myTabpay.setContent(this.layres[2]);
    	this.mytabhost.addTab(myTabpay); 
    	timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
    	
    	//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
  	    //===============
    	//纸币器设置页面
    	//===============
  	    spinbillmanagerbill = (Spinner) findViewById(R.id.spinbillmanagerbill);
  	    billStringArray=getResources().getStringArray(R.array.bill_label);
  	    //使用自定义的ArrayAdapter
        billAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner,billStringArray);
        spinbillmanagerbill.setAdapter(billAdapter);// 为ListView列表设置数据源
	  	txtbillmanagerpar = (TextView) findViewById(R.id.txtbillmanagerpar);
	  	txtbillmanagerpar2 = (TextView) findViewById(R.id.txtbillmanagerpar2);
	  	txtbillmanagerstate = (TextView) findViewById(R.id.txtbillmanagerstate);
	  	txtbillpayback = (TextView) findViewById(R.id.txtbillpayback);
	  	txtbillerr = (TextView) findViewById(R.id.txtbillerr);
	  	txtbillmanagerbillin = (TextView) findViewById(R.id.txtbillmanagerbillin);
	  	txtbillmanagerbillin2 = (TextView) findViewById(R.id.txtbillmanagerbillin2);
	  	txtbillmanagerbillincount = (TextView) findViewById(R.id.txtbillmanagerbillincount);
	  	txtbillmanagerbillpay = (TextView) findViewById(R.id.txtbillmanagerbillpay);
	  	txtbillmanagerbillpay2 = (TextView) findViewById(R.id.txtbillmanagerbillpay2);
	  	txtbillmanagerbillpaycount = (TextView) findViewById(R.id.txtbillmanagerbillpaycount);
	  	txtbillmanagerbillpayamount = (TextView) findViewById(R.id.txtbillmanagerbillpayamount);
	  	txtbillpayin = (TextView) findViewById(R.id.txtbillpayin);
	  	txtpaymoney = (TextView) findViewById(R.id.txtpaymoney);
		edtpayout = (EditText) findViewById(R.id.edtpayout);
		btnbillpayout = (Button) findViewById(R.id.btnbillpayout);
		btnbillpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbPayout(ToolClass.getCom_id(),1,0,ToolClass.MoneySend(Float.parseFloat(edtpayout.getText().toString())),0);
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_PAYOUT);	
				intent.putExtra("bill", 1);	
				intent.putExtra("coin", 0);	
				intent.putExtra("billPay", ToolClass.MoneySend(Float.parseFloat(edtpayout.getText().toString())));	
				intent.putExtra("coinPay", 0);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				devopt=COMService.EV_MDB_PAYOUT;
				ercheck=true;
		    }
		});
		btnbillon = (Button) findViewById(R.id.btnbillon);
		btnbillon.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,0,1);
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
				intent.putExtra("bill", 1);	
				intent.putExtra("coin", 0);	
				intent.putExtra("opt", 1);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				devopt=1;//操作纸币器
		    }
		});
		btnbilloff = (Button) findViewById(R.id.btnbilloff);
		btnbilloff.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,0,0);
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
				intent.putExtra("bill", 1);	
				intent.putExtra("coin", 0);	
				intent.putExtra("opt", 0);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				devopt=1;//操作纸币器
		    }
		});
		btnbillquery = (Button) findViewById(R.id.btnbillquery);
		btnbillquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
		    	Intent intent2=new Intent();
		    	intent2.putExtra("EVWhat", COMService.EV_MDB_HEART);
				intent2.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent2);
				devopt=COMService.EV_MDB_HEART;//Heart操作
		    }
		});
		btnbillset = (Button) findViewById(R.id.btnbillset);
		btnbillset.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
//		    	//创建警告对话框
//		    	Dialog alert=new AlertDialog.Builder(AddInaccount.this)
//		    		.setTitle("对话框")//标题
//		    		.setMessage("您确定要配置纸币器吗？")//表示对话框中得内容
//		    		.setIcon(R.drawable.ic_launcher)//设置logo
//		    		.setPositiveButton("配置", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
//		    			{				
//			    				@Override
//			    				public void onClick(DialogInterface dialog, int which) 
//			    				{
//			    					// TODO Auto-generated method stub	
//			    					int billtype=0;
//			    			    	if(spinbillmanagerbill.getSelectedItemPosition()==1)
//			    			    		billtype=2;
//			    					else if(spinbillmanagerbill.getSelectedItemPosition()==0)
//			    						billtype=0;
//			    			    	//EVprotocolAPI.EV_mdbBillConfig(ToolClass.getCom_id(),billtype);	
//			    			    	Intent intent2=new Intent();
//			    			    	intent2.putExtra("EVWhat", COMService.EV_MDB_B_CON);
//			    			    	intent2.putExtra("billtype", billtype);
//			    			    	intent2.setAction("android.intent.action.comsend");//action与接收器相同
//			    					comBroadreceiver.sendBroadcast(intent2);
//			    					devopt=COMService.EV_MDB_B_CON;
//			    			    	// 弹出信息提示
//						            Toast.makeText(AddInaccount.this, "配置纸币器成功！", Toast.LENGTH_SHORT).show();
//						     }
//		    		      }
//		    			)		    		        
//	    		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
//	    		        	{			
//	    						@Override
//	    						public void onClick(DialogInterface dialog, int which) 
//	    						{
//	    							// TODO Auto-generated method stub				
//	    						}
//	    		        	}
//	    		        )
//	    		        .create();//创建一个对话框
//	    		        alert.show();//显示对话框	
		    	// TODO: handle exception
				ToolClass.failToast("本功能暂未开启！");
		    }
		});	
		btnbillexit = (Button) findViewById(R.id.btnbillexit2);
		btnbillexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
		//===============
    	//硬币器设置页面
    	//===============
		spincoinmanagercoin = (Spinner) findViewById(R.id.spincoinmanagercoin);
		coinStringArray=getResources().getStringArray(R.array.coin_label);
  	    //使用自定义的ArrayAdapter
		coinAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner,coinStringArray);
        spincoinmanagercoin.setAdapter(coinAdapter);// 为ListView列表设置数据源
	  	txtcoinmanagerpar = (TextView) findViewById(R.id.txtcoinmanagerpar);
	  	txtcoinmanagerpar2 = (TextView) findViewById(R.id.txtcoinmanagerpar2);
	  	txtcoinmanagerstate = (TextView) findViewById(R.id.txtcoinmanagerstate);
	  	txtcoinpayback = (TextView) findViewById(R.id.txtcoinpayback);
	  	txtcoinerr = (TextView) findViewById(R.id.txtcoinerr);
	  	txtcoinmanagercoinincount = (TextView) findViewById(R.id.txtcoinmanagercoinincount);
	  	txtcoinmanagercoininamount = (TextView) findViewById(R.id.txtcoinmanagercoininamount);
	  	txtcoinpayin = (TextView) findViewById(R.id.txtcoinpayin);
	  	txtcoinpaymoney = (TextView) findViewById(R.id.txtcoinpaymoney);
	  	txtcoinmanagercoinin1 = (EditText) findViewById(R.id.txtcoinmanagercoinin1);
	  	txtcoinmanagercoinin2 = (EditText) findViewById(R.id.txtcoinmanagercoinin2);
	  	txtcoinmanagercoinin3 = (EditText) findViewById(R.id.txtcoinmanagercoinin3);
	  	txtcoinmanagercoinin4 = (EditText) findViewById(R.id.txtcoinmanagercoinin4);
	  	txtcoinmanagercoinin5 = (EditText) findViewById(R.id.txtcoinmanagercoinin5);
	  	txtcoinmanagercoinin6 = (EditText) findViewById(R.id.txtcoinmanagercoinin6);
	  	txtcoinmanagercoinin7 = (EditText) findViewById(R.id.txtcoinmanagercoinin7);
	  	txtcoinmanagercoinin8 = (EditText) findViewById(R.id.txtcoinmanagercoinin8);
	  	txtcoinmanagercoinin9 = (EditText) findViewById(R.id.txtcoinmanagercoinin9);
	  	txtcoinmanagercoinin10 = (EditText) findViewById(R.id.txtcoinmanagercoinin10);
	  	txtcoinmanagercoinin11 = (EditText) findViewById(R.id.txtcoinmanagercoinin11);
	  	txtcoinmanagercoinin12 = (EditText) findViewById(R.id.txtcoinmanagercoinin12);
	  	txtcoinmanagercoinin13 = (EditText) findViewById(R.id.txtcoinmanagercoinin13);
	  	txtcoinmanagercoinin14 = (EditText) findViewById(R.id.txtcoinmanagercoinin14);
	  	txtcoinmanagercoinin15 = (EditText) findViewById(R.id.txtcoinmanagercoinin15);
	  	txtcoinmanagercoinin16 = (EditText) findViewById(R.id.txtcoinmanagercoinin16);
	  	edtcoinpayout = (EditText) findViewById(R.id.edtcoinpayout);
	  	btncoinon = (Button) findViewById(R.id.btncoinon);
	  	btncoinon.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),0,1,1);
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
				intent.putExtra("bill", 0);	
				intent.putExtra("coin", 1);	
				intent.putExtra("opt", 1);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				devopt=2;//操作硬币器
		    }
		});
	  	btncoinoff = (Button) findViewById(R.id.btncoinoff);
	  	btncoinoff.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),0,1,0);
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
				intent.putExtra("bill", 0);	
				intent.putExtra("coin", 1);	
				intent.putExtra("opt", 0);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				devopt=2;//操作硬币器
		    }
		});
	  	btncoinquery = (Button) findViewById(R.id.btncoinquery);
	  	btncoinquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
		    	Intent intent2=new Intent();
		    	intent2.putExtra("EVWhat", COMService.EV_MDB_HEART);
				intent2.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent2);
				devopt=COMService.EV_MDB_HEART;//Heart操作
		    }
		});
	  	btncoinpayout = (Button) findViewById(R.id.btncoinpayout);
	  	btncoinpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbPayout(ToolClass.getCom_id(),0,1,0,ToolClass.MoneySend(Float.parseFloat(edtcoinpayout.getText().toString())));
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_PAYOUT);	
				intent.putExtra("bill", 0);	
				intent.putExtra("coin", 1);	
				intent.putExtra("billPay", 0);	
				intent.putExtra("coinPay", ToolClass.MoneySend(Float.parseFloat(edtcoinpayout.getText().toString())));	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
				devopt=COMService.EV_MDB_PAYOUT;
				ercheck=true;
		    }
		});
	  	btncoinset = (Button) findViewById(R.id.btncoinset);
	  	btncoinset.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//CoinConfig();
		    	// TODO: handle exception
				ToolClass.failToast("本功能暂未开启！");
		    }
		});
	  	btncoinexit = (Button) findViewById(R.id.btncoinexit);
	  	btncoinexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
	    //===============
    	//hopper设置页面
    	//===============
	  	spinhopper = (Spinner) findViewById(R.id.spinhopper);
	  	hopperStringArray=getResources().getStringArray(R.array.payout_label);
  	    //使用自定义的ArrayAdapter
	  	hopperAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner,hopperStringArray);
	  	spinhopper.setAdapter(hopperAdapter);// 为ListView列表设置数据源
	  	txthopperincount = (TextView) findViewById(R.id.txthopperincount);
	  	txthopperpaymoney = (TextView) findViewById(R.id.txthopperpaymoney);
	    txthopperpaynum = (TextView) findViewById(R.id.txthopperpaynum);
	  	txthopperin1 = (EditText) findViewById(R.id.txthopperin1);
	  	txthopperin2 = (EditText) findViewById(R.id.txthopperin2);
	  	txthopperin3 = (EditText) findViewById(R.id.txthopperin3);
	  	txthopperin4 = (EditText) findViewById(R.id.txthopperin4);
	  	txthopperin5 = (EditText) findViewById(R.id.txthopperin5);
	  	txthopperin6 = (EditText) findViewById(R.id.txthopperin6);
	  	txthopperin7 = (EditText) findViewById(R.id.txthopperin7);
	  	txthopperin8 = (EditText) findViewById(R.id.txthopperin8);	  	
	  	edthopperpayout = (EditText) findViewById(R.id.edthopperpayout);
	  	edthopperpayno = (EditText) findViewById(R.id.edthopperpayno);
	  	edthopperpaynum = (EditText) findViewById(R.id.edthopperpaynum);
	  	btnhopperquery = (Button) findViewById(R.id.btnhopperquery);
	  	btnhopperquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
		    	//EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
		    	Intent intent2=new Intent();
		    	intent2.putExtra("EVWhat", COMService.EV_MDB_HEART);
				intent2.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent2);
				devopt=COMService.EV_MDB_HEART;//Heart操作
		    }
		});
	  	btnhopperpay = (Button) findViewById(R.id.btnhopperpay);
	  	btnhopperpay.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbPayout(ToolClass.getCom_id(),0,1,0,ToolClass.MoneySend(Float.parseFloat(edthopperpayout.getText().toString())));
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_PAYOUT);	
		    	intent.putExtra("bill", 0);	
		    	intent.putExtra("coin", 1);	
		    	intent.putExtra("billPay", 0);	
		    	intent.putExtra("coinPay", ToolClass.MoneySend(Float.parseFloat(edthopperpayout.getText().toString())));	
		    	intent.setAction("android.intent.action.comsend");//action与接收器相同
		    	comBroadreceiver.sendBroadcast(intent);
		    	devopt=COMService.EV_MDB_PAYOUT;
		    	ercheck=true;
		    }
		});
	  	btnhopperpaymoney = (Button) findViewById(R.id.btnhopperpaymoney);
	  	btnhopperpaymoney.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbPayout(ToolClass.getCom_id(),0,1,0,ToolClass.MoneySend(Float.parseFloat(edthopperpayout.getText().toString())));
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_PAYOUT);	
		    	intent.putExtra("bill", 0);	
		    	intent.putExtra("coin", 1);	
		    	intent.putExtra("billPay", 0);	
		    	intent.putExtra("coinPay", ToolClass.MoneySend(Float.parseFloat(edthopperpayout.getText().toString())));	
		    	intent.setAction("android.intent.action.comsend");//action与接收器相同
		    	comBroadreceiver.sendBroadcast(intent);
		    	devopt=COMService.EV_MDB_PAYOUT;
		    	ercheck=true;
		    }
		});
	  	btnhopperpaynum = (Button) findViewById(R.id.btnhopperpaynum);
	  	btnhopperpaynum.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//EVprotocolAPI.EV_mdbHopperPayout(ToolClass.getCom_id(),Integer.parseInt(edthopperpayno.getText().toString()),Integer.parseInt(edthopperpaynum.getText().toString()));
		    	Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_HP_PAYOUT);	
		    	intent.putExtra("no", Integer.parseInt(edthopperpayno.getText().toString()));	
		    	intent.putExtra("nums", Integer.parseInt(edthopperpaynum.getText().toString()));	
		    	intent.setAction("android.intent.action.comsend");//action与接收器相同
		    	comBroadreceiver.sendBroadcast(intent);
		    	devopt=COMService.EV_MDB_HP_PAYOUT;
		    	ercheck=true;
		    }
		});
	  	btnhopperset = (Button) findViewById(R.id.btnhopperset);
	  	btnhopperset.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	//CoinConfig();
		    	// TODO: handle exception
				ToolClass.failToast("本功能暂未开启！");
		    }
		});
	  	btnhopperexit = (Button) findViewById(R.id.btnhopperexit);
	  	btnhopperexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();
		    }
		});
	}
	
	//调用倒计时定时器
	TimerTask task = new TimerTask() { 
        @Override 
        public void run() { 
  
            runOnUiThread(new Runnable() {      // UI thread 
                @Override 
                public void run() { 
                    //发送查询交易指令
                    if(cashopt)
                    {
                    	//没有在退币中，才可以查询
                    	if(ercheck==false)
                    	{
		                    queryLen++;
		                    if(queryLen>=5)
		                    {
		                    	queryLen=0;
		                    	//EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
		        		    	Intent intent2=new Intent();
		        		    	intent2.putExtra("EVWhat", COMService.EV_MDB_HEART);
		        				intent2.setAction("android.intent.action.comsend");//action与接收器相同
		        				comBroadreceiver.sendBroadcast(intent2);
		        				devopt=COMService.EV_MDB_HEART;//Heart操作
		                    }
                    	}
                    }                    
                } 
            }); 
        } 
    };
	
	//2.创建COMReceiver的接收器广播，用来接收服务器同步的内容
	public class COMReceiver extends BroadcastReceiver 
	{

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
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 现金设备操作="+Set,"com.txt");
				int EV_TYPE=Set.get("EV_TYPE");
				switch(EV_TYPE)
				{
					case COMService.EV_MDB_ENABLE:
						if(devopt==1)//操作纸币器
						{
							//纸币启动成功
							if((Integer)Set.get("bill_result")>0)
							{
								ToolClass.setBill_err(0);
	//							EVprotocolAPI.EV_mdbBillInfoCheck(ToolClass.getCom_id());
	//							EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
								//纸币器查询接口
								Intent intent2=new Intent();
						    	intent2.putExtra("EVWhat", COMService.EV_MDB_B_INFO);	
								intent2.setAction("android.intent.action.comsend");//action与接收器相同
								comBroadreceiver.sendBroadcast(intent2);
								cashopt=true;
							}
							else
							{
								ToolClass.setBill_err(2);
								cashopt=false;
							}
						}
						
						if(devopt==2)//操作硬币器
						{
							//硬币启动成功
							if((Integer)Set.get("coin_result")>0)
							{
								ToolClass.setCoin_err(0);
	//							EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
	//							EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
								//硬币器查询接口
								Intent intent3=new Intent();
						    	intent3.putExtra("EVWhat", COMService.EV_MDB_C_INFO);	
								intent3.setAction("android.intent.action.comsend");//action与接收器相同
								comBroadreceiver.sendBroadcast(intent3);
								cashopt=true;
							}
							else
							{
								ToolClass.setCoin_err(2);
								cashopt=false;
							}
						}
						break;
					case COMService.EV_MDB_B_INFO:							 
						String acceptor=((Integer)Set.get("acceptor")==2)?"MDB":"无";
						String dispenser=((Integer)Set.get("dispenser")==2)?"MDB":"无";
						String code=String.valueOf(Set.get("code"));
						String sn=String.valueOf(Set.get("sn"));
						String model=String.valueOf( Set.get("model"));
						String ver=String.valueOf(Set.get("ver"));
						int capacity=(Integer)Set.get("capacity");
						String str="纸币接收器:"+acceptor+"纸币找零器:"+dispenser+"厂商:"+code
								+"序列号"+sn;
						txtbillmanagerpar.setText(str);
						str=" 型号:"+model+"版本号:"+ver+"储币量:"+capacity;
						txtbillmanagerpar2.setText(str);
						if((Integer)Set.get("acceptor")==2)
							spinbillmanagerbill.setSelection((Integer)Set.get("acceptor")-1);
						else if((Integer)Set.get("acceptor")==0)
							spinbillmanagerbill.setSelection(0);	
						
						Map<String,Integer> allSet1=new LinkedHashMap<String, Integer>();
						allSet1.put("ch_r1", Set.get("ch_r1"));
						allSet1.put("ch_r2", Set.get("ch_r2"));
						allSet1.put("ch_r3", Set.get("ch_r3"));
						allSet1.put("ch_r4", Set.get("ch_r4"));
						allSet1.put("ch_r5", Set.get("ch_r5"));
						allSet1.put("ch_r6", Set.get("ch_r6"));
						allSet1.put("ch_r7", Set.get("ch_r7"));
						allSet1.put("ch_r8", Set.get("ch_r8"));
						String allb1[]=new String[allSet1.size()];	
						int bi=0;
						Set<Map.Entry<String,Integer>> allset=allSet1.entrySet();  //实例化
					    Iterator<Map.Entry<String,Integer>> iter=allset.iterator();
					    while(iter.hasNext())
					    {
					        Map.Entry<String,Integer> me=iter.next();
					        //str+="["+me.getKey() + "]" + ToolClass.MoneyRec(me.getValue()) + ",";
					        allb1[bi++]="["+me.getKey() + "]" + ToolClass.MoneyRec(me.getValue());
					    }
					    String bstr1="",bstr2="";
					    for(bi=0;bi<8;bi++)
					    {
					    	if(bi<4)
					    		bstr1+=allb1[bi];
					    	else
					    		bstr2+=allb1[bi];							
					    }
					    txtbillmanagerbillin.setText(bstr1);
					    txtbillmanagerbillin2.setText(bstr2);
					  
					    Map<String,Integer> allSet2=new LinkedHashMap<String, Integer>();
						allSet2.put("ch_d1", Set.get("ch_d1"));
						allSet2.put("ch_d2", Set.get("ch_d2"));
						allSet2.put("ch_d3", Set.get("ch_d3"));
						allSet2.put("ch_d4", Set.get("ch_d4"));
						allSet2.put("ch_d5", Set.get("ch_d5"));
						allSet2.put("ch_d6", Set.get("ch_d6"));
						allSet2.put("ch_d7", Set.get("ch_d7"));
						allSet2.put("ch_d8", Set.get("ch_d8"));
					    String allb2[]=new String[allSet2.size()];	
					    bi=0;
						Set<Map.Entry<String,Integer>> allset2=allSet2.entrySet();  //实例化
					    Iterator<Map.Entry<String,Integer>> iter2=allset2.iterator();
					    while(iter2.hasNext())
					    {
					        Map.Entry<String,Integer> me=iter2.next();
					        //str+="[通道"+me.getKey() + "]=" + ToolClass.MoneyRec(me.getValue()) + ",";
					        allb2[bi++]="["+me.getKey() + "]" + ToolClass.MoneyRec(me.getValue());
					    }
					    bstr1="";
					    bstr2="";
					    for(bi=0;bi<8;bi++)
					    {
					    	if(bi<4)
					    		bstr1+=allb2[bi];
					    	else
					    		bstr2+=allb2[bi];							
					    }
					    txtbillmanagerbillpay.setText(bstr1);
					    txtbillmanagerbillpay2.setText(bstr2);
					    //Heart操作
					    Intent intent2=new Intent();
				    	intent2.putExtra("EVWhat", COMService.EV_MDB_HEART);
						intent2.setAction("android.intent.action.comsend");//action与接收器相同
						comBroadreceiver.sendBroadcast(intent2);
						devopt=COMService.EV_MDB_HEART;//Heart操作
						break;
					case COMService.EV_MDB_C_INFO:
						String acceptor2="";
						if((Integer)Set.get("acceptor")==3)
							acceptor2="串行脉冲";
						else if((Integer)Set.get("acceptor")==2)
							acceptor2="MDB";
						else if((Integer)Set.get("acceptor")==1)
							acceptor2="并行脉冲";
						else if((Integer)Set.get("acceptor")==0)
							acceptor2="无";
						String dispenser2="";
						if((Integer)Set.get("dispenser")==2)
							dispenser2="MDB";
						else if((Integer)Set.get("dispenser")==1)
							dispenser2="hopper";
						else if((Integer)Set.get("dispenser")==0)
							dispenser2="无";
						String code2=String.valueOf(Set.get("code"));
						String sn2=String.valueOf(Set.get("sn"));
						String model2=String.valueOf(Set.get("model"));
						String ver2=String.valueOf(Set.get("ver"));
						int capacity2=(Integer)Set.get("capacity");
						String str2="硬币接收器:"+acceptor2+"硬币找零器:"+dispenser2+"厂商:"+code2
								+"序列号"+sn2;
						txtcoinmanagerpar.setText(str2);
						str2=" 型号:"+model2+"版本号:"+ver2+"储币量:"+capacity2;
						txtcoinmanagerpar2.setText(str2);
						spincoinmanagercoin.setSelection((Integer)Set.get("acceptor"));
						
						
						str2="";
						Map<String,Integer> allSet3=new LinkedHashMap<String, Integer>();
						allSet3.put("ch_r1", Set.get("ch_r1"));
						allSet3.put("ch_r2", Set.get("ch_r2"));
						allSet3.put("ch_r3", Set.get("ch_r3"));
						allSet3.put("ch_r4", Set.get("ch_r4"));
						allSet3.put("ch_r5", Set.get("ch_r5"));
						allSet3.put("ch_r6", Set.get("ch_r6"));
						allSet3.put("ch_r7", Set.get("ch_r7"));
						allSet3.put("ch_r8", Set.get("ch_r8"));
						//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet3.toString());
						double all[]=new double[allSet3.size()];	
						int i=0;
						Set<Map.Entry<String,Integer>> allset3=allSet3.entrySet();  //实例化
						Iterator<Map.Entry<String,Integer>> iter3=allset3.iterator();
					    while(iter3.hasNext())
					    {
					        Map.Entry<String,Integer> me=iter3.next();
					        all[i++]=ToolClass.MoneyRec(me.getValue());
					        //str+="[通道"+me.getKey() + "]=" + ToolClass.MoneyRec(me.getValue()) + ",";
					    }
					    txtcoinmanagercoinin1.setText(String.valueOf(all[0]));
					    txtcoinmanagercoinin2.setText(String.valueOf(all[1]));
					    txtcoinmanagercoinin3.setText(String.valueOf(all[2]));
					    txtcoinmanagercoinin4.setText(String.valueOf(all[3]));
					    txtcoinmanagercoinin5.setText(String.valueOf(all[4]));
					    txtcoinmanagercoinin6.setText(String.valueOf(all[5]));
					    txtcoinmanagercoinin7.setText(String.valueOf(all[6]));
					    txtcoinmanagercoinin8.setText(String.valueOf(all[7]));
					    txtcoinmanagercoinin9.setText("0");
					    txtcoinmanagercoinin10.setText("0");
					    txtcoinmanagercoinin11.setText("0");
					    txtcoinmanagercoinin12.setText("0");
					    txtcoinmanagercoinin13.setText("0");
					    txtcoinmanagercoinin14.setText("0");
					    txtcoinmanagercoinin15.setText("0");
					    txtcoinmanagercoinin16.setText("0");
					    //找零通道面值
					    spinhopper.setSelection((Integer)Set.get("dispenser"));
					    str2="";
					    Map<String,Integer> allSet4=new LinkedHashMap<String, Integer>();
						allSet4.put("ch_d1", Set.get("ch_d1"));
						allSet4.put("ch_d2", Set.get("ch_d2"));
						allSet4.put("ch_d3", Set.get("ch_d3"));
						allSet4.put("ch_d4", Set.get("ch_d4"));
						allSet4.put("ch_d5", Set.get("ch_d5"));
						allSet4.put("ch_d6", Set.get("ch_d6"));
						allSet4.put("ch_d7", Set.get("ch_d7"));
						allSet4.put("ch_d8", Set.get("ch_d8"));
					    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+allSet4.toString(),"log.txt");
					    double all2[]=new double[allSet4.size()];	
						i=0;
						Set<Map.Entry<String,Integer>> allset4=allSet4.entrySet();  //实例化
					    Iterator<Map.Entry<String,Integer>> iter4=allset4.iterator();
					    while(iter4.hasNext())
					    {
					        Map.Entry<String,Integer> me=iter4.next();
					        all2[i++]=ToolClass.MoneyRec(me.getValue());
					        //str2+="[通道"+me.getKey() + "]=" + ToolClass.MoneyRec(me.getValue()) + ",";
					    }
					    txthopperin1.setText(String.valueOf(all2[0]));
					    txthopperin2.setText(String.valueOf(all2[1]));
					    txthopperin3.setText(String.valueOf(all2[2]));
					    txthopperin4.setText(String.valueOf(all2[3]));
					    txthopperin5.setText(String.valueOf(all2[4]));
					    txthopperin6.setText(String.valueOf(all2[5]));
					    txthopperin7.setText(String.valueOf(all2[6]));
					    txthopperin8.setText(String.valueOf(all2[7]));
					    //Heart操作
					    Intent intent4=new Intent();
				    	intent4.putExtra("EVWhat", COMService.EV_MDB_HEART);
						intent4.setAction("android.intent.action.comsend");//action与接收器相同
						comBroadreceiver.sendBroadcast(intent4);
						devopt=COMService.EV_MDB_HEART;//Heart操作
						break;	
					case COMService.EV_MDB_HEART://心跳查询
						Map<String,Object> obj=new LinkedHashMap<String, Object>();
						obj.putAll(Set);
						String bill_enable=((Integer)Set.get("bill_enable")==1)?"使能":"禁能";
						txtbillmanagerstate.setText(bill_enable);
						String bill_payback=((Integer)Set.get("bill_payback")==1)?"触发":"没触发";
					  	txtbillpayback.setText(bill_payback);
					  	String bill_err=((Integer)Set.get("bill_err")==0)?"正常":"故障码:"+(Integer)Set.get("bill_err");
					  	txtbillerr.setText(bill_err);
					  	double money=ToolClass.MoneyRec((Integer)Set.get("bill_recv"));					  	
					  	txtbillpayin.setText(String.valueOf(money));
					  	amount=money;//当前纸币投入
					  	money=ToolClass.MoneyRec((Integer)Set.get("bill_remain"));
					  	txtbillmanagerbillpayamount.setText(String.valueOf(money));
					  	int bill_errstatus=ToolClass.getvmcStatus(obj,1);
					  	ToolClass.setBill_err(bill_errstatus);
					  	
					  	String coin_enable=((Integer)Set.get("coin_enable")==1)?"使能":"禁能";
					  	txtcoinmanagerstate.setText(coin_enable);
						String coin_payback=((Integer)Set.get("coin_payback")==1)?"触发":"没触发";
						txtcoinpayback.setText(coin_payback);
					  	String coin_err=((Integer)Set.get("coin_err")==0)?"正常":"故障码:"+(Integer)Set.get("coin_err");
					  	txtcoinerr.setText(coin_err);
					  	money=ToolClass.MoneyRec((Integer)Set.get("coin_recv"));					  	
					  	txtcoinpayin.setText(String.valueOf(money));
					  	amount+=money;//当前硬币投入
					  	money=ToolClass.MoneyRec((Integer)Set.get("coin_remain"));
					  	txtcoinmanagercoininamount.setText(String.valueOf(money));
					  	int coin_errstatus=ToolClass.getvmcStatus(obj,2);
					  	ToolClass.setCoin_err(coin_errstatus);
					  	
					  	String hopperString=null;
					  	if(Set.containsKey("hopper1"))
					  	{
						  	hopperString="[1]:"+ToolClass.gethopperstats((Integer)Set.get("hopper1"))+"[2]:"+ToolClass.gethopperstats((Integer)Set.get("hopper2"))
						  				+"[[3]:"+ToolClass.gethopperstats((Integer)Set.get("hopper3"))+"[4]:"+ToolClass.gethopperstats((Integer)Set.get("hopper4"))
							  			+"[5]:"+ToolClass.gethopperstats((Integer)Set.get("hopper5"))+"[6]:"+ToolClass.gethopperstats((Integer)Set.get("hopper6"))
							  			+"[7]:"+ToolClass.gethopperstats((Integer)Set.get("hopper7"))+"[8]:"+ToolClass.gethopperstats((Integer)Set.get("hopper8"));
					  	}
					  	txthopperincount.setText(hopperString);
						break;
					case COMService.EV_MDB_PAYOUT://找零
						money=ToolClass.MoneyRec((Integer)Set.get("bill_changed"));					  	
						txtpaymoney.setText(String.valueOf(money));	
						money=ToolClass.MoneyRec((Integer)Set.get("coin_changed"));					  	
						txtcoinpaymoney.setText(String.valueOf(money));	
						txthopperpaymoney.setText(String.valueOf(money));
						ercheck=false;
						break;
					case COMService.EV_MDB_HP_PAYOUT://找零
						txthopperpaynum.setText(String.valueOf((Integer)Set.get("changed")));
						ercheck=false;
						break;
					case COMService.EV_MDB_COST://扣钱
						//如果已经打开了现金设备，则关闭它
						if(cashopt)
						{
							Intent intent5=new Intent();
					    	intent5.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
							intent5.putExtra("bill", 1);	
							intent5.putExtra("coin", 1);	
							intent5.putExtra("opt", 0);	
							intent5.setAction("android.intent.action.comsend");//action与接收器相同
							comBroadreceiver.sendBroadcast(intent5);
						}
						finish();
						break;
					default:break;	
				}
				break;			
			}			
		}

	}
	
	private void CoinConfig()
	{
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(AddInaccount.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要配置硬币器和找零器吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("配置", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					Map<String, Integer>ch_r=new LinkedHashMap<String, Integer>();
	    			    	Map<String, Integer>ch_d=new LinkedHashMap<String, Integer>();
	    			    	ch_r.put("1", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin1.getText().toString())));
	    			    	ch_r.put("2", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin2.getText().toString())));
	    			    	ch_r.put("3", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin3.getText().toString())));
	    			    	ch_r.put("4", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin4.getText().toString())));
	    			    	ch_r.put("5", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin5.getText().toString())));
	    			    	ch_r.put("6", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin6.getText().toString())));
	    			    	ch_r.put("7", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin7.getText().toString())));
	    			    	ch_r.put("8", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin8.getText().toString())));
	    			    	ch_r.put("9", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin9.getText().toString())));
	    			    	ch_r.put("10", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin10.getText().toString())));
	    			    	ch_r.put("11", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin11.getText().toString())));
	    			    	ch_r.put("12", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin12.getText().toString())));
	    			    	ch_r.put("13", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin13.getText().toString())));
	    			    	ch_r.put("14", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin14.getText().toString())));
	    			    	ch_r.put("15", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin15.getText().toString())));
	    			    	ch_r.put("16", ToolClass.MoneySend(Float.parseFloat(txtcoinmanagercoinin16.getText().toString())));
	    			    	
	    			    	ch_d.put("1", ToolClass.MoneySend(Float.parseFloat(txthopperin1.getText().toString())));
	    			    	ch_d.put("2", ToolClass.MoneySend(Float.parseFloat(txthopperin2.getText().toString())));
	    			    	ch_d.put("3", ToolClass.MoneySend(Float.parseFloat(txthopperin3.getText().toString())));
	    			    	ch_d.put("4", ToolClass.MoneySend(Float.parseFloat(txthopperin4.getText().toString())));
	    			    	ch_d.put("5", ToolClass.MoneySend(Float.parseFloat(txthopperin5.getText().toString())));
	    			    	ch_d.put("6", ToolClass.MoneySend(Float.parseFloat(txthopperin6.getText().toString())));
	    			    	ch_d.put("7", ToolClass.MoneySend(Float.parseFloat(txthopperin7.getText().toString())));
	    			    	ch_d.put("8", ToolClass.MoneySend(Float.parseFloat(txthopperin8.getText().toString())));
	    			    	ch_d.put("9", 0);
	    			    	ch_d.put("10", 0);
	    			    	ch_d.put("11", 0);
	    			    	ch_d.put("12", 0);
	    			    	ch_d.put("13", 0);
	    			    	ch_d.put("14", 0);
	    			    	ch_d.put("15", 0);
	    			    	ch_d.put("16", 0);
	    			    	//EVprotocolAPI.EV_mdbCoinConfig(ToolClass.getCom_id(),spincoinmanagercoin.getSelectedItemPosition(),spinhopper.getSelectedItemPosition(),ch_r,ch_d);
	    			    	//ToolClass.Log(ToolClass.INFO,"EV_COM","cointype="+spincoinmanagercoin.getSelectedItemPosition()+"hoppertype="+spinhopper.getSelectedItemPosition(),"com.txt");
	    			    	Intent intent2=new Intent();
	    			    	intent2.putExtra("EVWhat", COMService.EV_MDB_C_CON);
	    			    	intent2.putExtra("acceptor", spincoinmanagercoin.getSelectedItemPosition());
	    			    	intent2.putExtra("dispenser", spinhopper.getSelectedItemPosition());
	    			    	//传递数据
					        SerializableMap myMap=new SerializableMap();
					        myMap.setMap(ch_r);//将map数据添加到封装的myMap<span></span>中
					        Bundle bundle=new Bundle();
					        bundle.putSerializable("ch_r", myMap);
					        intent2.putExtras(bundle);
					        //传递数据
					        SerializableMap myMap2=new SerializableMap();
					        myMap2.setMap(ch_d);//将map数据添加到封装的myMap<span></span>中
					        Bundle bundle2=new Bundle();
					        bundle2.putSerializable("ch_d", myMap2);
					        intent2.putExtras(bundle2);
	    			    	intent2.setAction("android.intent.action.comsend");//action与接收器相同
	    					comBroadreceiver.sendBroadcast(intent2);
	    					devopt=COMService.EV_MDB_B_CON;
	    			    	// 弹出信息提示
				            Toast.makeText(AddInaccount.this, "配置硬币器找零器成功！", Toast.LENGTH_SHORT).show();
				     }
    		      }
    			)		    		        
		        .setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
		        	{			
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							// TODO Auto-generated method stub				
						}
		        	}
		        )
		        .create();//创建一个对话框
		        alert.show();//显示对话框
	}
	//关闭页面
	private void finishActivity()
	{
		//扣钱
	    //EVprotocolAPI.EV_mdbCost(ToolClass.getCom_id(),ToolClass.MoneySend((float)amount));
		if(amount>0)
		{
			Intent intent=new Intent();
	    	intent.putExtra("EVWhat", COMService.EV_MDB_COST);	
			intent.putExtra("cost", ToolClass.MoneySend((float)amount));	
			intent.setAction("android.intent.action.comsend");//action与接收器相同
			comBroadreceiver.sendBroadcast(intent);
			devopt=COMService.EV_MDB_COST;
		}
		else
		{
			//如果已经打开了现金设备，则关闭它
			if(cashopt)
			{
				Intent intent=new Intent();
		    	intent.putExtra("EVWhat", COMService.EV_MDB_ENABLE);	
				intent.putExtra("bill", 1);	
				intent.putExtra("coin", 1);	
				intent.putExtra("opt", 0);	
				intent.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent);
			}
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
