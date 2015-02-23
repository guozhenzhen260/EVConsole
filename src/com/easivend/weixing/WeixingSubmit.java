package com.easivend.weixing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.util.Log;

import com.easivend.weixing.MD5;

public class WeixingSubmit 
{
	/**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    public static String buildRequestPara(Map<String, String> sParaTemp,String key) {
    	//除去数组中的空值和签名参数
    	Map<String, String> paraMap = paraFilter(sParaTemp);
    	Log.i("EV_JNI","Send2="+paraMap);
        //生成签名结果
        String mysign = buildRequestMysign(paraMap,key);   
        return mysign;
    }
	 // <summary>
	 // 除去数组中的空值和签名参数并以字母a到z的顺序排序
	 // </summary>
	 // <param name="dicArrayPre">过滤前的参数组</param>
	 // <returns>过滤后的参数组</returns>
	static Map<String, String> paraFilter(Map<String, String> sArray)
	{
	
			Map<String, String> result = new TreeMap<String, String>();
			
			List<String> alllist=new ArrayList<String>();
	        if (sArray == null || sArray.size() <= 0) {
	            return result;
	        }
	
	        for (String key : sArray.keySet()) {
	            String value = sArray.get(key);
	            if (value == null || value.equals("") || key.equalsIgnoreCase("key")
	                ) {
	                continue;
	            }             
	            alllist.add(key);
	        }
	        
	        String asc[]= alllist.toArray(new String[]{});
	        for(int i=0; i<asc.length; i++)
	        {
	            for(int j=i+1; j<asc.length; j++)
	            {
	
	               if(asc[i].compareTo(asc[j])>0)
	               {
	                   String t = asc[i];
	                   asc[i]=asc[j];
	                   asc[j]=t;
	               }
	            }
	        }
	        for (String key : asc) 
	        {  
	        	//Log.i("EV_JNI",key + " = " + result.get(key));  
	        	result.put(key, sArray.get(key));   
	        }  
	        
	        return result;
	}
	 
	//添加MD5校验生成签名结果
	static String buildRequestMysign(Map<String, String> mapArr,String key)
	{
	   String preStr = createLinkString(mapArr);
	   preStr += "&key="+key;
	   //Log.i("EV_JNI","Send3="+preStr);
	    //默认 MD5校验
	   String mysign=null;
	   mysign=MD5.GetMD5Code(preStr).toUpperCase();
	   //String mysign=md5(preStr.getBytes());
	   Log.i("EV_JNI","Send4="+mysign);
	   return mysign;
	}
	//把参数组中所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
	static String createLinkString(Map<String, String> mapArr)
	{
		StringBuilder prestr = new StringBuilder();
		//遍历内容
		Set<Map.Entry<String,String>> allset=mapArr.entrySet();  //实例化
	    Iterator<Map.Entry<String,String>> iter=allset.iterator();
	    while(iter.hasNext())
	    {
	        Map.Entry<String,String> me=iter.next();
	        prestr.append(me.getKey() + "=" + me.getValue() + "&");
	    }   
	    //去掉最後一個&字符
	    String str=prestr.toString();
	    //Log.i("EV_JNI","Send3.1="+str);
	    int len = str.length();
	    if(len > 0)
	    	str=str.substring(0,len-1);
	    //Log.i("EV_JNI","Send3.2="+str);
	    return str;
	}
}
