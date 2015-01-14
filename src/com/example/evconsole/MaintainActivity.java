package com.example.evconsole;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class MaintainActivity extends Activity
{
	TextView txtcom=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintain);
		Intent intent=getIntent();
		String str=intent.getStringExtra("comport");
		txtcom=(TextView)super.findViewById(R.id.txtcom);
		txtcom.setText(str);
	}

}
