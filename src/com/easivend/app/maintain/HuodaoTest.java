/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           HuodaoTest.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        货道测试页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_class;
import com.easivend.model.Tb_vmc_column;
import com.easivend.common.HuoPictureAdapter;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_CabinetAdapter;
import com.easivend.common.Vmc_ClassAdapter;
import com.easivend.common.Vmc_HuoAdapter;
import com.easivend.common.Vmc_ProductAdapter;
import com.example.evconsole.R;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.Switch;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class HuodaoTest extends TabActivity 
{
	private TabHost mytabhost = null;
	private ProgressBar barhuomanager=null;
	private int[] layres=new int[]{R.id.tab_huodaomanager,R.id.tab_huodaotest,R.id.tab_huodaoset};//内嵌布局文件的id
	private TextView txthuosetrst=null;
	private int con=1;//查询连接次数
	private int ishuoquery=0;//是否正在查询1,正在查询,0查询完成
	Timer timer = new Timer(); 
	private Button btnhuosetadd=null,btnhuosetdel=null,btnhuosetbu=null,btnhuosetexit=null;
	private Spinner spinhuosetCab=null,spinhuotestCab=null,spinhuopeiCab=null;
	private String[] cabinetID=null;//用来分离出货柜编号
	private int[] cabinetType = null;//用来分离出货柜类型
	private int cabinetsetvar=0,cabinetTypesetvar=0;
	Map<String, Integer> huoSet= new LinkedHashMap<String,Integer>();
	private int huonum=0;//本柜货道数量
	private int huonno=0;//循环出货第几个格子了
	// 定义货道列表
	Vmc_HuoAdapter huoAdapter=null;
	GridView gvhuodao=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	
	private int device=0;//出货柜号		
	private int status=0;//出货结果
	private int hdid=0;//货道id
	private int cool=0;//是否支持制冷 	 	1:支持 0:不支持
	private int hot=0;//是否支持加热  		1:支持 0:不支持
	private int light=0;//是否支持照明  	1:支持 0:不支持
	private TextView txthuorst=null,txthuotestrst=null;
	private Button btnhuochu=null,btnhuochuall=null;// 创建Button对象“出货”
	private Button btnhuocancel=null;// 创建Button对象“重置”
	private Button btnhuoexit=null;// 创建Button对象“退出”
	private EditText edtcolumn=null;
	private TextView txtlight=null,txtcold=null,txthot=null;
	private Switch switchlight = null,switcold = null,switchhot = null;
	private int cabinetvar=0,cabinetTypevar=0;
	//货道配置页面
	private int cabinetpeivar=0,cabinetTypepeivar=0;
	private Switch btnhuosetc1=null,btnhuosetc2=null,btnhuosetc3=null,btnhuosetc4=null,
			btnhuosetc5=null,btnhuosetc6=null,btnhuosetc7=null,btnhuosetc8=null,
			btnhuoset11=null,btnhuoset12=null,btnhuoset13=null,btnhuoset14=null,btnhuoset15=null,
			btnhuoset16=null,btnhuoset17=null,btnhuoset18=null,btnhuoset19=null,btnhuoset110=null,
			btnhuoset21=null,btnhuoset22=null,btnhuoset23=null,btnhuoset24=null,btnhuoset25=null,
			btnhuoset26=null,btnhuoset27=null,btnhuoset28=null,btnhuoset29=null,btnhuoset210=null,
			btnhuoset31=null,btnhuoset32=null,btnhuoset33=null,btnhuoset34=null,btnhuoset35=null,
			btnhuoset36=null,btnhuoset37=null,btnhuoset38=null,btnhuoset39=null,btnhuoset310=null,
			btnhuoset41=null,btnhuoset42=null,btnhuoset43=null,btnhuoset44=null,btnhuoset45=null,
			btnhuoset46=null,btnhuoset47=null,btnhuoset48=null,btnhuoset49=null,btnhuoset410=null,
			btnhuoset51=null,btnhuoset52=null,btnhuoset53=null,btnhuoset54=null,btnhuoset55=null,
			btnhuoset56=null,btnhuoset57=null,btnhuoset58=null,btnhuoset59=null,btnhuoset510=null,
			btnhuoset61=null,btnhuoset62=null,btnhuoset63=null,btnhuoset64=null,btnhuoset65=null,
			btnhuoset66=null,btnhuoset67=null,btnhuoset68=null,btnhuoset69=null,btnhuoset610=null,
			btnhuoset71=null,btnhuoset72=null,btnhuoset73=null,btnhuoset74=null,btnhuoset75=null,
			btnhuoset76=null,btnhuoset77=null,btnhuoset78=null,btnhuoset79=null,btnhuoset710=null,
			btnhuoset81=null,btnhuoset82=null,btnhuoset83=null,btnhuoset84=null,btnhuoset85=null,
			btnhuoset86=null,btnhuoset87=null,btnhuoset88=null,btnhuoset89=null,btnhuoset810=null;
	private Button btnhuosetsethuo=null,btnhuosetclose=null;							
			
	private Handler myhHandler=null;
	//EVprotocolAPI ev=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.huodao);// 设置布局文件
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.huodao, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabhuodaomana=this.mytabhost.newTabSpec("tab0");
        myTabhuodaomana.setIndicator("货道设置");
        myTabhuodaomana.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabhuodaomana); 
    	
    	TabSpec myTabhuodaotest=this.mytabhost.newTabSpec("tab1");
    	myTabhuodaotest.setIndicator("货道测试");
    	myTabhuodaotest.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabhuodaotest);
    	
    	TabSpec myTabhuodaoset=this.mytabhost.newTabSpec("tab2");
    	myTabhuodaoset.setIndicator("货道配置");
    	myTabhuodaoset.setContent(this.layres[2]);
    	this.mytabhost.addTab(myTabhuodaoset); 
    	
    	//注册出货监听器
  	    EVprotocolAPI.setCallBack(new JNIInterface() {
			
			@Override
			public void jniCallback(Map<String, Object> allSet) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao货道相关","log.txt");
				Map<String, Object> Set= allSet;
				int jnirst=(Integer)Set.get("EV_TYPE");
				switch (jnirst)
				{
					case EVprotocolAPI.EV_TRADE_RPT://接收子线程消息
//						device=allSet.get("device");//出货柜号
//						status=allSet.get("status");//出货结果
//						hdid=allSet.get("hdid");//货道id
//						hdtype=allSet.get("type");//出货类型
//						cost=ToolClass.MoneyRec(allSet.get("cost"));//扣钱
//						totalvalue=ToolClass.MoneyRec(allSet.get("totalvalue"));//剩余金额
//						huodao=allSet.get("huodao");//剩余存货数量
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
//								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");	
//						
//						txthuorst.setText("device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
//								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");
//						sethuorst(status);
						break;
					case EVprotocolAPI.EV_COLUMN_RPT://接收子线程消息
//						huoSet.clear();
//						//输出内容
//				        Set<Entry<String, Integer>> allmap=Set.entrySet();  //实例化
//				        Iterator<Entry<String, Integer>> iter=allmap.iterator();
//				        while(iter.hasNext())
//				        {
//				            Entry<String, Integer> me=iter.next();
//				            if(me.getKey().equals("EV_TYPE")!=true)
//				            	huoSet.put(me.getKey(), me.getValue());
//				        } 
//						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道状态:"+huoSet.toString());	
//						showhuodao();						
						break;
					case EVprotocolAPI.EV_BENTO_CHECK://格子柜查询
						ishuoquery=0;
						String tempno=null;
						
						cool=(Integer)Set.get("cool");
						hot=(Integer)Set.get("hot");
						light=(Integer)Set.get("light");
						ToolClass.Log(ToolClass.INFO,"EV_JNI","API<<货道cool:"+cool+",hot="+hot+",light="+light,"log.txt");
						if(light>0)
						{
							txtlight.setText("支持");
							switchlight.setEnabled(true);
							
						}
						else
						{
							txtlight.setText("不支持");
							switchlight.setEnabled(false);
						}
						if(cool>0)
						{
							txtcold.setText("支持");
							switcold.setEnabled(true);
						}
						else
						{
							txtcold.setText("不支持");
							switcold.setEnabled(false);
						}
						if(hot>0)
						{
							txthot.setText("支持");
							switchhot.setEnabled(true);
						}
						else
						{
							txthot.setText("不支持");
							switchhot.setEnabled(false);
						}
						
						huoSet.clear();
						//输出内容
				        Set<Entry<String, Object>> allmap=Set.entrySet();  //实例化
				        Iterator<Entry<String, Object>> iter=allmap.iterator();
				        while(iter.hasNext())
				        {
				            Entry<String, Object> me=iter.next();
				            if(
				               (me.getKey().equals("EV_TYPE")!=true)&&(me.getKey().equals("cool")!=true)
				               &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
				            )   
				            {
				            	if(Integer.parseInt(me.getKey())<10)
				    				tempno="0"+me.getKey();
				    			else 
				    				tempno=me.getKey();
				            	
				            	huoSet.put(tempno, (Integer)me.getValue());
				            }
				        } 
				        huonum=huoSet.size();
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+huonum+"货道状态:"+huoSet.toString(),"log.txt");	
						showhuodao();						
						break;	
					case EVprotocolAPI.EV_BENTO_OPEN://格子柜出货
						device=(Integer)allSet.get("addr");//出货柜号						
						hdid=(Integer)allSet.get("box");//货道id
						status=(Integer)allSet.get("result");//出货结果
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],hdid=["+hdid+"],status=["+status+"]","log.txt");	
						
						txthuorst.setText("device=["+device+"],hdid=["+hdid+"],status=["+status+"]");
						sethuorst(status);
						//循环继续做出货操作
						if((huonno>0)&&(huonno<huonum))
						{							
							huonno++;
							//格子柜
							if(cabinetTypevar==5)
							{
								ToolClass.Log(ToolClass.INFO,"EV_JNI",
								    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
								    	+" cabType="+String.valueOf(cabinetTypevar)
								    	+" column="+huonno		    	
								    	,"log.txt");	 
								EVprotocolAPI.EV_bentoOpen(ToolClass.getBentcom_id(),cabinetvar,huonno);						
							}
						}
						else if(huonno>=huonum)
						{
							huonno=0;
						}
						break;	
					case EVprotocolAPI.EV_BENTO_LIGHT://格子柜开灯
						device=(Integer)allSet.get("addr");//柜号						
						int opt=(Integer)allSet.get("opt");//货道id
						status=(Integer)allSet.get("result");//结果
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货柜操作结果"+"device=["+device+"],opt=["+opt+"],status=["+status+"]","log.txt");	
						txthuorst.setText("device=["+device+"],opt=["+opt+"],status=["+status+"]");						
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
		}); 
    	//===============
    	//货道设置页面
    	//===============
  	    timer.schedule(task, 3*1000, 3*1000);       // timeTask 
  	    txthuosetrst=(TextView)findViewById(R.id.txthuosetrst);
  	    barhuomanager= (ProgressBar) findViewById(R.id.barhuomanager);
  	    huoAdapter=new Vmc_HuoAdapter();
  	    this.gvhuodao=(GridView) findViewById(R.id.gvhuodao); 
    	spinhuosetCab= (Spinner) findViewById(R.id.spinhuosetCab); 
    	spinhuotestCab= (Spinner) findViewById(R.id.spinhuotestCab); 
    	spinhuopeiCab= (Spinner) findViewById(R.id.spinhuopeiCab); 
    	//显示柜信息
    	showabinet();
    	this.spinhuosetCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//只有有柜号的时候，才请求加载柜内货道信息
				if(cabinetID!=null)
				{
					barhuomanager.setVisibility(View.VISIBLE); 
					cabinetsetvar=Integer.parseInt(cabinetID[arg2]); 
					cabinetTypesetvar=cabinetType[arg2]; 
					queryhuodao();					
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	//修改或添加货道对应商品
    	gvhuodao.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub cabinetID[0],
				String huo[]=huoAdapter.getHuoID();
				String huoID = huo[arg2];// 记录收入信息               
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道ID="+cabinetsetvar+huoID+"status="+huoSet.get(huoID),"log.txt");
				Intent intent = new Intent();
		    	intent.setClass(HuodaoTest.this, HuodaoSet.class);// 使用AddInaccount窗口初始化Intent
                intent.putExtra("huoID", huoID);
                intent.putExtra("cabID", String.valueOf(cabinetsetvar));
                intent.putExtra("huoStatus", String.valueOf(huoSet.get(huoID)));
		    	startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
			}// 为GridView设置项单击事件
    		
    	});
    	//添加柜
    	btnhuosetadd = (Button) findViewById(R.id.btnhuosetadd);
    	btnhuosetadd.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetAdd();
		    }
		});
    	//删除柜
    	btnhuosetdel = (Button) findViewById(R.id.btnhuosetdel);
    	btnhuosetdel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetDel();
		    }
		});
    	//本柜补货
    	btnhuosetbu = (Button) findViewById(R.id.btnhuosetbu);
    	btnhuosetbu.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	cabinetbuhuo();
		    }
		});
    	btnhuosetexit = (Button) findViewById(R.id.btnhuosetexit);
    	btnhuosetexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	timer.cancel();
		    	finish();
		    }
		});    	
    	
    	//动态设置控件高度
    	//
    	DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        int screenWidth = dm.widthPixels;          
        //窗口高度  
        int screenHeight = dm.heightPixels;      
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<屏幕"+screenWidth
				+"],["+screenHeight+"]","log.txt");	
		
    	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvhuodao.getLayoutParams(); // 取控件mGrid当前的布局参数
    	linearParams.height =  screenHeight-500;// 当控件的高强制设成75象素
    	gvhuodao.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
  	   
    	//===============
    	//出货测试页面
    	//===============
		
    	//spinhuoCab= (Spinner) findViewById(R.id.spinhuoCab); 
		txthuorst=(TextView)findViewById(R.id.txthuorst);
		txthuotestrst=(TextView)findViewById(R.id.txthuotestrst);
		btnhuochu = (Button) findViewById(R.id.btnhuochu);
		btnhuochuall = (Button) findViewById(R.id.btnhuochuall);
		btnhuocancel = (Button) findViewById(R.id.btnhuocancel);
		btnhuoexit = (Button) findViewById(R.id.btnhuoexit);
		edtcolumn = (EditText) findViewById(R.id.edtcolumn);
		txtlight = (TextView) findViewById(R.id.txtlight);		
		txtcold = (TextView) findViewById(R.id.txtcold);
		txthot = (TextView) findViewById(R.id.txthot);	
		switchlight = (Switch)findViewById(R.id.switchlight);
		switchlight.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					//格子柜
					if(cabinetTypevar==5)
					{
						EVprotocolAPI.EV_bentoLight(ToolClass.getBentcom_id(),cabinetvar,1);						
					}
					//普通柜
					else 
					{
						//rst=EVprotocolAPI.trade(cabinetvar,Integer.parseInt(edtcolumn.getText().toString()),typevar,
				    	//		ToolClass.MoneySend(price));	
					}
				}
				else 
				{
					//格子柜
					if(cabinetTypevar==5)
					{
						EVprotocolAPI.EV_bentoLight(ToolClass.getBentcom_id(),cabinetvar,0);						
					}
					//普通柜
					else 
					{
						//rst=EVprotocolAPI.trade(cabinetvar,Integer.parseInt(edtcolumn.getText().toString()),typevar,
				    	//		ToolClass.MoneySend(price));	
					}
					
				}
			}  
			
			
		}); 
		
		switcold = (Switch)findViewById(R.id.switcold);
		switcold.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
			}  
			
			
		});
		switchhot = (Switch)findViewById(R.id.switchhot);
		switchhot.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
			}  
			
			
		});
		this.spinhuotestCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//只有有柜号的时候，才请求加载柜内货道信息
				if(cabinetID!=null)
				{
					cabinetvar=Integer.parseInt(cabinetID[arg2]); 
					cabinetTypevar=cabinetType[arg2]; 
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		btnhuochu.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {		    	  
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
		    	+" cabType="+String.valueOf(cabinetTypevar)
		    	+" column="+String.valueOf(Integer.parseInt(edtcolumn.getText().toString())),"log.txt"		    	
		    	);
		    	if (edtcolumn.getText().toString().isEmpty()!=true)	
		    	{
		    		float price=0;
		    		int typevar=0;
		    		if(price>0)
		    			typevar=0;
		    		else 
		    			typevar=2;
		    		int rst=0;
		    		//格子柜
					if(cabinetTypevar==5)
					{
						EVprotocolAPI.EV_bentoOpen(ToolClass.getBentcom_id(),cabinetvar,Integer.parseInt(edtcolumn.getText().toString()));						
					}
					//普通柜
					else 
					{
						rst=EVprotocolAPI.trade(cabinetvar,Integer.parseInt(edtcolumn.getText().toString()),typevar,
				    			ToolClass.MoneySend(price));	
					}
			    	   	
		    	}
		    }
		});
		btnhuochuall.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {		    	  
		    	   		    		
	    		int rst=0;
	    		//格子柜
				if(cabinetTypevar==5)
				{
					huonno=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI",
					    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
					    	+" cabType="+String.valueOf(cabinetTypevar)
					    	+" column="+huonno,"log.txt"		    	
					    	);	 
					EVprotocolAPI.EV_bentoOpen(ToolClass.getBentcom_id(),cabinetvar,huonno);						
				}
				//普通柜
				else 
				{
//					rst=EVprotocolAPI.trade(cabinetvar,Integer.parseInt(edtcolumn.getText().toString()),typevar,
//			    			ToolClass.MoneySend(price));	
				}
		    }
		});
		btnhuocancel.setOnClickListener(new OnClickListener() {// 为重置按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	edtcolumn.setText("");// 设置金额文本框为空		    	      
		    }
		});
		btnhuoexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	timer.cancel();
		    	finish();
		    }
		});
		
		//===============
    	//货道配置页面
    	//===============		
		btnhuosetc1 = (Switch) findViewById(R.id.btnhuosetc1);
		btnhuosetc2 = (Switch) findViewById(R.id.btnhuosetc2);
		btnhuosetc3 = (Switch) findViewById(R.id.btnhuosetc3);
		btnhuosetc4 = (Switch) findViewById(R.id.btnhuosetc4);
		btnhuosetc5 = (Switch) findViewById(R.id.btnhuosetc5);
		btnhuosetc6 = (Switch) findViewById(R.id.btnhuosetc6);
		btnhuosetc7 = (Switch) findViewById(R.id.btnhuosetc7);
		btnhuosetc8 = (Switch) findViewById(R.id.btnhuosetc8);		
		btnhuoset11 = (Switch) findViewById(R.id.btnhuoset11);
		btnhuoset12 = (Switch) findViewById(R.id.btnhuoset12);
		btnhuoset13 = (Switch) findViewById(R.id.btnhuoset13);
		btnhuoset14 = (Switch) findViewById(R.id.btnhuoset14);
		btnhuoset15 = (Switch) findViewById(R.id.btnhuoset15);
		btnhuoset16 = (Switch) findViewById(R.id.btnhuoset16);
		btnhuoset17 = (Switch) findViewById(R.id.btnhuoset17);
		btnhuoset18 = (Switch) findViewById(R.id.btnhuoset18);
		btnhuoset19 = (Switch) findViewById(R.id.btnhuoset19);
		btnhuoset110 = (Switch) findViewById(R.id.btnhuoset110);
		btnhuoset21 = (Switch) findViewById(R.id.btnhuoset21);
		btnhuoset22 = (Switch) findViewById(R.id.btnhuoset22);
		btnhuoset23 = (Switch) findViewById(R.id.btnhuoset23);
		btnhuoset24 = (Switch) findViewById(R.id.btnhuoset24);
		btnhuoset25 = (Switch) findViewById(R.id.btnhuoset25);
		btnhuoset26 = (Switch) findViewById(R.id.btnhuoset26);
		btnhuoset27 = (Switch) findViewById(R.id.btnhuoset27);
		btnhuoset28 = (Switch) findViewById(R.id.btnhuoset28);
		btnhuoset29 = (Switch) findViewById(R.id.btnhuoset29);
		btnhuoset210 = (Switch) findViewById(R.id.btnhuoset210);
		btnhuoset31 = (Switch) findViewById(R.id.btnhuoset31);
		btnhuoset32 = (Switch) findViewById(R.id.btnhuoset32);
		btnhuoset33 = (Switch) findViewById(R.id.btnhuoset33);
		btnhuoset34 = (Switch) findViewById(R.id.btnhuoset34);
		btnhuoset35 = (Switch) findViewById(R.id.btnhuoset35);
		btnhuoset36 = (Switch) findViewById(R.id.btnhuoset36);
		btnhuoset37 = (Switch) findViewById(R.id.btnhuoset37);
		btnhuoset38 = (Switch) findViewById(R.id.btnhuoset38);
		btnhuoset39 = (Switch) findViewById(R.id.btnhuoset39);
		btnhuoset310 = (Switch) findViewById(R.id.btnhuoset310);
		btnhuoset41 = (Switch) findViewById(R.id.btnhuoset41);
		btnhuoset42 = (Switch) findViewById(R.id.btnhuoset42);
		btnhuoset43 = (Switch) findViewById(R.id.btnhuoset43);
		btnhuoset44 = (Switch) findViewById(R.id.btnhuoset44);
		btnhuoset45 = (Switch) findViewById(R.id.btnhuoset45);
		btnhuoset46 = (Switch) findViewById(R.id.btnhuoset46);
		btnhuoset47 = (Switch) findViewById(R.id.btnhuoset47);
		btnhuoset48 = (Switch) findViewById(R.id.btnhuoset48);
		btnhuoset49 = (Switch) findViewById(R.id.btnhuoset49);
		btnhuoset410 = (Switch) findViewById(R.id.btnhuoset410);
		btnhuoset51 = (Switch) findViewById(R.id.btnhuoset51);
		btnhuoset52 = (Switch) findViewById(R.id.btnhuoset52);
		btnhuoset53 = (Switch) findViewById(R.id.btnhuoset53);
		btnhuoset54 = (Switch) findViewById(R.id.btnhuoset54);
		btnhuoset55 = (Switch) findViewById(R.id.btnhuoset55);
		btnhuoset56 = (Switch) findViewById(R.id.btnhuoset56);
		btnhuoset57 = (Switch) findViewById(R.id.btnhuoset57);
		btnhuoset58 = (Switch) findViewById(R.id.btnhuoset58);
		btnhuoset59 = (Switch) findViewById(R.id.btnhuoset59);
		btnhuoset510 = (Switch) findViewById(R.id.btnhuoset510);
		btnhuoset61 = (Switch) findViewById(R.id.btnhuoset61);
		btnhuoset62 = (Switch) findViewById(R.id.btnhuoset62);
		btnhuoset63 = (Switch) findViewById(R.id.btnhuoset63);
		btnhuoset64 = (Switch) findViewById(R.id.btnhuoset64);
		btnhuoset65 = (Switch) findViewById(R.id.btnhuoset65);
		btnhuoset66 = (Switch) findViewById(R.id.btnhuoset66);
		btnhuoset67 = (Switch) findViewById(R.id.btnhuoset67);
		btnhuoset68 = (Switch) findViewById(R.id.btnhuoset68);
		btnhuoset69 = (Switch) findViewById(R.id.btnhuoset69);
		btnhuoset610 = (Switch) findViewById(R.id.btnhuoset610);
		btnhuoset71 = (Switch) findViewById(R.id.btnhuoset71);
		btnhuoset72 = (Switch) findViewById(R.id.btnhuoset72);
		btnhuoset73 = (Switch) findViewById(R.id.btnhuoset73);
		btnhuoset74 = (Switch) findViewById(R.id.btnhuoset74);
		btnhuoset75 = (Switch) findViewById(R.id.btnhuoset75);
		btnhuoset76 = (Switch) findViewById(R.id.btnhuoset76);
		btnhuoset77 = (Switch) findViewById(R.id.btnhuoset77);
		btnhuoset78 = (Switch) findViewById(R.id.btnhuoset78);
		btnhuoset79 = (Switch) findViewById(R.id.btnhuoset79);
		btnhuoset710 = (Switch) findViewById(R.id.btnhuoset710);
		btnhuoset81 = (Switch) findViewById(R.id.btnhuoset81);
		btnhuoset82 = (Switch) findViewById(R.id.btnhuoset82);
		btnhuoset83 = (Switch) findViewById(R.id.btnhuoset83);
		btnhuoset84 = (Switch) findViewById(R.id.btnhuoset84);
		btnhuoset85 = (Switch) findViewById(R.id.btnhuoset85);
		btnhuoset86 = (Switch) findViewById(R.id.btnhuoset86);
		btnhuoset87 = (Switch) findViewById(R.id.btnhuoset87);
		btnhuoset88 = (Switch) findViewById(R.id.btnhuoset88);
		btnhuoset89 = (Switch) findViewById(R.id.btnhuoset89);
		btnhuoset810 = (Switch) findViewById(R.id.btnhuoset810);
		
		btnhuosetc1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset11.setChecked(isChecked);
				btnhuoset12.setChecked(isChecked);
				btnhuoset13.setChecked(isChecked);
				btnhuoset14.setChecked(isChecked);
				btnhuoset15.setChecked(isChecked);
				btnhuoset16.setChecked(isChecked);
				btnhuoset17.setChecked(isChecked);
				btnhuoset18.setChecked(isChecked);
				btnhuoset19.setChecked(isChecked);
				btnhuoset110.setChecked(isChecked);
			} 
        });
		btnhuosetc2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset21.setChecked(isChecked);
				btnhuoset22.setChecked(isChecked);
				btnhuoset23.setChecked(isChecked);
				btnhuoset24.setChecked(isChecked);
				btnhuoset25.setChecked(isChecked);
				btnhuoset26.setChecked(isChecked);
				btnhuoset27.setChecked(isChecked);
				btnhuoset28.setChecked(isChecked);
				btnhuoset29.setChecked(isChecked);
				btnhuoset210.setChecked(isChecked);
			} 
        });
		btnhuosetc3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset31.setChecked(isChecked);
				btnhuoset32.setChecked(isChecked);
				btnhuoset33.setChecked(isChecked);
				btnhuoset34.setChecked(isChecked);
				btnhuoset35.setChecked(isChecked);
				btnhuoset36.setChecked(isChecked);
				btnhuoset37.setChecked(isChecked);
				btnhuoset38.setChecked(isChecked);
				btnhuoset39.setChecked(isChecked);
				btnhuoset310.setChecked(isChecked);
			} 
        });
		btnhuosetc4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset41.setChecked(isChecked);
				btnhuoset42.setChecked(isChecked);
				btnhuoset43.setChecked(isChecked);
				btnhuoset44.setChecked(isChecked);
				btnhuoset45.setChecked(isChecked);
				btnhuoset46.setChecked(isChecked);
				btnhuoset47.setChecked(isChecked);
				btnhuoset48.setChecked(isChecked);
				btnhuoset49.setChecked(isChecked);
				btnhuoset410.setChecked(isChecked);
			} 
        });
		btnhuosetc5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset51.setChecked(isChecked);
				btnhuoset52.setChecked(isChecked);
				btnhuoset53.setChecked(isChecked);
				btnhuoset54.setChecked(isChecked);
				btnhuoset55.setChecked(isChecked);
				btnhuoset56.setChecked(isChecked);
				btnhuoset57.setChecked(isChecked);
				btnhuoset58.setChecked(isChecked);
				btnhuoset59.setChecked(isChecked);
				btnhuoset510.setChecked(isChecked);
			} 
        });
		btnhuosetc6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset61.setChecked(isChecked);
				btnhuoset62.setChecked(isChecked);
				btnhuoset63.setChecked(isChecked);
				btnhuoset64.setChecked(isChecked);
				btnhuoset65.setChecked(isChecked);
				btnhuoset66.setChecked(isChecked);
				btnhuoset67.setChecked(isChecked);
				btnhuoset68.setChecked(isChecked);
				btnhuoset69.setChecked(isChecked);
				btnhuoset610.setChecked(isChecked);
			} 
        });
		btnhuosetc7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset71.setChecked(isChecked);
				btnhuoset72.setChecked(isChecked);
				btnhuoset73.setChecked(isChecked);
				btnhuoset74.setChecked(isChecked);
				btnhuoset75.setChecked(isChecked);
				btnhuoset76.setChecked(isChecked);
				btnhuoset77.setChecked(isChecked);
				btnhuoset78.setChecked(isChecked);
				btnhuoset79.setChecked(isChecked);
				btnhuoset710.setChecked(isChecked);
			} 
        });
		btnhuosetc8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnhuoset81.setChecked(isChecked);
				btnhuoset82.setChecked(isChecked);
				btnhuoset83.setChecked(isChecked);
				btnhuoset84.setChecked(isChecked);
				btnhuoset85.setChecked(isChecked);
				btnhuoset86.setChecked(isChecked);
				btnhuoset87.setChecked(isChecked);
				btnhuoset88.setChecked(isChecked);
				btnhuoset89.setChecked(isChecked);
				btnhuoset810.setChecked(isChecked);
			} 
        });
		
		this.spinhuopeiCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//只有有柜号的时候，才请求加载柜内货道信息
				if(cabinetID!=null)
				{
					cabinetpeivar=Integer.parseInt(cabinetID[arg2]); 
					cabinetTypepeivar=cabinetType[arg2]; 
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	//===============
	//货道设置页面
	//===============
	//调用倒计时定时器
	TimerTask task = new TimerTask() { 
        @Override 
        public void run() { 
  
            runOnUiThread(new Runnable() {      // UI thread 
                @Override 
                public void run() { 
                    if(ishuoquery==1)
                    {
                    	queryhuodao();
                    }
//                    //查询已经完成
//                    else if(ishuoquery==0)
//                    {
//                    	timer.cancel(); //取消掉定时器，就不会再继续查了
//                    }
                } 
            }); 
        } 
    };
	private void queryhuodao()
	{
		txthuosetrst.setText("查询次数:"+(con++));		
		ishuoquery=1;
		//格子柜
		if(cabinetTypesetvar==5)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao格子柜相关","log.txt");
			EVprotocolAPI.EV_bentoCheck(ToolClass.getBentcom_id(),cabinetsetvar);
		}
		//普通柜
		else 
		{
			EVprotocolAPI.getColumn(cabinetsetvar);
		}
	}
	//接收GoodsProSet返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==HuodaoTest.RESULT_OK)
			{
				barhuomanager.setVisibility(View.VISIBLE);  
				showhuodao();
			}			
		}
	}
	
	@Override
	protected void onDestroy() {
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
	
	//添加柜号
	private void cabinetAdd()
	{
		final String[] values=null;
		View myview=null;    
		String [] mStringArray; 
		ArrayAdapter<String> mAdapter ;
		
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(HuodaoTest.this);
		myview=factory.inflate(R.layout.selectcabinet, null);
		final EditText dialogcab=(EditText) myview.findViewById(R.id.dialogcab);
		final Spinner dialogspincabtype=(Spinner) myview.findViewById(R.id.dialogspincabtype);
		mStringArray=getResources().getStringArray(R.array.cabinet_Type);
  	    //使用自定义的ArrayAdapter
	  	mAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner,mStringArray);
	  	dialogspincabtype.setAdapter(mAdapter);// 为ListView列表设置数据源
		
		Dialog dialog = new AlertDialog.Builder(HuodaoTest.this)
		.setTitle("设置")
		.setPositiveButton("保存", new DialogInterface.OnClickListener() 	
		{
				
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub	
				
				//Toast.makeText(HuodaoTest.this, "数值="+dialogspincabtype.getSelectedItemId(), Toast.LENGTH_LONG).show();
				String no = dialogcab.getText().toString();
		    	int type = (int)dialogspincabtype.getSelectedItemId()+1;
		    	if ((no.isEmpty()!=true)&&(type!=0))
		    	{
		    		addabinet(no,type);
		    	}
		    	else
		        {
		            Toast.makeText(HuodaoTest.this, "请输入货柜编号和类型！", Toast.LENGTH_SHORT).show();
		        }
			}
		})
		.setNegativeButton("取消",  new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
    	{			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// TODO Auto-generated method stub				
			}
    	})
		.setView(myview)//这里将对话框布局文件加入到对话框中
		.create();
		dialog.show();	
	}
	//往数据库添加柜类型表的值
	private void addabinet(String no,int type)
	{
		try 
		{
			// 创建InaccountDAO对象
			vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(HuodaoTest.this);
            // 创建Tb_inaccount对象
        	Tb_vmc_cabinet tb_vmc_cabinet = new Tb_vmc_cabinet(no,type);
        	cabinetDAO.add(tb_vmc_cabinet);// 添加收入信息
        	showabinet();//显示柜信息
        	ToolClass.addOptLog(HuodaoTest.this,0,"添加柜:"+no);
        	// 弹出信息提示
            Toast.makeText(HuodaoTest.this, "〖货柜〗数据添加成功！", Toast.LENGTH_SHORT).show();
            
		} catch (Exception e)
		{
			// TODO: handle exception
			Toast.makeText(HuodaoTest.this, "货柜添加失败！", Toast.LENGTH_SHORT).show();
		}
	}
	//显示全部柜信息
	private void showabinet()
	{
		ArrayAdapter<String> arrayAdapter = null;// 创建ArrayAdapter对象 
		Vmc_CabinetAdapter vmc_cabAdapter=new Vmc_CabinetAdapter();
	    String[] strInfos = vmc_cabAdapter.showSpinInfo(HuodaoTest.this);	    
	    // 使用字符串数组初始化ArrayAdapter对象
	    arrayAdapter = new ArrayAdapter<String>(this,R.layout.viewspinner, strInfos);
	    spinhuosetCab.setAdapter(arrayAdapter);// 为spin列表设置数据源
	    spinhuotestCab.setAdapter(arrayAdapter);// 为spin列表设置数据源
	    spinhuopeiCab.setAdapter(arrayAdapter);// 为spin列表设置数据源
	    cabinetID=vmc_cabAdapter.getCabinetID();    
	    cabinetType=vmc_cabAdapter.getCabinetType();
	    //只有有柜号的时候，才请求加载柜内货道信息
//		if(cabinetID!=null)
//		{
//		    cabinetsetvar=Integer.parseInt(cabinetID[0]); 
//		    cabinetTypesetvar=cabinetType[0]; 
//		}
	}
	//导入本柜全部货道信息
	private void showhuodao()
	{		 
		huoAdapter.showProInfo(HuodaoTest.this, "", huoSet,String.valueOf(cabinetsetvar));
		
		HuoPictureAdapter adapter = new HuoPictureAdapter(String.valueOf(cabinetsetvar),huoAdapter.getHuoID(),huoAdapter.getHuoproID(),huoAdapter.getHuoRemain(),huoAdapter.getHuolasttime(), huoAdapter.getProImage(),HuodaoTest.this);// 创建pictureAdapter对象
		gvhuodao.setAdapter(adapter);// 为GridView设置数据源		 
		barhuomanager.setVisibility(View.GONE);  
	}
	//删除柜号以及柜内货道全部信息
	private void cabinetDel()
	{
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要删除该柜吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					// 创建InaccountDAO对象
	    					vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoTest.this);
				            columnDAO.deteleCab(String.valueOf(cabinetsetvar));// 删除该柜货道信息
				            
				            vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(HuodaoTest.this);
				            cabinetDAO.detele(String.valueOf(cabinetsetvar));// 删除该柜信息
				            ToolClass.addOptLog(HuodaoTest.this,2,"删除柜:"+cabinetsetvar);
	    					// 弹出信息提示
				            Toast.makeText(HuodaoTest.this, "柜删除成功！", Toast.LENGTH_SHORT).show();						            
				            finish();
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
	
	//补满本柜货道
	private void cabinetbuhuo()
	{
		//创建警告对话框
    	Dialog alert=new AlertDialog.Builder(HuodaoTest.this)
    		.setTitle("对话框")//标题
    		.setMessage("您确定要补满本柜货道吗？")//表示对话框中得内容
    		.setIcon(R.drawable.ic_launcher)//设置logo
    		.setPositiveButton("补货", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
    			{				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) 
	    				{
	    					// TODO Auto-generated method stub	
	    					barhuomanager.setVisibility(View.VISIBLE);
	    					// 创建InaccountDAO对象
	    					vmc_columnDAO columnDAO = new vmc_columnDAO(HuodaoTest.this);
				            columnDAO.buhuoCab(String.valueOf(cabinetsetvar));
				            showhuodao();
				            //=============
			    			//Server服务相关
			    			//=============
			    			//7.发送指令广播给EVServerService
			    			Intent intent2=new Intent();
		    				intent2.putExtra("EVWhat", EVServerhttp.SETHUODAOSTATUCHILD);
		    				intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
		    				sendBroadcast(intent2);
	    					// 弹出信息提示
				            Toast.makeText(HuodaoTest.this, "补货成功！", Toast.LENGTH_SHORT).show();	
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
	
	//===============
	//货道测试页面
	//===============
	private void sethuorst(int status)
	{
		switch(status)
		{			
			case 0:
				txthuotestrst.setText("出货失败");
				break;
			case 1:
				txthuotestrst.setText("出货成功");
				break;		
		}
	}
}
