package com.easivend.http;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.easivend.evprotocol.AlipayConfig;
import com.easivend.alipay.AlipaySubmit;
import com.easivend.evprotocol.ToolClass;
import com.easivend.alipay.HttpRequester;
import com.easivend.alipay.HttpRespons;

public class Zhifubaohttp implements Runnable
{
	private PostZhifubaoInterface callBack=null;//与activity交互注册回调
	private final int SETMAIN=1;//what标记,设置主线程
	private final int SETCHILD=2;//what标记,设置子线程
	private Handler mainhand=null,childhand=null;
	
	public Zhifubaohttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		childhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case SETCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIzhifubao>>]"+msg.obj.toString());
						//往支付宝服务器发送交易信息
						Map<String, String> sPara = new HashMap<String, String>();
						 sPara.put("service","alipay.acquire.precreate");
						 sPara.put("partner","2088711021642556");//支付宝 PID号
						 sPara.put("_input_charset","utf-8");//编码		
						 sPara.put("seller_email", "2544282805@qq.com");//卖家支付宝帐户		 
						 sPara.put("product_code","QR_CODE_OFFLINE");//二维码
						 sPara.put("total_fee","0.1");//订单总金额		   
						 sPara.put("out_trade_no","205211376305670");//商户网站唯一订单号		 
						 sPara.put("subject","订单下载");	 
					    String json=null;
					    try {
					    	JSONObject temp=new JSONObject();
							temp.put("goodsName","water");
							temp.put("quantity","1");
						    temp.put("price","0.1");
						    JSONArray singArray=new JSONArray();//定义操作数组
						    singArray.put(temp);
						    json=singArray.toString();	
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					     sPara.put("goods_detail",json);	 
						 Map<String, String> map1 = AlipaySubmit.buildRequestPara(sPara);
						 Log.i("EV_JNI","Send1="+map1);
				       try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.input_charset;           
				           HttpRespons hr = request.sendPost(url, map1);
				           //result=hr.getContent();           
				           String strpicString=hr.getContent();	//得到返回字符串
				           
				           
				           //通过支付宝提供的订单直接生成二维码
				           String result=strpicString.substring(strpicString.indexOf("<qr_code>")+9, strpicString.indexOf("</qr_code>"));
				           Log.i("EV_JNI","rec1="+result);
				           Message tomain=mainhand.obtainMessage();
						   tomain.what=SETMAIN;
						   tomain.obj=result;
						   mainhand.sendMessage(tomain); // 发送消息
//				           //下载图片
//				           result=strpicString.substring(strpicString.indexOf("<pic_url>")+9, strpicString.indexOf("</pic_url>"));
//						   //txt.setText(strpicString); // 清空内容编辑框	
//						   Log.i("EV_JNI","rec1="+result);		  		   
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
				           e.printStackTrace();  
				       }
						
						
					break;

				default:
					break;
				}
			}
			
		};
		Looper.loop();//用户自己定义的类，创建线程需要自己准备loop
	}
}
