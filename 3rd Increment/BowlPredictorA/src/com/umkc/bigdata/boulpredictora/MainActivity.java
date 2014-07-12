package com.umkc.bigdata.boulpredictora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	
	public void onTrainClick(View view) {
		startActivity(new Intent(MainActivity.this, Train.class));
	}
	
	
	public void onGameClick(View view) {
		
		startActivity(new Intent(MainActivity.this, Game.class));
	}
	
	public void onAboutClick(View view) {
		startActivity(new Intent(MainActivity.this, About.class));
	}
}