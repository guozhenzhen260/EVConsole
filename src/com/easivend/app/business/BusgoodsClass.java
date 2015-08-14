package com.easivend.app.business;

import com.easivend.app.maintain.GoodsManager;
import com.easivend.app.maintain.HuodaoTest;
import com.easivend.app.maintain.MaintainActivity;
import com.easivend.app.maintain.ParamManager;
import com.easivend.common.ClassPictureAdapter;
import com.easivend.common.HuoPictureAdapter;
import com.easivend.common.Vmc_ClassAdapter;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

public class BusgoodsClass extends Activity
{
	private final int SPLASH_DISPLAY_LENGHT = 5*60*1000; // 延迟5分钟	
	public static BusgoodsClass BusgoodsClassAct=null;
	GridView gvbusgoodsclass=null;
	ImageButton imgbtnbusgoodsclassback=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.busgoodsclass);
		BusgoodsClassAct = this;
		gvbusgoodsclass=(GridView) findViewById(R.id.gvbusgoodsclass); 
		Vmc_ClassAdapter vmc_classAdapter=new Vmc_ClassAdapter();
	    String[] strInfos = vmc_classAdapter.showSpinInfo(BusgoodsClass.this);
	    ClassPictureAdapter adapter = new ClassPictureAdapter(vmc_classAdapter.getProclassName(),vmc_classAdapter.getProImage(),BusgoodsClass.this);// 创建pictureAdapter对象
	    final String proclassID[]=vmc_classAdapter.getProclassID();
	    gvbusgoodsclass.setAdapter(adapter);// 为GridView设置数据源	
	    gvbusgoodsclass.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = null;// 创建Intent对象
                switch (arg2) {
                case 0:
                	intent = new Intent(BusgoodsClass.this, Busgoods.class);// 使用Accountflag窗口初始化Intent
                	intent.putExtra("proclassID", "");
                	startActivity(intent);// 打开Accountflag
                    break;
                default:
                	intent = new Intent(BusgoodsClass.this, Busgoods.class);// 使用Accountflag窗口初始化Intent
                	intent.putExtra("proclassID", proclassID[arg2]);
                	startActivity(intent);// 打开Accountflag
                    break;                
                }
            }
        });
	    imgbtnbusgoodsclassback=(ImageButton)findViewById(R.id.imgbtnbusgoodsclassback);
	    imgbtnbusgoodsclassback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
	    //5分钟钟之后退出页面
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {	
            	finish();  
            }

		}, SPLASH_DISPLAY_LENGHT);
	}
	
}
