package com.easivend.app.maintain;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class OpendoorTest extends Activity 
{
	private EditText edtcabid=null,edthuoid=null;
	private Button btnchuhuo=null,btncancel=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opendoortest);
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		edtcabid = (EditText) findViewById(R.id.edtcabid);
		edthuoid = (EditText) findViewById(R.id.edthuoid);
		//出货
		btnchuhuo = (Button)findViewById(R.id.btnchuhuo);		
		btnchuhuo.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	//心跳
		    	String httpStr="";
				String target2 = httpStr+"/api/vmcPoll";	//要提交的目标地址
				
				//4.准备加载信息设置
				StringRequest stringRequest2 = new StringRequest(Method.POST, target2,  new Response.Listener<String>() {  
					@Override  
					public void onResponse(String response) {  
					   
//					  //如果请求成功
//						result = response;	//获取返回的字符串
//						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
//						JSONObject object;
//						try {
//							object = new JSONObject(result);
//							int errType =  object.getInt("Error");
//							//返回有故障
//							if(errType>0)
//							{
//								tomain2.what=SETERRFAILHEARTMAIN;
//								tomain2.obj=object.getString("Message");
//								ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail2]SETERRFAILHEARTMAIN","server.txt");
//							}
//							else
//							{
//								tomain2.what=SETHEARTMAIN;
//								tomain2.obj=result;
//								ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok2]","server.txt");
//							}
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}										    	    
//						mainhand.sendMessage(tomain2); // 发送消息
					}  
				}, new Response.ErrorListener() {  
					@Override  
					public void onErrorResponse(VolleyError error) {  
//						result = "请求失败！";
//						tomain2.what=SETFAILMAIN;
//						mainhand.sendMessage(tomain2); // 发送消息
//						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail2]SETFAILMAIN"+result,"server.txt");
					}  
				}) 
				{  
					@Override  
					protected Map<String, String> getParams() throws AuthFailureError {  
						//3.添加params
						Map<String, String> map = new HashMap<String, String>();  
//						map.put("Token", Tok);  
//						map.put("LastPollTime", ToolClass.getLasttime());
//						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
						return map;  
				   }  
				}; 	
				//5.加载信息并发送到网络上
//				mQueue.add(stringRequest2);	
		    }
		});
		//退出
		btncancel = (Button)findViewById(R.id.btncancel);		
		btncancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	finish();
		    }
		});
	}
	
}
