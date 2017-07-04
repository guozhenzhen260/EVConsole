package cn.yoc.unionPay.sdk;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by yoc on 2016/7/27.
 */
public class DateUtils {

    public static String timeToString(long time,String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        try{

            String lastEditTime = simpleDateFormat.format(new Date(time));
            return lastEditTime;
        }catch (Exception e){
            return "";
        }
    }

    public static String timeToString(Object time,String pattern){
        if(time != null){
            return timeToString(Long.parseLong(time.toString()),pattern);
        }
        return "";
    }

    /**
     *
     * @param pattern  yyyy-MM-dd'T'HH:mm:ss
     * @return
     */
    public static String currentDateTime(String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        try{
            String lastEditTime = simpleDateFormat.format(new Date());
            return lastEditTime;
        }catch (Exception e){
            return "";
        }
    }

    public static String currentDateTime(){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        try{
            String lastEditTime = simpleDateFormat.format(new Date());
            return lastEditTime;
        }catch (Exception e){
            return "";
        }
    }


    public static String currentDateTimeString(){
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        try{
            String lastEditTime = simpleDateFormat.format(new Date());
            return lastEditTime;
        }catch (Exception e){
            return "";
        }
    }


    public static long stringToTime(String date,String pattern){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            long lastTime = simpleDateFormat.parse(date).getTime();
            return lastTime;
        }catch (Exception e){
            return 0;
        }
    }


    public static long stringToTime(Object date,String pattern){
        if(date == null){
            return 0;
        }

        return stringToTime(date.toString(),pattern);
    }


    public static String getRandom(){
        int a = (int) (Math.random() * 9000 + 1000);
        return String.valueOf(a);
    }


    public static String getRandom(int n){

        long a = (long)(Math.random()*9* Math.pow(10, n - 1)) + (long) Math.pow(10, n - 1);
        return String.valueOf(a);
    }


    public static String makeOrderNo(String clientNo){
        String id = "";
        if (clientNo != null && !clientNo.isEmpty()) {
            id = clientNo;
        }
        //生成订单号
        String orderNo = DateUtils.currentDateTime("yyMMddHHmmss")  + id;
        int len = orderNo.length();
        // System.out.println("长度:" + len);
        if(len >= 32){
            orderNo = orderNo.substring(0,32).toUpperCase();
        }
        else{
            orderNo = orderNo + "T" +  DateUtils.getRandom(31 - len);
            orderNo = orderNo.toUpperCase();
        }
        return orderNo;
    }



    public static Date toDate(String str1) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return df.parse(str1);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<String> getDistanceDaysList(String str1, String str2) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date one;
        Date two;
        Date start;
        long days=0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1 < time2) {
                diff = time2 - time1;
                start = one;
            } else {
                diff = time1 - time2;
                start = two;
            }
            days = diff / (1000 * 60 * 60 * 24);
            List<String> list = new ArrayList<String>();
            for (int i = 0;i <= days;i++) {
                list.add(df.format(new Date(start.getTime() + (i) * 24 * 60 * 60 * 1000)));
            }
            return  list;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 两个时间之间相差距离多少天
     * @param one 时间参数 1：
     * @param two 时间参数 2：
     * @return 相差天数
     */
    public static long getDistanceDays(String str1, String str2) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date one;
        Date two;
        long days=0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }


    public static void main(String[] args) {
        String date = "2017-01-02 12:12:11";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date1 = simpleDateFormat.parse(date);

            System.out.println(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
