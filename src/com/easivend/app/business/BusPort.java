package com.easivend.app.business;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.HuodaoSet;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.app.maintain.HuodaoTest.COMReceiver;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.fragment.BusgoodsFragment;
import com.easivend.fragment.BusgoodsFragment.BusgoodsFragInteraction;
import com.easivend.fragment.BusgoodsclassFragment;
import com.easivend.fragment.BusgoodsclassFragment.BusgoodsclassFragInteraction;
import com.easivend.fragment.BusgoodsselectFragment;
import com.easivend.fragment.BusgoodsselectFragment.BusgoodsselectFragInteraction;
import com.easivend.fragment.BushuoFragment;
import com.easivend.fragment.BushuoFragment.BushuoFragInteraction;
import com.easivend.fragment.BusinesslandFragment;
import com.easivend.fragment.BusinessportFragment;
import com.easivend.fragment.BusinessportFragment.BusportFragInteraction;
import com.easivend.fragment.BuszhiamountFragment;
import com.easivend.fragment.BuszhiamountFragment.BuszhiamountFragInteraction;
import com.easivend.fragment.BuszhierFragment;
import com.easivend.fragment.BuszhierFragment.BuszhierFragInteraction;
import com.easivend.fragment.BuszhiweiFragment;
import com.easivend.fragment.BuszhiweiFragment.BuszhiweiFragInteraction;
import com.easivend.fragment.MoviewlandFragment;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.http.EVServerhttp;
import com.easivend.http.Weixinghttp;
import com.easivend.http.Zhifubaohttp;
import com.easivend.view.COMService;
import com.easivend.view.PassWord;
import com.example.evconsole.R;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class BusPort extends Activity implements 
//business页面接口
MovieFragInteraction,BusportFragInteraction,
//busgoodsclass页面接口
BusgoodsclassFragInteraction,
//busgoods页面接口
BusgoodsFragInteraction,
//Busgoodsselect页面接口
BusgoodsselectFragInteraction,
//Buszhiamount页面接口
BuszhiamountFragInteraction,
//Buszhier页面接口
BuszhierFragInteraction,
//Buszhiwei页面接口
BuszhiweiFragInteraction,
//Bushuo页面接口
BushuoFragInteraction
{
	private BusinessportFragment businessportFragment;
	private BusgoodsclassFragment busgoodsclassFragment;
	private BusgoodsFragment busgoodsFragment;
	private BusgoodsselectFragment busgoodsselectFragment;
	private BuszhiamountFragment buszhiamountFragment;
	private BuszhierFragment buszhierFragment;
	private BuszhiweiFragment buszhiweiFragment;
	private BushuoFragment bushuoFragment;
	//交易页面
    Intent intent=null;
    //final static int REQUEST_CODE=1; 
    final static int PWD_CODE=2; 
    public static final int BUSPORT=1;//首页面
    public static final int BUSGOODSCLASS=2;//商品类别页面
	public static final int BUSGOODS=3;//商品导购页面
	public static final int BUSGOODSSELECT=4;//商品详细页面
	public static final int BUSZHIAMOUNT=5;//现金支付页面
	public static final int BUSZHIER=6;//支付宝支付页面
	public static final int BUSZHIWEI=7;//微信支付页面
	public static final int BUSHUO=8;//出货页面
	private int gotoswitch=0;//当前跳转到哪个页面
	private int con=0;
	//进度对话框
	ProgressDialog dialog= null;
	private String zhifutype = "0";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	private String out_trade_no=null;
	//=================
	//==现金支付页面相关
	//=================
	private int queryLen = 0; 
    private int iszhienable=0;//1发送打开指令,0还没发送打开指令
    private boolean isempcoin=false;//false还未发送关纸币器指令，true因为缺币，已经发送关纸币器指令
    private int dispenser=0;//0无,1hopper,2mdb
	float billmoney=0,coinmoney=0,money=0;//投币金额
	float amount=0;//商品需要支付金额
	private int iszhiamount=0;//1成功投入钱,0没有成功投入钱
    private boolean ischuhuo=false;//true已经出货过了，可以上报日志
	float RealNote=0,RealCoin=0,RealAmount=0;//退币金额	
	//=================
	//==支付宝支付页面相关
	//=================
	//线程进行支付宝二维码操作
    private ExecutorService zhifubaothread=null;
    private Handler zhifubaomainhand=null,zhifubaochildhand=null;
    Zhifubaohttp zhifubaohttp=null;
    private int iszhier=0;//1成功生成了二维码,0没有成功生成二维码
    private boolean ercheck=false;//true正在二维码的线程操作中，请稍后。false没有二维码的线程操作
    //=================
  	//==微信支付页面相关
  	//=================
    //线程进行微信二维码操作
    private ExecutorService weixingthread=null;
    private Handler weixingmainhand=null,weixingchildhand=null;   
    Weixinghttp weixinghttp=null;
    private int iszhiwei=0;//1成功生成了二维码,0没有成功生成二维码
    //=================
	//==出货页面相关
	//=================
	private int status=0;//出货结果	
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    private final int SPLASH_DISPLAY_LENGHT = 5*60; //  5*60延迟5分钟	
    private int recLen = SPLASH_DISPLAY_LENGHT; 
    private boolean isbus=true;//true表示在广告页面，false在其他页面
    //=================
    //COM服务相关
    //=================
  	LocalBroadcastManager comBroadreceiver;
  	COMReceiver comreceiver;
    
    //=========================
    //activity与fragment回调相关
    //=========================
    /**
     * 用来与其他fragment交互的,设置静态变量
     */
    public static BusPortFragInteraction listterner;
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        if(activity instanceof BuszhiamountFragInteraction)
//        {
//            listterner = (BuszhiamountFragInteraction)activity;
//        }
//        else{
//            throw new IllegalArgumentException("activity must implements BuszhiamountFragInteraction");
//        }
//
//    }
    /**
     * 用来与其他fragment交互的,
     * 步骤一、定义了所有fragment必须实现的接口
     */
    public interface BusPortFragInteraction
    {
        /**
         * Activity 向Fragment传递指令，这个方法可以根据需求来定义
         * @param str
         */
    	//视频页面
        void BusportMovie();      //视频页面轮播
        //现金页面
        void BusportTsxx(String str);      //提示信息
        void BusportTbje(String str);      //投币金额
        //出货页面
        void BusportChjg(int sta);      //出货结果
        //非现金页面
        void BusportSend(String str);      //二维码
    }
        
    /**
     * 用来与其他fragment交互的,
     * 步骤四、当Fragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
  	public static void setCallBack(BusPortFragInteraction call){ 
  		listterner = call;
      } 
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.busport);		
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		//注册串口监听器
		EVprotocolAPI.setCallBack(new jniInterfaceImp());
		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	  //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<portthread="+Thread.currentThread().getId(),"log.txt");
	        	  if(isbus==false)
	        	  {
		        	  //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
		        	  recLen--; 
		        	  //回到首页
		        	  if(recLen == 0)
		              { 
		                  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recclose=BusinessportFragment","log.txt");
		                  //=================
			        	  //==现金支付页面相关
			        	  //=================
				          if(gotoswitch==BUSZHIAMOUNT)
				          {
				        	  BuszhiamountFinish();
				          }
				          //=================
			        	  //==支付宝支付页面相关
			        	  //=================
				          else if(gotoswitch==BUSZHIER)
				          {
				        	  timeoutBuszhierFinish();
				          }
				          //=================
			        	  //==微信支付页面相关
			        	  //=================
				          else if(gotoswitch==BUSZHIWEI)
				          {
				        	  timeoutBuszhiweiFinish();
				          }
				          else
				          {
				        	  viewSwitch(BUSPORT,null);	  
						  }				          
		                  	                   
		              }	
		        	//=================
	        		//==现金支付页面相关
	        		//=================
		        	if(gotoswitch==BUSZHIAMOUNT)
		        	{
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
		                    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
		                    }
	                    }
		        	}
		        		        	
		        	
		        	//=================
	        		//==支付宝支付页面相关
	        		//=================
		        	else if(gotoswitch==BUSZHIER)
		        	{
			        	//发送查询交易指令
	                    if(iszhier==1)
	                    {
		                    queryLen++;
		                    if(queryLen>=4)
		                    {
		                    	queryLen=0;
		                    	queryzhier();
		                    }
	                    }
	                    //发送订单交易指令
	                    else if(iszhier==0)
	                    {
		                    queryLen++;
		                    if(queryLen>=10)
		                    {
		                    	queryLen=0;
		                    	//发送订单
		                		sendzhier();
		                    }
	                    }
		        	}
		        	
		        	//=================
	        		//==微信支付页面相关
	        		//=================
		        	else if(gotoswitch==BUSZHIWEI)
		        	{
			        	//发送查询交易指令
	                    if(iszhiwei==1)
	                    {
		                    queryLen++;
		                    if(queryLen>=4)
		                    {
		                    	queryLen=0;
		                    	queryzhiwei();
		                    }
	                    }
	                    //发送订单交易指令
	                    else if(iszhiwei==0)
	                    {
		                    queryLen++;
		                    if(queryLen>=10)
		                    {
		                    	queryLen=0;
		                    	//发送订单
		                		sendzhiwei();
		                    }
	                    }
		        	}
	        	  }
	        } 
	    }, 1, 1, TimeUnit.SECONDS);       // timeTask 
		//初始化默认fragment
		initView();
		//***********************
		//线程进行支付宝二维码操作
		//***********************
		zhifubaomainhand=new Handler()
		{			
			@Override
			public void handleMessage(Message msg) {
				//barzhifubaotest.setVisibility(View.GONE);
				ercheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Zhifubaohttp.SETMAIN://子线程接收主线程消息
						listterner.BusportSend(msg.obj.toString());
						iszhier=1;
						break;
					case Zhifubaohttp.SETFAILNETCHILD://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:重试"+msg.obj.toString()+con);
						con++;
						break;		
					case Zhifubaohttp.SETPAYOUTMAIN://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:退款成功");
						dialog.dismiss();
						//清数据
						clearamount();						
						recLen=10;						
						break;
					case Zhifubaohttp.SETDELETEMAIN://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:撤销成功");
						clearamount();
				    	viewSwitch(BUSPORT, null);
						break;	
					case Zhifubaohttp.SETQUERYMAINSUCC://交易成功
						listterner.BusportTsxx("交易结果:交易成功");
//						//reamin_amount=String.valueOf(amount);
						tochuhuo();
						break;
					case Zhifubaohttp.SETQUERYMAIN://子线程接收主线程消息
					case Zhifubaohttp.SETFAILPROCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILBUSCHILD://子线程接收主线程消息	
					case Zhifubaohttp.SETFAILQUERYPROCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILQUERYBUSCHILD://子线程接收主线程消息	
					case Zhifubaohttp.SETFAILPAYOUTPROCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETFAILPAYOUTBUSCHILD://子线程接收主线程消息
					case Zhifubaohttp.SETFAILDELETEPROCHILD://子线程接收主线程消息		
					case Zhifubaohttp.SETFAILDELETEBUSCHILD://子线程接收主线程消息						
						listterner.BusportTsxx("交易结果:"+msg.obj.toString());
						break;	
				}				
			}
			
		};
		//启动用户自己定义的类
		zhifubaohttp=new Zhifubaohttp(zhifubaomainhand);
		zhifubaothread=Executors.newFixedThreadPool(3);
		zhifubaothread.execute(zhifubaohttp);
		
		
		//***********************
		//线程进行微信二维码操作
		//***********************
		weixingmainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				//barweixingtest.setVisibility(View.GONE);
				ercheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Weixinghttp.SETMAIN://子线程接收主线程消息
						listterner.BusportSend(msg.obj.toString());
						iszhiwei=1;
						break;
					case Weixinghttp.SETFAILNETCHILD://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:重试"+msg.obj.toString()+con);
						con++;						
						break;	
					case Weixinghttp.SETPAYOUTMAIN://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:退款成功");
						dialog.dismiss();
						//清数据
						clearamount();						
						recLen=10;	
						break;
					case Weixinghttp.SETDELETEMAIN://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:撤销成功");
						clearamount();
				    	viewSwitch(BUSPORT, null);
						break;	
					case Weixinghttp.SETQUERYMAINSUCC://子线程接收主线程消息		
						listterner.BusportTsxx("交易结果:交易成功");
						//reamin_amount=String.valueOf(amount);
						tochuhuo();
						break;
					case Weixinghttp.SETFAILPROCHILD://子线程接收主线程消息
					case Weixinghttp.SETFAILBUSCHILD://子线程接收主线程消息	
					case Weixinghttp.SETFAILQUERYPROCHILD://子线程接收主线程消息
					case Weixinghttp.SETFAILQUERYBUSCHILD://子线程接收主线程消息		
					case Weixinghttp.SETQUERYMAIN://子线程接收主线程消息	
					case Weixinghttp.SETFAILPAYOUTPROCHILD://子线程接收主线程消息		
					case Weixinghttp.SETFAILPAYOUTBUSCHILD://子线程接收主线程消息
					case Weixinghttp.SETFAILDELETEPROCHILD://子线程接收主线程消息		
					case Weixinghttp.SETFAILDELETEBUSCHILD://子线程接收主线程消息
						listterner.BusportTsxx("交易结果:"+msg.obj.toString());
						break;		
				}				
			}
			
		};
		//启动用户自己定义的类
		weixinghttp=new Weixinghttp(weixingmainhand);
		weixingthread=Executors.newFixedThreadPool(3);
		weixingthread.execute(weixinghttp);
				
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

        businessportFragment = new BusinessportFragment();
        transaction.replace(R.id.id_content, businessportFragment);
        transaction.commit();
    }
	
	//=======================
	//实现Business页面相关接口
	//=======================
	@Override
	public void switchBusiness() {
		// TODO Auto-generated method stub		
	}

	//步骤三、实现Business接口,打开密码框
	@Override
	public void finishBusiness() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=打开密码框","log.txt");
    	Intent intent = new Intent();
    	intent.setClass(BusPort.this, PassWord.class);// 使用AddInaccount窗口初始化Intent
        startActivityForResult(intent, PWD_CODE);
	}
	//步骤三、实现Business接口,转到商品购物页面
	//buslevel跳转到的页面
	@Override
	public void gotoBusiness(int buslevel, Map<String, String> str)
	{
		viewSwitch(buslevel, str);
	}
	
	//=======================
	//实现BusgoodsClass页面相关接口
	//=======================
	//步骤三、实现Busgoodsclass接口,转到商品导购页面
	@Override
	public void BusgoodsclassSwitch(Map<String, String> str) {
		// TODO Auto-generated method stub
		viewSwitch(BUSGOODS, str);
	}

	//步骤三、实现Busgoodsclass接口,转到首页面
	@Override
	public void BusgoodsclassFinish() {
		// TODO Auto-generated method stub
		viewSwitch(BUSPORT, null);
	}
	
	
	//=======================
	//实现Busgoods页面相关接口
	//=======================
	//步骤三、实现Busgoods接口,转到商品详细页面
	@Override
	public void BusgoodsSwitch(Map<String, String> str) {
		// TODO Auto-generated method stub
		viewSwitch(BUSGOODSSELECT, str);
	}
	//步骤三、实现Busgoodsclass接口,转到首页面
	@Override
	public void BusgoodsFinish() {
		// TODO Auto-generated method stub
		viewSwitch(BUSPORT, null);
	}
	
	//=======================
	//实现Busgoodsselect页面相关接口
	//=======================
	//步骤三、实现Busgoodsselect接口,转到商品详细页面
	@Override
	public void BusgoodsselectSwitch(int buslevel) {
		// TODO Auto-generated method stub		
		viewSwitch(buslevel, null);
	}
	//步骤三、实现Busgoodsselect接口,转到首页面
	@Override
	public void BusgoodsselectFinish() {
		// TODO Auto-generated method stub
		viewSwitch(BUSPORT, null);
	}
	
	//=======================
	//实现Buszhiamount页面相关接口
	//=======================
	//步骤三、实现Buszhiamount接口,转到首页面
	@Override
	public void BuszhiamountFinish() {
		// TODO Auto-generated method stub	
		if(iszhiamount==1)
  		{
  			dialog= ProgressDialog.show(BusPort.this,"正在退币中","请稍候...");
  			OrderDetail.setPayStatus(2);//支付失败
  			//退币
  	    	EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
  		} 
  		else 
  		{
  			clearamount();
  			//关闭纸币硬币器
  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);   
  	    	viewSwitch(BUSPORT, null);
		} 	    
	    
	}
	
	
    
    //=======================
  	//实现Buszhier页面相关接口
  	//=======================
    //步骤三、实现Buszhier接口,转到首页面
    @Override
	public void BuszhierFinish() {
		// TODO Auto-generated method stub
    	if(iszhier==1)
			deletezhier();
		else 
		{
	    	clearamount();
	    	viewSwitch(BUSPORT, null);
		}
	}
    //用于超时的结束界面
  	private void timeoutBuszhierFinish()
  	{
  		//如果需要撤销，而且线程可以操作，才作撤销操作，否则直接退出页面
  		if((iszhier==1)&&(ercheck==false))
  			deletezhier();
  		else 
  		{
	    	clearamount();
	    	viewSwitch(BUSPORT, null);
		}
  	}
    
    //发送订单
  	private void sendzhier()
  	{	
  		if(ercheckopt())
  		{
	      	// 将信息发送到子线程中
	      	zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			out_trade_no=ToolClass.out_trade_no(BusPort.this);// 创建InaccountDAO对象;
	  	        ev.put("out_trade_no", out_trade_no);
	  			ev.put("total_fee", String.valueOf(amount));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
    //查询交易
  	private void queryzhier()
  	{
  		if(ercheckopt())
  		{
	  		// 将信息发送到子线程中
	  		zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETQUERYCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301113215800");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
  	//撤销交易
  	private void deletezhier()
  	{
  		if(ercheckopt())
  		{
	  		// 将信息发送到子线程中
	  		zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETDELETECHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301092857698");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
    //退款
  	private void payoutzhier()
  	{
  		if(ercheckopt())
  		{
	  		// 将信息发送到子线程中
	  		zhifubaochildhand=zhifubaohttp.obtainHandler();
	  		Message childmsg=zhifubaochildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			ev.put("refund_amount", String.valueOf(amount));
	  			ev.put("out_request_no", ToolClass.out_trade_no(BusPort.this));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		zhifubaochildhand.sendMessage(childmsg);
  		}
  	}
    
    //=======================
  	//实现Buszhiwei页面相关接口
  	//=======================
    //步骤三、实现Buszhiwei接口,转到首页面
  	@Override
	public void BuszhiweiFinish() {
		// TODO Auto-generated method stub
  		if(iszhiwei==1)
			deletezhiwei();
		else 
		{
	    	clearamount();
	    	viewSwitch(BUSPORT, null);
		}
	}
    //用于超时的结束界面
  	private void timeoutBuszhiweiFinish()
  	{
  		//如果需要撤销，而且线程可以操作，才作撤销操作，否则直接退出页面
  		if((iszhiwei==1)&&(ercheck==false))
  			deletezhiwei();
  		else 
		{
	    	clearamount();
	    	viewSwitch(BUSPORT, null);
		}
  	}
    //发送订单
  	private void sendzhiwei()
  	{	
  		if(ercheckopt())
  		{
	      	// 将信息发送到子线程中
	      	weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			out_trade_no=ToolClass.out_trade_no(BusPort.this);
	  	        ev.put("out_trade_no", out_trade_no);
	  			ev.put("total_fee", String.valueOf(amount));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
  	//查询交易
  	private void queryzhiwei()
  	{
  		if(ercheckopt())
  		{
	  		// 将信息发送到子线程中
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETQUERYCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301113215800");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
  	//退款交易
  	private void payoutzhiwei()
  	{
  		if(ercheckopt())
  		{
	  		// 将信息发送到子线程中
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			ev.put("total_fee", String.valueOf(amount));
	  			ev.put("refund_fee", String.valueOf(amount));
	  			ev.put("out_refund_no", ToolClass.out_trade_no(BusPort.this));
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
  	//撤销交易
  	private void deletezhiwei()
  	{
  		if(ercheckopt())
  		{
	  		// 将信息发送到子线程中
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETDELETECHILD;
	  		JSONObject ev=null;
	  		try {
	  			ev=new JSONObject();
	  			ev.put("out_trade_no", out_trade_no);		
	  			//ev.put("out_trade_no", "000120150301092857698");	
	  			Log.i("EV_JNI","Send0.1="+ev.toString());
	  		} catch (JSONException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		childmsg.obj=ev;
	  		weixingchildhand.sendMessage(childmsg);
  		}
  	}
    
    //=======================
  	//实现Bushuo页面相关接口
  	//=======================
    //步骤三、实现Bushuo接口,出货
    @Override
	public void BushuoChuhuoOpt(int cabinetvar, int huodaoNo,int cabinetTypevar) {
		// TODO Auto-generated method stub
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<busport商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt");
		dialog= ProgressDialog.show(BusPort.this,"正在出货中","请稍候...");
		ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
		    	+" column="+huodaoNo		    	
		    	,"log.txt");
		Intent intent = new Intent();
		//4.发送指令广播给COMService
		intent.putExtra("EVWhat", COMService.EV_CHUHUOCHILD);	
		intent.putExtra("cabinet", cabinetvar);	
		intent.putExtra("column", huodaoNo);	
		intent.setAction("android.intent.action.comsend");//action与接收器相同
		comBroadreceiver.sendBroadcast(intent);
    }
    
    //步骤三、实现Bushuo接口,结束出货页面
    @Override
	public void BushuoFinish(int status) {
    	// TODO Auto-generated method stub
    	switch(OrderDetail.getPayType())
    	{
    		//现金页面
    		case 0:
    			//viewSwitch(BUSZHIAMOUNT, null);
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
  				//没剩下余额了，不退币
  				if(money==0)
  				{  					
			    	OrderDetail.addLog(BusPort.this);
			    	clearamount();
		  			//关闭纸币硬币器
		  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);   
		  	    	recLen=10;
  				}
  				//退币
  				else 
  				{
  					dialog= ProgressDialog.show(BusPort.this,"正在退币中,金额"+money,"请稍候...");
	    			//退币
	    	    	EVprotocolAPI.EV_mdbPayback(ToolClass.getCom_id(),1,1);
				} 
    			break;
    		//支付宝页面	
    		case 3:
    			//出货成功,结束交易
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ali无退款","log.txt");
					OrderDetail.addLog(BusPort.this);					
					clearamount();
					recLen=10;
				}
				//出货失败,退钱
				else
				{	
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ali退款amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusPort.this,"正在退款中","请稍候...");
					payoutzhier();//退款操作
					OrderDetail.setRealStatus(1);//记录退币成功
					OrderDetail.setRealCard(amount);//记录退币金额
					OrderDetail.addLog(BusPort.this);					
				}
    			break;
    		//微信页面	
    		case 4:
    			//出货成功,结束交易
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<wei无退款","log.txt");
					OrderDetail.addLog(BusPort.this);					
					clearamount();
					recLen=10;
				}
				//出货失败,退钱
				else
				{	
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<wei退款amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusPort.this,"正在退款中","请稍候...");
					payoutzhiwei();//退款操作
					OrderDetail.setRealStatus(1);//记录退币成功
					OrderDetail.setRealCard(amount);//记录退币金额
					OrderDetail.addLog(BusPort.this);					
				}
    			break;	
    	}
    	
	}
    
    
    //=======================
  	//实现相关基础函数接口
  	//=======================
    //出货界面
    private void tochuhuo()
    {        
    	ischuhuo=true;
//    	Intent intent = null;// 创建Intent对象                
//    	intent = new Intent(BusZhiAmount.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
////    	intent.putExtra("out_trade_no", out_trade_no);
////    	intent.putExtra("proID", proID);
////    	intent.putExtra("productID", productID);
////    	intent.putExtra("proType", proType);
////    	intent.putExtra("cabID", cabID);
////    	intent.putExtra("huoID", huoID);
////    	intent.putExtra("prosales", prosales);
////    	intent.putExtra("count", count);
////    	intent.putExtra("reamin_amount", reamin_amount);
////    	intent.putExtra("zhifutype", zhifutype);    	
//    	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
    	OrderDetail.setOrdereID(out_trade_no);
    	OrderDetail.setPayType(Integer.parseInt(zhifutype));
    	if(gotoswitch==BUSZHIAMOUNT)
    	{    		
    	}
    	else if(gotoswitch==BUSZHIER)
    	{
        	OrderDetail.setSmallCard(amount);
    	}
    	else if(gotoswitch==BUSZHIWEI)
    	{
    		OrderDetail.setSmallCard(amount);
    	}
    	viewSwitch(BUSHUO, null);
    }
    //清参数
  	public void clearamount()
  	{
  		//现金页面
  	    con = 0;
  	    queryLen = 0; 
  	    iszhienable=0;//1发送打开指令,0还没发送打开指令
  	    isempcoin=false;//false还未发送关纸币器指令，true因为缺币，已经发送关纸币器指令
  	    dispenser=0;//0无,1hopper,2mdb
  		billmoney=0;coinmoney=0;money=0;//投币金额
  		amount=0;//商品需要支付金额
  		iszhiamount=0;//1成功投入钱,0没有成功投入钱
  		RealNote=0;
  		RealCoin=0;
  		RealAmount=0;//退币金额
  		ischuhuo=false;//true已经出货过了，可以上报日志
  		//支付宝页面
  		iszhier=0;//1成功生成了二维码,0没有成功生成二维码
  	    //微信页面
  		iszhiwei=0;//1成功生成了二维码,0没有成功生成二维码
  		ercheck=false;//true正在二维码的线程操作中，请稍后。false没有二维码的线程操作
  	}
  	
  	//判断是否处在二维码的线程操作中,true表示可以操作了,false不能操作
  	private boolean ercheckopt()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		if(ercheck==false)
  		{
  			ercheck=true;
  			return true;
  		}
  		else
  		{
  			return false;
		}
  	}
	
	//全局用于切换view的函数
	public void viewSwitch(int buslevel, Map<String, String> str)
	{
		recLen=SPLASH_DISPLAY_LENGHT;
		gotoswitch=buslevel;
		Bundle data = new Bundle();
		FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
		// TODO Auto-generated method stub
		switch(buslevel)
		{
			case BUSPORT://首页面
				isbus=true;
				if (businessportFragment == null) {
					businessportFragment = new BusinessportFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, businessportFragment);
				break;
			case BUSGOODSCLASS://商品类别
				isbus=false;
				if (busgoodsclassFragment == null) {
					busgoodsclassFragment = new BusgoodsclassFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, busgoodsclassFragment);
				break;
			case BUSGOODS:
				isbus=false;
//				intent = new Intent(BusPort.this, Busgoods.class);// 使用Accountflag窗口初始化Intent
//            	intent.putExtra("proclassID", "");
//            	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
				if (busgoodsFragment == null) {
					busgoodsFragment = new BusgoodsFragment();
	            }
				//步骤五、传递这个数据给friendfragment
				//传递数据
		        data.clear();
		        data.putString("proclassID", str.get("proclassID"));
		        busgoodsFragment.setArguments(data);
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, busgoodsFragment);
				break;
			case BUSGOODSSELECT:
				isbus=false;
//				intent = new Intent(BusPort.this, BusgoodsSelect.class);// 使用Accountflag窗口初始化Intent
//	        	intent.putExtra("proID", str.get("proID"));
//	        	intent.putExtra("productID", str.get("productID"));
//	        	intent.putExtra("proImage", str.get("proImage"));
//	        	intent.putExtra("prosales", str.get("prosales"));
//	        	intent.putExtra("procount", str.get("procount"));
//	        	intent.putExtra("proType", str.get("proType"));//1代表通过商品ID出货,2代表通过货道出货
//	        	intent.putExtra("cabID", str.get("cabID"));//出货柜号,proType=1时无效
//	        	intent.putExtra("huoID", str.get("huoID"));//出货货道号,proType=1时无效


//	        	OrderDetail.setProID(proID);
//            	OrderDetail.setProductID(productID);
//            	OrderDetail.setProType("2");
//            	OrderDetail.setCabID(cabID);
//            	OrderDetail.setColumnID(huoID);
//            	OrderDetail.setShouldPay(Float.parseFloat(prosales));
//            	OrderDetail.setShouldNo(1);
	        	
//	        	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
				if (busgoodsselectFragment == null) {
					busgoodsselectFragment = new BusgoodsselectFragment();
	            }
				//步骤五、传递这个数据给friendfragment
				//传递数据
				data.clear();
		        data.putString("proID", str.get("proID"));
		        data.putString("productID", str.get("productID"));
		        data.putString("proImage", str.get("proImage"));
		        data.putString("prosales", str.get("prosales"));
		        data.putString("procount", str.get("procount"));
		        data.putString("proType", str.get("proType"));//1代表通过商品ID出货,2代表通过货道出货
		        data.putString("cabID", str.get("cabID"));//出货柜号,proType=1时无效
		        data.putString("huoID", str.get("huoID"));//出货货道号,proType=1时无效
	        	busgoodsselectFragment.setArguments(data);
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, busgoodsselectFragment);
				break;
			case BUSZHIAMOUNT://现金支付
				isbus=false;
				EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);  
				amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo(); 
				zhifutype="0";
				out_trade_no=ToolClass.out_trade_no(BusPort.this);// 创建InaccountDAO对象;
				//切换页面
				if (buszhiamountFragment == null) {
					buszhiamountFragment = new BuszhiamountFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, buszhiamountFragment);
				break;	
			case BUSZHIER://支付宝支付
				isbus=false;
				zhifutype="3";
				amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
				if (buszhierFragment == null) {
					buszhierFragment = new BuszhierFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, buszhierFragment);
	            //发送订单
        		sendzhier();
				break;
			case BUSZHIWEI://微信支付
				isbus=false;
				zhifutype="4";
				amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
				if (buszhiweiFragment == null) {
					buszhiweiFragment = new BuszhiweiFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, buszhiweiFragment);
	            //发送订单
        		sendzhiwei();
				break;	
			case BUSHUO://出货页面	
				isbus=false;
				if (bushuoFragment == null) {
					bushuoFragment = new BushuoFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, bushuoFragment);
				break;
		}
		// transaction.addToBackStack();
        // 事务提交
        transaction.commit();
	}
		
	
	//创建一个专门处理单击接口的子类
	private class jniInterfaceImp implements JNIInterface
	{
		@Override
		public void jniCallback(Map<String, Object> allSet) {
			// TODO Auto-generated method stub
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<business监听到","log.txt");	
			Map<String, Object> Set= allSet;
			int jnirst=(Integer) Set.get("EV_TYPE");
			//txtcom.setText(String.valueOf(jnirst));
			switch (jnirst)
			{
				/**
			     * 用来与其他fragment交互的,
			     * 步骤二、activity向fragment发送回调信息
			     * @param activity
			     */
				case EVprotocolAPI.EV_MDB_ENABLE://接收子线程投币金额消息	
					//打开
					if((Integer)Set.get("opt")==1)
					{
						//打开失败,等待重新打开
						if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==0) )
						{
							listterner.BusportTsxx("提示信息：重试"+con);
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
					}
					break;
				case EVprotocolAPI.EV_MDB_B_INFO:	
					break;
				case EVprotocolAPI.EV_MDB_C_INFO:
					dispenser=(Integer)Set.get("acceptor");
					EVprotocolAPI.EV_mdbHeart(ToolClass.getCom_id());
					iszhienable=1;	
					break;	
				//现金设备状态查询
				case EVprotocolAPI.EV_MDB_HEART://心跳查询
					//纸币器页面
					if(iszhienable==1)
					{
						String bill_enable="";
						String coin_enable="";
						String hopperString="";
						int bill_err=ToolClass.getvmcStatus(Set,1);
						int coin_err=ToolClass.getvmcStatus(Set,2);
						int hopper1=ToolClass.getvmcStatus(Set,3);
						if(bill_err>0)
							bill_enable="[纸币器]无法使用";
						if(coin_err>0)
							coin_enable="[硬币器]无法使用";					
						if(hopper1>0)
							hopperString="[找零器]:"+ToolClass.gethopperstats(hopper1);
						listterner.BusportTsxx("提示信息："+bill_enable+coin_enable+hopperString);
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
						  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);
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
						  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);
						  			isempcoin=true;
						  		}
					  		}
					  	}
					  	
					  	if(money>0)
					  	{
					  		iszhiamount=1;
					  		recLen = 180;
					  		listterner.BusportTbje(String.valueOf(money));
					  		OrderDetail.setSmallNote(billmoney);
					  		OrderDetail.setSmallConi(coinmoney);
					  		OrderDetail.setSmallAmount(money);
					  		if(money>=amount)
					  		{
					  			tochuhuo();
					  		}
					  	}
					}
					//首页或者其他页面
					else
					{
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现金设备状态:","log.txt");	
						int bill_err=ToolClass.getvmcStatus(Set,1);
						int coin_err=ToolClass.getvmcStatus(Set,2);
						ToolClass.setBill_err(bill_err);
						ToolClass.setCoin_err(coin_err);
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
			    		OrderDetail.addLog(BusPort.this);
			    	}
			    	else
			    	{
			    		OrderDetail.cleardata();
					}
			    	dialog.dismiss();
					//清数据
			    	clearamount();
		  			//关闭纸币硬币器
		  	    	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0); 
		  	    	if(gotoswitch==BUSZHIAMOUNT)
		  	    	{
		  	    		viewSwitch(BUSPORT, null);	
		  	    	}
		  	    	else
		  	    	{
		  	    		recLen=10;
		  	    	}
					break;
				case EVprotocolAPI.EV_BENTO_OPEN://格子柜出货					
				case EVprotocolAPI.EV_COLUMN_OPEN://主柜出货
					status=(Integer)allSet.get("result");//出货结果
					dialog.dismiss();
					listterner.BusportChjg(status);
