package com.example.sample;

import gif.decoder.GifRun;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends Activity {

	Button collect;
	Button stopCollect;
	Button create;
	Button insert;
	Button retrive;
	Button delete;
	String url;
	int counter=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		collect = (Button)findViewById(R.id.button1);
		stopCollect = (Button)findViewById(R.id.button2);
	    final Intent servicecall = new Intent(this,ConnectionService.class); 
	    
	    

		
		collect.setOnClickListener(new View.OnClickListener() {
			
			

			@Override
			public void onClick(View v) {
				
				startService(servicecall);
				//stopService(servicecall);
				counter++;
				
				
			}
		});
		
		stopCollect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//stopService(servicecall);
				//unbindService(servicecall);
				//stopSelf(servicecall);
				
				//ConnectionService.stop();
				Intent devl = new Intent("com.example.sample.Develop");
				startActivity(devl);
				
			}
		});
		
		
		
		
		
		//startService(new Intent(this,ConnectionService.class));
		
		
		//stopService(new Intent(this,ConnectionService.class));
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	

}
