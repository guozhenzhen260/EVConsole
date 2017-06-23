package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import cn.yoc.unionPay.UnionPay;
import cn.yoc.unionPay.po.UnionOrder;
import cn.yoc.unionPay.sdk.DateUtils;

import com.easivend.common.ToolClass;
import com.easivend.weixing.WeiConfigAPI;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class Yinlianhttp implements Runnable 
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
	private UnionOrder order;
	
	public Yinlianhttp(Handler mainhand) {
		this.mainhand=mainhand;	
		order = new UnionOrder();
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<yinlianhttp="+Thread.currentThread().getId(),"log.txt");
		Looper.prepare();//用户自己定义的类，创建线程需要自己准备loop
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what)
				{
					case SETCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIyinlian>>二维码]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						
						//1.添加订单信息
						JSONObject ev=null;
						try {
							ev = new JSONObject(msg.obj.toString());
							order.setOrderId(ev.getString("out_trade_no"));//订单编号
							long fee=ToolClass.MoneySend(Float.parseFloat(ev.getString("total_fee")));
							String total_fee=String.valueOf(fee);
							order.setTxnAmt(total_fee);//订单总金额	
							order.setTxnTime(DateUtils.currentDateTime("yyyyMMddHHmmss"));//下单时间
							order.setTermId("00000001");//终端号 例如设备号      
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+order.toString(),"log.txt");
						try {
							//2.生成支付请求消息并发送
							Map<String, String> map2 = UnionPay.getInstance().precreate(order);						
							
							ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map2.toString(),"log.txt");
					       //向主线程返回信息
					        Message tomain=mainhand.obtainMessage();
					        if(map2.get("respCode").equals("00"))
				            {
				        	   tomain.what=SETMAIN;
				        	   JSONObject zhuhe=new JSONObject();
				        	   zhuhe.put("code_url", map2.get("qrCode"));
				        	   zhuhe.put("out_trade_no", ev.getString("out_trade_no"));
							   tomain.obj=zhuhe;
				            }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");	
					       mainhand.sendMessage(tomain); // 发送消息	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				        	//向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
				        } 
						
					break;	
					case SETQUERYCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIyinlian>>查询]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						try {
							//3.发送订单信息
							Map<String, String> map4 = UnionPay.getInstance().query(order);
					        ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map4.toString(),"log.txt");
					        //向主线程返回信息
					        Message tomain=mainhand.obtainMessage();
					        if(map4.get("respCode").equals("00"))
				            {
			        		   //支付成功
			        		   if(map4.get("origRespCode").equals("00"))
			        		   {
					        	   tomain.what=SETQUERYMAINSUCC;
								   tomain.obj=map4.get("origRespMsg");
			        		   }
			        		   //正在等待支付或者其他情况
			        		   else 
			        		   {
			        			   tomain.what=SETQUERYMAIN;
								   tomain.obj=map4.get("origRespMsg");
			        		   }
				            }
				            ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");
					        mainhand.sendMessage(tomain); // 发送消息	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				          //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");			           
						   mainhand.sendMessage(tomain); // 发送消息
				        }
					break;
					case SETPAYOUTCHILD://子线程接收主线程消息
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIyinlian>>退款]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara3 = new HashMap<String, String>();
						//1.添加订单信息
						JSONObject ev3=null;
						try {
							ev3 = new JSONObject(msg.obj.toString());							
							long fee3=ToolClass.MoneySend(Float.parseFloat(ev3.getString("refund_fee")));
							String refund_fee=String.valueOf(fee3);
							order.setRefundAmt(refund_fee);//订单总金额	
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+order.toString(),"log.txt");
						try {
							//2.生成支付请求消息并发送
							Map<String, String> map6 = UnionPay.getInstance().refund(order);
                            
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map6.toString(),"log.txt");
					       //向主线程返回信息
					       Message tomain=mainhand.obtainMessage();
					       if(map6.get("respCode").equals("00"))
				           {
				        	   tomain.what=SETPAYOUTMAIN;
							   tomain.obj=map6.get("respMsg");
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");	
					       mainhand.sendMessage(tomain); // 发送消息	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				          //向主线程返回信息
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // 发送消息
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
