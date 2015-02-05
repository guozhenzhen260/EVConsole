package com.example.evconsole;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TimePicker;


public class ParamManager extends TabActivity 
{
	private TabHost mytabhost = null;
	private int[] layres=new int[]{R.id.tab_machine,R.id.tab_device,R.id.tab_run};//内嵌布局文件的id
	private static final int TIME_DIALOG_ID = 0;// 创建时间对话框常量
	private EditText edtrstTime=null;
	private Switch switchisNet = null;  
	private Spinner spinparamsort=null;
	//排序有关的定义
    private ArrayAdapter<String> arrayadapter = null;
	private List<String> dataSortID = null,dataSortName=null;
	private int mHour=0,mMinute=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.parammanage);		
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.parammanage, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabmachine=this.mytabhost.newTabSpec("tab0");
        myTabmachine.setIndicator("机器参数配置");
        myTabmachine.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabmachine); 
    	
    	TabSpec myTabdevice=this.mytabhost.newTabSpec("tab1");
    	myTabdevice.setIndicator("设备参数配置");
    	myTabdevice.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabdevice); 
    	
    	TabSpec myTabrun=this.mytabhost.newTabSpec("tab2");
    	myTabrun.setIndicator("运行参数配置");
    	myTabrun.setContent(this.layres[2]);
    	this.mytabhost.addTab(myTabrun); 
    	
    	//===============
    	//机器参数配置页面
    	//===============
    	edtrstTime = (EditText) findViewById(R.id.edtrstTime);// 获取时间文本框
    	edtrstTime.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
            @Override
            public void onClick(View arg0) {
                showDialog(TIME_DIALOG_ID);// 显示日期选择对话框
            }
        });
    	//排序
    	this.spinparamsort = (Spinner) super.findViewById(R.id.spinparamsort);
    	showsortInfo();
    	switchisNet = (Switch)findViewById(R.id.switchisNet); //获取到控件  
    	switchisNet.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
			}  
            
            
        });   
	}
	//===============
	//机器参数配置页面
	//===============
	// spinner显示商品检索信息
	private void showsortInfo() 
	{	    
	    // 获取所有收入信息，并存储到List泛型集合中
	    dataSortID = new ArrayList<String>();
	    dataSortName = new ArrayList<String>();
	    dataSortID.add("sale");
	    dataSortName.add("sale销售情况");
	    dataSortID.add("marketPrice");
	    dataSortName.add("marketPrice原价");
	    dataSortID.add("salesPrice");
	    dataSortName.add("salesPrice销售价");
	    dataSortID.add("shelfLife");
	    dataSortName.add("shelfLife过保质期");
	    dataSortID.add("colucount");
	    dataSortName.add("colucount剩余数量");
	    dataSortID.add("onloadTime");
	    dataSortName.add("onloadTime上架时间");
	    dataSortID.add("shoudong");
	    dataSortName.add("shoudong手动更改");	    
	    // 使用字符串数组初始化ArrayAdapter对象
	    arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSortName);
	    spinparamsort.setAdapter(arrayadapter);// 为ListView列表设置数据源
	}
	
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
    	edtrstTime.setText(new StringBuilder().append(mHour).append(":").append(mMinute + 1));
    }
}
