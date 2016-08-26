package com.easivend.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easivend.app.maintain.ParamManager;
import com.easivend.common.MediaFileAdapter;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.XZip;
import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;

public class EVServerhttp implements Runnable {
	RequestQueue mQueue = null; 
	String vmc_no="";
	String vmc_auth_code="";
	public final static int SETNONE=0;//what标记,发送给主线程无效标识
	
	public final static int SETCHILD=2;//what标记,发送给子线程签到
	public final static int SETMAIN=1;//what标记,发送给主线程签到完成返回	
	public final static int SETERRFAILMAIN=4;//what标记,发送给主线程签到故障失败返回	
	
	public final static int SETHEARTCHILD=5;//what标记,发送给子线程心跳
	public final static int SETERRFAILHEARTMAIN=6;//what标记,发送给主线程心跳故障
	public final static int SETHEARTMAIN=7;//what标记,发送给主线程获取心跳返回
	
	public final static int SETCLASSCHILD=8;//what标记,发送给子线程获取商品分类
	public final static int SETERRFAILCLASSMAIN=9;//what标记,发送给主线程获取商品分类故障
	public final static int SETCLASSMAIN=10;//what标记,发送给主线程获取商品分类返回
	
	public final static int SETJOINCLASSCHILD=54;//what标记,发送给子线程获取商品分类对应的商品id
	public final static int SETERRFAILJOINCLASSMAIN=55;//what标记,发送给主线程获取商品分类对应的商品id故障
	public final static int SETJOINCLASSMAIN=56;//what标记,发送给主线程获取商品分类对应的商品id返回
	
	public final static int SETPRODUCTCHILD=11;//what标记,发送给子线程获取商品信息
	public final static int SETERRFAILRODUCTMAIN=12;//what标记,发送给主线程获取商品故障
	public final static int SETRODUCTMAIN=13;//what标记,发送给主线程获取商品返回
	
	public final static int SETHUODAOCHILD=14;//what标记,发送给子线程获取货道信息
	public final static int SETERRFAILHUODAOMAIN=15;//what标记,发送给主线程获取货道故障
	public final static int SETHUODAOMAIN=16;//what标记,发送给主线程获取货道返回
	
	public final static int SETHUODAOSTATUCHILD=17;//what标记,发送给子线程上报货道信息
	public final static int SETERRFAILHUODAOSTATUMAIN=18;//what标记,发送给主线程上报货道信息故障
	public final static int SETHUODAOSTATUMAIN=19;//what标记,发送给主线程上报货道信息
	
	public final static int SETDEVSTATUCHILD=20;//what标记,发送给子线程上报设备状态信息
	public final static int SETERRFAILDEVSTATUMAIN=21;//what标记,发送给主线程获取设备故障
	public final static int SETDEVSTATUMAIN=22;//what标记,发送给主线程获取设备返回	
	
	public final static int SETRECORDCHILD=23;//what标记,发送给子线程上报交易记录
	public final static int SETERRFAILRECORDMAIN=24;//what标记,发送给主线程上报交易故障
	public final static int SETRECORDMAIN=25;//what标记,发送给主线程获取上报交易返回
	
	public final static int SETPVERSIONCHILD=27;//what标记,发送给子线程获取版本信息
	public final static int SETERRFAILVERSIONMAIN=28;//what标记,发送给主线程获取版本故障
	public final static int SETVERSIONMAIN=29;//what标记,发送给主线程获取版本返回
	public final static int SETINSTALLMAIN=30;//what标记,发送给主线程重新安装程序
	
	public final static int SETLOGCHILD=31;//what标记,发送给子线程获取日志信息
	public final static int SETERRFAILLOGMAIN=32;//what标记,发送给主线程获取日志故障
	public final static int SETLOGMAIN=33;//what标记,发送给主线程获取日志返回
	
	public final static int SETACCOUNTCHILD=34;//what标记,发送给子线程获取支付宝微信账号信息
	public final static int SETERRFAILACCOUNTMAIN=35;//what标记,发送给主线程获取支付宝微信账号故障
	public final static int SETACCOUNTMAIN=36;//what标记,发送给主线程获取支付宝微信账号返回
	public final static int SETACCOUNTRESETMAIN=37;//what标记,发送给主线程支付宝微信账号重新设置
	
	public final static int SETADVCHILD=38;//what标记,发送给子线程获取广告信息
	public final static int SETERRFAILADVMAIN=39;//what标记,发送给主线程获取广告故障
	public final static int SETADVMAIN=40;//what标记,发送给主线程获取广告返回
	public final static int SETADVRESETMAIN=41;//what标记,发送给主线程广告更新完成
	
	public final static int SETCLIENTCHILD=42;//what标记,发送给子线程获取设备信息
	public final static int SETERRFAILCLIENTMAIN=43;//what标记,发送给主线程获取设备故障
	public final static int SETCLIENTMAIN=44;//what标记,发送给主线程获取设备返回
	
	public final static int SETEVENTINFOCHILD=45;//what标记,发送给子线程获取活动信息
	public final static int SETERRFAILEVENTINFOMAIN=46;//what标记,发送给主线程获取活动故障
	public final static int SETEVENTINFOMAIN=47;//what标记,发送给主线程获取活动返回
	
	public final static int SETDEMOINFOCHILD=48;//what标记,发送给子线程获取购买演示信息
	public final static int SETERRFAILDEMOINFOMAIN=49;//what标记,发送给主线程获取购买演示故障
	public final static int SETDEMOINFOMAIN=50;//what标记,发送给主线程获取购买演示返回
	
	public final static int SETPICKUPCHILD=51;//what标记,发送给子线取货码信息
	public final static int SETERRFAILPICKUPMAIN=52;//what标记,发送给主线程取货码无效
	public final static int SETPICKUPMAIN=53;//what标记,发送给主线程取货码出货
	
	public final static int SETCHECKCHILD=26;//what标记,发送给子线程更改签到信息码
	public final static int SETFAILMAIN=3;//what标记,发送给主线程网络失败返回	
	String result = "";
	String Tok="";	
	String httpStr="http://easivend.net.cn/shj";
	private Handler mainhand=null,childhand=null;
	
	public EVServerhttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	
	public RequestQueue getRequestQueue() {
        if (mQueue == null) {
            // getApplicationContext()是关键, 它会避免
            // Activity或者BroadcastReceiver带来的缺点.
        	mQueue = Volley.newRequestQueue(ToolClass.getContext());
        }
        //强制清除缓存
        File cacheDir = new File(ToolClass.getContext().getCacheDir(), "volley");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        mQueue.start();

        // clear all volley caches.
        mQueue.add(new ClearCacheRequest(cache, null));
        return mQueue;
    }
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread start["+Thread.currentThread().getId()+"]","server.txt");
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case SETCHILD://子线程接收主线程签到消息					
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
					//httpStr="http://easivend.net.cn/shj";
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 签到["+Thread.currentThread().getId()+"]="+httpStr,"server.txt");
					//新建Volley 
					mQueue = getRequestQueue();
					//2.设置参数,设置服务器地址
					String target = httpStr+"/api/vmcCheckin";	//要提交的目标地址
					//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread target="+target,"server.txt");
					//1.添加到类集中，其中key,value类型为String
					Map<String,Object> parammap = new TreeMap<String,Object>() ;
					parammap.put("vmc_no",vmc_no);
					parammap.put("vmc_auth_code",vmc_auth_code);			
					//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap.toString(),"server.txt");
					//将2.map类集转为json格式
					Gson gson=new Gson();
					final String param=gson.toJson(parammap);		
					//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param.toString(),"server.txt");
					
