package com.easivend.app.business;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.app.business.BusHuo.COMReceiver;
import com.easivend.app.business.BusPort.EVServerReceiver;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.fragment.BusinesslandFragment;
import com.easivend.fragment.BusinesslandFragment.BusFragInteraction;
import com.easivend.fragment.MoviewlandFragment;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_product;
import com.easivend.view.PassWord;
import com.example.evconsole.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BusLand extends Activity implements MovieFragInteraction,BusFragInteraction{	
    private MoviewlandFragment moviewlandFragment;
    private BusinesslandFragment businesslandFragment;
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    private final int SPLASH_DISPLAY_LENGHT = 5*60; //  5*60延迟5分钟	
    private int recLen = SPLASH_DISPLAY_LENGHT; 
    private boolean isbus=true;//true表示在广告页面，false在其他页面
    //交易页面
    Intent intent=null;
    final static int REQUEST_CODE=1; 
    final static int PWD_CODE=2;
    //Server服务相关
  	LocalBroadcastManager localBroadreceiver;
  	EVServerReceiver receiver;
    //=================
    //COM服务相关
    //=================
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
		setContentView(R.layout.busland);		
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		//=============
		//Server服务相关
		//=============
		//4.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
		
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		//初始化默认fragment
		initView();		
		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<portthread="+Thread.currentThread().getId(),"log.txt"); 
	        	  if(isbus==false)
	        	  {
		        	  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
		        	  recLen--; 		    	      
		        	  if(recLen == 0)
		              { 
		                  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recclose=movielandFragment","log.txt");
			    	      switchMovie();
		              }	
	        	  }
	        } 
	    }, 1, 1, TimeUnit.SECONDS);       // timeTask  
	}
	
	//初始化默认fragment
	public void initView() {        
        // 设置默认的Fragment
        setDefaultFragment();
    }
	
	// 设置默认的Fragment
	@SuppressLint("NewApi")
    private void setDefaultFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        moviewlandFragment = new MoviewlandFragment();
        transaction.replace(R.id.id_content, moviewlandFragment);
        transaction.commit();
    }

	//步骤三、实现Movie接口,跳转到Business中
	@Override
	public void switchBusiness() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=switchBusiness","log.txt");
	    FragmentManager fm = getFragmentManager();
	    // 开启Fragment事务
	    FragmentTransaction transaction = fm.beginTransaction();
	    if (businesslandFragment == null) 
	    {
	       	businesslandFragment = new BusinesslandFragment();
	    }
	    //步骤五、传递这个数据给businesslandFragment
	    //....本流程不用传递数据
	    
	    transaction.replace(R.id.id_content, businesslandFragment);
	    // transaction.addToBackStack();
	    // 事务提交
	    transaction.commit();
	    //打开定时器
	    isbus=false;
	    recLen=SPLASH_DISPLAY_LENGHT;
	}
	
	//步骤三、实现Business接口,关闭交易界面
	@Override
	public void finishBusiness() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=退出交易页面","log.txt");
