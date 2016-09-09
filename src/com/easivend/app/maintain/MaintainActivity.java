/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           MaintainActivity.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        维护菜单主页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.http.EVServerhttp;
import com.easivend.view.COMService;
import com.easivend.view.DogService;
import com.easivend.view.EVServerService;
import com.easivend.weixing.WeiConfigAPI;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.BusLand;
import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusZhitihuo;
import com.easivend.common.PictureAdapter;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;
import com.tencent.bugly.crashreport.CrashReport;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MaintainActivity extends Activity
{
	private GridView gvInfo;// 创建GridView对象	
	//进度对话框
	ProgressDialog dialog= null;
	// 定义字符串数组，存储系统功能
    private String[] titles = new String[] { "商品管理", "货道管理", "参数配置", "订单信息", "操作日志", "本机配置", "交易页面", "退出" };
    // 定义int数组，存储功能对应的图标
    private int[] images = new int[] { R.drawable.addoutaccount, R.drawable.addinaccount, R.drawable.outaccountinfo, R.drawable.showinfo,
            R.drawable.inaccountinfo, R.drawable.sysset, R.drawable.accountflag, R.drawable.exit };
    String com=null,bentcom=null,columncom=null,extracom=null,cardcom=null;
    final static int REQUEST_CODE=1;   
    //获取货柜信息
   //Map<String,Integer> huoSet=new HashMap<String,Integer>();
    SerializableMap serializableMap = null;
    //Dog服务相关
    int isallopen=1;//是否保持持续一直打开,1一直打开,0关闭后不打开
	private final int SPLASH_DISPLAY_LENGHT = 5000; // 延迟5秒
	//LocalBroadcastManager dogBroadreceiver;
	//Server服务相关
	LocalBroadcastManager localBroadreceiver;
	EVServerReceiver receiver;
	private boolean issale=false;//true是否已经自动打开过售卖页面了，如果打开过，就不再打开了
	Map<String, String> vmcmap;
	//COM服务相关
	LocalBroadcastManager comBroadreceiver;
	COMReceiver comreceiver;
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintain);	
		//bugly加载调试模式,false发布版本用,true调试版本用
		//CrashReport.testJavaCrash();这条语句用来测试奔溃
		CrashReport.initCrashReport(getApplicationContext(), "900048192", false);		
		//取得屏幕的长和宽，进行比较设置横竖屏的变量
		Display display = getWindowManager().getDefaultDisplay();  
		int width = display.getWidth();  
		int height = display.getHeight();  
		if (width > height) {  
			ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设为横屏
		} else { 
			ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设为竖屏
			//ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设为横屏
		}
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		//获取本应用context
		ToolClass.setContext(getApplicationContext());
		
		//==========
		//Dog服务相关
		//==========
		//启动服务
		startService(new Intent(this,DogService.class));
		//dogBroadreceiver.getInstance(this);
		//延时5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	//发送指令广播给DogService
        		Intent intent=new Intent();
        		intent.putExtra("isallopen", isallopen);
        		intent.setAction("android.intent.action.dogserversend");//action与接收器相同
        		//dogBroadreceiver.sendBroadcast(intent); 
        		sendBroadcast(intent); 
        		//==========
            	//EVDog服务相关
            	//==========
            	//发送指令广播给DogService
	    		Intent intent2=new Intent();
	    		intent2.putExtra("isallopen", isallopen);
	    		if(isallopen==1)
	    			intent2.putExtra("watchfeed", 1);
	    		else
	    			intent2.putExtra("watchfeed", 0);	
	    		intent2.setAction("android.intent.action.watchdog");//action与接收器相同
	    		//dogBroadreceiver.sendBroadcast(intent); 
	    		sendBroadcast(intent2);
            }

		}, SPLASH_DISPLAY_LENGHT);
		
		
		//=============
		//Server服务相关
		//=============
	    //3.开启服务
		startService(new Intent(MaintainActivity.this,EVServerService.class));
		//4.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
		vmcmap = ToolClass.getvmc_no(MaintainActivity.this);
		
		//=============
		//COM服务相关
		//=============
		//3.开启服务
		startService(new Intent(MaintainActivity.this,COMService.class));
		//4.注册接收器
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		//延时3s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	dialog= ProgressDialog.show(MaintainActivity.this,"正在同步货道","请稍候片刻...");
            	ToolClass.ResstartPort(1);
            	ToolClass.ResstartPort(2);
            	ToolClass.ResstartPort(3);
            	ToolClass.ResstartPort(4);
            	
            	//7.发送指令广播给COMService
        		Intent intent=new Intent();
        		intent.putExtra("EVWhat", COMService.EV_CHECKALLCHILD);	
        		intent.setAction("android.intent.action.comsend");//action与接收器相同
        		comBroadreceiver.sendBroadcast(intent);
            }

		}, 1000);
				
		//================
		//串口配置和注册相关
		//================
		ToolClass.SetDir();	//设置根目录
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[程序启动...]log路径:"+ToolClass.getEV_DIR()+File.separator+"logs","log.txt");			
		//从配置文件获取数据
		Map<String, String> list=ToolClass.ReadConfigFile();
		if(list!=null)
		{
			if(list.containsKey("com"))//设置现金串口号
	        {
				com = list.get("com");
				ToolClass.setCom(com);
	        }
			if(list.containsKey("bentcom"))//设置格子柜串口号
	        {
				bentcom = list.get("bentcom");
				ToolClass.setBentcom(bentcom);
	        }
			if(list.containsKey("columncom"))//设置主柜串口号
	        {
				columncom = list.get("columncom");
				ToolClass.setColumncom(columncom);	
	        }
	        if(list.containsKey("extracom"))//设置外协串口号
	        {
	        	extracom = list.get("extracom");
	        	ToolClass.setExtracom(extracom);	
	        }
	        if(list.containsKey("cardcom"))//设置读卡器串口号
	        {
	        	cardcom = list.get("cardcom");
	        	ToolClass.setCardcom(cardcom);
	        }	        	        
	        AlipayConfigAPI.SetAliConfig(list);//设置阿里账号
	        WeiConfigAPI.SetWeiConfig(list);//设置微信账号	        
	        
	        //2.作文件备份
  	        ToolClass.ResetConfigFile();  
		}
		else
		{
			dialog.dismiss();
		}
		//加载售罄水印图片		
		Bitmap mark = BitmapFactory.decodeResource(this.getResources(), R.drawable.ysq);  
		ToolClass.setMark(mark);
		//加载微信证书
		ToolClass.setWeiCertFile();		
		//加载goc
		ToolClass.setGoc(MaintainActivity.this);
		//加载机型
		ToolClass.setExtraComType(MaintainActivity.this);		
		//================
		//九宫格相关
		//================		
		gvInfo = (GridView) findViewById(R.id.gvInfo);// 获取布局文件中的gvInfo组件
        PictureAdapter adapter = new PictureAdapter(titles, images, this);// 创建pictureAdapter对象
        gvInfo.setAdapter(adapter);// 为GridView设置数据源
        gvInfo.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = null;// 创建Intent对象
                switch (arg2) {
                case 0:
                	intent = new Intent(MaintainActivity.this, GoodsManager.class);// 使用GoodsManager窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);// 打开GoodsManager
                    break;
                case 1:
                	intent = new Intent(MaintainActivity.this, HuodaoTest.class);// 使用HuodaoTest窗口初始化Intent
                    startActivityForResult(intent,REQUEST_CODE);// 打开HuodaoTest
                    break;
                case 2:
                	intent = new Intent(MaintainActivity.this, ParamManager.class);// 使用ParamManager窗口初始化Intent
                    startActivityForResult(intent,REQUEST_CODE);// 打开ParamManager                    
                    break;    
                case 3:
                	intent = new Intent(MaintainActivity.this, Order.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);
                    break;                
                case 4:
                	intent = new Intent(MaintainActivity.this, LogOpt.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);
                    break;
                case 5:
                	intent = new Intent(MaintainActivity.this, Login.class);// 使用Accountflag窗口初始化Intent
                	startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
                    break;
                case 6:
                	//横屏
    				if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    				{
    					intent = new Intent(MaintainActivity.this, BusLand.class);// 使用Accountflag窗口初始化Intent
    				}
    				//竖屏
    				else
    				{
    					intent = new Intent(MaintainActivity.this, BusPort.class);// 使用Accountflag窗口初始化Intent
    				}                	
                    startActivityForResult(intent,REQUEST_CODE);// 打开Accountflag
                    break;
                case 7:
                	//创建警告对话框
                	Dialog alert=new AlertDialog.Builder(MaintainActivity.this)
                		.setTitle("对话框")//标题
                		.setMessage("您确定要退出程序吗？")//表示对话框中得内容
                		.setIcon(R.drawable.ic_launcher)//设置logo
                		.setPositiveButton("退出", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
                			{				
            	    				@Override
            	    				public void onClick(DialogInterface dialog, int which) 
            	    				{
            	    					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[程序关闭...]","log.txt");			
            	    					// TODO Auto-generated method stub	
            	    					finish();// 关闭当前Activity
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
                    
                    break;
                }
            }
        });

	}
			
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==MaintainActivity.RESULT_CANCELED)
			{				
			}	
			else if(resultCode==MaintainActivity.RESULT_OK)
			{	
				//从配置文件获取数据
				Map<String, String> list=ToolClass.ReadConfigFile();
				if(list!=null)
				{
			        if(list.containsKey("isallopen"))
			        {
			        	isallopen=Integer.parseInt(list.get("isallopen"));	
			        	//发送指令广播给DogService
		        		Intent intent=new Intent();
		        		intent.putExtra("isallopen", isallopen);
		        		intent.setAction("android.intent.action.dogserversend");//action与接收器相同
		        		//dogBroadreceiver.sendBroadcast(intent);
		        		sendBroadcast(intent); 
		        		//==========
		            	//EVDog服务相关
		            	//==========
		            	//发送指令广播给DogService
			    		Intent intent2=new Intent();
			    		intent2.putExtra("isallopen", isallopen);
			    		if(isallopen==1)
			    			intent2.putExtra("watchfeed", 1);
			    		else
			    			intent2.putExtra("watchfeed", 0);	
			    		intent2.setAction("android.intent.action.watchdog");//action与接收器相同
			    		//dogBroadreceiver.sendBroadcast(intent); 
			    		sendBroadcast(intent2);
			        }
				}
			}
		}	
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
			case EVServerhttp.SETMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=签到成功","log.txt");			
				if(dialog.isShowing())
					dialog.dismiss();
				timer.scheduleWithFixedDelay(task, 10,10, TimeUnit.SECONDS);       // timeTask 
				if(issale==false)
				{
					issale=true;
					//签到完成，自动开启售货程序
					Intent intbus;
					//横屏
					if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
					{
						intbus = new Intent(MaintainActivity.this, BusLand.class);// 使用Accountflag窗口初始化Intent
					}
					//竖屏
					else
					{
						intbus = new Intent(MaintainActivity.this, BusPort.class);// 使用Accountflag窗口初始化Intent
					}
					startActivityForResult(intbus,REQUEST_CODE);// 打开Accountflag
				}
	    		break;
			case EVServerhttp.SETFAILMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=失败，网络故障","log.txt");	
				if(dialog.isShowing())
					dialog.dismiss();
				timer.scheduleWithFixedDelay(task, 10,10, TimeUnit.SECONDS);       // timeTask 
				if(issale==false)
				{
					issale=true;
					//签到完成，自动开启售货程序
					Intent intbus;
					//横屏
					if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
					{
						intbus = new Intent(MaintainActivity.this, BusLand.class);// 使用Accountflag窗口初始化Intent
					}
					//竖屏
					else
					{
						intbus = new Intent(MaintainActivity.this, BusPort.class);// 使用Accountflag窗口初始化Intent
					}
					startActivityForResult(intbus,REQUEST_CODE);// 打开Accountflag
				}
	    		break;	
			}			
		}

	}
	
	//=============
	//COM服务相关
	//=============	
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
			case COMThread.EV_CHECKALLMAIN:
				dialog.setTitle("正在同步服务器");
				//ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询全部","com.txt");
				serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询全部="+Set,"com.txt");	
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<vmserversend","log.txt");
		    	//*******
				//服务器同步
				//*******
				Intent intent2=new Intent(); 
				intent2.putExtra("EVWhat", EVServerhttp.SETCHILD);
				intent2.putExtra("vmc_no", vmcmap.get("vmc_no"));
				intent2.putExtra("vmc_auth_code", vmcmap.get("vmc_auth_code"));
				//传递数据
		        final SerializableMap myMap=new SerializableMap();
		        myMap.setMap(Set);//将map数据添加到封装的myMap<span></span>中
		        Bundle bundle2=new Bundle();
		        bundle2.putSerializable("huoSet", myMap);
		        intent2.putExtras(bundle2);
				intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
				localBroadreceiver.sendBroadcast(intent2);  
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","开机启动后台服务...","server.txt");
	    		break;	
	    		//按钮返回
			case COMThread.EV_BUTTONMAIN:
				SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set2=serializableMap2.getMap();				
				int EV_TYPE=Set2.get("EV_TYPE");
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 按键操作="+Set2,"com.txt");
				//上报维护模式按键
				if(EV_TYPE==COMThread.EV_BUTTONRPT_MAINTAIN)
				{
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 维护模式","com.txt");
					if(issale==false)
					{
						// 弹出信息提示
			            Toast.makeText(MaintainActivity.this, "〖本机处于维护模式，不能同步〗！", Toast.LENGTH_SHORT).show();
					}
				}
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
                public void run() 
                {
                	Boolean bool=false;                	
                	//==========
                	//Dog服务相关
                	//==========
                	bool=ToolClass.isServiceRunning("com.easivend.view.DogService");
                	ToolClass.Log(ToolClass.INFO,"EV_DOG","Check DogService:"+bool,"dog.txt");
                	if(bool==false)
                	{
                		//启动服务
                		startService(new Intent(MaintainActivity.this,DogService.class));
                		//dogBroadreceiver.getInstance(this);
                		//延时1s
                		new Handler().postDelayed(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{      
                				//从配置文件获取数据
                				Map<String, String> list=ToolClass.ReadConfigFile();
                				if(list!=null)
                				{
                			        if(list.containsKey("isallopen"))
                			        {
                			        	//发送指令广播给DogService
                			    		Intent intent=new Intent();
                			    		intent.putExtra("isallopen", isallopen);
                			    		intent.setAction("android.intent.action.dogserversend");//action与接收器相同
                			    		//dogBroadreceiver.sendBroadcast(intent); 
                			    		sendBroadcast(intent); 
                			        }
                				}
                			}

                		}, 1000);
                	}
                	//==========
                	//EVDog服务相关
                	//==========
                	//发送指令广播给DogService
		    		Intent intent=new Intent();
		    		intent.putExtra("isallopen", isallopen);
		    		if(isallopen==1)
		    			intent.putExtra("watchfeed", 1);
		    		else
		    			intent.putExtra("watchfeed", 0);	
		    		intent.setAction("android.intent.action.watchdog");//action与接收器相同
		    		//dogBroadreceiver.sendBroadcast(intent); 
		    		sendBroadcast(intent);
                	//=============
                	//COM服务相关
                	//=============
                	bool=ToolClass.isServiceRunning("com.easivend.view.COMService");
                	ToolClass.Log(ToolClass.INFO,"EV_DOG","Check COMService:"+bool,"dog.txt");
                	if(bool==false)
                	{
                		//3.开启服务
                		startService(new Intent(MaintainActivity.this,COMService.class));
                		//4.注册接收器
                		IntentFilter comfilter=new IntentFilter();
                		comfilter.addAction("android.intent.action.comrec");
                		comBroadreceiver.registerReceiver(comreceiver,comfilter);
                	}
                	
                	//=============
                	//Server服务相关
                	//=============
                	bool=ToolClass.isServiceRunning("com.easivend.view.EVServerService");
                	ToolClass.Log(ToolClass.INFO,"EV_DOG","Check EVServerService:"+bool,"dog.txt");
                	if(bool==false)
                	{
                		//3.开启服务
                		startService(new Intent(MaintainActivity.this,EVServerService.class));
                		//4.注册接收器
                		IntentFilter filter=new IntentFilter();
                		filter.addAction("android.intent.action.vmserverrec");
                		localBroadreceiver.registerReceiver(receiver,filter);
                		//延时1s
                		new Handler().postDelayed(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{      
                				//ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询全部","com.txt");
                				Map<String, Integer> Set=serializableMap.getMap();
                				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 货道查询全部="+Set,"com.txt");	
                				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<vmserversend","log.txt");
                		    	//*******
                				//服务器同步
                				//*******
                				Intent intent2=new Intent(); 
                				intent2.putExtra("EVWhat", EVServerhttp.SETCHILD);
                				intent2.putExtra("vmc_no", vmcmap.get("vmc_no"));
                				intent2.putExtra("vmc_auth_code", vmcmap.get("vmc_auth_code"));
                				//传递数据
                		        final SerializableMap myMap=new SerializableMap();
                		        myMap.setMap(Set);//将map数据添加到封装的myMap<span></span>中
                		        Bundle bundle2=new Bundle();
                		        bundle2.putSerializable("huoSet", myMap);
                		        intent2.putExtras(bundle2);
                				intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
                				localBroadreceiver.sendBroadcast(intent2); 
                				ToolClass.Log(ToolClass.INFO,"EV_SERVER","自检重启后台服务...","server.txt");
                			}

                		}, 1000);
                	}
                	
                } 
            }); 
        } 
    };
	
	@Override
	protected void onDestroy() {		
		//=============
		//Server服务相关
		//=============
		//5.解除注册接收器
		localBroadreceiver.unregisterReceiver(receiver);
		//6.结束服务
		stopService(new Intent(MaintainActivity.this, EVServerService.class));
		//=============
		//COM服务相关
		//=============
		//5.解除注册接收器
		comBroadreceiver.unregisterReceiver(comreceiver);
		//6.结束服务
		stopService(new Intent(MaintainActivity.this, COMService.class));
		//关闭自检重启定时器
		timer.shutdown();
		// TODO Auto-generated method stub
		super.onDestroy();		
	}
}


