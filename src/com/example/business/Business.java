package com.example.business;

import java.io.File;

import com.example.evconsole.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Business extends Activity
{
	//VideoView
	private VideoView video=null;
	private File filev;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business);
		
		//VideoView
		video=(VideoView)findViewById(R.id.video);
		filev=new File("/sdcard/testmedia/huodao.mp4");
		MediaController mc=new MediaController(Business.this);
		if(filev.exists())
		{
			video.setVideoPath(filev.getAbsolutePath());
			video.setMediaController(mc);
			video.requestFocus();
			try 
			{
				video.start();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			//播放完成事件监听器
			video.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// 提示播放完毕
					Toast.makeText(Business.this, "视频播放完毕", Toast.LENGTH_LONG).show();
				}
			});
		}
		else {
			Toast.makeText(Business.this, "视频文件不存在", Toast.LENGTH_LONG).show();
		}
		
	}
	
}
