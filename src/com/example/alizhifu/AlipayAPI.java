package com.example.alizhifu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.easivend.alipay.AlipayConfig;
import com.easivend.common.ToolClass;

public class AlipayAPI 
{
	static String appId ="2015072300183987";//开发者账号appid,唯一
	//私钥，每个商户号都对应唯一的，在本程序里面用来作RSA签名参数
	//static String privateKey ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANHnYMLymQkH3wa2TgtMaDy9FwhqTEdfwsXWbv4MmoF0ag8X/ZffwaQFfcORWfzOXSMC9epJFvXFY4MPx4HaX1dU6nsM0WBYHmBFxCwSwOmrOVHAq9VdSNKdE3W4EoeCFWfD8vC4vjGTTUpowHeQWMx0NIEDBKHRPOQbsKIwAL7PAgMBAAECgYAazfJEUtiKF7A6WjNzK+mvv/HeCDz/bFIiE3UPCir81xHoJYcjytYejPj3bWtRZkTsgKdIqNa+wdsoVG6EvY8pC3CpMlliWfRuYBTOyMCS/RVGTkftM6vuS07bW4Je1qvoO26pCk4kgRl0Z3GoYw9AOi5cl1q0fZDvsncDG8Dh0QJBAPs2cqE3K/+UaDdxWd/di1xi0p4SZJ/4WbSUUCwe1L3o3/jwLKgvCASlPeIVcJamhyfdbmJ4E1QWsfeuPhOUinkCQQDV52cvWs1vDyTKxvjQzwEFkajHzctLpCpBoO1funxwVkmYU/cmRZn292mFGywHA4Gq9vgF+S7jwzOYvwQw20GHAkByVcGuZnH8DQux0EFbhnXbQo8hqrVpqZsKeUZUDmQ9WzQ1FPr+QQmhM6QKtj9cEccJ+do3rvb9Gqc9V2yhdMXhAkEAscu4PupQ28FQqaQdaSLHDKP4EKwEEQmRfh+PbwSJLq7qWU1hn1Q3F8qq0NK3E9VcUIkbu4tV6Ed2eb48c4ervQJAHWO0ocjLrtgIHW41b2u4mEcD3bjeeOvdfGCIDXHPkchHw5L+fbk2JkLv3DEY468eEUyH7xFHCXjFHYYWQICYWw==";
	//公钥，每个商户号都对应唯一的，分别注册到支付宝开放平台那里，在本程序里面不使用
	String public_key ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDR52DC8pkJB98Gtk4LTGg8vRcIakxHX8LF1m7+DJqBdGoPF/2X38GkBX3DkVn8zl0jAvXqSRb1xWODD8eB2l9XVOp7DNFgWB5gRcQsEsDpqzlRwKvVXUjSnRN1uBKHghVnw/LwuL4xk01KaMB3kFjMdDSBAwSh0TzkG7CiMAC+zwIDAQAB";
	//发送二维码
	public static String PostAliBuy(Map<String, String> list)
	{
		String rsp=null;
		
		ToolClass.CheckAliWeiFile();
		//往支付宝服务器发送交易信息
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = list.get("out_trade_no");

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject =  list.get("subject");

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = list.get("total_fee");
        
        JSONObject bizContent=new JSONObject();
        try {
			bizContent.put("out_trade_no", outTradeNo);
			bizContent.put("total_amount", totalAmount);
			bizContent.put("subject", subject);
			
			//添加分账账号
			if(AlipayConfig.getIsalisub()>0)
	        {
	        	JSONObject royalty_info=new JSONObject();
	        	royalty_info.put("royalty_type", "ROYALTY");
	        	
	        	JSONArray royalty_detail_infos=new JSONArray();
	        	JSONObject royalty_detail=new JSONObject();
	        	royalty_detail.put("serial_no", 1);
	        	royalty_detail.put("trans_in_type", "userId");
	        	royalty_detail.put("batch_no", 123);
	        	royalty_detail.put("trans_out_type", "userId");
	        	royalty_detail.put("trans_out", AlipayConfig.getPartner());//主账号id号
	        	royalty_detail.put("trans_in", "2088101126708402");//子账号id号
	        	royalty_detail.put("amount", totalAmount);
	        	royalty_detail.put("desc", "分账测试1");
	        	royalty_detail.put("amount_percentage", "100");
	        	royalty_detail_infos.put(royalty_detail);
	        	royalty_info.put("royalty_detail_infos", royalty_detail_infos);
	        	bizContent.put("royalty_info", royalty_info);
	        }
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        Log.i("EV_JNI","trade.precreate bizContent:" + bizContent);
        
        Map<String,String> request = new HashMap<String,String>();
        request.put("biz_content", bizContent.toString());
        request.put("method", "alipay.trade.precreate");//预下单

        try {
        	rsp=doPost(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return rsp;
	}
	
	//生成查询请求消息
    public static String PostAliQuery(Map<String, String> list) 
    		throws Exception
    {
    	String rsp=null;
    	
    	String outTradeNo = list.get("out_trade_no");
    	
    	JSONObject bizContent=new JSONObject();
        try {
			bizContent.put("out_trade_no", outTradeNo);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Log.i("EV_JNI","trade.query bizContent:" + bizContent);
        
        Map<String,String> request = new HashMap<String,String>();
        request.put("biz_content", bizContent.toString());
        request.put("method", "alipay.trade.query");//查询
        
        try {
        	rsp=doPost(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return rsp;
    }
    
    //撤销交易消息
    public static String PostAliDelete(Map<String, String> list) 
    {
    	String rsp=null;
    	
    	String outTradeNo = list.get("out_trade_no");
    	
    	JSONObject bizContent=new JSONObject();
        try {
			bizContent.put("out_trade_no", outTradeNo);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Log.i("EV_JNI","trade.cancel bizContent:" + bizContent);
        
        Map<String,String> request = new HashMap<String,String>();
        request.put("biz_content", bizContent.toString());
        request.put("method", "alipay.trade.cancel");//撤销
        
        try {
        	rsp=doPost(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return rsp;
    }
    
    //生成退款消息
    public static String PostAliPayout(Map<String, String> list) 
    {
    	String rsp=null;
    	
    	String outTradeNo = list.get("out_trade_no");
    	
    	String totalAmount = list.get("refund_amount");
    	
    	JSONObject bizContent=new JSONObject();
        try {
			bizContent.put("out_trade_no", outTradeNo);
			bizContent.put("refund_amount", totalAmount);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Log.i("EV_JNI","trade.refund bizContent:" + bizContent);
        
        Map<String,String> request = new HashMap<String,String>();
        request.put("biz_content", bizContent.toString());
        request.put("method", "alipay.trade.refund");//查询
        
        try {
        	rsp=doPost(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return rsp;
    }
	
	//组包
    private static String doPost(Map<String,String> request)
            throws Exception
	{
		
		RequestParametersHolder requestHolder = new RequestParametersHolder();
		Map<String,String> appParams = new HashMap<String,String>();
		appParams.put("biz_content",request.get("biz_content"));
		requestHolder.setApplicationParams(appParams);
		
		
		
		Map<String,String> protocalMustParams = new HashMap<String,String>();
		protocalMustParams.put("method", request.get("method"));//预下单
		protocalMustParams.put("version", "1.0");
		protocalMustParams.put("app_id", appId);
		protocalMustParams.put("sign_type", "RSA");
		protocalMustParams.put("charset", "UTF-8");
		
		Long timestamp = System.currentTimeMillis();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		protocalMustParams.put("timestamp", df.format(new Date(timestamp)));
		requestHolder.setProtocalMustParams(protocalMustParams);
		
		Map<String,String> protocalOptParams = new HashMap<String,String>();
		protocalOptParams.put("format", "json");
		protocalOptParams.put("alipay_sdk", "alipay-sdk-java-dynamicVersionNo");
		requestHolder.setProtocalOptParams(protocalOptParams);
		
		
		//sign
		String signContent = AlipaySignature.getSignatureContent(requestHolder);
		//得到RSA的sign
		protocalMustParams.put("sign",
		AlipaySignature.rsaSign(signContent, AlipayConfig.getAliprivateKey(), "UTF-8"));
		
		
		StringBuffer urlSb = new StringBuffer("https://openapi.alipay.com/gateway.do");
		try {
			String sysMustQuery = buildQuery(requestHolder.getProtocalMustParams(),
				"UTF-8");
			String sysOptQuery = buildQuery(requestHolder.getProtocalOptParams(), "UTF-8");
			
			urlSb.append("?");
			urlSb.append(sysMustQuery);
			if (sysOptQuery != null & sysOptQuery.length() > 0) {
			urlSb.append("&");
			urlSb.append(sysOptQuery);
			}
		} 
		catch (IOException e) {
			throw new Exception(e);
		}
		Log.i("EV_JNI","Send1=urlandSign="+urlSb);
		Log.i("EV_JNI","Send1=content="+appParams);
		
		
		String rsp = null;
		try {			
			rsp = doPost(urlSb.toString(), appParams, "UTF-8", 9000,
					3000);			
		} catch (IOException e) {
			throw new Exception(e);
		}
		return rsp;		
	}
    
    static String buildQuery(Map<String, String> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 蹇界暐鍙傛暟鍚嶆垨鍙傛暟鍊间负绌虹殑鍙傛暟
            if (StringUtils.areNotEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }

        return query.toString();
    }
    
    //AES加密
    static String doPost(String url, Map<String, String> params, String charset,
            int connectTimeout, int readTimeout) throws Exception {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		//AES加密
		String query = buildQuery(params, charset);
		Log.i("EV_JNI","send2=AES="+query);
		byte[] content = {};
		//getbytes
		if (query != null) {
			content = query.getBytes(charset);
		} 
		return doPost(url, ctype, content, connectTimeout, readTimeout);
    }
    
    //发送
    public static String doPost(String urlstr, String ctype, byte[] contents, int connectTimeout,
            int readTimeout) throws Exception 
    {
		String content=null;    	
		
		Log.i("EV_JNI","send3=str="+urlstr);
		Log.i("EV_JNI","send3=ctype="+ctype);
		Log.i("EV_JNI","Send3=content="+contents);
		//第二种可行的方案
		//4.发送信息	
		byte[] xmlbyte = contents;
		URL url = new URL(urlstr);    
		HttpsURLConnection  conn = (HttpsURLConnection) url.openConnection();
		//if(ToolClass.getSsl()!=null)
		//{
		//conn.setSSLSocketFactory(ToolClass.getSsl());
		//}
		conn.setConnectTimeout(connectTimeout);//设置连接主机超时（单位：毫秒）
		conn.setReadTimeout(readTimeout);//设置从主机读取数据超时（单位：毫秒）
		conn.setDoOutput(true);// 允许输出
		conn.setDoInput(true);// 允许输入
		conn.setUseCaches(false);// 不使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Length",String.valueOf(contents.length));
		conn.setRequestProperty("Content-Type", ctype);
		conn.setRequestProperty("X-ClientType", "2");//发送自定义的头信息
		
		conn.getOutputStream().write(xmlbyte);
		conn.getOutputStream().flush();
		conn.getOutputStream().close();
		
		
		if (conn.getResponseCode() != 200)
		throw new RuntimeException("请求url失败");
		
		InputStream in = conn.getInputStream();// 获取返回数据
		
		BufferedReader bufferedReader = new BufferedReader(  
		new InputStreamReader(in));  
		StringBuffer temp = new StringBuffer();  
		String line = bufferedReader.readLine();  
		while (line != null) {  
		temp.append(line).append("\r\n");  
		line = bufferedReader.readLine();  
		}  
		bufferedReader.close(); 
		content = new String(temp.toString().getBytes(), "UTF-8"); 				            
		Log.i("EV_JNI","rec1="+content);
		
		
		return content;
    }
}
