/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           ProPictureAdapter.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        GridView适配器类，这里面配置商品设置页面的图片数据     
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
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.evconsole.R;

public class ProPictureAdapter extends BaseAdapter {// 创建基于BaseAdapter的子类

    private LayoutInflater inflater;// 创建LayoutInflater对象
    private List<ProPicture> pictures;// 创建List泛型集合
    

    // 为类创建构造函数
    public ProPictureAdapter(String[] proID, String[] promarket,String[] prosales,String[] proImage,String[] procount, Context context) {
        super();
        pictures = new ArrayList<ProPicture>();// 初始化泛型集合对象
        inflater = LayoutInflater.from(context);// 初始化LayoutInflater对象
        for (int i = 0; i < proImage.length; i++)// 遍历图像数组
        {        	
            ProPicture picture = new ProPicture(proID[i],promarket[i],prosales[i], proImage[i],procount[i]);// 使用标题和图像生成ProPicture对象
            pictures.add(picture);// 将Picture对象添加到泛型集合中
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<proID="+picture.getProID()+",promarket="+picture.getPromarket()+",prosales="+picture.getProsales()+",proImage="+picture.getProImage()+",procount="+picture.getProcount(),"log.txt");
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
        ProViewHolder viewHolder;// 创建ProViewHolder对象
        if (arg1 == null) {// 判断图像标识是否为空

            arg1 = inflater.inflate(R.layout.productgv, null);// 设置图像标识
            viewHolder = new ProViewHolder();// 初始化ProViewHolder对象
            viewHolder.proID = (TextView) arg1.findViewById(R.id.proID);// 设置图像标题
            viewHolder.image = (ImageView) arg1.findViewById(R.id.proImage);// 设置图像的二进制值
            viewHolder.promarket = (TextView) arg1.findViewById(R.id.promarket);// 设置图像标题
            viewHolder.prosales = (TextView) arg1.findViewById(R.id.prosales);// 设置图像标题
            viewHolder.count = (TextView) arg1.findViewById(R.id.count);// 设置剩余数量
            
            arg1.setTag(viewHolder);// 设置标识
        } 
        else
        {
            viewHolder = (ProViewHolder) arg1.getTag();// 得到标识
        }
        //标识作为唯一id,用来比对和查询        
        viewHolder.proID.setText(pictures.get(arg0).getProID());// 设置图像ID
        viewHolder.promarket.setText("原价:"+pictures.get(arg0).getPromarket());// 设置原价
        viewHolder.promarket.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //删除线
        viewHolder.prosales.setText("零售价:"+pictures.get(arg0).getProsales());// 设置现价
        //显示商品数量
		if(Integer.parseInt(pictures.get(arg0).getProcount())>0)
        {
        	viewHolder.count.setText("剩余数量:"+pictures.get(arg0).getProcount());// 设置剩余数量
        	viewHolder.proID.setTextColor(android.graphics.Color.BLACK);
        	viewHolder.count.setTextColor(android.graphics.Color.BLACK);
        	viewHolder.promarket.setTextColor(android.graphics.Color.BLACK);
        	viewHolder.prosales.setTextColor(android.graphics.Color.RED);		        	
        }
        else
        {
        	viewHolder.count.setText("剩余数量:已售罄");// 设置剩余数量
        	viewHolder.proID.setTextColor(android.graphics.Color.GRAY);
        	viewHolder.count.setTextColor(android.graphics.Color.GRAY);
        	viewHolder.promarket.setTextColor(android.graphics.Color.GRAY);
        	viewHolder.prosales.setTextColor(android.graphics.Color.GRAY);		        	
        }
        ToolClass.Log(ToolClass.INFO,"EV_JNI","商品:"+pictures.get(arg0).getProID()+",promarket="+pictures.get(arg0).getPromarket()+",prosales="+pictures.get(arg0).getProsales()+",proImage="+pictures.get(arg0).getProImage()+",procount="+pictures.get(arg0).getProcount(),"log.txt");
        //*********
        //商品图片显示
        //*********
        //有对应的商品图片
        if((pictures.get(arg0).getProImage()!=null)&&(pictures.get(arg0).getProImage().equals("0")!=true)&&(pictures.get(arg0).getProImage().equals("")!=true))
        {
        	String ATT_ID="";
    		if(pictures.get(arg0).getProImage().equals("null")!=true)
    		{
    			String a[] = pictures.get(arg0).getProImage().split("/");  
    			ATT_ID=a[a.length-1];
    			ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
    			ToolClass.Log(ToolClass.INFO,"EV_JNI","图片ATT_ID="+ATT_ID,"log.txt");
    		}    		
        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<图片pro="+pictures.get(arg0).getProID()+",addr="+pictures.get(arg0).getProImage()+",ATT_ID="+ATT_ID,"log.txt");
        	//图片没有下载下来
        	if(ToolClass.isImgFile(ATT_ID)==false)
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","商品["+pictures.get(arg0).getProID()+"]图片不存在","log.txt");
				viewHolder.image.setImageResource(R.drawable.wutupian);
			}
        	//图片有下载下来
			else 
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","商品["+pictures.get(arg0).getProID()+"]显示图片","log.txt");
	        	if(Integer.parseInt(pictures.get(arg0).getProcount())>0)
		        {
		        	/*为什么图片一定要转化为 Bitmap格式的！！ */
		            Bitmap bitmap = ToolClass.getLoacalBitmap(pictures.get(arg0).getProImage()); //从本地取图片(在cdcard中获取)  //
		            if(bitmap!=null)
		            	viewHolder.image.setImageBitmap(bitmap);// 设置图像的二进制值
		        }
		        else
		        {
		        	/*原图片加载已售完水印 */
		            Bitmap photo = ToolClass.getLoacalBitmap(pictures.get(arg0).getProImage()); //从本地取图片(在cdcard中获取)  //
		            if(photo!=null)
		            {
			            Bitmap mark=ToolClass.getMark();
			            //ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<proID="+pictures.get(arg0).getProID()+"overproImage="+pictures.get(arg0).getProImage(),"log.txt");
			            Bitmap photoMark = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Config.ARGB_8888);  
			            Canvas canvas = new Canvas(photoMark);  
			            canvas.drawBitmap(photo, 0, 0, null);  
			            canvas.drawBitmap(mark, photo.getWidth() - mark.getWidth(), photo.getHeight() - mark.getHeight(), null);  
			            canvas.save(Canvas.ALL_SAVE_FLAG);  
			            canvas.restore();
			            viewHolder.image.setImageBitmap(photoMark);// 设置图像的二进制值
		            }
		        }
			}
        }
        else
        {
        	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<无图片pro="+pictures.get(arg0).getProID()+","+pictures.get(arg0).getProImage(),"log.txt");
        	viewHolder.image.setImageResource(R.drawable.wutupian);
		}
        
        return arg1;// 返回图像标识
    }
}

