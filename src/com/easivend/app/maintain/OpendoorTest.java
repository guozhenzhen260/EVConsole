package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.easivend.common.ToolClass;
import com.easivend.http.Zhifubaohttp;
import com.example.evconsole.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OpendoorTest extends Activity 
{
	private TextView txtzhifubaotest=null;
	private EditText edtzhifubaotest=null;
	private Button btnzhifubaotestok=null,btnzhifubaotestcancel=null,btnzhifubaotestquery=null
			,btnzhifubaotestdelete=null,btnzhifubaotestpayout=null;
	private String editstr="";
	private int editread=0;
	EditText editTextTimeCOMA;
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opendoortest);
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		
		editTextTimeCOMA=(EditText)findViewById(R.id.editTextTimeCOMA);
        editTextTimeCOMA.setInputType(InputType.TYPE_NULL);  
        editTextTimeCOMA.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				editstr=s.toString().trim();	
				//Log.i("EV_JNI","String s="+editstr);
				editread=10;
			}
		});
		
        //发送订单
  		btnzhifubaotestok = (Button)findViewById(R.id.btnzhifubaotestok);
  		btnzhifubaotestok.setOnClickListener(new OnClickListener() {
  		    @Override
  		    public void onClick(View arg0) {
	  		    	editTextTimeCOMA.setFocusable(true);
					editTextTimeCOMA.setFocusableInTouchMode(true);
					editTextTimeCOMA.requestFocus();
//  		    	barzhifubaotest.setVisibility(View.VISIBLE);
//  		    	// 将信息发送到子线程中
//  		    	childhand=zhifubaohttp.obtainHandler();
//  				Message childmsg=childhand.obtainMessage();
//  				childmsg.what=Zhifubaohttp.SETCHILD;
//  				JSONObject ev=null;
//  				try {
//  					ev=new JSONObject();
//  					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
//  			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
//  			        out_trade_no=id+datetime;
//  			        ev.put("out_trade_no", out_trade_no);
//  					ev.put("total_fee", edtzhifubaotest.getText());
//  					Log.i("EV_JNI","Send0.1="+ev.toString());
//  				} catch (JSONException e) {
//  					// TODO Auto-generated catch block
//  					e.printStackTrace();
//  				}
//  				childmsg.obj=ev;
//  				childhand.sendMessage(childmsg);
  		    }
  		});
  		timer.scheduleWithFixedDelay(task, 100, 10, TimeUnit.MILLISECONDS);  

		//退出
		btnzhifubaotestcancel = (Button)findViewById(R.id.btnzhifubaotestcancel);			
		btnzhifubaotestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	finish();
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
		        	 if(editread>0)
		        		 editread--;
		        	 if(editread==0)
		        	 {
		        		 if(editstr.equals("")==false)
		        		 {
		        			 int length=editstr.length();
		        			 if(length<18)
		        			 {
		        				 Log.i("EV_JNI","String edit=readerr!!!");
		        				 editstr="";
			        			 editTextTimeCOMA.setText("");
			        			 editTextTimeCOMA.setFocusable(true);
			     				 editTextTimeCOMA.setFocusableInTouchMode(true);
			     				 editTextTimeCOMA.requestFocus();
		        			 }
		        			 else
		        			 {
		        				 Log.i("EV_JNI","String edit="+editstr);
		        				 editstr="";
			        			 editTextTimeCOMA.setText("");
			        			 editTextTimeCOMA.setFocusable(false);
		        			 }	        			 
		        			 
		        		 }
		        	 }
		        } 
            });
        }     	    
    };
	
}
