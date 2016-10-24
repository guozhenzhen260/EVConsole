/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           ToolClass.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        工具类，这里面存放的主要是static函数，static成员，统一作为全局变量和全局函数用       
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/


package com.easivend.common;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.conn.ssl.SSLContexts;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.alipay.AlipayConfig;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.BusZhiAmount;
import com.easivend.app.maintain.HuodaoSet;
import com.easivend.app.maintain.ParamManager;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_logDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_log;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.XZip;
import com.easivend.weixing.WeiConfig;
import com.easivend.weixing.WeiConfigAPI;
import com.example.evconsole.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToolClass 
{
	public final static int VERBOSE=0;
	public final static int DEBUG=1;
	public final static int INFO=2;
	public final static int WARN=3;
	public final static int ERROR=4;
	public static String EV_DIR=null;//ev包的地址
	private static int bentcom_id=-1,com_id=-1,columncom_id=-1,extracom_id=-1;//串口id号
	private static String bentcom="",com="",columncom="",extracom="",cardcom="",printcom="",posip="",posipport="",columncom2="";//串口描述符
	private static int bill_err=0,coin_err=0;//纸币器，硬币器故障状态
	public static String vmc_no="";//本机编号
	public static Bitmap mark=null;//售完图片
	public static int goc=0;//是否使用出货确认板1是
	public static int extraComType=0;//1使用冰山机型，2使用展示位
	public static Map<Integer, Integer> huodaolist=null;//保存逻辑货道与物理货道的对应关系
	public static Map<Integer, Integer> elevatorlist=null;//保存升降机逻辑货道与物理货道的对应关系
	public static Map<Integer, Integer> huodaolist2=null;//保存副柜逻辑货道与物理货道的对应关系
	public static Map<Integer, Integer> elevatorlist2=null;//保存副柜升降机逻辑货道与物理货道的对应关系
	public static Map<String, String> selectlist=null;//保存选货按键id与商品id的对应关系
	public static int orientation=0;//使用横屏还是竖屏模式0横屏,1竖屏
	public static SSLSocketFactory ssl=null;//ssl网络加密
	public static Context context=null;//本应用context
	private static int ServerVer=1;//0旧的后台，1一期的后台
	public static String version="";//本机版本号
	public static boolean CLIENT_STATUS_SERVICE=true;//true本机可以使用,false本机暂停销售 
	public static boolean LAST_CHUHUO=false;//true上一笔出货成功,false上一笔出货失败
	
	public static boolean isLAST_CHUHUO() {
		return LAST_CHUHUO;
	}
	public static void setLAST_CHUHUO(boolean lAST_CHUHUO) {
		LAST_CHUHUO = lAST_CHUHUO;
	}
	//读取文件信息
	public static boolean  ReadSharedPreferencesAccess()
	{
		boolean isaccess=true;
		//文件是私有的
		SharedPreferences  user = context.getSharedPreferences("access_info",0);
		//读取
		isaccess=user.getBoolean("access",true);
		return isaccess;
	}
	//写入文件信息
	public static void  WriteSharedPreferences(boolean value)
	{
		//文件是私有的
		SharedPreferences  user = context.getSharedPreferences("access_info",0);
		//需要接口进行编辑
		SharedPreferences.Editor edit=user.edit();
		//设置
		edit.putBoolean("access", value);
		//提交更新
		edit.commit();
	}
		
	public static boolean isCLIENT_STATUS_SERVICE() {
		return CLIENT_STATUS_SERVICE;
	}

	public static void setCLIENT_STATUS_SERVICE(boolean cLIENT_STATUS_SERVICE) {
		CLIENT_STATUS_SERVICE = cLIENT_STATUS_SERVICE;
		WriteSharedPreferences(CLIENT_STATUS_SERVICE);
	}
	
	//判断如果本机暂停服务，不允许销售并提示
	public static boolean checkCLIENT_STATUS_SERVICE()
	{
		boolean check=isCLIENT_STATUS_SERVICE();
		if(check==false)
		{
			failToast("抱歉，本机暂停服务,请联系管理员！");
		}
		return check;
	}

	public static String getVersion() {
		String curVersion=null;
		int curVersionCode=0;
		 try {
	            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
	            		context.getPackageName(), 0);
	            curVersion = pInfo.versionName;
	            curVersionCode = pInfo.versionCode;
	        } catch (NameNotFoundException e) {
	            Log.e("update", e.getMessage());
	            curVersion = "1.1.1000";
	            curVersionCode = 111000;
	        }
		 version=(curVersion+curVersionCode).toString();
		return version;
	}

	public static int getServerVer() {
		return ServerVer;
	}

	public static void setServerVer(int serverVer) {
		ServerVer = serverVer;
	}

	public static String getBentcom() {
		return bentcom;
	}

	public static void setBentcom(String bentcom) {
		ToolClass.bentcom = bentcom;
	}

	public static String getCom() {
		return com;
	}

	public static void setCom(String com) {
		ToolClass.com = com;
	}

	public static String getColumncom() {
		return columncom;
	}

	public static void setColumncom(String columncom) {
		ToolClass.columncom = columncom;
	}
	
		
	public static String getColumncom2() {
		return columncom2;
	}
	public static void setColumncom2(String columncom2) {
		ToolClass.columncom2 = columncom2;
	}
	public static String getExtracom() {
		return extracom;
	}

	public static void setExtracom(String extracom) {
		ToolClass.extracom = extracom;
	}
		
	public static String getCardcom() {
		return cardcom;
	}

	public static void setCardcom(String cardcom) {
		ToolClass.cardcom = cardcom;
	}
	
	public static String getPrintcom() {
		return printcom;
	}
	public static void setPrintcom(String printcom) {
		ToolClass.printcom = printcom;
	}
	
	public static String getPosip() {
		return posip;
	}
	public static void setPosip(String posip) {
		ToolClass.posip = posip;
	}
	public static String getPosipport() {
		return posipport;
	}
	public static void setPosipport(String posipport) {
		ToolClass.posipport = posipport;
	}
	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		ToolClass.context = context;
	}

	public static String getEV_DIR() {
		return EV_DIR;
	}

	public static void setEV_DIR(String eV_DIR) {
		EV_DIR = eV_DIR;
	}

	public static SSLSocketFactory getSsl() {
		return ssl;
	}

	public static void setSsl(SSLSocketFactory ssl) {
		ToolClass.ssl = ssl;
	}

	public static int getOrientation() {
		return orientation;
	}

	public static void setOrientation(int orientation) {
		ToolClass.orientation = orientation;
	}

	public static Bitmap getMark() {
		return mark;
	}

	public static void setMark(Bitmap mark) {
		ToolClass.mark = mark;
	}
	
	public static void setExtraComType(Context context) 
	{
		//查找货道类型
		vmc_cabinetDAO cabinetDAO3 = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
	    if(cabinetDAO3.findUBoxData())
	    {
	    	extraComType=1;
	    }	
	}
			
	public static int getExtraComType() {
		return extraComType;
	}

	public static int getGoc() {
		return goc;
	}
	
	public static void setGoc(Context context) 
	{
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		ToolClass.goc = tb_inaccount.getIsbuyCar();
    	}
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<goc="+ToolClass.goc,"log.txt");	
	}

	public static String getVmc_no() {
		return vmc_no;
	}

	public static void setVmc_no(String vmc_no) {
		ToolClass.vmc_no = vmc_no;
	}

	public static int getBentcom_id() {
		return bentcom_id;
	}

	public static void setBentcom_id(int bentcom_id) {
		ToolClass.bentcom_id = bentcom_id;
	}
	
	public static int getColumncom_id() {
		return columncom_id;
	}

	public static void setColumncom_id(int columncom_id) {
		ToolClass.columncom_id = columncom_id;
	}

	public static int getCom_id() {
		return com_id;
	}

	public static void setCom_id(int com_id) {
		ToolClass.com_id = com_id;
	}
	
	public static int getExtracom_id() {
		return extracom_id;
	}

	public static void setExtracom_id(int extracom_id) {
		ToolClass.extracom_id = extracom_id;
	}

	//解析Map对象<String,Object>的数据集合
	public static Map<String, Object> getMapListgson(String jsonStr)
	{
		Map<String, Object> list=new HashMap<String,Object>();
		try {
			JSONObject object=new JSONObject(jsonStr);//{"EV_json":{"EV_type":"EV_STATE_RPT","state":2}}
			JSONObject perobj=object.getJSONObject("EV_json");//{"EV_type":"EV_STATE_RPT","state":2}
			Gson gson=new Gson();
			list=gson.fromJson(perobj.toString(), new TypeToken<Map<String, Object>>(){}.getType());
			//Log.i("EV_JNI",perobj.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	
	/**
     * 设置根目录文件
     */
    public static void SetDir() 
    {
    	final String SDCARD_DIR=File.separator+"sdcard"+File.separator+"ev";
    	final String NOSDCARD_DIR=File.separator+"ev";
    	File fileName=null;
    	String  sDir =null,str=null;
    	Map<String, String> list=null;
    	    	
        try {
        	  //首先判断sdcard是否插入
        	  String status = Environment.getExternalStorageState();
        	  if (status.equals(Environment.MEDIA_MOUNTED)) 
        	  {
        		 sDir = SDCARD_DIR;;
        	  } 
        	  else
        	  {
        		  sDir = NOSDCARD_DIR;
        	  }
        	  ToolClass.setEV_DIR(sDir); 
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//Log方法，用于打印log，和在文本文件中打印操作日志
	public static void Log(int info,String tag,String str,String filename)
	{
		String infotype="";
		switch(info)
		{
			case VERBOSE:
				infotype="VERBOSE";
				Log.v(tag,str);
				break;
			case DEBUG:
				infotype="DEBUG";
				Log.d(tag,str);
				break;
			case INFO:
				infotype="INFO";
				Log.i(tag,str);
				break;
			case WARN:
				infotype="WARN";
				Log.w(tag,str);
				break;
			case ERROR:
				infotype="ERROR";
				Log.e(tag,str);
				break;	
		}	
		infotype="("+infotype+"),["+tag+"] "+str;
		AppendLogFile(infotype,filename);
	}
	
	/**
     * 追加文件：使用FileWriter
     */
    public static void AppendLogFile(String content,String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "  
                + "HH:mm:ss:SSS"); //精确到毫秒 
        String datetime = tempDate.format(new java.util.Date()).toString();  
        String cont=datetime+content+"\r\n";
    	
        try {
        	 sDir = ToolClass.getEV_DIR()+File.separator+"logs";
        	
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	//如果不存在，则创建文件
        	if(!fileName.exists())
        	{  
    	      fileName.createNewFile(); 
    	    }  
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//            FileWriter writer = new FileWriter(fileName, true);
//            writer.write(cont);
//            writer.close();
        	Writer writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(fileName, true),Charset.forName("gbk")));
            writer.write(cont);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 操作整理日志：隔一天就重命名文件，半个月就清掉这个文件
     */
    public static void optLogFile() 
    {
    	String  sDir =null;
    	File fileName=null;
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "  
                + "HH:mm:ss"); //精确到毫秒 
    	
    	//当前时间
//        String datetime = tempDate.format(new java.util.Date()).toString();  
//        ParsePosition pos = new ParsePosition(0);  
//    	Date d1 = (Date) tempDate.parse(datetime, pos); 
//    	ToolClass.Log(ToolClass.INFO,"EV_DOG","当前时间="+datetime+",="+d1.getTime(),"dog.txt");
    	 
    	//起始时间
        Calendar todayStart = Calendar.getInstance();  
        todayStart.set(Calendar.HOUR_OF_DAY, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0); 
        Date date = todayStart.getTime(); 
        String starttime=tempDate.format(date);
        ParsePosition posstart = new ParsePosition(0);  
    	Date dstart = (Date) tempDate.parse(starttime, posstart);
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","起始时间="+starttime+",="+dstart.getTime(),"dog.txt");
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"logs";
        	  
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	        	
        	//2.如果存在log文件，则判断
        	fileName=new File(sDir+File.separator+"log.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
				Date dlog = (Date) tempDate.parse(logdatetime, poslog);
				ToolClass.Log(ToolClass.INFO,"EV_DOG","判断重命名文件log.txt时间="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		//判断是否文件早于今天
            	if(dlog.getTime()<=dstart.getTime())
            	{
            		ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件log重分割","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
            	else
            	{
            		ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件log排除","dog.txt"); 
            	}
    	    }
        	//3.如果存在dog文件，则判断
        	fileName=new File(sDir+File.separator+"dog.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
        		ToolClass.Log(ToolClass.INFO,"EV_DOG","判断重命名文件dog.txt时间="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		
        		//判断是否文件早于今天
        		if(dlog.getTime()<=dstart.getTime())
            	{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件dog重分割","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
        		else
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件dog排除","dog.txt"); 
        		}
    	    } 
        	//4.如果存在server文件，则判断
        	fileName=new File(sDir+File.separator+"server.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
        		ToolClass.Log(ToolClass.INFO,"EV_DOG","判断重命名文件server.txt时间="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		//判断是否文件早于今天
        		if(dlog.getTime()<=dstart.getTime())
            	{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件server重分割","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
        		else
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件server排除","dog.txt"); 
        		}
    	    }
        	//5.如果存在com文件，则判断
        	fileName=new File(sDir+File.separator+"com.txt"); 
        	if(fileName.exists())
        	{  
        		String logdatetime = getFileCreated(fileName);
        		ParsePosition poslog = new ParsePosition(0);  
        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
        		ToolClass.Log(ToolClass.INFO,"EV_DOG","判断重命名文件com.txt时间="+logdatetime+",="+dlog.getTime(),"dog.txt");
        		//判断是否文件早于今天
        		if(dlog.getTime()<=dstart.getTime())
            	{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件com重分割","dog.txt"); 
            		updatefile(fileName,sDir);
            	}
        		else
        		{
        			ToolClass.Log(ToolClass.INFO,"EV_DOG"," 文件com排除","dog.txt"); 
        		}
    	    }
        	//6.将目录下的所有文件，如果有超出半个月的，全部删除
        	delFiles(dirName);
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//获取文件创建时间
    public static String getFileCreated(final File file)  
    {  
		 String res=null;
		 Scanner scan = null ;
		 try{
		 	scan = new Scanner(file) ;	// 从文件接收数据
		 	if(scan.hasNext())
		 	{
		 		res=scan.next()+" "+scan.next();	//	取数据		 		
		 	}
		 	
		 }catch(Exception e)
		 {
			
		 }
		 res=res.substring(0, res.indexOf("(INFO)"));// 从收入信息中截取收入编号
		 System.out.println(" 文件创建时间1="+res);
         return res;
    }
	
	 
	 
	//重命名文件名fileName原文件名,sDir是目录
    public static void updatefile(File fileName,String  sDir)
	{
		SimpleDateFormat tempDate2 = new SimpleDateFormat("yyyy-MM-dd-HHmmss"); //精确到毫秒 
        String datetime2 = tempDate2.format(new java.util.Date()).toString();
        String oldname=fileName.getName();
		String newname=datetime2+oldname;
		System.out.println(fileName+" 修改文件操作="+newname);            		
		fileName.renameTo(new File(sDir+File.separator+newname));
	}
    
    //java设定一个日期时间，加几分钟（小时或者天）后得到新的日期
    //返回的是字符串型的时间，
    //输入的是String day基准时间, int x天数
    public static String addDateMinut(String day, int x)
    {   
    	// 24小时制  
    	//引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
    	//量day格式一致
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;   
        try {   
            date = format.parse(day);   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        if (date == null)   
            return "";   
        System.out.println("front:" + format.format(date)); //显示输入的日期  
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   //得到基准时间
        
        cal.add(Calendar.DATE, x);// 天 
        date = cal.getTime();   
        System.out.println("after:" + format.format(date));  //显示更新后的日期 
        cal = null;   
        return format.format(date);   
  
    } 
    /**
     * 递归删除productImage文件和文件夹
     * @param file    要删除的根目录
     */
    public static void deleteproductImageFile()
    {
    	String  sDir =null;
    	 try {
    		  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllZIPFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 递归删除ads,adshuo文件和文件夹
     * @param file    要删除的根目录
     */
    public static void deleteadsImageFile()
    {
    	String  sDir =null;
    	 try {
    		 //删除广告目录
    		  sDir = ToolClass.getEV_DIR()+File.separator+"ads";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllZIPFile(dirName);
        	 
        	//删除弹窗广告目录
	   		sDir = ToolClass.getEV_DIR()+File.separator+"adshuo";
	       	dirName = new File(sDir);
	       	//如果目录不存在，则创建目录
	       	if (!dirName.exists()) 
	       	{  
	            //按照指定的路径创建文件夹  
	       		dirName.mkdirs(); 
	        }
	       	 
	       	deleteAllZIPFile(dirName);
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 递归删除ZIP文件和文件夹
     * @param file    要删除的根目录
     */
    private static void deleteZIPFile()
    {
    	String  sDir =null;
    	 try {
        	  sDir = ToolClass.ReadLogFile()+"ZIPFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllZIPFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void deleteAllZIPFile(File file)
    {        
        if(file.isDirectory())
        {
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                return;
            }
            for(File f : childFile)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","删除log="+f.toString(),"server.txt");										
                f.delete();
            }
        }
    }
     /**  
     * 复制单个文件  
     * @param oldPath String 原文件路径 如：c:/fqf.txt  
     * @param newPath String 复制后路径 如：f:/fqf.txt  
     * @return boolean  
     */   
	   private static void copyFile(String oldPath, String newPath) 
	   {   
	       try {   
	           int bytesum = 0;   
	           int byteread = 0;   
	           File oldfile = new File(oldPath);   
	           if (oldfile.exists()) { //文件存在时   
	               InputStream inStream = new FileInputStream(oldPath); //读入原文件   
	               FileOutputStream fs = new FileOutputStream(newPath);   
	               byte[] buffer = new byte[1444];   
	               int length;   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   bytesum += byteread; //字节数 文件大小   
	                   System.out.println(bytesum);   
	                   fs.write(buffer, 0, byteread);   
	               }   
	               inStream.close();   
	           }   
	       }   
	       catch (Exception e) {   
	           System.out.println("复制单个文件操作出错");   
	           e.printStackTrace();   
	  
	       }   
	  
	   }  
	   /**
	     * zipLogFiles压缩需要上传的日志包
	     * @param 
	     */
	    private static String zipLogFiles(String srcFileString) 
	  	{  
	  		//遍历这个文件夹里的所有文件
	  		String zipFileString=ToolClass.getEV_DIR()+File.separator+"logzip.zip";
	  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<srcFileString="+srcFileString+" zipFileString="+zipFileString,"log.txt"); 
	  		try {
	  			XZip.ZipFolder(srcFileString, zipFileString);
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  		return zipFileString;
	  	}   
    /**
     * 判断与当前时间差距多久,createtime是文件创建时间,datetime是当前时间
     * 传入的时间格式必须类似于2012-8-21 17:53:20这样的格式  
     * 返回值：1秒，2分，3时，4天，5半个月
     */
    public static String logFileInterval(String starttime,String endtime)
    {
    	boolean inter=false;//true表示需要压缩数据包
        String  sDir =null,zipDir=null,zipFileString=null;
    	File fileName=null;
    	
        //1.设置起始时间和结束时间
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        
        ParsePosition pos = new ParsePosition(0);  
        Date d1 = (Date) sd.parse(starttime, pos); 
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","起始时间="+starttime+",="+d1.getTime(),"server.txt");
        
        endtime=addDateMinut(endtime,1);
        ParsePosition posnow = new ParsePosition(0);  
        Date dnow = (Date) sd.parse(endtime, posnow);
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","结束时间="+endtime+",="+dnow.getTime(),"server.txt");
        //2.整理压缩目标目录 
        deleteZIPFile(); 	
        zipDir=ToolClass.ReadLogFile()+"ZIPFile"+File.separator;
        //3.遍历log文件，判断是否是在这个时间之内
        try {
        	 sDir = ToolClass.getEV_DIR()+File.separator+"logs";
  		  
	  		 File dirName = new File(sDir);
	  		 //如果目录不存在，则创建目录
	  		 if (!dirName.exists()) 
	  		 {  
	  			//按照指定的路径创建文件夹  
	  			dirName.mkdirs(); 
	  		 }   
	  		//遍历这个文件夹里的所有文件
	   		File[] files = dirName.listFiles();
	   		if (files.length > 0) 
	   		{  
	   			for (int i = 0; i < files.length; i++) 
	   			{
	   			  if(!files[i].isDirectory())
	   			  {		
	   				    String filestr=files[i].toString();
	   				  	ToolClass.Log(ToolClass.INFO,"EV_SERVER"," 判断日志目录内文件="+filestr,"server.txt"); 
	   				    //排除dog类文件
	   				  	int attimg2=filestr.lastIndexOf("dog.txt");
		   		        if(attimg2==-1)
		   		        {
		   		        	fileName=new File(files[i].toString()); 
		   		        	if(fileName.exists())
		   		        	{ 
		   		        		try
				        		{
			   		        		String logdatetime = getFileCreated(fileName);
			   		        		ParsePosition poslog = new ParsePosition(0);  
			   		        		Date dlog = (Date) sd.parse(logdatetime, poslog);
			   		        		ToolClass.Log(ToolClass.INFO,"EV_SERVER","文件时间="+logdatetime+",="+dlog.getTime(),"server.txt");
			   		            	if((d1.getTime()<=dlog.getTime())&&(dlog.getTime()<=dnow.getTime()))
			   		            	{
			   		            		//4.拷贝文件到压缩目录中
			   		            		String a[] = files[i].toString().split("/");  
			   		            		String ATT_ID=a[a.length-1];  
			   		            		String ZIPFile=zipDir+ATT_ID;
			   		            		ToolClass.Log(ToolClass.INFO,"EV_SERVER"," 文件"+files[i].toString()+"选定,zip="+ZIPFile,"server.txt"); 
			   		            		copyFile(files[i].toString(),ZIPFile);
			   		            		inter=true;
			   		            	}
			   		            	else
			   		            	{
			   		            		ToolClass.Log(ToolClass.INFO,"EV_SERVER"," 文件"+files[i].toString()+"排除","server.txt"); 
			   		            	}
				        		}
				        		catch(Exception e)
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_SERVER","文件="+files[i].toString()+"异常，无法判断","server.txt");
				        		}	
		   		    	    } 
		   		        }
	   			  }
	   			}
	   		}
	   		//5.压缩数据包
	   		if(inter)
	   		{
	   			zipFileString=zipLogFiles(zipDir);
	   		}
	     } catch (Exception e) {
			e.printStackTrace();
		 } 
        return zipFileString;
    }
	
	 /* 遍历目录内文件列表， file是目录名，datetime是当前时间，如果超过半个月，就删除掉这个文件
	  * */  
    public static void delFiles(File file) 
    {  
    	//1.设置起始时间和结束时间
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    	
    	//本周起始时间
        Calendar todayStart = Calendar.getInstance(); 
        todayStart.setFirstDayOfWeek(Calendar.MONDAY);  
        todayStart.set(Calendar.HOUR_OF_DAY, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0); 
        todayStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
        //再推前两周
        todayStart.add(Calendar.WEEK_OF_YEAR, -2);
        Date date = todayStart.getTime(); 
        String starttime=tempDate.format(date);
        ParsePosition posstart = new ParsePosition(0);  
    	Date dstart = (Date) tempDate.parse(starttime, posstart);
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","保存日志的起始时间="+starttime+",="+dstart.getTime(),"dog.txt");
    	ToolClass.Log(ToolClass.INFO,"EV_DOG","目录="+file.toString(),"dog.txt");
    	//遍历这个文件夹里的所有文件
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {		
				    //3.如果存在文件，则判断
		        	File fileName=new File(files[i].toString()); 
		        	if(fileName.exists())
		        	{  
		        		try
		        		{
			        		String logdatetime = getFileCreated(fileName);
			        		ParsePosition poslog = new ParsePosition(0);  
			        		Date dlog = (Date) tempDate.parse(logdatetime, poslog);
			        		ToolClass.Log(ToolClass.INFO,"EV_DOG","判断日志目录内文件="+files[i].toString()+"时间="+logdatetime+",="+dlog.getTime(),"dog.txt");
			        		//判断是否文件早于本周
			        		if(dlog.getTime()<=dstart.getTime())
			            	{
			        			ToolClass.Log(ToolClass.INFO,"EV_DOG","文件="+files[i].toString()+"删除","dog.txt");
			            		fileName.delete();		            		
			            	}
			        		else
			        		{
			        			ToolClass.Log(ToolClass.INFO,"EV_DOG","文件="+files[i].toString()+"排除","dog.txt");
			        		}
		        		}
		        		catch(Exception e)
		        		{
		        			ToolClass.Log(ToolClass.INFO,"EV_DOG","文件="+files[i].toString()+"异常，无法判断","dog.txt");
		        		}
		    	    } 
			  }
			}
		}    
    }
    
    /**
     * 递归删除certzip文件和文件夹
     * @param file    要删除的根目录
     */
    public static void deleteCertFile()
    {
    	String  sDir =null;
    	 try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"CertFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 递归删除cert文件和文件夹
     * @param file    要删除的根目录
     */
    public static void deleteCertFolder()
    {
    	String  sDir =null;
    	 try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"cert";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 使用setCertFile,保存这个程序到目录中
     */
    public static File setCertFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"CertFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
    /**
     * 使用isAPKFile,判断这个程序是已经存在目录中,true存在,false不存在
     */
    public static boolean isAPKFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"APKFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	//如果不存在，则创建文件
        	if(!fileName.exists())
        	{  
        		fileext=false; 
    	    }  
        	else
        		fileext=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileext; 
    }
    /**
     * 使用isAPKFile,保存这个程序到目录中
     */
    public static File setAPKFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"APKFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    /**
     * 递归删除APK文件和文件夹
     * @param file    要删除的根目录
     */
    public static void deleteAPKFile()
    {
    	String  sDir =null;
    	 try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"APKFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 deleteAllFile(dirName);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void deleteAllFile(File file)
    {        
        if(file.isDirectory())
        {
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                return;
            }
            for(File f : childFile)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","删除程序="+f.toString(),"server.txt");										
                f.delete();
            }
        }
    }
    
    /**
     * zipFiles压缩日志包
     * @param 
     */
    public static String zipFiles() 
  	{  
  		//遍历这个文件夹里的所有文件
  		String srcFileString=ToolClass.ReadLogFile();
  		String zipFileString=ToolClass.getEV_DIR()+File.separator+"logzip.zip";
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<srcFileString="+srcFileString+" zipFileString="+zipFileString,"log.txt"); 
  		try {
  			XZip.ZipFolder(srcFileString, zipFileString);
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		return zipFileString;
  	}
    
     
    /**
     * 使用isImgFile,判断这个商品图片是已经存在目录中,true存在,false不存在
     */
    public static boolean isImgFile(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+".jpg");         	
        	//如果不存在，则创建文件
        	if(!fileName.exists())
        	{  
        		fileext=false; 
    	    }  
        	else
        		fileext=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileext; 
    }
            
    //将Bitmap图片保存在本地
    public static boolean  saveBitmaptofile(Bitmap bmp,String filename)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+".jpg");         	
        	//如果不存在，则开始保存图片
        	if(!fileName.exists())
        	{  
        		//1.保存原版图片
        		CompressFormat format= Bitmap.CompressFormat.JPEG;  
    	        int quality = 100;  
    	        OutputStream stream = null;  
    	        stream = new FileOutputStream(fileName);     	        
    	        fileext=bmp.compress(format, quality, stream); 
    	        //2.压缩裁剪图片
 	    	   //如果我们把它设为true，那么BitmapFactory.decodeFile(String path, Options opt)并不会真的返回一个Bitmap给你，
 	    	   //它仅仅会把它的宽，高取回来给你，这样就不会占用太多的内存
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
                Bitmap bmptmp = BitmapFactory.decodeFile(fileName.toString(), options);
                //按比例收缩和压缩他的值，这样可以减低内存使用
                // 缩放的比例，缩放是很难按准备的比例进行缩放的，其值表明缩放的倍数，
                //SDK中建议其值是2的指数值,值越大会导致图片不清晰 
                int inSampleSize = options.outWidth / 350;
                options.inSampleSize = inSampleSize; 
                //在图片不变形的情况下获取到图片指定大小的缩略图呢
                //那么我们需要先计算一下缩放之后，图片的高度是多少,就能显示这么大的长和宽的图片
                int height = options.outHeight * 350 / options.outWidth;
                options.outWidth = 350;
                options.outHeight = height;              
                //为了节约内存我们还可以使用下面的几个字段
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;// 默认是Bitmap.Config.ARGB_8888
                /* 下面两个字段需要组合使用 */
                options.inPurgeable = true;
                options.inInputShareable = true;
                /* 这样才能真正的返回一个Bitmap给你 */
                options.inJustDecodeBounds = false;
                Bitmap bmp2 = BitmapFactory.decodeFile(fileName.toString(), options);
                //3.保存裁剪版图片
        		stream = new FileOutputStream(fileName);     	        
    	        fileext=bmp2.compress(format, quality, stream); 
    	        stream.close();
    	    }  
        	else
        		fileext=false;
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     } 
    
    //将商品详细信息图片保存在本地
    public static boolean  saveBitproductmaptofile(Bitmap bmp,String filename)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
      	  File dirName = new File(sDir);
      	 //如果目录不存在，则创建目录
      	 if (!dirName.exists()) 
      	 {  
              //按照指定的路径创建文件夹  
      		dirName.mkdirs(); 
           }
      	 
      	 fileName=new File(sDir+File.separator+filename+".jpg");         	
        	//如果不存在，则开始保存图片
        	if(!fileName.exists())
        	{  
        		CompressFormat format= Bitmap.CompressFormat.JPEG;  
    	        int quality = 100;  
    	        OutputStream stream = null;  
    	        stream = new FileOutputStream(fileName);      	         
    	        fileext=bmp.compress(format, quality, stream); 
    	    }  
        	else
        		fileext=false;
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     } 
    
    /**
     * 使用getImgFile,得到这个商品图片的完整目录
     */
    public static String getImgFile(String filename) 
    {
    	String  sDir =null;
    	String fileName=null;
    	try {
    		  sDir = ToolClass.getEV_DIR()+File.separator+"productImage";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	fileName=sDir+File.separator+filename+".jpg";  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
  //获取一个字符串中图片链接地址列表传入一个字符串获取其中全部图片链接地址add到Vector<String>中返回
    public static Vector<String> parseImageURL(String text)
  	{
  		  text = text.replaceAll("< *(?i)img ", "\n<img ");
  		  String[] list = text.split("\n");
  		  if (list == null || list.length == 0)
  			  return null;
  		  Vector<String> imageList = new java.util.Vector<String>();
  		  String image;
  		  for (String line : list) 
  		  {
  			   line = line.trim();
  			   if (!line.startsWith("<img "))
  				   continue;
  			   image = line.replaceFirst("<img .*?(?i)src=\"(.*?)\".*", "$1");
  			   if (image.equals(line))
  				   continue;
  			   imageList.add(image);
  		  }
  		  return imageList.size() == 0 ? null : imageList;
  	 }
  	
  	//获取图片名称(如：传入http://xxx/xx/photo.jpg 则返回photo.jpg)
    public static String getImageName(String text) 
  	{
  		String[] urlArray = text.split("/");
  		String ATT_ID=urlArray[urlArray.length - 1];
  		ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","图片ATT_ID="+ATT_ID,"server.txt");	
  		return ATT_ID;
  	}
  	
  	//通过前面的2个方法 可以把一个字符串中的所有图片名字获得到 之后调用下面的方法把希望从本地加载的图片地址修改成指定目录
  	//附正则方法 传入一个字符串 传入图片名称（如photo.jpg) 此方法会把这段文字中名字为photo.jpg的图片加载地址，从网上地址替换成本地地址(SD卡根目录)，之后返回替换完的字符串
  	//此格式为从网上加载图片的格式
  	//把<img src = http://xxx/xxx/xxx/a.jpg/>格式
  	//此格式为从本地加载图片的格式此目录为SD卡根目录(建议使用Environment.getExternalStorageDirectory()方法获取SD卡根目录)
  	//改成<img src = file:///sdcard/a.jpg/>格式
    public static String repImageURL(String text, String filename) {
  		String pattern = "<img src=\"" + filename + "\"";
  		String files=getImageName(filename);
  		files=ToolClass.getImgFile(files);
  	  return text.replaceAll(pattern, "<img src=\"file://"+files+"\"");
  	 }
    
    /**
     * 读取出货时广告文件
     */
    public static Bitmap ReadAdshuoFile() 
    {
    	String  sDir =null;
    	Bitmap bitmap=null;
    	sDir = ToolClass.getEV_DIR()+File.separator+"adshuo"+File.separator;
    	File dirName = new File(sDir);
	   	//如果目录不存在，则创建目录
	   	if (!dirName.exists()) 
	   	{  
          //按照指定的路径创建文件夹  
   		  dirName.mkdirs(); 
        }
	    //遍历这个文件夹里的所有文件
  		File file = new File(sDir);
  		File[] files = file.listFiles();
  		if (files.length > 0)
  		{
  			//初始化广告列表
  			List<String> mHuoList =  new ArrayList<String>(); //保存出货页面广告列表
  			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {	
				    try
	        		{
					  //是否图片文件
					  if(MediaFileAdapter.isImgFileType(files[i].toString())==true)
					  {
						  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道广告ID="+files[i].toString(),"log.txt");
						  mHuoList.add(files[i].toString());
					  }
	        		}
				    catch(Exception e)
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_JNI","文件="+files[i].toString()+"异常，无法判断","log.txt");
	        		}
			  }
			}  
  			//选择需要显示的广告
  			if(mHuoList.size()>0)
  			{
  				Random r=new Random(); 
  				int curIndex = r.nextInt(mHuoList.size()); 
  				/*为什么图片一定要转化为 Bitmap格式的！！ */
		        bitmap = ToolClass.getLoacalBitmap(mHuoList.get(curIndex)); //从本地取图片(在cdcard中获取)  //
		        
  			}
  		}
    	return bitmap;
    }
    
    /**
     * 读取广告文件
     */
    public static String ReadAdsFile() 
    {
    	String  sDir =null;
    	sDir = ToolClass.getEV_DIR()+File.separator+"ads"+File.separator;
    	File dirName = new File(sDir);
	   	//如果目录不存在，则创建目录
	   	if (!dirName.exists()) 
	   	{  
          //按照指定的路径创建文件夹  
   		  dirName.mkdirs(); 
        }
    	return sDir;
    }
    
    /**
     * 使用isAdsFile,判断这个广告是已经存在目录中,true存在,false不存在
     */
    public static boolean isAdsFile(String filename,String TypeStr,String ads) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);         	
        	//如果不存在，则创建文件
        	if(!fileName.exists())
        	{  
        		fileext=false; 
    	    }  
        	else
        		fileext=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileext; 
    }
    
    //将Bitmap图片保存在本地
    public static boolean  saveBitmaptoads(Bitmap bmp,String TypeStr,String filename,String ads)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);         	
        	//如果不存在，则开始保存图片
        	if(!fileName.exists())
        	{  
        		CompressFormat format= Bitmap.CompressFormat.JPEG;  
    	        int quality = 100;  
    	        OutputStream stream = null;  
    	        stream = new FileOutputStream(fileName);      	         
    	        fileext=bmp.compress(format, quality, stream); 
    	    }  
        	else
        		fileext=false;
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     } 
    
    /**
     * 使用savetoCert,保存这个cert到目录中
     */
    public static File savetoCert(String filename) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+"CertFile";
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
    /**
     * 使用saveAvitoads,保存这个视频广告到目录中
     */
    public static File saveAvitoads(String filename,String TypeStr,String ads) 
    {
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);         	
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName; 
    }
    
    //将广告文件删除
    public static boolean  delAds(String filename,String TypeStr,String ads)
    {      	
    	String  sDir =null;
    	File fileName=null;
    	boolean fileext=false;
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+ads;
        	  File dirName = new File(sDir);
        	 //如果目录不存在，则创建目录
        	 if (!dirName.exists()) 
        	 {  
                //按照指定的路径创建文件夹  
        		dirName.mkdirs(); 
             }
        	 
        	 fileName=new File(sDir+File.separator+filename+"."+TypeStr);   
        	 //如果存在，删除
        	 if(fileName.exists())
        	{  
        		 fileName.delete();	
    	    } 
        } catch (Exception e) {
            e.printStackTrace();
        }   
       return fileext; 
     }
    
    /**
     * 读取日志文件
     */
    public static String ReadLogFile() 
    {
    	String  sDir =null;
    	sDir = ToolClass.getEV_DIR()+File.separator+"logs"+File.separator;
    	return sDir;
    }
    
    /**
     * 检测后，如果没有导入成功支付宝或者微信的账号信息，重新导入一次
     */
    public static void CheckAliWeiFile()
    {
    	if(
    	  (AlipayConfig.getPartner()==null)||(AlipayConfig.getSeller_email()==null)
    	  ||(AlipayConfig.getKey()==null)
    	  ||(WeiConfig.getWeiappid()==null)||(WeiConfig.getWeimch_id()==null)
    	  ||(WeiConfig.getWeikey()==null)
    	  )
    	{
	    	//从配置文件获取数据
			Map<String, String> list=ReadConfigFile();
			if(list!=null)
			{
		        AlipayConfigAPI.SetAliConfig(list);//设置阿里账号
		        WeiConfigAPI.SetWeiConfig(list);//设置微信账号	        
			}
			//加载微信证书
			setWeiCertFile();
    	}
    }
    
    /**
     * 读取配置文件
     */
    public static Map<String, String> ReadConfigFile() 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	Map<String, String> list=null;
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"easivendconfig.txt";
        	  fileName=new File(sDir);
        	  //如果存在，才读文件
        	  if(fileName.exists())
        	  {
	    	  	 //打开文件
	    		  FileInputStream input = new FileInputStream(sDir);
	    		 //输出信息
	  	          Scanner scan=new Scanner(input);
	  	          while(scan.hasNext())
	  	          {
	  	           	str=scan.next()+"\n";
	  	          }
	  	         ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config="+str,"log.txt");
	  	         //文件损坏,则从备份那边覆盖一份过来
	  	         if(str==null)
	  	         {
	  	        	 //恢复文件
	  	        	String copyDir = ToolClass.getEV_DIR()+File.separator+"CONFIG"+File.separator;
	  	        	String copyFile = copyDir+"easivendconfig.txt";
	  	  		    ToolClass.Log(ToolClass.INFO,"EV_JNI"," 文件"+sDir+"损坏,恢复备份="+copyFile,"log.txt");
	  	  		    copyFile(copyFile,sDir);
	  	  		    
	  	  		    //重新读入文件信息
	  	  		    //打开文件
		    		  FileInputStream input2 = new FileInputStream(sDir);
		    		 //输出信息
		  	          Scanner scan2=new Scanner(input2);
		  	          while(scan2.hasNext())
		  	          {
		  	           	str=scan2.next()+"\n";
		  	          }
		  	         ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config恢复备份="+str,"log.txt");
	  	         }
	  	         //将json格式解包
	  	         list=new HashMap<String,String>();      			
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String, Object>>(){}.getType());
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config2="+list.toString(),"log.txt");
        	  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 写入配置文件
     */
    public static void WriteConfigFile(String com,String bentcom,String columncom,String extracom,String cardcom,String printcom,String columncom2,
    		String posip,String posipport) 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"easivendconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      } 
        	  
          	  //1.将数据从文件中读入
    	  	  //打开文件
    		  FileInputStream input = new FileInputStream(sDir);
    		  //输出信息
  	          Scanner scan=new Scanner(input);
  	          while(scan.hasNext())
  	          {
  	           	str=scan.next()+"\n";
  	          }
  	         ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config="+str,"log.txt");
  	         if(str!=null)
  	         {
	  	        Map<String, String> list=new HashMap<String,String>();      			
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String, Object>>(){}.getType());
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config2="+list.toString(),"log.txt");
				Map<String,String> list2=new HashMap<String,String>();
				//输出内容
		        Set<Map.Entry<String,String>> allset=list.entrySet();  //实例化
		        Iterator<Map.Entry<String,String>> iter=allset.iterator();
		        while(iter.hasNext())
		        {
		            Map.Entry<String,String> me=iter.next();
		            if(
		            		(me.getKey().equals("com")!=true)
		            	  &&(me.getKey().equals("bentcom")!=true)
		            	  &&(me.getKey().equals("columncom")!=true)
		            	  &&(me.getKey().equals("extracom")!=true)
		            	  &&(me.getKey().equals("cardcom")!=true)
		            	  &&(me.getKey().equals("printcom")!=true)
		            	  &&(me.getKey().equals("isallopen")!=true)
		            	  &&(me.getKey().equals("server")!=true)
		            	  &&(me.getKey().equals("posip")!=true)
		            	  &&(me.getKey().equals("posipport")!=true)
		              )
		            	list2.put(me.getKey(), me.getValue());
		            	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config3="+me.getKey()+"--"+me.getValue());
		        } 	
		        list2.put("com", com);
		        list2.put("bentcom", bentcom);
		        list2.put("columncom", columncom);
		        list2.put("extracom", extracom);
		        list2.put("cardcom", cardcom);
		        list2.put("printcom", printcom);
		        list2.put("isallopen", columncom2);	
		        list2.put("posip", posip);
		        list2.put("posipport", posipport);
		        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config3="+list2.toString(),"log.txt");
		        JSONObject jsonObject = new JSONObject(list2);
		        String mapstrString=jsonObject.toString();
		        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config4="+mapstrString,"log.txt");
		        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
	            FileWriter writer = new FileWriter(fileName);
	            writer.write(mapstrString);
	            writer.close();
  	         }
  	         else
  	         {
  	        	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<com="+com+","+bentcom);
  	        	JSONObject jsonObject = new JSONObject();
  	        	jsonObject.put("com", com);
  	        	jsonObject.put("bentcom", bentcom);
  	        	jsonObject.put("columncom", columncom);
  	        	jsonObject.put("extracom", extracom);
  	        	jsonObject.put("cardcom", cardcom);
  	        	jsonObject.put("printcom", printcom);
  	        	jsonObject.put("isallopen", columncom2);
  	        	jsonObject.put("posip", posip);
  	        	jsonObject.put("posipport", posipport);
  	        	String mapstrString=jsonObject.toString();
  	        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config2="+mapstrString,"log.txt");
  	            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
  	            FileWriter writer = new FileWriter(fileName, true);
  	            writer.write(mapstrString);
  	            writer.close();
			 }
