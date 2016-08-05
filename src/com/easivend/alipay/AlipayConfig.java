package com.easivend.alipay;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = null;
	public static String seller_email=null;
	// 商户的key
	public static String key = null;
	public static String subpartner = null;//分账账号
	// 商户的私钥
	public static String aliprivateKey = null;
	
	//是否使用分账模式,0是不分账,0.99是指99%钱打到分账号上,1%打到主账号上,
	//最大值是1，代表所有钱都打到分账号上
	public static float isalisub = 0;

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
		

	public static String getAliprivateKey() {
		return aliprivateKey;
	}

	public static void setAliprivateKey(String aliprivateKey) {
		AlipayConfig.aliprivateKey = aliprivateKey;
	}
	
	public static float getIsalisub() {
		return isalisub;
	}

	public static void setIsalisub(float isalisub) {
		AlipayConfig.isalisub = isalisub;
	}

	public static String getSubpartner() {
		return subpartner;
	}

	public static void setSubpartner(String subpartner) {
		AlipayConfig.subpartner = subpartner;
	}

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "MD5";

	public static String getPartner() {
		return partner;
	}

	public static void setPartner(String partner) {
		AlipayConfig.partner = partner;
	}

	public static String getSeller_email() {
		return seller_email;
	}

	public static void setSeller_email(String seller_email) {
		AlipayConfig.seller_email = seller_email;
	}

	public static String getKey() {
		return key;
	}

	public static void setKey(String key) {
		AlipayConfig.key = key;
	}

	public static String getInput_charset() {
		return input_charset;
	}
	
	

}
