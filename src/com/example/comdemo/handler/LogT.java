package com.example.comdemo.handler;

import android.annotation.SuppressLint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SdCardPath") 
@SuppressWarnings("unused")
public class LogT {
	
	public static String Consum_True = "Consum_true";
	public static String Consum_False ="Consum_false";
	public static String Threshold_True ="Chenchao_true";
	public static String Threshold_False ="Chenchao_false";
	public static String Voting_True ="VotingResult_true";
	public static String Voting_False ="VotingResult_false";
	
	public static String IS_REAL_PERSON = "real_person";
	public static String IS_FORGE_PERSON = "forge_person";
	private static SimpleDateFormat sdf = null;
	private static final String StorageDir = "/sdcard/";
	private static final String File_Suffix_Log = ".log";
	private static final String File_Suffix_Txt = ".txt";
	
	
	public static synchronized void writeLog(String pathStr, String logName,
			String Appendstr) {
		BufferedWriter out = null;
		try {
			File destDir = new File(StorageDir + pathStr);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			FileOutputStream outStream = new FileOutputStream(StorageDir
					+ pathStr + "/" + logName + File_Suffix_Log, true);
			OutputStreamWriter writer = new OutputStreamWriter(outStream,
					"utf-8");
			out = new BufferedWriter(writer);
			out.write(Appendstr);
			out.write("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String getLogName(String mark){
		sdf = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String logName = String.valueOf(sdf.format(new Date())) + mark;// new Date()为获取当前系统时间
		return logName;
	}
	public static String AppendLogTime(){
		sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss-SSS");//设置日期格式
		String _time = String.valueOf(sdf.format(new Date()));// new Date()为获取当前系统时间
		return _time;
	}
}