//  	         //将json格式解包
//  	         list=new HashMap<String,String>();      			
//			JSONObject object=new JSONObject(str);      				
//			Gson gson=new Gson();
//			list=gson.fromJson(object.toString(), new TypeToken<Map<String, Object>>(){}.getType());
//			//Log.i("EV_JNI",perobj.toString());
//			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config2="+list.toString());
        	//2.作文件备份
  	        ResetConfigFile();             
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * 加载微信证书文件
     */
    public static void setWeiCertFile() 
    {
    	File fileName=null;
    	String  sDir =null,str=null,mch_id=null;
    	
    	mch_id=WeiConfig.getWeimch_id();
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"cert"+File.separator+"apiclient_cert.p12";
        	 
        	 
        	  fileName=new File(sDir);
        	  //如果存在，才读文件
        	  if(fileName.exists())
        	  {
        		//指定读取证书格式为PKCS12
    	    	KeyStore keyStore = KeyStore.getInstance("PKCS12");
    	    	//读取本机存放的PKCS12证书文件
    	    	FileInputStream instream = new FileInputStream(sDir);
    	    	try 
    	    	{
    	    		//指定PKCS12的密码(商户ID)
    	    		keyStore.load(instream, mch_id.toCharArray());
	    		} 
    	    	finally 
    	    	{
    	    		instream.close();
	    		}	
    	    	SSLContext sslcontext = SSLContexts.custom()
    	        		.loadKeyMaterial(keyStore, mch_id.toCharArray()).build();
    	    	ToolClass.ssl=sslcontext.getSocketFactory(); 
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<havessl,mch_id="+mch_id,"log.txt");
        	  }
        	  else 
        	  {
        		  ToolClass.ssl=null;
        		  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<nossl","log.txt");
			  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
            ToolClass.ssl=null;
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<sslerror","log.txt");
        }
        
    }
    
    /**
     * 重新更新支付宝微信文件,后台服务器下发用
     */
    public static void ResetConfigFileServer(JSONObject object2,String VMC_NO) 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	
    	    	
        try {      
        	  //1.从服务器中下载的信息修改配置文件
        	  sDir = ToolClass.getEV_DIR()+File.separator+"easivendconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      } 
        	  
          	  //1.将数据从文件中读入
    	  	  //打开文件
    		  FileInputStream input = new FileInputStream(sDir);
    		  //输出信息
  	          Scanner scan=new Scanner(input);
  	          while(scan.hasNext())
  	          {
  	           	str=scan.next()+"\n";
  	          }
  	         ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<config="+str,"server.txt");
  	         if(str!=null)
  	         {
	  	        Map<String, String> list=new HashMap<String,String>();      			
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String, Object>>(){}.getType());
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<config2="+list.toString(),"server.txt");
				Map<String,String> list2=new HashMap<String,String>();
				//输出内容
		        Set<Map.Entry<String,String>> allset=list.entrySet();  //实例化
		        Iterator<Map.Entry<String,String>> iter=allset.iterator();
		        while(iter.hasNext())
		        {
		            Map.Entry<String,String> me=iter.next();
		            if(
		            	  //支付宝	
		            		(me.getKey().equals("alipartner")!=true)
		            	  &&(me.getKey().equals("aliseller_email")!=true)
		            	  &&(me.getKey().equals("alikey")!=true)
		            	  &&(me.getKey().equals("alisubpartner")!=true)
		            	  &&(me.getKey().equals("isalisub")!=true)
		            	  &&(me.getKey().equals("aliprivateKey")!=true)
		            	  //微信	
		            	  &&(me.getKey().equals("weiappid")!=true)
		            	  &&(me.getKey().equals("weimch_id")!=true)
		            	  &&(me.getKey().equals("weikey")!=true)
		            	  &&(me.getKey().equals("weisubmch_id")!=true)
		            	  &&(me.getKey().equals("isweisub")!=true)
		              )
		            	list2.put(me.getKey(), me.getValue());
		            	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config3="+me.getKey()+"--"+me.getValue());
		        } 	
		        //支付宝
		        int ALIPAYMODE=1;
		        if((ToolClass.isEmptynull(object2.get("ALIPAYMODE").toString())==false)
		        	&&(object2.get("ALIPAYMODE").toString().equals("null")==false)
		        )
		        	ALIPAYMODE=Integer.parseInt(object2.get("ALIPAYMODE").toString());
		       
		        //搜索支付宝是否已经打开
		        int getZhifubaoer=0;
		        vmc_system_parameterDAO parameterDAOrd = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
			    // 获取所有收入信息，并存储到List泛型集合中
		    	Tb_vmc_system_parameter tb_inaccount = parameterDAOrd.find();
		    	if(tb_inaccount!=null)
		    	{
		    		getZhifubaoer=tb_inaccount.getZhifubaoer();
		    	}
		    	
		        //2.0
		        if(ALIPAYMODE==2)
		        {
		        	ALIPAYMODE=(getZhifubaoer>0)?ALIPAYMODE:getZhifubaoer;
		        	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	  			    //创建Tb_inaccount对象 
	    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
	    					0,0,"",0,0,0,ALIPAYMODE,0,0,0,"",0,
	    					0,0, 0,0,0,"","");
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置支付宝VMC_NO="+tb_vmc_system_parameter.getDevID()+",zhifubaoer="+tb_vmc_system_parameter.getZhifubaoer(),"server.txt");	
	    			parameterDAO.updatezhifubao(tb_vmc_system_parameter); 
			        
		        	list2.put("alipartner", object2.get("ALIPAYTWO_PID").toString());
	  	        	list2.put("aliseller_email", "");
	  	        	list2.put("alikey", "");
	  	        	list2.put("alisubpartner", object2.get("ALIPAYTWO_ALIOTHERPARTNER").toString());
	  	        	if((ToolClass.isEmptynull(object2.get("ALIPAYTWO_MERCHANT_PRIVATE_ANDROID_KEY").toString())==false)
	  			        	&&(object2.get("ALIPAYTWO_MERCHANT_PRIVATE_ANDROID_KEY").toString().equals("null")==false)
	  			        )
	  	        	{
	  	        		list2.put("aliprivateKey", object2.get("ALIPAYTWO_MERCHANT_PRIVATE_ANDROID_KEY").toString());
	  	        	}
	  	        	else
	  	        	{
	  	        		list2.put("aliprivateKey", object2.get("ALIPAYTWO_MERCHANT_PRIVATE_KEY").toString());
	  	        	}	
	  	        	
	  	        	if(ToolClass.isEmptynull(object2.get("ALIPAYTWO_ALIOTHERPARTNER").toString()))
	  	        	{
	  	        		list2.put("isalisub", "0");
	  	        	}
	  	        	else
	  	        	{
	  	        		list2.put("isalisub", "0.995");
	  	        	}
		        }
		        //1.0
		        else
		        {
		        	ALIPAYMODE=(getZhifubaoer>0)?ALIPAYMODE:getZhifubaoer;
		        	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	  			    //创建Tb_inaccount对象 
	    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
	    					0,0,"",0,0,0,1,0,0,0,"",0,
	    					0,0, 0,0,0,"","");
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置支付宝VMC_NO="+tb_vmc_system_parameter.getDevID()+",zhifubaoer="+tb_vmc_system_parameter.getZhifubaoer(),"server.txt");	
	    			parameterDAO.updatezhifubao(tb_vmc_system_parameter); 
			        
			        list2.put("alipartner", object2.get("ALI_PARTNER").toString());
	  	        	list2.put("aliseller_email", object2.get("ALI_SELLER_EMAIL").toString());
	  	        	list2.put("alikey", object2.get("ALI_SECURITY_KEY").toString());
	  	        	list2.put("alisubpartner", object2.get("ALI_OTHER_PARTNER").toString());
	  	        	list2.put("aliprivateKey", "");
	  	        	if(ToolClass.isEmptynull(object2.get("ALI_OTHER_PARTNER").toString()))
	  	        	{
	  	        		list2.put("isalisub", "0");
	  	        	}
	  	        	else
	  	        	{
	  	        		list2.put("isalisub", "0.995");
	  	        	}	
		        }
  	        	
  	        	//微信
  	        	list2.put("weiappid", object2.get("WX_APP_ID").toString());
  	        	list2.put("weimch_id", object2.get("WX_MCHID").toString());
  	        	list2.put("weikey", object2.get("WX_KEY").toString());
  	        	list2.put("weisubmch_id", object2.get("WX_OTHER_MCHID").toString());
  	        	if(ToolClass.isEmptynull(object2.get("WX_OTHER_MCHID").toString()))
  	        	{
  	        		list2.put("isweisub", "0");
  	        	}
  	        	else
  	        	{
  	        		list2.put("isweisub", "1");
  	        	}
		        ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<config3="+list2.toString(),"server.txt");
		        JSONObject jsonObject = new JSONObject(list2);
		        String mapstrString=jsonObject.toString();
		        ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<config4="+mapstrString,"server.txt");
		        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
	            FileWriter writer = new FileWriter(fileName);
	            writer.write(mapstrString);
	            writer.close();
  	         }
  	         else
  	         {  	        	
  	        	JSONObject jsonObject = new JSONObject();
  	            //支付宝
  	        	int ALIPAYMODE=1;
		        if(ToolClass.isEmptynull(object2.get("ALIPAYMODE").toString())==false)
		        	ALIPAYMODE=Integer.parseInt(object2.get("ALIPAYMODE").toString());
		        //搜索支付宝是否已经打开
		        int getZhifubaoer=0;
		        vmc_system_parameterDAO parameterDAOrd = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
			    // 获取所有收入信息，并存储到List泛型集合中
		    	Tb_vmc_system_parameter tb_inaccount = parameterDAOrd.find();
		    	if(tb_inaccount!=null)
		    	{
		    		getZhifubaoer=tb_inaccount.getZhifubaoer();
		    	}
		    	
		        //2.0
		        if(ALIPAYMODE==2)
		        {
		        	ALIPAYMODE=(getZhifubaoer>0)?ALIPAYMODE:getZhifubaoer;
		        	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	  			    //创建Tb_inaccount对象 
	    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
	    					0,0,"",0,0,0,ALIPAYMODE,0,0,0,"",0,
	    					0,0, 0,0,0,"","");
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置支付宝VMC_NO="+tb_vmc_system_parameter.getDevID()+",zhifubaoer="+tb_vmc_system_parameter.getZhifubaoer(),"server.txt");	
	    			parameterDAO.updatezhifubao(tb_vmc_system_parameter); 
			        
	    			jsonObject.put("alipartner", object2.get("ALIPAYTWO_PID").toString());
	    			jsonObject.put("aliseller_email", "");
	  	        	jsonObject.put("alikey", "");
	  	        	jsonObject.put("alisubpartner", object2.get("ALIPAYTWO_ALIOTHERPARTNER").toString());
	  	        	jsonObject.put("aliprivateKey", object2.get("ALIPAYTWO_MERCHANT_PRIVATE_KEY").toString());
	  	        	if(ToolClass.isEmptynull(object2.get("ALIPAYTWO_ALIOTHERPARTNER").toString()))
	  	        	{
	  	        		jsonObject.put("isalisub", "0");
	  	        	}
	  	        	else
	  	        	{
	  	        		jsonObject.put("isalisub", "0.995");
	  	        	}
		        }
		        //1.0
		        else
		        {
		        	ALIPAYMODE=(getZhifubaoer>0)?ALIPAYMODE:getZhifubaoer;
		        	vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	  			    //创建Tb_inaccount对象 
	    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
	    					0,0,"",0,0,0,1,0,0,0,"",0,
	    					0,0, 0,0,0,"","");
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","重置支付宝VMC_NO="+tb_vmc_system_parameter.getDevID()+",zhifubaoer="+tb_vmc_system_parameter.getZhifubaoer(),"server.txt");	
	    			parameterDAO.updatezhifubao(tb_vmc_system_parameter); 
			        
			        jsonObject.put("alipartner", object2.get("ALI_PARTNER").toString());
	  	        	jsonObject.put("aliseller_email", object2.get("ALI_SELLER_EMAIL").toString());
	  	        	jsonObject.put("alikey", object2.get("ALI_SECURITY_KEY").toString());
	  	        	jsonObject.put("alisubpartner", object2.get("ALI_OTHER_PARTNER").toString());
	  	        	jsonObject.put("aliprivateKey", "");
	  	        	if(ToolClass.isEmptynull(object2.get("ALI_OTHER_PARTNER").toString()))
	  	        	{
	  	        		jsonObject.put("isalisub", "0");
	  	        	}
	  	        	else
	  	        	{
	  	        		jsonObject.put("isalisub", "0.995");
	  	        	}	
		        }	
  	        	
  	        	//微信
  	        	jsonObject.put("weiappid", object2.get("WX_APP_ID"));
  	        	jsonObject.put("weimch_id", object2.get("WX_MCHID"));
  	        	jsonObject.put("weikey", object2.get("WX_KEY"));
  	        	jsonObject.put("weisubmch_id", object2.get("WX_OTHER_MCHID"));
  	        	if(ToolClass.isEmptynull(object2.get("WX_OTHER_MCHID").toString()))
  	        	{
  	        		jsonObject.put("isweisub", "0");
  	        	}
  	        	else
  	        	{
  	        		jsonObject.put("isweisub", "1");
  	        	}
  	        	String mapstrString=jsonObject.toString();
  	        	ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<config2="+mapstrString,"server.txt");
  	            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
  	            FileWriter writer = new FileWriter(fileName, true);
  	            writer.write(mapstrString);
  	            writer.close();
			 }
  	        
  	         //2.重置支付宝微信账号
  	        //从配置文件获取数据
  			Map<String, String> list=ToolClass.ReadConfigFile(); 
  	        AlipayConfigAPI.SetAliConfig(list);//设置阿里账号
	        WeiConfigAPI.SetWeiConfig(list);//设置微信账号
	        //加载微信证书
			ToolClass.setWeiCertFile();
