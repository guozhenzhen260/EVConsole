package com.easivend.app.business;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.ToolClass;
import com.easivend.fragment.BusinesslandFragment;
import com.easivend.fragment.BusinesslandFragment.BusFragInteraction;
import com.easivend.fragment.MoviewlandFragment;
import com.easivend.fragment.MoviewlandFragment.MovieFragInteraction;
import com.easivend.view.PassWord;
import com.example.evconsole.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
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
		//初始化默认fragment
		initView();		
		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<portthread="+Thread.currentThread().getId(),"log.txt"); 
	        	  if(isbus==false)
	        	  {
		        	  //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<recLen="+recLen,"log.txt");
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
	
	//步骤三、实现Business接口,打开密码框
	@Override
	public void finishBusiness() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<busland=打开密码框","log.txt");
//    	Intent intent = new Intent();
//    	intent.setClass(BusLand.this, PassWord.class);// 使用AddInaccount窗口初始化Intent
//        startActivityForResult(intent, PWD_CODE);
		finish();
	}
	//步骤三、实现Business接口,暂停倒计时定时器并且转到商品购物页面
	//buslevel级别1到商品类别，2到商品导购页面，3到商品详细页面
	@Override
	public void gotoBusiness(int buslevel,Map<String, String>str) {
		// TODO Auto-generated method stub
		isbus=true;
	    recLen=SPLASH_DISPLAY_LENGHT;
		//switchMovie();
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
				break;
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
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<businessJNI","log.txt");		
		if(requestCode==PWD_CODE)
		{
			if(resultCode==PassWord.RESULT_OK)
			{
				Bundle bundle=data.getExtras();
				String pwd = bundle.getString("pwd");
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<维护密码pwd="+pwd,"log.txt");
				boolean istrue=ToolClass.getpwdStatus(BusLand.this,pwd);
				if(istrue)
		    	{
		    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<确定退出","log.txt");
		    		finish();
		    	}
		    	else
		    	{		    		
		    		switchMovie();
		    		// 弹出信息提示
			        Toast.makeText(BusLand.this, "抱歉，维护密码输入错误！", Toast.LENGTH_LONG).show();
				}
			}			
		}
		else 
		{
			switchMovie();	
		}
	}
	@Override
	protected void onDestroy() {
		timer.shutdown();
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}

	

	

	

	
}
