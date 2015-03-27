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
import com.easivend.model.Tb_vmc_system_parameter;

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
	
	//添加或修改货道数据
	public void addorupdate(Tb_vmc_column tb_vmc_column)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		//是否已经存在本商品
        Cursor cursor = db.rawQuery("select columnID from vmc_column where cabID = ? and columnID=?", 
        		new String[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID() });// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) // 遍历查找到的支出信息
        {
        	//执行修改货道数据
	 		db.execSQL(
				"update vmc_column set " +
				"productID=?,pathCount=?,pathRemain=?,columnStatus=?," +
				"lasttime=(datetime('now', 'localtime')) " +
				"where cabID=? and columnID=?",
		        new Object[] { tb_vmc_column.getProductID(),tb_vmc_column.getPathCount(),
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
     				"columnStatus,lasttime" +
     				") " +
     				"values" +
     				"(" +
     				"?,?,?,?,?,?,(datetime('now', 'localtime'))" +
     				")",
     		        new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID(),tb_vmc_column.getProductID(),
     						tb_vmc_column.getPathCount(),tb_vmc_column.getPathRemain(),tb_vmc_column.getColumnStatus()});
     		
        }
        
        
        
 		if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
	}
	//删除单条
	public void detele(Tb_vmc_column tb_vmc_column) 
	{       
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 执行删除商品表
        db.execSQL("delete from vmc_column where cabID=? and columnID=?", 
        		new Object[] { tb_vmc_column.getCabineID(),tb_vmc_column.getColumnID()});        
        db.close(); 
	}
	//删除该柜全部货道信息
  	public void deteleCab(String cabID) 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
          // 执行删除该柜商品表
          db.execSQL("delete from vmc_column where cabID=?", 
          		new Object[] { cabID});    
          
          db.close(); 
  	}		
	/**
     * 查找一条商品信息
     * 
     * @param id
     * @return
     */
    public Tb_vmc_column find(String cabID,String columnID) 
    {
    	Tb_vmc_column tb_vmc_column=null;
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select cabID,columnID,productID,pathCount,pathRemain," +
            		"columnStatus,lasttime from vmc_column where cabID=? and columnID=?", 
            		new String[] { cabID,columnID });// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
        	tb_vmc_column=new Tb_vmc_column(
    				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
    				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
    				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
    				cursor.getString(cursor.getColumnIndex("lasttime"))
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
        		"columnStatus,lasttime from vmc_column where cabID=? ", 
        		new String[] { cabID});// 根据编号查找支出信息，并存储到Cursor类中
    
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_column
        		(
        				cursor.getString(cursor.getColumnIndex("cabID")), cursor.getString(cursor.getColumnIndex("columnID")),
        				cursor.getString(cursor.getColumnIndex("productID")),cursor.getInt(cursor.getColumnIndex("pathCount")),
        				cursor.getInt(cursor.getColumnIndex("pathRemain")),cursor.getInt(cursor.getColumnIndex("columnStatus")),
        				cursor.getString(cursor.getColumnIndex("lasttime"))
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
