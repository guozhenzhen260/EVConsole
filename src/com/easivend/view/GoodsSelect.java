/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           GoodsSelect.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        选择商品页面          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.view;

import com.easivend.app.maintain.GoodsManager;
import com.easivend.app.maintain.HuodaoSet;
import com.easivend.app.maintain.HuodaoTest;
import com.easivend.common.ProPictureAdapter;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_ProductAdapter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class GoodsSelect extends Activity 
{
	private GridView gvselectProduct=null;
	private Button btnselectexit=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodsselect);// 设置布局文件		
		this.gvselectProduct=(GridView) findViewById(R.id.gvselectProduct); 		
		
		// 商品表中的所有商品信息补充到商品数据结构数组中
		final Vmc_ProductAdapter productAdapter=new Vmc_ProductAdapter();
    	productAdapter.showProInfo(GoodsSelect.this,"","shoudong",""); 
    	ProPictureAdapter adapter = new ProPictureAdapter(productAdapter.getProID(),productAdapter.getPromarket(),productAdapter.getProsales(),productAdapter.getProImage(),productAdapter.getProcount(), GoodsSelect.this);// 创建pictureAdapter对象
    	gvselectProduct.setAdapter(adapter);// 为GridView设置数据源
    	//修改或添加货道对应商品
    	gvselectProduct.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub cabinetID[0],
				String strInfo[]=productAdapter.getProductID();
				String productID = strInfo[arg2];// 记录收入信息               
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品productID="+productID,"log.txt");
				//退出时，返回intent
	            Intent intent=new Intent();
	            intent.putExtra("productID", productID);
	            setResult(HuodaoSet.RESULT_OK,intent);
	            finish();
			}// 为GridView设置项单击事件
    		
    	});
		//按下返回
		this.btnselectexit=(Button) findViewById(R.id.btnselectexit); 
		btnselectexit.setOnClickListener(new OnClickListener() {// 为退出按钮设置监听事件
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	}
	
}