					//向主线程返回信息
					final Message tomain1=mainhand.obtainMessage();
					tomain1.what=SETNONE;
					//4.设备签到
					StringRequest stringRequest = new StringRequest(Method.POST, target,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                          //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","一期的后台","server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain1.what=SETERRFAILMAIN;
									tomain1.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail1]SETERRFAILMAIN","server.txt");
								}
								else
								{
									tomain1.what=SETMAIN;
									Tok=object.getString("Token");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok1]","server.txt");
								}	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
				    	    mainhand.sendMessage(tomain1); // 发送消息
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "请求失败！";
							tomain1.what=SETFAILMAIN;
							mainhand.sendMessage(tomain1); // 发送消息
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail1]SETFAILMAIN"+result,"server.txt");  
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.添加params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("param", param);  
					        //params.add(new BasicNameValuePair("param", param));
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+param.toString(),"server.txt");
					        return map;  
					    }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest);
					break;
				case SETHEARTCHILD://子线程接收主线程心跳消息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 心跳["+Thread.currentThread().getId()+"]","server.txt");
					//心跳
					String target2 = httpStr+"/api/vmcPoll";	//要提交的目标地址
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain2=mainhand.obtainMessage();
					tomain2.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest2 = new StringRequest(Method.POST, target2,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain2.what=SETERRFAILHEARTMAIN;
									tomain2.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail2]SETERRFAILHEARTMAIN","server.txt");
								}
								else
								{
									tomain2.what=SETHEARTMAIN;
									tomain2.obj=result;
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok2]","server.txt");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
							mainhand.sendMessage(tomain2); // 发送消息
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain2.what=SETFAILMAIN;
							mainhand.sendMessage(tomain2); // 发送消息
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail2]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LastPollTime", ToolClass.getLasttime());
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest2);				
						
					break;
				case SETCLASSCHILD://获取商品分类
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取商品分类["+Thread.currentThread().getId()+"]","server.txt");
					String target3 = httpStr+"/api/productClass";	//要提交的目标地址
					final String LAST_EDIT_TIME3=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain3=mainhand.obtainMessage();
					tomain3.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                          //如果请求成功
                        	result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain3.what=SETERRFAILCLASSMAIN;
									tomain3.obj=object.getString("Message");
									mainhand.sendMessage(tomain3); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail3]SETERRFAILCLASSMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok3]","server.txt");
									JSONObject jsonObject = new JSONObject(result); 
									if(jsonObject.has("ProductClassList")==true)
									{										
										tomain3.what=SETCLASSMAIN;
										tomain3.obj=result;	
										mainhand.sendMessage(tomain3); // 发送消息
									}
									else if(jsonObject.has("List")==true)
									{										
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok4]准备更新商品分类...","server.txt");
										classArray(result);
										if(classarr.length()>0)
										{
											updateclass(0);
										}
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}									    	    
				    	    
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "请求失败！";
							tomain3.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain3); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail3]SETFAILMAIN"+result,"server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.添加params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("Token", Tok);  
					        map.put("LAST_EDIT_TIME", LAST_EDIT_TIME3);
					        map.put("vmc_no", vmc_no); 
					        ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
					        return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest3);									
					break;
				case SETJOINCLASSCHILD://获取商品分类对应的商品信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取商品分类商品对应关系["+Thread.currentThread().getId()+"]","server.txt");
					String target20 = httpStr+"/api/productJoinClass";	//要提交的目标地址
					final String LAST_EDIT_TIME20=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain20=mainhand.obtainMessage();
					tomain20.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest20 = new StringRequest(Method.POST, target20,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                          //如果请求成功
                        	result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain20.what=SETERRFAILJOINCLASSMAIN;
									tomain20.obj=object.getString("Message");
									mainhand.sendMessage(tomain20); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail20]SETERRFAILCLASSMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok20]","server.txt");
									JSONObject jsonObject = new JSONObject(result); 
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok4]准备更新商品分类对应的商品信息...","server.txt");
									classjoinArray(result);
									if(classjoinarr.length()>0)
									{
										updateclassjoin(0);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}									    	    
				    	    
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "请求失败！";
							tomain20.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain20); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail20]SETFAILMAIN"+result,"server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.添加params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("Token", Tok);  
					        map.put("LastEditTime", LAST_EDIT_TIME20);
					        map.put("CLIENT_NO", vmc_no); 
					        ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
					        return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest20);	
					break;
				case SETPRODUCTCHILD://获取商品信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取商品信息["+Thread.currentThread().getId()+"]","server.txt");
					String target4 = httpStr+"/api/productData";	//要提交的目标地址
					final String LAST_EDIT_TIME4=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain4=mainhand.obtainMessage();
					tomain4.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest4 = new StringRequest(Method.POST, target4,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain4.what=SETERRFAILRODUCTMAIN;
									tomain4.obj=object.getString("Message");
									mainhand.sendMessage(tomain4); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail4]SETERRFAILRODUCTMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok4]准备更新商品信息...","server.txt");
									productArray(result);
									if(productarr.length()>0)
									{
										updateproduct(0);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										  
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain4.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain4); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail4]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);
							map.put("VMC_NO", vmc_no);
							map.put("PAGE_INDEX", "");
							map.put("PAGE_SIZE", "");
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME4);
							map.put("PRODUCT_NO", "");
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest4);	
					break;
				case SETHUODAOCHILD://获取货道信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取货道信息["+Thread.currentThread().getId()+"]","server.txt");
					String target5 = httpStr+"/api/vmcPathConfigDownload";	//要提交的目标地址
					final String LAST_EDIT_TIME5=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain5=mainhand.obtainMessage();
					tomain5.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest5 = new StringRequest(Method.POST, target5,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain5.what=SETERRFAILHUODAOMAIN;
									tomain5.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail5]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									tomain5.what=SETHUODAOMAIN;
									tomain5.obj=result;
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok5]","server.txt");
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
							mainhand.sendMessage(tomain5); // 发送消息
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain5.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain5); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail5]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME5);
							map.put("VMC_NO", vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest5);					
					break;										
				case EVServerhttp.SETDEVSTATUCHILD://设备状态上报
					int bill_err=0;
	    			int coin_err=0;
					//1.得到本机编号信息
					JSONObject ev7=null;
					try {
						ev7 = new JSONObject(msg.obj.toString());
						bill_err=ev7.getInt("bill_err");
						coin_err=ev7.getInt("coin_err");
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=bill_err="+bill_err+"coin_err="+coin_err
						,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 设备状态上报["+Thread.currentThread().getId()+"]","server.txt");
					//设备状态上报
					String target7 = httpStr+"/api/vmcStatus";	//要提交的目标地址
					//1.添加到类集中，其中key,value类型为String
					Map<String,Object> parammap7 = new TreeMap<String,Object>() ;
					parammap7.put("VMC_NO",vmc_no);
					parammap7.put("CLIENT_STATUS","0");
					parammap7.put("COINS_STATUS",coin_err);
					parammap7.put("NOTE_STATUS",bill_err);	
					parammap7.put("DOOR_STATUS","0");	
					parammap7.put("WAREHOUSE_TEMPERATURE","0");		
					if(ToolClass.getServerVer()==1)//一期后台
					{
						parammap7.put("CLIENT_VERSION",ToolClass.getVersion());	
						parammap7.put("CLIENT_DESC","本机版本号");	
					}
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap7.toString(),"server.txt");
					//将2.map类集转为json格式
					Gson gson7=new Gson();
					final String param7=gson7.toJson(parammap7);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param7.toString(),"server.txt");
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain7=mainhand.obtainMessage();
					tomain7.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest7 = new StringRequest(Method.POST, target7,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain7.what=SETERRFAILDEVSTATUMAIN;
									tomain7.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail6]SETERRFAILDEVSTATUMAIN","server.txt");
								}
								else
								{
									tomain7.what=SETDEVSTATUMAIN;
									tomain7.obj=result;
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok6]","server.txt");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
							mainhand.sendMessage(tomain7); // 发送消息
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain7.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain7); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail6]SETFAILMAIN","server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("param", param7);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest7);
					
					break;
				case EVServerhttp.SETRECORDCHILD://交易记录上报	
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 交易记录上报["+Thread.currentThread().getId()+"]","server.txt");
					//1.得到交易记录编号信息
					try {
						recordArray(msg.obj.toString());
						if(recordarr.length()>0)
						{
							updaterecord(0);
						}
					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					break;
				case SETHUODAOSTATUCHILD://货道状态上报消息	
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 货道状态上报["+Thread.currentThread().getId()+"]","server.txt");
					//1.得到交易记录编号信息
					try {
						columnArray(msg.obj.toString());
						if(columnarr.length()>0)
						{
							updatecolumn(0);
						}
					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					break;	
				case SETCHECKCHILD://子线程接收主线程签到消息					
					//1.得到本机编号信息
					JSONObject ev10=null;
					try {
						ev10 = new JSONObject(msg.obj.toString());
						vmc_no=ev10.getString("vmc_no");
						vmc_auth_code=ev10.getString("vmc_auth_code");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 设备签到["+Thread.currentThread().getId()+"]","server.txt");
					//设备签到
					String target11 = httpStr+"/api/vmcCheckin";	//要提交的目标地址
					//1.添加到类集中，其中key,value类型为String
					Map<String,Object> parammap11 = new TreeMap<String,Object>() ;
					parammap11.put("vmc_no",vmc_no);
					parammap11.put("vmc_auth_code",vmc_auth_code);			
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap11.toString(),"server.txt");
					//将2.map类集转为json格式
					Gson gson11=new Gson();
					final String param11=gson11.toJson(parammap11);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param11.toString(),"server.txt");
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain11=mainhand.obtainMessage();
					tomain11.what=SETNONE;
					//4.设备签到
					StringRequest stringRequest11 = new StringRequest(Method.POST, target11,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                            //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{	
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail9]SETERRFAILMAIN","server.txt");	
								}
								else
								{
									Tok=object.getString("Token");	
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok9]","server.txt");
								}	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
				    	    mainhand.sendMessage(tomain11); // 发送消息
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "请求失败！";
                        	tomain11.what=SETFAILMAIN;
    						mainhand.sendMessage(tomain11); // 发送消息	
    						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail9]SETFAILMAIN","server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.添加params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("param", param11);  					        
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
					        return map;  
					    }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest11);						
					break;	
				case SETPVERSIONCHILD://获取版本信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取版本信息["+Thread.currentThread().getId()+"]","server.txt");
					String target12 = httpStr+"/api/clientVersion";	//要提交的目标地址
					final String LAST_EDIT_TIME12=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain12=mainhand.obtainMessage();
					tomain12.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest12 = new StringRequest(Method.POST, target12,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain12.what=SETERRFAILVERSIONMAIN;
									tomain12.obj=object.getString("Message");							 	    
									mainhand.sendMessage(tomain12); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail12]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok12]","server.txt");
									versionArray(result);
									if(versionarr.length()>0)
									{
										updateversion(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										   
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain12.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain12); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail12]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME12);
							map.put("VMC_NO", vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest12);					
					break;
				case SETLOGCHILD://获取日志信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取日志信息["+Thread.currentThread().getId()+"]","server.txt");
					String target13 = httpStr+"/api/clientLogInfo";	//要提交的目标地址
					final String LAST_EDIT_TIME13=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain13=mainhand.obtainMessage();
					tomain13.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest13 = new StringRequest(Method.POST, target13,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain13.what=SETERRFAILLOGMAIN;
									tomain13.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain13); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail13]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok13]","server.txt");
									logArray(result);
									if(logarr.length()>0)
									{
										updatelog(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain13.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain13); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail13]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME13);
							map.put("VMC_NO", vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest13);					
					break;
				case SETACCOUNTCHILD://获取支付宝微信信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取支付宝微信信息["+Thread.currentThread().getId()+"]","server.txt");
					String target14 = httpStr+"/api/selectAccount";	//要提交的目标地址
					final String LAST_EDIT_TIME14=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain14=mainhand.obtainMessage();
					tomain14.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest14 = new StringRequest(Method.POST, target14,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain14.what=SETERRFAILACCOUNTMAIN;
									tomain14.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain14); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail14]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok14]","server.txt");
									AccountArray(result);
									if(Accountarr.length()>0)
									{
										updateAccount(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain14.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain14); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail14]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME14);
							map.put("VMC_NO",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest14);					
					break;
				case SETADVCHILD://获取广告信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取广告信息["+Thread.currentThread().getId()+"]","server.txt");
					String target15 = httpStr+"/api/advInfo";	//要提交的目标地址
					final String LAST_EDIT_TIME15=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain15=mainhand.obtainMessage();
					tomain15.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest15 = new StringRequest(Method.POST, target15,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain15.what=SETERRFAILADVMAIN;
									tomain15.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain15); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail15]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok15]","server.txt");
									advArray(result);
									if(advarr.length()>0)
									{
										updateadv(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain15.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain15); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail15]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>(); 
							map.put("vmc_no",vmc_no);
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME15);							
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest15);					
					break;
				case SETCLIENTCHILD://获取设备信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取设备信息["+Thread.currentThread().getId()+"]","server.txt");
					String target16 = httpStr+"/api/selectClientSetting";	//要提交的目标地址
					final String LAST_EDIT_TIME16=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain16=mainhand.obtainMessage();
					tomain16.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest16 = new StringRequest(Method.POST, target16,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain16.what=SETERRFAILCLIENTMAIN;
									tomain16.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain16); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail16]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok16]","server.txt");
									clientArray(result);
									if(clientarr.length()>0)
									{
										updateclient(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain16.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain16); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail16]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME16);	
							map.put("VMC_NO",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest16);					
					break;
				case SETEVENTINFOCHILD://获取活动信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取活动信息["+Thread.currentThread().getId()+"]","server.txt");
					
					String target18 = httpStr+"/api/eventInfo";	//要提交的目标地址
					final String LAST_EDIT_TIME18=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain18=mainhand.obtainMessage();
					tomain18.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest18 = new StringRequest(Method.POST, target18,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain18.what=SETERRFAILEVENTINFOMAIN;
									tomain18.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain18); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail18]SETERRFAILEVENTINFOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok18]","server.txt");
									eventArray(result);
									if(eventarr.length()>0)
									{
										updateevent(0);
									}									
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain18.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain18); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail18]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME18);	
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest18);					
					break;	
				case SETDEMOINFOCHILD://获取购买演示信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取购买演示信息["+Thread.currentThread().getId()+"]","server.txt");
					
					String target19 = httpStr+"/api/demoInfo";	//要提交的目标地址
					final String LAST_EDIT_TIME19=msg.obj.toString();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain19=mainhand.obtainMessage();
					tomain19.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest19 = new StringRequest(Method.POST, target19,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain19.what=SETERRFAILDEMOINFOMAIN;
									tomain19.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain19); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail19]SETERRFAILEVENTINFOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok19]","server.txt");
									demoArray(result);
									if(demoarr.length()>0)
									{
										updatedemo(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain19.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain19); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail19]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME19);
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest19);					
					break;	
				//取货码比较特殊，不能用作复制的例子
				case SETPICKUPCHILD://获取取货码
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取取货码信息["+Thread.currentThread().getId()+"]","server.txt");
					String target17 = httpStr+"/api/getPickupCodeStatus";	//要提交的目标地址
					final String PICKUP_CODE=msg.obj.toString();
					final String LAST_EDIT_TIME17=ToolClass.getLasttime();
					//新建Volley 
					mQueue = getRequestQueue();
					//向主线程返回信息
					final Message tomain17=mainhand.obtainMessage();
					tomain17.what=SETNONE;
					//4.准备加载信息设置
					StringRequest stringRequest17 = new StringRequest(Method.POST, target17,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //如果请求成功
							result = response;	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//返回有故障
								if(errType>0)
								{
									tomain17.what=SETERRFAILPICKUPMAIN;
									tomain17.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain17); // 发送消息
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok17]","server.txt");
									pickupArray(result);
									if(pickuparr.length()>0)
									{
										updatepickup(0,PICKUP_CODE);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "请求失败！";
							tomain17.what=SETERRFAILPICKUPMAIN;
				    	    mainhand.sendMessage(tomain17); // 发送消息
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.添加params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME17);	
							map.put("PICKUP_CODE", PICKUP_CODE);
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest17);					
					break;	
				default:
					break;
				}
			}
			
		};
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
	
	//==============
	//==商品分类管理模块
	//==============
	JSONArray classarr=null;
	JSONArray zhuheclassArray=null;
	JSONObject zhuheclassjson = null; 
	int classint=0;
	//分解分类信息
	private void classArray(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		classarr=jsonObject.getJSONArray("List");
		classint=0;
		zhuheclassArray=new JSONArray();
		zhuheclassjson = new JSONObject(); 
		if(classarr.length()==0)
		{
			//向主线程返回信息
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETCLASSMAIN;
			tomain.obj=zhuheclassjson.toString();
			mainhand.sendMessage(tomain); // 发送消息	
		}
	}
	
	//更新分类和图片信息
	private String updateclass(int i) throws JSONException
	{
		final JSONObject object2=classarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新分类和图片="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=object2;
		//第一步，获取图片名字ATTID
		final String CLS_URL=object2.getString("CLS_URL");
		String ATT_ID="";
		if(CLS_URL.equals("null")!=true)
		{
			String a[] = CLS_URL.split("/");  
			ATT_ID=a[a.length-1];
			ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","图片ATT_ID="+ATT_ID,"server.txt");										
			zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
		}
		else
		{
			zhuheobj.put("AttImg", "");
		}
		
		
		try
		{	
			if(ATT_ID.equals("")==true)
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","分类["+object2.getString("CLASS_NAME")+"]无图片","server.txt");
			}
			else
			{
				if(ToolClass.isImgFile(ATT_ID))
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","分类["+object2.getString("CLASS_NAME")+"]图片已存在","server.txt");
				}
				else 
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","分类["+object2.getString("CLASS_NAME")+"]图片,下载图片...","server.txt");
					//第二步.准备下载	
					String serip=httpStr.substring(0,httpStr.lastIndexOf("shj"));
					String url= serip+CLS_URL;	//要提交的目标地址
					final String ATTIDS=ATT_ID;
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
					ImageRequest imageRequest = new ImageRequest(  
							url,  
					        new Response.Listener<Bitmap>() {  
					            @Override  
					            public void onResponse(Bitmap response) {  
					            	ToolClass.saveBitmaptofile(response,ATTIDS);
					            	try {
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","分类["+object2.getString("CLASS_NAME")+"]图片,下载图片完成","server.txt");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            }  
					        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
					            @Override  
					            public void onErrorResponse(VolleyError error) {  
									result = "请求失败！";
									try {
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","分类["+object2.getString("CLASS_NAME")+"]图片,下载图片失败","server.txt");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            }  
					        });
					mQueue.add(imageRequest); 
				}
				
			}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
		}
		
		//第三步，把图片名字保存到json中		
		zhuheclassArray.put(zhuheobj);
		
		
		//第四步：进行下一个分类信息
		classint++;
		if(classint<classarr.length())
		{
			try {
				updateclass(classint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				zhuheclassjson.put("List", zhuheclassArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheclassjson.toString(),"server.txt");

			//上传给server
			//向主线程返回信息
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETCLASSMAIN;
			tomain4.obj=zhuheclassjson.toString();
			mainhand.sendMessage(tomain4); // 发送消息
		}		
		return "";
	}
	
	//=======================
	//==商品分类对应的商品信息管理模块
	//=======================
	JSONArray classjoinarr=null;
	JSONArray zhuheclassjoinArray=null;
	JSONObject zhuheclassjoinjson = null; 
	int classjoinint=0;
	//分解分类信息
	private void classjoinArray(String classjoinrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classjoinrst); 
		classjoinarr=jsonObject.getJSONArray("List");
		classjoinint=0;
		zhuheclassjoinArray=new JSONArray();
		zhuheclassjoinjson = new JSONObject(); 
		if(classjoinarr.length()==0)
		{
			//向主线程返回信息
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETJOINCLASSMAIN;
			tomain.obj=zhuheclassjoinjson.toString();
			mainhand.sendMessage(tomain); // 发送消息	
		}
	}
	
	//更新分类和图片信息
	private String updateclassjoin(int i) throws JSONException
	{
		final JSONObject object2=classjoinarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新分类对应的商品信息="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=new JSONObject();
		//第一步，获取图片有用的商品id
		int IS_DELETE=object2.getInt("IS_DELETE");
		if(IS_DELETE==0)
		{
			zhuheobj.put("PRODUCT_NO", object2.getString("PRODUCT_NO"));
			zhuheobj.put("CLASS_CODE", object2.getString("CLASS_CODE"));
			//第三步，把图片名字保存到json中		
			zhuheclassjoinArray.put(zhuheobj);
		}
				
		//第四步：进行下一个分类信息
		classjoinint++;
		if(classjoinint<classjoinarr.length())
		{
			try {
				updateclassjoin(classjoinint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				zhuheclassjoinjson.put("List", zhuheclassjoinArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheclassjoinjson.toString(),"server.txt");

			//上传给server
			//向主线程返回信息
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETJOINCLASSMAIN;
			tomain4.obj=zhuheclassjoinjson.toString();
			mainhand.sendMessage(tomain4); // 发送消息
		}		
		return "";
	}
	
	
	//==========
	//==商品管理模块
	//==========
	JSONArray productarr=null;
	JSONArray zhuheproductArray=null;
	JSONObject zhuheproductjson = null; 
	int productint=0;
	//分解商品信息
	private void productArray(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst);
		if(ToolClass.getServerVer()==0)//旧的后台
		{
			productarr=jsonObject.getJSONArray("ProductList");
			productint=0;
			zhuheproductArray=new JSONArray();
			zhuheproductjson = new JSONObject(); 
			if(productarr.length()==0)
			{
				//向主线程返回信息
				Message tomain=mainhand.obtainMessage();
				tomain.what=SETRODUCTMAIN;
				tomain.obj=zhuheproductjson.toString();
				mainhand.sendMessage(tomain); // 发送消息	
			}
		}
		else if(ToolClass.getServerVer()==1)//一期后台
		{
			productarr=jsonObject.getJSONArray("List");
			productint=0;
			zhuheproductArray=new JSONArray();
			zhuheproductjson = new JSONObject(); 
			if(productarr.length()==0)
			{
				//向主线程返回信息
				Message tomain=mainhand.obtainMessage();
				tomain.what=SETRODUCTMAIN;
				tomain.obj=zhuheproductjson.toString();
				mainhand.sendMessage(tomain); // 发送消息	
			}
		}
	}
	//更新商品和图片信息
	private String updateproduct(int i) throws JSONException
	{
		final JSONObject object2=productarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新商品和图片="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=object2;
		if(ToolClass.getServerVer()==0)//旧的后台
		{
			//第一步.获取商品图片名字
			String target6 = httpStr+"/api/productImage";	//要提交的目标地址
			JSONObject json=new JSONObject();
			try {
				json.put("VmcNo", vmc_no);
				json.put("attId", object2.getString("att_batch_id"));				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
			final String param6=json.toString();		
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param6.toString(),"server.txt");
			//新建Volley 
			mQueue = getRequestQueue();
			//4.准备加载信息设置
			StringRequest stringRequest6 = new StringRequest(Method.POST, target6,  new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {  				   
					//如果请求成功
					result = response;	//获取返回的字符串
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1[ok10]="+result,"server.txt");
					try
					{
						//第二步，获取图片名字ATTID
						JSONObject jsonObject3 = new JSONObject(result); 
						JSONArray arr3=null;
						if(ToolClass.getServerVer()==0)//旧的后台
						{
							arr3=jsonObject3.getJSONArray("ProductImageList");
						}
						else if(ToolClass.getServerVer()==1)//一期后台
						{
							arr3=jsonObject3.getJSONArray("List");
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]1","server.txt");
						JSONObject object3=arr3.getJSONObject(0);
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]2","server.txt");
						final String ATT_ID=object3.getString("ATT_ID");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]3","server.txt");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]ATT_ID="+ATT_ID,"server.txt");
						//ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]zhuheobj="+zhuheobj+"zhuheproductArray="+zhuheproductArray,"server.txt");
						//第三步，把图片名字保存到json中
						zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
						zhuheproductArray.put(zhuheobj);
						if(ToolClass.isEmptynull(ATT_ID))
						{
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]无图片","server.txt");
						}
						else
						{
							if(ToolClass.isImgFile(ATT_ID))
							{
								ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片已存在","server.txt");
							}
							else 
							{
								ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片,下载图片...","server.txt");
								//第四步.准备下载	
								String url= httpStr+"/topic/getFile/"+ATT_ID + ".jpg";	//要提交的目标地址
								ImageRequest imageRequest = new ImageRequest(  
										url,  
								        new Response.Listener<Bitmap>() {  
								            @Override  
								            public void onResponse(Bitmap response) {  
								            	ToolClass.saveBitmaptofile(response,ATT_ID);
								            	try {
													ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片,下载图片完成","server.txt");
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
								            }  
								        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
								            @Override  
								            public void onErrorResponse(VolleyError error) {  
												result = "请求失败！";
												try {
													ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片,下载图片失败","server.txt");
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
								            }  
								        });
								mQueue.add(imageRequest); 
							}
							
						}
					}
					catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
							try {
								zhuheobj.put("AttImg", "");
								zhuheproductArray.put(zhuheobj);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
						}
					
					
					//第五步：进行下一个商品信息
					productint++;
					if(productint<productarr.length())
					{
						try {
							updateproduct(productint);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try {
							zhuheproductjson.put("ProductList", zhuheproductArray);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheproductjson.toString(),"server.txt");
	
						//上传给server
						//向主线程返回信息
						Message tomain4=mainhand.obtainMessage();
						tomain4.what=SETRODUCTMAIN;
						tomain4.obj=zhuheproductjson.toString();
						mainhand.sendMessage(tomain4); // 发送消息
					}
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					result = "请求失败！";
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10]"+result,"server.txt");
					//第三步，把图片名字保存到json中
					try {
						zhuheobj.put("AttImg", "");
						zhuheproductArray.put(zhuheobj);					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
					//第五步：进行下一个商品信息
					productint++;
					if(productint<productarr.length())
					{
						try {
							updateproduct(productint);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try {
							zhuheproductjson.put("ProductList", zhuheproductArray);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheproductjson.toString(),"server.txt");
	
						//上传给server
						//向主线程返回信息
						Message tomain4=mainhand.obtainMessage();
						tomain4.what=SETRODUCTMAIN;
						tomain4.obj=zhuheproductjson.toString();
						mainhand.sendMessage(tomain4); // 发送消息
					}
				}  
			}) 
			{  
				@Override  
				protected Map<String, String> getParams() throws AuthFailureError {  
					//3.添加params
					Map<String, String> map = new HashMap<String, String>();  
					map.put("Token", Tok);  
					map.put("param", param6);
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
					return map;  
			   }  
			}; 	
			//5.加载信息并发送到网络上
			mQueue.add(stringRequest6);	
		}
		else if(ToolClass.getServerVer()==1)//一期后台
		{
			//第一步，获取图片名字ATTID
			final String CLS_URL=object2.getString("product_Images");
			String ATT_ID="";
			if(CLS_URL.equals("")!=true)
			{
				String a[] = CLS_URL.split("/");  
				ATT_ID=a[a.length-1];
				ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","图片ATT_ID="+ATT_ID,"server.txt");										
				zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
			}
			else
			{
				zhuheobj.put("AttImg", "");
			}
			
			try
			{	
				if(ATT_ID.equals("")==true)
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]无图片","server.txt");
				}
				else
				{
					if(ToolClass.isImgFile(ATT_ID))
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片已存在","server.txt");
					}
					else 
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片,下载图片...","server.txt");
						//第二步.准备下载	
						String serip=httpStr.substring(0,httpStr.lastIndexOf("shj"));
						String url= serip+CLS_URL;	//要提交的目标地址
						final String ATTIDS=ATT_ID;
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
						ImageRequest imageRequest = new ImageRequest(  
								url,  
								new Response.Listener<Bitmap>() {  
									@Override  
									public void onResponse(Bitmap response) {  
										ToolClass.saveBitmaptofile(response,ATTIDS);
										try {
											ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片,下载图片完成","server.txt");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}  
								}, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
									@Override  
									public void onErrorResponse(VolleyError error) {  
										result = "请求失败！";
										try {
											ToolClass.Log(ToolClass.INFO,"EV_SERVER","商品["+object2.getString("product_Name")+"]图片,下载图片失败","server.txt");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}  
								});
						mQueue.add(imageRequest); 
					}
					
				}
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
			}
			
			//第三步，把图片名字保存到json中		
			zhuheproductArray.put(zhuheobj);
			
			
			//第五步：进行下一个商品信息
			productint++;
			if(productint<productarr.length())
			{
				try {
					updateproduct(productint);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				try {
					zhuheproductjson.put("ProductList", zhuheproductArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheproductjson.toString(),"server.txt");

				//上传给server
				//向主线程返回信息
				Message tomain4=mainhand.obtainMessage();
				tomain4.what=SETRODUCTMAIN;
				tomain4.obj=zhuheproductjson.toString();
				mainhand.sendMessage(tomain4); // 发送消息
			}
		}
		return "";
	}
	
	
	//==========
	//==交易记录模块
	//==========
	JSONArray recordarr=null;
	JSONArray retjson=null;	
	int retint=0;
	//分解交易记录信息
	private void recordArray(String classrst) throws JSONException
	{
		retint=0;
		recordarr=new JSONArray(classrst);
		retjson=new JSONArray();
		if(recordarr.length()==0)
		{
			//向主线程返回信息
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETRECORDMAIN;
			tomain.obj=retjson;
			mainhand.sendMessage(tomain); // 发送消息	
		}
	}
	//更新交易记录信息
	private void updaterecord(int i) 
	{
		final String ret[]={null};
		JSONObject jsonObject = null; 
		String productNo="";
		String shouldPay="";
		String orderNo="";
		final String orderNoVal[]={null};
		int payStatus=0;
		String customerPrice="";
		int payType=0;
		int actualQuantity=0;
		int quantity=0;
		String orderTime="";
		int orderStatus=0;
		String productName="";
		String RefundAmount="";
		//一期后台
		String NOTE_AMOUNT="",COIN_AMOUNT="",CASH_AMOUNT="",REFUND_NOTE_AMOUNT="",
				REFUND_COIN_AMOUNT="",REFUND_CASH_AMOUNT="",AMOUNT_OWED="",Amount="",
				Cab="",PATH_NO="";
		int Status=0;
		try {
			jsonObject =recordarr.getJSONObject(i);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+jsonObject.toString()
			,"server.txt");
			productNo=jsonObject.getString("productNo");
			shouldPay=jsonObject.getString("shouldPay");
			orderNo=jsonObject.getString("orderNo");
			orderNoVal[0]=orderNo;
			payStatus=jsonObject.getInt("payStatus");
			customerPrice=jsonObject.getString("customerPrice");
			payType=jsonObject.getInt("payType");
			actualQuantity=jsonObject.getInt("actualQuantity");
			quantity=jsonObject.getInt("quantity");
			orderTime=jsonObject.getString("orderTime");
			orderStatus=jsonObject.getInt("orderStatus");
			productName=jsonObject.getString("productName");
			RefundAmount=jsonObject.getString("RefundAmount");
			Status=jsonObject.getInt("Status");
			if(ToolClass.getServerVer()==0)//旧的后台
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=orderNo="+orderNo+"orderTime="+orderTime+"orderStatus="+orderStatus+"payStatus="
					+payStatus+"payType="+payType+"shouldPay="+shouldPay+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productNo+"quantity="+quantity+
					"actualQuantity="+actualQuantity+"customerPrice="+customerPrice+"productName="+productName,"server.txt");			
			}
			else if(ToolClass.getServerVer()==1)//一期后台
			{
				NOTE_AMOUNT=jsonObject.getString("NOTE_AMOUNT");
				COIN_AMOUNT=jsonObject.getString("COIN_AMOUNT");
				CASH_AMOUNT=jsonObject.getString("CASH_AMOUNT");
				REFUND_NOTE_AMOUNT=jsonObject.getString("REFUND_NOTE_AMOUNT");
				REFUND_COIN_AMOUNT=jsonObject.getString("REFUND_COIN_AMOUNT");
				REFUND_CASH_AMOUNT=jsonObject.getString("REFUND_CASH_AMOUNT");
				AMOUNT_OWED=jsonObject.getString("AMOUNT_OWED");
				Amount=jsonObject.getString("Amount");
				Cab=jsonObject.getString("Cab");
				PATH_NO=jsonObject.getString("PATH_NO");
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=orderNo="+orderNo+"orderTime="+orderTime+"orderStatus="+orderStatus+"payStatus="
						+payStatus+"payType="+payType+"shouldPay="+shouldPay+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productNo+"quantity="+quantity+
						"actualQuantity="+actualQuantity+"customerPrice="+customerPrice+"productName="+productName+"NOTE_AMOUNT="+NOTE_AMOUNT+"COIN_AMOUNT="+COIN_AMOUNT
						+"CASH_AMOUNT="+CASH_AMOUNT+"REFUND_NOTE_AMOUNT="+REFUND_NOTE_AMOUNT+"REFUND_COIN_AMOUNT="+REFUND_COIN_AMOUNT+"REFUND_CASH_AMOUNT="+REFUND_CASH_AMOUNT
						+"AMOUNT_OWED="+AMOUNT_OWED+"Amount="+Amount+"Cab="+Cab+"PATH_NO="+PATH_NO,"server.txt");			
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//组装json包
		//交易记录信息
		String target3 = httpStr+"/api/vmcTransactionRecords";	//要提交的目标地址
				
		JSONObject param=null;
		try {
			JSONObject order=new JSONObject();
			order.put("orderID", 121);
			order.put("orderNo", orderNo);//订单id
			order.put("orderType", 1);
			order.put("orderTime", orderTime);//支付时间
			order.put("productCount", quantity);//交易数量
			order.put("customerName", "test");
			order.put("proCode", "test");
			order.put("cityCode", "test");
			order.put("areaCode", "test");
			order.put("address", "test");
			order.put("addressDetail", "test");
			order.put("consTel", "test");
			order.put("consName", "test");
			order.put("freight", 3);
			order.put("insurance", 2);
			order.put("consPost", "test");
			order.put("shipType", 3);
			order.put("orderStatus", orderStatus);//1未支付,2出货成功,3出货未完成
			order.put("shipTime", orderTime);//支付时间
			order.put("isNoFreight", 3);
			order.put("lastUpdateTime", orderTime);//支付时间
			order.put("orderDesc", "test");
			order.put("shouldPay", 2);
			order.put("integre", 3);
			order.put("sendStatus", 2);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","order="+order.toString(),"server.txt");
			
			JSONObject orderpay=new JSONObject();
			orderpay.put("payID", 120);
			orderpay.put("orderID", 121);
			orderpay.put("payStatus", payStatus);//0未付款,1正在付款,2付款完成,3付款失败，4付款取消，5支付过程事件--仅银联支付)
			orderpay.put("payType", payType);//0现金,1支付宝声波,2银联,3支付宝二维码,4微信
			orderpay.put("shouldPay", shouldPay);//交易金额,如2.5元
			orderpay.put("realPay", 2);
			if(ToolClass.getServerVer()==0)//旧的后台
			{
				orderpay.put("RefundAmount", RefundAmount);//退款金额,如1.5元
			}
			else if(ToolClass.getServerVer()==1)//一期后台
			{
				orderpay.put("RefundAmount", 0);//退款金额,如1.5元
			}
			orderpay.put("smallChange", 0);
			orderpay.put("realNote", 1);
			orderpay.put("realCoins", 0);
			orderpay.put("smallNote", 1);
			orderpay.put("smallConis", 0);
			orderpay.put("integre", 0);
			orderpay.put("payDesc", "test");
			orderpay.put("payTime", orderTime);	//支付时间
			if(ToolClass.getServerVer()==1)//一期后台
			{
				orderpay.put("NOTE_AMOUNT", NOTE_AMOUNT);
				orderpay.put("COIN_AMOUNT", COIN_AMOUNT);
				orderpay.put("CASH_AMOUNT", CASH_AMOUNT);
				orderpay.put("REFUND_NOTE_AMOUNT", REFUND_NOTE_AMOUNT);
				orderpay.put("REFUND_COIN_AMOUNT", REFUND_COIN_AMOUNT);
				orderpay.put("REFUND_CASH_AMOUNT", REFUND_CASH_AMOUNT);
				orderpay.put("AMOUNT_OWED", AMOUNT_OWED);
				orderpay.put("Status", Status);//0：未退款；1：正在退款；2：退款成功；3：退款失败'
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderpay="+orderpay.toString(),"server.txt");
			
			JSONObject orderrefund=new JSONObject();
			orderrefund.put("RefundId", 122);
			orderrefund.put("orderNo", orderNo);//商品id
			orderrefund.put("Reason", "test");
			if(ToolClass.getServerVer()==0)//旧的后台
			{
				orderrefund.put("Amount", 0);
				orderrefund.put("Debt", 0);
			}
			else if(ToolClass.getServerVer()==1)//一期后台			
			{
				orderrefund.put("Amount", Amount);
				orderrefund.put("Debt", AMOUNT_OWED);
			}
			orderrefund.put("Refund", 0);			
			orderrefund.put("ResultCode", "test");
			orderrefund.put("TradeNo", "test");
			orderrefund.put("Description", "test");
			orderrefund.put("Status", Status);//0：未退款；1：正在退款；2：退款成功；3：退款失败'
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderrefund="+orderrefund.toString(),"server.txt");
			
			JSONObject orderproduct=new JSONObject();
			orderproduct.put("opID", 123);
			orderproduct.put("orderID", 121);
			orderproduct.put("productID", 120);
			orderproduct.put("productNo", productNo);//商品编号
			orderproduct.put("productType", "0001");
			orderproduct.put("quantity", quantity);//预计出货
			orderproduct.put("actualQuantity", actualQuantity);//实际出货
			orderproduct.put("productPrice", 1);			
			orderproduct.put("customerPrice", customerPrice);//商品单价
			orderproduct.put("productName", productName);//商品名称
			orderproduct.put("moneyAmount", 1);
			orderproduct.put("productIntegre", 1);
			orderproduct.put("IntegreAmount", 1);
			orderproduct.put("firstpurchaseprice", 1);
			if(ToolClass.getServerVer()==1)//一期后台
			{
				orderproduct.put("Cab", Cab);
				orderproduct.put("PATH_NO", PATH_NO);
			}
			JSONArray orderpro=new JSONArray();
			orderpro.put(orderproduct);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderproduct="+orderpro.toString(),"server.txt");
			
			
			
			JSONObject trans=new JSONObject();
			trans.put("Order", order);
			trans.put("OrderPay", orderpay);
			trans.put("OrderProducts", orderpro);
			trans.put("OrderRefund", orderrefund);			
			JSONArray TRANSACTION=new JSONArray();
			TRANSACTION.put(trans);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","TRANSACTION="+TRANSACTION.toString(),"server.txt");
			param=new JSONObject();
			param.put("VMC_NO", vmc_no);
			param.put("TOTAL",1);
			param.put("ACTUAL_TOTAL",1);
			param.put("TRANSACTION", TRANSACTION);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","param="+param.toString(),"server.txt");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final JSONObject paramVal[]={null};
		paramVal[0]=param;
		
		//向主线程返回信息
		final Message tomain3=mainhand.obtainMessage();
		//新建Volley 
		mQueue = getRequestQueue();
		//4.准备加载信息设置
		StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
			@Override  
			public void onResponse(String response) {  
			   
			  //如果请求成功
				result = response;	//获取返回的字符串
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
				JSONObject object;
				try {
					object = new JSONObject(result);
					int errType =  object.getInt("Error");
					//返回有故障
					if(errType>0)
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail7]Records","server.txt");
					}
					else
					{
						ret[0]=orderNoVal[0];						
						JSONObject retj=new JSONObject();
						retj.put("orderno", ret[0]);
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok7]["+retj.toString()+"]","server.txt");
						retjson.put(retj);
					}			
					
				} catch (JSONException e) {
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail7-1]","server.txt");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//第五步：进行下一个记录信息
				retint++;
				if(retint<recordarr.length())
				{
					updaterecord(retint);
				}
				else
				{					
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2="+retjson.toString(),"server.txt");
					//上传给server
					//向主线程返回信息
					tomain3.what=SETRECORDMAIN;
					tomain3.obj=retjson;
					mainhand.sendMessage(tomain3); // 发送消息
				}
			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				result = "请求失败！";
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail7]"+result,"server.txt");
				//第五步：进行下一个记录信息
				retint++;
				if(retint<recordarr.length())
				{
					updaterecord(retint);
				}
				else
				{						
					//上传给server
					//向主线程返回信息
					tomain3.what=SETRECORDMAIN;
					tomain3.obj=retjson;
					mainhand.sendMessage(tomain3); // 发送消息
				}
			}  
		}) 
		{  
			@Override  
			protected Map<String, String> getParams() throws AuthFailureError {  
				//3.添加params
				Map<String, String> map = new HashMap<String, String>();  
				map.put("Token", Tok);  
				map.put("param", paramVal[0].toString());
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","Records="+map.toString(),"server.txt");
				return map;  
		   }  
		}; 	
		//5.加载信息并发送到网络上
		mQueue.add(stringRequest3);				
	}
	
	//==========
	//==货道管理模块
	//==========
	JSONArray columnarr=null;
	JSONArray columnjson=null;	
	int columnint=0;
	//分解交易记录信息
	private void columnArray(String classrst) throws JSONException
	{
		columnint=0;
		columnarr=new JSONArray(classrst);
		columnjson=new JSONArray();
		if(columnarr.length()==0)
		{
			//向主线程返回信息
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETHUODAOSTATUMAIN;
			tomain.obj=columnjson;
			mainhand.sendMessage(tomain); // 发送消息	
		}
	}
	//更新货道状态信息
	private void updatecolumn(int i) 
	{
		JSONObject jsonObject = null; 
		String pathID="";
		String cabinetNumber="";
		String pathName="";
		final String cabinetNumberVal[]={null};
		final String pathNameVal[]={null};
		String productID="";
		String productNum="";
		String pathCount="";
		String pathStatus="";
		String pathRemaining="";
		String lastedittime="";
		String isdisable="";
		try {
			jsonObject =columnarr.getJSONObject(i);
			pathID=jsonObject.getString("pathID");
			cabinetNumber=jsonObject.getString("cabinetNumber");
			cabinetNumberVal[0]=cabinetNumber;
			pathName=jsonObject.getString("pathName");
			pathNameVal[0]=pathName;
			productID=jsonObject.getString("productID");
			productNum=jsonObject.getString("productNum");
			pathCount=jsonObject.getString("pathCount");
			pathStatus=jsonObject.getString("pathStatus");
			pathRemaining=jsonObject.getString("pathRemaining");
			lastedittime=jsonObject.getString("lastedittime");
			isdisable=jsonObject.getString("isdisable");
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=pathID="+pathID+"cabinetNumber="+cabinetNumber+"pathName="+pathName+"productID="
				+productID+"productNum="+productNum+"pathCount="+pathCount+"pathStatus="+pathStatus+"pathRemaining="+pathRemaining+"lastedittime="+lastedittime+
				"isdisable="+isdisable,"server.txt");			
			  	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		//组装json包
		//交易记录信息
		String target3 = httpStr+"/api/vmcPathStatus";	//要提交的目标地址
				
		JSONObject param=null;
		try {
			JSONObject trans=new JSONObject();
			trans.put("pathID", pathID);
			trans.put("cabinetNumber", cabinetNumber);
			trans.put("pathName", pathName);
			trans.put("productID", productID);	
			trans.put("productNum", productNum);	
			trans.put("pathCount", pathCount);	
			trans.put("pathStatus", pathStatus);	
			trans.put("pathRemaining", pathRemaining);
			trans.put("lastedittime", lastedittime);	
			trans.put("isdisable", isdisable);
			JSONArray PATHLIST=new JSONArray();
			PATHLIST.put(trans);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","PATHLIST="+PATHLIST.toString(),"server.txt");
			param=new JSONObject();
			param.put("VMC_NO", vmc_no);
			param.put("PATHLIST", PATHLIST);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","param="+param.toString(),"server.txt");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final JSONObject paramVal[]={null};
		paramVal[0]=param;

			
		//向主线程返回信息
		final Message tomain3=mainhand.obtainMessage();
		//新建Volley 
		mQueue = getRequestQueue();
		//4.准备加载信息设置
		StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
			@Override  
			public void onResponse(String response) {  
			   
			    //如果请求成功
				result = response;	//获取返回的字符串
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
				JSONObject object;
				try {
					object = new JSONObject(result);
					int errType =  object.getInt("Error");
					//返回有故障
					if(errType>0)
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]Records","server.txt");
					}
					else
					{
						JSONObject retj=new JSONObject();
						retj.put("cabinetNumber", cabinetNumberVal[0]);
						retj.put("pathName", pathNameVal[0]);
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok8]["+retj.toString()+"]","server.txt");
						columnjson.put(retj);
					}				
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8-1]","server.txt");
					e.printStackTrace();
				}	
				
				//第五步：进行下一个货道信息
				columnint++;
				if(columnint<columnarr.length())
				{
					updatecolumn(columnint);
				}
				else
				{						
					//上传给server
					//向主线程返回信息
					tomain3.what=SETHUODAOSTATUMAIN;
					tomain3.obj=columnjson;
					mainhand.sendMessage(tomain3); // 发送消息
				}
			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				result = "请求失败！";
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]"+result,"server.txt");
				//第五步：进行下一个货道信息
				columnint++;
				if(columnint<columnarr.length())
				{
					updatecolumn(columnint);
				}
				else
				{						
					//上传给server
					//向主线程返回信息
					tomain3.what=SETHUODAOSTATUMAIN;
					tomain3.obj=columnjson;
					mainhand.sendMessage(tomain3); // 发送消息
				}
			}  
		}) 
		{  
			@Override  
			protected Map<String, String> getParams() throws AuthFailureError {  
				//3.添加params
				Map<String, String> map = new HashMap<String, String>();  
				map.put("Token", Tok);  
				map.put("param", paramVal[0].toString());
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","columns="+map.toString(),"server.txt");
				return map;  
		   }  
		}; 	
		//5.加载信息并发送到网络上
		mQueue.add(stringRequest3);				
	}
	
	
	//==========
	//==版本更新模块
	//==========
	JSONArray versionarr=null;
	JSONArray zhuheversionArray=null;
	JSONObject zhuheversionjson = null; 
	int versionint=0;
	//分解商品信息
	private void versionArray(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst);
		if(ToolClass.getServerVer()==1)//一期后台
		{
			versionarr=jsonObject.getJSONArray("List");
			versionint=0;
			zhuheversionArray=new JSONArray();
			zhuheversionjson = new JSONObject(); 
			if(versionarr.length()==0)
			{
				ToolClass.deleteAPKFile();
				//向主线程返回信息
				Message tomain=mainhand.obtainMessage();
				tomain.what=SETVERSIONMAIN;
				tomain.obj=zhuheversionjson.toString();
				mainhand.sendMessage(tomain); // 发送消息	
			}			
		}
	}
	//更新程序信息
	private String updateversion(int i) throws JSONException
	{
		final JSONObject object2=versionarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新版本="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=object2;
		//第一步.获取商品图片名字
		final String FILE_URL=object2.getString("FILE_URL");
		String ATT_ID="";
		if(FILE_URL.equals("null")!=true)
		{
			String a[] = FILE_URL.split("/");  
			ATT_ID=a[a.length-1];
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","下载ATT_ID="+ATT_ID,"server.txt");										
			zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
		}
		else
		{
			zhuheobj.put("AttImg", "");
		}
		
		try
		{	
			if(ATT_ID.equals("")==true)
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","程序["+ATT_ID+"]不存在","server.txt");
			}
			else
			{
				if(ToolClass.isAPKFile(ATT_ID))
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","程序["+ATT_ID+"]已存在","server.txt");
				}
				else 
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","程序["+ATT_ID+"]开始下载...","server.txt");
					//第二步.准备下载	
					String serip=httpStr.substring(0,httpStr.lastIndexOf('/'));
					String url= serip+FILE_URL;	//要提交的目标地址
					final String ATTIDS=ATT_ID;
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
					downloadApk(ATTIDS,url);//下载程序
										
				}
				
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]"+e,"server.txt");
		}
		
		//第三步，把图片名字保存到json中		
		zhuheversionArray.put(zhuheobj);
		
		
		//第四步：进行下一个分类信息
		versionint++;
		if(versionint<versionarr.length())
		{
			try {
				updateversion(versionint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				zhuheversionjson.put("List", zhuheversionArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheversionjson.toString(),"server.txt");

			//上传给server
			//向主线程返回信息
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETVERSIONMAIN;
			tomain4.obj=zhuheversionjson.toString();
			mainhand.sendMessage(tomain4); // 发送消息
		}		
		return "";
		
	}
	
	private String url=null;
	private String ATTIDS=null;
	/**
     * 下载apk文件
     */
    private void downloadApk(String ATT,String str)
    {    	
    	ATTIDS=ATT;
    	url=str;
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
            	//1.下载
            	HttpClient client = new DefaultHttpClient();  
                HttpGet get = new HttpGet(url);  
                HttpResponse response = client.execute(get);  
                HttpEntity entity = response.getEntity();  
                long length = entity.getContentLength();  
                InputStream is = entity.getContent();  
                FileOutputStream fileOutputStream = null;  
                if (is != null)
                {  
                    File file = ToolClass.setAPKFile(ATTIDS);  
                    fileOutputStream = new FileOutputStream(file);  
                    byte[] buf = new byte[1024];  
                    int ch = -1;  
                    int count = 0;  
                    while ((ch = is.read(buf)) != -1) {  
                        fileOutputStream.write(buf, 0, ch);  
                        count += ch;  
                        if (length > 0) {  
                        }  
                    }  
                }  
                fileOutputStream.flush();  
                if (fileOutputStream != null) {  
                    fileOutputStream.close();  
                } 
                //2.上传更新版本状态
                String target3 = httpStr+"/api/updateClientVersion";	//要提交的目标地址
                //新建Volley 
				mQueue = getRequestQueue();
                StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
        			@Override  
        			public void onResponse(String response) {              			   
        			    //如果请求成功
        				result = response;	//获取返回的字符串
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
        				
        			}  
        		}, new Response.ErrorListener() {  
        			@Override  
        			public void onErrorResponse(VolleyError error) {  
        				result = "请求失败！";
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]"+result,"server.txt");            				
        			}  
        		}) 
        		{  
        			@Override  
        			protected Map<String, String> getParams() throws AuthFailureError {  
        				//3.添加params
        				Map<String, String> map = new HashMap<String, String>();  
        				map.put("Token", Tok);  
        				map.put("VMC_NO", vmc_no);
        				map.put("OPEN_DOOR_ID", "");
        				map.put("EXEC_RESULT", "9");
        				map.put("EXEC_TIME", ToolClass.getLasttime());
        				map.put("VERSION_STATUS", "2");
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","下载完成version="+map.toString(),"server.txt");
        				return map;  
        		   }  
        		}; 	
        		//3.加载信息并发送到网络上
        		mQueue.add(stringRequest3);
               
        		
        		//4.上传给server
    			//向主线程返回信息
    			Message tomain4=mainhand.obtainMessage();
    			tomain4.what=SETINSTALLMAIN;
    			tomain4.obj=ATTIDS;
    			mainhand.sendMessage(tomain4); // 发送消息                
            } catch (Exception e)
            {
                e.printStackTrace();
                //2.上传更新版本状态
                String target3 = httpStr+"/api/updateClientVersion";	//要提交的目标地址
                //新建Volley 
				mQueue = getRequestQueue();
                StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
        			@Override  
        			public void onResponse(String response) {              			   
        			    //如果请求成功
        				result = response;	//获取返回的字符串
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
        				
        			}  
        		}, new Response.ErrorListener() {  
        			@Override  
        			public void onErrorResponse(VolleyError error) {  
        				result = "请求失败！";
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]"+result,"server.txt");            				
        			}  
        		}) 
        		{  
        			@Override  
        			protected Map<String, String> getParams() throws AuthFailureError {  
        				//3.添加params
        				Map<String, String> map = new HashMap<String, String>();  
        				map.put("Token", Tok);  
        				map.put("VMC_NO", vmc_no);
        				map.put("OPEN_DOOR_ID", "");
        				map.put("EXEC_RESULT", "2");
        				map.put("EXEC_TIME", ToolClass.getLasttime());
        				map.put("VERSION_STATUS", "1");
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","下载失败version="+map.toString(),"server.txt");
        				return map;  
        		   }  
        		}; 	
        		//3.加载信息并发送到网络上
        		mQueue.add(stringRequest3);
            }
        }
    };
	
    
    //==========
  	//==日志更新模块
  	//==========
  	JSONArray logarr=null;
  	JSONArray zhuhelogArray=null;
  	JSONObject zhuhelogjson = null; 
  	int logint=0;
  	//分解商品信息
  	private void logArray(String classrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(classrst);
  		if(ToolClass.getServerVer()==1)//一期后台
  		{
  			logarr=jsonObject.getJSONArray("List");
  			logint=0;
  			zhuhelogArray=new JSONArray();
  			zhuhelogjson = new JSONObject(); 
  			if(logarr.length()==0)
  			{
  				//向主线程返回信息
  				Message tomain=mainhand.obtainMessage();
  				tomain.what=SETLOGMAIN;
  				tomain.obj=zhuhelogjson.toString();
  				mainhand.sendMessage(tomain); // 发送消息	
  			}			
  		}
  	}
  	//更新日志信息
  	private String updatelog(int i) throws JSONException
  	{
  		final JSONObject object2=logarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新日志="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//第一步.获取日志ID
  		final int LOG_ID=object2.getInt("CLIENT_LOG_ID");
  		String START_LOG_TIME=object2.getString("START_LOG_TIME");
  		String END_LOG_TIME=object2.getString("END_LOG_TIME");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","需要上传LOG_ID="+LOG_ID+"START_LOG_TIME="+START_LOG_TIME+"END_LOG_TIME"+END_LOG_TIME,"server.txt");										
		zhuheobj.put("AttImg", LOG_ID);
		//第二步.压缩日志包
		final String f=ToolClass.logFileInterval(START_LOG_TIME, END_LOG_TIME);
		
		if(f==null)
		{
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","日志["+LOG_ID+"]无日志需要上传","server.txt");
		}
		else
		{
	  		try
	  		{	
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","日志["+LOG_ID+"]开始上传...","server.txt");
				//第二步.准备上传	
	  			uploadLog(String.valueOf(LOG_ID),f);
	  		}
	  		catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]"+e,"server.txt");
	  		}
		}
  		
  		//第三步，把图片名字保存到json中		
  		zhuhelogArray.put(zhuheobj);
  		
  		
  		//第四步：进行下一个分类信息
  		logint++;
  		if(logint<logarr.length())
  		{
  			try {
  				updatelog(logint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuhelogjson.put("List", zhuhelogArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuhelogjson.toString(),"server.txt");

  			//上传给server
  			//向主线程返回信息
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETLOGMAIN;
  			tomain4.obj=zhuhelogjson.toString();
  			mainhand.sendMessage(tomain4); // 发送消息
  		}		
  		return "";
  		
  	}
  	
  	private String LOG_IDS=null;
  	private File file=null;
  	/**
     * 上传日志文件
     */
    private void uploadLog(String LOG_ID,String f)
    {    	
    	LOG_IDS=LOG_ID;
    	file=new File(f);
        // 启动新线程下载软件
        new uploadLogThread().start();
    }
  	
    /**
     * 上传文件线程
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class uploadLogThread extends Thread
    {
        @Override
        public void run()
        {
//        	String target = httpStr+"/api/uploadClientLog";	//要提交的目标地址
//			HttpClient httpclient = new DefaultHttpClient();	//创建HttpClient对象
//			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);//请求超时
//			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);//读取超时
//			HttpPost httppost = new HttpPost(target);	//创建HttpPost对象
//			//1.添加params
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("LAST_EDIT_TIME", ToolClass.getLasttime()));
//			params.add(new BasicNameValuePair("Token", Tok));			
//			params.add(new BasicNameValuePair("CLIENT_LOG_ID", LOG_IDS));
//			String bal=null;
//			try {
//				//输入文件流
//				FileInputStream inputFile = new FileInputStream(file);
//				//抓为byte字节
//				byte[] buffer = new byte[(int)file.length()];
//				inputFile.read(buffer);
//				inputFile.close();
//				//压缩为Base64格式
//				bal= Base64.encodeToString(buffer,Base64.DEFAULT);
//			} catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			params.add(new BasicNameValuePair("FILE_CONTENT", bal));
//			//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+params.toString(),"server.txt");
//			try {
//				httppost.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //设置编码方式
//				HttpResponse httpResponse = httpclient.execute(httppost);	//执行HttpClient请求
//				//向主线程返回信息
//				Message tomain=mainhand.obtainMessage();
//				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
//					//如果请求成功
//					result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=日志上传完成"+result,"server.txt");					
//				}else{
//					result = "请求失败！";
//					tomain.what=SETFAILMAIN;
//					mainhand.sendMessage(tomain); // 发送消息
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail1]日志上传"+result,"server.txt");
//				}
//				
//				
//			} 
//	       catch (Exception e) 
//	       {  
//	           //e.printStackTrace();  
//	    	   //向主线程返回网络失败信息
//				Message tomain=mainhand.obtainMessage();
//	    	    tomain.what=SETFAILMAIN;
//	    	    mainhand.sendMessage(tomain); // 发送消息
//	    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail1]SETFAILMAIN","server.txt");
//	       }
        	
        	//第二步：上传给server
  			String target17 = httpStr+"/api/uploadClientLog";	//要提交的目标地址
			final String LAST_EDIT_TIME17=ToolClass.getLasttime();
        	//向主线程返回信息
			final Message tomain17=mainhand.obtainMessage();
			tomain17.what=SETNONE;
			//新建Volley 
			mQueue = getRequestQueue();
			//4.准备加载信息设置
			StringRequest stringRequest17 = new StringRequest(Method.POST, target17,  new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {  
				   
				    //如果请求成功
					result = response;	//获取返回的字符串
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=日志上传完成"+result,"server.txt");
															 
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					result = "请求失败！";
					tomain17.what=SETFAILMAIN;
		    	    mainhand.sendMessage(tomain17); // 发送消息
		    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETFAILMAIN"+result,"server.txt");
				}  
			}) 
			{  
				@Override  
				protected Map<String, String> getParams() throws AuthFailureError {  
					String bal=null;
					try {
						//输入文件流
						FileInputStream inputFile = new FileInputStream(file);
						//抓为byte字节
						byte[] buffer = new byte[(int)file.length()];
						inputFile.read(buffer);
						inputFile.close();
						//压缩为Base64格式
						bal= Base64.encodeToString(buffer,Base64.DEFAULT);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//3.添加params
					Map<String, String> map = new HashMap<String, String>();  
					map.put("Token", Tok);  
					map.put("LAST_EDIT_TIME", LAST_EDIT_TIME17);	
					map.put("CLIENT_LOG_ID", LOG_IDS);
					map.put("FILE_CONTENT", bal);
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1=日志上传...","server.txt");
					return map;  
			   }  
			}; 	
			//5.加载信息并发送到网络上
			mQueue.add(stringRequest17);
        }
    };
    
    //==============
  	//==支付宝微信更新模块
  	//==============
  	JSONArray Accountarr=null;
  	JSONArray zhuheAccountArray=null;
  	JSONObject zhuheAccountjson = null; 
  	int Accountint=0;
  	//分解商品信息
  	private void AccountArray(String classrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(classrst);
  		if(ToolClass.getServerVer()==1)//一期后台
  		{
  			Accountarr=jsonObject.getJSONArray("List");
  			Accountint=0;
  			zhuheAccountArray=new JSONArray();
  			zhuheAccountjson = new JSONObject(); 
  			if(Accountarr.length()==0)
  			{
  				//向主线程返回信息
  				Message tomain=mainhand.obtainMessage();
  				tomain.what=SETACCOUNTMAIN;
  				tomain.obj=zhuheAccountjson.toString();
  				mainhand.sendMessage(tomain); // 发送消息	
  			}			
  		}
  	}
  	//更新支付宝微信信息
  	private String updateAccount(int i) throws JSONException
  	{
  		final JSONObject object2=Accountarr.getJSONObject(i);
  		Message tomain14=mainhand.obtainMessage();
  		tomain14.what=SETACCOUNTMAIN;
		tomain14.obj="";							   	    
		mainhand.sendMessage(tomain14); // 发送消息
		
		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新支付宝微信信息="+object2.toString(),"server.txt");										
		//延时3s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            { 
			ToolClass.ResetConfigFileServer(object2);
			Message tomain=mainhand.obtainMessage();
	  		tomain.what=SETACCOUNTRESETMAIN;
			tomain.obj="";							   	    
			mainhand.sendMessage(tomain); // 发送消息
            }

		}, 1000);
  		return "";
  		
  	}
  	
    //==============
  	//==广告模块
  	//==============
  	JSONArray advarr=null;
  	JSONArray zhuheadvArray=null;
  	JSONObject zhuheadvjson = null; 
  	int advint=0;
  	//分解广告信息
  	private void advArray(String advrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(advrst); 
  		advarr=jsonObject.getJSONArray("List");
  		advint=0;
  		zhuheadvArray=new JSONArray();
  		zhuheadvjson = new JSONObject(); 
  		if(advarr.length()==0)
  		{
  			//向主线程返回信息
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETADVMAIN;
  			tomain.obj=zhuheadvjson.toString();
  			mainhand.sendMessage(tomain); // 发送消息	
  		}
  	}
  	
  	//更新广告信息
  	private String updateadv(int i) throws JSONException
  	{
  		final JSONObject object2=advarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新广告="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//第一步，获取图片名字ATTID
  		final String CLS_URL=object2.getString("ADV_URL");
  		String ATT_ID="",TypeStr="";
  		int FileType=0;//1图片,2视频
  		if(CLS_URL.equals("null")!=true)
  		{
  			String a[] = CLS_URL.split("/");  
  			ATT_ID=a[a.length-1];  
  			String tmp = ATT_ID;
	    	ATT_ID=tmp.substring(0,tmp.lastIndexOf("."));		    	
	    	TypeStr=tmp.substring(tmp.lastIndexOf(".")+1);
		    //是否视频文件
		    if(MediaFileAdapter.isVideoFileType(tmp)==true)
		    {
		    	FileType=2;
		        ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告视频ATT_ID="+ATT_ID+"."+TypeStr,"server.txt");										
		    }
		    //是否图片文件
		    else if(MediaFileAdapter.isImgFileType(tmp)==true)
		    {
		    	FileType=1;
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告图片ATT_ID="+ATT_ID+"."+TypeStr,"server.txt");										
	  		}
  			
  			zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
  		}
  		else
  		{
  			zhuheobj.put("AttImg", "");
  		}
  		
  		
  		try
  		{	
  			if(ATT_ID.equals("")==true)
  			{
  				ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告["+object2.getString("ADV_TITLE")+"]无","server.txt");
  			}
  			else
  			{
  				int IS_DELETE=object2.getInt("IS_DELETE");
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告["+object2.getString("ADV_TITLE")+"],IS_DELETE="+IS_DELETE,"server.txt");
					
  				if(ToolClass.isAdsFile(ATT_ID,TypeStr))
  				{
  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告["+object2.getString("ADV_TITLE")+"]已存在","server.txt");
  					if(IS_DELETE==1)
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告["+object2.getString("ADV_TITLE")+"]删除","server.txt");
  						ToolClass.delAds(ATT_ID,TypeStr);
  					}
  				}
  				else 
  				{
  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告["+object2.getString("ADV_TITLE")+"],不存在","server.txt");
  					if(IS_DELETE==0)
  					{
	  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告["+object2.getString("ADV_TITLE")+"],下载...","server.txt");
	  					//第二步.准备下载	
	  					String serip=httpStr.substring(0,httpStr.lastIndexOf('/'));
	  					String url= serip+CLS_URL;	//要提交的目标地址
	  					final String ATTIDS=ATT_ID;
	  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
	  					//下载图片
	  					if(FileType==1)
	  					{
		  					ImageRequest imageRequest = new ImageRequest(  
		  							url,  
		  					        new Response.Listener<Bitmap>() {  
		  					            @Override  
		  					            public void onResponse(Bitmap response) {  
		  					            	ToolClass.saveBitmaptoads(response,ATTIDS);
		  					            	try {
		  										ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告图片["+object2.getString("ADV_TITLE")+"],下载完成","server.txt");
		  									} catch (JSONException e) {
		  										// TODO Auto-generated catch block
		  										e.printStackTrace();
		  									}
		  					            }  
		  					        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
		  					            @Override  
		  					            public void onErrorResponse(VolleyError error) {  
		  									result = "请求失败！";
		  									try {
		  										ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告图片["+object2.getString("ADV_TITLE")+"],下载失败","server.txt");
		  									} catch (JSONException e) {
		  										// TODO Auto-generated catch block
		  										e.printStackTrace();
		  									}
		  					            }  
		  					        });
		  					mQueue.add(imageRequest); 
	  					}
	  				    //下载视频
	  					else if(FileType==2)
	  					{
	  						downloadAds(url,ATTIDS,TypeStr);//下载程序
	  					}
  					}
  				}
  				
  			}
  		}
  		catch (JSONException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//第三步，把名字保存到json中		
  		zhuheadvArray.put(zhuheobj);
  		
  		
  		//第四步：进行下一个分类信息
  		advint++;
  		if(advint<advarr.length())
  		{
  			try {
  				updateadv(advint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuheadvjson.put("List", zhuheadvArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheadvjson.toString(),"server.txt");

  			//上传给server
  			//向主线程返回信息
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETADVMAIN;
  			tomain4.obj=zhuheclassjson.toString();
  			mainhand.sendMessage(tomain4); // 发送消息
  			
  			//不管成功或失败，都重新置广告时间
  		    //延时3s
  		    new Handler().postDelayed(new Runnable() 
  			{
  	            @Override
  	            public void run() 
  	            { 
  				Message tomain=mainhand.obtainMessage();
  		  		tomain.what=SETADVRESETMAIN;
  				tomain.obj="";							   	    
  				mainhand.sendMessage(tomain); // 发送消息
  	            }

  			}, 1000);
  		}		
  		return "";
  	}
  	
  	private String adsurl=null;
  	private String ATTADS=null;
  	private String TypeStr=null;
	/**
     * 下载apk广告视频文件
     */
    private void downloadAds(String str,String ATT_ID,String Type)
    {    	
    	adsurl=str;
    	ATTADS=ATT_ID;
    	TypeStr=Type;
        // 启动新线程下载软件
        new downloadAdsThread().start();
    }
  	
    /**
     * 下载文件线程
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadAdsThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
            	//1.下载
            	HttpClient client = new DefaultHttpClient();  
                HttpGet get = new HttpGet(adsurl);  
                HttpResponse response = client.execute(get);  
                HttpEntity entity = response.getEntity();  
                long length = entity.getContentLength();  
                InputStream is = entity.getContent();  
                FileOutputStream fileOutputStream = null;  
                if (is != null)
                {  
                    File file = ToolClass.saveAvitoads(ATTADS,TypeStr);  
                    fileOutputStream = new FileOutputStream(file);  
                    byte[] buf = new byte[1024];  
                    int ch = -1;  
                    int count = 0;  
                    while ((ch = is.read(buf)) != -1) {  
                        fileOutputStream.write(buf, 0, ch);  
                        count += ch;  
                        if (length > 0) {  
                        }  
                    }  
                }  
                fileOutputStream.flush();  
                if (fileOutputStream != null) {  
                    fileOutputStream.close();  
                }   
                ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告视频["+ATTADS+"."+TypeStr+"],下载完成","server.txt");
            } catch (Exception e)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","广告视频["+ATTADS+"."+TypeStr+"],下载失败","server.txt");
                e.printStackTrace();                
            }
        }
    };
    
    //==============
  	//==设备状态模块
  	//==============
  	JSONArray clientarr=null;
  	JSONArray zhuheclientArray=null;
  	JSONObject zhuheclientjson = null; 
  	int clientint=0;
  	//分解设备信息
  	private void clientArray(String clientrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(clientrst); 
  		clientarr=jsonObject.getJSONArray("List");
  		clientint=0;
  		zhuheclientArray=new JSONArray();
  		zhuheclientjson = new JSONObject(); 
  		if(clientarr.length()==0)
  		{
  			//向主线程返回信息
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETCLIENTMAIN;
  			tomain.obj=zhuheclientjson.toString();
  			mainhand.sendMessage(tomain); // 发送消息	
  		}
  	    //本地VMC_NO和密码
  		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		devID=tb_inaccount.getDevID().toString();
    		mainPwd=tb_inaccount.getMainPwd();
    		ToolClass.Log(ToolClass.INFO,"EV_SERVER","本地VMC_NO="+devID+",MANAGER_PASSWORD="+mainPwd,"server.txt");	
    	}
  	}
  	
  	String devID="";
  	String mainPwd="";
  	//更新设备信息
  	private String updateclient(int i) throws JSONException
  	{  	
  		final JSONObject object2=clientarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新设备="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//第一步，获取VMC_NO和密码
  		final String VMC_NO=object2.getString("VMC_NO");
  		final String MANAGER_PASSWORD=object2.getString("MANAGER_PASSWORD");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","设备VMC_NO="+VMC_NO+",MANAGER_PASSWORD="+MANAGER_PASSWORD,"server.txt");	
  		zhuheobj.put("AttImg", "");
  		  		
  		try
  		{	   
  			if((ToolClass.isEmptynull(devID)==false)&&(devID.equals(VMC_NO)))
  			{  				
  				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
  			    //创建Tb_inaccount对象 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
    					0,0,MANAGER_PASSWORD,0,0,0,0,0,0,0,"",0,
    					0,0, 0,0,0,"","");
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置设备VMC_NO="+tb_vmc_system_parameter.getDevID()+",MANAGER_PASSWORD="+tb_vmc_system_parameter.getMainPwd(),"server.txt");	
    			parameterDAO.updatepwd(tb_vmc_system_parameter); 
  			}
  		}
  		catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//第三步，把名字保存到json中		
  		zhuheclientArray.put(zhuheobj);
  		
  		
  		//第四步：进行下一个分类信息
  		clientint++;
  		if(clientint<clientarr.length())
  		{
  			try {
  				updateclient(clientint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuheclientjson.put("List", zhuheclientArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheclientjson.toString(),"server.txt");

  			//上传给server
  			//向主线程返回信息
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETCLIENTMAIN;
  			tomain4.obj=zhuheclassjson.toString();
  			mainhand.sendMessage(tomain4); // 发送消息  			
  		}		
  		return "";
  	}
  	
    //==============
  	//==活动信息模块
  	//==============
  	JSONArray eventarr=null;
  	JSONArray zhuheeventArray=null;
  	JSONObject zhuheeventjson = null; 
  	int eventint=0;
  	//分解设备信息
  	private void eventArray(String eventrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(eventrst); 
  		eventarr=jsonObject.getJSONArray("List");
  		eventint=0;
  		zhuheeventArray=new JSONArray();
  		zhuheeventjson = new JSONObject(); 
  		if(eventarr.length()==0)
  		{
  			//向主线程返回信息
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETEVENTINFOMAIN;
  			tomain.obj=zhuheeventjson.toString();
  			mainhand.sendMessage(tomain); // 发送消息	
  		}  	    
  	}
  	
  	//更新活动信息
  	private String updateevent(int i) throws JSONException
  	{  	
  		final JSONObject object2=eventarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新活动信息="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//第一步，获取VMC_NO和密码
  		final String EVENT_CONTENT=object2.getString("EVENT_CONTENT");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","EVENT_CONTENT="+EVENT_CONTENT,"server.txt");	
  		zhuheobj.put("AttImg", "");
  		  
  		try
  		{	
  			{  				
  				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
  			    //创建Tb_inaccount对象 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(devID, "", 0,0, 
    					0,0,"",0,0,0,0,0,0,0,"",0,
    					0,0, 0,0,0,EVENT_CONTENT,"");
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置EVENT_CONTENT","server.txt");	
    			parameterDAO.updateevent(tb_vmc_system_parameter); 
  			}
  		}
  		catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//第三步，把名字保存到json中		
  		zhuheeventArray.put(zhuheobj);
  		
  		
  		//第四步：进行下一个分类信息
  		eventint++;
  		if(eventint<eventarr.length())
  		{
  			try {
  				updateevent(eventint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuheeventjson.put("List", zhuheeventArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheeventjson.toString(),"server.txt");

  			//上传给server
  		    //向主线程返回信息
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETEVENTINFOMAIN;
			tomain.obj=zhuheeventjson.toString();
			mainhand.sendMessage(tomain); // 发送消息 			
  		}		
  		return "";
  	}
  	
    //==============
  	//==购买演示模块
  	//==============
  	JSONArray demoarr=null;
  	JSONArray zhuhedemoArray=null;
  	JSONObject zhuhedemojson = null; 
  	int demoint=0;
  	//分解设备信息
  	private void demoArray(String demorst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(demorst); 
  		demoarr=jsonObject.getJSONArray("List");
  		demoint=0;
  		zhuhedemoArray=new JSONArray();
  		zhuhedemojson = new JSONObject(); 
  		if(demoarr.length()==0)
  		{
  			//向主线程返回信息
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETDEMOINFOMAIN;
  			tomain.obj=zhuhedemojson.toString();
  			mainhand.sendMessage(tomain); // 发送消息	
  		}  	    
  	}
  	
  	//更新购买演示
  	private String updatedemo(int i) throws JSONException
  	{  	
  		final JSONObject object2=demoarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","更新购买演示="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//第一步，获取VMC_NO和密码
  		final String DEMO_CONTENT=object2.getString("DEMO_CONTENT");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","DEMO_CONTENT="+DEMO_CONTENT,"server.txt");	
  		zhuheobj.put("AttImg", "");
  		  
  		try
  		{	
  			{  				
  				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
  			    //创建Tb_inaccount对象 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(devID, "", 0,0, 
    					0,0,"",0,0,0,0,0,0,0,"",0,
    					0,0, 0,0,0,"",DEMO_CONTENT);
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置DEMO_CONTENT","server.txt");	
    			parameterDAO.updatedemo(tb_vmc_system_parameter); 
  			}
  		}
  		catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//第三步，把名字保存到json中		
  		zhuhedemoArray.put(zhuheobj);
  		
  		
  		//第四步：进行下一个分类信息
  		demoint++;
  		if(demoint<demoarr.length())
  		{
  			try {
  				updatedemo(demoint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuhedemojson.put("List", zhuhedemoArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuhedemojson.toString(),"server.txt");

  			//上传给server
  		    //向主线程返回信息
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETDEMOINFOMAIN;
			tomain.obj=zhuhedemojson.toString();
			mainhand.sendMessage(tomain); // 发送消息 			
  		}		
  		return "";
  	}
  	
    //==============
  	//==取货码模块
    //取货码比较特殊，不能用作复制的例子
  	//==============
  	JSONArray pickuparr=null;
  	JSONArray zhuhepickupArray=null;
  	JSONObject zhuhepickupjson = null; 
  	int pickupint=0;
  	//分解设备信息
  	private void pickupArray(String pickuprst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(pickuprst); 
  		pickuparr=jsonObject.getJSONArray("List");
  		pickupint=0;
  		zhuhepickupArray=new JSONArray();
  		zhuhepickupjson = new JSONObject(); 
  		if(pickuparr.length()==0)
  		{
  			//向主线程返回信息
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETERRFAILPICKUPMAIN;
  			tomain.obj=zhuhepickupjson.toString();
  			mainhand.sendMessage(tomain); // 发送消息	
  		}  	   
  	}
  	String PICKUP_CODE="";
  	//更新取货码信息
  	private String updatepickup(int i,String pick) throws JSONException
  	{  	  		
  		boolean quhuo=false; 
  		PICKUP_CODE=pick;
  		final JSONObject object2=pickuparr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","取货码出货="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=new JSONObject();
  		//第一步，获取PRODUCT_NO和STAUTS
  		final String PRODUCT_NO=object2.getString("PRODUCT_NO");
  		final int STAUTS=object2.getInt("STAUTS");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","取货码PRODUCT_NO="+PRODUCT_NO+",STAUTS="+STAUTS,"server.txt");	
  		//返回值表示，可以取货
  		if(STAUTS==0)
  		{
  			//本地有货物
  			vmc_columnDAO columnDAO = new vmc_columnDAO(ToolClass.getContext());// 创建InaccountDAO对象
  			if(columnDAO.getproductCount(PRODUCT_NO)>0)
  			{
  				quhuo=true;
  			}  			 
  		}
  		//获取成功
  		if(quhuo)
  		{
  			//第一步：确认参数
  			final String out_trade_no=ToolClass.out_trade_no(ToolClass.getContext());	
  			zhuheobj.put("PRODUCT_NO", PRODUCT_NO);
  			zhuheobj.put("out_trade_no", out_trade_no);
  			
  			//第二步：上传给server取货码使用掉
  			String target17 = httpStr+"/api/savePickupCode";	//要提交的目标地址
			final String LAST_EDIT_TIME17=ToolClass.getLasttime();
			//向主线程返回信息
			final Message tomain17=mainhand.obtainMessage();
			tomain17.what=SETNONE;
			//新建Volley 
			mQueue = getRequestQueue();
			//4.准备加载信息设置
			StringRequest stringRequest17 = new StringRequest(Method.POST, target17,  new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {  
				   
				    //如果请求成功
					result = response;	//获取返回的字符串
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
															 
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					result = "请求失败！";
					tomain17.what=SETFAILMAIN;
		    	    mainhand.sendMessage(tomain17); // 发送消息
		    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETFAILMAIN"+result,"server.txt");
				}  
			}) 
			{  
				@Override  
				protected Map<String, String> getParams() throws AuthFailureError {  
					//3.添加params
					Map<String, String> map = new HashMap<String, String>();  
					map.put("Token", Tok);  
					map.put("LAST_EDIT_TIME", LAST_EDIT_TIME17);	
					map.put("PICKUP_CODE", PICKUP_CODE);
					map.put("ORDER_NO", out_trade_no);
					map.put("VMC_NO", vmc_no);
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1=上传取货码完成"+map.toString(),"server.txt");
					return map;  
			   }  
			}; 	
			//5.加载信息并发送到网络上
			mQueue.add(stringRequest17);
			
			
  			//第三步：向主线程返回信息
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETPICKUPMAIN;
  			tomain4.obj=zhuheobj;
  			mainhand.sendMessage(tomain4); // 发送消息 
  		}
  		else
  		{
  			//向主线程返回信息
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETERRFAILPICKUPMAIN;
  			tomain.obj=zhuhepickupjson.toString();
  			mainhand.sendMessage(tomain); // 发送消息	
  		}	
  		return "";
  	}
	
}
