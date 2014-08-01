package com.fgrim.msnake;

import java.util.ArrayList;

import net.kenyang.piechart.PieChart;
import net.kenyang.piechart.PieChart.OnSelectedLisenter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Charts extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);
		
		final PieChart pie = (PieChart) findViewById(R.id.pieChart);
		
		int right = Utilities.returnupmotions()+1;
		int left  = Utilities.returnleftmotions()+1;
		int down = Utilities.returndownmotions()+1;
		int up = Utilities.returnupmotions()+1;
		
		Float total = (float) (right+left+down+up);
		Float avg = total/4;
		
		
		
		ArrayList<Float> alPercentage = new ArrayList<Float>();
		alPercentage.add(right/total*100);
		alPercentage.add(left/total*100);
		alPercentage.add(up/total*100);
		alPercentage.add(down/total*100);
		
		final ArrayList<String> names = new ArrayList<String>();
		names.add("Right Motions");
		names.add("Left Motions");
		names.add("Up motions");
		names.add("Down Motions");
		
		try {
			  // setting data
			  pie.setAdapter(alPercentage);

			  // setting a listener 
			  pie.setOnSelectedListener(new OnSelectedLisenter() {
			    @Override
			    public void onSelected(int iSelectedIndex) {
			      Toast.makeText(Charts.this, "Select index:" + names.get(iSelectedIndex), Toast.LENGTH_SHORT).show();
			    }
			  });  
			} catch (Exception e) {
			  if (e.getMessage().equals(PieChart.ERROR_NOT_EQUAL_TO_100)){
			    Log.e("kenyang","percentage is not equal to 100");
			  }
			}
		
		
	}
	
	

}
