/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           vmc_machinesetDAO.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        vmc_machinesetDAO 机器配置表操作文件  
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

import com.easivend.model.Tb_vmc_machineset;
import com.easivend.model.Tb_vmc_system_parameter;

public class vmc_machinesetDAO 
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    //SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    // 定义构造函数
	public vmc_machinesetDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	//添加或修改
	public void add(Tb_vmc_machineset tb_vmc_machineset)throws SQLException
	{
		int max=0;
		//取得排序值
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select count(machID) from vmc_machineset", null);// 获取收入信息的记录数
        if (cursor.moveToNext()) {// 判断Cursor中是否有数据

            max=cursor.getInt(0);// 返回总记录数
        }
        if(max==0)
        {
	        // 执行添加商品		
	 		db.execSQL(
	 				"insert into vmc_machineset" +
	 				"(" +
	 				"islogo,audioWork,audioWorkstart,audioWorkend,audioSun," +
	 				"audioSunstart,audioSunend,tempWork,tempWorkstart,tempWorkend,tempSunstart,tempSunend,ligntWorkstart," +
	 				"ligntWorkend,ligntSunstart,ligntSunend,coldWorkstart,coldWorkend,coldSunstart," +
	 				"coldSunend,chouWorkstart,chouWorkend,chouSunstart,chouSunend" +
	 				") " +
	 				"values" +
	 				"(" +
	 				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
	 				")",
	 		        new Object[] { tb_vmc_machineset.getIslogo(), tb_vmc_machineset.getAudioWork(),tb_vmc_machineset.getAudioWorkstart(), tb_vmc_machineset.getAudioWorkend(),
	 						tb_vmc_machineset.getAudioSun(), tb_vmc_machineset.getAudioSunstart(),tb_vmc_machineset.getAudioSunend(), tb_vmc_machineset.getTempWork(),
	 						tb_vmc_machineset.getTempWorkstart(), tb_vmc_machineset.getTempWorkend(), tb_vmc_machineset.getTempSunstart(), tb_vmc_machineset.getTempSunend(),
	 						tb_vmc_machineset.getLigntWorkstart(), tb_vmc_machineset.getLigntWorkend(), tb_vmc_machineset.getLigntSunstart(), tb_vmc_machineset.getLigntSunend(),
	 						tb_vmc_machineset.getColdWorkstart(), tb_vmc_machineset.getColdWorkend(), tb_vmc_machineset.getColdSunstart(), tb_vmc_machineset.getColdSunend(),
	 						tb_vmc_machineset.getChouWorkstart(), tb_vmc_machineset.getChouWorkend(), tb_vmc_machineset.getChouSunstart(), tb_vmc_machineset.getChouSunend()});
 		
        }
        else
        {
            // 执行添加商品		
     		db.execSQL(
     				"update vmc_machineset " +
     				"set " +
     				"islogo=?,audioWork=?,audioWorkstart=?,audioWorkend=?,audioSun=?," +
     				"audioSunstart=?,audioSunend=?,tempWork=?,tempWorkstart=?,tempWorkend=?,tempSunstart=?,tempSunend=?,ligntWorkstart=?," +
     				"ligntWorkend=?,ligntSunstart=?,ligntSunend=?,coldWorkstart=?,coldWorkend=?,coldSunstart=?,coldSunend=?,"+ 
					"chouWorkstart=?,chouWorkend=?,chouSunstart=?,chouSunend=?"
     				,
     				new Object[] { tb_vmc_machineset.getIslogo(), tb_vmc_machineset.getAudioWork(),tb_vmc_machineset.getAudioWorkstart(), tb_vmc_machineset.getAudioWorkend(),
	 						tb_vmc_machineset.getAudioSun(), tb_vmc_machineset.getAudioSunstart(),tb_vmc_machineset.getAudioSunend(), tb_vmc_machineset.getTempWork(),
	 						tb_vmc_machineset.getTempWorkstart(), tb_vmc_machineset.getTempWorkend(), tb_vmc_machineset.getTempSunstart(), tb_vmc_machineset.getTempSunend(),
	 						tb_vmc_machineset.getLigntWorkstart(), tb_vmc_machineset.getLigntWorkend(), tb_vmc_machineset.getLigntSunstart(), tb_vmc_machineset.getLigntSunend(),
	 						tb_vmc_machineset.getColdWorkstart(), tb_vmc_machineset.getColdWorkend(), tb_vmc_machineset.getColdSunstart(), tb_vmc_machineset.getColdSunend(),
	 						tb_vmc_machineset.getChouWorkstart(), tb_vmc_machineset.getChouWorkend(), tb_vmc_machineset.getChouSunstart(), tb_vmc_machineset.getChouSunend()});
 		
            }	
 		if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close(); 
	}    
	/**
     * 查找一条商品信息
     * 
     * @param id
     * @return
     */
    public Tb_vmc_machineset find() 
    {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        Cursor cursor = db.rawQuery("select islogo,audioWork,audioWorkstart,audioWorkend,audioSun," +
        		"audioSunstart,audioSunend,tempWork,tempWorkstart,tempWorkend,tempSunstart,tempSunend,ligntWorkstart," +
 				"ligntWorkend,ligntSunstart,ligntSunend,coldWorkstart,coldWorkend,coldSunstart," +
 				"coldSunend,chouWorkstart,chouWorkend,chouSunstart,chouSunend from vmc_machineset", null );// 根据编号查找支出信息，并存储到Cursor类中
        if (cursor.moveToNext()) 
        {// 遍历查找到的支出信息

            // 将遍历到的支出信息存储到Tb_outaccount类中
            return new Tb_vmc_machineset(cursor.getInt(cursor.getColumnIndex("islogo")), cursor.getInt(cursor.getColumnIndex("audioWork")),
    				cursor.getString(cursor.getColumnIndex("audioWorkstart")),cursor.getString(cursor.getColumnIndex("audioWorkend")),cursor.getInt(cursor.getColumnIndex("audioSun")),cursor.getString(cursor.getColumnIndex("audioSunstart")),
    				cursor.getString(cursor.getColumnIndex("audioSunend")),cursor.getInt(cursor.getColumnIndex("tempWork")), cursor.getString(cursor.getColumnIndex("tempWorkstart")), cursor.getString(cursor.getColumnIndex("tempWorkend")),
    				cursor.getString(cursor.getColumnIndex("tempSunstart")), cursor.getString(cursor.getColumnIndex("tempSunend")), cursor.getString(cursor.getColumnIndex("ligntWorkstart")), cursor.getString(cursor.getColumnIndex("ligntWorkend")),
    				cursor.getString(cursor.getColumnIndex("ligntSunstart")), cursor.getString(cursor.getColumnIndex("ligntSunend")),  cursor.getString(cursor.getColumnIndex("coldWorkstart")),  cursor.getString(cursor.getColumnIndex("coldWorkend")),
    				cursor.getString(cursor.getColumnIndex("coldSunstart")), cursor.getString(cursor.getColumnIndex("coldSunend")),  cursor.getString(cursor.getColumnIndex("chouWorkstart")),  cursor.getString(cursor.getColumnIndex("chouWorkend")),
    				cursor.getString(cursor.getColumnIndex("chouSunstart")), cursor.getString(cursor.getColumnIndex("chouSunend"))
    		);
        }
        if (!cursor.isClosed()) 
 		{  
 			cursor.close();  
 		}  
 		db.close();
        return null;// 如果没有信息，则返回null
    }
}
