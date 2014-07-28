package com.fgrim.msnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstPage extends Activity {

	Button game;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstpage);
		
		game = (Button)findViewById(R.id.play);
		
		game.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openStartingPoint = new Intent("android.intent.action.TAGACTIVITY");
				startActivity(openStartingPoint);
			}
		});
		
		
	}
	
	

}
