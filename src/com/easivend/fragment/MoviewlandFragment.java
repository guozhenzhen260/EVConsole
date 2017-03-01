package com.easivend.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortMovieFragInteraction;
import com.easivend.common.MediaFileAdapter;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.MyVideoView;
import  com.example.evconsole.R;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class MoviewlandFragment extends Fragment {
	//VideoView
	private MyVideoView videoView=null;
	private int curIndex = 0;//  
    Random r=new Random(); 
    private List<String> mMusicList = new ArrayList<String>();  
    private WebView webtishiInfo;
    private ImageView ivads=null,ivmobile=null;
    private TextView txtcashlessamount=null;
    private List<String> imgMusicList = new ArrayList<String>();  
    private boolean viewvideo=false;//true正在播放视频,false没有播放视频
    private boolean videopause=false;//true正在交易，不播放视频,false空闲状态，可以播放视频
    private final int SPLASH_DISPLAY_LENGHT = 30000; // 延迟30秒
    
    //=========================
    //fragment与activity回调相关
    //=========================
    /**
     * 用来与外部activity交互的
     */
    private MovieFragInteraction listterner;
    /**
     * 步骤四、当ContentFragment被加载到activity的时候，主动注册回调信息
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof MovieFragInteraction)
        {
            listterner = (MovieFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements MovieFragInteraction");
        }

    }
    /**
     * 步骤一、定义了所有activity必须实现的接口
     */
    public interface MovieFragInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void switchBusiness();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_movieland, container, false);  
		txtcashlessamount=(TextView)view.findViewById(R.id.txtcashlessamount);
		txtcashlessamount.setVisibility(View.GONE);//卡余额关闭		
		videoView=(MyVideoView)view.findViewById(R.id.video);
		//得到提示描述
		webtishiInfo = (WebView) view.findViewById(R.id.webtishiInfo); 
		ivads=(ImageView)view.findViewById(R.id.ivads);
		ivads.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changefragment();
			}
		});
		//得到网络状态
		ivmobile=(ImageView)view.findViewById(R.id.ivmobile);
		switch(ToolClass.getNetType())
		{
			case 1:
				ivmobile.setImageResource(R.drawable.network);
				break;
			case 2:
				ivmobile.setImageResource(R.drawable.wifi);
				break;
			case 3:
				ivmobile.setImageResource(R.drawable.mobile);
				break;	
			case 4:
				ivmobile.setImageResource(R.drawable.nosignal);
				break;	
		}
		listFiles(); 
		if((mMusicList.size()==0)&&(imgMusicList.size()==0))
        {
            videoView.setVisibility(View.GONE);//关闭视频
            webtishiInfo.setVisibility(View.GONE);//关闭提示
            ivads.setVisibility(View.VISIBLE);//打开图片
        }
        else
        {
            startVideo();
        }

		/**
	     * 用来与其他fragment交互的,
	     * 步骤五、当Fragment被加载到activity的时候，注册回调信息
	     * @param activity
	     */
		BusPort.setMovieCallBack(new buportIntermoviefaceImp());
		return view;
	}
	
	private class buportIntermoviefaceImp implements BusPortMovieFragInteraction//加载接口
	{
		/**
	     * 用来与其他fragment交互的,
	     * 步骤三、实现BusPortFragInteraction接口
	     * @param activity
	     */		
		@Override
		public void BusportMovie(int infotype) {
			// TODO Auto-generated method stub
			 ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<显示交易提示信息="+infotype,"log.txt");
			 showtishiInfo(infotype);
		}
		
		@Override
		public void BusportAds() {
			// TODO Auto-generated method stub
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<刷新广告列表重载入","log.txt");
			mMusicList.clear();
			imgMusicList.clear();
			listFiles(); 
			startVideo();
		}
		
		@Override
		public void BusportCashless(String cashbalance) {
			// TODO Auto-generated method stub
			ToolClass.Log(ToolClass.INFO,"EV_COM","APP<卡余额="+cashbalance,"com.txt");			
			txtcashlessamount.setVisibility(View.VISIBLE);
			txtcashlessamount.setText("卡余额:"+cashbalance);
			//延时
		    new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {   
	            	txtcashlessamount.setText("");
	            	txtcashlessamount.setVisibility(View.GONE);
				}

			}, 5000);
		}
				
		@Override
		//type=0暂停视频播放,1恢复视频播放
		public void BusportVideoStop(int type) {
			// TODO Auto-generated method stub
			if(type==0)
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<暂停视频文件","log.txt");
				//视频和图片文件都要有才行
		    	if((mMusicList.size()>0)||(imgMusicList.size()>0))
		    	{
		    		//播放视频
			    	if((viewvideo==true)&&(mMusicList.size()>0))
			    	{
			    		videoView.pause(); 
			    	}
			    	else
			    	{
			    		videopause=true;
			    	}
		    	}				
			}
			else if(type==1)
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<恢复视频文件","log.txt");
				//视频和图片文件都要有才行
		    	if((mMusicList.size()>0)||(imgMusicList.size()>0))
		    	{
		    		//播放视频
			    	if((viewvideo==true)&&(mMusicList.size()>0))
			    	{
			    		videoView.start();
			    	}
			    	else
			    	{
			    		videopause=false;
			    	}
		    	}				
			}
		}		
	}

	
	/* 播放列表 */  
    private void listFiles() 
    {  
    	//遍历这个文件夹里的所有文件
		File file = new File(ToolClass.ReadAdsFile());
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {		
				  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品1ID="+files[i].toString(),"log.txt");
				  try
	        	  {
					  //是否视频文件
					  if(MediaFileAdapter.isVideoFileType(files[i].toString())==true)
					  {
						  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品videoID="+files[i].toString(),"log.txt");
						  mMusicList.add(files[i].toString());
					  }
					  //是否图片文件
					  else if(MediaFileAdapter.isImgFileType(files[i].toString())==true)
					  {
						  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品imageID="+files[i].toString(),"log.txt");
						  imgMusicList.add(files[i].toString());
					  }
	        	  }
				  catch(Exception e)
	        	  {
	        			ToolClass.Log(ToolClass.INFO,"EV_JNI","文件="+files[i].toString()+"异常，无法判断","log.txt");
	        	  }
			  }
			}
		}    
    } 
    //打开播放器
    private void startVideo()
    { 
    	videoView.requestFocus(); 
    	show();  
    	//播放完成事件监听器
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
  
                    @Override  
                    public void onCompletion(MediaPlayer mp) {  
                        // TODO Auto-generated method stub  
                    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<播放完成...","log.txt");
                    	show();//播放完毕再继续下一首  
                    }  
                });  
        //播放出错事件监听器        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
              
            @Override  
            public boolean onError(MediaPlayer mp, int what, int extra) {  
                // TODO Auto-generated method stub 
            	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<播放故障...","log.txt");
            	show();//播放出错再继续下一首  
                return true;  
            }  
        });  
        
        //单击事件监听器
        videoView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
