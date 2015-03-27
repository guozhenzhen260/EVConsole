package com.easivend.common;

import java.util.List;

import android.content.Context;

import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_classDAO;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_class;

public class Vmc_CabinetAdapter
{
	private String[] cabinetID = null;//用来分离出货柜编号
	private int[] cabinetType = null;//用来分离出货柜类型
	private String[] cabType={"0无货道","1弹簧货道","2 老式升降台","3 升降台+传送带","4 升降台+弹簧","5格子柜"};
	// 显示商品分类信息,一般给spinner使用，用来选择货柜编号
	public String[] showSpinInfo(Context context) 
	{	  
		String[] strInfos = null;// 定义字符串数组，用来存储收入信息
	    
		vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(context);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
	    List<Tb_vmc_cabinet> listinfos = cabinetDAO.getScrollData();
	    strInfos = new String[listinfos.size()];// 设置字符串数组的长度
	    cabinetID = new String[listinfos.size()];// 设置字符串数组的长度
	    cabinetType=new int[listinfos.size()];// 设置字符串数组的长度
	    int m = 0;// 定义一个开始标识
	    // 遍历List泛型集合
	    for (Tb_vmc_cabinet tb_inaccount : listinfos) 
	    {
	        // 将收入相关信息组合成一个字符串，存储到字符串数组的相应位置
	        strInfos[m] = "柜号:"+tb_inaccount.getCabID() + "<---|--->" + "类型:"+cabType[tb_inaccount.getCabType()];
	        cabinetID[m] = tb_inaccount.getCabID();
	        cabinetType[m]= tb_inaccount.getCabType();
	        m++;// 标识加1
	    }
	    return strInfos;
	}
	public String[] getCabinetID() {
		return cabinetID;
	}
	public void setCabinetID(String[] cabinetID) {
		this.cabinetID = cabinetID;
	}
	public int[] getCabinetType() {
		return cabinetType;
	}
	public void setCabinetType(int[] cabinetType) {
		this.cabinetType = cabinetType;
	}
	
}
