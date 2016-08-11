package com.easivend.dao;

import java.util.ArrayList;
import java.util.List;

import com.easivend.model.Tb_vmc_cabinet;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class vmc_cabinetDAO
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_cabinetDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	//操作时，可以适当使用db.beginTransaction();  //开始事务 , db.endTransaction();    //结束事务   
	//添加柜数据
	public void add(Tb_vmc_cabinet tb_vmc_cabinet)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		// 开启一个事务
	    db.beginTransaction();
	    try {
			// 执行添加		
			db.execSQL("insert into vmc_cabinet (cabID,cabType) values (?,?)",
					new Object[] { tb_vmc_cabinet.getCabID(), tb_vmc_cabinet.getCabType() });
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
     * 获取全部柜信息
     *     
     * @return
     */
    public List<Tb_vmc_cabinet> getScrollData() 
    {
        List<Tb_vmc_cabinet> tb_inaccount = new ArrayList<Tb_vmc_cabinet>();// 创建集合对象
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 获取所有收入信息
        Cursor cursor = db.rawQuery("select cabID,cabType from vmc_cabinet", null);
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_cabinet(cursor.getString(cursor.getColumnIndex("cabID")),cursor.getInt(cursor.getColumnIndex("cabType"))));
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return tb_inaccount;// 返回集合
    }
    
    /**
     * 获取本柜信息
     *     
     * @return
     */
    public Tb_vmc_cabinet findScrollData(String cabID) 
    {
        Tb_vmc_cabinet tb_inaccount = null;
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 获取所有收入信息
        Cursor cursor = db.rawQuery("select cabID,cabType from vmc_cabinet where cabID=?", new String[] { cabID});
        //遍历所有的收入信息
        if (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount=new Tb_vmc_cabinet(cursor.getString(cursor.getColumnIndex("cabID")),cursor.getInt(cursor.getColumnIndex("cabType")));
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return tb_inaccount;// 返回集合
    }
    
    /**
     * 获取是否存在冰山机型
     *     
     * @return
     */
    public boolean findUBoxData() 
    {
    	boolean rst=false;
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 获取所有收入信息
        Cursor cursor = db.rawQuery("select cabID,cabType from vmc_cabinet where cabType=4", new String[] {});
        //遍历所有的收入信息
        if (cursor.moveToNext()) 
        {	
        	rst=true;
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
        return rst;// 返回集合
    }
    
    //删除该柜
  	public void detele(String cabID) 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 开启一个事务
  	    db.beginTransaction();
  	    try {
	          // 执行删除该柜商品表
	          db.execSQL("delete from vmc_cabinet where cabID=?", 
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
  	
    //删除该柜
  	public void deteleall() 
  	{       
          db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
          
        // 开启一个事务
	    db.beginTransaction();
	    try {
	          // 执行删除表
	          db.execSQL("delete from vmc_class"); 
	          db.execSQL("delete from vmc_classproduct"); 
	          db.execSQL("delete from vmc_product"); 
	          db.execSQL("delete from vmc_cabinet"); 
	          db.execSQL("delete from vmc_column");    
          
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