//    	Intent intent = new Intent();
//    	intent.setClass(BusLand.this, PassWord.class);// 使用AddInaccount窗口初始化Intent
//        startActivityForResult(intent, PWD_CODE);
		//延时0.5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	finish(); 
            }

		}, 500);		
	}
	//步骤三、实现Business接口,暂停倒计时定时器并且转到商品购物页面
	//buslevel级别1到商品类别，2到商品导购页面，3到商品详细页面
	@Override
	public void gotoBusiness(int buslevel,Map<String, String>str) {
		// TODO Auto-generated method stub
		isbus=true;
	    recLen=SPLASH_DISPLAY_LENGHT;
	    //=============
  		//COM服务相关
  		//=============
  		//5.解除注册接收器
  		comBroadreceiver.unregisterReceiver(comreceiver);
  		
		switch(buslevel)
		{
			case 1:
				intent = new Intent(BusLand.this, BusgoodsClass.class);// 使用Accountflag窗口初始化Intent
		    	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
				break;
			case 2:
				intent = new Intent(BusLand.this, Busgoods.class);// 使用Accountflag窗口初始化Intent
            	intent.putExtra("proclassID", "");
            	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
				break;
			case 3:
				//可以提货
    			if(ToolClass.getzhitihuotype(BusLand.this, str.get("cabID"), str.get("huoID")))
    			{
    				intent = new Intent(BusLand.this, BusZhitihuo.class);// 使用Accountflag窗口初始化Intent
    				OrderDetail.setProID(str.get("proID"));
    		    	OrderDetail.setProductID(str.get("productID"));
    		    	OrderDetail.setProType(str.get("proType"));
    		    	OrderDetail.setShouldPay(Float.parseFloat(str.get("prosales")));
    		    	OrderDetail.setShouldNo(1);
    		    	OrderDetail.setCabID(str.get("cabID"));
    		    	OrderDetail.setColumnID(str.get("huoID"));
    		    	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
    			}
    			else
    			{
					intent = new Intent(BusLand.this, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
		        	intent.putExtra("proID", str.get("proID"));
		        	intent.putExtra("productID", str.get("productID"));
		        	intent.putExtra("proImage", str.get("proImage"));
		        	intent.putExtra("prosales", str.get("prosales"));
		        	intent.putExtra("procount", str.get("procount"));
		        	intent.putExtra("proType", str.get("proType"));//1代表通过商品ID出货,2代表通过货道出货
		        	intent.putExtra("cabID", str.get("cabID"));//出货柜号,proType=1时无效
		        	intent.putExtra("huoID", str.get("huoID"));//出货货道号,proType=1时无效
	
	
	//	        	OrderDetail.setProID(proID);
	//            	OrderDetail.setProductID(productID);
	//            	OrderDetail.setProType("2");
	//            	OrderDetail.setCabID(cabID);
	//            	OrderDetail.setColumnID(huoID);
	//            	OrderDetail.setShouldPay(Float.parseFloat(prosales));
	//            	OrderDetail.setShouldNo(1);
		        	
		        	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
    			}
				break;
			case 4:
				intent = new Intent(BusLand.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
            	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
				break;	
		}
	}
	//步骤三、实现Business接口,传递取货码
	@Override
	public void quhuoBusiness(String PICKUP_CODE)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<land取货码="+PICKUP_CODE,"log.txt");
		Intent intent2=new Intent(); 
		intent2.putExtra("EVWhat", EVServerhttp.SETPICKUPCHILD);
		intent2.putExtra("PICKUP_CODE", PICKUP_CODE);
		intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
		localBroadreceiver.sendBroadcast(intent2);
	}
	
	//步骤三、实现Business接口,传递提示信息
	@Override
	public void tishiInfo(int infotype) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<land提示类型="+infotype,"log.txt");
		Intent intent3 = new Intent(BusLand.this, BusTishi.class);// 使用Accountflag窗口初始化Intent
    	intent3.putExtra("infotype", infotype);
    	startActivityForResult(intent3,REQUEST_CODE);// 打开Accountflag
	}
	
	//=============
	//Server服务相关
	//=============	
	//2.创建EVServerReceiver的接收器广播，用来接收服务器同步的内容
	public class EVServerReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub			
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case EVServerhttp.SETPICKUPMAIN:
				String PRODUCT_NO=bundle.getString("PRODUCT_NO");
				String out_trade_no=bundle.getString("out_trade_no");
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=取货码成功PRODUCT_NO="+PRODUCT_NO+"out_trade_no="+out_trade_no,"log.txt");					
				// 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_product表中
		 	    vmc_productDAO productdao = new vmc_productDAO(context);
		 	    Tb_vmc_product tb_vmc_product=productdao.find(PRODUCT_NO);
		 	    //保存到报表表里面
		 	    //订单总信息
		 	    OrderDetail.setProID(tb_vmc_product.getProductID()+"-"+tb_vmc_product.getProductName());		 	    
		 	    OrderDetail.setProType("1");
		 	    OrderDetail.setOrdereID(out_trade_no);
		 	    //订单支付表 
				OrderDetail.setPayType(-1);
		 	    OrderDetail.setShouldPay(tb_vmc_product.getSalesPrice());
		 	    OrderDetail.setShouldNo(1);
		 	    OrderDetail.setCabID("");
		 		OrderDetail.setColumnID("");
		 	    //订单详细信息表   
		 	    OrderDetail.setProductID(PRODUCT_NO);
		 	    gotoBusiness(4,null);
				break;
			case EVServerhttp.SETERRFAILPICKUPMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=取货码失败","log.txt");
				restarttimer();//重新打开定时器
				// 弹出信息提示
				ToolClass.failToast("抱歉，取货码无效,请联系管理员！");
				break;	
			case EVServerhttp.SETADVRESETMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=刷新广告","log.txt");
				//listterner.BusportAds();
				break;
			}			
		}

	}
	
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
			case COMThread.EV_BUTTONMAIN: 
					SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
					Map<String, Integer> Set2=serializableMap2.getMap();
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 按键操作="+Set2,"com.txt");
					int EV_TYPE=Set2.get("EV_TYPE");
					//上报货道按键
					if(EV_TYPE==COMThread.EV_BUTTONRPT_HUODAO)
					{
						//跳转商品
						//发送出货指令
				        String proID = null;
				    	String productID = null;
				    	String proImage = null;
				    	String cabID = null;
				    	String huoID = null;
				        String prosales = null;
				        
						cabID="1";
					    int huono=Set2.get("btnvalue");
					    huoID=(huono<=9)?("0"+huono):String.valueOf(huono);
					    vmc_columnDAO columnDAO = new vmc_columnDAO(context);// 创建InaccountDAO对象		    
					    Tb_vmc_product tb_inaccount = columnDAO.getColumnproduct(cabID,huoID);
					    if(tb_inaccount!=null)
					    {	
					    	switchBusiness();
						    productID=tb_inaccount.getProductID().toString();
						    prosales=String.valueOf(tb_inaccount.getSalesPrice());
						    proImage=tb_inaccount.getAttBatch1();
						    proID=productID+"-"+tb_inaccount.getProductName().toString();
						    ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
									+productID+" proType="
									+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
									+"1","log.txt");						    

				        	Map<String, String>str=new HashMap<String, String>();
				        	str.put("proID", proID);
				        	str.put("productID", productID);
				        	str.put("proImage", proImage);
				        	str.put("prosales", prosales);
				        	str.put("procount", "1");
				        	str.put("proType", "2");//1代表通过商品ID出货,2代表通过货道出货
				        	str.put("cabID", cabID);//出货柜号,proType=1时无效
				        	str.put("huoID", huoID);//出货货道号,proType=1时无效
				        	gotoBusiness(3,str);
					    }
					    else
					    {
					    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品proID="+proID+" productID="
									+productID+" proType="
									+"2"+" cabID="+cabID+" huoID="+huoID+" prosales="+prosales+" count="
									+"1","log.txt");						    
						    // 弹出信息提示
						    ToolClass.failToast("抱歉，本商品已售完！");					
					    }
					}				
					//上报维护模式按键
					else if(EV_TYPE==COMThread.EV_BUTTONRPT_MAINTAIN)
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 维护模式","com.txt");
						finishBusiness();
					}
					break;
				
			}			
		}

	}
	
	//返回到广告页面
	public void switchMovie() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=switchMovie","log.txt");
	    FragmentManager fm = getFragmentManager();
	    // 开启Fragment事务
	    FragmentTransaction transaction = fm.beginTransaction();
	    if (moviewlandFragment == null) 
	    {
        	moviewlandFragment = new MoviewlandFragment();
        }
	    //步骤五、传递这个数据给businesslandFragment
	    //....本流程不用传递数据
	    
        // 使用当前Fragment的布局替代id_content的控件
        transaction.replace(R.id.id_content, moviewlandFragment);	    
	    // transaction.addToBackStack();
	    // 事务提交
	    transaction.commit();
	    recLen=SPLASH_DISPLAY_LENGHT;
	    isbus=true;
	}
	
	//步骤三、实现Business接口,暂停定时器
	@Override
	public void stoptimer() {
		// TODO Auto-generated method stub
		isbus=true;
	    recLen=SPLASH_DISPLAY_LENGHT;
	}
	//步骤三、实现Business接口,重新打开定时器
	@Override
	public void restarttimer() {
		// TODO Auto-generated method stub
		isbus=false;
	    recLen=SPLASH_DISPLAY_LENGHT;
	}
