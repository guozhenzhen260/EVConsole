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

import com.easivend.model.Tb_vmc_class;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



public class vmc_classDAO 
{
	private DBOpenHelper helper;// 创建DBOpenHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象
    // 定义构造函数
	public vmc_classDAO(Context context) 
	{
		helper=new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}
	//添加
	public void add(Tb_vmc_class tb_vmc_class)
	{
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		// 执行添加
		db.execSQL("insert into vmc_class (classID,className) values (?,?)",
                new Object[] { tb_vmc_class.getClassID(), tb_vmc_class.getClassName() });
	}
    //修改
	public void update(Tb_vmc_class tb_vmc_class) {
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 执行修改
        db.execSQL("update vmc_class set className = ? where classID = ?", 
        		new Object[] { tb_vmc_class.getClassName(),tb_vmc_class.getClassID()});
    }
	//删除
	public void detele(Tb_vmc_class tb_vmc_class) 
	{       
        db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
        // 执行删除收入信息操作
        db.execSQL("delete from vmc_class where classID = ?", 
        		new Object[] { tb_vmc_class.getClassID()});
        
    }
}
