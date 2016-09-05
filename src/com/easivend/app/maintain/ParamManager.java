package com.easivend.app.maintain;

import java.util.concurrent.TimeUnit;

import com.easivend.dao.vmc_machinesetDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.http.EVServerhttp;
import com.easivend.app.business.BusLand;
import com.easivend.app.business.BusPort;
import com.easivend.app.maintain.MaintainActivity.EVServerReceiver;
import com.easivend.common.MachineExpanseListAdapter;
import com.easivend.common.ShowSortAdapter;
import com.easivend.common.ToolClass;
import com.easivend.model.Tb_vmc_machineset;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TimePicker;


public class ParamManager extends TabActivity 
{
	private TabHost mytabhost = null;
	private int[] layres=new int[]{R.id.tab_machine,R.id.tab_device,R.id.tab_run};//内嵌布局文件的id
	private static final int TIME_DIALOG_ID = 0;// 创建时间对话框常量
	private EditText edtdevID=null,edtdevhCode=null,edtmainPwd=null,edtrstTime=null,edtrstDay=null,edtmarketAmount=null,edtbillAmount=null;
	private Switch switchisNet = null,switchisbuhuo=null,switchisbuyCar = null,switchisqiangbuy=null,switchlanguage = null,switchbaozhiProduct = null,switchemptyProduct = null,switchamount = null,switchcard = null,
			switchweixing = null,switchprinter = null;    
	private RadioGroup zhifubaogrp=null;
	private RadioButton rbtnclose=null,rbtnzhifubao1=null,rbtnzhifubao2=null;
	private Spinner spinparamsort=null,spinCashless=null;
	private Button btnmachinecheck=null,btnmachineSave=null,btnmachineexit=null,btndeviceSave=null,btndeviceexit=null,btnamount=null,btncard=null,btnCashless=null,
			btnzhifubaoer=null,btnweixing=null,btnprinter=null;	
	private int proSortType=6;
	//排序有关的定义
	private ShowSortAdapter showSortAdapter=null;
    private ArrayAdapter<String> arrayadapter = null;
	private int mHour=0,mMinute=0;
	
