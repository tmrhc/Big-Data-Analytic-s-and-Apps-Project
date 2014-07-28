package com.fgrim.msnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Analysis extends Activity{

	TextView up;
	TextView down;
	TextView right;
	TextView left;
	TextView calories;
	Button chart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis);
		
		up = (TextView)findViewById(R.id.up1);
		down = (TextView)findViewById(R.id.down1);
		right = (TextView)findViewById(R.id.right1);
		left = (TextView)findViewById(R.id.left1);
		calories = (TextView)findViewById(R.id.calories1);
		
		up.setText(Integer.toString( Utilities.returnupmotions()));
		down.setText(Integer.toString( Utilities.returndownmotions()));
		left.setText( Integer.toString( Utilities.returnleftmotions()));
		right.setText(Integer.toString( Utilities.returnrightmotions()));
		calories.setText(Integer.toString(Utilities.returncalories()));
		
		
		chart = (Button)findViewById(R.id.chartb);
		chart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ana2 = new Intent("android.intent.action.CHARTS");
				startActivity(ana2);
				
			}
		});
				
	}
	
	

}
