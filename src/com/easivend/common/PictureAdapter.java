/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           PictureAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        GridView适配器类，这里面配置主维护页面的图片数据     
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.evconsole.R;


public class PictureAdapter extends BaseAdapter {// 创建基于BaseAdapter的子类

    private LayoutInflater inflater;// 创建LayoutInflater对象
    private List<Picture> pictures;// 创建List泛型集合

    // 为类创建构造函数
    public PictureAdapter(String[] titles, int[] images, Context context) {
        super();
        pictures = new ArrayList<Picture>();// 初始化泛型集合对象
        inflater = LayoutInflater.from(context);// 初始化LayoutInflater对象
        for (int i = 0; i < images.length; i++)// 遍历图像数组
        {
            Picture picture = new Picture(titles[i], images[i]);// 使用标题和图像生成Picture对象
            pictures.add(picture);// 将Picture对象添加到泛型集合中
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
    //主要是这个函数，他用来加载图片image和表述text的
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;// 创建ViewHolder对象
        if (arg1 == null) {// 判断图像标识是否为空

            arg1 = inflater.inflate(R.layout.givtem, null);// 设置图像标识
            viewHolder = new ViewHolder();// 初始化ViewHolder对象
            viewHolder.title = (TextView) arg1.findViewById(R.id.ItemTitle);// 设置图像标题
            viewHolder.image = (ImageView) arg1.findViewById(R.id.ItemImage);// 设置图像的二进制值
            arg1.setTag(viewHolder);// 设置提示
        } else {
            viewHolder = (ViewHolder) arg1.getTag();// 设置提示
        }
        viewHolder.title.setText(pictures.get(arg0).getTitle());// 设置图像标题
        viewHolder.image.setImageResource(pictures.get(arg0).getImageId());// 设置图像的二进制值
        return arg1;// 返回图像标识
    }
}

class ViewHolder {// 创建ViewHolder类

    public TextView title;// 创建TextView对象
    public ImageView image;// 创建ImageView对象
}

class Picture {// 创建Picture配置类,既是config的作用

    private String title;// 定义字符串，表示图像标题
    private int imageId;// 定义int变量，表示图像的二进制值

    public Picture() {// 默认构造函数

        super();
    }

    public Picture(String title, int imageId) {// 定义有参构造函数

        super();
        this.title = title;// 为图像标题赋值
        this.imageId = imageId;// 为图像的二进制值赋值
    }

    public String getTitle() {// 定义图像标题的可读属性
        return title;
    }

    public void setTitle(String title) {// 定义图像标题的可写属性
        this.title = title;
    }

    public int getImageId() {// 定义图像二进制值的可读属性
        return imageId;
    }

    public void setimageId(int imageId) {// 定义图像二进制值的可写属性
        this.imageId = imageId;
    }
}