//				if(isClick==0)
//				{
//					onPause();	
//					isClick=1;
//				}
//				else 
//				{
//					onResume();	
//					isClick=0;
//				}
				changefragment();								
				return true;
			}
		});       
        
	}  
    /**得到随机的索引值
     * @param type 0视频,1图片
     * @return
     */
    private void rNext(int type)
    {
        if(type==0)// 0视频
        {
        	curIndex=r.nextInt(mMusicList.size());
        }
        else if(type==1)//1图片
        {
        	curIndex=r.nextInt(imgMusicList.size());
        }
    }
    
    //图片和视频切换显示
    private String showOpt()
    {
        String rst=null;
        //视频和图片文件至少要有一个才行
        //播放视频:没有播放         有视频文件             在空闲状态
        if((viewvideo==false)&&(mMusicList.size()>0)&&(videopause==false))
        {
        	viewvideo=true;//设置开始播放
            rNext(0);
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<播放视频ID="+mMusicList.get(curIndex),"log.txt");
            rst=mMusicList.get(curIndex);
        }
        //播放图片
        else
        {
        	viewvideo=false;
            if(imgMusicList.size()>0)
            {
                rNext(1);
                ToolClass.Log(ToolClass.INFO, "EV_JNI", "APP<播放图片ID=" + imgMusicList.get(curIndex), "log.txt");
                rst = imgMusicList.get(curIndex);
            }
            else
            {
            	viewvideo=true;//设置开始播放
                rNext(0);
                ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<播放视频ID="+mMusicList.get(curIndex),"log.txt");
                rst=mMusicList.get(curIndex);
            }
        }
        return rst;
    }
    //图片和视频切换显示
    private void show()
    {
    	String rst=showOpt();
    	//是否视频文件
		if(MediaFileAdapter.isVideoFileType(rst)==true)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<视频可以播放ID="+rst,"log.txt");
			ivads.setVisibility(View.GONE);//图片关闭
    		webtishiInfo.setVisibility(View.GONE);//提示关闭
    		videoView.setVisibility(View.VISIBLE);//视频打开
    		play();
    	}
		//是否图片文件
		else if(MediaFileAdapter.isImgFileType(rst)==true)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<图片可以播放ID="+rst,"log.txt");
    		videoView.setVisibility(View.GONE);//视频关闭
    		webtishiInfo.setVisibility(View.GONE);//提示关闭
    		ivads.setVisibility(View.VISIBLE);//图片打开
    		playImage();
		}
    }
    //显示图片
    private void playImage()
    {  
    	/*为什么图片一定要转化为 Bitmap格式的！！ */
        Bitmap bitmap = ToolClass.getLoacalBitmap(imgMusicList.get(curIndex)); //从本地取图片(在cdcard中获取)  //
        ivads.setImageBitmap(bitmap);// 设置图像的二进制值
        //延时10s
        new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {
            	show();
            }

		}, SPLASH_DISPLAY_LENGHT);	
    }  
    //播放视频
    private void play()
    {  
    	videoView.setVideoPath(mMusicList.get(curIndex));  
        videoView.start(); 
    }   
    
    private boolean checkAds(String CLS_URL)
    {
    	String ATT_ID="",TypeStr="";
//  		int FileType=0;//1图片,2视频
  		boolean rst=false;
  		if(CLS_URL.equals("null")!=true)
  		{
  			String a[] = CLS_URL.split("/");  
  			ATT_ID=a[a.length-1];  
  			String tmp = ATT_ID;
	    	ATT_ID=tmp.substring(0,tmp.lastIndexOf("."));		    	
	    	TypeStr=tmp.substring(tmp.lastIndexOf(".")+1);
		    //是否视频文件
		    if(MediaFileAdapter.isVideoFileType(tmp)==true)
		    {
//		    	FileType=2;
		        ToolClass.Log(ToolClass.INFO,"EV_JNI","广告视频ATT_ID="+ATT_ID+"."+TypeStr,"log.txt");										
		    }
		    //是否图片文件
		    else if(MediaFileAdapter.isImgFileType(tmp)==true)
		    {
//		    	FileType=1;
	  			ToolClass.Log(ToolClass.INFO,"EV_JNI","广告图片ATT_ID="+ATT_ID+"."+TypeStr,"log.txt");										
	  		}
  			
		    if(ATT_ID.equals("")==true)
  			{
  				ToolClass.Log(ToolClass.INFO,"EV_JNI","广告["+ATT_ID+"]无","log.txt");
  			}
		    else if(ToolClass.isAdsFile(ATT_ID,TypeStr,"ads"))
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","广告["+ATT_ID+"]已存在","log.txt");
				rst=true;
			}
		    else
		    {
				ToolClass.Log(ToolClass.INFO,"EV_JNI","广告["+ATT_ID+"]不存在","log.txt");
			}
  		}
  		return rst;
    }
    
    //显示提示信息
    private void showtishiInfo(int infotype)
    {  
    	ivads.setVisibility(View.GONE);//图片关闭
    	videoView.setVisibility(View.GONE);//视频关闭
    	
		 webtishiInfo.setVisibility(View.VISIBLE);//提示打开
		 WebSettings settings = webtishiInfo.getSettings();
	     settings.setSupportZoom(true);
	     settings.setTextSize(WebSettings.TextSize.LARGEST);
	     webtishiInfo.getSettings().setSupportMultipleWindows(true);
	     webtishiInfo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //设置滚动条样式
	     webtishiInfo.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
	     String info="敬请期待!";
	     //购买演示和提示信息
  		 vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// 创建InaccountDAO对象
	     // 获取所有收入信息，并存储到List泛型集合中
    	 Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	 if(tb_inaccount!=null)
    	 {
    		 //购买演示
    		 if(infotype==1)
    		 {
    			 if(ToolClass.isEmptynull(tb_inaccount.getDemo())==false)
	    		 {
    				 info=tb_inaccount.getDemo();
    			 }	 
    		 }
    		 //活动信息
    		 else if(infotype==2)
    		 {    			 
				 if(ToolClass.isEmptynull(tb_inaccount.getEvent())==false)
    			 {
    				 info=tb_inaccount.getEvent();
    			 }    			 
    		 }
    	 }
	     
	     webtishiInfo.loadDataWithBaseURL(null,info, "text/html; charset=UTF-8","utf-8", null);//这种写法可以正确中文解码
		    
    	//延时10s
        new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {
            	show();
            }

		}, SPLASH_DISPLAY_LENGHT);
    }  
    
    
    //切换到操作fragment
    private void changefragment()
    {
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<goto=businesslandFragment","log.txt");
    	//步骤二、fragment向activity发送回调信息
    	listterner.switchBusiness();
    }
    
//    //暂停
//    @Override  
//    protected void onPause() {  
//        // TODO Auto-generated method stub  
//        super.onPause();  
//        if(videoView!=null&&videoView.isPlaying()){  
//            videoView.pause();  
//        }  
//          
//    }  
//  
//    //恢复
//    @Override  
//    protected void onResume() {  
//        // TODO Auto-generated method stub  
//        super.onResume();  
//        startVideo();  
//    } 
}
