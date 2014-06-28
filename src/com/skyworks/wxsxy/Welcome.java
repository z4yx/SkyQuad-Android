package com.skyworks.wxsxy;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class Welcome extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 1500; // 延迟六秒
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(Welcome.this,
						BluetoothConnect.class);
				Welcome.this.startActivity(mainIntent);
				Welcome.this.finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
        //Intent intent = new Intent();
        //intent.setClass(Welcome.this,BluetoothConnect.class);
        //startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_welcome, menu);
       // return true;
        boolean result = super.onCreateOptionsMenu(menu);
    	menu.add(0,0,0,"重玩");
    	menu.add(0,1,1,"关于...");
    	menu.add(0,2,2,"退出");
    	return result;       
    }

    
}
