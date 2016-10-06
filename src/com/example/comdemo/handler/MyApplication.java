package com.example.comdemo.handler;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

/**
 * 
 * @ClassName: MyApplication.java
 * @Description: application共享信息
 * @author wxy
 * @version V1.0
 * @Date 2015年12月08日
 */
@SuppressWarnings({ "unused" })
public class MyApplication extends Application {
	String strError = "";
	
	@Override
	public void onCreate() {
		super.onCreate();
		/*
		 * 在Application里面加上下面这2句：
		 * 这样就可以随时得到crash时候的log信息了
		 */
		CustomCrashHandler catchHandler = CustomCrashHandler.getInstance();
        catchHandler.init(getApplicationContext()); 
        
		setStrError(strError);
		
	}
	public String getStrError() {
		return strError;
	}
	public void setStrError(String strError) {
		this.strError = strError;
	}
	
}
