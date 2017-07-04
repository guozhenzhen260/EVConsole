package cn.yoc.unionPay.sdk;

import android.util.Log;

/**
 * Created by yoc on 2017/3/4.
 */
public class LogUtils {

    private String tag;
    private int level = DEBUG;
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARN = 3;
    private static final int ERROR = 4;


    public LogUtils(String name) {
        if (name != null && !"".equals(name)) {
            tag = name;
        }
        else {
            tag = "systemout";
        }
    }

    public static LogUtils getLogger(String name) {
        return new LogUtils(name);
    }


    public boolean isDebugEnabled() {
        return true;
    }

    public void debug(String s) {
        if (level <= DEBUG) {
            Log.d(tag, s);
        }

    }

    public void info(String s) {
        if (level <= INFO){
            Log.i(tag, s);
        }

    }

    public void warn(String s) {
        if (level <= WARN) {
            Log.w(tag, s);
        }
    }

    public void error(String s) {
        if (level <= ERROR) {
            Log.e(tag, s);
        }
    }




}
