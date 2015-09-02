package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_OrderAdapter;
import com.easivend.dao.vmc_logDAO;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.model.Tb_vmc_log;
import com.easivend.model.Tb_vmc_order_pay;
import com.easivend.model.Tb_vmc_order_product;
import com.easivend.model.Tb_vmc_product;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class LogOpt extends Activity {
	private EditText edtloggridstart=null,edtloggridend=null;
	private Button btnloggridquery=null,btnloggriddel=null,btnloggridexit=null;	
	private ListView lvlog=null;
	private SimpleDateFormat df;
	private String date=null;
	private int datetype=0;//1开始时间,2结束时间
	private static final int DATE_DIALOG_IDSTART = 1;// 创建开始时间对话框常量	
	private int mYear=0,mMon=0,mDay=0;
	private static final int DATE_DIALOG_IDEND = 2;// 创建结束时间对话框常量
	private int eYear=0,eMon=0,eDay=0;
	private SimpleAdapter simpleada = null;//进行数据的转换操作
	//定义显示的内容包装
    private List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
    private String[] logID;// 操作ID[pk]
    private String[] logType;// 操作类型0添加,1修改,2删除
    private String[] logDesc;// 操作描述
    private String[] logTime;//操作时间
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log);	
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		df = new SimpleDateFormat("yyyy");//设置日期格式
    	date=df.format(new Date()); 
    	mYear=Integer.parseInt(date);
    	eYear=Integer.parseInt(date);
    	df = new SimpleDateFormat("MM");//设置日期格式
    	date=df.format(new Date()); 
    	mMon=Integer.parseInt(date);
    	eMon=Integer.parseInt(date);
    	df = new SimpleDateFormat("dd");//设置日期格式
    	date=df.format(new Date()); 
    	mDay=Integer.parseInt(date);
    	eDay=Integer.parseInt(date);
    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现在时刻是"+y+"-"+m+"-"+d);   
    	lvlog = (ListView) findViewById(R.id.lvlog);
    	edtloggridstart = (EditText) findViewById(R.id.edtloggridstart);// 获取时间文本框
    	edtloggridstart.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
            @Override
            public void onClick(View arg0) {
            	datetype=DATE_DIALOG_IDSTART;
                showDialog(DATE_DIALOG_IDSTART);// 显示日期选择对话框                
            }
        });
    	edtloggridend = (EditText) findViewById(R.id.edtloggridend);// 获取时间文本框
    	edtloggridend.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
            @Override
            public void onClick(View arg0) {
            	datetype=DATE_DIALOG_IDEND;
                showDialog(DATE_DIALOG_IDEND);// 显示日期选择对话框                
            }
        });
    	//查询
    	btnloggridquery = (Button) findViewById(R.id.btnloggridquery);
    	btnloggridquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	loggrid();
		    }
		});
    	//删除查询
    	btnloggriddel = (Button) findViewById(R.id.btnloggriddel);
    	btnloggriddel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	delloggrid();
		    }
		});
    	//退出
    	btnloggridexit = (Button) findViewById(R.id.btnloggridexit);
    	btnloggridexit.setOnClickListener(new OnClickListener() {
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
				+"],["+screenHeight+"]","log.txt");	
		
    	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) lvlog.getLayoutParams(); // 取控件mGrid当前的布局参数
    	linearParams.height =  screenHeight-700;// 当控件的高强制设成75象素
    	lvlog.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
	}
	@Override
    protected Dialog onCreateDialog(int id) {// 重写onCreateDialog方法	
        switch (id)
        {
	        case DATE_DIALOG_IDSTART:// 弹出日期选择对话框
	            return new DatePickerDialog(this, mDateSetListener, mYear, mMon-1, mDay);            
	        case DATE_DIALOG_IDEND:// 弹出日期选择对话框
	            return new DatePickerDialog(this, mDateSetListener, eYear, eMon-1, eDay);    
        }
        return null;
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener= new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			switch (datetype)
	        {
		        case DATE_DIALOG_IDSTART:// 弹出日期选择对话框
		        	mYear=year;
					mMon=(monthOfYear+1);
					mDay=dayOfMonth;
					edtloggridstart.setText(mYear+"-"+mMon+"-"+mDay);
					break;
	            //edtloggridstart.setText(mYear+"-"+mMon+"-"+mDay);
		        case DATE_DIALOG_IDEND:// 弹出日期选择对话框
		        	eYear=year;
					eMon=(monthOfYear+1);
					eDay=dayOfMonth; 
					edtloggridend.setText(eYear+"-"+eMon+"-"+eDay);
					break;
	        }
			
			
		}
		
	};
	//查询报表
	private void loggrid()
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<start:"+ToolClass.getDayOfMonth(mYear, mMon, mDay)+"end:"+ToolClass.getDayOfMonth(eYear, eMon, eDay)+"时间大小="+ToolClass.dateCompare(ToolClass.getDayOfMonth(mYear, mMon, mDay),ToolClass.getDayOfMonth(eYear, eMon, eDay)),"log.txt");
		if(
				(!edtloggridstart.getText().toString().isEmpty())
			  &&(!edtloggridend.getText().toString().isEmpty())
			  &&(ToolClass.dateCompare(ToolClass.getDayOfMonth(mYear, mMon, mDay),ToolClass.getDayOfMonth(eYear, eMon, eDay))<0)
		  )
		{
			String mYearStr=null,mMonthStr=null,mDayStr=null;
			String eYearStr=null,eMonthStr=null,eDayStr=null;
			
			mYearStr=((mYear<10)?("0"+String.valueOf(mYear)):String.valueOf(mYear));
			mMonthStr=((mMon<10)?("0"+String.valueOf(mMon)):String.valueOf(mMon));
			mDayStr=((mDay<10)?("0"+String.valueOf(mDay)):String.valueOf(mDay));
			eYearStr=((eYear<10)?("0"+String.valueOf(eYear)):String.valueOf(eYear));
			eMonthStr=((eMon<10)?("0"+String.valueOf(eMon)):String.valueOf(eMon));
			eDayStr=((eDay<10)?("0"+String.valueOf(eDay)):String.valueOf(eDay));
			// 创建InaccountDAO对象
			vmc_logDAO logDAO = new vmc_logDAO(LogOpt.this);
			String start=mYearStr+"-"+mMonthStr+"-"+mDayStr;
			String end=eYearStr+"-"+eMonthStr+"-"+eDayStr;	
			List<Tb_vmc_log> listinfos=logDAO.getScrollPay(start,end);
			String[] strInfos = new String[listinfos.size()];
			logID = new String[listinfos.size()];
			logType = new String[listinfos.size()];
			logDesc = new String[listinfos.size()];
			logTime = new String[listinfos.size()];
			int m=0;
			// 遍历List泛型集合
		    for (Tb_vmc_log tb_inaccount : listinfos) 
		    {
		    	//总支付订单
		    	logID[m]= tb_inaccount.getLogID();
		    	logType[m] = ToolClass.typestr(3,tb_inaccount.getLogType());
		    	logDesc[m] = tb_inaccount.getLogDesc();
		    	logTime[m] = tb_inaccount.getLogTime();			
		    	m++;// 标识加1
		    }
			
			int x=0;
			this.listMap.clear();
			for(x=0;x<listinfos.size();x++)
			{
			  	Map<String,String> map = new HashMap<String,String>();//定义Map集合，保存每一行数据
			   	map.put("logID", logID[x]);
		    	map.put("logType", logType[x]);
		    	map.put("logDesc", logDesc[x]);
		    	map.put("logTime", logTime[x]);
		    	this.listMap.add(map);//保存数据行
			}
			//将这个构架加载到data_list中
			this.simpleada = new SimpleAdapter(this,this.listMap,R.layout.loglist,
			    		new String[]{"logID","logType","logDesc","logTime"},//Map中的key名称
			    		new int[]{R.id.txtlogID,R.id.txtlogType,R.id.txtlogDesc,R.id.txtlogTime});
			this.lvlog.setAdapter(this.simpleada);
			
		}
		else
		{
			Toast.makeText(LogOpt.this, "请输入正确查询时间！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//删除查询报表
	private void delloggrid()
	{
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<start:"+ToolClass.getDayOfMonth(mYear, mMon, mDay)+"end:"+ToolClass.getDayOfMonth(eYear, eMon, eDay)+"时间大小="+ToolClass.dateCompare(ToolClass.getDayOfMonth(mYear, mMon, mDay),ToolClass.getDayOfMonth(eYear, eMon, eDay)),"log.txt");
		if(
				(!edtloggridstart.getText().toString().isEmpty())
			  &&(!edtloggridend.getText().toString().isEmpty())
			  &&(ToolClass.dateCompare(ToolClass.getDayOfMonth(mYear, mMon, mDay),ToolClass.getDayOfMonth(eYear, eMon, eDay))<0)
		  )
		{
			//创建警告对话框
	    	Dialog alert=new AlertDialog.Builder(LogOpt.this)
	    		.setTitle("对话框")//标题
	    		.setMessage("您确定要删除该记录吗？")//表示对话框中得内容
	    		.setIcon(R.drawable.ic_launcher)//设置logo
	    		.setPositiveButton("删除", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
	    			{				
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) 
		    				{
		    					// TODO Auto-generated method stub	
		    					String mYearStr=null,mMonthStr=null,mDayStr=null;
		    					String eYearStr=null,eMonthStr=null,eDayStr=null;
		    					
		    					mYearStr=((mYear<10)?("0"+String.valueOf(mYear)):String.valueOf(mYear));
		    					mMonthStr=((mMon<10)?("0"+String.valueOf(mMon)):String.valueOf(mMon));
		    					mDayStr=((mDay<10)?("0"+String.valueOf(mDay)):String.valueOf(mDay));
		    					eYearStr=((eYear<10)?("0"+String.valueOf(eYear)):String.valueOf(eYear));
		    					eMonthStr=((eMon<10)?("0"+String.valueOf(eMon)):String.valueOf(eMon));
		    					eDayStr=((eDay<10)?("0"+String.valueOf(eDay)):String.valueOf(eDay));
		    					// 创建InaccountDAO对象
		    					vmc_logDAO logDAO = new vmc_logDAO(LogOpt.this);
		    					String start=mYearStr+"-"+mMonthStr+"-"+mDayStr;
		    					String end=eYearStr+"-"+eMonthStr+"-"+eDayStr;	
		    					logDAO.detele(start,end);
		    					// 弹出信息提示
					            Toast.makeText(LogOpt.this, "记录删除成功！", Toast.LENGTH_SHORT).show();
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
		else
		{
			Toast.makeText(LogOpt.this, "请输入正确查询时间！", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onDestroy() {
    	//退出时，返回intent
        Intent intent=new Intent();
        setResult(MaintainActivity.RESULT_CANCELED,intent);
		super.onDestroy();		
	}
}
