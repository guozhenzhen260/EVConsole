package com.easivend.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.easivend.weixing.WeixingSubmit;
import com.easivend.alipay.HttpRequester;
import com.easivend.alipay.HttpRespons;

public class Zhifubaohttp implements Runnable
{
	private PostZhifubaoInterface callBack=null;//与activity交互注册回调
	private final int SETMAIN=1;//what标记,主线程接收到子线程支付宝金额二维码
	private final int SETCHILD=2;//what标记,发送给子线程支付宝交易
	private final int SETWEIMAIN=3;//what标记,主线程接收到子线程微信金额二维码
	private final int SETWEICHILD=4;//what标记,发送给子线程微信交易
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
				case SETWEICHILD://子线程接收主线程消息
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
					Map<String, String> swPara = new HashMap<String, String>();
					 
					//可以使用的例子，这个例子是微信提供的demo
//					 sPara.put("appid","wxd930ea5d5a258f4f");
//					 sPara.put("auth_code","123456");//订单总金额
//					 sPara.put("body","test");//编码		
//					 sPara.put("device_info","123");//商户网站唯一订单号	
//					 sPara.put("mch_id","1900000109");
//					 sPara.put("nonce_str","960f228109051b9969f76c82bde183ac");	 
//					 sPara.put("out_trade_no", "1400755861");//卖家支付宝帐户
//					 sPara.put("spbill_create_ip", "127.0.0.1");//卖家支付宝帐户
//					 sPara.put("sub_mch_id", "124");
//					 sPara.put("total_fee", "1");//卖家支付宝帐户
//					 String sign=buildRequestPara(sPara);
//					 StringBuilder xml = new StringBuilder();
//				     xml.append("<xml>");
//				     xml.append("<appid>wxd930ea5d5a258f4f</appid>");
//				     xml.append("<auth_code>123456</auth_code>");
//				     xml.append("<body><![CDATA[test]]></body>");
//				     xml.append("<device_info>123</device_info>");
//				     xml.append("<mch_id>1900000109</mch_id>");
//				     xml.append("<nonce_str>960f228109051b9969f76c82bde183ac</nonce_str>");
//				     xml.append("<out_trade_no>1400755861</out_trade_no>");
//				     xml.append("<spbill_create_ip>127.0.0.1</spbill_create_ip>");
//				     xml.append("<sub_mch_id>124</sub_mch_id>");
//				     xml.append("<total_fee>1</total_fee>");
//				     xml.append("<sign><![CDATA["+sign+"]]></sign>");
//				     xml.append("</xml>");
					
					
					swPara.put("appid","wx37a5d49081f487c4");
					swPara.put("mch_id","10052966");
					swPara.put("nonce_str","960f228109051b9969f76c82bde183ac");	 		 
					swPara.put("body","test");//编码		
					swPara.put("out_trade_no", "1400755861");//卖家支付宝帐户
					swPara.put("total_fee", "1");//卖家支付宝帐户
					swPara.put("spbill_create_ip", "127.0.0.1");//卖家支付宝帐户
					swPara.put("notify_url", "127.0.0.1");//卖家支付宝帐户
					swPara.put("trade_type","NATIVE");//商户网站唯一订单号	
					String key="1bd78d29964553116c7c405dd87b2072";
					String sign=WeixingSubmit.buildRequestPara(swPara,key);
					 
					 StringBuilder xml = new StringBuilder();
				     xml.append("<xml>");
				     xml.append("<appid>wx37a5d49081f487c4</appid>");
				     xml.append("<mch_id>10052966</mch_id>");
				     xml.append("<nonce_str>960f228109051b9969f76c82bde183ac</nonce_str>");
				     xml.append("<body><![CDATA[test]]></body>");
				     xml.append("<out_trade_no>1400755861</out_trade_no>");
				     xml.append("<total_fee>1</total_fee>");
				     xml.append("<spbill_create_ip>127.0.0.1</spbill_create_ip>");	     
				     xml.append("<notify_url>127.0.0.1</notify_url>");	     
				     xml.append("<trade_type>NATIVE</trade_type>");
				     xml.append("<sign><![CDATA["+sign+"]]></sign>");
				     xml.append("</xml>");	
					
				     try {
				            byte[] xmlbyte = xml.toString().getBytes("UTF-8");
				            
				            Log.i("EV_JNI","Send5="+xml);

				            URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");
				            
				            
				            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				            conn.setConnectTimeout(5000);
				            conn.setDoOutput(true);// 允许输出
				            conn.setDoInput(true);
				            conn.setUseCaches(false);// 不使用缓存
				            conn.setRequestMethod("POST");
				            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
				            conn.setRequestProperty("Charset", "UTF-8");
				            conn.setRequestProperty("Content-Length",
				                    String.valueOf(xmlbyte.length));
				            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
				            conn.setRequestProperty("X-ClientType", "2");//发送自定义的头信息

				            conn.getOutputStream().write(xmlbyte);
				            conn.getOutputStream().flush();
				            conn.getOutputStream().close();


				            if (conn.getResponseCode() != 200)
				                throw new RuntimeException("请求url失败");

				            InputStream is = conn.getInputStream();// 获取返回数据
				              

				            // 使用输出流来输出字符(可选)
				            ByteArrayOutputStream out = new ByteArrayOutputStream();
				            byte[] buf = new byte[1024];
				            int len;
				            while ((len = is.read(buf)) != -1) {
				                out.write(buf, 0, len);
				            }
				            String strpicStringwei = out.toString("UTF-8");
				            out.close();
				            
				           String resultwei=strpicStringwei.substring(strpicStringwei.indexOf("<code_url><![CDATA[")+19, strpicStringwei.indexOf("]]></code_url>"));
				 		   //txt.setText(strpicString); // 清空内容编辑框	
				 		   Log.i("EV_JNI","rec1="+resultwei);
				 		   Message tomain=mainhand.obtainMessage();
						   tomain.what=SETWEIMAIN;
						   tomain.obj=resultwei;
						   mainhand.sendMessage(tomain); // 发送消息
				           
				     } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
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
