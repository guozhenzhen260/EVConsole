package com.easivend.app.business;

import com.example.evconsole.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class BusZhipos extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ÎÞtitle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // È«ÆÁ
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhipos);
	}

}
