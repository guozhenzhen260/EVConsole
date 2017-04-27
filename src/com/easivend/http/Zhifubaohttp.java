package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.easivend.alipay.AlipayConfig;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.alipay.HttpRequester;
import com.easivend.alipay.HttpRespons;
import com.example.alizhifu.AlipayAPI;

public class Zhifubaohttp implements Runnable
{
	//交易
	public final static int SETCHILD=2;//what标记,发送给子线程支付宝交易
	public final static int SETMAIN=1;//what标记,发送给主线程支付宝金额二维码	
	public final static int SETFAILPROCHILD=5;//what标记,发送给主线程交易协议失败
	public final static int SETFAILBUSCHILD=6;//what标记,发送给主线程交易信息失败
	public final static int SETFAILNETCHILD=4;//what标记,发送给主线程交易网络
	//查询
	public final static int SETQUERYCHILD=7;//what标记,发送给子线程支付宝查询
	public final static int SETQUERYMAIN=8;//what标记,发送给主线程查询结果正在付款
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
	
	//private final int SETWEIMAIN=3;//what标记,主线程接收到子线程微信金额二维码
	//private final int SETWEICHILD=4;//what标记,发送给子线程微信交易
	private Handler mainhand=null,childhand=null;
	private int zhifubaotype=2;//1使用1.0版本支付宝，2使用2.0版本支付宝口碑
	