//  	         //将json格式解包
//  	         list=new HashMap<String,String>();      			
//			JSONObject object=new JSONObject(str);      				
//			Gson gson=new Gson();
//			list=gson.fromJson(object.toString(), new TypeToken<Map<String, Object>>(){}.getType());
//			//Log.i("EV_JNI",perobj.toString());
//			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<config2="+list.toString());
        	
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    //作配置文件备份
    public static void ResetConfigFile()
    {
    	String sDir = ToolClass.getEV_DIR()+File.separator+"easivendconfig.txt";
    	String copyDir = ToolClass.getEV_DIR()+File.separator+"CONFIG"+File.separator;
		File dirName = new File(copyDir);
		//如果目录不存在，则创建目录
  		 if (!dirName.exists()) 
  		 {  
  			//按照指定的路径创建文件夹  
  			dirName.mkdirs(); 
  		 } 
		String copyFile = copyDir+"easivendconfig.txt";
		ToolClass.Log(ToolClass.INFO,"EV_SERVER"," 原文件"+sDir+"选定,copy目标文件="+copyFile,"server.txt"); 
		  
		copyFile(sDir,copyFile);
    }
    
    /**
     * 读取货道配置文件
     */
    public static Map<String, Integer> ReadColumnFile() 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	Map<String, Integer> list=null;
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evColumnconfig.txt";
        	  fileName=new File(sDir);
        	  //如果存在，才读文件
        	  if(fileName.exists())
        	  {
	    	  	 //打开文件
	    		  FileInputStream input = new FileInputStream(sDir);
	    		 //输出信息
	  	          Scanner scan=new Scanner(input);
	  	          while(scan.hasNext())
	  	          {
	  	           	str=scan.next()+"\n";
	  	          }
	  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
	  	         //将json格式解包
	  	         list=new HashMap<String,Integer>();   
	  	         huodaolist=new HashMap<Integer,Integer>(); 
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
				//输出内容
		        Set<Entry<String, Integer>> allmap=list.entrySet();  //实例化
		        Iterator<Entry<String, Integer>> iter=allmap.iterator();
		        while(iter.hasNext())
		        {
		            Entry<String, Integer> me=iter.next();	
	            	huodaolist.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
		        } 			
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+huodaolist.toString(),"com.txt");
        	  }
        	  else
        	  {
        		  WriteColumnFile("");
        		  list=new HashMap<String,Integer>();
        		  huodaolist=new HashMap<Integer,Integer>();
        	  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 读取逻辑物理货道配置表
     */
    public static Map<Integer, Integer> getHuodaolist() {
		return huodaolist;
	}
    
    /**
     * 弹簧出货
     */
    public static int columnChuhuo(Integer logic)
    {
    	int val=0;
    	if(huodaolist!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]huodaolist="+huodaolist,"com.txt");
    		if(huodaolist.containsKey(logic))
    		{
    			//根据key取出内容
    		    val=huodaolist.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    
    
    /**
     * 读取副柜货道配置文件
     */
    public static Map<String, Integer> ReadColumnFile2() 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	Map<String, Integer> list=null;
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evColumnconfig2.txt";
        	  fileName=new File(sDir);
        	  //如果存在，才读文件
        	  if(fileName.exists())
        	  {
	    	  	 //打开文件
	    		  FileInputStream input = new FileInputStream(sDir);
	    		 //输出信息
	  	          Scanner scan=new Scanner(input);
	  	          while(scan.hasNext())
	  	          {
	  	           	str=scan.next()+"\n";
	  	          }
	  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
	  	         //将json格式解包
	  	         list=new HashMap<String,Integer>();   
	  	         huodaolist2=new HashMap<Integer,Integer>(); 
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
				//输出内容
		        Set<Entry<String, Integer>> allmap=list.entrySet();  //实例化
		        Iterator<Entry<String, Integer>> iter=allmap.iterator();
		        while(iter.hasNext())
		        {
		            Entry<String, Integer> me=iter.next();	
	            	huodaolist2.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
		        } 			
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+huodaolist2.toString(),"com.txt");
        	  }
        	  else
        	  {
        		  WriteColumnFile2("");
        		  list=new HashMap<String,Integer>();
        		  huodaolist2=new HashMap<Integer,Integer>();
        	  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 读取副柜逻辑物理货道配置表
     */
    public static Map<Integer, Integer> getHuodaolist2() {
		return huodaolist2;
	}
    
    /**
     * 副柜弹簧出货
     */
    public static int columnChuhuo2(Integer logic)
    {
    	int val=0;
    	if(huodaolist2!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]huodaolist2="+huodaolist2,"com.txt");
    		if(huodaolist2.containsKey(logic))
    		{
    			//根据key取出内容
    		    val=huodaolist2.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    
    /**
     * 副柜写入货道配置文件
     */
    public static void WriteColumnFile2(String str) 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evColumnconfig2.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
  	         
  	          //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
  	          FileWriter writer = new FileWriter(fileName, false);
  	          writer.write(str);
  	          writer.close();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    
    /**
     * 弹簧返回出货结果
     *   GOODS_SOLD_ERR          (1 << 0)   //bit0总故障位
	* 	 GOODS_SOLDOUT_BIT       (1 << 1)   //bit1电机故障
	* 	 MOTO_MISPLACE_BIT       (1 << 2)   //bit2电机在转之前就不在正确的位置上(也算电机故障)
	* 	 MOTO_NOTMOVE_BIT        (1 << 3)   //bit3电机不能转(也算电机故障)
	* 	 MOTO_NOTRETURN_BIT      (1 << 4)   //bit4电机没转到正确位置(也算电机故障)
	* 	 GOODS_NOTPASS_BIT       (1 << 5)   //bit5商品没出(出货确认没检测到)
	* 	 DRV_CMDERR_BIT          (1 << 6)   //bit6命令错误(只有发送命令和查询命令着两个命令，如果发了其他的命令就报错)
	* 	 DRV_GOCERR_BIT          (1 << 7)   //bit7出货检测模块状态(GOC故障)
	 * 1:成功;0:故障;2:货道不存在;3:电机未到位;4:无货 5:通信故障
     */
    public static int colChuhuorst(int Rst)
    {
    	int GOODS_SOLD_ERR=(1 << 0);   //bit0总故障位
    	int GOODS_SOLDOUT_BIT=(1 << 1);   //bit1电机故障
    	int MOTO_MISPLACE_BIT=(1 << 2);  //bit2电机在转之前就不在正确的位置上(也算电机故障)
    	int MOTO_NOTMOVE_BIT=(1 << 3); //bit3电机不能转(也算电机故障)
    	int MOTO_NOTRETURN_BIT=(1 << 4);   //bit4电机没转到正确位置(也算电机故障)
    	int GOODS_NOTPASS_BIT=(1 << 5);   //bit5商品没出(出货确认没检测到)
    	int DRV_CMDERR_BIT   =(1 << 6);   //bit6命令错误(只有发送命令和查询命令着两个命令，如果发了其他的命令就报错)
    	int DRV_GOCERR_BIT   =(1 << 7);   //bit7出货检测模块状态(GOC故障)
    	if(Rst == 0x00)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_OK","com.txt");
    		return 1;	
    	}
    	else if(Rst == 0xff)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_COMERR","com.txt");
    		return 5;
    	}	
    	//1.判断GOC是否故障bit7:  GOC故障->扣钱
    	else if((Rst&DRV_GOCERR_BIT)>0)
    	{
    		if((Rst&GOODS_SOLD_ERR)>0)
    		{
    			//2.在判断电机：
    			//没转到位等其他状况bit1,bit2,bit3,bit4->货道置故障  
    			if( (Rst & (GOODS_SOLDOUT_BIT|MOTO_MISPLACE_BIT|MOTO_NOTMOVE_BIT|MOTO_NOTRETURN_BIT))>0) 
    			{
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_3","com.txt");
    				return 3;								
    			}
    			else
    			{
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_4","com.txt");
    				return 4;	
    			}
    		}
    		else
    		{
    			ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_1","com.txt");
    			return 1;
    		}
    	}
    	else if((Rst&GOODS_SOLD_ERR)>0)
    	{
    		/*
    		//电机未转动
    		if((GocType==1)&&(Rst&0x08))
    			return 1;
    		//出货确认开启。出货确认未检测到商品出货
    		if((GocType==1)&&(Rst&0x20))
    			return 4;	
    		else
    		//出货确认开启，电机未转到位，但出货确认检测到商品出货
    		if((GocType==1)&&(Rst==0x11))
    			return 0;
    		else
    		//出货确认未开启，电机未转到位
    		if((GocType==0)&&(Rst&0x10))
    			return 3;
    		else
    		//出货确认未开启，电机不能转动
    		if((GocType==0)&&(Rst&0x08))
    			return 1;
    		*/
    		/*********GOC打开的情况下********************/
    		if(ToolClass.getGoc()==1)
    		{			
    			//1.电机未转动,不扣钱
    			if((Rst&MOTO_NOTMOVE_BIT)>0)
    			{
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_0","com.txt");
    				return 0;
    			}			
    			//2.先判断GOC是否检测到bit5,  ==0说明出货确认检测到商品出货 			
    			else if((Rst & GOODS_NOTPASS_BIT) == 0) 
    			{				
    				//有检测到->出货成功扣钱  
    				/*************************************************************************/
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_1","com.txt");
    				return 1;		
    			}
    			else
    			{ 
    				   //没检测到->出货失败不扣钱
    				   //3.在判断电机：
    				   //没转到位等其他状况bit1,bit2,bit3,bit4->货道置故障	
    				   if( (Rst & (GOODS_SOLDOUT_BIT|MOTO_MISPLACE_BIT|MOTO_NOTMOVE_BIT|MOTO_NOTRETURN_BIT))>0) 
    				   {
    						ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_3","com.txt");
    						return 3;
    				   }
    				   else
    				   {
    						ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_4","com.txt");
    						return 4;
    				   }				   
    			}
    			
    		}	
    		/*********GOC关闭的情况下********************/
    		else
    		{
    			//2.在判断电机：
    		   //没转到位等其他状况bit1,bit2,bit3,bit4->货道置故障	
    		   if( (Rst & (GOODS_SOLDOUT_BIT|MOTO_MISPLACE_BIT|MOTO_NOTMOVE_BIT|MOTO_NOTRETURN_BIT))>0) 
    		   {
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_3","com.txt");
    				return 3;								
    		   }
    		   else
    		   {
    				ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]rst=OutGoods_4","com.txt");
    				return 4;	
    		   }
    		}
    	}
    	return 0;
    }
    
    
    
    /**
     * 写入货道配置文件
     */
    public static void WriteColumnFile(String str) 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evColumnconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
  	         
  	          //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
  	          FileWriter writer = new FileWriter(fileName, false);
  	          writer.write(str);
  	          writer.close();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /*=============
     * 升降机模块
     =============*/
    /**
     * 读取货道配置文件
     */
    public static Map<String, Integer> ReadElevatorFile() 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	Map<String, Integer> list=null;
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evElevatorconfig.txt";
        	  fileName=new File(sDir);
        	  //如果存在，才读文件
        	  if(fileName.exists())
        	  {
	    	  	 //打开文件
	    		  FileInputStream input = new FileInputStream(sDir);
	    		 //输出信息
	  	          Scanner scan=new Scanner(input);
	  	          while(scan.hasNext())
	  	          {
	  	           	str=scan.next()+"\n";
	  	          }
	  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
	  	         //将json格式解包
	  	         list=new HashMap<String,Integer>();   
	  	         elevatorlist=new HashMap<Integer,Integer>(); 
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String, Integer>>(){}.getType());
				//输出内容
		        Set<Entry<String, Integer>> allmap=list.entrySet();  //实例化
		        Iterator<Entry<String, Integer>> iter=allmap.iterator();
		        while(iter.hasNext())
		        {
		            Entry<String, Integer> me=iter.next();	
		            elevatorlist.put(Integer.parseInt(me.getKey()),(Integer)me.getValue());		            
		        } 			
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+elevatorlist.toString(),"com.txt");
        	  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 升降机出货
     */
    public static int elevatorChuhuo(Integer logic)
    {
    	int val=0;
    	if(elevatorlist!=null)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]elevatorlist="+elevatorlist,"com.txt");
    		if(elevatorlist.containsKey(logic))
    		{
    			//根据key取出内容
    		    val=elevatorlist.get(logic); 
    		    ToolClass.Log(ToolClass.INFO,"EV_COM","[APPcolumn>>]logic="+logic+"physic="+val,"com.txt");
    		}
    	}
    	return val;
    }
    /**
     * 写入升降机货道配置文件
     */
    public static void WriteElevatorFile(String str) 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evElevatorconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
  	         
  	          //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
  	          FileWriter writer = new FileWriter(fileName, false);
  	          writer.write(str);
  	          writer.close();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * 升降机返回出货结果
    private static final int LIFT_VENDOUT_COM_ERR	=		0x1F;		//通信故障
	private static final int LIFT_VENDOUT_FAULT		=		0x12;		//升降机故障
	private static final int LIFT_VENDOUT_BUSY		=		0x11;		//升降机忙
	private static final int LIFT_VENDOUT_FAIL		=		0;			//出货失败 通信失败
	private static final int LIFT_VENDOUT_SUC		=		1;			//出货成功
	private static final int LIFT_VENDOUT_DATAERR	=		2;			//数据错误
	private static final int LIFT_VENDOUT_EMPTY		=		3;			//无货
	private static final int LIFT_VENDOUT_STUCK		=		4 ; 			//卡货
	private static final int LIFT_VNEDOUT_DOOR_NOT_OPEN	=	5;			//取货门未开启
	private static final int LIFT_VENDOUT_GOODS_NOT_TAKE=	6;			//货物未取走
	private static final int LIFT_VENDOUT_OTHER_FAULT	=	7	;		//其他故障
	private static final int LIFT_VENDOUT_VENDING		=	0x88;		//正在出货
	 *  1:成功;4:无货8:卡货;其他与故障码一样
     */    
    public static int elevatorChuhuorst(int Rst)
    {    
    	int LIFT_VENDOUT_COM_ERR	=		0x1F;		//通信故障
    	int LIFT_VENDOUT_FAULT		=		0x12;		//升降机故障
    	int LIFT_VENDOUT_BUSY		=		0x11;		//升降机忙
    	int LIFT_VENDOUT_FAIL		=		0;			//出货失败 通信失败
    	int LIFT_VENDOUT_SUC		=		1;			//出货成功
    	int LIFT_VENDOUT_DATAERR	=		2;			//数据错误
    	int LIFT_VENDOUT_EMPTY		=		3;			//无货
    	int LIFT_VENDOUT_STUCK		=		4 ; 			//卡货
    	int LIFT_VNEDOUT_DOOR_NOT_OPEN	=	5;			//取货门未开启
    	int LIFT_VENDOUT_GOODS_NOT_TAKE=	6;			//货物未取走
    	int LIFT_VENDOUT_OTHER_FAULT	=	7	;		//其他故障
    	int LIFT_VENDOUT_VENDING		=	0x88;		//正在出货
    	if(Rst==LIFT_VENDOUT_EMPTY)
    	{
    		return 4;
    	}
    	else if(Rst==LIFT_VENDOUT_STUCK)
    	{
    		return 8;
    	}
    	else
    	{
    		return Rst;
    	}
    }
    
    /*=============
     * 选货按键模块
     =============*/
    /**
     * 读取选货按键文件
     */
    public static Map<String,String> ReadSelectFile() 
    {
    	File fileName=null;
    	String  sDir =null,str=null;
    	Map<String,String> list=null;
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evSelectconfig.txt";
        	  fileName=new File(sDir);
        	  //如果存在，才读文件
        	  if(fileName.exists())
        	  {
	    	  	 //打开文件
	    		  FileInputStream input = new FileInputStream(sDir);
	    		 //输出信息
	  	          Scanner scan=new Scanner(input);
	  	          while(scan.hasNext())
	  	          {
	  	           	str=scan.next()+"\n";
	  	          }
	  	         ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config="+str,"com.txt");
	  	         //将json格式解包
	  	         list=new HashMap<String,String>();   
	  	         selectlist=new HashMap<String,String>(); 
				JSONObject object=new JSONObject(str);      				
				Gson gson=new Gson();
				list=gson.fromJson(object.toString(), new TypeToken<Map<String,String>>(){}.getType());
				//输出内容
		        Set<Entry<String,String>> allmap=list.entrySet();  //实例化
		        Iterator<Entry<String,String>> iter=allmap.iterator();
		        while(iter.hasNext())
		        {
		            Entry<String,String> me=iter.next();	
		            selectlist.put(me.getKey(),me.getValue());		            
		        } 			
				//Log.i("EV_JNI",perobj.toString());
				ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<config2="+selectlist.toString(),"com.txt");
        	  }
        	             
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 写入按键配置文件
     */
    public static void WriteSelectFile(String selectKey,String productID) 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evSelectconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
  	          
          	  ReadSelectFile();
          	  selectlist.put(selectKey,productID);
          	  ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<selectlist="
                	  +selectlist.toString(),"com.txt");
          	  Map<String,String> list=new HashMap<String,String>();
              //删除这个相同的Value值
			  for(Map.Entry entry:selectlist.entrySet())
			  {
				 if(entry.getValue().equals(productID)!=true)
				 {
					 list.put(entry.getKey().toString(), entry.getValue().toString());
				 }				 
			  }
			  ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<list="
                	  +list.toString(),"com.txt");
          	  
			  JSONObject obj=new JSONObject();	
          	  //将其余的补充进来
			  for(Map.Entry entry:list.entrySet())
			  {
			      obj.put(entry.getKey().toString(), entry.getValue().toString());
			  }
			  //将本次添加进来
			  obj.put(selectKey, productID);
			  
          	  ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<JSONObject="+obj.toString(),"com.txt");
  	          //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
  	          FileWriter writer = new FileWriter(fileName, false);  	          
  	          writer.write(obj.toString());
  	          writer.close();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * 删除按键配置文件
     */
    public static void DelSelectFile() 
    {
    	File fileName=null;
    	String  sDir =null;
    	
    	    	
        try {
        	  sDir = ToolClass.getEV_DIR()+File.separator+"evSelectconfig.txt";
        	 
        	  fileName=new File(sDir);
        	  //如果不存在，则创建文件
          	  if(!fileName.exists())
          	  {  
      	        fileName.createNewFile(); 
      	      }  
          	  fileName.delete();
  	          
          	  ReadSelectFile();	
  	          
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    //保存操作日志
    public static void addOptLog(Context context, int logType, String logDesc)
	{
    	String id="";
 	    vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 得到设备ID号
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		id=tb_inaccount.getDevhCode().toString();
    	}
    	Log.i("EV_JNI","Send0.0="+id);
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
        String datetime = tempDate.format(new java.util.Date()).toString(); 					
        String logID="log"+id+datetime;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
    	String date=df.format(new Date());
    	       
    	vmc_logDAO logDAO = new vmc_logDAO(context);// 创建InaccountDAO对象		
    	Tb_vmc_log tb_vmc_log=new Tb_vmc_log(logID, logType, logDesc,
    			date);
		logDAO.add(tb_vmc_log);		
	}
	
	//发送金额函数，浮点的元,转为以分为单位发送到底下
	public static int MoneySend(float sendMoney)
	{
		int values=(int)(sendMoney*100);
		return values;
	}
	
	//接收金额转换函数，接收的分,转为浮点的元
	public static float MoneyRec(long Money)
	{
		float amount1=0,amount2=0;
		amount1=(float)(Money/100);
		amount2=(float)(Money%100);
		amount2=amount2/100;
		float amount = amount1+amount2;
		return amount;
	}
	
	/**
	 * 根据这个Uri获得其在文件系统中的路径
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath( final Context context, final Uri uri )
	{
	    if ( null == uri ) 
	    	return "";
	    final String scheme = uri.getScheme();
	    String data = null;
	    if ( scheme == null )
	        data = uri.getPath();
	    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
	        data = uri.getPath();
	    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
	        Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
	        if ( null != cursor ) {
	            if ( cursor.moveToFirst() ) {
	                int index = cursor.getColumnIndex( ImageColumns.DATA );
	                if ( index > -1 ) {
	                    data = cursor.getString( index );
	                }
	            }
	            cursor.close();
	        }
	    }
	    return data;
	}
	
	/**
     * 加载本地图片
     * @param url
     * @return
     */
	public static Bitmap getLoacalBitmap(String url) {
        try {
        	 BitmapFactory.Options opt = new BitmapFactory.Options();  
        	 opt.inPreferredConfig = Bitmap.Config.RGB_565;   
        	 opt.inPurgeable = true;  
        	 opt.inInputShareable = true;  
             FileInputStream fis = new FileInputStream(url);
             return BitmapFactory.decodeStream(fis,null,opt);  ///把流转化为Bitmap图片        

          } catch (FileNotFoundException e) {
             e.printStackTrace();
             return null;
        }
   }
     
     /**
      * @方法功能说明: 生成二维码图片,实际使用时要初始化sweepIV,不然会报空指针错误
      * @作者:饶正勇
      * @时间:2013-4-18上午11:14:16
      * @参数: @param url 要转换的地址或字符串,可以是中文
      * @return void
      * @throws
      */
     //要转换的地址或字符串,可以是中文
     public static Bitmap createQRImage(String url)
     {
    	int QR_WIDTH = 200;
		int QR_HEIGHT = 200; 
     	try
     	{
     		//判断URL合法性
     		if (url == null || "".equals(url) || url.length() < 1)
     		{
     			return null;
     		}
     		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
     		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
     		//图像数据转换，使用了矩阵转换
     		BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
     		int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
     		//下面这里按照二维码的算法，逐个生成二维码的图片，
     		//两个for循环是图片横列扫描的结果
     		for (int y = 0; y < QR_HEIGHT; y++)
     		{
     			for (int x = 0; x < QR_WIDTH; x++)
     			{
     				if (bitMatrix.get(x, y))
     				{
     					pixels[y * QR_WIDTH + x] = 0xff000000;
     				}
     				else
     				{
     					pixels[y * QR_WIDTH + x] = 0xffffffff;
     				}
     			}
     		}
     		//生成二维码图片的格式，使用ARGB_8888
     		Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
     		bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
     		return bitmap;
     	}
     	catch (WriterException e)
     	{
     		e.printStackTrace();
     	}
		return null;
     }    
     
    //out_trade_no方法，生成订单号
 	public static String out_trade_no(Context context)
 	{
 		String id="";
 	    String out_trade_no=null;
 		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 得到设备ID号
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		id=tb_inaccount.getDevID().toString();
    	}
    	Log.i("EV_JNI","Send0.0="+id);
    	SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //精确到毫秒 
        String datetime = tempDate.format(new java.util.Date()).toString(); 					
        out_trade_no=id+datetime;
        return out_trade_no;
 	}
 	
 	//vmc_no方法，得到设备id,签到码id号
 	public static Map<String, String> getvmc_no(Context context)
 	{
 		Map<String,String> allSet = new HashMap<String,String>() ;
 	    vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 得到设备ID号
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		allSet.put("vmc_no",tb_inaccount.getDevID().toString());
    		allSet.put("vmc_auth_code",tb_inaccount.getDevhCode().toString());
    		ToolClass.setVmc_no(tb_inaccount.getDevID().toString());
    	}
    	return allSet;
 	}
 	
 	/*********************************************************************************************************
	** Function name:     	typestr
	** Descriptions:	       将各个数字状态转成字符串显示
	** input parameters:    type=0支付方式,1订单状态,2退款状态
	** payType;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	** payStatus;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
	** RealStatus;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	** logType    操作类型0添加,1修改,2删除
	** output parameters:   无
	** Returned value:      无
	*********************************************************************************************************/
	public static String typestr(int type,int value)
	{
		switch(type)
		{
			case 0:// 支付方式
				// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
				switch(value)
				{
					case 0:
						return "现金";						
					case 1:
						return "银联";	
					case 2:
						return "支付宝声波";
					case 3:
						return "支付宝二维码";
					case 4:
						return "微支付";		
				}
				break;
			case 1:// 订单状态
				// 订单状态0出货成功，1出货失败，2支付失败，3未支付
				switch(value)
				{
					case 0:
						return "出货成功";						
					case 1:
						return "出货失败";	
					case 2:
						return "支付失败";
					case 3:
						return "未支付";					
				}
				break;
			case 2:// 退款状态
				// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
				switch(value)
				{
					case 0:
						return "";						
					case 1:
						return "退款完成";	
					case 2:
						return "部分退款";
					case 3:
						return "退款失败";					
				}
				break;
			case 3:// 操作类型
				// 操作类型0添加,1修改,2删除
				switch(value)
				{
					case 0:
						return "添加";						
					case 1:
						return "修改";	
					case 2:
						return "删除";									
				}
				break;	
		}
		return "";
	}
	
	/**
	 * 格式化时间
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@param year
	 * @param:@param month
	 * @param:@param type=0月初,1月中旬,2月下旬
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	public static String getDayOfMonth(int year,int month,int day)
	{
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//设置日
		cal.set(Calendar.DATE, day);
		
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastDayOfMonth = sdf.format(cal.getTime());
		
		return lastDayOfMonth;
	}
	/**
	 * 获取某月的最后一天或者中旬
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@param year
	 * @param:@param month
	 * @param:@param type=0月初,1月中旬,2月下旬
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	public static String getLastDayOfMonth(int year,int month,int type)
	{
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		if(type==0)
		{
			//设置日历中月份的中旬
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		else if(type==1)
		{
			//设置日历中月份的中旬
			cal.set(Calendar.DAY_OF_MONTH, 15);
		}
		else if(type==2)
		{
			//获取某月最大天数
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			//设置日历中月份的最大天数
			cal.set(Calendar.DAY_OF_MONTH, lastDay);
		}
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastDayOfMonth = sdf.format(cal.getTime());
		
		return lastDayOfMonth;
	}
	//是否在时间区间中,s是需要比较的时间,begin,end是时间区间
	//是返回true
	public static boolean isdatein(String begin,String end,String s)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s+"在"+begin+"="+dateCompare(s,begin));
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s+"在"+end+"="+dateCompare(end,s));
		if((dateCompare(s,begin)>=0)&&(dateCompare(end,s)>=0))
		{
			return true;
		}
		return false;
	}
	//时间比较,返回值result==0s1相等s2,result<0s1小于s2,result:>0s1大于s2,
	public static int dateCompare(String s1,String s2)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s1);
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s2);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Calendar c1=java.util.Calendar.getInstance();
		java.util.Calendar c2=java.util.Calendar.getInstance();
		try
		{
			c1.setTime(df.parse(s1));
			c2.setTime(df.parse(s2));
		}catch(java.text.ParseException e){
			System.err.println("格式不正确");
		}
		return c1.compareTo(c2);
	}
	
	//得到hopper设备的当前状态
	public static String gethopperstats(int hopper)
	{
		String res=null;
		//"hopper":8个hopper的状态,0正常,1缺币,2故障,3通讯故障
		if(hopper==0)
		{
			res="正常";
		}
		else if(hopper==1)
		{
			res="缺币";
		}
		else if(hopper==2)
		{
			res="故障";
		}
		else if(hopper==3)
		{
			res="通讯故障";
		}
		return res;
	}
	
	//为上传给server使用，将当前时间转换为给server上报的时间
	public static String getLasttime()
	{
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd"); //精确到毫秒 
		SimpleDateFormat tempTime = new SimpleDateFormat("HH:mm:ss"); //精确到毫秒 
        String datetime = tempDate.format(new java.util.Date()).toString()+"T"
        		+tempTime.format(new java.util.Date()).toString(); 
		return datetime;
	}
	
	//为上传给server使用，传入一个时间，转换为给server上报的时间
	public static String getStrtime(String orderTime)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date =null;
		try {
			date = df.parse(orderTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd"); //精确到毫秒 
		SimpleDateFormat tempTime = new SimpleDateFormat("HH:mm:ss"); //精确到毫秒 
        String datetime = tempDate.format(date).toString()+"T"
        		+tempTime.format(date).toString(); 
		return datetime;
	}
	
	//获取设备状态并上传给服务器
	//dev设备：1纸币器,2硬币器,3hopper1,4hopper2,5hopper3,6hopper4,7hopper5,8hopper6,9hopper7,10hopper8
	//返回：2故障，0正常
	public static int getvmcStatus(Map<String, Object> Set,int dev)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现金设备状态="+rst,"log.txt");	
		int rst=0;
		switch (dev) 
		{
			//纸币器
			case 1:
				int bill_err=(Integer)Set.get("bill_err");
				int bill_enable=(Integer)Set.get("bill_enable");
				if(bill_err>0)
				{
					rst=2;
				}
				else
				{
					rst=0;
				}
				break;
			//硬币器	
			case 2:
				int coin_err=(Integer)Set.get("coin_err");
				int coin_enable=(Integer)Set.get("coin_enable");
				if(coin_err>0)
				{
					rst=2;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper1	
			case 3:
				int hopper1=(Integer)Set.get("hopper1");
				if((hopper1==3)||(hopper1==2))
				{
					rst=2;
				}
				else if(hopper1==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper2	
			case 4:
				int hopper2=(Integer)Set.get("hopper2");
				if((hopper2==3)||(hopper2==2))
				{
					rst=2;
				}
				else if(hopper2==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper3	
			case 5:
				int hopper3=(Integer)Set.get("hopper3");
				if((hopper3==3)||(hopper3==2))
				{
					rst=2;
				}
				else if(hopper3==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper4		
			case 6:
				int hopper4=(Integer)Set.get("hopper4");
				if((hopper4==3)||(hopper4==2))
				{
					rst=2;
				}
				else if(hopper4==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper5	
			case 7:
				int hopper5=(Integer)Set.get("hopper5");
				if((hopper5==3)||(hopper5==2))
				{
					rst=2;
				}
				else if(hopper5==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper6
			case 8:
				int hopper6=(Integer)Set.get("hopper6");
				if((hopper6==3)||(hopper6==2))
				{
					rst=2;
				}
				else if(hopper6==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper7
			case 9:
				int hopper7=(Integer)Set.get("hopper7");
				if((hopper7==3)||(hopper7==2))
				{
					rst=2;
				}
				else if(hopper7==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;
			//hopper8
			case 10:
				int hopper8=(Integer)Set.get("hopper8");
				if((hopper8==3)||(hopper8==2))
				{
					rst=2;
				}
				else if(hopper8==1)
				{
					rst=1;
				}
				else
				{
					rst=0;
				}
				break;	
			default:
				break;
		}		
		return rst;
	}
	
	public static int getBill_err() {
		return bill_err;
	}

	public static void setBill_err(int bill_err) {
		ToolClass.bill_err = bill_err;
	}

	public static int getCoin_err() {
		return coin_err;
	}

	public static void setCoin_err(int coin_err) {
		ToolClass.coin_err = coin_err;
	}
	
	//密码框比对
	private static boolean passcmp(String pwd,String value)
    {
    	boolean istrue=false;
    	if((pwd==null)||(pwd.equals("")==true))
    	{
    		if(value.equals("83718557"))
    		{
    			istrue=true;
    		}
    	}
    	else
    	{
    		if(value.equals(pwd))
    		{
    			istrue=true;
    		}
    	}
    	return istrue;
    }
	//获取是否输入正确
	//返回：true正确,false错误
	public static boolean getpwdStatus(Context context,String passwd)
	{
		boolean istrue=false;
		//调出维护页面密码
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		String Pwd=tb_inaccount.getMainPwd().toString();
    		if(Pwd==null)
    		{
    			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<数值=null","log.txt");
    			istrue=passcmp(null,passwd);
    		}
    		else
    		{
    			//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<数值="+Pwd,"log.txt");
    			istrue=passcmp(Pwd,passwd);
    		}
    	}
    	else
    	{
    		istrue=passcmp(null,passwd);
		}
		return istrue;
	}
	
	//提货码比对
	//返回：true正确,false错误
	public static boolean getzhitihuo(Context context,String cabid,String columnID,String pwd)
	{
		boolean istrue=false;
		if((pwd==null)||(pwd.equals("")==true))
		{
			istrue=false;
		}
		else if((cabid==null)||(cabid.equals("")==true))
		{
			istrue=false;
		}	
		else if((columnID==null)||(columnID.equals("")==true))
		{
			istrue=false;
		}
		//调出货道提货密码
		else
		{
			// 创建InaccountDAO对象
			vmc_columnDAO columnDAO = new vmc_columnDAO(context);
			Tb_vmc_column tb_vmc_column = columnDAO.find(cabid,columnID);// 添加商品信息
			if(tb_vmc_column!=null)
			{
				String str=tb_vmc_column.getTihuoPwd();
				if((str==null)||(str.equals("")==true))
				{
					istrue=false;
				}
				else
				{
					if(pwd.equals(str)==true)
					{
						istrue=true;
					}
				}
			}	
		}
		return istrue;
	}
	//是否可以使用提货码出货
	//返回：true正确,false错误
	public static boolean getzhitihuotype(Context context,String cabid,String columnID)
	{
		boolean istrue=false;
		if((cabid==null)||(cabid.equals("")==true))
		{
			istrue=false;
		}	
		else if((columnID==null)||(columnID.equals("")==true))
		{
			istrue=false;
		}
		//调出货道提货密码
		else
		{
			// 创建InaccountDAO对象
			vmc_columnDAO columnDAO = new vmc_columnDAO(context);
			Tb_vmc_column tb_vmc_column = columnDAO.find(cabid,columnID);// 添加商品信息
			if(tb_vmc_column!=null)
			{
				String str=tb_vmc_column.getTihuoPwd();
				if((str==null)||(str.equals("")==true))
				{
					istrue=false;
				}
				else
				{
					istrue=true;
				}
			}	
		}
		return istrue;
	}
	/*********************************************************************************************************
	** Function name:     	CrcCheck
	** Descriptions:	    CRC校验和
	** input parameters:    msg需要检验的数据;len数据长度
	** output parameters:   无
	** Returned value:      CRC检验结果
	*********************************************************************************************************/
	public static int crcCheck(byte msg[], int len){
        int i, j, crc = 0, current;
        for(i = 0;i < len;i++)
        {
            current = ((msg[i] & 0x000000FF) << 8);
            for (j = 0;j < 8;j++)
            {
                if((short)(crc ^ current) < 0)
                {
                    crc =  ((crc << 1) ^ 0x1021) & 0x0000FFFF;
                }
                else
                {
                    crc = (crc << 1) & 0x0000FFFF;
                }
                current = (current << 1) & 0x0000FFFF;
            }
        }

        return crc;
    }

	/*********************************************************************************************************
	** Function name:     	printHex
	** Descriptions:	    打印十六进制信息
	** input parameters:    bentrecv需要打印的数据;bentindex数据长度
	** output parameters:   无
	** Returned value:      打印的十六进制信息
	*********************************************************************************************************/
	public static StringBuilder printHex(byte[] bentrecv,int bentindex)
	{
		StringBuilder res=new StringBuilder();
		for(int i=0;i<bentindex;i++)
	    {
	    	String hex = Integer.toHexString(bentrecv[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }	
	    	res.append("[").append(hex.toUpperCase()).append("]");				    	
	    }
		return res;
	}
	
	//提取出portid
	public static int Resetportid(String bentcom)
	{
		int bentcom_id=0;
		//2.重新组包
		try {
			JSONObject jsonObject = new JSONObject(bentcom); 
			//根据key取出内容
			JSONObject ev_head = (JSONObject) jsonObject.getJSONObject("EV_json");
			int str_evType =  ev_head.getInt("EV_type");
			if(str_evType==EVprotocol.EV_REGISTER)
			{
				bentcom_id=ev_head.getInt("port_id");				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bentcom_id;
	}
	
	//type=1是现金,2是格子柜，3是弹簧柜,4外协设备串口
	public static void ResstartPort(int type)
	{
		if(type==1)//现金
		{
			//打开现金设备串口
			if(ToolClass.getCom().equals("")==false) 
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","comRelease=port="+ToolClass.getCom()+"    port_id="+ToolClass.getCom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getCom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","comRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String com = EVprotocol.EVPortRegister(ToolClass.getCom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","comRegister="+com,"com.txt");
				ToolClass.setCom_id(ToolClass.Resetportid(com));
			}			
		}
		else if(type==2)
		{
			//打开格子柜串口
			if(ToolClass.getBentcom().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","bentcomRelease=port="+ToolClass.getBentcom()+"    port_id="+ToolClass.getBentcom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getBentcom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","bentcomRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String bentcom = EVprotocol.EVPortRegister(ToolClass.getBentcom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","bentcomRegister="+bentcom,"com.txt");
				ToolClass.setBentcom_id(ToolClass.Resetportid(bentcom));
			}
		}
		else if(type==3)
		{
			//打开弹簧柜串口
			if(ToolClass.getColumncom().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncomRelease=port="+ToolClass.getColumncom()+"    port_id="+ToolClass.getColumncom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getColumncom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncomRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String columncom = EVprotocol.EVPortRegister(ToolClass.getColumncom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","columncomRegister="+columncom,"com.txt");
				ToolClass.setColumncom_id(ToolClass.Resetportid(columncom));
			}
		}
		else if(type==4)
		{
			//打开外协设备串口
			if(ToolClass.getExtracom().equals("")==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_COM","extracomcomRelease=port="+ToolClass.getExtracom()+"    port_id="+ToolClass.getExtracom_id(),"com.txt");
				String com2 = EVprotocol.EVPortRelease(ToolClass.getExtracom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","extracomRelease="+com2,"com.txt");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String extracom = EVprotocol.EVPortRegister(ToolClass.getExtracom());
				ToolClass.Log(ToolClass.INFO,"EV_COM","extracomRegister="+extracom,"com.txt");
				ToolClass.setExtracom_id(ToolClass.Resetportid(extracom));
			}
		}
	}
	
	//弹出图形界面失败提示
	public static void failToast(String str)
	{
		// 弹出信息提示
		Toast myToast=Toast.makeText(context, str, Toast.LENGTH_LONG);
		myToast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) myToast.getView();
		ImageView imageCodeProject = new ImageView(context);
		imageCodeProject.setImageResource(R.drawable.search);
		toastView.addView(imageCodeProject, 0);
		myToast.show();
	}
	
	//判断字符串是否为空:true空，false非空
	public static boolean isEmptynull(String str)
	{
		boolean result=true;
		if(str!=null)
		{
			if((str.isEmpty()==false)&&(str.equals("")==false))
			{
				result=false;
			}
		}
		return result;
	}
	
	//检测Service是否已启动
	 public static boolean isServiceRunning(String serviceClassName)
	 { 
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE); 

        for (RunningServiceInfo runningServiceInfo : services) 
        { 
        	//ToolClass.Log(ToolClass.INFO,"EV_DOG","service appName:"+runningServiceInfo.service.getClassName()+"-->pack:"+runningServiceInfo.service.getPackageName(),"dog.txt");
            if (runningServiceInfo.service.getClassName().equals(serviceClassName))
            { 
                return true; 
            } 
        } 
        return false; 
	 }
	 
	 //去除字符串中的空格、回车、换行符、制表符
	 public static String replaceBlank(String str) {
	        String dest = "";
	        if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            dest = m.replaceAll("");
	        }
	        return dest;
	    }
	
}
