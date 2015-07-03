package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.google.gson.Gson;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EVServerhttp implements Runnable {
	public final static int SETCHILD=2;//what标记,发送给子线程签到
	public final static int SETMAIN=1;//what标记,发送给主线程签到完成返回	
	public final static int SETERRFAILMAIN=4;//what标记,发送给主线程签到故障失败返回	
	public final static int SETHEARTCHILD=5;//what标记,发送给子线程心跳
	public final static int SETCLASSCHILD=6;//what标记,发送给子线程心跳
	public final static int SETFAILMAIN=3;//what标记,发送给主线程网络失败返回	
	String result = "";
	String Tok="";	
	String httpStr="";
	private Handler mainhand=null,childhand=null;
	
	public EVServerhttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread start","server.txt");
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case SETCHILD://子线程接收主线程签到消息
					String vmc_no="";
					String vmc_auth_code="";
					//1.得到本机编号信息
					JSONObject ev=null;
					try {
						ev = new JSONObject(msg.obj.toString());
						vmc_no=ev.getString("vmc_no");
						vmc_auth_code=ev.getString("vmc_auth_code");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//得到服务端地址信息
					Map<String, String> list=ToolClass.ReadConfigFile();
					if(list!=null)
					{				       
						httpStr= list.get("server");
					}
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 签到="+httpStr,"server.txt");
										
					//设备签到
					String target = httpStr+"/api/vmcCheckin";	//要提交的目标地址
					HttpClient httpclient = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost = new HttpPost(target);	//创建HttpPost对象
					//1.添加到类集中，其中key,value类型为String
					Map<String,Object> parammap = new TreeMap<String,Object>() ;
					parammap.put("vmc_no",vmc_no);
					parammap.put("vmc_auth_code",vmc_auth_code);			
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap.toString(),"server.txt");
					//将2.map类集转为json格式
					Gson gson=new Gson();
					String param=gson.toJson(parammap);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param.toString(),"server.txt");
					//3.添加params
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("param", param));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+params.toString(),"server.txt");
					try {
						httppost.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient.execute(httppost);	//执行HttpClient请求
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							
						}else{
							result = "请求失败！";
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
						//向主线程返回签到信息
						Message tomain=mainhand.obtainMessage();
						JSONObject object=new JSONObject(result);
						int errType =  object.getInt("Error");
						//返回有故障
						if(errType>0)
						{
							tomain.what=SETERRFAILMAIN;
							tomain.obj=object.getString("Message");
						}
						else
						{
							tomain.what=SETMAIN;
							Tok=object.getString("Token");
						}			    	    
			    	    mainhand.sendMessage(tomain); // 发送消息
					} 
			       catch (Exception e) 
			       {  
			           //e.printStackTrace();  
			    	   //向主线程返回网络失败信息
						Message tomain=mainhand.obtainMessage();
			    	    tomain.what=SETFAILMAIN;
			    	    mainhand.sendMessage(tomain); // 发送消息
			       }
					break;	
				case SETHEARTCHILD://子线程接收主线程心跳消息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 心跳","server.txt");
					//心跳
					String target2 = httpStr+"/api/vmcPoll";	//要提交的目标地址
					
					HttpClient httpclient2 = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost2 = new HttpPost(target2);	//创建HttpPost对象
					//添加到类集中，其中key,value类型为String
//					Map<String,Object> parammap = new TreeMap<String,Object>() ;
//					parammap.put("Token",Tok);
//					parammap.put("LastPollTime",new Date());			
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER",parammap.toString(),"server.txt");
//					//将map类集转为json格式
//					Gson gson=new Gson();
//					String param=gson.toJson(parammap);		
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER",param.toString(),"server.txt");
					//添加params
					List<NameValuePair> params2 = new ArrayList<NameValuePair>();
					params2.add(new BasicNameValuePair("Token", Tok));
					params2.add(new BasicNameValuePair("LastPollTime", getLasttime()));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+params2.toString(),"server.txt");
					try {
						httppost2.setEntity(new UrlEncodedFormEntity(params2, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient2.execute(httppost2);	//执行HttpClient请求
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							
						}else{
							result = "请求失败！";
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
						//向主线程返回签到信息
//						Message tomain=mainhand.obtainMessage();
//						JSONObject object=new JSONObject(result);
//						int errType =  object.getInt("Error");
//						//返回有故障
//						if(errType>0)
//						{
//							tomain.what=SETERRFAILMAIN;
//							tomain.obj=object.getString("Message");
//						}
//						else
//						{
//							tomain.what=SETMAIN;
//							Tok=object.getString("Token");
//						}			    	    
//			    	    mainhand.sendMessage(tomain); // 发送消息
					}
					catch (Exception e) 
			        {  
			           //e.printStackTrace();  
			    	   //向主线程返回网络失败信息
						Message tomain=mainhand.obtainMessage();
			    	    tomain.what=SETFAILMAIN;
			    	    mainhand.sendMessage(tomain); // 发送消息
			        }
					break;
				case SETCLASSCHILD://获取商品分类
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取商品分类","server.txt");
					String target3 = httpStr+"/api/productClass";	//要提交的目标地址
					
					HttpClient httpclient3 = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost3 = new HttpPost(target3);	//创建HttpPost对象
					//添加到类集中，其中key,value类型为String
//					Map<String,Object> parammap = new TreeMap<String,Object>() ;
//					parammap.put("Token",Tok);
//					parammap.put("LastPollTime",new Date());			
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER",parammap.toString(),"server.txt");
//					//将map类集转为json格式
//					Gson gson=new Gson();
//					String param=gson.toJson(parammap);		
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER",param.toString(),"server.txt");
					//添加params
					List<NameValuePair> params3 = new ArrayList<NameValuePair>();
					params3.add(new BasicNameValuePair("Token", Tok));
					params3.add(new BasicNameValuePair("LastPollTime", getLasttime()));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+params3.toString(),"server.txt");
					try {
						httppost3.setEntity(new UrlEncodedFormEntity(params3, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient3.execute(httppost3);	//执行HttpClient请求
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							
						}else{
							result = "请求失败！";
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
						//向主线程返回签到信息
//						Message tomain=mainhand.obtainMessage();
//						JSONObject object=new JSONObject(result);
//						int errType =  object.getInt("Error");
//						//返回有故障
//						if(errType>0)
//						{
//							tomain.what=SETERRFAILMAIN;
//							tomain.obj=object.getString("Message");
//						}
//						else
//						{
//							tomain.what=SETMAIN;
//							Tok=object.getString("Token");
//						}			    	    
//			    	    mainhand.sendMessage(tomain); // 发送消息
					}
					catch (Exception e) 
			        {  
			           //e.printStackTrace();  
			    	   //向主线程返回网络失败信息
						Message tomain=mainhand.obtainMessage();
			    	    tomain.what=SETFAILMAIN;
			    	    mainhand.sendMessage(tomain); // 发送消息
			        }
					break;
				default:
					break;
				}
			}
			
		};
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
	
	private String getLasttime()
	{
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd"); //精确到毫秒 
		SimpleDateFormat tempTime = new SimpleDateFormat("hh:mm:ss"); //精确到毫秒 
        String datetime = tempDate.format(new java.util.Date()).toString()+"T"
        		+tempTime.format(new java.util.Date()).toString(); 
		return datetime;
	}

}