	public Zhifubaohttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	@Override
	public void run() {
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<zhifubaothread="+Thread.currentThread().getId(),"log.txt");
		// TODO Auto-generated method stub
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		childhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case SETCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIzhifubao>>二维码]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara = new HashMap<String, String>();
						//1.添加订单信息
						JSONObject ev=null;
						try {
							ev = new JSONObject(msg.obj.toString());							
							sPara.put("out_trade_no", ev.getString("out_trade_no"));//订单编号
							sPara.put("total_fee",ev.getString("total_fee"));//订单总金额		
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sPara.put("subject","支付宝二维码");//订单标题
						//商品明细
						String json=null;
					    try {
					    	JSONObject temp=new JSONObject();
							temp.put("goodsName","商品水");
							temp.put("quantity","1");
						    temp.put("price",ev.getString("total_fee"));
						    JSONArray singArray=new JSONArray();//定义操作数组
						    singArray.put(temp);
						    json=singArray.toString();	
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    sPara.put("goods_detail",json);//商品明细
					    sPara.put("it_b_pay","10m");//交易关闭时间
					    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara.toString(),"log.txt");
					    // 获取支付宝类型
					    vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
					    // 获取所有收入信息，并存储到List泛型集合中
				    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
				    	zhifubaotype=tb_inaccount.getZhifubaoer();
				    	//使用1.0版本的支付宝
				    	if(zhifubaotype==1)
				    	{
						    //2.生成支付请求消息
							Map<String, String> map1 = AlipayConfigAPI.PostAliBuy(sPara);
					        try {          	       	                    	       	
					           HttpRequester request = new HttpRequester();              
					           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
					           //3.发送支付订单信息
					           HttpRespons hr = request.sendPost(url, map1);
					           //4.得到返回字符串
					           String strpicString=hr.getContent();	
					           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
					           //5.解包返回的信息
					           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// 获取返回数据
					           Map<String, String> map2=AlipayConfigAPI.PendAliBuy(is);
					           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map2.toString(),"log.txt");
					           //向主线程返回信息
					           Message tomain=mainhand.obtainMessage();	
					           //协议失败
					           if(map2.get("is_success").equals("F"))
					           {
					        	   tomain.what=SETFAILPROCHILD;
								   tomain.obj=map2.get("error");
					           }
					           else
					           {
					        	   //业务处理失败
					        	   if(map2.get("result_code").equals("FAIL"))
						           {
						        	   tomain.what=SETFAILBUSCHILD;
									   tomain.obj=map2.get("detail_error_code")+map2.get("detail_error_des");
						           }
					        	   //通过支付宝提供的订单直接生成二维码
					        	   else if(map2.get("result_code").equals("SUCCESS"))
						           {
						        	   tomain.what=SETMAIN;
						        	   JSONObject zhuhe=new JSONObject();
						        	   zhuhe.put("qr_code", map2.get("qr_code"));
						        	   zhuhe.put("out_trade_no", ev.getString("out_trade_no"));
									   tomain.obj=zhuhe;
						           }
					           }
					           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
							   mainhand.sendMessage(tomain); // 发送消息
							   
							   
	//				           //下载图片
	//				           result=strpicString.substring(strpicString.indexOf("<pic_url>")+9, strpicString.indexOf("</pic_url>"));
	//						   //txt.setText(strpicString); // 清空内容编辑框	
	//						   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+result);		  		   
	//						   HttpClient httpClient=new DefaultHttpClient();
	//							HttpGet httprequest=new HttpGet(result);
	//							HttpResponse httpResponse;
	//							try {
	//								httpResponse=httpClient.execute(httprequest);
	//								if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//如果请求成功
	//									//result = EntityUtils.toString(httpResponse.getEntity());	//获取返回的字符串
	//									//取得相关信息 取得HttpEntiy  
	//					                HttpEntity httpEntity = httpResponse.getEntity();  
	//					                //获得一个输入流  
	//					                InputStream is = httpEntity.getContent();  
	//					                bitmap = BitmapFactory.decodeStream(is);  
	//					                is.close();  
	//					                Message m = handler.obtainMessage(); // 获取一个Message
	//					                m.what=1;
	//									handler.sendMessage(m); // 发送消息
	//					                 
	//								}else{
	//									result = "请求失败！";
	//								}
	//							} catch (ClientProtocolException e) {
	//								// TODO Auto-generated catch block
	//								e.printStackTrace();
	//							} catch (IOException e) {
	//								// TODO Auto-generated catch block
	//								e.printStackTrace();
	//							}
					       } catch (Exception e) {  
					           //e.printStackTrace();  
					    	   //向主线程返回信息
					           Message tomain=mainhand.obtainMessage();	
					    	   tomain.what=SETFAILNETCHILD;
					    	   tomain.obj="netfail";
					    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
							   mainhand.sendMessage(tomain); // 发送消息
					       }
				       }	
				    	//使用2.0版本的支付宝口碑
				       else if(zhifubaotype==2)	
				       {				    	   
				    	   try {
				    		    String rsp=AlipayAPI.PostAliBuy(sPara);
					   			JSONObject response=new JSONObject(rsp);
					   			JSONObject obj=new JSONObject(response.get("alipay_trade_precreate_response").toString());
					   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
					   			//向主线程返回信息
					   			Message tomain=mainhand.obtainMessage();	
					   			String msg2=obj.getString("msg");
					   			if(msg2.toLowerCase().equals("success"))
					   			{
					   				String qr_code=obj.getString("qr_code");
					   				tomain.what=SETMAIN;
					   				JSONObject zhuhe=new JSONObject();
					        	    zhuhe.put("qr_code", qr_code);
					        	    zhuhe.put("out_trade_no", ev.getString("out_trade_no"));
					   				tomain.obj=zhuhe;
					   			}
					   			else
					   			{
					   				tomain.what=SETFAILBUSCHILD;
					   			    tomain.obj=msg2;					   				
					   			}
					   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
					   			mainhand.sendMessage(tomain); // 发送消息
					   		} catch (Exception e) {
					   			// TODO Auto-generated catch block
					   			e.printStackTrace();
					   		   //向主线程返回信息
					           Message tomain=mainhand.obtainMessage();	
					    	   tomain.what=SETFAILNETCHILD;
					    	   tomain.obj="netfail";
					    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
							   mainhand.sendMessage(tomain); // 发送消息
					   		}
				       }
						
