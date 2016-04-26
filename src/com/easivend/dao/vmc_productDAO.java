/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           EVprotocol.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_productDAO 商品表操作文件  
**------------------------------------------------------------------------------------------------------
** Created by:          yanbo 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.easivend.common.ToolClass;
import com.easivend.model.Tb_vmc_product;


public class vmc_productDAO
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_productDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	//添加
	public void add(Tb_vmc_product tb_vmc_product,String classID)throws SQLException
	{
		int max=0;
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		//取得排序值
		Cursor cursor = db.rawQuery("select max(paixu) from vmc_product", null);// 获取收入信息表中的最大编号
        if (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
        	max=cursor.getInt(0); 
        }
        // 开启一个事务
	    db.beginTransaction();
	    try {
		
	        // 执行添加商品		
	 		db.execSQL(
	 				"insert into vmc_product" +
	 				"(" +
	 				"productID,productName,productDesc,marketPrice,salesPrice," +
	 				"shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3,paixu,isdelete" +
	 				") " +
	 				"values" +
	 				"(" +
	 				"?,?,?,?,?,?,(datetime('now', 'localtime')),(datetime('now', 'localtime')),?,?,?,?,?" +
	 				")",
	 		        new Object[] { tb_vmc_product.getProductID(), tb_vmc_product.getProductName(),tb_vmc_product.getProductDesc(), tb_vmc_product.getMarketPrice(),
	 						tb_vmc_product.getSalesPrice(), tb_vmc_product.getShelfLife(),tb_vmc_product.getAttBatch1(), tb_vmc_product.getAttBatch2(),
	 						tb_vmc_product.getAttBatch3(), max+1,tb_vmc_product.getIsdelete()});
	 		
	 		// 执行添加商品与类别关联表	
	 		if(classID.equals("")!=true)
	 		{
			db.execSQL(
					"insert into vmc_classproduct" +
					"(" +
					"classID,productID,classTime" +
					") " +
					"values" +
					"(" +
					"?,?,(datetime('now', 'localtime'))" +
					")",
			        new Object[] { classID,tb_vmc_product.getProductID()});
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
    //修改
	public void update(Tb_vmc_product tb_vmc_product,String classID)throws SQLException
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象		
        // 执行修改商品		
 		db.execSQL(
 				"update vmc_product set " +
 				"productName=?,productDesc=?,marketPrice=?,salesPrice=?,shelfLife=?," +
 				"downloadTime=(datetime('now', 'localtime')),onloadTime=(datetime('now', 'localtime'))," +
 				"attBatch1=?,attBatch2=?,attBatch3=? " +
 				"where productID=?",
 		        new Object[] { tb_vmc_product.getProductName(),tb_vmc_product.getProductDesc(), tb_vmc_product.getMarketPrice(),
 						tb_vmc_product.getSalesPrice(), tb_vmc_product.getShelfLife(),tb_vmc_product.getAttBatch1(), tb_vmc_product.getAttBatch2(),
 						tb_vmc_product.getAttBatch3(),tb_vmc_product.getProductID()});
 		
 		// 执行添加商品与类别关联表	
 		//查找原先商品ID对应的类别ID
 		String clsID=findclass(tb_vmc_product.getProductID()); 	
 		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象		
 		// 开启一个事务
	    db.beginTransaction();
	    try {
	 		//如果需要进行商品分类
	 		if(classID.equals("")!=true)
	 		{
	 			if(clsID.isEmpty()==true)//如果原先没有商品分类
	 			{
	 				db.execSQL(
	 						"insert into vmc_classproduct" +
	 						"(" +
	 						"classID,productID,classTime" +
	 						") " +
	 						"values" +
	 						"(" +
	 						"?,?,(datetime('now', 'localtime'))" +
	 						")",
	 				        new Object[] { classID,tb_vmc_product.getProductID()});
	 			}
	 			//如果原先有商品分类
	 			else 
	 			{
					db.execSQL(
							"update vmc_classproduct set " +
							"classID=?,classTime=(datetime('now', 'localtime')) " +
							"where productID=?",
					        new Object[] { classID,tb_vmc_product.getProductID()});
	 			}
	 		}	
	 		//不需要进行商品分类
	 		else
	 		{
	 			if(clsID.isEmpty()!=true)//如果原先有商品分类
	 			{
	 				db.execSQL(
							"delete from vmc_classproduct " +
							"where productID=?",
					        new Object[] { tb_vmc_product.getProductID()});
	 			}
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
	//添加或者修改
	public void addorupdate(Tb_vmc_product tb_vmc_product,String classID) {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select productID from vmc_product where productID=?", new String[] { tb_vmc_product.getProductID()});// 获取收入信息表中的最大编号
        if (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
        	update(tb_vmc_product,classID);//执行修改
        }
        else {
        	add(tb_vmc_product,classID);//执行添加
		}
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
        db.close(); 
	}
	//删除单条
	public void detele(Tb_vmc_product tb_vmc_product) 
	{       
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        //是否在货道表上有关联
  		Cursor cursor = db.rawQuery("select productID from vmc_column where productID=?", new String[] { tb_vmc_product.getProductID()});// 获取收入信息表中的最大编号
  		// 开启一个事务
	    db.beginTransaction();
	    try {
	  		// 没有关联货道，可以删除
	  		if (!cursor.moveToLast()) 
	        {
	  			// 执行删除商品表
	  	        db.execSQL("delete from vmc_product where productID=?", 
	  	        		new Object[] { tb_vmc_product.getProductID()});
	  	        //删除商品分类关联表
	  	        db.execSQL(
	  					"delete from vmc_classproduct " +
	  					"where productID=?",
	  			        new Object[] { tb_vmc_product.getProductID()});
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
//    	// 判断是否存在要删除的id
//        if (ids.length > 0) 
//        {
//            StringBuffer sb = new StringBuffer();// 创建StringBuffer对象
//            for (int i = 0; i < ids.length; i++) 
//            {// 遍历要删除的id集合
//
//                sb.append('?').append(',');// 将删除条件添加到StringBuffer对象中
//            }
//            sb.deleteCharAt(sb.length() - 1);// 去掉最后一个“,“字符
//            db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
//            // 执行删除收入信息操作
//            db.execSQL("delete from vmc_class where classID in (" + sb + ")", (Object[]) ids);
//        }
    }
    
    /**
     * 上移或下移排序序号
     * 
     * @param type=1上移,2下移
     */
  	public void sortupdown(Tb_vmc_product tb_vmc_product,int type) 
  	{   
  		int index=0,max=0;
  		Cursor cursor = null;
  		String nextid="";
  		
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
	    //取得当前排序值
	  	cursor = db.rawQuery("select paixu from vmc_product where productID=?", 
        		 new String[] { tb_vmc_product.getProductID()});// 获取收入信息表中的最大编号
	    if (cursor.moveToLast()) 
	    {// 访问Cursor中的最后一条数据
	    	index=cursor.getInt(0); 
	    }
	    
	    // 开启一个事务
	    db.beginTransaction();
	    try {
		    //上移
		    if(type==1)
		    {
		    	//不是第一个值
		    	if(index>0)
		    	{
		    		//取得上一排序值的id
		    	  	cursor = db.rawQuery("select productID from vmc_product where paixu=?", 
		            		 new String[] { String.valueOf(index-1)});// 获取收入信息表中的最大编号
		    	    if (cursor.moveToLast()) 
		    	    {// 访问Cursor中的最后一条数据
		    	    	nextid=cursor.getString(0); 
		    	    }
		    	    //上一个值+1
		    	    db.execSQL(
							"update vmc_product set " +
							"paixu=? " +
							"where productID=?",
					        new Object[] { index,nextid});
		    	    //本值-1
		    	    db.execSQL(
							"update vmc_product set " +
							"paixu=? " +
							"where productID=?",
					        new Object[] { index-1,tb_vmc_product.getProductID()});
		    	}
		    }
		    //下移
		    else 
		    {
		    	//取得最大排序值
		  		cursor = db.rawQuery("select max(paixu) from vmc_product", null);// 获取收入信息表中的最大编号
		          if (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
		          	max=cursor.getInt(0); 
		          }
		        //不是最后一个值  
		        if(index<max)
		        {
		    		//取得下一排序值的id
		    	  	cursor = db.rawQuery("select productID from vmc_product where paixu=?", 
		            		 new String[] { String.valueOf(index+1)});// 获取收入信息表中的最大编号
		    	    if (cursor.moveToLast()) 
		    	    {// 访问Cursor中的最后一条数据
		    	    	nextid=cursor.getString(0); 
		    	    }
		    	    //下一个值-1
		    	    db.execSQL(
							"update vmc_product set " +
							"paixu=? " +
							"where productID=?",
					        new Object[] { index,nextid});
		    	    //本值+1
		    	    db.execSQL(
							"update vmc_product set " +
							"paixu=? " +
							"where productID=?",
					        new Object[] { index+1,tb_vmc_product.getProductID()});
		    	}
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
    public Tb_vmc_product find(String productID) 
    {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productID = ?", new String[] { String.valueOf(productID) });// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
            return new Tb_vmc_product(
    				cursor.getString(cursor.getColumnIndex("productID")), cursor.getString(cursor.getColumnIndex("productName")),
    				cursor.getString(cursor.getColumnIndex("productDesc")),cursor.getFloat(cursor.getColumnIndex("marketPrice")),
    				cursor.getFloat(cursor.getColumnIndex("salesPrice")),cursor.getInt(cursor.getColumnIndex("shelfLife")),
    				cursor.getString(cursor.getColumnIndex("downloadTime")),cursor.getString(cursor.getColumnIndex("onloadTime")),
    				cursor.getString(cursor.getColumnIndex("attBatch1")), cursor.getString(cursor.getColumnIndex("attBatch2")),
    				cursor.getString(cursor.getColumnIndex("attBatch3")),cursor.getInt(cursor.getColumnIndex("paixu")),
    				cursor.getInt(cursor.getColumnIndex("isdelete"))
    		);
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return null;// 如果没有信息，则返回null
    }
    
    /**
     * 查找一条商品对应的类别的信息
     * 
     * @param id
     * @return
     */
    public String findclass(String productID) 
    {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select classID from vmc_classproduct where productID = ?", new String[] { String.valueOf(productID) });// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
            return cursor.getString(cursor.getColumnIndex("classID"));    		
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return "";// 如果没有信息，则返回null
    }

    /**
     * 获取指定条数商品信息
     * 
     * @param start
     *            起始位置
     * @param count
     *            每页显示数量
     *        datasort排序方法    
     * @return
     */
    public List<Tb_vmc_product> getScrollData(int start, int count,String datasort) 
    {
        List<Tb_vmc_product> tb_inaccount = new ArrayList<Tb_vmc_product>();// 创建集合对象
        Cursor cursor = null;
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品sort="+datasort,"log.txt");
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        if(datasort.equals("sale"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by paixu asc", null);
        }
        else if(datasort.equals("marketPrice"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by marketPrice asc", null);
        }
        else if(datasort.equals("salesPrice"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by salesPrice asc", null);
        }
        else if(datasort.equals("shelfLife"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by (datetime('now', 'localtime'))-onloadTime asc", null);
        }
        else if(datasort.equals("colucount"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by paixu asc", null);
        }
        else if(datasort.equals("onloadTime"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by onloadTime asc", null);
        }
        else if(datasort.equals("shoudong"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product order by paixu asc", null);
        }
       
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_product
        		(
        				cursor.getString(cursor.getColumnIndex("productID")), cursor.getString(cursor.getColumnIndex("productName")),
        				cursor.getString(cursor.getColumnIndex("productDesc")),cursor.getFloat(cursor.getColumnIndex("marketPrice")),
        				cursor.getFloat(cursor.getColumnIndex("salesPrice")),cursor.getInt(cursor.getColumnIndex("shelfLife")),
        				cursor.getString(cursor.getColumnIndex("downloadTime")),cursor.getString(cursor.getColumnIndex("onloadTime")),
        				cursor.getString(cursor.getColumnIndex("attBatch1")), cursor.getString(cursor.getColumnIndex("attBatch2")),
        				cursor.getString(cursor.getColumnIndex("attBatch3")),cursor.getInt(cursor.getColumnIndex("paixu")),
        				cursor.getInt(cursor.getColumnIndex("isdelete"))
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
     * 重载函数，获取指定搜索条件商品信息
     * 
     * @param param搜索条件
     *        datasort排序方法    
     * 
     *            
     * @return
     */
    public List<Tb_vmc_product> getScrollData(String param,String datasort) 
    {
    	String params="";
        List<Tb_vmc_product> tb_inaccount = new ArrayList<Tb_vmc_product>();// 创建集合对象
        Cursor cursor = null;
        params="%"+param+"%";
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productName="+params+" sort="+datasort,"log.txt");
        
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        if(datasort.equals("sale"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ?  order by paixu asc", new String[] { params });
        }
        else if(datasort.equals("marketPrice"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ? order by marketPrice asc", new String[] { params });
        }
        else if(datasort.equals("salesPrice"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ? order by salesPrice asc", new String[] { params });
        }
        else if(datasort.equals("shelfLife"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ?  order by (datetime('now', 'localtime'))-onloadTime asc", new String[] { params });
        }
        else if(datasort.equals("colucount"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ? order by paixu asc", new String[] { params });
        }
        else if(datasort.equals("onloadTime"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ? order by onloadTime asc", new String[] { params });
        }
        else if(datasort.equals("shoudong"))
        {
        	// 获取所有收入信息
            cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
            		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
            		"paixu,isdelete from vmc_product where productName like ? order by paixu asc", new String[] { params });
        }
        
        
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_product
        		(
        				cursor.getString(cursor.getColumnIndex("productID")), cursor.getString(cursor.getColumnIndex("productName")),
        				cursor.getString(cursor.getColumnIndex("productDesc")),cursor.getFloat(cursor.getColumnIndex("marketPrice")),
        				cursor.getFloat(cursor.getColumnIndex("salesPrice")),cursor.getInt(cursor.getColumnIndex("shelfLife")),
        				cursor.getString(cursor.getColumnIndex("downloadTime")),cursor.getString(cursor.getColumnIndex("onloadTime")),
        				cursor.getString(cursor.getColumnIndex("attBatch1")), cursor.getString(cursor.getColumnIndex("attBatch2")),
        				cursor.getString(cursor.getColumnIndex("attBatch3")),cursor.getInt(cursor.getColumnIndex("paixu")),
        				cursor.getInt(cursor.getColumnIndex("isdelete"))
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
     * 重载函数，获取指定类型商品信息
     * 
     * @param param搜索条件
     *        datasort排序方法    
     * 
     *            
     * @return
     */
    public List<Tb_vmc_product> getScrollData(String classID) 
    {
    	List<Tb_vmc_product> tb_inaccount = new ArrayList<Tb_vmc_product>();// 创建集合对象
        Cursor cursor = null;
        //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productName="+params+" sort="+datasort);
        
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象        
    	// 获取所有收入信息
        cursor = db.rawQuery("select productID,productName,productDesc,marketPrice," +
        		"salesPrice,shelfLife,downloadTime,onloadTime,attBatch1,attBatch2,attBatch3," +
        		"paixu,isdelete from vmc_product where productID in" +
        		"(select productID from vmc_classproduct where classID=?)", new String[] { classID });
         
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_product
        		(
        				cursor.getString(cursor.getColumnIndex("productID")), cursor.getString(cursor.getColumnIndex("productName")),
        				cursor.getString(cursor.getColumnIndex("productDesc")),cursor.getFloat(cursor.getColumnIndex("marketPrice")),
        				cursor.getFloat(cursor.getColumnIndex("salesPrice")),cursor.getInt(cursor.getColumnIndex("shelfLife")),
        				cursor.getString(cursor.getColumnIndex("downloadTime")),cursor.getString(cursor.getColumnIndex("onloadTime")),
        				cursor.getString(cursor.getColumnIndex("attBatch1")), cursor.getString(cursor.getColumnIndex("attBatch2")),
        				cursor.getString(cursor.getColumnIndex("attBatch3")),cursor.getInt(cursor.getColumnIndex("paixu")),
        				cursor.getInt(cursor.getColumnIndex("isdelete"))
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
     * 获取总记录数
     * 
     * @return
     */
    public long getCount() {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(productID) from vmc_product", null);// 获取收入信息的记录数
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
//        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
//        Cursor cursor = db.rawQuery("select max(productID) from vmc_product", null);// 获取收入信息表中的最大编号
//        while (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
//            return cursor.getInt(0);// 获取访问到的数据，即最大编号
//        }
        return 0;// 如果没有数据，则返回0
    }
}
