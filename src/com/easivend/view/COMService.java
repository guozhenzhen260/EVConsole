/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           COMService.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        与串口设备连接的服务      
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.view;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.view.EVServerService.ActivityReceiver;

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

public class COMService extends Service {
	public static final int EV_CHECKALLCHILD= 1;	//查询全部柜子状态
	public static final int EV_CHECKALLMAIN	= 2;	//查询全部柜子状态返回
	public static final int EV_CHECKCHILD	= 3;	//货道查询	
	public static final int EV_CHUHUOCHILD	= 4;	//货道出货	
	public static final int EV_LIGHTCHILD	= 5;	//照明	
	public static final int EV_COOLCHILD	= 6;	//制冷	
	public static final int EV_HOTCHILD		= 7;	//加热
	
	public static final int EV_CHECKMAIN	= 8;	//货道查询	返回
	public static final int EV_OPTMAIN  	= 9;	//货道操作返回
	
	ActivityReceiver receiver;
	LocalBroadcastManager localBroadreceiver;
	private ExecutorService thread=null;
    private Handler mainhand=null,childhand=null; 
    COMThread comserial=null;
    Map<String,Integer> huoSet=new LinkedHashMap<String,Integer>();
    private String[] cabinetID = null;//用来分离出货柜编号    
    private int huom = 0;// 定义一个开始标识
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
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
			//查询全部柜子状态
			case EV_CHECKALLCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 货道查询全部","com.txt");
//				vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
//			    // 1.获取所有柜号
//			    List<Tb_vmc_cabinet> listinfos = cabinetDAO.getScrollData();
//			    cabinetID = new String[listinfos.size()];// 设置字符串数组的长度
//			    // 遍历List泛型集合
//			    for (Tb_vmc_cabinet tb_inaccount : listinfos) 
//			    {
//			        cabinetID[huom] = tb_inaccount.getCabID();
//			        ToolClass.Log(ToolClass.INFO,"EV_COM","获取柜号="+cabinetID[huom],"com.txt");
//				    huom++;// 标识加1
//			    }
//			    huom=0;
//			    if(listinfos.size()==0)
//			    {
//			    	//返回给activity广播
//					Intent recintent=new Intent();
//					recintent.putExtra("EVWhat", COMService.EV_CHECKALLMAIN);						
//					//传递数据
//			        SerializableMap myMap=new SerializableMap();
//			        myMap.setMap(huoSet);//将map数据添加到封装的myMap<span></span>中
//			        Bundle bundlerec=new Bundle();
//			        bundlerec.putSerializable("result", myMap);
//			        recintent.putExtras(bundlerec);
//			        recintent.setAction("android.intent.action.comrec");//action与接收器相同
//					sendBroadcast(recintent);
//			    }
//			    else
//				{
//			    	childhand=comserial.obtainHandler();
//	        		Message childrec=childhand.obtainMessage();
//	        		//查找货道类型
//	        		vmc_cabinetDAO cabinetDAOrec = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
//	        		// 获取所有收入信息，并存储到List泛型集合中
//	        	    Tb_vmc_cabinet listinfosrec = cabinetDAOrec.findScrollData(cabinetID[huom]);
//	        		//格子柜
//	        	    if(listinfosrec.getCabType()==5)
//	        		{
//	        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 格子柜查询="+cabinetID[huom],"com.txt");
//	        			childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
//	        		}
//	        		else
//	        		{
//	        			
//	        		}
//	        		JSONObject evrec=null;
//		    		try {
//		    			evrec=new JSONObject();
//		    			evrec.put("cabinet", cabinetID[huom]);	    			  			
//		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
//		    		} catch (JSONException e) {
//		    			// TODO Auto-generated catch block
//		    			e.printStackTrace();
//		    		}
//		    		childrec.obj=evrec;
//	        		childhand.sendMessage(childrec);	        	
//				}	
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	childhand=comserial.obtainHandler();
        		Message childrec=childhand.obtainMessage();
        		childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
	    		childrec.obj="";
        		childhand.sendMessage(childrec);
				
        		//返回给activity广播
				Intent recintent=new Intent();
				recintent.putExtra("EVWhat", COMService.EV_CHECKALLMAIN);						
				recintent.setAction("android.intent.action.comrec");//action与接收器相同
				localBroadreceiver.sendBroadcast(recintent);

				break;    
			//货道查询	
			case EV_CHECKCHILD:
				
