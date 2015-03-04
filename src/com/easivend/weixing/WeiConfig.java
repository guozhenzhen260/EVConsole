package com.easivend.weixing;

public class WeiConfig 
{
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String weiappid = "";
	public static String weimch_id="";
	// 商户的私钥
	public static String weikey = "";
	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "MD5";

	public static String getWeiappid() {
		return weiappid;
	}

	public static void setWeiappid(String weiappid) {
		WeiConfig.weiappid = weiappid;
	}

	public static String getWeimch_id() {
		return weimch_id;
	}

	public static void setWeimch_id(String weimch_id) {
		WeiConfig.weimch_id = weimch_id;
	}

	public static String getWeikey() {
		return weikey;
	}

	public static void setWeikey(String weikey) {
		WeiConfig.weikey = weikey;
	}

	public static String getInput_charset() {
		return input_charset;
	}

	public static void setInput_charset(String input_charset) {
		WeiConfig.input_charset = input_charset;
	}

	public static String getSign_type() {
		return sign_type;
	}

	public static void setSign_type(String sign_type) {
		WeiConfig.sign_type = sign_type;
	}
	
}