//	// 切换Fragment
//	@SuppressLint("NewApi")
//    public void setonClick(View v) {
//        FragmentManager fm = getFragmentManager();
//        // 开启Fragment事务
//        FragmentTransaction transaction = fm.beginTransaction();
//        switch (v.getId()) {
//        case R.id.btnweixin:
//            if (moviewlandFragment == null) {
//            	moviewlandFragment = new MoviewlandFragment();
//            }
//            // 使用当前Fragment的布局替代id_content的控件
//            transaction.replace(R.id.id_content, moviewlandFragment);
//            break;
//        case R.id.btnfriend:
//            if (businesslandFragment == null) {
//            	businesslandFragment = new BusinesslandFragment();
//            }
//            transaction.replace(R.id.id_content, businesslandFragment);
//            break;
//        }
//        // transaction.addToBackStack();
//        // 事务提交
//        transaction.commit();
//    }
		
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<businessJNI,requestCode="+requestCode+"resultCode="+resultCode,"log.txt");		
    	//4.注册接收器
		//comBroadreceiver = LocalBroadcastManager.getInstance(this);
		//comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		
		
    	if((requestCode==REQUEST_CODE)&&(resultCode==0x03))
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<取货码页面","log.txt");
			OrderDetail.addLog(BusLand.this);	
			switchMovie();
		}		
		else 
		{
			switchMovie();	
		}
	}
	@Override
	protected void onDestroy() {
		timer.shutdown();
		//=============
		//Server服务相关
		//=============
		//5.解除注册接收器
		localBroadreceiver.unregisterReceiver(receiver);
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}

	

	

	

	

	
}
