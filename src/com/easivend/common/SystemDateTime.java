package com.easivend.common;
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.IOException;  
import java.util.Calendar;    
import android.os.SystemClock;  
  
//设置系统时间
public class SystemDateTime {  
      
    static final String TAG = "SystemDateTime";   
      
    public static void setDateTime(int year, int month, int day, int hour, int minute) throws IOException, InterruptedException {  
  
        requestPermission();  
  
        Calendar c = Calendar.getInstance();  
  
        c.set(Calendar.YEAR, year);  
        c.set(Calendar.MONTH, month-1);  
        c.set(Calendar.DAY_OF_MONTH, day);  
        c.set(Calendar.HOUR_OF_DAY, hour);  
        c.set(Calendar.MINUTE, minute);  
          
          
        long when = c.getTimeInMillis();  
  
        if (when / 1000 < Integer.MAX_VALUE) {  
            SystemClock.setCurrentTimeMillis(when);  
        }  
  
        long now = Calendar.getInstance().getTimeInMillis();  
        //Log.d(TAG, "set tm="+when + ", now tm="+now);  
  
        if(now - when > 1000)  
            throw new IOException("failed to set Date.");   
          
    }  
  
    public static void setDate(int year, int month, int day) throws IOException, InterruptedException {  
  
        requestPermission();  
  
        Calendar c = Calendar.getInstance();  
  
        c.set(Calendar.YEAR, year);  
        c.set(Calendar.MONTH, month);  
        c.set(Calendar.DAY_OF_MONTH, day);  
        long when = c.getTimeInMillis();  
  
        if (when / 1000 < Integer.MAX_VALUE) {  
            SystemClock.setCurrentTimeMillis(when);  
        }  
  
        long now = Calendar.getInstance().getTimeInMillis();  
        //Log.d(TAG, "set tm="+when + ", now tm="+now);  
  
        if(now - when > 1000)  
            throw new IOException("failed to set Date.");  
    }  
  
    public static void setTime(int hour, int minute) throws IOException, InterruptedException {  
          
        requestPermission();  
  
        Calendar c = Calendar.getInstance();  
  
        c.set(Calendar.HOUR_OF_DAY, hour);  
        c.set(Calendar.MINUTE, minute);  
        long when = c.getTimeInMillis();  
  
        if (when / 1000 < Integer.MAX_VALUE) {  
            SystemClock.setCurrentTimeMillis(when);  
        }  
  
        long now = Calendar.getInstance().getTimeInMillis();  
        //Log.d(TAG, "set tm="+when + ", now tm="+now);  
  
        if(now - when > 1000)  
            throw new IOException("failed to set Time.");  
    }  
      
    static void requestPermission() throws InterruptedException, IOException {  
        createSuProcess("chmod 666 /dev/alarm").waitFor();  
    }  
      
    static Process createSuProcess() throws IOException  {  
        File rootUser = new File("/system/xbin/ru");  
        if(rootUser.exists()) {  
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());  
        } else {  
            return Runtime.getRuntime().exec("su");  
        }  
    }  
      
    static Process createSuProcess(String cmd) throws IOException {  
  
        DataOutputStream os = null;  
        Process process = createSuProcess();  
  
        try {  
            os = new DataOutputStream(process.getOutputStream());  
            os.writeBytes(cmd + "\n");  
            os.writeBytes("exit $?\n");  
        } finally {  
            if(os != null) {  
                try {  
                    os.close();  
                } catch (IOException e) {  
                }  
            }  
        }  
  
        return process;  
    }  
}  
