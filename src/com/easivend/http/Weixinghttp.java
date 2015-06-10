package com.easivend.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.common.ToolClass;
import com.easivend.weixing.WeiConfigAPI;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class Weixinghttp implements Runnable 
{
	//交易
	public final static int SETCHILD=2;//what标记,发送给子线程支付宝交易
	public final static int SETMAIN=1;//what标记,发送给主线程支付宝金额二维码	
	public final static int SETFAILPROCHILD=5;//what标记,发送给主线程交易协议失败
	public final static int SETFAILBUSCHILD=6;//what标记,发送给主线程交易信息失败
	public final static int SETFAILNETCHILD=4;//what标记,发送给主线程交易网络
	//查询
	public final static int SETQUERYCHILD=7;//what标记,发送给子线程支付宝查询
	public final static int SETQUERYMAIN=8;//what标记,发送给主线程查询结果
	public final static int SETQUERYMAINSUCC=9;//what标记,发送给主线程查询结果付款成功
	public final static int SETFAILQUERYPROCHILD=10;//what标记,发送给主线程交易协议失败
	public final static int SETFAILQUERYBUSCHILD=11;//what标记,发送给主线程交易信息失败
	//退款
	public final static int SETPAYOUTCHILD=12;//what标记,发送给子线程支付宝退款
	public final static int SETPAYOUTMAIN=13;//what标记,发送给主线程退款结果
	public final static int SETFAILPAYOUTPROCHILD=14;//what标记,发送给主线程交易协议失败
	public final static int SETFAILPAYOUTBUSCHILD=15;//what标记,发送给主线程交易信息失败
	//撤销交易
	public final static int SETDELETECHILD=16;//what标记,发送给子线程支付宝撤销交易
	public final static int SETDELETEMAIN=17;//what标记,发送给主线程退款结果
	public final static int SETFAILDELETEPROCHILD=18;//what标记,发送给主线程交易协议失败
	public final static int SETFAILDELETEBUSCHILD=19;//what标记,发送给主线程交易信息失败
	private Handler mainhand=null,childhand=null;
	
	public Weixinghttp(Handler mainhand) {
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
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what)
				{
					case SETCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
						Map<String, String> sPara = new HashMap<String, String>();
						//1.添加订单信息
						JSONObject ev=null;
						try {
							ev = new JSONObject(msg.obj.toString());							
							sPara.put("out_trade_no", ev.getString("out_trade_no"));//订单编号
							long fee=ToolClass.MoneySend(Float.parseFloat(ev.getString("total_fee")));
							String total_fee=String.valueOf(fee);
							sPara.put("total_fee",total_fee);//订单总金额		
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("EV_JNI","Send0.2="+sPara.toString());
						//2.生成支付请求消息
						Map<String, String> map1 = WeiConfigAPI.PostWeiBuy(sPara);
						//3.发送支付订单信息
						String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";			            
						String content= WeiConfigAPI.sendPost(url, map1);
						try {
				            //4.解包返回的信息
				            InputStream is = new ByteArrayInputStream(content.getBytes());// 获取返回数据
					           
				           Map<String, String> map2=WeiConfigAPI.PendWeiBuy(is);
					       Log.i("EV_JNI","rec2="+map2.toString());
					       //向主线程返回信息
					        Message tomain=mainhand.obtainMessage();
					      //协议失败
				           if(map2.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILPROCHILD;
							   tomain.obj=map2.get("return_msg");
				           }
				           else
				           {
				        	   //业务处理失败
				        	   if(map2.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILBUSCHILD;
								   tomain.obj=map2.get("err_code")+map2.get("err_code_des");
					           }
				        	   //通过支付宝提供的订单直接生成二维码
				        	   else if(map2.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETMAIN;
								   tomain.obj=map2.get("code_url");
					           }
				           }
				           Log.i("EV_JNI","rec3="+tomain.obj);	
					       mainhand.sendMessage(tomain); // 发送消息	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				        	//向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   Log.i("EV_JNI","rec="+tomain.obj);				           
						   mainhand.sendMessage(tomain); // 发送消息
				        } 
						
					break;	
					case SETQUERYCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
						Map<String, String> sPara2 = new HashMap<String, String>();
						//1.添加订单信息
						JSONObject ev2=null;
						try {
							ev2 = new JSONObject(msg.obj.toString());							
							sPara2.put("out_trade_no", ev2.getString("out_trade_no"));//订单编号									
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("EV_JNI","Send0.2="+sPara2.toString());
						//2.生成请求消息
						Map<String, String> map3 = WeiConfigAPI.PostWeiQuery(sPara2);
						//3.发送订单信息
						String url2 = "https://api.mch.weixin.qq.com/pay/orderquery";			            
						String content2= WeiConfigAPI.sendPost(url2, map3);
						try {
				            //4.解包返回的信息
				            InputStream is = new ByteArrayInputStream(content2.getBytes());// 获取返回数据
					           
				           Map<String, String> map4=WeiConfigAPI.PendWeiQuery(is);
					       Log.i("EV_JNI","rec2="+map4.toString());
					       //向主线程返回信息
					        Message tomain=mainhand.obtainMessage();
					      //协议失败
				           if(map4.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILQUERYPROCHILD;
							   tomain.obj=map4.get("return_msg");
				           }
				           else
				           {
				        	   //业务处理失败
				        	   if(map4.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILQUERYBUSCHILD;
								   tomain.obj=map4.get("err_code")+map4.get("err_code_des");
					           }
				        	   //交易成功状态
				        	   else if(map4.get("result_code").equals("SUCCESS"))
					           {
				        		  //正在等待支付
				        		   if(map4.get("trade_state").equals("NOTPAY"))
				        		   {
				        			   tomain.what=SETQUERYMAIN;
									   tomain.obj=map4.get("trade_state");
				        		   }
				        		   //通过支付宝提供的订单直接生成二维码
				        		   else if(map4.get("trade_state").equals("SUCCESS"))
				        		   {
						        	   tomain.what=SETQUERYMAINSUCC;
									   tomain.obj=map4.get("trade_state");
				        		   }
					        	   
					           }
				           }
				           Log.i("EV_JNI","rec3="+tomain.obj);	
					       mainhand.sendMessage(tomain); // 发送消息	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				        }
					break;
					case SETPAYOUTCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
						Map<String, String> sPara3 = new HashMap<String, String>();
						//1.添加订单信息
						JSONObject ev3=null;
						try {
							ev3 = new JSONObject(msg.obj.toString());							
							sPara3.put("out_trade_no", ev3.getString("out_trade_no"));//订单编号
							sPara3.put("out_refund_no", ev3.getString("out_refund_no"));//退款单编号
							long fee3=ToolClass.MoneySend(Float.parseFloat(ev3.getString("total_fee")));
							String total_fee=String.valueOf(fee3);
							sPara3.put("total_fee",total_fee);//订单总金额
							
							fee3=ToolClass.MoneySend(Float.parseFloat(ev3.getString("refund_fee")));
							String refund_fee=String.valueOf(fee3);
							sPara3.put("refund_fee",refund_fee);//订单总金额	
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("EV_JNI","Send0.2="+sPara3.toString());
						//2.生成支付请求消息
						Map<String, String> map5 = WeiConfigAPI.PostWeiPayout(sPara3);
						//3.发送支付订单信息
						String url3 = "https://api.mch.weixin.qq.com/secapi/pay/refund";			            
						String content3= WeiConfigAPI.sendPost(url3, map5);
						try {
				            //4.解包返回的信息
				            InputStream is = new ByteArrayInputStream(content3.getBytes());// 获取返回数据
					           
				           Map<String, String> map2=WeiConfigAPI.PendWeiPayout(is);
					       Log.i("EV_JNI","rec2="+map2.toString());
					       //向主线程返回信息
					        Message tomain=mainhand.obtainMessage();
					      //协议失败
				           if(map2.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILPAYOUTPROCHILD;
							   tomain.obj=map2.get("return_msg");
				           }
				           else
				           {
				        	   //业务处理失败
				        	   if(map2.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILPAYOUTBUSCHILD;
								   tomain.obj=map2.get("err_code")+map2.get("err_code_des");
					           }
				        	   //通过支付宝提供的订单直接生成二维码
				        	   else if(map2.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETPAYOUTMAIN;
								   tomain.obj=map2.get("code_url");
					           }
				           }
				           Log.i("EV_JNI","rec3="+tomain.obj);	
					       mainhand.sendMessage(tomain); // 发送消息	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				        } 
						
					break;
				}
			}
			
		};
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}

}
