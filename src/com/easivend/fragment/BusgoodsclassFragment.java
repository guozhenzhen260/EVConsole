package com.easivend.fragment;

import java.util.HashMap;
import java.util.Map;

import com.easivend.common.ClassPictureAdapter;
import com.easivend.common.Vmc_ClassAdapter;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

public class BusgoodsclassFragment extends Fragment 
{
	GridView gvbusgoodsclass=null;
	ImageView imgbtnbusgoodsback=null;
	Button btnreturn=null;
	private Context context;
	
	//=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private BusgoodsclassFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BusgoodsclassFragInteraction)
        {
            listterner = (BusgoodsclassFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BusgoodsclassFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface BusgoodsclassFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void BusgoodsclassSwitch(Map<String, String> str);//切换到Busgoods页面
        void BusgoodsclassFinish();      //切换到business页面
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_busgoodsclass, container, false);  
		context=this.getActivity();//获取activity的context
		gvbusgoodsclass=(GridView) view.findViewById(R.id.gvbusgoodsclass); 
		Vmc_ClassAdapter vmc_classAdapter=new Vmc_ClassAdapter();
	    String[] strInfos = vmc_classAdapter.showSpinInfo(context);
	    ClassPictureAdapter adapter = new ClassPictureAdapter(vmc_classAdapter.getProclassName(),vmc_classAdapter.getProImage(),context);// 创建pictureAdapter对象
	    final String proclassID[]=vmc_classAdapter.getProclassID();
	    gvbusgoodsclass.setAdapter(adapter);// 为GridView设置数据源	
	    gvbusgoodsclass.setOnItemClickListener(new OnItemClickListener() {// 为GridView设置项单击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //Intent intent = null;// 创建Intent对象
            	Map<String, String>str=new HashMap<String, String>();
                switch (arg2) {
                case 0:
//                	intent = new Intent(BusgoodsClass.this, Busgoods.class);// 使用Accountflag窗口初始化Intent
//                	intent.putExtra("proclassID", "");
//                	startActivity(intent);// 打开Accountflag                	
    	        	str.put("proclassID", "");
                	listterner.BusgoodsclassSwitch(str);//步骤二、fragment向activity发送回调信息
                    break;
                default:
//                	intent = new Intent(BusgoodsClass.this, Busgoods.class);// 使用Accountflag窗口初始化Intent
//                	intent.putExtra("proclassID", proclassID[arg2]);
//                	startActivity(intent);// 打开Accountflag
                	str.put("proclassID", proclassID[arg2]);
                	listterner.BusgoodsclassSwitch(str);//步骤二、fragment向activity发送回调信息
                    break;                
                }
            }
        });
	    imgbtnbusgoodsback=(ImageView)view.findViewById(R.id.imgbtnbusgoodsback);
	    imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.BusgoodsclassFinish();//步骤二、fragment向activity发送回调信息
		    }
		});	 
	    btnreturn=(Button)view.findViewById(R.id.btnreturn);
	    btnreturn.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	listterner.BusgoodsclassFinish();//步骤二、fragment向activity发送回调信息
		    }
		});	 
		return view;
	}
}
