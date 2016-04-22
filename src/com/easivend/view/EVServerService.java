package com.easivend.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.GoodsProSet;
import com.easivend.app.maintain.HuodaoSet;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.app.maintain.Order;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_OrderAdapter;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_class;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class EVServerService extends Service {
	private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟3秒
	private ExecutorService thread=null;
    private Handler mainhand=null,childhand=null;  
    private String vmc_no=null;
    private String vmc_auth_code=null;
    private int tokenno=0;
    EVServerhttp serverhttp=null;
    LocalBroadcastManager localBroadreceiver;
    ActivityReceiver receiver;
    Map<String,Integer> huoSet=null;
    private String LAST_EDIT_TIME="";
    private boolean ischeck=false;//true签到成功,false开始签到流程
    private boolean isspempty=false;//true有不存在的商品,false没有不存在的商品
    private int isspretry=0;//有不存在的商品时，重试3次，不行就跳过
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service bind","server.txt");
		return null;
	}
	//8.创建activity的接收器广播，用来接收内容
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//签到
			case EVServerhttp.SETCHILD:
				vmc_no=bundle.getString("vmc_no");
				vmc_auth_code=bundle.getString("vmc_auth_code");
				SerializableMap serializableMap = (SerializableMap) bundle.get("huoSet");
				huoSet=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","receiver:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code
						+"huoSet="+huoSet.toString(),"server.txt");
				ToolClass.setContext(context);
				//处理接收到的内容,发送签到命令到子线程中
				//初始化一:发送签到指令
	        	childhand=serverhttp.obtainHandler();
	    		Message childmsg=childhand.obtainMessage();
	    		childmsg.what=EVServerhttp.SETCHILD;
	    		JSONObject ev=null;
	    		try {
	    			ev=new JSONObject();
	    			ev.put("vmc_no", vmc_no);
	    			ev.put("vmc_auth_code", vmc_auth_code);
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childmsg.obj=ev;
	    		childhand.sendMessage(childmsg);
	    		ischeck=false;
	    		break;    			
    		//发送交易记录命令到子线程中
			case EVServerhttp.SETRECORDCHILD://子线程接收主线程消息获取心跳信息
				childhand=serverhttp.obtainHandler();
        		Message childheartmsg2=childhand.obtainMessage();
        		childheartmsg2.what=EVServerhttp.SETRECORDCHILD;
        		childheartmsg2.obj=grid();
        		childhand.sendMessage(childheartmsg2);						
				break;	
    		//发送货道上传命令到子线程中	
			case EVServerhttp.SETHUODAOSTATUCHILD:				
				childhand=serverhttp.obtainHandler();
        		Message childheartmsg3=childhand.obtainMessage();
        		childheartmsg3.what=EVServerhttp.SETHUODAOSTATUCHILD;
        		childheartmsg3.obj=columngrid();
        		childhand.sendMessage(childheartmsg3);
				break;	
			}			
		}

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service create","server.txt");
		super.onCreate();
		//9.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserversend");
		localBroadreceiver.registerReceiver(receiver,filter);						
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service destroy","server.txt");
		//解除注册接收器
		localBroadreceiver.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service start","server.txt");		
		//***********************
		//线程进行vmserver操作
		//***********************
		mainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				Intent intent;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					//签到
					case EVServerhttp.SETERRFAILMAIN://子线程接收主线程消息签到失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 签到失败，原因="+msg.obj.toString(),"server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						localBroadreceiver.sendBroadcast(intent);	
						break;					
					case EVServerhttp.SETMAIN://子线程接收主线程消息签到完成
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 签到成功","server.txt");
						//初始化二:获取商品分类信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg=childhand.obtainMessage();
		        		childmsg.what=EVServerhttp.SETCLASSCHILD;
		        		childmsg.obj=LAST_EDIT_TIME;
		        		childhand.sendMessage(childmsg);
						break;					
					//获取商品分类信息	
					case EVServerhttp.SETERRFAILCLASSMAIN://子线程接收主线程消息获取商品分类信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品分类信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETCLASSMAIN://子线程接收主线程消息获取商品分类信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品分类信息成功","server.txt");
						try 
						{
							updatevmcClass(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						//初始化三:获取商品信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg2=childhand.obtainMessage();
		        		childmsg2.what=EVServerhttp.SETPRODUCTCHILD;
		        		childmsg2.obj=LAST_EDIT_TIME;
		        		childhand.sendMessage(childmsg2);
						break;
					//获取商品信息	
					case EVServerhttp.SETERRFAILRODUCTMAIN://子线程接收主线程消息获取商品信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETRODUCTMAIN://子线程接收主线程消息获取商品信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取商品信息成功","server.txt");
						try 
						{
							boolean shprst=updatevmcProduct(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						//初始化四:获取货道配置信息
						childhand=serverhttp.obtainHandler();
		        		Message childmsg3=childhand.obtainMessage();
		        		childmsg3.what=EVServerhttp.SETHUODAOCHILD;
		        		childmsg3.obj=LAST_EDIT_TIME;
		        		childhand.sendMessage(childmsg3);
						break;	
					//获取货道信息	
					case EVServerhttp.SETERRFAILHUODAOMAIN://子线程接收主线程消息获取货道信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取货道信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETHUODAOMAIN://子线程接收主线程消息获取货道信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取货道信息成功","server.txt");
						try 
						{							
							updatevmcColumn(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						//有不存在的商品
						if((isspempty)&&(isspretry<4))
						{
							isspempty=false;
							isspretry++;
							//初始化三:获取商品信息
							childhand=serverhttp.obtainHandler();
			        		Message childmsg4=childhand.obtainMessage();
			        		childmsg4.what=EVServerhttp.SETPRODUCTCHILD;
			        		childmsg4.obj="";
			        		childhand.sendMessage(childmsg4);
						}
						else 
						{
							isspretry=0;
							//初始化五、发送心跳命令到子线程中
			            	childhand=serverhttp.obtainHandler();
			        		Message childheartmsg=childhand.obtainMessage();
			        		childheartmsg.what=EVServerhttp.SETHEARTCHILD;
			        		childhand.sendMessage(childheartmsg);	
						}
						break;					
					//获取心跳信息	
					case EVServerhttp.SETERRFAILHEARTMAIN://子线程接收主线程消息获取心跳信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取心跳信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETHEARTMAIN://子线程接收主线程消息获取心跳信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取心跳信息成功","server.txt");
						//初始化八、返回给activity广播,初始化完成
						if(ischeck==false)
						{
							intent=new Intent();
							intent.putExtra("EVWhat", EVServerhttp.SETMAIN);
							intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
							localBroadreceiver.sendBroadcast(intent);
							ischeck=true;
							LAST_EDIT_TIME=ToolClass.getLasttime();
						}
//						//初始化六、发送交易记录命令到子线程中
//		            	childhand=serverhttp.obtainHandler();
//		        		Message childheartmsg2=childhand.obtainMessage();
//		        		childheartmsg2.what=EVServerhttp.SETRECORDCHILD;
//		        		childheartmsg2.obj=grid();
//		        		childhand.sendMessage(childheartmsg2);						
						break;
					//获取上报交易记录返回	
					case EVServerhttp.SETERRFAILRECORDMAIN://子线程接收主线程消息上报交易记录失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报交易记录失败","server.txt");
						break;
					case EVServerhttp.SETRECORDMAIN://子线程接收主线程消息上报交易记录
						//修改交易数据上报状态为已上报
						updategrid(msg.obj.toString());
						
						//初始化七、发送货道上传命令到子线程中
						childhand=serverhttp.obtainHandler();
		        		Message childheartmsg3=childhand.obtainMessage();
		        		childheartmsg3.what=EVServerhttp.SETHUODAOSTATUCHILD;
		        		childheartmsg3.obj=columngrid();
		        		childhand.sendMessage(childheartmsg3);
						break;	
					//获取上报货道信息返回	
					case EVServerhttp.SETERRFAILHUODAOSTATUMAIN://子线程接收主线程上报货道信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报货道信息失败","server.txt");
						break;
					case EVServerhttp.SETHUODAOSTATUMAIN://子线程接收主线程上报货道信息
						//修改数据上报状态为已上报
						updatecolumns(msg.obj.toString());
						//重新更新token的值
						if(tokenno>=80)
						{
							//处理接收到的内容,发送签到命令到子线程中
							childhand=serverhttp.obtainHandler();
				    		Message childheartmsg4=childhand.obtainMessage();
				    		childheartmsg4.what=EVServerhttp.SETCHECKCHILD;
				    		JSONObject ev=null;
				    		try {
				    			ev=new JSONObject();
				    			ev.put("vmc_no", vmc_no);
				    			ev.put("vmc_auth_code", vmc_auth_code);
				    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
				    		} catch (JSONException e) {
				    			// TODO Auto-generated catch block
				    			e.printStackTrace();
				    		}
				    		childheartmsg4.obj=ev;
				    		childhand.sendMessage(childheartmsg4);
				    		tokenno=0;
						}
						else
						{
							tokenno++;
						}
						//初始化八、返回给activity广播,初始化完成
						if(ischeck==false)
						{
							intent=new Intent();
							intent.putExtra("EVWhat", EVServerhttp.SETMAIN);
							intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
							localBroadreceiver.sendBroadcast(intent);
							ischeck=true;
							LAST_EDIT_TIME=ToolClass.getLasttime();
						}
							        		
						break;
					//上传设备信息	
					case EVServerhttp.SETERRFAILDEVSTATUMAIN://子线程接收主线程消息上传设备信息失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取设备信息失败，原因="+msg.obj.toString(),"server.txt");
						break;
					case EVServerhttp.SETDEVSTATUMAIN://子线程接收主线程消息获取设备信息
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 获取设备信息成功","server.txt");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service LAST_EDIT_TIME="+LAST_EDIT_TIME,"server.txt");
						//同步二、下载当前时间以后有更新的商品分类，商品，以及货道，同时上报各个状态
						childhand=serverhttp.obtainHandler();
		        		Message childheartmsg4=childhand.obtainMessage();
		        		childheartmsg4.what=EVServerhttp.SETCLASSCHILD;
		        		childheartmsg4.obj=LAST_EDIT_TIME;
		        		childhand.sendMessage(childheartmsg4);	
						
						break;	
					//网络故障
					case EVServerhttp.SETFAILMAIN://子线程接收主线程网络失败
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 失败，网络故障","server.txt");
						//返回给activity广播
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action与接收器相同
						localBroadreceiver.sendBroadcast(intent);	
						break;						
				}				
			}
			
		};
		//启动用户自己定义的类，启动线程
  		serverhttp=new EVServerhttp(mainhand);
  		thread=Executors.newFixedThreadPool(3);
  		thread.execute(serverhttp);	
  		//启动设备同步定时器
  		ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
  		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	if(ischeck)
	        	{
	        		//ToolClass.Log(ToolClass.INFO,"EV_SERVER","server["+Thread.currentThread().getId()+"]","server.txt");
	        		int bill_err=ToolClass.getBill_err();
					int coin_err=ToolClass.getCoin_err();
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报设备bill_err="+bill_err
							+" coin_err="+coin_err,"server.txt");				
	    			//
		        	childhand=serverhttp.obtainHandler();
		    		Message childmsg3=childhand.obtainMessage();
		    		childmsg3.what=EVServerhttp.SETDEVSTATUCHILD;
		    		JSONObject ev3=null;
		    		try {
		    			ev3=new JSONObject();
		    			ev3.put("bill_err", bill_err);
		    			ev3.put("coin_err", coin_err);	    			  			
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev3.toString(),"server.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		childmsg3.obj=ev3;
		    		childhand.sendMessage(childmsg3);
	        	}
	        	//重试签到指令
	        	else
	        	{
	        		ToolClass.Log(ToolClass.INFO,"EV_SERVER","checkretry:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code
							+"huoSet="+huoSet.toString(),"server.txt");					
					//处理接收到的内容,发送签到命令到子线程中
					//初始化一:发送签到指令
		        	childhand=serverhttp.obtainHandler();
		    		Message childmsg=childhand.obtainMessage();
		    		childmsg.what=EVServerhttp.SETCHILD;
		    		JSONObject ev=null;
		    		try {
		    			ev=new JSONObject();
		    			ev.put("vmc_no", vmc_no);
		    			ev.put("vmc_auth_code", vmc_auth_code);
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		childmsg.obj=ev;
		    		childhand.sendMessage(childmsg);
	        	}
	        } 
	    },10*60,10*60,TimeUnit.SECONDS);       // 10*60timeTask  
	}	
	
	//更新商品分类信息
	private void updatevmcClass(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		if(ToolClass.getServerVer()==0)//旧的后台
		{
			JSONArray arr1=jsonObject.getJSONArray("ProductClassList");
			for(int i=0;i<arr1.length();i++)
			{
				JSONObject object2=arr1.getJSONObject(i);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品分类CLASS_CODE="+object2.getString("CLASS_CODE")
						+"CLASS_NAME="+object2.getString("CLASS_NAME"),"server.txt");										
				// 创建InaccountDAO对象
	        	vmc_classDAO classDAO = new vmc_classDAO(EVServerService.this);
	            // 创建Tb_inaccount对象
	        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(object2.getString("CLASS_CODE"), object2.getString("CLASS_NAME"),object2.getString("LAST_EDIT_TIME"),"");
	        	classDAO.addorupdate(tb_vmc_class);// 修改
			}
		}
		else if(ToolClass.getServerVer()==1)//一期后台
		{
			JSONArray arr1=jsonObject.getJSONArray("List");
			for(int i=0;i<arr1.length();i++)
			{
				JSONObject object2=arr1.getJSONObject(i);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品分类CLASS_CODE="+object2.getString("CLASS_CODE")
						+"CLASS_NAME="+object2.getString("CLASS_NAME")+"IS_DELETE="+object2.getInt("IS_DELETE")
						+"AttImg="+object2.getString("AttImg"),"server.txt");										
				// 创建InaccountDAO对象
	        	vmc_classDAO classDAO = new vmc_classDAO(EVServerService.this);
	            // 创建Tb_inaccount对象
	        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(object2.getString("CLASS_CODE"), object2.getString("CLASS_NAME"),object2.getString("LAST_EDIT_TIME"),object2.getString("AttImg"));
	        	//删除商品分类
	        	if(object2.getInt("IS_DELETE")==1) 
	        	{
	        		classDAO.detele(tb_vmc_class);
	        	}
	        	else
	        	{
	        		classDAO.addorupdate(tb_vmc_class);// 修改
	        	}
			}
			
		}
	}
	
	//更新商品信息
	private boolean updatevmcProduct(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=jsonObject.getJSONArray("ProductList");
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品="+arr1.length()+"txt="+classrst,"server.txt");
		
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品="+i+"txt="+object2.toString(),"server.txt");
			
			String product_Class_NO=(object2.getString("product_Class_NO").isEmpty())?"0":object2.getString("product_Class_NO");
			product_Class_NO=product_Class_NO.substring(product_Class_NO.lastIndexOf(',')+1,product_Class_NO.length());
			String product_TXT=object2.getString("product_TXT");
			//用于签到完成后，更新商品信息时间段
//			if(ischeck==true) 
//			{
//				LAST_EDIT_TIME=ToolClass.getLasttime();
//			}
			// 创建InaccountDAO对象
			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
            //创建Tb_inaccount对象
			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(object2.getString("product_NO"), object2.getString("product_Name"),product_TXT,Float.parseFloat(object2.getString("market_Price")),
					Float.parseFloat(object2.getString("sales_Price")),0,object2.getString("create_Time"),object2.getString("last_Edit_Time"),object2.getString("AttImg"),"","",0,0);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","2更新商品"+i+"txt=product_NO="+tb_vmc_product.getProductID()
					+"product_Name="+tb_vmc_product.getProductName()+"product_Class_NO="+product_Class_NO
					+"AttImg="+tb_vmc_product.getAttBatch1()+"product_TXT="+tb_vmc_product.getProductDesc(),"server.txt");	
			productDAO.addorupdate(tb_vmc_product,product_Class_NO);// 修改
		}
		return true;
	}
		
	//更新货道信息
	private void updatevmcColumn(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=null;
		if(ToolClass.getServerVer()==0)//旧的后台
		{
			arr1=jsonObject.getJSONArray("PathList");
		}
		else if(ToolClass.getServerVer()==1)//一期后台
		{
			arr1=jsonObject.getJSONArray("List");
		}
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			int PATH_ID=object2.getInt("PATH_ID");
			int CABINET_NO=Integer.parseInt(object2.getString("CABINET_NO"));
			int PATH_NO=Integer.parseInt(object2.getString("PATH_NO"));
			String PATH_NOSTR=(PATH_NO<10)?("0"+String.valueOf(PATH_NO)):String.valueOf(PATH_NO);
			int PATH_REMAINING=Integer.parseInt(object2.getString("PATH_REMAINING"));
			int IS_DELETE=0;
			if(ToolClass.getServerVer()==1)//一期后台
			{
				IS_DELETE=Integer.parseInt(object2.getString("PATH_REMAINING"));
			}
			int status=0;//货道状态
			int j=0;
			//输出内容
	        Set<Map.Entry<String,Integer>> allset=huoSet.entrySet();  //实例化
	        Iterator<Map.Entry<String,Integer>> iter=allset.iterator();
	        while(iter.hasNext())
	        {
	            Map.Entry<String,Integer> me=iter.next();
				String huo=me.getKey();
				int cabid=Integer.parseInt(huo.substring(0, 1));
				int huoid=Integer.parseInt(huo.substring(1, huo.length()));
				status=me.getValue();				
			    if((CABINET_NO==cabid)&&(PATH_NO==huoid))
			    {
			    	j=1;
			    	break;
			    }
				//ToolClass.Log(ToolClass.INFO,"EV_SERVER","huo="+cabid+","+huoid+"sta="+me.getValue(),"server.txt");
	        } 			

	        //可以更新货道
			if(j==1)
			{		
				status=updatehuodaostatus(status,PATH_REMAINING);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新货道PATH_ID="+PATH_ID+"CABINET_NO="+object2.getString("CABINET_NO")
						+"PATH_NO="+PATH_NOSTR+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
						+"PATH_COUNT="+object2.getString("PATH_COUNT")+"IS_DELETE="+IS_DELETE,"server.txt");	
				// 创建InaccountDAO对象
    			vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
	            //创建Tb_inaccount对象
    			Tb_vmc_column tb_vmc_column = new Tb_vmc_column(object2.getString("CABINET_NO"), PATH_NOSTR,"",object2.getString("PRODUCT_NO"),
    					Integer.parseInt(object2.getString("PATH_COUNT")),Integer.parseInt(object2.getString("PATH_REMAINING")),
    					status,"",PATH_ID,0);  
    			if(ToolClass.getServerVer()==1)//一期后台
    			{
    				//删除货道
    				if(IS_DELETE==1)
    				{
    					columnDAO.detele(tb_vmc_column);
    				}
    			}
    			else
    			{
	    			columnDAO.addorupdateforserver(tb_vmc_column);// 添加货道信息
	    			//查看本货道对应的商品是否存在
	    			// 创建InaccountDAO对象
	    			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
	    			//创建Tb_inaccount对象
	    			Tb_vmc_product tb_vmc_product = productDAO.find(object2.getString("PRODUCT_NO"));
	    			if(tb_vmc_product==null)
	    			{
	    				ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品PRODUCT_NO="+object2.getString("PRODUCT_NO")
	    						+"不存在","server.txt");	
	    				isspempty=true;    				
	    			}
    			}
			}
			//更新货道失败
			else
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新货道失败CABINET_NO="+object2.getString("CABINET_NO")
						+"PATH_NO="+PATH_NOSTR+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
						+"PATH_COUNT="+object2.getString("PATH_COUNT"),"server.txt");										
			}			
		}
	}
	
	//更新货道状态信息
	//huostate从货道板上得到的货道状态，upremain从服务端下载的货道存货数量
	private int updatehuodaostatus(int huostate,int upremain)
	{
		int huostatus=0;
		if(huostate==0)//货道故障
			huostatus=2;
		else if(huostate==1)//货道正常
		{
			if(upremain>0)
	    		huostatus=1;
	    	else                             //缺货
	    		huostatus=3;	
		}
		return huostatus;
	}
	
	//获取需要上报的报表
	private JSONArray grid()
	{		
		//总支付订单
		String[] ordereID;// 订单ID[pk]
		String[] payType;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
		String[] payStatus;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
		String[] RealStatus;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
		String[] smallNote;// 纸币金额
		String[] smallConi;// 硬币金额
		String[] smallAmount;// 现金投入金额
		String[] smallCard;// 非现金支付金额
		String[] shouldPay;// 商品总金额
		String[] shouldNo;// 商品总数量
		String[] realNote;// 纸币退币金额
		String[] realCoin;// 硬币退币金额
		String[] realAmount;// 现金退币金额
		String[] debtAmount;// 欠款金额
		String[] realCard;// 非现金退币金额
		String[] payTime;//支付时间
			//详细支付订单
		String[] productID;//商品id
		String[] cabID;//货柜号
		String[] columnID;//货道号
		    //商品信息
		String[] productName;// 商品全名
		String[] salesPrice;// 优惠价,如”20.00”
		
		//数字类型订单信息
	    //总支付订单
		int[] payTypevalue;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	    int[] payStatusvalue;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
	    int[] RealStatusvalue;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	  	double[] smallNotevalue;// 纸币金额
		double[] smallConivalue;// 硬币金额
		double[] smallAmountvalue;// 现金投入金额
		double[] smallCardvalue;// 非现金支付金额
		double[] shouldPayvalue;// 商品总金额
		double[] shouldNovalue;// 商品总数量
		double[] realNotevalue;// 纸币退币金额
		double[] realCoinvalue;// 硬币退币金额
		double[] realAmountvalue;// 现金退币金额
		double[] debtAmountvalue;// 欠款金额
		double[] realCardvalue;// 非现金退币金额
	  	//商品信息
		double[] salesPricevalue;// 优惠价,如”20.00”
		int ourdercount=0;//记录的数量
		JSONArray array=new JSONArray();
		
		
		Vmc_OrderAdapter vmc_OrderAdapter=new Vmc_OrderAdapter();
		vmc_OrderAdapter.grid(EVServerService.this);
		//总支付订单
		ordereID = vmc_OrderAdapter.getOrdereID();// 订单ID[pk]
		payType = vmc_OrderAdapter.getPayType();// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
		payStatus = vmc_OrderAdapter.getPayStatus();// 订单状态0出货成功，1出货失败，2支付失败，3未支付
		RealStatus = vmc_OrderAdapter.getRealStatus();// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
		smallNote = vmc_OrderAdapter.getSmallNote();// 纸币金额
		smallConi = vmc_OrderAdapter.getSmallConi();// 硬币金额
		smallAmount = vmc_OrderAdapter.getSmallAmount();// 现金投入金额
		smallCard = vmc_OrderAdapter.getSmallCard();// 非现金支付金额
		shouldPay = vmc_OrderAdapter.getShouldPay();// 商品总金额
		shouldNo = vmc_OrderAdapter.getShouldNo();// 商品总数量
		realNote = vmc_OrderAdapter.getRealNote();// 纸币退币金额
		realCoin = vmc_OrderAdapter.getRealCoin();// 硬币退币金额
		realAmount = vmc_OrderAdapter.getRealAmount();// 现金退币金额
		debtAmount = vmc_OrderAdapter.getDebtAmount();// 欠款金额
		realCard = vmc_OrderAdapter.getRealCard();// 非现金退币金额
		payTime = vmc_OrderAdapter.getPayTime();//支付时间
		//详细支付订单
		productID = vmc_OrderAdapter.getProductID();//商品id
		cabID = vmc_OrderAdapter.getCabID();//货柜号
	    columnID = vmc_OrderAdapter.getColumnID();//货道号
	    //商品信息
	    productName = vmc_OrderAdapter.getProductName();// 商品全名
	    salesPrice = vmc_OrderAdapter.getSalesPrice();// 优惠价,如”20.00”
	    
	    //数字类型订单信息
	    payTypevalue= vmc_OrderAdapter.getPayTypevalue();// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
		payStatusvalue = vmc_OrderAdapter.getPayStatusvalue();// 订单状态0出货成功，1出货失败，2支付失败，3未支付
		RealStatusvalue = vmc_OrderAdapter.getRealStatusvalue();// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	    smallNotevalue= vmc_OrderAdapter.getSmallNotevalue();// 纸币金额
	    smallConivalue= vmc_OrderAdapter.getSmallConivalue();// 硬币金额
	    smallAmountvalue= vmc_OrderAdapter.getSmallAmountvalue();// 现金投入金额
	    smallCardvalue= vmc_OrderAdapter.getSmallCardvalue();// 非现金支付金额
	    shouldPayvalue= vmc_OrderAdapter.getShouldPayvalue();// 商品总金额
	    shouldNovalue= vmc_OrderAdapter.getShouldNovalue();// 商品总数量
	    realNotevalue= vmc_OrderAdapter.getRealNotevalue();// 纸币退币金额
	    realCoinvalue= vmc_OrderAdapter.getRealCoinvalue();// 硬币退币金额
	    realAmountvalue= vmc_OrderAdapter.getRealAmountvalue();// 现金退币金额
	    debtAmountvalue= vmc_OrderAdapter.getDebtAmountvalue();// 欠款金额
	    realCardvalue= vmc_OrderAdapter.getRealCardvalue();// 非现金退币金额
	    //商品信息
	    salesPricevalue= vmc_OrderAdapter.getSalesPricevalue();// 优惠价,如”20.00”
	    ourdercount=vmc_OrderAdapter.getCount();
	    
	    int orderStatus=0;//1未支付,2出货成功,3出货未完成
	    int payStatue=0;//0未付款,1正在付款,2付款完成,3付款失败
	    int payTyp=0;//0现金,1支付宝声波,2银联,3支付宝二维码,4微信
	    int actualQuantity=0;//实际出货,1成功,0失败
	    String RefundAmount="";//找零金额
	    int Status=0;//0：未退款；1：正在退款；2：退款成功；3：退款失败'
	    try {
	    	for(int x=0;x<vmc_OrderAdapter.getCount();x++)	
			{
		    	// 订单状态0出货成功，1出货失败，2支付失败，3未支付
		    	if(payStatusvalue[x]==2||payStatusvalue[x]==3)
				{
		    		orderStatus=1;
		    		payStatue=3;
		    		actualQuantity=0;
				}
				else if(payStatusvalue[x]==0)
				{
					orderStatus=2;
					payStatue=2;
					actualQuantity=1;
				}
				else if(payStatusvalue[x]==1)
				{
					orderStatus=3;
					payStatue=2;
					actualQuantity=0;
				}
		    	// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
		    	if(payTypevalue[x]==0)
				{
		    		payTyp=0;
				}
				else if(payTypevalue[x]==1)
				{
					payTyp=2;
				}
				else if(payTypevalue[x]==2)
				{
					payTyp=1;
				}
				else if(payTypevalue[x]==3)
				{
					payTyp=3;
				}
				else if(payTypevalue[x]==4)
				{
					payTyp=4;
				}
		    	
		    	if(RealStatusvalue[x]==0)
		    	{
		    		Status=0;
		    	}
		    	else if(RealStatusvalue[x]==1)
		    	{
		    		Status=2;
		    	}
		    	else if(RealStatusvalue[x]==2||RealStatusvalue[x]==3)
		    	{
		    		Status=3;
		    	}
		    	RefundAmount=String.valueOf(realAmountvalue[x]+realCardvalue[x]);
		    	//ToolClass.Log(ToolClass.INFO,"EV_SERVER","销售payStatus="+payStatusvalue[x]+"payType="+payTypevalue[x],"server.txt");
				
		    	ToolClass.Log(ToolClass.INFO,"EV_SERVER","销售orderNo="+ordereID[x]+"orderTime="+ToolClass.getStrtime(payTime[x])+"orderStatus="+orderStatus+"payStatus="
				+payStatue+"payType="+payTyp+"shouldPay="+shouldPay[x]+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productID[x]+"quantity="+1+
				"actualQuantity="+actualQuantity+"customerPrice="+salesPrice[x]+"productName="+productName[x],"server.txt");			
		    	JSONObject object=new JSONObject();
		    	object.put("orderNo", ordereID[x]);
		    	object.put("orderTime", ToolClass.getStrtime(payTime[x]));
		    	object.put("orderStatus", orderStatus);
		    	object.put("payStatus", payStatue);
		    	object.put("payType", payTyp);
		    	object.put("shouldPay", shouldPay[x]);
		    	object.put("RefundAmount", RefundAmount);
		    	object.put("Status", Status);
		    	object.put("productNo", productID[x]);		    	
		    	object.put("quantity", 1);
		    	object.put("actualQuantity", actualQuantity);
		    	object.put("customerPrice", salesPrice[x]);
		    	object.put("productName", productName[x]);	
		    	
		    	array.put(object);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    return array;
	}
	
	
	//修改数据上报状态为已上报
	private void updategrid(String str)
	{	
		// 创建InaccountDAO对象
  		vmc_orderDAO orderDAO = new vmc_orderDAO(EVServerService.this);
  		
		JSONArray json=null;
		try {
			json=new JSONArray(str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报交易记录成功="+json.toString(),"server.txt");
		for(int i=0;i<json.length();i++)
		{
			JSONObject object2=null;
			String orderno=null;
			try {
				object2 = json.getJSONObject(i);
				orderno=object2.getString("orderno");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","上报交易记录="+orderno
					,"server.txt");
			orderDAO.update(orderno);
		}		  	
	}
	
	//获取需要上报的货道
	private JSONArray columngrid()
	{				
		int ourdercount=0;//记录的数量
		JSONArray array=new JSONArray();
		
		// 创建InaccountDAO对象
		vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
		List<Tb_vmc_column> listinfos=columnDAO.getScrollPay();
		String[] pathIDs= new String[listinfos.size()];
		String[] cabinetNumbers= new String[listinfos.size()];
		String[] pathNames= new String[listinfos.size()];
		String[] productIDs= new String[listinfos.size()];
		String[] productNums= new String[listinfos.size()];
		String[] pathCounts= new String[listinfos.size()];
		String[] pathStatuss= new String[listinfos.size()];
		String[] pathRemainings= new String[listinfos.size()];
		String[] lastedittimes= new String[listinfos.size()];
		String[] isdisables= new String[listinfos.size()];
		String[] isupload= new String[listinfos.size()];
		int x=0;
		try {
			// 遍历List泛型集合
	  	    for (Tb_vmc_column tb_inaccount : listinfos) 
	  	    {
	  	    	//总支付订单
	  	    	pathIDs[x]= String.valueOf(tb_inaccount.getPath_id());
	  	    	cabinetNumbers[x]= tb_inaccount.getCabineID();
	  	    	String PATH_NOSTR=String.valueOf(Integer.parseInt(tb_inaccount.getColumnID()));
	  			pathNames[x]= PATH_NOSTR;
	  			productIDs[x]= tb_inaccount.getCabineID();
	  			productNums[x]= tb_inaccount.getProductID();
	  			pathCounts[x]= String.valueOf(tb_inaccount.getPathCount());
	  			pathStatuss[x]= String.valueOf(tb_inaccount.getColumnStatus());
	  			pathRemainings[x]= String.valueOf(tb_inaccount.getPathRemain());
	  			lastedittimes[x]= tb_inaccount.getLasttime();
	  			isdisables[x]= "0";
	  			isupload[x] = String.valueOf(tb_inaccount.getIsupload());
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","需传货道pathID="+pathIDs[x]+"cabinetNumber="+cabinetNumbers[x]+"pathName="+pathNames[x]+"productID="
	  					+productIDs[x]+"productNum="+productNums[x]+"pathCount="+pathCounts[x]+"pathStatus="+pathStatuss[x]+"pathRemaining="+pathRemainings[x]+"lastedittime="+ToolClass.getStrtime(lastedittimes[x])+
	  					"isdisable="+isdisables[x]+"isupload="+isupload[x],"server.txt");			
		    	JSONObject object=new JSONObject();
		    	object.put("pathID", pathIDs[x]);
		    	object.put("cabinetNumber", cabinetNumbers[x]);
		    	object.put("pathName", pathNames[x]);	    	
		    	object.put("productID", productIDs[x]);
		    	object.put("productNum", productNums[x]);
		    	object.put("pathCount", pathCounts[x]);
		    	object.put("pathStatus", pathStatuss[x]);
		    	object.put("pathRemaining", pathRemainings[x]);
		    	object.put("lastedittime", ToolClass.getStrtime(lastedittimes[x]));		    	
		    	object.put("isdisable", isdisables[x]);
		    	object.put("isupload", isupload[x]);
		    	array.put(object);
	  			x++;  			
	  	    }		
		} catch (Exception e) {
			// TODO: handle exception
		}		
	    return array;
	}
	
	//修改货道上报状态为已上报
	private void updatecolumns(String str)
	{	
		// 创建InaccountDAO对象
  		vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
  		
		JSONArray json=null;
		try {
			json=new JSONArray(str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service 上报货道成功="+json.toString(),"server.txt");
		for(int i=0;i<json.length();i++)
		{
			JSONObject object2=null;
			String cabinetNumber=null;
			String pathName=null;
			try {
				object2 = json.getJSONObject(i);
				cabinetNumber=object2.getString("cabinetNumber");
				int PATH_NO=Integer.parseInt(object2.getString("pathName"));
				String PATH_NOSTR=(PATH_NO<10)?("0"+String.valueOf(PATH_NO)):String.valueOf(PATH_NO);
				pathName=PATH_NOSTR;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","上报货道cabinetNumber="+cabinetNumber
					+"pathName="+pathName,"server.txt");
			//用于签到完成后，更新商品信息时间段
			if(ischeck==true)
			{
				LAST_EDIT_TIME=ToolClass.getLasttime();
			}
			columnDAO.updatecol(cabinetNumber,pathName);
		}		  	
	}

}
