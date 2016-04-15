/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_orderDAO 订单表操作文件  
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
import com.easivend.model.Tb_vmc_order_pay;
import com.easivend.model.Tb_vmc_order_product;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class vmc_orderDAO 
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_orderDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	
	//添加
	public void add(Tb_vmc_order_pay tb_vmc_order_pay,Tb_vmc_order_product tb_vmc_order_product)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		// 开启一个事务
	    db.beginTransaction();
	    try {
	        // 执行添加订单支付表	
	 		db.execSQL(
	 				"insert into vmc_order_pay" +
	 				"(" +
	 				"ordereID,payType,payStatus,RealStatus,smallNote,smallConi,smallAmount," +
	 				"smallCard,shouldPay,shouldNo,realNote,realCoin,realAmount,debtAmount,realCard,payTime,isupload" +
	 				") " +
	 				"values" +
	 				"(" +
	 				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(datetime('now', 'localtime')),0" +
	 				")",
	 		        new Object[] { tb_vmc_order_pay.getOrdereID(), tb_vmc_order_pay.getPayType(),tb_vmc_order_pay.getPayStatus(), tb_vmc_order_pay.getRealStatus(),
	 						tb_vmc_order_pay.getSmallNote(), tb_vmc_order_pay.getSmallConi(),tb_vmc_order_pay.getSmallAmount(), tb_vmc_order_pay.getSmallCard(),
	 						tb_vmc_order_pay.getShouldPay(), tb_vmc_order_pay.getShouldNo(), tb_vmc_order_pay.getRealNote(), tb_vmc_order_pay.getRealCoin(), 
	 						tb_vmc_order_pay.getRealAmount(), tb_vmc_order_pay.getDebtAmount(), tb_vmc_order_pay.getRealCard()});
	 		
			// 执行添加订单详细信息表	
	 		db.execSQL(
	 				"insert into vmc_order_product" +
	 				"(" +
	 				"orderID,productID,yujiHuo,realHuo,cabID,columnID,huoStatus" +
	 				") " +
	 				"values" +
	 				"(" +
	 				"?,?,?,?,?,?,?" +
	 				")",
	 		        new Object[] { tb_vmc_order_product.getOrderID(), tb_vmc_order_product.getProductID(),tb_vmc_order_product.getYujiHuo(), tb_vmc_order_product.getRealHuo(),
	 						tb_vmc_order_product.getCabID(), tb_vmc_order_product.getColumnID(),tb_vmc_order_product.getHuoStatus()});
	 	
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
		List<String> alllist=new ArrayList<String>(); 
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        //取得时间范围内订单支付单号
		Cursor cursor = db.rawQuery("select ordereID FROM [vmc_order_pay] where payTime between ? and ?", 
				new String[] { starttime,endtime });// 获取收入信息表中的最大编号
		while (cursor.moveToNext()) 
        {
			alllist.add(cursor.getString(cursor.getColumnIndex("ordereID")));
        }
		
		// 开启一个事务
	    db.beginTransaction();
	    try {
			for(int i=0;i<alllist.size();i++) 
			{	
				// 执行删除订单详细信息表	
		 		db.execSQL(
		 				"delete from vmc_order_product where orderID=?",
		 		        new Object[] { alllist.get(i)});
		 		// 执行添加订单支付表	
		 		db.execSQL(
		 				"delete from vmc_order_pay where ordereID=?",
		 		        new Object[] { alllist.get(i)});
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
	//删除所有的数据
	public void deteleall() 
	{   
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		// 开启一个事务
	    db.beginTransaction();
	    try {
	        // 执行删除订单详细信息表	
	 		db.execSQL(
	 				"delete from vmc_order_product");
	 		// 执行添加订单支付表	
	 		db.execSQL(
	 				"delete from vmc_order_pay"); 
	 		
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
	//查找所有的订单支付数据
	public List<Tb_vmc_order_pay> getScrollPay() 
	{   
		List<Tb_vmc_order_pay> tb_inaccount = new ArrayList<Tb_vmc_order_pay>();// 创建集合对象
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现在时刻是所有时间","log.txt");         
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        //取得时间范围内订单支付单号
		Cursor cursor = db.rawQuery("select ordereID,payType,payStatus,RealStatus,smallNote,smallConi,smallAmount," +
				"smallCard,shouldPay,shouldNo,realNote,realCoin,realAmount,debtAmount,realCard,payTime " +
				" FROM [vmc_order_pay] where isupload<>1", null);// 获取收入信息表中的最大编号
		while (cursor.moveToNext()) 
        {
			// 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_order_pay
        		(
        				cursor.getString(cursor.getColumnIndex("ordereID")), cursor.getInt(cursor.getColumnIndex("payType")),
        				cursor.getInt(cursor.getColumnIndex("payStatus")),cursor.getInt(cursor.getColumnIndex("RealStatus")),
        				cursor.getFloat(cursor.getColumnIndex("smallNote")),cursor.getFloat(cursor.getColumnIndex("smallConi")),
        				cursor.getFloat(cursor.getColumnIndex("smallAmount")),cursor.getFloat(cursor.getColumnIndex("smallCard")),
        				cursor.getFloat(cursor.getColumnIndex("shouldPay")), cursor.getInt(cursor.getColumnIndex("shouldNo")),
        				cursor.getFloat(cursor.getColumnIndex("realNote")),cursor.getFloat(cursor.getColumnIndex("realCoin")),
        				cursor.getFloat(cursor.getColumnIndex("realAmount")),cursor.getFloat(cursor.getColumnIndex("debtAmount")),
        				cursor.getFloat(cursor.getColumnIndex("realCard")),cursor.getString(cursor.getColumnIndex("payTime"))
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
	/**
     * 上报成功后，修改已上报状态
     * 
     * @return
     */
  	public void update(String orderno) 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象          
       // 开启一个事务
  	    db.beginTransaction();
  	    try {
          // 执行删除商品表
          db.execSQL("update [vmc_order_pay] set isupload=1 where ordereID=?", 
          		new Object[] { orderno});        
          
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
	public List<Tb_vmc_order_pay> getScrollPay(String starttime, String endtime) 
	{   
		List<Tb_vmc_order_pay> tb_inaccount = new ArrayList<Tb_vmc_order_pay>();// 创建集合对象
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现在时刻是"+starttime+",到"+endtime,"log.txt");         
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        //取得时间范围内订单支付单号
		Cursor cursor = db.rawQuery("select ordereID,payType,payStatus,RealStatus,smallNote,smallConi,smallAmount," +
				"smallCard,shouldPay,shouldNo,realNote,realCoin,realAmount,debtAmount,realCard,payTime " +
				" FROM [vmc_order_pay] where payTime between ? and ?", 
				new String[] { starttime,endtime });// 获取收入信息表中的最大编号
		while (cursor.moveToNext()) 
        {
			// 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_order_pay
        		(
        				cursor.getString(cursor.getColumnIndex("ordereID")), cursor.getInt(cursor.getColumnIndex("payType")),
        				cursor.getInt(cursor.getColumnIndex("payStatus")),cursor.getInt(cursor.getColumnIndex("RealStatus")),
        				cursor.getFloat(cursor.getColumnIndex("smallNote")),cursor.getFloat(cursor.getColumnIndex("smallConi")),
        				cursor.getFloat(cursor.getColumnIndex("smallAmount")),cursor.getFloat(cursor.getColumnIndex("smallCard")),
        				cursor.getFloat(cursor.getColumnIndex("shouldPay")), cursor.getInt(cursor.getColumnIndex("shouldNo")),
        				cursor.getFloat(cursor.getColumnIndex("realNote")),cursor.getFloat(cursor.getColumnIndex("realCoin")),
        				cursor.getFloat(cursor.getColumnIndex("realAmount")),cursor.getFloat(cursor.getColumnIndex("debtAmount")),
        				cursor.getFloat(cursor.getColumnIndex("realCard")),cursor.getString(cursor.getColumnIndex("payTime"))
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
	//查找时间范围内的订单详细信息数据
	public Tb_vmc_order_product getScrollProduct(String orderID) 
	{   
		Tb_vmc_order_product tb_inaccount = null;// 创建集合对象
             
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        //取得时间范围内订单支付单号
		Cursor cursor = db.rawQuery("select orderID,productID,yujiHuo,realHuo,cabID,columnID,huoStatus " +
				" FROM [vmc_order_product] where orderID = ?", 
				new String[] { orderID });// 获取收入信息表中的最大编号
		while (cursor.moveToNext()) 
        {
			// 将遍历到的收入信息添加到集合中
			tb_inaccount =new Tb_vmc_order_product
        		(
        				cursor.getString(cursor.getColumnIndex("orderID")), cursor.getString(cursor.getColumnIndex("productID")),
        				cursor.getInt(cursor.getColumnIndex("yujiHuo")),cursor.getInt(cursor.getColumnIndex("realHuo")),
        				cursor.getString(cursor.getColumnIndex("cabID")),cursor.getString(cursor.getColumnIndex("columnID")),
        				cursor.getInt(cursor.getColumnIndex("huoStatus"))
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
