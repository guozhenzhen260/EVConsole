/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Vmc_ClassAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        商品类型适配器类，这里面配置商品的类型  
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.easivend.app.maintain.GoodsManager;
import com.easivend.app.maintain.GoodsProSet;
import com.easivend.dao.vmc_classDAO;
import com.easivend.model.Tb_vmc_class;

public class Vmc_ClassAdapter 
{
	private String[] proclassID = null;//用来分离出产品编号
	
	// 显示商品分类信息有几样就显示几样,一般给list使用，用来添加类别
	public String[] showListInfo(Context context) 
	{
	    String[] strInfos = null;// 定义字符串数组，用来存储收入信息
	    vmc_classDAO classdao = new vmc_classDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
	    List<Tb_vmc_class> listinfos = classdao.getScrollData(0, (int) classdao.getCount());
	    strInfos = new String[listinfos.size()];// 设置字符串数组的长度
	    int m = 0;// 定义一个开始标识
	    // 遍历List泛型集合
	    for (Tb_vmc_class tb_inaccount : listinfos) 
	    {
	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
	        strInfos[m] = tb_inaccount.getClassID() + "<---|--->" + tb_inaccount.getClassName();
	        m++;// 标识加1
	    }
	    return strInfos;
	}
	// 显示商品分类信息,与上面不同的是多了0000<-|->全部 这一项,一般给spinner使用，用来选择类别
	public String[] showSpinInfo(Context context) 
	{	  
		String[] strInfos = null;// 定义字符串数组，用来存储收入信息
	    
	    vmc_classDAO classdao = new vmc_classDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
	    List<Tb_vmc_class> listinfos = classdao.getScrollData(0, (int) classdao.getCount());
	    strInfos = new String[listinfos.size()+1];// 设置字符串数组的长度
	    proclassID = new String[listinfos.size()+1];// 设置字符串数组的长度
	    int m = 0;// 定义一个开始标识
	    //添加全部，即不分类这一项
	    strInfos[m++] = "0<---|--->全部";        
	    // 遍历List泛型集合
	    for (Tb_vmc_class tb_inaccount : listinfos) 
	    {
	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
	        strInfos[m] = tb_inaccount.getClassID() + "<---|--->" + tb_inaccount.getClassName();
	        proclassID[m] = tb_inaccount.getClassID();
	        m++;// 标识加1
	    }
	    return strInfos;
	}
	public String[] getProclassID() {
		return proclassID;
	}
	public void setProclassID(String[] proclassID) {
		this.proclassID = proclassID;
	}
	
	
}