class ProViewHolder {// 创建ProViewHolder类存放控件集合

    public TextView proID;// 创建商品ID和名称
    public ImageView image;// 创建ImageView对象
    public TextView promarket;// 创建商品原价
    public TextView prosales;// 创建商品销售价
    public TextView count;// 创建商品剩余数量
}

class ProPicture {// 创建ProPicture类

    private String proID;// 定义字符串，表示图像标题
    private String proImage;//图像位置
    private String promarket;//原价
    private String prosales;//现价
    private String procount;//商品数量
	public ProPicture(String proID, String promarket,String prosales,String proImage,String procount)
	{
		super();
		this.proID = proID;
		this.proImage = proImage;
		this.promarket = promarket;
		this.prosales = prosales;
		this.procount = procount;
	}
	public String getProID() {
		return proID;
	}
	public void setProID(String proID) {
		this.proID = proID;
	}
	public String getProImage() {
		return proImage;
	}
	public void setProImage(String proImage) {
		this.proImage = proImage;
	}
	public String getPromarket() {
		return promarket;
	}
	public void setPromarket(String promarket) {
		this.promarket = promarket;
	}
	public String getProsales() {
		return prosales;
	}
	public void setProsales(String prosales) {
		this.prosales = prosales;
	}
	public String getProcount() {
		return procount;
	}
	public void setProcount(String procount) {
		this.procount = procount;
	}
	
    
}