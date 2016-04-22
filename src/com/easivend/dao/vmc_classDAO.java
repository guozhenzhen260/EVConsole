/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_classDAO 柜类型操作文件  
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.dao;

import java.util.ArrayList;
import java.util.List;

import com.easivend.model.Tb_vmc_class;



import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class vmc_classDAO 
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_classDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	//添加
	public void add(Tb_vmc_class tb_vmc_class)throws SQLException
	{
		
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		// 开启一个事务
	    db.beginTransaction();
	    try {
			// 执行添加		
			db.execSQL("insert into vmc_class (classID,className,classTime,attBatch1) values (?,?,(datetime('now', 'localtime')),?)",
			        new Object[] { tb_vmc_class.getClassID(), tb_vmc_class.getClassName(), tb_vmc_class.getAttBatch1() });
			
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
    //修改
	public void update(Tb_vmc_class tb_vmc_class) {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 开启一个事务
	    db.beginTransaction();
	    try {
	        // 执行修改
	        db.execSQL("update vmc_class set className = ?,classTime=(datetime('now', 'localtime')),attBatch1= ? where classID = ?", 
	        		new Object[] { tb_vmc_class.getClassName(),tb_vmc_class.getAttBatch1(),tb_vmc_class.getClassID()});
	        
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
	//添加或者修改
	public void addorupdate(Tb_vmc_class tb_vmc_class) {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select classID from vmc_class where classID=?", new String[] { tb_vmc_class.getClassID()});// 获取收入信息表中的最大编号
        if (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
        	update(tb_vmc_class);//执行修改
        }
        else {
        	add(tb_vmc_class);//执行添加
		}
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
        db.close(); 
	}
	//删除单条
	public void detele(Tb_vmc_class tb_vmc_class) 
	{           
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        //是否在商品分类关联表上有关联
  		Cursor cursor = db.rawQuery("select classID from vmc_classproduct where classID=?", new String[] { tb_vmc_class.getClassID()});// 获取收入信息表中的最大编号
  		// 开启一个事务
	    db.beginTransaction();
	    try {
	  		// 有关联商品，删除商品分类关联表相关	    	
	  		if (cursor.moveToLast()) 
	  		{
		        // 执行删除收入信息操作
		        db.execSQL("delete from vmc_classproduct where classID = ?", 
		        		new Object[] { tb_vmc_class.getClassID()});
	        }	
	  		//删除分类表相关	
	        {
		        // 执行删除收入信息操作
		        db.execSQL("delete from vmc_class where classID = ?", 
		        		new Object[] { tb_vmc_class.getClassID()});
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
     * 通过ID刪除多条信息
     * 
     * @param ids
     */
    public void detele(StringBuffer... ids) 
    {
    	// 判断是否存在要删除的id
        if (ids.length > 0) 
        {
            StringBuffer sb = new StringBuffer();// 创建StringBuffer对象
            for (int i = 0; i < ids.length; i++) 
            {// 遍历要删除的id集合

                sb.append('?').append(',');// 将删除条件添加到StringBuffer对象中
            }
            sb.deleteCharAt(sb.length() - 1);// 去掉最后一个“,“字符
            db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
            // 开启一个事务
    	    db.beginTransaction();
    	    try {
	            // 执行删除收入信息操作
	            db.execSQL("delete from vmc_class where classID in (" + sb + ")", (Object[]) ids);
	            
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

    /**
     * 获取指定条数商品类别信息
     * 
     * @param start
     *            起始位置
     * @param count
     *            每页显示数量
     * @return
     */
    public List<Tb_vmc_class> getScrollData(int start, int count) 
    {
        List<Tb_vmc_class> tb_inaccount = new ArrayList<Tb_vmc_class>();// 创建集合对象
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 获取所有收入信息
        Cursor cursor = db.rawQuery("select classID,className,classTime,attBatch1 from vmc_class limit ?,?", new String[] { String.valueOf(start), String.valueOf(count) });
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_class(cursor.getString(cursor.getColumnIndex("classID")), cursor.getString(cursor.getColumnIndex("className")),
            		cursor.getString(cursor.getColumnIndex("classTime")),cursor.getString(cursor.getColumnIndex("attBatch1"))));
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return tb_inaccount;// 返回集合
    }

    /**
     * 获取总记录数
     * 
     * @return
     */
    public long getCount() {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(classID) from vmc_class", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            return cursor.getLong(0);// 返回总记录数
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return 0;// 如果没有数据，则返回0
    }

    /**
     * 获取收入最大编号
     * 
     * @return
     */
    public int getMaxId() {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select max(classID) from vmc_class", null);// 获取收入信息表中的最大编号
        while (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
            return cursor.getInt(0);// 获取访问到的数据，即最大编号
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return 0;// 如果没有数据，则返回0
    }
	
}