					break;
				case SETQUERYCHILD://子线程接收主线程消息
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIzhifubao>>查询]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
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
				    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara2.toString(),"log.txt");
				    //使用1.0版本的支付宝
			    	if(zhifubaotype==1)
			    	{
					    //2.生成支付请求消息
						Map<String, String> map3 = AlipayConfigAPI.PostAliQuery(sPara2);
				        try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
				           //3.发送支付订单信息
				           HttpRespons hr = request.sendPost(url, map3);
				           //4.得到返回字符串
				           String strpicString=hr.getContent();	
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
				           //5.解包返回的信息
				           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// 获取返回数据
				           Map<String, String> map4=AlipayConfigAPI.PendAliQuery(is);
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map4.toString(),"log.txt");
				           //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				           //协议失败
				           if(map4.get("is_success").equals("F"))
				           {
				        	   tomain.what=SETFAILQUERYPROCHILD;
							   tomain.obj=map4.get("error");
				           }
				           else
				           {
				        	   //业务处理失败
				        	   if(map4.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILQUERYBUSCHILD;
								   tomain.obj=map4.get("detail_error_code")+map4.get("detail_error_des");
					           }
				        	   //交易成功状态
				        	   else if(map4.get("result_code").equals("SUCCESS"))
					           {
				        		   //正在等待支付
				        		   if(map4.get("trade_status").equals("WAIT_BUYER_PAY"))
				        		   {
						        	   tomain.what=SETQUERYMAIN;
									   tomain.obj=map4.get("trade_status");
				        		   }
				        		   //通过支付宝提供的订单直接生成二维码
				        		   else if(map4.get("trade_status").equals("TRADE_SUCCESS"))
				        		   {
						        	   tomain.what=SETQUERYMAINSUCC;
									   tomain.obj=map4.get("trade_status");
				        		   }
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
						   
				       } catch (Exception e) {  
				           e.printStackTrace();
				           //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
				       }
			       }
			       //使用2.0版本的支付宝口碑
			       else if(zhifubaotype==2)	
			       {			    	   
			    	   try {
			    		    String rsp=AlipayAPI.PostAliQuery(sPara2);
				   			JSONObject response=new JSONObject(rsp);
				   			JSONObject obj=new JSONObject(response.get("alipay_trade_query_response").toString());
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
				   			//向主线程返回信息
				   			Message tomain=mainhand.obtainMessage();	
				   			String msg2=obj.getString("msg");
				   			if(msg2.toLowerCase().equals("success"))
				   			{
				   				//正在等待支付
			        		   if(obj.getString("trade_status").equals("WAIT_BUYER_PAY"))
			        		   {
					        	   tomain.what=SETQUERYMAIN;
								   tomain.obj=obj.getString("trade_status");
			        		   }
			        		   //通过支付宝提供的订单直接生成二维码
			        		   else if(obj.getString("trade_status").equals("TRADE_SUCCESS"))
			        		   {
					        	   tomain.what=SETQUERYMAINSUCC;
								   tomain.obj=obj.getString("trade_status");
			        		   }
				   			}
				   			else
				   			{
				   				tomain.what=SETFAILQUERYBUSCHILD;
				   			    tomain.obj=msg2;					   				
				   			}
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
				   			mainhand.sendMessage(tomain); // 发送消息
				   		} catch (Exception e) {
				   			// TODO Auto-generated catch block
				   			e.printStackTrace();
				   			//向主线程返回信息
				            Message tomain=mainhand.obtainMessage();	
				    	    tomain.what=SETFAILNETCHILD;
				    	    tomain.obj="netfail";
				    	    ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						    mainhand.sendMessage(tomain); // 发送消息
				   		}
			       }
				break;
				case SETPAYOUTCHILD://子线程接收主线程消息
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIzhifubao>>退款]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
					Map<String, String> sPara3 = new HashMap<String, String>();
					//1.添加订单信息
					JSONObject ev3=null;
					try {
						ev3 = new JSONObject(msg.obj.toString());							
						sPara3.put("out_trade_no", ev3.getString("out_trade_no"));//订单编号
						sPara3.put("refund_amount", ev3.getString("refund_amount"));
						sPara3.put("out_request_no", ev3.getString("out_request_no"));
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara3.toString(),"log.txt");
				    //使用1.0版本的支付宝
			    	if(zhifubaotype==1)
			    	{
					    //2.生成支付请求消息
						Map<String, String> map5 = AlipayConfigAPI.PostAliPayout(sPara3);
				        try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
				           //3.发送支付订单信息
				           HttpRespons hr = request.sendPost(url, map5);
				           //4.得到返回字符串
				           String strpicString=hr.getContent();	
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
				           //5.解包返回的信息
				           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// 获取返回数据
				           Map<String, String> map6=AlipayConfigAPI.PendAliPayout(is);
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map6.toString(),"log.txt");
				           //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				           //协议失败
				           if(map6.get("is_success").equals("F"))
				           {
				        	   tomain.what=SETFAILPAYOUTPROCHILD;
							   tomain.obj=map6.get("error");
				           }
				           else
				           {
				        	   //业务处理失败
				        	   if(map6.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILPAYOUTBUSCHILD;
								   tomain.obj=map6.get("detail_error_code")+map6.get("detail_error_des");
					           }
				        	   //通过支付宝提供的订单直接生成二维码
				        	   else if(map6.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETPAYOUTMAIN;							   
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
						   
				       } catch (Exception e) {  
				           e.printStackTrace(); 
				           //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
				       }
			    	} 
			    	//使用2.0版本的支付宝口碑
			        else if(zhifubaotype==2)	
			        {			    	   
			    	   try {
			    		    String rsp=AlipayAPI.PostAliPayout(sPara3);
				   			JSONObject response=new JSONObject(rsp);
				   			JSONObject obj=new JSONObject(response.get("alipay_trade_refund_response").toString());
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
				   			//向主线程返回信息
				   			Message tomain=mainhand.obtainMessage();	
				   			String msg2=obj.getString("msg");
				   			if(msg2.toLowerCase().equals("success"))
				   			{
				   				tomain.what=SETPAYOUTMAIN;
								tomain.obj="退款成功";
				   			}
				   			else
				   			{
				   				tomain.what=SETFAILPAYOUTBUSCHILD;
				   			    tomain.obj=msg2;					   				
				   			}
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
				   			mainhand.sendMessage(tomain); // 发送消息
				   		} catch (Exception e) {
				   			// TODO Auto-generated catch block
				   			e.printStackTrace();
				   			//向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
				   		}
			    	   
			        }
				break;	
				case SETDELETECHILD://子线程接收主线程消息
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIzhifubao>>撤销]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
					Map<String, String> sPara4 = new HashMap<String, String>();
					//1.添加订单信息
					JSONObject ev4=null;
					try {
						ev4 = new JSONObject(msg.obj.toString());							
						sPara4.put("out_trade_no", ev4.getString("out_trade_no"));//订单编号
								
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara4.toString(),"log.txt");
				    //使用1.0版本的支付宝
			    	if(zhifubaotype==1)
			    	{
					    //2.生成支付请求消息
						Map<String, String> map7 = AlipayConfigAPI.PostAliDelete(sPara4);
				        try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
				           //3.发送支付订单信息
				           HttpRespons hr = request.sendPost(url, map7);
				           //4.得到返回字符串
				           String strpicString=hr.getContent();	
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
				           //5.解包返回的信息
				           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// 获取返回数据
				           Map<String, String> map8=AlipayConfigAPI.PendAliDelete(is);
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map8.toString(),"log.txt");
				           //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				           //协议失败
				           if(map8.get("is_success").equals("F"))
				           {
				        	   tomain.what=SETFAILDELETEPROCHILD;
							   tomain.obj=map8.get("error");
				           }
				           else
				           {
				        	   //业务处理失败
				        	   if(map8.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILDELETEBUSCHILD;
								   tomain.obj=map8.get("detail_error_code")+map8.get("detail_error_des");
					           }
				        	   //通过支付宝提供的订单直接生成二维码
				        	   else if(map8.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETDELETEMAIN;
								   tomain.obj=map8.get("trade_status");
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");			           
						   mainhand.sendMessage(tomain); // 发送消息
						   
				       } catch (Exception e) {  
				           e.printStackTrace(); 
				           //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
				       }
			    	}
			    	//使用2.0版本的支付宝口碑
			        else if(zhifubaotype==2)	
			        {			    	   
			    	   try {
			    		    String rsp=AlipayAPI.PostAliDelete(sPara4);
				   			JSONObject response=new JSONObject(rsp);
				   			JSONObject obj=new JSONObject(response.get("alipay_trade_cancel_response").toString());
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
				   			//向主线程返回信息
				   			Message tomain=mainhand.obtainMessage();	
				   			String msg2=obj.getString("msg");
				   			if(msg2.toLowerCase().equals("success"))
				   			{
				   				tomain.what=SETDELETEMAIN;
								tomain.obj="撤销成功";
				   			}
				   			else
				   			{
				   				tomain.what=SETFAILDELETEBUSCHILD;
				   			    tomain.obj=msg2;					   				
				   			}
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
				   			mainhand.sendMessage(tomain); // 发送消息
				   		} catch (Exception e) {
				   			// TODO Auto-generated catch block
				   			e.printStackTrace();
				   			//向主线程返回信息
				            Message tomain=mainhand.obtainMessage();	
				    	    tomain.what=SETFAILNETCHILD;
				    	    tomain.obj="netfail";
				    	    ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						    mainhand.sendMessage(tomain); // 发送消息
				   		}
			    	   
			        }
				break;	
				default:
					   //向主线程返回信息
			           Message tomain=mainhand.obtainMessage();	
			    	   tomain.what=SETFAILNETCHILD;
			    	   tomain.obj="netfail";
			    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
					   mainhand.sendMessage(tomain); // 发送消息
				break;
				}
			}
			
		};
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
}
