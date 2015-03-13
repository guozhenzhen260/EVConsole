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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.evprotocol.JNIInterface;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_class;
import com.easivend.common.HuoPictureAdapter;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_CabinetAdapter;
import com.easivend.common.Vmc_ClassAdapter;
import com.easivend.common.Vmc_HuoAdapter;
import com.easivend.common.Vmc_ProductAdapter;
import com.example.evconsole.R;

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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
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
	private int[] layres=new int[]{R.id.tab_huodaomanager,R.id.tab_huodaotest};//内嵌布局文件的id
	private Button btnhuosetadd=null,btnhuosetdel=null,btnhuosetexit=null;
	private Spinner spinhuosetCab=null;
	private String[] cabinetID=null;
	Map<String, Integer> huoSet= new TreeMap<String,Integer>();
	// 定义货道列表
	Vmc_HuoAdapter huoAdapter=null;
	GridView gvhuodao=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	
	private int device=0;//出货柜号		
	private int status=0;//出货结果
	private int hdid=0;//货道id
	private int hdtype=0;//出货类型
	private float cost=0;//扣钱
	private float totalvalue=0;//剩余金额
	private int huodao=0;//剩余存货数量
	private TextView txthuorst=null;
	private Button btnhuochu=null;// 创建Button对象“出货”
	private Button btnhuocancel=null;// 创建Button对象“重置”
	private Button btnhuoexit=null;// 创建Button对象“退出”
	private EditText edtcolumn=null,edtprice=null;
	private RadioGroup cabinet=null, type=null;
	private RadioButton mainhuodao=null,fuhuodao=null,moneychu=null,pcchu=null;
	private int cabinetvar=0,typevar=0;
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
    	
    	//注册出货监听器
  	    EVprotocolAPI.setCallBack(new JNIInterface() {
			
			@Override
			public void jniCallback(Map<String, Integer> allSet) {
				// TODO Auto-generated method stub
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<huodao货道相关");
				Map<String, Integer> Set= allSet;
				int jnirst=Set.get("EV_TYPE");
				switch (jnirst)
				{
					case EVprotocolAPI.EV_TRADE_RPT://接收子线程消息
						device=allSet.get("device");//出货柜号
						status=allSet.get("status");//出货结果
						hdid=allSet.get("hdid");//货道id
						hdtype=allSet.get("type");//出货类型
						cost=ToolClass.MoneyRec(allSet.get("cost"));//扣钱
						totalvalue=ToolClass.MoneyRec(allSet.get("totalvalue"));//剩余金额
						huodao=allSet.get("huodao");//剩余存货数量
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<出货结果"+"device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");	
						
						txthuorst.setText("device=["+device+"],status=["+status+"],hdid=["+hdid+"],type=["+hdtype+"],cost=["
								+cost+"],totalvalue=["+totalvalue+"],huodao=["+huodao+"]");
						break;
					case EVprotocolAPI.EV_COLUMN_RPT://接收子线程消息
						huoSet.clear();
						//输出内容
				        Set<Entry<String, Integer>> allmap=Set.entrySet();  //实例化
				        Iterator<Entry<String, Integer>> iter=allmap.iterator();
				        while(iter.hasNext())
				        {
				            Entry<String, Integer> me=iter.next();
				            if(me.getKey().equals("EV_TYPE")!=true)
				            	huoSet.put(me.getKey(), me.getValue());
				        } 
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道状态:"+huoSet.toString());	
						showhuodao();						
						break;
				}
			}
		}); 
    	//===============
    	//货道设置页面
    	//===============
  	    
  	    huoAdapter=new Vmc_HuoAdapter();
  	    this.gvhuodao=(GridView) findViewById(R.id.gvhuodao); 
    	spinhuosetCab= (Spinner) findViewById(R.id.spinhuosetCab); 
    	//显示柜信息
    	showabinet();
    	this.spinhuosetCab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				EVprotocolAPI.getColumn(Integer.parseInt(cabinetID[arg2]));
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
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道ID="+cabinetID[0]+huoID+"status="+huoSet.get(huoID));
				Intent intent = new Intent();
		    	intent.setClass(HuodaoTest.this, HuodaoSet.class);// 使用AddInaccount窗口初始化Intent
                intent.putExtra("huoID", huoID);
                intent.putExtra("cabID", cabinetID[0]);
                intent.putExtra("huoStatus", String.valueOf(huoSet.get(huoID)));
		    	startActivityForResult(intent, REQUEST_CODE);// 打开AddInaccount	
			}// 为GridView设置项单击事件
    		
    	});
    	btnhuosetadd = (Button) findViewById(R.id.btnhuosetadd);
    	btnhuosetadd.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	cabinetAdd();
		    }
		});
    	btnhuosetdel = (Button) findViewById(R.id.btnhuosetdel);
    	btnhuosetexit = (Button) findViewById(R.id.btnhuosetexit);
    	btnhuosetexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
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
				+"],["+screenHeight+"]");	
		
    	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) gvhuodao.getLayoutParams(); // 取控件mGrid当前的布局参数
    	linearParams.height =  screenHeight-500;// 当控件的高强制设成75象素
    	gvhuodao.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
  	   
    	//===============
    	//出货测试页面
    	//===============
		
		
		txthuorst=(TextView)findViewById(R.id.txthuorst);
		btnhuochu = (Button) findViewById(R.id.btnhuochu);
		btnhuocancel = (Button) findViewById(R.id.btnhuocancel);
		btnhuoexit = (Button) findViewById(R.id.btnhuoexit);
		edtcolumn = (EditText) findViewById(R.id.edtcolumn);
		edtprice = (EditText) findViewById(R.id.edtprice);
		//得到那个柜出货
		this.cabinet = (RadioGroup) super.findViewById(R.id.cabinet);
	    this.mainhuodao = (RadioButton) super.findViewById(R.id.mainhuodao);
	    this.fuhuodao = (RadioButton) super.findViewById(R.id.fuhuodao);		
	    this.cabinet.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 主柜出货
				if(mainhuodao.getId()==checkedId)
				{
					cabinetvar=1;
				}
				//副柜出货
				else if(fuhuodao.getId()==checkedId)
				{
					cabinetvar=2;
				}
			}
		});
	    //得到出货方式
  		this.type = (RadioGroup) super.findViewById(R.id.type);
  	    this.moneychu = (RadioButton) super.findViewById(R.id.moneychu);
  	    this.pcchu = (RadioButton) super.findViewById(R.id.pcchu);		
  	    this.type.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
  			@Override
  			public void onCheckedChanged(RadioGroup group, int checkedId) {
  				// 现金出货
  				if(moneychu.getId()==checkedId)
  				{
  					typevar=0;
  				}
  				//PC出货
  				else if(pcchu.getId()==checkedId)
  				{
  					typevar=2;
  				}
  			}
  		});
  	    
  	    
		btnhuochu.setOnClickListener(new OnClickListener() {// 为出货按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {		    	  
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI",
		    	"[APPsend>>]cabinet="+String.valueOf(cabinetvar)
		    	+" column="+String.valueOf(Integer.parseInt(edtcolumn.getText().toString()))
		    	+" type="+String.valueOf(typevar)
		    	+" price="+edtprice.getText().toString()
		    	);
		    	int rst=EVprotocolAPI.trade(cabinetvar,Integer.parseInt(edtcolumn.getText().toString()),typevar,
		    			ToolClass.MoneySend(Float.parseFloat(edtprice.getText().toString())));	   	
		    	
		    }
		});
		btnhuocancel.setOnClickListener(new OnClickListener() {// 为重置按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	edtcolumn.setText("");// 设置金额文本框为空
		    	edtprice.setText("");// 设置时间文本框为空		        
		    }
		});
		btnhuoexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	}
	//===============
	//货道设置页面
	//===============
	//接收GoodsProSet返回信息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==HuodaoTest.RESULT_OK)
			{
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
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(HuodaoTest.this);
		myview=factory.inflate(R.layout.selectcabinet, null);
		final EditText dialogcab=(EditText) myview.findViewById(R.id.dialogcab);
		final Spinner dialogspincabtype=(Spinner) myview.findViewById(R.id.dialogspincabtype);
		
		
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
        	Tb_vmc_cabinet tb_vmc_cabinet = new Tb_vmc_cabinet(no, type);
        	cabinetDAO.add(tb_vmc_cabinet);// 添加收入信息
        	showabinet();//显示柜信息
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
	    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strInfos);
	    spinhuosetCab.setAdapter(arrayAdapter);// 为ListView列表设置数据源
	    cabinetID=vmc_cabAdapter.getCabinetID();    	
	}
	//导入本柜全部货道信息
	private void showhuodao()
	{
		huoAdapter.showProInfo(HuodaoTest.this, "", huoSet,cabinetID[0]);
		HuoPictureAdapter adapter = new HuoPictureAdapter(cabinetID[0],huoAdapter.getHuoID(),huoAdapter.getHuoproID(),huoAdapter.getHuoRemain(),huoAdapter.getHuolasttime(), huoAdapter.getProImage(),HuodaoTest.this);// 创建pictureAdapter对象
		gvhuodao.setAdapter(adapter);// 为GridView设置数据源
	}
		
}
