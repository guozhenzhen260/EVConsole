package com.easivend.app.business;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.CahslessTest;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.app.maintain.MaintainActivity.EVServerReceiver;
import com.easivend.common.OrderDetail;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_productDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.fragment.BusgoodsFragment;
import com.easivend.fragment.BusgoodsFragment.BusgoodsFragInteraction;
import com.easivend.fragment.BusgoodsclassFragment;
import com.easivend.fragment.BusgoodsclassFragment.BusgoodsclassFragInteraction;
import com.easivend.fragment.BusgoodsselectFragment;
import com.easivend.fragment.BusgoodsselectFragment.BusgoodsselectFragInteraction;
import com.easivend.fragment.BushuoFragment;
import com.easivend.fragment.BushuoFragment.BushuoFragInteraction;
import com.easivend.fragment.BusinessportFragment;
import com.easivend.fragment.BusinessportFragment.BusportFragInteraction;
import com.easivend.fragment.BuszhiamountFragment;
import com.easivend.fragment.BuszhiamountFragment.BuszhiamountFragInteraction;
import com.easivend.fragment.BuszhierFragment;
import com.easivend.fragment.BuszhierFragment.BuszhierFragInteraction;
import com.easivend.fragment.BuszhiposFragment;
import com.easivend.fragment.BuszhiposFragment.BuszhiposFragInteraction;
import com.easivend.fragment.BuszhiweiFragment;
import com.easivend.fragment.BuszhiweiFragment.BuszhiweiFragInteraction;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.http.EVServerhttp;
import com.easivend.http.Weixinghttp;
import com.easivend.http.Zhifubaohttp;
import com.easivend.model.Tb_vmc_product;
import com.easivend.view.COMService;
import com.easivend.view.EVServerService;
import com.easivend.view.PassWord;
import com.example.evconsole.R;
import com.landfone.common.utils.IUserCallback;
import com.landfoneapi.mispos.Display;
import com.landfoneapi.mispos.DisplayType;
import com.landfoneapi.mispos.ErrCode;
import com.landfoneapi.mispos.LfMISPOSApi;
import com.landfoneapi.protocol.pkg.REPLY;
import com.landfoneapi.protocol.pkg._04_GetRecordReply;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
//Buszhipos页面接口
BuszhiposFragInteraction,
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
	private BuszhiposFragment buszhiposFragment;
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
	public static final int BUSZHIPOS=9;//POS支付页面
	public static final int BUSHUO=8;//出货页面
	private int gotoswitch=0;//当前跳转到哪个页面
	private int con=0;
	//进度对话框
	ProgressDialog dialog= null;
	private String zhifutype = "0";//支付方式0=现金1=pos 3=二维码4=微支付 -1=取货码,5自提密码
	private String out_trade_no=null;
	//Server服务相关
	LocalBroadcastManager localBroadreceiver;
	EVServerReceiver receiver;
	//=================
	//==现金支付页面相关
	//=================
	private int queryLen = 0; 
	private int billdev=1;//是否需要打开纸币器,1需要
    private int iszhienable=0;//1发送打开指令,0还没发送打开指令，2本次投币已经结束
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
    private int iszhier=0;//1成功生成了二维码,0没有成功生成二维码，2本次交易已经结束
    private boolean ercheck=false;//true正在二维码的线程操作中，请稍后。false没有二维码的线程操作
    private int ispayoutopt=0;//1正在进行退币操作,0未进行退币操作
    //=================
  	//==微信支付页面相关
  	//=================
    //线程进行微信二维码操作
    private ExecutorService weixingthread=null;
    private Handler weixingmainhand=null,weixingchildhand=null;   
    Weixinghttp weixinghttp=null;
    private int iszhiwei=0;//1成功生成了二维码,0没有成功生成二维码，2本次投币已经结束
    //=================
  	//==pos支付页面相关
  	//=================
    private LfMISPOSApi mMyApi = new LfMISPOSApi();
    private Handler posmainhand=null;
    private int iszhipos=0;//1成功发送了扣款请求,0没有发送成功扣款请求，2刷卡扣款已经完成并且金额足够
    String SpecInfoField=null;
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
    public static BusPortMovieFragInteraction listternermovie;
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
        //现金页面
        void BusportTsxx(String str);      //提示信息
        void BusportTbje(String str);      //投币金额
        //出货页面
        void BusportChjg(int sta);      //出货结果
        //非现金页面
        void BusportSend(String str);      //二维码
    }
    public interface BusPortMovieFragInteraction
    {
    	//显示交易提示信息
        void BusportMovie(int infotype);      //显示交易提示信息
        //刷新广告页面
        void BusportAds();      //刷新广告列表
    }
        
    /**
     * 用来与其他fragment交互的,
     * 步骤四、当Fragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
  	public static void setCallBack(BusPortFragInteraction call){ 
  		listterner = call;
      }
  	public static void setMovieCallBack(BusPortMovieFragInteraction call){ 
  		listternermovie = call;
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
		//=============
		//Server服务相关
		//=============
		//4.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
		//=============
		//COM服务相关
		//=============
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	  //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<portthread="+Thread.currentThread().getId(),"log.txt");
	        	  if(isbus==false)
	        	  {
		        	  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
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
		        	
		        	//=================
	        		//==pos支付页面相关
	        		//=================
		        	else if(gotoswitch==BUSZHIPOS)
		        	{
			        	//发送查询交易指令
	                    if(iszhipos==1)
	                    {		                    
	                    }
	                    //发送订单交易指令
	                    else if(iszhipos==0)
	                    {		                    
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
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(3);//记录退币失败
							OrderDetail.setRealCard(0);//记录退币金额
							OrderDetail.setDebtAmount(amount);//欠款金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款失败");
							dialog.dismiss();
							//清数据
							clearamount();						
							recLen=10;
						}
						break;		
					case Zhifubaohttp.SETPAYOUTMAIN://子线程接收主线程消息
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(1);//记录退币成功
							OrderDetail.setRealCard(amount);//记录退币金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款成功");
							dialog.dismiss();
							//清数据
							clearamount();						
							recLen=10;
						}
						break;
					case Zhifubaohttp.SETDELETEMAIN://子线程接收主线程消息
//						listterner.BusportTsxx("交易结果:撤销成功");
//						clearamount();
//				    	viewSwitch(BUSPORT, null);
						break;	
					case Zhifubaohttp.SETQUERYMAINSUCC://交易成功
						listterner.BusportTsxx("交易结果:交易成功");
						iszhier=2;
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
						//listterner.BusportTsxx("交易结果:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(3);//记录退币失败
							OrderDetail.setRealCard(0);//记录退币金额
							OrderDetail.setDebtAmount(amount);//欠款金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款失败");
							dialog.dismiss();
							//清数据
							clearamount();						
							recLen=10;
						}
						break;	
				}				
			}
			
		};
		//创建用户自己定义的类
		zhifubaohttp=new Zhifubaohttp(zhifubaomainhand);
		zhifubaothread=Executors.newCachedThreadPool();
		
		
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
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(3);//记录退币失败
							OrderDetail.setRealCard(0);//记录退币金额
							OrderDetail.setDebtAmount(amount);//欠款金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款失败");
							dialog.dismiss();
							//清数据
							clearamount();						
							recLen=10;
						}
						break;	
					case Weixinghttp.SETPAYOUTMAIN://子线程接收主线程消息
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(1);//记录退币成功
							OrderDetail.setRealCard(amount);//记录退币金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款成功");
							dialog.dismiss();
							//清数据
							clearamount();						
							recLen=10;
						}
						break;
					case Weixinghttp.SETDELETEMAIN://子线程接收主线程消息
//						listterner.BusportTsxx("交易结果:撤销成功");
//						clearamount();
//				    	viewSwitch(BUSPORT, null);
						break;	
					case Weixinghttp.SETQUERYMAINSUCC://子线程接收主线程消息		
						listterner.BusportTsxx("交易结果:交易成功");
						//reamin_amount=String.valueOf(amount);
						iszhiwei=2;
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
						//listterner.BusportTsxx("交易结果:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(3);//记录退币失败
							OrderDetail.setRealCard(0);//记录退币金额
							OrderDetail.setDebtAmount(amount);//欠款金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款失败");
							dialog.dismiss();
							//清数据
							clearamount();						
							recLen=10;
						}
						break;		
				}				
			}
			
		};
		//启动用户自己定义的类
		weixinghttp=new Weixinghttp(weixingmainhand);
		weixingthread=Executors.newCachedThreadPool();
		
		
		//***********************
		//进行pos操作
		//***********************
		posmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case CahslessTest.OPENSUCCESS:
						break;
					case CahslessTest.OPENFAIL:	
						break;
					case CahslessTest.CLOSESUCCESS:
						break;
					case CahslessTest.CLOSEFAIL:	
						break;
					case CahslessTest.COSTSUCCESS:
						listterner.BusportTsxx("提示信息：付款完成");
						iszhipos=2;
						//延时
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	//读卡器获取交易信息
								mMyApi.pos_getrecord("000000000000000", "00000000","000000", mIUserCallback);
							}

						}, 300);
						break;
					case CahslessTest.COSTFAIL:	
						listterner.BusportTsxx("提示信息：扣款失败");
						iszhipos=0;
						break;
					case CahslessTest.QUERYSUCCESS:
					case CahslessTest.QUERYFAIL:	
						SpecInfoField=msg.obj.toString();
						listterner.BusportTsxx("单据信息："+SpecInfoField);
						tochuhuo();	
						break;
					case CahslessTest.DELETESUCCESS:
					case CahslessTest.DELETEFAIL:	
						//延时
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=BUSPORT","log.txt");
								ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
						    	mMyApi.pos_release();
								clearamount();
						    	viewSwitch(BUSPORT, null);
							}

						}, 300);						
						break;						
					case CahslessTest.PAYOUTSUCCESS:
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(1);//记录退币成功
							OrderDetail.setRealCard(amount);//记录退币金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款成功");
							//延时
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
					            	dialog.dismiss();
									//清数据
									clearamount();						
									recLen=10;
								}

							}, 300);							
						}
						break;
					case CahslessTest.PAYOUTFAIL:	
						if(ispayoutopt==1)
						{
							//记录日志退币完成
							OrderDetail.setRealStatus(3);//记录退币失败
							OrderDetail.setRealCard(0);//记录退币金额
							OrderDetail.setDebtAmount(amount);//欠款金额
							OrderDetail.addLog(BusPort.this);
							ispayoutopt=0;
							//结束交易页面
							listterner.BusportTsxx("交易结果:退款失败");
							//延时
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
					            	dialog.dismiss();
									//清数据
									clearamount();						
									recLen=10;
								}

							}, 300);							
						}
						break;		
				}
			}
		};	
				
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
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=退出交易页面","log.txt");
//    	Intent intent = new Intent();
//    	intent.setClass(BusPort.this, PassWord.class);// 使用AddInaccount窗口初始化Intent
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
	//步骤三、实现Business接口,转到商品购物页面
	//buslevel跳转到的页面
	@Override
	public void gotoBusiness(int buslevel, Map<String, String> str)
	{
		if(ToolClass.checkCLIENT_STATUS_SERVICE())
		{
			viewSwitch(buslevel, str);
		}
	}
	//步骤三、实现Business接口,传递取货码
	@Override
	public void quhuoBusiness(String PICKUP_CODE)
	{
		if(ToolClass.checkCLIENT_STATUS_SERVICE())
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<port取货码="+PICKUP_CODE,"log.txt");
			Intent intent2=new Intent(); 
			intent2.putExtra("EVWhat", EVServerhttp.SETPICKUPCHILD);
			intent2.putExtra("PICKUP_CODE", PICKUP_CODE);
			intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
			localBroadreceiver.sendBroadcast(intent2);
		}
	}
	//步骤三、实现Business接口,传递提示信息
	@Override
	public void tishiInfo(int infotype)
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<port提示类型="+infotype,"log.txt");
		listternermovie.BusportMovie(infotype);
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
				out_trade_no=bundle.getString("out_trade_no");
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=取货码成功PRODUCT_NO="+PRODUCT_NO+"out_trade_no="+out_trade_no,"log.txt");					
				// 创建InaccountDAO对象，用于从数据库中提取数据到Tb_vmc_product表中
		 	    vmc_productDAO productdao = new vmc_productDAO(context);
		 	    Tb_vmc_product tb_vmc_product=productdao.find(PRODUCT_NO);
		 	    //保存到报表表里面
		 	    //订单总信息
		 	    OrderDetail.setProID(tb_vmc_product.getProductID()+"-"+tb_vmc_product.getProductName());		 	    
		 	    OrderDetail.setProType("1");
		 	    //订单支付表 
		 	    zhifutype="-1";
		 	    OrderDetail.setShouldPay(tb_vmc_product.getSalesPrice());
		 	    OrderDetail.setShouldNo(1);
		 	    OrderDetail.setCabID("");
		 		OrderDetail.setColumnID("");
		 	    //订单详细信息表   
		 	    OrderDetail.setProductID(PRODUCT_NO);
		 	    tochuhuo();
				break;
			case EVServerhttp.SETERRFAILPICKUPMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=取货码失败","log.txt");
				// 弹出信息提示
				ToolClass.failToast("抱歉，取货码无效,请联系管理员！");
	    		break;	
			case EVServerhttp.SETADVRESETMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","BusPort=刷新广告","log.txt");
				listternermovie.BusportAds();
				break;
			}			
		}

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
		//如果本次投币已经结束，可以购买，则不进行退币操作
		if(iszhienable==2)
		{
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusPort 退币按钮无效","com.txt");
		}
		else if(iszhiamount==1)
  		{
  			dialog= ProgressDialog.show(BusPort.this,"正在退币中","请稍候...");
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
  			//关闭纸币硬币器
  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0);   
  			BillEnable(0);
  			clearamount();
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
    	//如果本次扫码已经结束，可以购买，则不进行退款操作
    	if(iszhier==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhier退币按钮无效","log.txt");
    	}
    	else if(iszhier==1)
			deletezhier();
		else 
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=BUSPORT","log.txt");
	    	clearamount();
	    	viewSwitch(BUSPORT, null);
		}
	}
    //用于超时的结束界面
  	private void timeoutBuszhierFinish()
  	{
  		BuszhierFinish();
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
  		//if(ercheckopt())
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=撤销交易","log.txt");
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
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
  		clearamount();
    	viewSwitch(BUSPORT, null);
  	}
    //退款
  	private void payoutzhier()
  	{
  		//if(ercheckopt())
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
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
  		//如果本次扫码已经结束，可以购买，则不进行退款操作
    	if(iszhiwei==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhiwei退币按钮无效","log.txt");
    	}
    	else if(iszhiwei==1)
			deletezhiwei();
		else 
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=BUSPORT","log.txt");
	    	clearamount();
	    	viewSwitch(BUSPORT, null);
		}
	}
    //用于超时的结束界面
  	private void timeoutBuszhiweiFinish()
  	{
  		BuszhiweiFinish();
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
	  		childmsg.what=Weixinghttp.SETQUERYCHILD;
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
  		//if(ercheckopt())
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
	  		// 将信息发送到子线程中
	  		weixingchildhand=weixinghttp.obtainHandler();
	  		Message childmsg=weixingchildhand.obtainMessage();
	  		childmsg.what=Weixinghttp.SETPAYOUTCHILD;
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
  		//if(ercheckopt())
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=撤销交易","log.txt");
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
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
  		clearamount();
    	viewSwitch(BUSPORT, null);
  	}
  	
    //=======================
  	//实现Buszhipos页面相关接口
  	//=======================
    //步骤三、实现Buszhipos接口,转到首页面
  	@Override
	public void BuszhiposFinish() {
		// TODO Auto-generated method stub
  		//如果本次扫码已经结束，可以购买，则不进行退款操作
    	if(iszhipos==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhipos退币按钮无效","log.txt");
    	}
    	else if(iszhipos==1)
			deletezhipos();
		else 
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=BUSPORT","log.txt");
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
	    	mMyApi.pos_release();
			clearamount();
	    	viewSwitch(BUSPORT, null);
		}
	}
    //用于超时的结束界面
  	private void timeoutBuszhiposFinish()
  	{
  		BuszhiposFinish();
  	}
  	
    //撤销交易
  	private void deletezhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<viewSwitch=撤销交易","log.txt");
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 操作撤销（刷卡前）..","com.txt");
    	mMyApi.pos_cancel();
  	}
  	
    //退款交易
  	private void payoutzhipos()
  	{
  		Message childmsg=posmainhand.obtainMessage();
  		childmsg.what=CahslessTest.PAYOUTFAIL;
		childmsg.obj="退款失败";
		posmainhand.sendMessage(childmsg);
  	}
  
  	//接口返回
  	private IUserCallback mIUserCallback = new IUserCallback(){
  		@Override
  		public void onResult(REPLY rst) 
  		{
  			if(rst!=null) 
  			{
  				Message childmsg=posmainhand.obtainMessage();
  				//info(rst.op + ":" + rst.code + "," + rst.code_info);
  				//【操作标识符】LfMISPOSApi下“OP_”开头的静态变量，如：LfMISPOSApi.OP_INIT、LfMISPOSApi.OP_PURCHASE等等
  				//打开串口
  				if(rst.op.equals(LfMISPOSApi.OP_INIT))
  				{
  					//【返回码和信息】code和code_info的返回/说明，见com.landfoneapi.mispos.ErrCode
  					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开成功"+ToolClass.getExtracom(),"com.txt");
  						childmsg.what=CahslessTest.OPENSUCCESS;
  						childmsg.obj="打开成功"+ToolClass.getExtracom();
  					}else{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开失败"+ToolClass.getExtracom()+",code:"+rst.code+",info:"+rst.code_info,"com.txt");						
  						childmsg.what=CahslessTest.OPENFAIL;
  						childmsg.obj="打开失败"+ToolClass.getExtracom()+",code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//关闭串口
  				else if(rst.op.equals(LfMISPOSApi.OP_RELEASE))
  				{
  					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭成功","com.txt");
  						childmsg.what=CahslessTest.CLOSESUCCESS;
  						childmsg.obj="关闭成功";
  					}else{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
  						childmsg.what=CahslessTest.CLOSEFAIL;
  						childmsg.obj="关闭失败,code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//扣款
  				else if(rst.op.equals(LfMISPOSApi.OP_PURCHASE))
  				{
  					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款成功","com.txt");
  						childmsg.what=CahslessTest.COSTSUCCESS;
  						childmsg.obj="扣款成功";
  					}
  					else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 撤销成功","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="撤销成功";
  					}
  					else
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
  						childmsg.what=CahslessTest.COSTFAIL;
  						childmsg.obj="扣款失败,code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//返回结果
  				else if(rst.op.equals(LfMISPOSApi.OP_GETRECORD))
  				{
  					//返回00，代表成功
  					if(rst.code.equals(ErrCode._00.getCode()))
  					{
  						String tmp = "单据:特定信息=";
  						tmp += "[" + ((_04_GetRecordReply) (rst)).getSpecInfoField();//特定信息【会员卡需要！！】
  						/*特定信息说明
  						+储值卡号(19)
  						+终端流水号(6)
  						+终端编号(8)
  						+批次号(6)
  						+商户号(15)
  						+商户名称(60)
  						+会员名称(60)
  						+交易时间(6)
  						+交易日期(8)
  						+交易单号(14)
  						+消费金额(12)
  						+账户余额(12)
  						+临时交易流水号（26）
  						以上都是定长，金额都是定长12位，前补0，其他不足位数后补空格

  						* */
  						tmp += "],商户代码=[" + ((_04_GetRecordReply) (rst)).getMer();//商户代码
  						tmp += "],终端号=[" + ((_04_GetRecordReply) (rst)).getTmn();//终端号
  						tmp += "],卡号=[" + ((_04_GetRecordReply) (rst)).getCardNo();//卡号
  						tmp += "],交易批次号=[" + ((_04_GetRecordReply) (rst)).getTransacionBatchNo();//交易批次号
  						tmp += "],原交易类型=[" + ((_04_GetRecordReply) (rst)).getTransacionVoucherNo();//原交易类型
  						tmp += "],交易日期和时间=[" + ((_04_GetRecordReply) (rst)).getTransacionDatetime();//交易日期和时间
  						tmp += "],交易金额=[" + ((_04_GetRecordReply) (rst)).getTransacionAmount();//交易金额
  						tmp +="]";
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询成功="+tmp,"com.txt");
  						childmsg.what=CahslessTest.QUERYSUCCESS;
  						childmsg.obj=((_04_GetRecordReply) (rst)).getSpecInfoField();
  					}
  					else
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
  						childmsg.what=CahslessTest.QUERYFAIL;
  						childmsg.obj="";
  					}
  				}
  				posmainhand.sendMessage(childmsg);
  			}
  		}

  		@Override
  		public void onProcess(Display dpl) {//过程和提示信息
  			if(dpl!=null) {
  				//lcd(dpl.getType() + "\n" + dpl.getMsg());

  				//【提示信息类型】type的说明，见com.landfoneapi.mispos.DisplayType
  				if(dpl.getType().equals(DisplayType._4.getType())){
  					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 通讯提示<<"+dpl.getMsg(),"com.txt");
  				}

  			}
  		}
  	};
    //=======================
  	//实现Bushuo页面相关接口
  	//=======================
    //步骤三、实现Bushuo接口,出货
    @Override
	public void BushuoChuhuoOpt(int cabinetvar, int huodaoNo,int cabinetTypevar) {
		// TODO Auto-generated method stub
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<busport商品cabID="+cabinetvar+"huoID="+huodaoNo+"cabType="+cabinetTypevar,"log.txt");
    	//2.计算货物金额
		float cost=0;
		if(Integer.parseInt(zhifutype)==0)
		{
			cost=amount;
		}
    	ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
		    	+" column="+huodaoNo
		    	+" cost="+cost
		    	,"log.txt");
		Intent intent = new Intent();
		//4.发送指令广播给COMService
		intent.putExtra("EVWhat", COMService.EV_CHUHUOCHILD);	
		intent.putExtra("cabinet", cabinetvar);	
		intent.putExtra("column", huodaoNo);	
		intent.putExtra("cost", ToolClass.MoneySend(cost));
		intent.setAction("android.intent.action.comsend");//action与接收器相同
		comBroadreceiver.sendBroadcast(intent);
    }
    
    //步骤三、实现Bushuo接口,结束出货页面
    @Override
	public void BushuoFinish(int status) {
    	// TODO Auto-generated method stub
    	recLen=SPLASH_DISPLAY_LENGHT;
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
		  	    	//EVprotocolAPI.EV_mdbCost(ToolClass.getCom_id(),ToolClass.MoneySend(amount));
					Intent intent=new Intent();
			    	intent.putExtra("EVWhat", EVprotocol.EV_MDB_COST);	
					intent.putExtra("cost", ToolClass.MoneySend((float)amount));	
					intent.setAction("android.intent.action.comsend");//action与接收器相同
					comBroadreceiver.sendBroadcast(intent);					
				}
				//出货失败,不扣钱
				else
				{	
					payback();
				}				
    			break;
    		//pos页面	
    		case 1:
    			//出货成功,结束交易
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<pos无退款","log.txt");
					OrderDetail.addLog(BusPort.this);					
					clearamount();
					recLen=10;
				}
				//出货失败,退钱
				else
				{	
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<pos退款amount="+amount,"com.txt");
					dialog= ProgressDialog.show(BusPort.this,"正在退款中","请稍候...");
					payoutzhipos();//退款操作									
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
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ali退款amount="+amount,"log.txt");					
					dialog= ProgressDialog.show(BusPort.this,"正在退款中","请稍候...");
					payoutzhier();//退款操作	
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
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<wei退款amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusPort.this,"正在退款中","请稍候...");
					payoutzhiwei();//退款操作									
				}
    			break;    			
    		//取货码页面		
    		case -1:
    			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<取货码页面","log.txt");
				OrderDetail.addLog(BusPort.this);					
				clearamount();
				recLen=10;
    			break;
    	}
    	
	}
    
    //出货完成后，进入退币流程
  	private void payback()
  	{
  		//2.更新投币金额
		ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<退款money="+money,"com.txt");
		
	    //还有余额退币
		if(money>0)
		{
			dialog= ProgressDialog.show(BusPort.this,"正在退币中,金额"+money,"请稍候...");
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
	    	OrderDetail.addLog(BusPort.this);
	    	clearamount();
  			new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {            	
	            	//关闭纸币硬币器
		  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0); 
			    	BillEnable(0);					    	
	            }

			}, 500); 
  	    	recLen=10;
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
        	BillEnable(0);	
    	}
    	else if(gotoswitch==BUSZHIER)
    	{
        	OrderDetail.setSmallCard(amount);
    	}
    	else if(gotoswitch==BUSZHIWEI)
    	{
    		OrderDetail.setSmallCard(amount);
    	}
    	//延时
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {   
            	viewSwitch(BUSHUO, null);
            }

		}, 2500);    	
    }
    //清参数
  	public void clearamount()
  	{
  		//现金页面
  	    con = 0;
  	    queryLen = 0; 
  	    iszhienable=0;//1发送打开指令,0还没发送打开指令
  	    isempcoin=false;//false还未发送关纸币器指令，true因为缺币，已经发送关纸币器指令
  	    billdev=1;//是否需要打开纸币器,1需要
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
  		//pos页面
  		iszhipos=0;//1成功发送了扣款请求,0没有发送成功扣款请求，2刷卡扣款已经完成并且金额足够
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
				//Heart操作
			    Intent intent2=new Intent();
		    	intent2.putExtra("EVWhat", EVprotocol.EV_MDB_HEART);
				intent2.setAction("android.intent.action.comsend");//action与接收器相同
				comBroadreceiver.sendBroadcast(intent2);
				
				amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo(); 
				zhifutype="0";
				out_trade_no=ToolClass.out_trade_no(BusPort.this);// 创建InaccountDAO对象;
				//切换页面
				if (buszhiamountFragment == null) {
					buszhiamountFragment = new BuszhiamountFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, buszhiamountFragment);
	            //延时
			    new Handler().postDelayed(new Runnable() 
				{
		            @Override
		            public void run() 
		            {   
		            	//打开纸币器
						BillEnable(1);
		            }

				}, 1500);
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
				//新建一个线程并启动
				zhifubaothread.execute(zhifubaohttp);
				//延时
			    new Handler().postDelayed(new Runnable() 
				{
		            @Override
		            public void run() 
		            {   
			            //发送订单
		        		sendzhier();
		            }

				}, 1500);		            
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
	            //新建一个线程并启动
	            weixingthread.execute(weixinghttp);
				//延时
			    new Handler().postDelayed(new Runnable() 
				{
		            @Override
		            public void run() 
		            {   
		            	//发送订单
		        		sendzhiwei();
		            }

				}, 1500);	           
				break;	
			case BUSZHIPOS://pos支付
				isbus=false;
				amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
				zhifutype="1";				
				out_trade_no=ToolClass.out_trade_no(BusPort.this);// 创建InaccountDAO对象;
				if (buszhiposFragment == null) {
					buszhiposFragment = new BuszhiposFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, buszhiposFragment);
	            ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开读卡器"+ToolClass.getExtracom(),"com.txt");
	            //打开串口
	            //ip、端口、串口、波特率必须准确
				mMyApi.pos_init("121.40.30.62", 18080
						,ToolClass.getExtracom(), "9600", mIUserCallback);
				//延时
			    new Handler().postDelayed(new Runnable() 
				{
		            @Override
		            public void run() 
		            {   
		            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器扣款="+amount,"com.txt");
		            	listterner.BusportTsxx("提示信息：请刷卡");
						mMyApi.pos_purchase(ToolClass.MoneySend(amount), mIUserCallback);	
				    	iszhipos=1;
					}

				}, 500);	           
				break;	
			case BUSHUO://出货页面	
				recLen=10*60;
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
		
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<businessJNI","log.txt");		
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
			case COMThread.EV_OPTMAIN: 
				SerializableMap serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusPort 综合设备操作="+Set,"com.txt");
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
								listterner.BusportTsxx("提示信息：重试"+con);
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
								listterner.BusportTsxx("提示信息：[纸币器]无法使用");
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
						  			iszhienable=2;
						  			tochuhuo();
						  		}
						  	}
						}						
						break;
					case EVprotocol.EV_MDB_COST://扣款流程
						float cost=ToolClass.MoneyRec((Integer)Set.get("cost"));	
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMBusPort 扣款="+cost,"com.txt");
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
			  	    	//EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0); 
				    	BillEnable(0);
			  	    	if(gotoswitch==BUSZHIAMOUNT)
			  	    	{
			  	    		viewSwitch(BUSPORT, null);	
			  	    	}
			  	    	else
			  	    	{
			  	    		recLen=10;
			  	    	}
						break; 
					//是出货操作	
					case EVprotocol.EV_BENTO_OPEN://格子柜出货 					
					case EVprotocol.EV_COLUMN_OPEN://主柜出货
						status=Set.get("result");//出货结果
						listterner.BusportChjg(status);
					default:break;	
				}
				break;				
			}
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
