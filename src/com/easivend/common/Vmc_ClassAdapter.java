/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           Vmc_ClassAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        ��Ʒ�����������࣬������������Ʒ������  
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
	private String[] proclassID = null;//������������ͱ��
	private String[] proclassName = null;//�����������������
	private String[] proImage = null;//�������������ͼƬ
	
	// ��ʾ��Ʒ������Ϣ�м�������ʾ����,һ���listʹ�ã������������
	public String[] showListInfo(Context context) 
	{
	    String[] strInfos = null;// �����ַ������飬�����洢������Ϣ
	    vmc_classDAO classdao = new vmc_classDAO(context);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
	    List<Tb_vmc_class> listinfos = classdao.getScrollData(0, (int) classdao.getCount());
	    strInfos = new String[listinfos.size()];// �����ַ�������ĳ���
	    int m = 0;// ����һ����ʼ��ʶ
	    // ����List���ͼ���
	    for (Tb_vmc_class tb_inaccount : listinfos) 
	    {
	        // �����������Ϣ��ϳ�һ���ַ������洢���ַ����������Ӧλ��
	        strInfos[m] = tb_inaccount.getClassID() + "<---|--->" + tb_inaccount.getClassName();
	        m++;// ��ʶ��1
	    }
	    return strInfos;
	}
	// ��ʾ��Ʒ������Ϣ,�����治ͬ���Ƕ���0000<-|->ȫ�� ��һ��,һ���spinnerʹ�ã�����ѡ�����
	public String[] showSpinInfo(Context context) 
	{	  
		String[] strInfos = null;// �����ַ������飬�����洢������Ϣ
	    
	    vmc_classDAO classdao = new vmc_classDAO(context);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
	    List<Tb_vmc_class> listinfos = classdao.getScrollData(0, (int) classdao.getCount());
	    strInfos = new String[listinfos.size()+1];// �����ַ�������ĳ���
	    proclassID = new String[listinfos.size()+1];// �����ַ�������ĳ���
	    proclassName = new String[listinfos.size()+1];// �����ַ�������ĳ���
	    proImage = new String[listinfos.size()+1];// �����ַ�������ĳ���
	    int m = 0;// ����һ����ʼ��ʶ
	    //����ȫ��������������һ��
	    strInfos[m] = "0<---|--->ȫ��";  
	    proclassID[m] = "0";
	    proclassName[m]="����";
	    proImage[m]="0";
	    m++;
	    // ����List���ͼ���
	    for (Tb_vmc_class tb_inaccount : listinfos) 
	    {
	        // �����������Ϣ��ϳ�һ���ַ������洢���ַ����������Ӧλ��
	        strInfos[m] = tb_inaccount.getClassID() + "<---|--->" + tb_inaccount.getClassName();
	        proclassID[m] = tb_inaccount.getClassID();
	        proclassName[m] = tb_inaccount.getClassName();
	        proImage[m]="0";
	        m++;// ��ʶ��1
	    }
	    return strInfos;
	}
	public String[] getProclassID() {
		return proclassID;
	}
	public void setProclassID(String[] proclassID) {
		this.proclassID = proclassID;
	}
	public String[] getProclassName() {
		return proclassName;
	}
	public void setProclassName(String[] proclassName) {
		this.proclassName = proclassName;
	}
	public String[] getProImage() {
		return proImage;
	}
	public void setProImage(String[] proImage) {
		this.proImage = proImage;
	}
	
	
}