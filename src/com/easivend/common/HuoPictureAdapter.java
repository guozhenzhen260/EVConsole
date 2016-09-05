/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           HuoPictureAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        GridView适配器类，这里面配置货道设置页面的图片数据     
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.evconsole.R;

public class HuoPictureAdapter extends BaseAdapter {// 创建基于BaseAdapter的子类

    private LayoutInflater inflater;// 创建LayoutInflater对象
    private List<HuoPicture> pictures;// 创建List泛型集合
    private String cabinetID=null;

    // 为类创建构造函数
    public HuoPictureAdapter(String cabinetID,String[] huoID, String[] huoproID,String[] huoRemain,String[] huolasttime,String[] proImage, Context context) {
        super();
        this.cabinetID=cabinetID;
        pictures = new ArrayList<HuoPicture>();// 初始化泛型集合对象
        inflater = LayoutInflater.from(context);// 初始化LayoutInflater对象
        for (int i = 0; i < proImage.length; i++)// 遍历图像数组
        {
            HuoPicture picture = new HuoPicture(huoID[i],huoproID[i],huoRemain[i], huolasttime[i],proImage[i]);// 使用标题和图像生成ProPicture对象
            pictures.add(picture);// 将Picture对象添加到泛型集合中
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<货道="+huoID[i]+","+huolasttime[i],"log.txt");
        }
    }

    @Override
    public int getCount() {// 获取泛型集合的长度
        if (null != pictures) {// 如果泛型集合不为空
            return pictures.size();// 返回泛型长度
        } else {
            return 0;// 返回0
        }
    }

    @Override
    public Object getItem(int arg0) {
        return pictures.get(arg0);// 获取泛型集合指定索引处的项
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;// 返回泛型集合的索引
    }
    
    
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        HuoViewHolder viewHolder;// 创建ProViewHolder对象
        if (arg1 == null) {// 判断图像标识是否为空

            arg1 = inflater.inflate(R.layout.huodaogv, null);// 设置图像标识
            viewHolder = new HuoViewHolder();// 初始化ProViewHolder对象
            viewHolder.huoID = (TextView) arg1.findViewById(R.id.huoID);// 设置图像标题
            viewHolder.huoproID = (TextView) arg1.findViewById(R.id.huoproID);// 设置图像标题
            viewHolder.huoRemain = (TextView) arg1.findViewById(R.id.huoRemain);// 设置图像标题
            viewHolder.huolasttime = (TextView) arg1.findViewById(R.id.huolasttime);// 设置图像标题
            viewHolder.huoImage = (ImageView) arg1.findViewById(R.id.huoImage);// 设置图像的二进制值
            
            
            arg1.setTag(viewHolder);// 设置提示
        } 
        else
        {
            viewHolder = (HuoViewHolder) arg1.getTag();// 设置提示
        }
        
        viewHolder.huoID.setText("货道:"+cabinetID+pictures.get(arg0).getHuoID());// 设置图像ID
        viewHolder.huoproID.setText("商品ID:"+pictures.get(arg0).getHuoproID());// 设置图像原价
        viewHolder.huoRemain.setText("余量:"+pictures.get(arg0).getHuoRemain());// 设置图像原价
        viewHolder.huolasttime.setText("商品:"+pictures.get(arg0).getHuolasttime());
        //ToolClass.Log(ToolClass.INFO,"EV_JNI","货道:"+pictures.get(arg0).getHuoID()+"Img2="+pictures.get(arg0).getProImage(),"log.txt");
        //viewHolder.huoImage.setImageResource(R.drawable.wufenleiimg);
        //有对应的商品id
//        if((pictures.get(arg0).getProImage()!=null)&&(pictures.get(arg0).getProImage().equals("0")!=true)&&(pictures.get(arg0).getProImage().equals("")!=true))
//        {
//        	String ATT_ID="";
//    		if(pictures.get(arg0).getProImage().equals("null")!=true)
//    		{
//    			String a[] = pictures.get(arg0).getProImage().split("/");  
//    			ATT_ID=a[a.length-1];
//    			ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
//    			ToolClass.Log(ToolClass.INFO,"EV_JNI","图片ATT_ID="+ATT_ID,"log.txt");
//    		}
//        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<图片pro="+pictures.get(arg0).getHuoID()+",addr="+pictures.get(arg0).getProImage()+",ATT_ID="+ATT_ID,"log.txt");
//        	if(ToolClass.isImgFile(ATT_ID)==false)
//			{
//				ToolClass.Log(ToolClass.INFO,"EV_JNI","货道["+pictures.get(arg0).getHuoID()+"]图片不存在","log.txt");
//			}
//			else
//			{
//				ToolClass.Log(ToolClass.INFO,"EV_JNI","货道["+pictures.get(arg0).getHuoID()+"]显示图片","log.txt");
//		        /*为什么图片一定要转化为 Bitmap格式的！！ */
//		        Bitmap bitmap = ToolClass.getLoacalBitmap(pictures.get(arg0).getProImage()); //从本地取图片(在cdcard中获取)  //
//		        if(bitmap!=null)
//		        	viewHolder.huoImage.setImageBitmap(bitmap);// 设置图像的二进制值
//			}
//        }
//        else
//        {
//        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<无图片pro="+pictures.get(arg0).getHuoID()+","+pictures.get(arg0).getProImage(),"log.txt");
//        	viewHolder.huoImage.setImageResource(R.drawable.wutupian);
//		}
        return arg1;// 返回图像标识
    }
}

class HuoViewHolder {// 创建ProViewHolder类存放控件集合

    public TextView huoID;// 货道id
    public TextView huoproID;// 货道对应商品id
    public TextView huoRemain;// 剩余存货数量
    public TextView huolasttime;// 上架时间
    public ImageView huoImage;// 创建ImageView对象
    
}

class HuoPicture {// 创建ProPicture类
    
    private String huoID = null;//货道id
	private String huoproID = null;//货道对应商品id
    private String huoRemain = null;//剩余存货数量
    private String huolasttime = null;//货道商品名称
    private String proImage = null;//图像位置
	public HuoPicture(String huoID, String huoproID, String huoRemain,
			String huolasttime, String proImage) {
		super();
		this.huoID = huoID;
		this.huoproID = huoproID;
		this.huoRemain = huoRemain;
		this.huolasttime = huolasttime;
		this.proImage = proImage;
	}
	public String getHuoID() {
		return huoID;
	}
	public void setHuoID(String huoID) {
		this.huoID = huoID;
	}
	public String getHuoproID() {
		return huoproID;
	}
	public void setHuoproID(String huoproID) {
		this.huoproID = huoproID;
	}
	public String getHuoRemain() {
		return huoRemain;
	}
	public void setHuoRemain(String huoRemain) {
		this.huoRemain = huoRemain;
	}
	public String getHuolasttime() {
		return huolasttime;
	}
	public void setHuolasttime(String huolasttime) {
		this.huolasttime = huolasttime;
	}
	public String getProImage() {
		return proImage;
	}
	public void setProImage(String proImage) {
		this.proImage = proImage;
	}
	
    
}
