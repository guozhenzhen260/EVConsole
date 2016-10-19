/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_columnDAO 货道表操作文件  
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
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class vmc_columnDAO 
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_columnDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	
	//修改货道提货码
	public void updatetihuo(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		//是否已经存在本商品
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// 根据编号查找支出信息，并存储到Cursor类中
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if (cursor.moveToNext()) // 遍历查找到的支出信息
	        {
	        	//执行修改货道数据
		 		db.execSQL(
					"update vmc_column set " +
					"tihuoPwd=?, " +
					"lasttime=(datetime('now', 'localtime')) " +
					"where cabID=? and columnID=?",
			        new Object[] { tb_vmc_column.getTihuoPwd(),
							tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});
			
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
	//添加或修改货道数据
	public void addorupdate(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		//是否已经存在本商品
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// 根据编号查找支出信息，并存储到Cursor类中
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if (cursor.moveToNext()) // 遍历查找到的支出信息
	        {
	        	//执行修改货道数据
		 		db.execSQL(
					"update vmc_column set " +
					"tihuoPwd=?,productID=?,pathCount=?,pathRemain=?,columnStatus=?," +
					"lasttime=(datetime('now', 'localtime')),isupload=0 " +
					"where cabID=? and columnID=?",
			        new Object[] { tb_vmc_column.getTihuoPwd(),tb_vmc_column.getProductID(),tb_vmc_column.getPathCount(),
							tb_vmc_column.getPathRemain(),tb_vmc_column.getColumnStatus(),
							tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});
			
			}	      
	        else
	        {	
	        	// 执行添加货道		
	     		db.execSQL(
	     				"insert into vmc_column" +
	     				"(" +
	     				"cabID,columnID,productID,pathCount,pathRemain," +
	     				"columnStatus,lasttime,isupload,tihuoPwd" +
	     				") " +
	     				"values" +
	     				"(" +
	     				"?,?,?,?,?,?,(datetime('now', 'localtime')),?,?" +
	     				")",
	     		        new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID(),tb_vmc_column.getProductID(),
	     						tb_vmc_column.getPathCount(),tb_vmc_column.getPathRemain(),tb_vmc_column.getColumnStatus(),
	     						tb_vmc_column.getIsupload(),tb_vmc_column.getTihuoPwd()});
	     		
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
	//为服务器设备下载添加或修改货道数据
	public void addorupdateforserver(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		//是否已经存在本商品
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// 根据编号查找支出信息，并存储到Cursor类中
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        if (cursor.moveToNext()) // 遍历查找到的支出信息
	        {
	        	//执行修改货道数据
		 		db.execSQL(
					"update vmc_column set " +
					"productID=?,pathCount=?,path_id=?," +
					"isupload=0 " +
					"where cabID=? and columnID=?",
			        new Object[] { tb_vmc_column.getProductID(),tb_vmc_column.getPathCount(),
							tb_vmc_column.getPath_id(),
							tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});
			
			}	      
	        else
	        {	
	        	// 执行添加货道		
	     		db.execSQL(
	     				"insert into vmc_column" +
	     				"(" +
	     				"cabID,columnID,productID,pathCount,pathRemain," +
	     				"columnStatus,lasttime,path_id,isupload" +
	     				") " +
	     				"values" +
	     				"(" +
	     				"?,?,?,?,?,?,(datetime('now', 'localtime')),?,?" +
	     				")",
	     		        new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID(),tb_vmc_column.getProductID(),
	     						tb_vmc_column.getPathCount(),0,3,tb_vmc_column.getPath_id(),tb_vmc_column.getIsupload()});
	     		
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
	//删除单条
	public void detele(Tb_vmc_column tb_vmc_column) 
	{       
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        // 执行删除商品表
	        db.execSQL("delete from vmc_column where cabID=? and columnID=?", 
	        		new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});        

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
	//删除该柜全部货道信息
  	public void deteleCab(String cabID) 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 开启一个事务
  	    db.beginTransaction();
  	    try {
          // 执行删除该柜商品表
          db.execSQL("delete from vmc_column where cabID=?", 
          		new Object[] { cabID});    
          
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
    //布满该柜全部货道存货数量
  	public void buhuoCab(String cabID) 
  	{       
            db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
            // 开启一个事务
    	    db.beginTransaction();
    	    try {
	          // 执行布满该柜货道表
	          db.execSQL("update vmc_column set pathRemain=pathCount,columnStatus=1,isupload=0,lasttime=(datetime('now', 'localtime')) where cabID=? and columnStatus<>2 ", 
	          		new Object[] { cabID});    
          
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
    //清空该柜全部货道存货数量
  	public void clearhuoCab(String cabID) 
  	{       
            db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
            // 开启一个事务
    	    db.beginTransaction();
    	    try {
	          // 执行布满该柜货道表
	          db.execSQL("update vmc_column set pathRemain=0,columnStatus=3,isupload=0 where cabID=? and columnStatus<>2 ", 
	          		new Object[] { cabID});    
          
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
	/**
     * 查找一条货道信息
     * 
     * @param id
     * @return
     */
    public Tb_vmc_column find(String cabID,String columnID) 
    {
    	Tb_vmc_column tb_vmc_column=null;
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
            		"columnStatus,lasttime,path_id,isupload,tihuoPwd from vmc_column where cabID=? and columnID=?", 
            		new String[] { cabID,columnID });// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
        	tb_vmc_column=new Tb_vmc_column(
    				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
    				cursor.getString(cursor.getColumnIndex("tihuoPwd")),
    				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
    				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
    				cursor.getString(cursor.getColumnIndex("lasttime")),cursor.getInt(cursor.getColumnIndex("path_id")),
    				cursor.getInt(cursor.getColumnIndex("isupload"))
    		);
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return tb_vmc_column;// 如果没有信息，则返回null
    }
    
    /**
     * 查找所有未上报的货道信息
     * 
     * @param id
     * @return
     */
    public List<Tb_vmc_column> getScrollPay() 
    {
    	List<Tb_vmc_column> tb_vmc_column = new ArrayList<Tb_vmc_column>();// 创建集合对象
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
            		"columnStatus,lasttime,path_id,isupload,tihuoPwd from vmc_column where isupload <> 1 ", null);// 根据编号查找支出信息，并存储到Cursor类中
        while (cursor.moveToNext()) {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
        	tb_vmc_column.add(new Tb_vmc_column
        			(
    				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
    				cursor.getString(cursor.getColumnIndex("tihuoPwd")),
    				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
    				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
    				cursor.getString(cursor.getColumnIndex("lasttime")),cursor.getInt(cursor.getColumnIndex("path_id")),
    				cursor.getInt(cursor.getColumnIndex("isupload"))
    				)
    		);
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return tb_vmc_column;// 如果没有信息，则返回null
    }
    /**
     * 上报成功后，修改已上报状态
     * 
     * @return
     */
  	public void updatecol(String cabID,String columnID) 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象     
       // 开启一个事务
  	    db.beginTransaction();
  	    try {
          // 执行删除商品表
          db.execSQL("update [vmc_column] set isupload=1 where cabID=? and columnID=?", 
            		new String[] { cabID,columnID });        

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
    
    /**
     * 获取所有条数商品信息
     * 
     * @param cabID
     *            柜号    
     * @return
     */
    public List<Tb_vmc_column> getScrollData(String cabID) 
    {
        List<Tb_vmc_column> tb_inaccount = new ArrayList<Tb_vmc_column>();// 创建集合对象
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
        		"columnStatus,lasttime,path_id,isupload,tihuoPwd from vmc_column where cabID=? ", 
        		new String[] { cabID});// 根据编号查找支出信息，并存储到Cursor类中
    
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_column
        		(
        				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
        				cursor.getString(cursor.getColumnIndex("tihuoPwd")),
        				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
        				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
        				cursor.getString(cursor.getColumnIndex("lasttime")),cursor.getInt(cursor.getColumnIndex("path_id")),
        				cursor.getInt(cursor.getColumnIndex("isupload"))
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
     * 获取存货数量
     * 
     * @return
     */
    public int getproductCount(String productID) { 
    	int count=0;
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select pathRemain from vmc_column where (tihuoPwd is null or tihuoPwd=='') and  productID=?", 
        		new String[] { productID});// 根据编号查找支出信息，并存储到Cursor类中
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
        	count+=cursor.getInt(cursor.getColumnIndex("pathRemain"));           
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return count;// 如果没有数据，则返回0
    }
    
    /**
     * 获取出货货道
     * 
     * @return
     */
    public List<String> getproductColumn(String productID) {
    	List<String> alllist=new ArrayList<String>();
    	
    	db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select cabID,columnID from vmc_column where (tihuoPwd is null or tihuoPwd=='') and pathRemain>0 and productID=? order by lasttime asc", 
        		new String[] { productID});// 根据编号查找支出信息，并存储到Cursor类中
        //遍历所有的收入信息
        if (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
        	alllist.add(cursor.getString(cursor.getColumnIndex("cabID")));//柜号
        	alllist.add(cursor.getString(cursor.getColumnIndex("columnID")));//货道号
        	Cursor cursor2 = db.rawQuery("select cabType from vmc_cabinet where cabID=?", 
            		new String[] { cursor.getString(cursor.getColumnIndex("cabID"))});// 根据编号查找支出信息，并存储到Cursor类中
        	if (cursor2.moveToNext()) 
        	{
        		alllist.add(String.valueOf(cursor2.getInt(cursor2.getColumnIndex("cabType"))));//柜类型        		
        	}
        	if (!cursor2.isClosed()) 
     		{  
     			cursor2.close();  
     		} 
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}         
 		db.close();
        return alllist;// 如果没有数据，则返回0
    }
    
    /**
     * 获取指定出货货道的商品信息
     * 
     * @return
     */
    public Tb_vmc_product getColumnproduct(String cabID,String columnID) {
    	String productID=null;
    	db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select productID from vmc_column where pathRemain>0 and cabID=? and columnID=?", 
        		new String[] { cabID,columnID});// 根据编号查找支出信息，并存储到Cursor类中    	
		
        //遍历所有的收入信息
        if (cursor.moveToNext()) 
        {	
        	productID=cursor.getString(cursor.getColumnIndex("productID"));//商品ID
        	Cursor cursor2 = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productID = ?", new String[] { String.valueOf(productID) });// 根据编号查找支出信息，并存储到Cursor类中
	        if (cursor2.moveToNext()) 
	        {// 遍历查找到的支出信息
	
	            // 将遍历到的支出信息存储到Tb_outaccount类中
	        	return new Tb_vmc_product(
	    				cursor2.getString(cursor2.getColumnIndex("productID")), cursor2.getString(cursor2.getColumnIndex("productName")),
	    				cursor2.getString(cursor2.getColumnIndex("productDesc")),cursor2.getFloat(cursor2.getColumnIndex("marketPrice")),
	    				cursor2.getFloat(cursor2.getColumnIndex("salesPrice")),cursor2.getInt(cursor2.getColumnIndex("shelfLife")),
	    				cursor2.getString(cursor2.getColumnIndex("downloadTime")),cursor2.getString(cursor2.getColumnIndex("onloadTime")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch1")), cursor2.getString(cursor2.getColumnIndex("attBatch2")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch3")),cursor2.getInt(cursor2.getColumnIndex("paixu")),
	    				cursor2.getInt(cursor2.getColumnIndex("isdelete"))
	    		);
	        }
	        if (!cursor2.isClosed()) 
     		{  
     			cursor2.close();  
     		} 
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}         
 		db.close();
 		return null;// 如果没有数据，则返回0
    }
    /**
     * 获取指定出货货道的商品信息,包括存货余量为0的
     * 
     * @return
     */
    public Tb_vmc_product getColumnproductforzero(String cabID,String columnID) {
    	String productID=null;
    	db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
    	Cursor cursor = db.rawQuery("select productID from vmc_column where pathRemain=0 and cabID=? and columnID=?", 
        		new String[] { cabID,columnID});// 根据编号查找支出信息，并存储到Cursor类中    	
		
        //遍历所有的收入信息
        if (cursor.moveToNext())  
        {	
        	productID=cursor.getString(cursor.getColumnIndex("productID"));//商品ID
        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+productID+"余量=0","log.txt");
        	
        	Cursor cursor2 = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productID = ?", new String[] { String.valueOf(productID) });// 根据编号查找支出信息，并存储到Cursor类中
	        if (cursor2.moveToNext()) 
	        {// 遍历查找到的支出信息
	
	            // 将遍历到的支出信息存储到Tb_outaccount类中
	        	return new Tb_vmc_product(
	    				cursor2.getString(cursor2.getColumnIndex("productID")), cursor2.getString(cursor2.getColumnIndex("productName")),
	    				cursor2.getString(cursor2.getColumnIndex("productDesc")),cursor2.getFloat(cursor2.getColumnIndex("marketPrice")),
	    				cursor2.getFloat(cursor2.getColumnIndex("salesPrice")),cursor2.getInt(cursor2.getColumnIndex("shelfLife")),
	    				cursor2.getString(cursor2.getColumnIndex("downloadTime")),cursor2.getString(cursor2.getColumnIndex("onloadTime")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch1")), cursor2.getString(cursor2.getColumnIndex("attBatch2")),
	    				cursor2.getString(cursor2.getColumnIndex("attBatch3")),cursor2.getInt(cursor2.getColumnIndex("paixu")),
	    				cursor2.getInt(cursor2.getColumnIndex("isdelete"))
	    		);
	        }
	        if (!cursor2.isClosed()) 
     		{  
     			cursor2.close();  
     		} 
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}         
 		db.close();
 		return null;// 如果没有数据，则返回0
    }
    
    /**
     * 获取指定出货货道的货道类型
     * 
     * @return
     */
    public String getcolumnType(String cabID) {
    	String alllist=null;    	
    	db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
    	Cursor cursor2 = db.rawQuery("select cabType from vmc_cabinet where cabID=?", 
        		new String[] { String.valueOf(cabID)});// 根据编号查找支出信息，并存储到Cursor类中
    	if (cursor2.moveToNext()) 
    	{
    		alllist=String.valueOf(cursor2.getInt(cursor2.getColumnIndex("cabType")));//柜类型        		
    	}
    	if (!cursor2.isClosed()) 
 		{  
 			cursor2.close();  
 		}                 
 		db.close();
        return alllist;// 如果没有数据，则返回0
    }
    
    /**
     * 出货后，修改出货货道存货数量
     * 
     * @return
     */
  	public void update(String cabID,String columnID) 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象          
	       // 开启一个事务
	  	  db.beginTransaction();
	  	  try {
	          // 执行删除商品表
	          db.execSQL("update vmc_column set pathRemain=(pathRemain-1),isupload=0,lasttime=(datetime('now', 'localtime')) where cabID=? and columnID=?", 
	          		new Object[] { cabID,columnID});    
	          Cursor cursor = db.rawQuery("select pathRemain from vmc_column where cabID=? and columnID=?", 
	          		new String[] { cabID,columnID}); // 根据编号查找支出信息，并存储到Cursor类中
	          //遍历所有的收入信息
	          if (cursor.moveToNext()) 
	          {
	        	  int pathRemain=cursor.getInt(cursor.getColumnIndex("pathRemain"));
	        	  if(pathRemain==0)
	        	  {
	        		  db.execSQL("update vmc_column set columnStatus=3 where cabID=? and columnID=?", 
	        	          		new Object[] { cabID,columnID}); 
	        	  }
	          }
	          if(!cursor.isClosed()) 
	   		 {  
	        	cursor.close();  
	   		 } 
	       
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
	
}
