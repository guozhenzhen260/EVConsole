/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           vmc_system_parameterDAO.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_system_parameterDAO 系统参数表操作文件  
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.easivend.common.ToolClass;
import com.easivend.model.Tb_vmc_system_parameter;

public class vmc_system_parameterDAO
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_system_parameterDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	//添加或修改
	public void add(Tb_vmc_system_parameter tb_vmc_system_parameter)throws SQLException
	{
		int max=0;
		//取得排序值
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(devID) from vmc_system_parameter", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            max=cursor.getInt(0);// 返回总记录数
        }
     
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if(max==0)
	        {
	        // 执行添加商品		
	 		db.execSQL(
	 				"insert into vmc_system_parameter" +
	 				"(" +
	 				"devID,devhCode,isNet,isfenClass,isbuyCar," +
	 				"liebiaoKuan,mainPwd,amount,card,zhifubaofaca,zhifubaoer,weixing,printer," +
	 				"language,rstTime,rstDay,baozhiProduct,emptyProduct,proSortType,marketAmount,billAmount" +
	 				") " +
	 				"values" +
	 				"(" +
	 				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
	 				")",
	 		        new Object[] { tb_vmc_system_parameter.getDevID(), tb_vmc_system_parameter.getDevhCode(),tb_vmc_system_parameter.getIsNet(), tb_vmc_system_parameter.getIsfenClass(),
	 						tb_vmc_system_parameter.getIsbuyCar(), tb_vmc_system_parameter.getLiebiaoKuan(),tb_vmc_system_parameter.getMainPwd(), tb_vmc_system_parameter.getAmount(),
	 						tb_vmc_system_parameter.getCard(), tb_vmc_system_parameter.getZhifubaofaca(), tb_vmc_system_parameter.getZhifubaoer(), tb_vmc_system_parameter.getWeixing(),
	 						tb_vmc_system_parameter.getPrinter(), tb_vmc_system_parameter.getLanguage(), tb_vmc_system_parameter.getRstTime(), tb_vmc_system_parameter.getRstDay(),
	 						tb_vmc_system_parameter.getBaozhiProduct(), tb_vmc_system_parameter.getEmptyProduct(), tb_vmc_system_parameter.getProSortType(),
	 						tb_vmc_system_parameter.getMarketAmount(),tb_vmc_system_parameter.getBillAmount()});
	 		
	        }
	        else
	        {
	            // 执行添加商品		
	     		db.execSQL(
	     				"update vmc_system_parameter " +
	     				"set " +
	     				"devID=?,devhCode=?,isNet=?,isfenClass=?,isbuyCar=?," +
	     				"liebiaoKuan=?,mainPwd=?,amount=?,card=?,zhifubaofaca=?,zhifubaoer=?,weixing=?,printer=?," +
	     				"language=?,rstTime=?,rstDay=?,baozhiProduct=?,emptyProduct=?,proSortType=?,marketAmount=?,billAmount=?" 
	     				,
	     		        new Object[] { tb_vmc_system_parameter.getDevID(), tb_vmc_system_parameter.getDevhCode(),tb_vmc_system_parameter.getIsNet(), tb_vmc_system_parameter.getIsfenClass(),
	     						tb_vmc_system_parameter.getIsbuyCar(), tb_vmc_system_parameter.getLiebiaoKuan(),tb_vmc_system_parameter.getMainPwd(), tb_vmc_system_parameter.getAmount(),
	     						tb_vmc_system_parameter.getCard(), tb_vmc_system_parameter.getZhifubaofaca(), tb_vmc_system_parameter.getZhifubaoer(), tb_vmc_system_parameter.getWeixing(),
	     						tb_vmc_system_parameter.getPrinter(), tb_vmc_system_parameter.getLanguage(), tb_vmc_system_parameter.getRstTime(), tb_vmc_system_parameter.getRstDay(),
	     						tb_vmc_system_parameter.getBaozhiProduct(), tb_vmc_system_parameter.getEmptyProduct(), tb_vmc_system_parameter.getProSortType(),
	     						tb_vmc_system_parameter.getMarketAmount(),tb_vmc_system_parameter.getBillAmount()});
	     		
	            }	
	        
	        // 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
	} 
	//修改密码
	public void updatepwd(Tb_vmc_system_parameter tb_vmc_system_parameter)throws SQLException
	{
		int max=0;
		//取得排序值
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(devID) from vmc_system_parameter", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            max=cursor.getInt(0);// 返回总记录数
        }
     
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if(max>0)	        
	        {
	            // 执行添加商品		
	     		db.execSQL(
	     				"update vmc_system_parameter " +
	     				"set " +
	     				"mainPwd=? " +
	     				"where devID=?" 
	     				,
	     		        new Object[] { tb_vmc_system_parameter.getMainPwd(),tb_vmc_system_parameter.getDevID()});
	     		
	            }		           
	        
	        // 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
	} 
	//修改活动信息
	public void updateevent(Tb_vmc_system_parameter tb_vmc_system_parameter)throws SQLException
	{
		int max=0;
		//取得排序值
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(devID) from vmc_system_parameter", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            max=cursor.getInt(0);// 返回总记录数
        }
     
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if(max>0)	        
	        {
	            // 执行添加商品		
	     		db.execSQL(
	     				"update vmc_system_parameter " +
	     				"set " +
	     				"event=? " +
	     				"where devID=?" 
	     				,
	     		        new Object[] { tb_vmc_system_parameter.getEvent(),tb_vmc_system_parameter.getDevID()});
	     		
	            }		           
	        
	        // 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
	} 
	//修改购买演示信息
	public void updatedemo(Tb_vmc_system_parameter tb_vmc_system_parameter)throws SQLException
	{
		int max=0;
		//取得排序值
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(devID) from vmc_system_parameter", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            max=cursor.getInt(0);// 返回总记录数
        }
     
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if(max>0)	        
	        {
	            // 执行添加商品		
	     		db.execSQL(
	     				"update vmc_system_parameter " +
	     				"set " +
	     				"demo=? " +
	     				"where devID=?" 
	     				,
	     		        new Object[] { tb_vmc_system_parameter.getDemo(),tb_vmc_system_parameter.getDevID()});
	     		
	            }		           
	        
	        // 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
	} 
	//修改支付宝类型是1.0还是2.0
	public void updatezhifubao(Tb_vmc_system_parameter tb_vmc_system_parameter)throws SQLException
	{
		int max=0;
		//取得排序值
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(devID) from vmc_system_parameter", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            max=cursor.getInt(0);// 返回总记录数
        }
     
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if(max>0)	        
	        {
	            // 执行添加商品		
	     		db.execSQL(
	     				"update vmc_system_parameter " +
	     				"set " +
	     				"zhifubaoer=? " +
	     				"where devID=?" 
	     				,
	     		        new Object[] { tb_vmc_system_parameter.getZhifubaoer(),tb_vmc_system_parameter.getDevID()});
	     		
	            }		           
	        
	        // 设置事务的标志为成功，如果不调用setTransactionSuccessful() 方法，默认会回滚事务。
		    db.setTransactionSuccessful();
	    } catch (Exception e) {
	        // process it
	        e.printStackTrace();
	    } finally {
	        // 会检查事务的标志是否为成功，如果为成功则提交事务，否则回滚事务
	        db.endTransaction();
	        if (!cursor.isClosed()) 
	 		{  
	 			cursor.close();  
	 		}  
	 		db.close(); 
	    }
	} 
	/**
     * 查找一条商品信息
     * 
     * @param id
     * @return
     */
    public Tb_vmc_system_parameter find() 
    {
    	Tb_vmc_system_parameter tb_vmc_system_parameter=null;
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select devID,devhCode,isNet,isfenClass,isbuyCar," +
 				"liebiaoKuan,mainPwd,amount,card,zhifubaofaca,zhifubaoer,weixing,printer," +
 				"language,rstTime,rstDay,baozhiProduct,emptyProduct,proSortType,marketAmount,billAmount," +
 				"event,demo from vmc_system_parameter", null );// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) 
        {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
            tb_vmc_system_parameter=new Tb_vmc_system_parameter(cursor.getString(cursor.getColumnIndex("devID")), cursor.getString(cursor.getColumnIndex("devhCode")),
    				cursor.getInt(cursor.getColumnIndex("isNet")),cursor.getInt(cursor.getColumnIndex("isfenClass")),cursor.getInt(cursor.getColumnIndex("isbuyCar")),cursor.getInt(cursor.getColumnIndex("liebiaoKuan")),
    				cursor.getString(cursor.getColumnIndex("mainPwd")),cursor.getInt(cursor.getColumnIndex("amount")), cursor.getInt(cursor.getColumnIndex("card")), cursor.getInt(cursor.getColumnIndex("zhifubaofaca")),
    				cursor.getInt(cursor.getColumnIndex("zhifubaoer")), cursor.getInt(cursor.getColumnIndex("weixing")), cursor.getInt(cursor.getColumnIndex("printer")), cursor.getInt(cursor.getColumnIndex("language")),
    				cursor.getString(cursor.getColumnIndex("rstTime")), cursor.getInt(cursor.getColumnIndex("rstDay")),  cursor.getInt(cursor.getColumnIndex("baozhiProduct")),  cursor.getInt(cursor.getColumnIndex("emptyProduct")),
    				cursor.getInt(cursor.getColumnIndex("proSortType")),cursor.getFloat(cursor.getColumnIndex("marketAmount")),cursor.getFloat(cursor.getColumnIndex("billAmount")),
    				cursor.getString(cursor.getColumnIndex("event")),cursor.getString(cursor.getColumnIndex("demo"))
    				
    		);
        }
        else
        {
        	
		}        
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return tb_vmc_system_parameter;// 如果没有信息，则返回null
    }
}
