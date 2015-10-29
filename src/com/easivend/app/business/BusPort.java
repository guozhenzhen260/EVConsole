package com.easivend.app.business;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.fragment.BusgoodsFragment;
import com.easivend.fragment.BusgoodsFragment.BusgoodsFragInteraction;
import com.easivend.fragment.BusgoodsclassFragment;
import com.easivend.fragment.BusgoodsclassFragment.BusgoodsclassFragInteraction;
import com.easivend.fragment.BusgoodsselectFragment;
import com.easivend.fragment.BusgoodsselectFragment.BusgoodsselectFragInteraction;
import com.easivend.fragment.BusinesslandFragment;
import com.easivend.fragment.BusinessportFragment;
import com.easivend.fragment.BusinessportFragment.BusportFragInteraction;
import com.easivend.fragment.BuszhiamountFragment;
import com.easivend.fragment.BuszhiamountFragment.BuszhiamountFragInteraction;
import com.easivend.fragment.MoviewlandFragment;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.http.EVServerhttp;
import com.example.evconsole.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
BuszhiamountFragInteraction
{
	private BusinessportFragment businessportFragment;
	private BusgoodsclassFragment busgoodsclassFragment;
	private BusgoodsFragment busgoodsFragment;
	private BusgoodsselectFragment busgoodsselectFragment;
	private BuszhiamountFragment buszhiamountFragment;
	//交易页面
    Intent intent=null;
    final static int REQUEST_CODE=1; 
    public static final int BUSPORT=1;//首页面
    public static final int BUSGOODSCLASS=2;//商品类别页面
	public static final int BUSGOODS=3;//商品导购页面
	public static final int BUSGOODSSELECT=4;//商品详细页面
	public static final int BUSZHIAMOUNT=5;//现金支付页面
	public static final int BUSZHIER=6;//支付宝支付页面
	public static final int BUSZHIWEI=7;//微信支付页面
	Timer timer = new Timer(true);
    private final int SPLASH_DISPLAY_LENGHT = 5*60; //  5*60延迟5分钟	
    private int recLen = SPLASH_DISPLAY_LENGHT; 
    private boolean isbus=true;//true表示在广告页面，false在其他页面
    
    //=========================
    //activity与fragment回调相关
    //=========================
    /**
     * 用来与其他fragment交互的
     */
    private BusPortFragInteraction listterner;
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
    public interface BusPortFragInteraction
    {
        /**
         * Activity 向Fragment传递指令，这个方法可以根据需求来定义
         * @param str
         */
        //现金页面
        void BusportTsxx();      //提示信息
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
		//注册串口监听器
		EVprotocolAPI.setCallBack(new jniInterfaceImp());
		timer.schedule(new TimerTask() { 
	        @Override 
	        public void run() { 
	        	  if(isbus==false)
	        	  {
		        	  //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
		        	  recLen--; 		    	      
		        	  if(recLen == 0)
		              { 
		                  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recclose=BusinessportFragment","log.txt");
		                  viewSwitch(BUSPORT,null);		                  
		              }	
	        	  }
	        } 
	    }, 1000, 1000);       // timeTask 
		//初始化默认fragment
		initView();
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

	//步骤三、实现Business接口,关闭本activity界面
	@Override
	public void finishBusiness() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=finishBusiness","log.txt");
		finish();
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
		//关闭纸币硬币器
	    EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,0); 
	    viewSwitch(BUSPORT, null);
	}
	
	
	//全局用于切换view的函数
	public void viewSwitch(int buslevel, Map<String, String> str)
	{
		recLen=SPLASH_DISPLAY_LENGHT;
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
				//打开纸币硬币器
            	EVprotocolAPI.EV_mdbEnable(ToolClass.getCom_id(),1,1,1);
            	//切换页面
				if (buszhiamountFragment == null) {
					buszhiamountFragment = new BuszhiamountFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, buszhiamountFragment);
				break;	
			case BUSZHIER://支付宝支付
				isbus=false;
				if (busgoodsclassFragment == null) {
					busgoodsclassFragment = new BusgoodsclassFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, busgoodsclassFragment);
				break;
			case BUSZHIWEI://微信支付
				isbus=false;
				if (busgoodsclassFragment == null) {
					busgoodsclassFragment = new BusgoodsclassFragment();
	            }
	            // 使用当前Fragment的布局替代id_content的控件
	            transaction.replace(R.id.id_content, busgoodsclassFragment);
				break;	
		}
		// transaction.addToBackStack();
        // 事务提交
        transaction.commit();
	}
		
	
	//创建一个专门处理单击接口的子类
	private class jniInterfaceImp implements JNIInterface
	{
		int con=0;
		@Override
		public void jniCallback(Map<String, Object> allSet) {
			// TODO Auto-generated method stub
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<business监听到","log.txt");	
			Map<String, Object> Set= allSet;
			int jnirst=(Integer) Set.get("EV_TYPE");
			//txtcom.setText(String.valueOf(jnirst));
			switch (jnirst)
			{
				case EVprotocolAPI.EV_MDB_ENABLE://接收子线程投币金额消息	
					//打开失败,等待重新打开
					if( ((Integer)Set.get("bill_result")==0)&&((Integer)Set.get("coin_result")==0) )
					{
						//txtbuszhiamounttsxx.setText("提示信息：重试"+con);
						con++;
					}
					//打开成功
					else
					{
//						//第一次打开才发送coninfo，以后就不再操作这个了
//						if(iszhienable==0)
//							EVprotocolAPI.EV_mdbCoinInfoCheck(ToolClass.getCom_id());
					}										
					break;
				//现金设备状态查询
				case EVprotocolAPI.EV_MDB_HEART://心跳查询
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现金设备状态:","log.txt");	
					int bill_err=ToolClass.getvmcStatus(Set,1);
					int coin_err=ToolClass.getvmcStatus(Set,2);
					//上报给服务器
					Intent intent=new Intent();
    				intent.putExtra("EVWhat", EVServerhttp.SETDEVSTATUCHILD);
    				intent.putExtra("bill_err", bill_err);
    				intent.putExtra("coin_err", coin_err);
    				intent.setAction("android.intent.action.vmserversend");//action与接收器相同
    				sendBroadcast(intent); 
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
	}
	@Override
	protected void onDestroy() {
		//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}

	

	

	

	

	

}
