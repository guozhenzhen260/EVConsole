/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           ShowSortAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        Spinner适配器类，这里面配置商品可以安照哪种排序方式     
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.ArrayList;
import java.util.List;

public class ShowSortAdapter
{
	private List<String> dataSortID = null,dataSortName=null;	
	public ShowSortAdapter()
	{
		super();
		// 获取所有收入信息，并存储到List泛型集合中
	    dataSortID = new ArrayList<String>();
	    dataSortName = new ArrayList<String>();
	    dataSortID.add("sale");
	    dataSortName.add("sale销售情况");
	    dataSortID.add("marketPrice");
	    dataSortName.add("marketPrice原价");
	    dataSortID.add("salesPrice");
	    dataSortName.add("salesPrice销售价");
	    dataSortID.add("shelfLife");
	    dataSortName.add("shelfLife过保质期");
	    dataSortID.add("colucount");
	    dataSortName.add("colucount剩余数量");
	    dataSortID.add("onloadTime");
	    dataSortName.add("onloadTime上架时间");
	    dataSortID.add("shoudong");
	    dataSortName.add("shoudong手动更改");	
	}
	public List<String> getDataSortID() {
		return dataSortID;
	}
	public void setDataSortID(List<String> dataSortID) {
		this.dataSortID = dataSortID;
	}
	public List<String> getDataSortName() {
		return dataSortName;
	}
	public void setDataSortName(List<String> dataSortName) {
		this.dataSortName = dataSortName;
	}
	
	
	
}
