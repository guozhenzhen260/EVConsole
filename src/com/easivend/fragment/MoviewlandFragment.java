package com.easivend.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortFragInteraction;
import com.easivend.common.MediaFileAdapter;
import com.easivend.common.ToolClass;
import com.easivend.view.MyVideoView;
import  com.example.evconsole.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.ImageView;

public class MoviewlandFragment extends Fragment {
	//VideoView
	private MyVideoView videoView=null;
	private File filev;
	private int curIndex = 0,isClick=0;//  
    Random r=new Random(); 
    private List<String> mMusicList = new ArrayList<String>();  
    //ImageView
    private ImageView ivads=null;
    private List<String> imgMusicList = new ArrayList<String>();  
    private boolean viewvideo=false;
    private final int SPLASH_DISPLAY_LENGHT = 30000; // 延迟30秒
    private Context context;
    
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
		context=this.getActivity();//获取activity的context
		videoView=(MyVideoView)view.findViewById(R.id.video);	
		ivads=(ImageView)view.findViewById(R.id.ivads);
		ivads.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changefragment();
			}
		});
		listFiles(); 
		startVideo();
		/**
	     * 用来与其他fragment交互的,
	     * 步骤五、当Fragment被加载到activity的时候，注册回调信息
	     * @param activity
	     */
		BusPort.setCallBack(new buportInterfaceImp());
		return view;
	}
	
	private class buportInterfaceImp implements BusPortFragInteraction//加载接口
	{
		/**
	     * 用来与其他fragment交互的,
	     * 步骤三、实现BusPortFragInteraction接口
	     * @param activity
	     */
		@Override
		public void BusportTsxx(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportTbje(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportChjg(int sta) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportSend(String str) {
			// TODO Auto-generated method stub
		}

		@Override
		public void BusportMovie() {
			// TODO Auto-generated method stub
			 ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<视频重载入","log.txt");
			 viewvideo=true;
			 show(); 
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
                    	show();//播放完毕再继续下一首  
                    }  
                });  
        //播放出错事件监听器        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
              
            @Override  
            public boolean onError(MediaPlayer mp, int what, int extra) {  
                // TODO Auto-generated method stub  
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
    //图片和视频切换显示
    private void show()
    {
    	//视频和图片文件都要有才行
    	if((mMusicList.size()>0)&&(imgMusicList.size()>0))
    	{
	    	//播放视频
	    	if((viewvideo==false)&&(mMusicList.size()>0))
	    	{
	    		viewvideo=true;
	    		ivads.setVisibility(View.GONE);//图片关闭
	    		videoView.setVisibility(View.VISIBLE);//视频打开
	    		play();
	    	}
	    	//播放图片
	    	else 
	    	{
	    		viewvideo=false;
	    		if(imgMusicList.size()>0)
	    		{
		    		videoView.setVisibility(View.GONE);//视频关闭
		    		ivads.setVisibility(View.VISIBLE);//图片打开
		    		showImage();
	    		}
	    		else
	    		{
	    			show();
	    		}
			}
    	}
    }
    //显示图片
    private void showImage()
    {  
        curIndex=r.nextInt(imgMusicList.size()); 
        try 
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
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
    }  
    //播放视频
    private void play()
    {  
        curIndex=r.nextInt(mMusicList.size()); 
        try 
		{
	        videoView.setVideoPath(mMusicList.get(curIndex));  
	        videoView.start(); 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
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
