package com.example.comdemo.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

public class CustomCrashHandler implements UncaughtExceptionHandler {  
    private static final String TAG = "CrashDemo";  
    private static final String FOLDER_NAME = "crash";
//    private static final String FILE_NAME = "eauth_sdk.log";
    private static final String FILE_NAME = "ComDemoLog.txt";
    
    private Context mContext;  
    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();  
    private static CustomCrashHandler mInstance;  
    // 系统默认的UncaughtException处理类
//    private Thread.UncaughtExceptionHandler mDefaultHandler;
      
    private CustomCrashHandler(){}  
    /** 
     * 单例模式，保证只有一个CustomCrashHandler实例存在 
     * @return 
     */  
    public static CustomCrashHandler getInstance(){  
    	if(mInstance == null){
    		mInstance = new CustomCrashHandler();  
    	}
        return mInstance;  
    }  
  
    /** 
     * 异常发生时，系统回调的函数，我们在这里处理一些操作 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        //将一些信息保存到SDcard中  
        savaInfoToSD(mContext, ex);  
          
        //提示用户程序即将退出  
        showToast(mContext); 

        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.exit(0);
          
        //完美退出程序方法  
        //ExitAppUtils.getInstance().exit();  
          
    }  
  
      
    /** 
     * 为我们的应用程序设置自定义Crash处理 
     */  
    public void init(Context context){  
        mContext = context;  
        // 获取系统默认的UncaughtException处理器
//        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
      
    /** 
     * 显示提示信息，需要在线程中显示Toast 
     * @param context 
     * @param msg 
     */  
    private void showToast(final Context context){  
        new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
                Looper.prepare();  

//                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)  
//                .setMessage("SDK崩溃了...").setNeutralButton("我知道了", new OnClickListener() {  
//                    @Override  
//                    public void onClick(DialogInterface dialog, int which) {  
//                        System.exit(0);  
//                    }  
//                })  
//                .create().show(); 
                
                Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出。", Toast.LENGTH_LONG).show();
                
                Looper.loop();  
            }  
        }).start();  
    }  
      
      
    /** 
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中 
     * @param context 
     * @return 
     */  
    private HashMap<String, String> obtainSimpleInfo(Context context){  
        HashMap<String, String> map = new HashMap<String, String>();  
        PackageManager mPackageManager = context.getPackageManager();  
        PackageInfo mPackageInfo = null;  
        try {  
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
          
        map.put("CREATED_TIME", paserTime(System.currentTimeMillis()));
        map.put("VERSION_NAME", mPackageInfo.versionName);  
        map.put("VERSION_CODE", "" + mPackageInfo.versionCode);  
        
        map.put("MODEL", "" + Build.MODEL);  
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);  
        map.put("RELEASE", "" + Build.VERSION.RELEASE);
        map.put("PRODUCT", "" +  Build.PRODUCT);  
        map.put("DEVICE", "" +  Build.DEVICE);  
        map.put("MANUFACTURER", "" +  Build.MANUFACTURER);  
        map.put("BRAND", "" +  Build.BRAND);  
        map.put("HARDWARE", "" +  Build.HARDWARE);  
        
          
        return map;  
    }  
      
      
    /** 
     * 获取系统未捕捉的错误信息 
     * @param throwable 
     * @return 
     */  
    private String obtainExceptionInfo(Throwable throwable) {  
        StringWriter mStringWriter = new StringWriter();  
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);  
        throwable.printStackTrace(mPrintWriter);  
        mPrintWriter.close();  
          
        LogUtil.e(TAG, mStringWriter.toString());  
        return mStringWriter.toString();  
    }  
      
    /** 
     * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中 
     * @param context 
     * @param ex 
     * @return 
     */  
    private String savaInfoToSD(Context context, Throwable ex){  
        String fileName = null;  
        StringBuffer sb = new StringBuffer();  
          
        for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key).append(" = ").append(value).append("\n");  
        }    
          
        sb.append(obtainExceptionInfo(ex));  
          
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
            File dir = new File(SDCARD_ROOT + File.separator + FOLDER_NAME + File.separator);  
            if(! dir.exists()){  
                dir.mkdir();  
            }  
            try{  
                fileName = dir.toString() + File.separator + LogT.AppendLogTime() + FILE_NAME;  
                FileOutputStream fos = new FileOutputStream(fileName);  
                fos.write(sb.toString().getBytes());  
                fos.flush();  
                fos.close();  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
              
        }  
          
        return fileName;  
          
    }  
      
      
    /** 
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式 
     * @param milliseconds 
     * @return 
     */  
    private String paserTime(long milliseconds) {  
        System.setProperty("user.timezone", "Asia/Shanghai");  
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");  
        TimeZone.setDefault(tz);  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String times = format.format(new Date(milliseconds));  
          
        return times;  
    }  
}
