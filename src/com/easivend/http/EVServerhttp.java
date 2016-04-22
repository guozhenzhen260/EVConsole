package com.easivend.http;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easivend.common.ToolClass;
import com.google.gson.Gson;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class EVServerhttp implements Runnable {
	RequestQueue mQueue = null; 
	String vmc_no="";
	String vmc_auth_code="";
	public final static int SETCHILD=2;//what标记,发送给子线程签到
	public final static int SETMAIN=1;//what标记,发送给主线程签到完成返回	
	public final static int SETERRFAILMAIN=4;//what标记,发送给主线程签到故障失败返回	
	
	public final static int SETHEARTCHILD=5;//what标记,发送给子线程心跳
	public final static int SETERRFAILHEARTMAIN=6;//what标记,发送给主线程心跳故障
	public final static int SETHEARTMAIN=7;//what标记,发送给主线程获取心跳返回
	
	public final static int SETCLASSCHILD=8;//what标记,发送给子线程获取商品分类
	public final static int SETERRFAILCLASSMAIN=9;//what标记,发送给主线程获取商品分类故障
	public final static int SETCLASSMAIN=10;//what标记,发送给主线程获取商品分类返回
	
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
	
	public final static int SETCHECKCHILD=26;//what标记,发送给子线程更改签到信息码
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
					Map<String, String> list=ToolClass.ReadConfigFile();
					if(list!=null)
					{				       
						httpStr= list.get("server");
					}
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 签到["+Thread.currentThread().getId()+"]="+httpStr,"server.txt");
					//新建Volley
					mQueue = Volley.newRequestQueue(ToolClass.getContext()); 
					
					//2.设置参数,设置服务器地址
					String target = httpStr+"/api/vmcCheckin";	//要提交的目标地址
					//1.添加到类集中，其中key,value类型为String
					Map<String,Object> parammap = new TreeMap<String,Object>() ;
					parammap.put("vmc_no",vmc_no);
					parammap.put("vmc_auth_code",vmc_auth_code);			
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap.toString(),"server.txt");
					//将2.map类集转为json格式
					Gson gson=new Gson();
					final String param=gson.toJson(parammap);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param.toString(),"server.txt");
					
					//向主线程返回信息
					final Message tomain1=mainhand.obtainMessage();
					//4.设备签到
					StringRequest stringRequest = new StringRequest(Method.POST, target,  new Response.Listener<String>() {  
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
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+param.toString(),"server.txt");
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
					
					//向主线程返回信息
					final Message tomain2=mainhand.obtainMessage();
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
					
					//向主线程返回信息
					final Message tomain3=mainhand.obtainMessage();
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
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","旧的后台","server.txt");
										ToolClass.setServerVer(0);
										tomain3.what=SETCLASSMAIN;
										tomain3.obj=result;	
										mainhand.sendMessage(tomain3); // 发送消息
									}
									else if(jsonObject.has("List")==true)
									{
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","一期的后台","server.txt");
										ToolClass.setServerVer(1);
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
					        ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
					        return map;  
					   }  
					}; 	
					//5.加载信息并发送到网络上
					mQueue.add(stringRequest3);									
					break;
				case SETPRODUCTCHILD://获取商品信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取商品信息["+Thread.currentThread().getId()+"]","server.txt");
					String target4 = httpStr+"/api/productData";	//要提交的目标地址
					final String LAST_EDIT_TIME4=msg.obj.toString();
					
					//向主线程返回信息
					final Message tomain4=mainhand.obtainMessage();
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
					
					//向主线程返回信息
					final Message tomain5=mainhand.obtainMessage();
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
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap7.toString(),"server.txt");
					//将2.map类集转为json格式
					Gson gson7=new Gson();
					final String param7=gson7.toJson(parammap7);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param7.toString(),"server.txt");
					//向主线程返回信息
					final Message tomain7=mainhand.obtainMessage();
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
					//向主线程返回信息
					final Message tomain11=mainhand.obtainMessage();
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
			ATT_ID=ATT_ID.substring(0,ATT_ID.indexOf(".jpg"));
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
					String serip=httpStr.substring(0,httpStr.lastIndexOf('/'));
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
					if(ATT_ID.isEmpty())
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
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=orderNo="+orderNo+"orderTime="+orderTime+"orderStatus="+orderStatus+"payStatus="
				+payStatus+"payType="+payType+"shouldPay="+shouldPay+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productNo+"quantity="+quantity+
				"actualQuantity="+actualQuantity+"customerPrice="+customerPrice+"productName="+productName,"server.txt");			
			    	
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
			orderpay.put("RefundAmount", RefundAmount);//退款金额,如1.5元
			orderpay.put("smallChange", 0);
			orderpay.put("realNote", 1);
			orderpay.put("realCoins", 0);
			orderpay.put("smallNote", 1);
			orderpay.put("smallConis", 0);
			orderpay.put("integre", 0);
			orderpay.put("payDesc", "test");
			orderpay.put("payTime", orderTime);	//支付时间
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderpay="+orderpay.toString(),"server.txt");
			
			JSONObject orderrefund=new JSONObject();
			orderrefund.put("RefundId", 122);
			orderrefund.put("orderNo", orderNo);//商品id
			orderrefund.put("Reason", "test");
			orderrefund.put("Amount", 1);
			orderrefund.put("Refund", 0);
			orderrefund.put("Debt", 0);
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

	
}