	//子名称的值
	public static String runValue[][]=new String[][]{{"","","",""},{"","",""},
		{"",""},{"",""},{"",""}};
	private ExpandableListView emachinelistview = null;	
	private Button btnmachinerunSave=null,btnmachinerunCancel=null;
	private Uri uri=null;
	private String imgDir=null;
	private Tb_vmc_machineset tb_vmc_machineset=null;
	//Server服务相关
	LocalBroadcastManager localBroadreceiver;
	EVServerReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.parammanage);		
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.parammanage, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabmachine=this.mytabhost.newTabSpec("tab0");
        myTabmachine.setIndicator("机器参数配置");
        myTabmachine.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabmachine); 
    	
    	TabSpec myTabdevice=this.mytabhost.newTabSpec("tab1");
    	myTabdevice.setIndicator("交易参数配置");
    	myTabdevice.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabdevice); 
    	
    	TabSpec myTabrun=this.mytabhost.newTabSpec("tab2");
    	myTabrun.setIndicator("运行参数配置");
    	myTabrun.setContent(this.layres[2]);
    	this.mytabhost.addTab(myTabrun); 
    	
    	
    	//4.注册接收器
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
    			
    			
    	//===========================
    	//机器参数配置
    	//===========================
    	edtdevID = (EditText) findViewById(R.id.edtdevID);
    	edtdevhCode = (EditText) findViewById(R.id.edtdevhCode);
    	switchisNet = (Switch)findViewById(R.id.switchisNet); //获取到控件  
    	switchisbuhuo = (Switch)findViewById(R.id.switchisbuhuo);     	
	    switchisbuyCar = (Switch)findViewById(R.id.switchisbuyCar);    	
	    switchisqiangbuy = (Switch)findViewById(R.id.switchisqiangbuy);    	
	    edtmainPwd = (EditText) findViewById(R.id.edtmainPwd);
	    switchlanguage = (Switch)findViewById(R.id.switchlanguage);
    	edtrstTime = (EditText) findViewById(R.id.edtrstTime);// 获取时间文本框
    	edtrstTime.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
            @Override
            public void onClick(View arg0) {
                showDialog(TIME_DIALOG_ID);// 显示日期选择对话框
            }
        });
    	edtrstDay = (EditText) findViewById(R.id.edtrstDay);
    	switchbaozhiProduct = (Switch)findViewById(R.id.switchbaozhiProduct);
    	switchemptyProduct = (Switch)findViewById(R.id.switchemptyProduct);
    	//排序
    	this.spinparamsort = (Spinner) super.findViewById(R.id.spinparamsort); 
    	showSortAdapter=new ShowSortAdapter();    	
	    arrayadapter = new ArrayAdapter<String>(this, R.layout.viewspinner, showSortAdapter.getDataSortName());
	    spinparamsort.setAdapter(arrayadapter);// 为ListView列表设置数据源
    	spinparamsort.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			//当选项改变时触发
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				proSortType=arg2;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}); 
    	edtmarketAmount = (EditText) findViewById(R.id.edtmarketAmount);
    	edtbillAmount = (EditText) findViewById(R.id.edtbillAmount);
    	loadmachineparam();    	
    	btnmachinecheck = (Button) findViewById(R.id.btnmachinecheck);
    	btnmachinecheck.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	String devID = edtdevID.getText().toString();
		    	String devhCode = edtdevhCode.getText().toString();
				if((ToolClass.isEmptynull(devID)!=true)&&(ToolClass.isEmptynull(devhCode)!=true)
		    		)
				{
					Intent intent2=new Intent(); 
					intent2.putExtra("EVWhat", EVServerhttp.SETCHECKCMDCHILD);
					intent2.putExtra("vmc_no", devID);
					intent2.putExtra("vmc_auth_code", devhCode);
					intent2.setAction("android.intent.action.vmserversend");//action与接收器相同
					localBroadreceiver.sendBroadcast(intent2);  
				}
				else
				{
					ToolClass.failToast("请完整输入签到设备号和签到码再校验!");	
				}
		    }
		});
    	btnmachineSave = (Button) findViewById(R.id.btnmachineSave);
    	btnmachineSave.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		        saveparam();
		    }
		});
    	btnmachineexit = (Button) findViewById(R.id.btnmachineexit);
    	btnmachineexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
    	
    	
    	
    	
    	
    	//===========================
    	//设备参数配置页面
    	//===========================
    	switchamount = (Switch)findViewById(R.id.switchamount);
    	switchamount.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnamount.setEnabled(isChecked);	
			}  
            
            
        }); 
    	switchcard = (Switch)findViewById(R.id.switchcard);
    	switchcard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btncard.setEnabled(isChecked);	
			}  
            
            
        });
    	this.spinCashless = (Spinner) super.findViewById(R.id.spinCashless);
    	spinCashless.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			//当选项改变时触发
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2>0)	
					btnCashless.setEnabled(true);
				else
					btnCashless.setEnabled(false);	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}); 
    	this.zhifubaogrp = (RadioGroup) super.findViewById(R.id.zhifubaogrp);
        this.rbtnclose = (RadioButton) super.findViewById(R.id.rbtnclose);
        this.rbtnzhifubao1 = (RadioButton) super.findViewById(R.id.rbtnzhifubao1);
        this.rbtnzhifubao2 = (RadioButton) super.findViewById(R.id.rbtnzhifubao2);
        this.zhifubaogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(ParamManager.this.rbtnclose.getId()==checkedId)
				{
					btnzhifubaoer.setEnabled(false);		
				}
				else
				{
					btnzhifubaoer.setEnabled(true);		
				}	
			}
		});
        switchweixing = (Switch)findViewById(R.id.switchweixing);
    	switchweixing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				btnweixing.setEnabled(isChecked);	
			}  
            
            
        });
    	switchprinter = (Switch)findViewById(R.id.switchprinter);
    	switchprinter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				//btnprinter.setEnabled(isChecked);	
			}  
            
            
        });
    	    	    	
    	btndeviceSave = (Button) findViewById(R.id.btndeviceSave);
    	btndeviceSave.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	saveparam();
		    }
		});
    	btndeviceexit = (Button) findViewById(R.id.btndeviceexit);
    	btndeviceexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
    	btnamount = (Button) findViewById(R.id.btnamount);
    	btnamount.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, AddInaccount.class);// 使用AddInaccount窗口初始化Intent
		        startActivity(intent);// 打开AddInaccount
		    }
		});
    	
    	btncard = (Button) findViewById(R.id.btncard);
    	btncard.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, OpendoorTest.class);// 使用AddInaccount窗口初始化Intent
		    	intent.putExtra("id", edtdevhCode.getText().toString());
		    	startActivity(intent);// 打开AddInaccount
		    }
		});
    	btnCashless = (Button) findViewById(R.id.btnCashless);
    	btnCashless.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, CahslessTest.class);// 使用AddInaccount窗口初始化Intent
		    	startActivity(intent);// 打开AddInaccount
		    }
		});
    	btnzhifubaoer = (Button) findViewById(R.id.btnzhifubaoer);
    	btnzhifubaoer.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, ZhifubaoTest.class);// 使用AddInaccount窗口初始化Intent
		        intent.putExtra("id", edtdevhCode.getText().toString());
		    	startActivity(intent);// 打开AddInaccount
		    }
		});
    	btnweixing = (Button) findViewById(R.id.btnweixing);
    	btnweixing.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = new Intent(ParamManager.this, WeixingTest.class);// 使用AddInaccount窗口初始化Intent
		    	intent.putExtra("id", edtdevhCode.getText().toString());
		    	startActivity(intent);// 打开AddInaccount
		    }
		});
    	btnprinter = (Button) findViewById(R.id.btnprinter);
    	loaddeviceparam();
    	
    	
    	//===========================
    	//运行参数配置
    	//===========================
    	this.emachinelistview = (ExpandableListView) super.findViewById(R.id.emachinelistview);
    	loadrunparam();
    	MachineExpanseListAdapter adapter = new MachineExpanseListAdapter(this,ParamManager.runValue);
        this.emachinelistview.setAdapter(adapter);
        
        this.emachinelistview.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(ParamManager.this, "子选项,group="+groupPosition+",child="+childPosition, Toast.LENGTH_LONG).show();
				if((groupPosition==0&&childPosition==0)||(groupPosition==0&&childPosition==2)||(groupPosition==1&&childPosition==0))
				{	
					emachineListviewSet(1,groupPosition,childPosition);				
				}
				else 
				{
					emachineListviewSet(2,groupPosition,childPosition);	
				}
				return false;
			}
		});        
        btnmachinerunSave = (Button)findViewById(R.id.btnmachinerunSave);          
  		btnmachinerunSave.setOnClickListener(new OnClickListener() {// 为保存按钮设置监听事件
		    @Override
		    public void onClick(View arg0) 
		    {
		    	saverunparam();
		    }
		});
        btnmachinerunCancel = (Button)findViewById(R.id.btnmachinerunCancel);  
        btnmachinerunCancel.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) 
		    {
		    	finish();
		    }
		});
        
	}
	//===========================
	//机器参数配置
	//===========================
		
	@Override
    protected Dialog onCreateDialog(int id) {// 重写onCreateDialog方法

        switch (id) {
        case TIME_DIALOG_ID:// 弹出日期选择对话框
            return new TimePickerDialog(this, mDateSetListener, mHour, mMinute, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener mDateSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			mHour=hourOfDay;
			mMinute=minute;
			updateDisplay(); 
		}
        
    };
    private void updateDisplay() 
    {
        // 显示设置的时间
    	edtrstTime.setText(new StringBuilder().append(mHour).append(":").append(mMinute));
    }
    
    private void saveparam()
    {
    	String devID = edtdevID.getText().toString();
    	String devhCode = edtdevhCode.getText().toString();
    	int isNet = (switchisNet.isChecked()==true)?1:0;
    	int isBuhuo = (switchisbuhuo.isChecked()==true)?1:0;
    	int isbuyCar = (switchisbuyCar.isChecked()==true)?1:0;
    	int isQiangbuy = (switchisqiangbuy.isChecked()==true)?1:0;
    	String mainPwd= edtmainPwd.getText().toString();
    	float marketAmount = Float.parseFloat(edtmarketAmount.getText().toString()); 
    	float billAmount = Float.parseFloat(edtbillAmount.getText().toString()); 
    	int amount = (switchamount.isChecked()==true)?1:0;
    	int card = (switchcard.isChecked()==true)?1:0;
    	int zhifubaofaca = (int)spinCashless.getSelectedItemId();
    	int zhifubaoer=0;
    	if(rbtnclose.isChecked())
    	{
    		zhifubaoer=0;
    	}
    	else if(rbtnzhifubao1.isChecked())
    	{
    		zhifubaoer=1;
    	}
    	else if(rbtnzhifubao2.isChecked())
    	{
    		zhifubaoer=2;
    	}
    	int weixing = (switchweixing.isChecked()==true)?1:0;
    	int printer = (switchprinter.isChecked()==true)?1:0;
    	int language = (switchlanguage.isChecked()==true)?1:0;
    	String rstTime = edtrstTime.getText().toString();
    	int rstDay = 0;
    	if(ToolClass.isEmptynull(edtrstDay.getText().toString())!=true)
    		rstDay = Integer.parseInt(edtrstDay.getText().toString());
    	int baozhiProduct = (switchbaozhiProduct.isChecked()==true)?1:0;
    	int emptyProduct = (switchemptyProduct.isChecked()==true)?1:0;
    	if((ToolClass.isEmptynull(devID)!=true)&&(ToolClass.isEmptynull(devhCode)!=true)
    		)
    	{
    		try 
    		{
    			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<参数devID="+devID+" devhCode="+devhCode+" isNet="
    					+isNet+" isBuhuo="+isBuhuo+" isbuyCar="+isbuyCar+" isQiangbuy="+isQiangbuy+" mainPwd="
    					+mainPwd+" amount="+amount+" card="+card+" zhifubaofaca="+zhifubaofaca+" zhifubaoer="+zhifubaoer
    					+" weixing="+weixing+" printer="+printer+" language="+language
    					+" rstTime="+rstTime+" rstDay="+rstDay+" baozhiProduct="+baozhiProduct
    					+" emptyProduct="+emptyProduct+" proSortType="+proSortType+" marketAmount="+marketAmount+" billAmount="+billAmount,"log.txt");
    			// 创建InaccountDAO对象
    			vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ParamManager.this);
	            //创建Tb_inaccount对象 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(devID, devhCode, isNet,isBuhuo, 
    					isbuyCar,isQiangbuy,mainPwd,amount,card,zhifubaofaca,zhifubaoer,weixing,printer,language,rstTime,rstDay,
    					baozhiProduct,emptyProduct, proSortType,marketAmount,billAmount,"","");
    			parameterDAO.add(tb_vmc_system_parameter); 
    			//加载goc
    			ToolClass.goc = isbuyCar;
	        	// 弹出信息提示
	            Toast.makeText(ParamManager.this, "数据添加成功！", Toast.LENGTH_SHORT).show();	            
	            
    		} catch (Exception e)
			{
				// TODO: handle exception
    			ToolClass.failToast("数据添加失败！");	
			}		    		
            
        } 
        else
        {
        	ToolClass.failToast("请填写红色部分！");	
        }
    }
    
    private void loadmachineparam()
    {
    	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ParamManager.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<参数devID="+tb_inaccount.getDevID().toString()+" devhCode="+tb_inaccount.getDevhCode().toString()+" isNet="
					+tb_inaccount.getIsNet()+" isfenClass="+tb_inaccount.getIsfenClass()+" isbuyCar="+tb_inaccount.getIsbuyCar()+" liebiaoKuan="+tb_inaccount.getLiebiaoKuan()+" mainPwd="
					+tb_inaccount.getMainPwd()+" amount="+tb_inaccount.getAmount()+" card="+tb_inaccount.getCard()+" zhifubaofaca="+tb_inaccount.getZhifubaofaca()+" zhifubaoer="+tb_inaccount.getZhifubaoer()
					+" weixing="+tb_inaccount.getWeixing()+" printer="+tb_inaccount.getPrinter()+" language="+tb_inaccount.getLanguage()
					+" rstTime="+tb_inaccount.getRstTime().toString()+" rstDay="+tb_inaccount.getRstDay()+" baozhiProduct="+tb_inaccount.getBaozhiProduct()
					+" emptyProduct="+tb_inaccount.getEmptyProduct()+" proSortType="+tb_inaccount.getProSortType()+" marketAmount="+tb_inaccount.getMarketAmount()+" billAmount="+tb_inaccount.getBillAmount(),"log.txt"); 
		    edtdevID.setText(tb_inaccount.getDevID().toString());
		    edtdevhCode.setText(tb_inaccount.getDevhCode().toString());	  
		    switchisNet.setChecked((tb_inaccount.getIsNet()==1)?true:false);
		    switchisbuhuo.setChecked((tb_inaccount.getIsfenClass()==1)?true:false);
		    switchisbuyCar.setChecked((tb_inaccount.getIsbuyCar()==1)?true:false);
		    switchisqiangbuy.setChecked((tb_inaccount.getLiebiaoKuan()==1)?true:false);
		    edtmainPwd.setText(tb_inaccount.getMainPwd().toString());
		    switchlanguage.setChecked((tb_inaccount.getLanguage()==1)?true:false);
		    edtrstTime.setText(tb_inaccount.getRstTime().toString());
		    edtrstDay.setText(String.valueOf(tb_inaccount.getRstDay()));
		    switchbaozhiProduct.setChecked((tb_inaccount.getBaozhiProduct()==1)?true:false);
		    switchemptyProduct.setChecked((tb_inaccount.getEmptyProduct()==1)?true:false);
		    //设置下拉框默认值
		    spinparamsort.setSelection(tb_inaccount.getProSortType());
		    edtmarketAmount.setText(String.valueOf(tb_inaccount.getMarketAmount()));
		    edtbillAmount.setText(String.valueOf(tb_inaccount.getBillAmount()));
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
  			case EVServerhttp.SETCHECKCMDMAIN://子线程接收主线程消息签到完成
  				btnmachinecheck.setText("恭喜您，验证通过!");
  	    		break;
  			case EVServerhttp.SETERRFAILDCHECKCMDMAIN://子线程接收主线程消息签到失败
  				ToolClass.failToast("抱歉，验证未通过,请联系管理员!");
  				btnmachinecheck.setText("抱歉，验证未通过!");
  	    		break;	
  			}			
  		}

  	}
    
    
    //===========================
	//设备参数配置页面
	//===========================
    private void loaddeviceparam()
    {
    	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ParamManager.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<参数devID="+tb_inaccount.getDevID().toString()+" devhCode="+tb_inaccount.getDevhCode().toString()+" isNet="
					+tb_inaccount.getIsNet()+" isfenClass="+tb_inaccount.getIsfenClass()+" isbuyCar="+tb_inaccount.getIsbuyCar()+" liebiaoKuan="+tb_inaccount.getLiebiaoKuan()+" mainPwd="
					+tb_inaccount.getMainPwd()+" amount="+tb_inaccount.getAmount()+" card="+tb_inaccount.getCard()+" zhifubaofaca="+tb_inaccount.getZhifubaofaca()+" zhifubaoer="+tb_inaccount.getZhifubaoer()
					+" weixing="+tb_inaccount.getWeixing()+" printer="+tb_inaccount.getPrinter()+" language="+tb_inaccount.getLanguage()
					+" rstTime="+tb_inaccount.getRstTime().toString()+" rstDay="+tb_inaccount.getRstDay()+" baozhiProduct="+tb_inaccount.getBaozhiProduct()
					+" emptyProduct="+tb_inaccount.getEmptyProduct()+" proSortType="+tb_inaccount.getProSortType(),"log.txt"); 
		    
		    switchamount.setChecked((tb_inaccount.getAmount()==1)?true:false);
		    switchcard.setChecked((tb_inaccount.getCard()==1)?true:false);
		    spinCashless.setSelection(tb_inaccount.getZhifubaofaca());
		    if(tb_inaccount.getZhifubaoer()==0)
		    {
		    	rbtnclose.setChecked(true);
		    }
		    else if(tb_inaccount.getZhifubaoer()==1)
		    {
		    	rbtnzhifubao1.setChecked(true);
		    }
		    else if(tb_inaccount.getZhifubaoer()==2)
		    {
		    	rbtnzhifubao2.setChecked(true);
		    }
		    switchweixing.setChecked((tb_inaccount.getWeixing()==1)?true:false);
		    switchprinter.setChecked((tb_inaccount.getPrinter()==1)?true:false);
    	}
	}
    
    //===========================
  	//运行参数配置页面
  	//===========================    
    //导入运行参数数据
    private void loadrunparam()
    {
    	vmc_machinesetDAO machinesetDAO = new vmc_machinesetDAO(ParamManager.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	tb_vmc_machineset = machinesetDAO.find();
    	
    	if(tb_vmc_machineset!=null)
    	{
//	    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<参数devID="+tb_inaccount.getDevID().toString()+" devhCode="+tb_inaccount.getDevhCode().toString()+" isNet="
//					+tb_inaccount.getIsNet()+" isfenClass="+tb_inaccount.getIsfenClass()+" isbuyCar="+tb_inaccount.getIsbuyCar()+" liebiaoKuan="+tb_inaccount.getLiebiaoKuan()+" mainPwd="
//					+tb_inaccount.getMainPwd()+" amount="+tb_inaccount.getAmount()+" card="+tb_inaccount.getCard()+" zhifubaofaca="+tb_inaccount.getZhifubaofaca()+" zhifubaoer="+tb_inaccount.getZhifubaoer()
//					+" weixing="+tb_inaccount.getWeixing()+" printer="+tb_inaccount.getPrinter()+" language="+tb_inaccount.getLanguage()
//					+" rstTime="+tb_inaccount.getRstTime().toString()+" rstDay="+tb_inaccount.getRstDay()+" baozhiProduct="+tb_inaccount.getBaozhiProduct()
//					+" emptyProduct="+tb_inaccount.getEmptyProduct()+" proSortType="+tb_inaccount.getProSortType()); 
    		
    		ParamManager.runValue[0][0]=String.valueOf(tb_vmc_machineset.getAudioWork());
    		ParamManager.runValue[0][1]=tb_vmc_machineset.getAudioWorkstart()+" --"+tb_vmc_machineset.getAudioWorkend();
    		ParamManager.runValue[0][2]=String.valueOf(tb_vmc_machineset.getAudioSun());
    		ParamManager.runValue[0][3]=tb_vmc_machineset.getAudioSunstart()+" --"+tb_vmc_machineset.getAudioSunend();
    		
    		ParamManager.runValue[1][0]=String.valueOf(tb_vmc_machineset.getTempWork());
    		ParamManager.runValue[1][1]=tb_vmc_machineset.getTempWorkstart()+"  --"+tb_vmc_machineset.getTempWorkend();
    		ParamManager.runValue[1][2]=tb_vmc_machineset.getTempSunstart()+"  --"+tb_vmc_machineset.getTempSunend();
    		
    		ParamManager.runValue[2][0]=tb_vmc_machineset.getLigntWorkstart()+"  --"+tb_vmc_machineset.getLigntWorkend();
    		ParamManager.runValue[2][1]=tb_vmc_machineset.getLigntSunstart()+"  --"+tb_vmc_machineset.getLigntSunend();
    		
    		ParamManager.runValue[3][0]=tb_vmc_machineset.getColdWorkstart()+"  --"+tb_vmc_machineset.getColdWorkend();
    		ParamManager.runValue[3][1]=tb_vmc_machineset.getColdSunstart()+"  --"+tb_vmc_machineset.getColdSunend();
    		
    		ParamManager.runValue[4][0]=tb_vmc_machineset.getChouWorkstart()+"  --"+tb_vmc_machineset.getChouWorkend();
    		ParamManager.runValue[4][1]=tb_vmc_machineset.getChouSunstart()+"  --"+tb_vmc_machineset.getChouSunend();
    	}
	}
    //dialogtype==1启动数值选择框,2启动时间选择框
    private void emachineListviewSet(int dialogtype,final int groupPosition,final int childPosition)
    {    	
    	View myview=null;    	
		// TODO Auto-generated method stub
		LayoutInflater factory = LayoutInflater.from(ParamManager.this);
		if(dialogtype==1)//数值选择框
		{
			myview=factory.inflate(R.layout.selectinteger, null);
			final EditText dialoginte=(EditText) myview.findViewById(R.id.dialoginte);
			
			Dialog dialog = new AlertDialog.Builder(ParamManager.this)
			.setTitle("设置")
			.setPositiveButton("保存", new DialogInterface.OnClickListener() 	
			{
					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					//Toast.makeText(ParamManager.this, "数值="+dialoginte.getText().toString(), Toast.LENGTH_LONG).show();
					updaterunparam(groupPosition,childPosition,Integer.parseInt(dialoginte.getText().toString()),"","");
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
		else if(dialogtype==2)//时间选择框
		{
			myview=factory.inflate(R.layout.selecttimepick, null);
			final TimePicker diaStartTime=(TimePicker) myview.findViewById(R.id.diaStartTime);
			final TimePicker diaEndTime=(TimePicker) myview.findViewById(R.id.diaEndTime);
			
			Dialog dialog = new AlertDialog.Builder(ParamManager.this)
			.setTitle("设置")
			.setPositiveButton("保存", new DialogInterface.OnClickListener() 	
			{
					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub					
					//Toast.makeText(ParamManager.this, "起始时间="+diaStartTime.getCurrentHour().toString()+":"+diaStartTime.getCurrentMinute().toString()
					//		+"结束时间="+diaEndTime.getCurrentHour().toString()+":"+diaEndTime.getCurrentMinute().toString(), Toast.LENGTH_LONG).show();
					updaterunparam(groupPosition,childPosition,0,diaStartTime.getCurrentHour().toString()+":"+diaStartTime.getCurrentMinute().toString(),diaEndTime.getCurrentHour().toString()+":"+diaEndTime.getCurrentMinute().toString());
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
			
			 	
    }
    //更新运行参数数据
    private void updaterunparam(int groupPosition,int childPosition,int value,String StartTime,String EndTime)
    {
    	
    	if(groupPosition==0)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setAudioWork(value);
    			ParamManager.runValue[groupPosition][childPosition]=String.valueOf(value);
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setAudioWorkstart(StartTime);
    			tb_vmc_machineset.setAudioWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    		else if(childPosition==2)
    		{
    			tb_vmc_machineset.setAudioSun(value);
    			ParamManager.runValue[groupPosition][childPosition]=String.valueOf(value);
    		}
    		else if(childPosition==3)
    		{
    			tb_vmc_machineset.setAudioSunstart(StartTime);
    			tb_vmc_machineset.setAudioSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    	}
    	else if(groupPosition==1)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setTempWork(value);
    			ParamManager.runValue[groupPosition][childPosition]=String.valueOf(value);
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setTempWorkstart(StartTime);
    			tb_vmc_machineset.setTempWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    		else if(childPosition==2)
    		{
    			tb_vmc_machineset.setTempSunstart(StartTime);
    			tb_vmc_machineset.setTempSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    	}
    	else if(groupPosition==2)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setLigntWorkstart(StartTime);
    			tb_vmc_machineset.setLigntWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setLigntSunstart(StartTime);
    			tb_vmc_machineset.setLigntSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    	}
    	else if(groupPosition==3)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setColdWorkstart(StartTime);
    			tb_vmc_machineset.setColdWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setColdSunstart(StartTime);
    			tb_vmc_machineset.setColdSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    	}
    	else if(groupPosition==4)
    	{
    		if(childPosition==0)
    		{
    			tb_vmc_machineset.setChouWorkstart(StartTime);
    			tb_vmc_machineset.setChouWorkend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    		else if(childPosition==1)
    		{
    			tb_vmc_machineset.setChouSunstart(StartTime);
    			tb_vmc_machineset.setChouSunend(EndTime);
    			ParamManager.runValue[groupPosition][childPosition]=StartTime+" --"+EndTime;
    		}
    	}  
    	MachineExpanseListAdapter adapter = new MachineExpanseListAdapter(this,ParamManager.runValue);
        this.emachinelistview.setAdapter(adapter);
	}
    //保存运行参数数据
    private void saverunparam()
    {
    	vmc_machinesetDAO machinesetDAO = new vmc_machinesetDAO(ParamManager.this);// 创建InaccountDAO对象
    	tb_vmc_machineset.setLogoStr(imgDir);
    	try {			
	    	machinesetDAO.add(tb_vmc_machineset);    
	    	// 弹出信息提示
	        Toast.makeText(ParamManager.this, "数据更新成功！", Toast.LENGTH_SHORT).show();	            
        
		} catch (Exception e)
		{
			// TODO: handle exception
			ToolClass.failToast("数据添加失败！");	
		}	    		    	
	}
    @Override
	protected void onDestroy() {
    	//=============
		//Server服务相关
		//=============
		//5.解除注册接收器
		localBroadreceiver.unregisterReceiver(receiver);
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
}


