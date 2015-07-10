package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.easivend.app.maintain.GoodsManager;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_classDAO;
import com.easivend.model.Tb_vmc_class;
import com.easivend.view.EVServerService;
import com.google.gson.Gson;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EVServerhttp implements Runnable {
	String vmc_no="";
	String vmc_auth_code="";
	public final static int SETCHILD=2;//what标记,发送给子线程签到
	public final static int SETMAIN=1;//what标记,发送给主线程签到完成返回	
	public final static int SETERRFAILMAIN=4;//what标记,发送给主线程签到故障失败返回	
	
	public final static int SETHEARTCHILD=5;//what标记,发送给子线程心跳
	
	public final static int SETCLASSCHILD=6;//what标记,发送给子线程获取商品分类
	public final static int SETERRFAILCLASSMAIN=7;//what标记,发送给主线程获取商品分类故障
	public final static int SETCLASSMAIN=8;//what标记,发送给主线程获取商品分类返回
	
	public final static int SETPRODUCTCHILD=9;//what标记,发送给子线程获取商品信息
	public final static int SETERRFAILRODUCTMAIN=10;//what标记,发送给主线程获取商品故障
	public final static int SETRODUCTMAIN=11;//what标记,发送给主线程获取商品返回
	
	public final static int SETHUODAOCHILD=12;//what标记,发送给子线程获取货道信息
	public final static int SETERRFAILHUODAOMAIN=13;//what标记,发送给主线程获取货道故障
	public final static int SETHUODAOMAIN=14;//what标记,发送给主线程获取货道返回
	
	public final static int SETHUODAOSTATUONECHILD=15;//what标记,发送给子线程上报一个货道信息
	public final static int SETHUODAOSTATUALLCHILD=16;//what标记,发送给子线程上报全部货道信息
	
	public final static int SETDEVSTATUCHILD=17;//what标记,发送给子线程上报设备状态信息
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
						//向主线程返回签到信息
						Message tomain=mainhand.obtainMessage();
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object=new JSONObject(result);
							int errType =  object.getInt("Error");
							//返回有故障
							if(errType>0)
							{
								tomain.what=SETERRFAILCLASSMAIN;
								tomain.obj=object.getString("Message");
							}
							else
							{
								tomain.what=SETCLASSMAIN;
								tomain.obj=result;
							}			    	    
				    	    mainhand.sendMessage(tomain); // 发送消息							
						}else{
							result = "请求失败！";
							tomain.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain); // 发送消息
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
						}

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
				case SETPRODUCTCHILD://获取商品信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取商品信息","server.txt");
					String target4 = httpStr+"/api/productData";	//要提交的目标地址
					
					HttpClient httpclient4 = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost4 = new HttpPost(target4);	//创建HttpPost对象
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
					List<NameValuePair> params4 = new ArrayList<NameValuePair>();
					params4.add(new BasicNameValuePair("Token", Tok));
					params4.add(new BasicNameValuePair("VMC_NO", vmc_no));
					params4.add(new BasicNameValuePair("PAGE_INDEX", ""));
					params4.add(new BasicNameValuePair("PAGE_SIZE", ""));
					params4.add(new BasicNameValuePair("LAST_EDIT_TIME", ""));
					params4.add(new BasicNameValuePair("PRODUCT_NO", ""));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+params4.toString(),"server.txt");
					try {
						httppost4.setEntity(new UrlEncodedFormEntity(params4, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient4.execute(httppost4);	//执行HttpClient请求
						//向主线程返回签到信息
						Message tomain=mainhand.obtainMessage();
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object=new JSONObject(result);
							int errType =  object.getInt("Error");
							//返回有故障
							if(errType>0)
							{
								tomain.what=SETERRFAILRODUCTMAIN;
								tomain.obj=object.getString("Message");
							}
							else
							{
								tomain.what=SETRODUCTMAIN;
								tomain.obj=result;
							}			    	    
				    	    mainhand.sendMessage(tomain); // 发送消息							
						}else{
							result = "请求失败！";
							tomain.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain); // 发送消息
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
						}

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
				case SETHUODAOCHILD://获取货道信息
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread 获取货道信息","server.txt");
					String target5 = httpStr+"/api/vmcPathConfigDownload";	//要提交的目标地址
					
					HttpClient httpclient5 = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost5 = new HttpPost(target5);	//创建HttpPost对象
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
					List<NameValuePair> params5 = new ArrayList<NameValuePair>();
					params5.add(new BasicNameValuePair("Token", Tok));
					params5.add(new BasicNameValuePair("VMC_NO", vmc_no));
					params5.add(new BasicNameValuePair("LAST_EDIT_TIME", ""));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+params5.toString(),"server.txt");
					try {
						httppost5.setEntity(new UrlEncodedFormEntity(params5, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient5.execute(httppost5);	//执行HttpClient请求
						//向主线程返回签到信息
						Message tomain=mainhand.obtainMessage();
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object=new JSONObject(result);
							int errType =  object.getInt("Error");
							//返回有故障
							if(errType>0)
							{
								tomain.what=SETERRFAILHUODAOMAIN;
								tomain.obj=object.getString("Message");
							}
							else
							{
								tomain.what=SETHUODAOMAIN;
								tomain.obj=result;
							}			    	    
				    	    mainhand.sendMessage(tomain); // 发送消息							
						}else{
							result = "请求失败！";
							tomain.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain); // 发送消息
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
						}

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
				case SETHUODAOSTATUONECHILD://子线程接收主线程单个货道上报消息	
					String CABINET_NO=null;
	    			String PATH_NO=null;
	    			String PATH_STATUS=null;
	    			String PATH_COUNT=null;
	    			String PATH_REMAINING=null;
	    			String PRODUCT_NO=null;
					//1.得到本机编号信息
					JSONObject ev2=null;
					try {
						ev2 = new JSONObject(msg.obj.toString());
						CABINET_NO=ev2.getString("CABINET_NO");
		    			PATH_NO=ev2.getString("PATH_NO");
		    			PATH_STATUS=ev2.getString("PATH_STATUS");
		    			PATH_COUNT=ev2.getString("PATH_COUNT");
		    			PATH_REMAINING=ev2.getString("PATH_REMAINING");
		    			PRODUCT_NO=ev2.getString("PRODUCT_NO");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=vmc_no="+vmc_no+"CABINET_NO="+CABINET_NO
						+" PATH_NO="+PATH_NO+" PATH_STATUS="+PATH_STATUS+" PATH_COUNT="+PATH_COUNT
						+" PATH_REMAINING="+PATH_REMAINING+" PRODUCT_NO="+PRODUCT_NO,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					String target6 = httpStr+"/api/vmcPathStatus";	//要提交的目标地址
					HttpClient httpclient6 = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost6 = new HttpPost(target6);	//创建HttpPost对象
					JSONObject json=new JSONObject();
					try {
						json.put("VMC_NO", vmc_no);
						json.put("PATH_NO", PATH_NO);
						json.put("cabinetNumber", CABINET_NO);
						json.put("PATH_STATUS",Integer.parseInt(PATH_STATUS));
						json.put("PATH_REMAINING",Integer.parseInt(PATH_REMAINING));
						json.put("PATH_COUNT",Integer.parseInt(PATH_COUNT));
						json.put("PRODUCT_NO", PRODUCT_NO);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
//					//1.添加到类集中，其中key,value类型为String
//					Map<String,Object> parammap6 = new TreeMap<String,Object>() ;
////					parammap6.put("VMC_NO",vmc_no);
////					//parammap6.put("cabinetNumber",CABINET_NO);
////					parammap6.put("PATH_NO",PATH_NO);	
////					parammap6.put("PATH_STATUS",PATH_STATUS);	
////					parammap6.put("PATH_COUNT",PATH_COUNT);
////					parammap6.put("PATH_REMAINING",PATH_REMAINING);	
////					parammap6.put("PRODUCT_NO",PRODUCT_NO);	
//					
//					parammap6.put("VMC_NO",vmc_no);
//					parammap6.put("pathID","2596");
//					parammap6.put("cabinetNumber",CABINET_NO);
//					parammap6.put("pathName",PATH_NO);	
//					parammap6.put("pathStatus",PATH_STATUS);	
//					parammap6.put("pathCount",PATH_COUNT);
//					parammap6.put("pathRemaining",PATH_REMAINING);	
//					parammap6.put("productID",PRODUCT_NO);
//					parammap6.put("productNum","12");
//					parammap6.put("LAST_EDIT_TIME",getLasttime());
//					parammap6.put("IS_DISABLE","0");
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap6.toString(),"server.txt");
//					//将2.map类集转为json格式
//					Gson gson6=new Gson();
					String param6=json.toString();		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param6.toString(),"server.txt");
					//3.添加params
					List<NameValuePair> params6 = new ArrayList<NameValuePair>();
					params6.add(new BasicNameValuePair("param", param6));
					params6.add(new BasicNameValuePair("Token", Tok));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+params6.toString(),"server.txt");
					
					try {
						httppost6.setEntity(new UrlEncodedFormEntity(params6, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient6.execute(httppost6);	//执行HttpClient请求
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							
						}else{
							result = "请求失败！";
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");

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
					//设备状态上报
					String target7 = httpStr+"/api/vmcStatus";	//要提交的目标地址
					HttpClient httpclient7 = new DefaultHttpClient();	//创建HttpClient对象
					HttpPost httppost7 = new HttpPost(target7);	//创建HttpPost对象
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
					String param7=gson7.toJson(parammap7);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param7.toString(),"server.txt");
					//3.添加params
					List<NameValuePair> params7 = new ArrayList<NameValuePair>();
					params7.add(new BasicNameValuePair("param", param7));
					params7.add(new BasicNameValuePair("Token", Tok));
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+params7.toString(),"server.txt");
					
					try {
						httppost7.setEntity(new UrlEncodedFormEntity(params7, "utf-8")); //设置编码方式
						HttpResponse httpResponse = httpclient7.execute(httppost7);	//执行HttpClient请求
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
							result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
							
						}else{
							result = "请求失败！";
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");

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
