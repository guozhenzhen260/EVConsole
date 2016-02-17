/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_logDAO 操作日志文件  
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.dao;

import java.util.ArrayList;
import java.util.List;

import com.easivend.common.ToolClass;
import com.easivend.model.Tb_vmc_log;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class vmc_logDAO {
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_logDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	
	//添加
	public void add(Tb_vmc_log tb_vmc_log)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		// 开启一个事务
	    db.beginTransaction();
	    try {
	        // 执行添加订单支付表	
	 		db.execSQL(
	 				"insert into vmc_log" +
	 				"(" +
	 				"logID,logType,logDesc,logTime" +
	 				") " +
	 				"values" +
	 				"(" +
	 				"?,?,?,(datetime('now', 'localtime'))" +
	 				")",
	 		        new Object[] { tb_vmc_log.getLogID(), tb_vmc_log.getLogType(),tb_vmc_log.getLogDesc()});
 		
	 		// 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
	        db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        db.close(); 
	    } 
	}
	//删除时间范围内的数据
	public void detele(String starttime, String endtime) 
	{   
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象        	
		// 开启一个事务
	    db.beginTransaction();
	    try {
			// 执行删除订单详细信息表	
	 		db.execSQL(
	 				"delete from vmc_log where logTime between ? and ?",
	 				new String[] { starttime,endtime });
	 		 
	 		// 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
	        db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        db.close(); 
	    } 
    }
	//查找时间范围内的订单支付数据
	public List<Tb_vmc_log> getScrollPay(String starttime, String endtime) 
	{   
		List<Tb_vmc_log> tb_inaccount = new ArrayList<Tb_vmc_log>();// 创建集合对象
             
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现在时刻是"+starttime+",到"+endtime,"log.txt");     
        //取得时间范围内订单支付单号
		Cursor cursor = db.rawQuery("select logID,logType,logDesc,logTime " +
				" from vmc_log where logTime between ? and ?", 
				new String[] { starttime,endtime });// 获取收入信息表中的最大编号
//		Cursor cursor = db.rawQuery("select logID,logType,logDesc,logTime " +
//				" from vmc_log ", 
//				null);// 获取收入信息表中的最大编号
		while (cursor.moveToNext()) 
        {
			// 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_log
        		(
        				cursor.getString(cursor.getColumnIndex("logID")), cursor.getInt(cursor.getColumnIndex("logType")),
        				cursor.getString(cursor.getColumnIndex("logDesc")),cursor.getString(cursor.getColumnIndex("logTime"))
        		)
           );
        }
				
		if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
        db.close();
        return tb_inaccount;// 返回集合
    }
}
