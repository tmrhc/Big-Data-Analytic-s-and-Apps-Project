package com.umkc.bigdata.boulpredictora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.umkc.bigdata.bowlpredictor.service.ConnectionService;

public class Game extends Activity {

	Intent servicecall;
	int counter = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		servicecall = new Intent(this,ConnectionService.class);
		startService(servicecall);
		
		
	}

	public void onPlayClick(View view) {
		servicecall.putExtra("start","0");
	}
	
	public void onDoneClick(View view) {
		servicecall.putExtra("start",Integer.toString(counter));		
		counter++;
	}
}
