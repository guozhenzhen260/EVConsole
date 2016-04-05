package com.easivend.app.business;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.app.maintain.HuodaoSet;
import com.easivend.app.maintain.HuodaoTest;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.model.Tb_vmc_column;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class BusZhitihuo extends Activity 
{
	public static BusZhitihuo BusZhitihuoAct=null;
	private final static int REQUEST_CODE=1;//声明请求标识
	private final int SPLASH_TIMEOUT_LENGHT = 5*60; //  5*60延迟5分钟
	private int recLen = SPLASH_TIMEOUT_LENGHT; 
	EditText txtadsTip=null;
	ImageButton btnads1=null,btnads2=null,btnads3=null,btnads4=null,btnads5=null,btnads6=null,
			   btnads7=null,btnads8=null,btnads9=null,btnads0=null,btnadscancel=null,
			   btnadsenter=null,imgbtnbuszhitihuoback=null;
	Button btnadsclass=null;
	StringBuilder str=new StringBuilder();
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	private String zhifutype = "5";//0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描,5取货密码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhitihuo);
		BusZhitihuoAct = this;
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道号=["+OrderDetail.getCabID()+"]["+OrderDetail.getColumnID()+"]","log.txt");	
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		txtadsTip = (EditText) findViewById(R.id.txtadsTip);
		txtadsTip.setFocusable(false);//不让该edittext获得焦点
		txtadsTip.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// 关闭软键盘，这样当点击该edittext的时候，不会弹出系统自带的输入法
				txtadsTip.setInputType(InputType.TYPE_NULL);
				return false;
			}
		});
		btnads1 = (ImageButton) findViewById(R.id.btnads1);		
		btnads1.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("1");
		    	txtadsTip.setText(str);		    	
		    }
		});
		btnads2 = (ImageButton) findViewById(R.id.btnads2);
		btnads2.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("2");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads3 = (ImageButton) findViewById(R.id.btnads3);
		btnads3.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("3");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads4 = (ImageButton) findViewById(R.id.btnads4);
		btnads4.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("4");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads5 = (ImageButton) findViewById(R.id.btnads5);
		btnads5.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("5");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads6 = (ImageButton) findViewById(R.id.btnads6);
		btnads6.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("6");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads7 = (ImageButton) findViewById(R.id.btnads7);
		btnads7.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("7");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads8 = (ImageButton) findViewById(R.id.btnads8);
		btnads8.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("8");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads9 = (ImageButton) findViewById(R.id.btnads9);
		btnads9.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("9");
		    	txtadsTip.setText(str);	
		    }
		});
		btnads0 = (ImageButton) findViewById(R.id.btnads0);
		btnads0.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	str.append("0");
		    	txtadsTip.setText(str);	
		    }
		});
		btnadscancel = (ImageButton) findViewById(R.id.btnadscancel);
		btnadscancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	if(str.length()>0)
		    	{
			    	str.deleteCharAt(str.length()-1); 
			    	txtadsTip.setText(str);
		    	}
		    }
		});
		btnadsenter = (ImageButton) findViewById(R.id.btnadsenter);
		btnadsenter.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	boolean rst=ToolClass.getzhitihuo(BusZhitihuo.this, OrderDetail.getCabID(), OrderDetail.getColumnID(), str.toString());
		    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<提货结果="+rst,"log.txt");
		    	if(rst)
		    	{
		    		tochuhuo();
		    	}
		    	else
		    	{
			    	str.delete(0,str.length()); 
			    	txtadsTip.setText(str);
			    	// 弹出信息提示
		            Toast.makeText(BusZhitihuo.this, "〖提货密码〗错误！", Toast.LENGTH_LONG).show();
		    	}
		    }
		});
		imgbtnbuszhitihuoback = (ImageButton) findViewById(R.id.imgbtnbuszhitihuoback);
		imgbtnbuszhitihuoback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) { 
                timer.shutdown(); 
                finish();
            } 
		});
		btnadsclass = (Button) findViewById(R.id.btnadsclass);
		btnadsclass.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	LayoutInflater factory = LayoutInflater.from(BusZhitihuo.this);
				final View myview=factory.inflate(R.layout.zhitipwd, null);
				Dialog dialog = new AlertDialog.Builder(BusZhitihuo.this)				
				.setPositiveButton("修改", new DialogInterface.OnClickListener() 		{
						
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						EditText oldpwd = (EditText) myview.findViewById(R.id.oldpwd);
						EditText newpwd = (EditText) myview.findViewById(R.id.newpwd);
						EditText newpwd2 = (EditText) myview.findViewById(R.id.newpwd2);
						String oldstr=oldpwd.getText().toString();
						String newstr=newpwd.getText().toString();
						String newstr2=newpwd2.getText().toString();
						if(newstr.equals("")==true)
						{
							// 弹出信息提示
				            Toast.makeText(BusZhitihuo.this, "〖修改提货密码〗错误！", Toast.LENGTH_LONG).show();
						}
						else if(newstr.equals(newstr2)==false)
						{
							// 弹出信息提示
				            Toast.makeText(BusZhitihuo.this, "〖修改提货密码〗错误！", Toast.LENGTH_LONG).show();
						}
						else if(ToolClass.getzhitihuo(BusZhitihuo.this, OrderDetail.getCabID(), OrderDetail.getColumnID(), oldstr))
						{
							ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<准备修改密码"+newstr,"log.txt");
							final String tihuoPwd=newstr;
							//创建警告对话框
					    	Dialog alert=new AlertDialog.Builder(BusZhitihuo.this)
					    		.setTitle("对话框")//标题
					    		.setMessage("您确定要修改提货码吗？")//表示对话框中得内容
					    		.setIcon(R.drawable.ic_launcher)//设置logo
					    		.setPositiveButton("修改", new DialogInterface.OnClickListener()//退出按钮，点击后调用监听事件
					    			{				
						    				@Override
						    				public void onClick(DialogInterface dialog, int which) 
						    				{
						    					// TODO Auto-generated method stub	
						    					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<修改密码="+tihuoPwd,"log.txt");
//						    					// 创建InaccountDAO对象
								    			vmc_columnDAO columnDAO = new vmc_columnDAO(BusZhitihuo.this);
									            //创建Tb_inaccount对象
								    			Tb_vmc_column tb_vmc_column = new Tb_vmc_column(OrderDetail.getCabID(), OrderDetail.getColumnID(),tihuoPwd,"",0,
								    					0,0,"",0,0);
								    			
								    			columnDAO.updatetihuo(tb_vmc_column);// 添加商品信息
								    			// 弹出信息提示
									            Toast.makeText(BusZhitihuo.this, "〖修改提货密码〗成功！", Toast.LENGTH_LONG).show();
									            
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
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener()//取消按钮，点击后调用监听事件
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
		});
	}
	//调用倒计时定时器
	TimerTask task = new TimerTask() { 
        @Override 
        public void run() { 
  
            runOnUiThread(new Runnable() {      // UI thread 
		         @Override 
		        public void run()
		        { 
		            recLen--; 
		            //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<倒计时="+recLen,"log.txt");	
		            //退出页面
		            if(recLen <= 0)
		            { 
		                timer.shutdown(); 
		                finish();
		            } 
		        } 
            });
        }      
    };
    
    //跳到出货页面
  	private void tochuhuo()
  	{
  		Intent intent = null;// 创建Intent对象                
      	intent = new Intent(BusZhitihuo.this, BusHuo.class);// 使用Accountflag窗口初始化Intent
      	//OrderDetail.setOrdereID(out_trade_no);
      	OrderDetail.setPayType(Integer.parseInt(zhifutype));
      	//OrderDetail.setSmallCard(amount);
      	startActivityForResult(intent, REQUEST_CODE);// 打开Accountflag
  	}
  
  	//接收BusHuo返回信息
  	@Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		// TODO Auto-generated method stub
  		if(requestCode==REQUEST_CODE)
  		{
  			if(resultCode==BusZhitihuo.RESULT_CANCELED)
  			{
  				finish();			
  			}			
  		}
  	}

}
