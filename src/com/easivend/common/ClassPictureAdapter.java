/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           ClassPictureAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        GridView适配器类，这里面配置商品类别页面的图片数据     
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.common;

import java.util.ArrayList;
import java.util.List;

import com.example.evconsole.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassPictureAdapter extends BaseAdapter 
{// 创建基于BaseAdapter的子类

    private LayoutInflater inflater;// 创建LayoutInflater对象
    private List<ClassPicture> pictures;// 创建List泛型集合
   

    // 为类创建构造函数
    public ClassPictureAdapter(String[] proclassName,String[] proImage, Context context) {
        super();
        pictures = new ArrayList<ClassPicture>();// 初始化泛型集合对象
        inflater = LayoutInflater.from(context);// 初始化LayoutInflater对象
        for (int i = 0; i < proclassName.length; i++)// 遍历图像数组
        {
        	ClassPicture picture = new ClassPicture(proclassName[i],proImage[i]);// 使用标题和图像生成ProPicture对象
            pictures.add(picture);// 将Picture对象添加到泛型集合中
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<Img="+proclassName[i]+","+proImage[i],"log.txt");
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
    	ClassViewHolder viewHolder;// 创建ProViewHolder对象
        if (arg1 == null) {// 判断图像标识是否为空

            arg1 = inflater.inflate(R.layout.busgoodsclassgv, null);// 设置图像标识
            viewHolder = new ClassViewHolder();// 初始化ProViewHolder对象
            viewHolder.busgoodsclassName = (TextView) arg1.findViewById(R.id.busgoodsclassName);// 设置图像标题
            viewHolder.busgoodsclassImage = (ImageView) arg1.findViewById(R.id.busgoodsclassImage);// 设置图像的二进制值
            
            
            arg1.setTag(viewHolder);// 设置提示
        } 
        else
        {
            viewHolder = (ClassViewHolder) arg1.getTag();// 设置提示
        }
        
        viewHolder.busgoodsclassName.setText("类别:"+pictures.get(arg0).getProclassName());// 设置图像原价
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<Img2="+pictures.get(arg0).getProImage(),"log.txt");
        if(pictures.get(arg0).getProImage()!=null)
        {
	        if(pictures.get(arg0).getProImage().equals("0")!=true)
	        {        	
	        	/*为什么图片一定要转化为 Bitmap格式的！！ */
		        Bitmap bitmap = ToolClass.getLoacalBitmap(pictures.get(arg0).getProImage()); //从本地取图片(在cdcard中获取)  //
		        if(bitmap!=null)
		        	viewHolder.busgoodsclassImage.setImageBitmap(bitmap);// 设置图像的二进制值
	        }
        }
        return arg1;// 返回图像标识
    }
}

class ClassViewHolder {// 创建ProViewHolder类存放控件集合

    public TextView busgoodsclassName;// 类别名称
    public ImageView busgoodsclassImage;// 创建ImageView对象
    
}

class ClassPicture {// 创建ProPicture类
    
    private String proclassName = null;//类别名称
	private String proImage = null;//图像位置
	public ClassPicture(String proclassName, String proImage) {
		super();
		this.proclassName = proclassName;
		this.proImage = proImage;
	}
	public String getProclassName() {
		return proclassName;
	}
	public void setProclassName(String proclassName) {
		this.proclassName = proclassName;
	}
	public String getProImage() {
		return proImage;
	}
	public void setProImage(String proImage) {
		this.proImage = proImage;
	}
	
	
    
}
