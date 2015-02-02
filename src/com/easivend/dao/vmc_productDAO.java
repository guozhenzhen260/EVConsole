package com.easivend.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.easivend.evprotocol.ToolClass;
import com.easivend.model.Tb_vmc_class;
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
	public void add(Tb_vmc_product tb_vmc_product)throws SQLException
	{
		int max=0;
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		//取得排序值
		Cursor cursor = db.rawQuery("select max(paixu) from vmc_product", null);// 获取收入信息表中的最大编号
        if (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
        	max=cursor.getInt(0); 
        }
        // 执行添加		
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
     		
	}
    //修改
	public void update(Tb_vmc_class tb_vmc_class) {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 执行修改
        db.execSQL("update vmc_class set className = ?,classTime=(datetime('now', 'localtime')) where classID = ?", 
        		new Object[] { tb_vmc_class.getClassName(),tb_vmc_class.getClassID()});
    }
	//删除单条
	public void detele(Tb_vmc_class tb_vmc_class) 
	{       
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 执行删除收入信息操作
        db.execSQL("delete from vmc_class where classID = ?", 
        		new Object[] { tb_vmc_class.getClassID()});
        
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
            // 执行删除收入信息操作
            db.execSQL("delete from vmc_class where classID in (" + sb + ")", (Object[]) ids);
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
        Cursor cursor = db.rawQuery("select * from vmc_class limit ?,?", new String[] { String.valueOf(start), String.valueOf(count) });
        //遍历所有的收入信息
        while (cursor.moveToNext()) 
        {	
            // 将遍历到的收入信息添加到集合中
            tb_inaccount.add(new Tb_vmc_class(cursor.getString(cursor.getColumnIndex("classID")), cursor.getString(cursor.getColumnIndex("className")),
            		cursor.getString(cursor.getColumnIndex("classTime"))));
        }
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
        return 0;// 如果没有数据，则返回0
    }
}
