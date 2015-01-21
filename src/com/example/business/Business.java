package com.example.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.easivend.evprotocol.ToolClass;
import com.example.evconsole.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Business extends Activity
{
	//VideoView
	private VideoView videoView=null;
	private File filev;
	private int curIndex = 0,isClick=0;//  
    Random r=new Random(); 
    private List<String> mMusicList = new ArrayList<String>();  
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business);
		listFiles(); 
		startVideo();		
	}
	
	 /* 播放列表 */  
    private void listFiles() 
    {  
    	//遍历这个文件夹里的所有文件
		File file = new File("/sdcard/ads/");
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {
				  ToolClass.Log(ToolClass.INFO,"EV_JNI",files[i].toString());
				  mMusicList.add(files[i].toString());
			  }
			}
		}    
    } 
    //打开播放器
    private void startVideo()
    {  
    	videoView=(VideoView)findViewById(R.id.video);
    	videoView.requestFocus(); 
    	play();  
    	//播放完成事件监听器
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
  
                    @Override  
                    public void onCompletion(MediaPlayer mp) {  
                        // TODO Auto-generated method stub  
                        play();//播放完毕再继续下一首  
                    }  
                });  
        //播放出错事件监听器        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
              
            @Override  
            public boolean onError(MediaPlayer mp, int what, int extra) {  
                // TODO Auto-generated method stub  
                play();//播放出错再继续下一首  
                return true;  
            }  
        });  
        
        //单击事件监听器
        videoView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(isClick==0)
				{
					onPause();	
					isClick=1;
				}
				else 
				{
					onResume();	
					isClick=0;
				}
				return true;
			}
		});       
        
	}  
    //播放
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
    
    //暂停
    @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        if(videoView!=null&&videoView.isPlaying()){  
            videoView.pause();  
        }  
          
    }  
  
    //恢复
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        startVideo();  
    } 
}