				childhand=comserial.obtainHandler();
        		Message child2=childhand.obtainMessage();
        		//查找货道类型
        		vmc_cabinetDAO cabinetDAO2 = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
        	    // 获取所有收入信息，并存储到List泛型集合中
        	    Tb_vmc_cabinet listinfos2 = cabinetDAO2.findScrollData(String.valueOf(bundle.getInt("cabinet")));
        		//格子柜
        	    if(listinfos2.getCabType()==5)
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 格子柜查询="+String.valueOf(bundle.getInt("cabinet")),"com.txt");
        			child2.what=COMThread.EV_BENTO_CHECKCHILD;
        		}
        		else
        		{
        			
        		}
        		JSONObject ev2=null;
	    		try {
	    			ev2=new JSONObject();
	    			ev2.put("cabinet", bundle.getInt("cabinet"));	    			  			
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev2.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child2.obj=ev2;
        		childhand.sendMessage(child2);
				break;
			//货道出货	
			case EV_CHUHUOCHILD:		
				Message child3=childhand.obtainMessage();
				//查找货道类型
        		vmc_cabinetDAO cabinetDAO3 = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
        	    // 获取所有收入信息，并存储到List泛型集合中
        	    Tb_vmc_cabinet listinfos3 = cabinetDAO3.findScrollData(String.valueOf(bundle.getInt("cabinet")));
        		//格子柜
        	    if(listinfos3.getCabType()==5)
        		{
        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 格子出货","com.txt");
    				child3.what=COMThread.EV_BENTO_OPENCHILD;
        		}
        		else
        		{
        			
        		}
        		JSONObject ev3=null;
	    		try {
	    			ev3=new JSONObject();
	    			ev3.put("cabinet", bundle.getInt("cabinet"));	
	    			ev3.put("column", bundle.getInt("column"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev3.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child3.obj=ev3;
        		childhand.sendMessage(child3);	
				break;
			//快递柜照明	
			case EV_LIGHTCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 照明","com.txt");
				Message child4=childhand.obtainMessage();
				child4.what=COMThread.EV_BENTO_LIGHTCHILD;
        		JSONObject ev4=null;
	    		try {
	    			ev4=new JSONObject();
	    			ev4.put("cabinet", bundle.getInt("cabinet"));	
	    			ev4.put("opt", bundle.getInt("opt"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev4.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child4.obj=ev4;
        		childhand.sendMessage(child4);	
				break;
			//快递柜制冷	
			case EV_COOLCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 制冷","com.txt");
				Message child5=childhand.obtainMessage();
				child5.what=COMThread.EV_BENTO_COOLCHILD;
        		JSONObject ev5=null;
	    		try {
	    			ev5=new JSONObject();
	    			ev5.put("cabinet", bundle.getInt("cabinet"));	
	    			ev5.put("opt", bundle.getInt("opt"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev5.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child5.obj=ev5;
        		childhand.sendMessage(child5);	
				break;
			//快递柜加热	
			case EV_HOTCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 加热","com.txt");
				Message child6=childhand.obtainMessage();
				child6.what=COMThread.EV_BENTO_HOTCHILD;
        		JSONObject ev6=null;
	    		try {
	    			ev6=new JSONObject();
	    			ev6.put("cabinet", bundle.getInt("cabinet"));	
	    			ev6.put("opt", bundle.getInt("opt"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev6.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child6.obj=ev6;
        		childhand.sendMessage(child6);	
				break;	
			}			
		}

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMService create","com.txt");
		super.onCreate();
		//9.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.comsend");
		localBroadreceiver.registerReceiver(receiver,filter);	
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMService start","com.txt");
		//***********************
		//线程进行vmserver操作
		//***********************
		mainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					//全部查询
					case COMThread.EV_BENTO_CHECKALLMAIN://子线程接收主线程消息签到完成
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 货道全部查询返回="+msg.obj,"com.txt");
//						String tempno=null; 
//						Map<String, Object> Set= (Map<String, Object>) msg.obj;
//						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 货道查询返回=2","com.txt");
//						//输出内容
//				        Set<Entry<String, Object>> allmap2=Set.entrySet();  //实例化
//				        Iterator<Entry<String, Object>> iter2=allmap2.iterator();
//				        while(iter2.hasNext())
//				        {
//				            Entry<String, Object> me=iter2.next();
//				            if(
//				               (me.getKey().equals("cabinet")!=true)&&(me.getKey().equals("cool")!=true)
//				               &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
//				            )   
//				            {
//				            	if(Integer.parseInt(me.getKey())<10)
//				    				tempno="0"+me.getKey();
//				    			else 
//				    				tempno=me.getKey();
//				            	
//				            	huoSet.put(cabinetID[huom]+tempno,(Integer)me.getValue());
//				            }
//				        } 
//				        ToolClass.Log(ToolClass.INFO,"EV_COM","COMService<<"+huoSet.size()+"货道状态:"+huoSet.toString(),"com.txt");	
//				        huom++;
//				        //2.继续获取所有货道号
//				        if(huom<cabinetID.length)
//				        {					        	
//				        	childhand=comserial.obtainHandler();
//			        		Message childrec=childhand.obtainMessage();
//			        		//查找货道类型
//			        		vmc_cabinetDAO cabinetDAOrec = new vmc_cabinetDAO(COMService.this);// 创建InaccountDAO对象
//			        		// 获取所有收入信息，并存储到List泛型集合中
//			        	    Tb_vmc_cabinet listinfosrec = cabinetDAOrec.findScrollData(cabinetID[huom]);
//			        		//格子柜
//			        	    if(listinfosrec.getCabType()==5)
//			        		{
//			        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 格子柜查询="+cabinetID[huom],"com.txt");
//			        			childrec.what=COMThread.EV_BENTO_CHECKCHILD;
//			        		}
//			        		else
//			        		{
//			        			
//			        		}
//			        		JSONObject evrec=null;
//				    		try {
//				    			evrec=new JSONObject();
//				    			evrec.put("cabinet", cabinetID[huom]);	    			  			
//				    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
//				    		} catch (JSONException e) {
//				    			// TODO Auto-generated catch block
//				    			e.printStackTrace();
//				    		}
//				    		childrec.obj=evrec;
//			        		childhand.sendMessage(childrec);
//				        }
//				        else
//				        {
//				        	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 格子柜连接完成","com.txt");
//				        	//返回给activity广播
//							Intent recintent=new Intent();
//							recintent.putExtra("EVWhat", COMService.EV_CHECKALLMAIN);						
//							//传递数据
//					        SerializableMap myMap=new SerializableMap();
//					        myMap.setMap(huoSet);//将map数据添加到封装的myMap<span></span>中
//					        Bundle bundle=new Bundle();
//					        bundle.putSerializable("result", myMap);
//					        recintent.putExtras(bundle);
//							recintent.setAction("android.intent.action.comrec");//action与接收器相同
//							sendBroadcast(recintent);
//						}												
						break;
					//查询
					case COMThread.EV_BENTO_CHECKMAIN://子线程接收主线程消息签到完成
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 货道查询返回="+msg.obj,"com.txt");
						//返回给activity广播
						Intent recintent=new Intent();
						recintent.putExtra("EVWhat", COMService.EV_CHECKMAIN);						
						//传递数据
				        SerializableMap myMap=new SerializableMap();
				        myMap.setMap((Map<String, Integer>) msg.obj);//将map数据添加到封装的myMap<span></span>中
				        Bundle bundle=new Bundle();
				        bundle.putSerializable("result", myMap);
				        recintent.putExtras(bundle);
						recintent.setAction("android.intent.action.comrec");//action与接收器相同
						localBroadreceiver.sendBroadcast(recintent);						
						break;	
					//操作完成	
					case COMThread.EV_BENTO_OPTMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService 货道操作="+msg.obj,"com.txt");	
						//返回给activity广播
						Intent recintent2=new Intent();
						recintent2.putExtra("EVWhat", COMService.EV_OPTMAIN);						
						//传递数据
				        SerializableMap myMap2=new SerializableMap();
				        myMap2.setMap((Map<String, Integer>) msg.obj);//将map数据添加到封装的myMap<span></span>中
				        Bundle bundle2=new Bundle();
				        bundle2.putSerializable("result", myMap2);
				        recintent2.putExtras(bundle2);
						recintent2.setAction("android.intent.action.comrec");//action与接收器相同
						localBroadreceiver.sendBroadcast(recintent2);
						break;
				}				
			}
			
		};
		//启动用户自己定义的类，启动线程
		comserial=new COMThread(mainhand);
  		thread=Executors.newFixedThreadPool(3);
  		thread.execute(comserial);	
	}
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMService destroy","com.txt");
		EVprotocol.EVPortRelease(ToolClass.getBentcom_id());
		//解除注册接收器
		localBroadreceiver.unregisterReceiver(receiver);
		super.onDestroy();
	}
	

}