//					device=allSet.get("device");//出货柜号
//					status=allSet.get("status");//出货结果
//					hdid=allSet.get("hdid");//货道id
//					hdtype=allSet.get("type");//出货类型
//					cost=ToolClass.MoneyRec(allSet.get("cost"));//扣钱
//					totalvalue=ToolClass.MoneyRec(allSet.get("totalvalue"));//剩余金额
//					huodao=allSet.get("huodao");//剩余存货数量
//					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
//							+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");	
//					if(status==0)
//					{
//						data[tempx][0]=String.valueOf(R.drawable.yes);
//						data[tempx][1]=proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品";
//						//扣除存货余量
//						chuhuoupdate(cabinetvar,huodaoNo);
//						chuhuoLog(0);//记录日志
//					}
//					else
//					{
//						data[tempx][0]=String.valueOf(R.drawable.no);
//						data[tempx][1]=proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，未扣钱";
//						//扣除存货余量
//						chuhuoupdate(cabinetvar,huodaoNo);
//						chuhuoLog(1);//记录日志
//					}
//					updateListview();
//					tempx++;
//					huorst=0;
//					while((huorst!=1)&&(tempx<count))
//			 	    {
//			 	    	huorst=chuhuoopt(tempx);
//			 	    	if(huorst==2)
//						{
//							data[tempx][0]=String.valueOf(R.drawable.yes);
//							data[tempx][1]=proID+"["+prosales+"]"+"->出货完成，请到"+cabinetvar+"柜"+huodaoNo+"货道取商品";
//							updateListview();
//							tempx++;
//							//扣除存货余量
//							chuhuoupdate(cabinetvar,huodaoNo);
//							chuhuoLog(0);//记录日志
//						}
//						else if(huorst==0)
//						{
//							data[tempx][0]=String.valueOf(R.drawable.no);
//							data[tempx][1]=proID+"["+prosales+"]"+"->"+cabinetvar+"柜"+huodaoNo+"货道出货失败，未扣钱";
//							updateListview();
//							tempx++;
//							//扣除存货余量
//							chuhuoupdate(cabinetvar,huodaoNo);
//							chuhuoLog(1);//记录日志
//						}
//			 	    }
//					if(tempx>=count)
//			 	    {
//						ivbushuoquhuo.setVisibility(View.VISIBLE);
//			 	    	new Handler().postDelayed(new Runnable() 
//						{
//	                        @Override
//	                        public void run() 
//	                        {
//	                        	//出货完成,把非现金模块去掉
//	                        	if(status==0)
//	                        	{
//	                        		if(BusZhier.BusZhierAct!=null)
//	                        			BusZhier.BusZhierAct.finish(); 
//	                        		if(BusZhiwei.BusZhiweiAct!=null)
//	                        			BusZhiwei.BusZhiweiAct.finish(); 
//	                        		OrderDetail.addLog(BusHuo.this);
//	                        	}
//	                        	//出货失败，退到非现金模块进行退币操作
//	                        	else
//	                        	{
//	                        		if(BusZhier.BusZhierAct!=null)
//	                        		{
//	                        			//退出时，返回intent
//	                    	            Intent intent=new Intent();
//	                    	            setResult(BusZhier.RESULT_CANCELED,intent);
//	                        		}
//	                        		if(BusZhiwei.BusZhiweiAct!=null)
//	                        		{
//	                        			//退出时，返回intent
//	                    	            Intent intent=new Intent();
//	                    	            setResult(BusZhiwei.RESULT_CANCELED,intent);
//	                        		}
//								}		                        	
//	                            finish();
//	                        }
//
//						}, SPLASH_DISPLAY_LENGHT);
//			 	    }
					break;		
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<businessJNI","log.txt");
		//注册串口监听器
		EVprotocolAPI.setCallBack(new jniInterfaceImp());	
		if(requestCode==PWD_CODE)
		{
			if(resultCode==PassWord.RESULT_OK)
			{
				Bundle bundle=data.getExtras();
				String pwd = bundle.getString("pwd");
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<维护密码pwd="+pwd,"log.txt");
				boolean istrue=ToolClass.getpwdStatus(BusPort.this,pwd);
				if(istrue)
		    	{
		    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<确定退出","log.txt");
		    		finish();
		    	}
		    	else
		    	{
		    		listterner.BusportMovie();
				}
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
			case COMService.EV_OPTMAIN: 
				SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set2=serializableMap2.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道操作="+Set2,"com.txt");
				//是出货操作
				if(gotoswitch==BUSHUO)
				{
					status=Set2.get("result");//出货结果
					dialog.dismiss();
					listterner.BusportChjg(status);
				}
				break;
			}			
		}

	}
	@Override
	protected void onDestroy() {
		timer.shutdown(); 
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
