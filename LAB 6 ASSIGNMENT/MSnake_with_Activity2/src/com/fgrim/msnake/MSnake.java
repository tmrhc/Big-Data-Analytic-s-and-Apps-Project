/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (C) 2011 Mariano Alvarez Fernandez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgrim.msnake;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MSnake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a
 * serpent roaming around the garden looking for apples. Be careful, though,
 * because when you catch one, not only will you become longer, but you'll move
 * faster. Running into yourself or the walls will end the game.
 * 
 * Derived from the Snake game in the Android SDK (M.Alvarez):
 * 
 * Added touch control
 * Added view score and record
 * Added menu walls on/off
 * Added menu big,normal,small size
 * Added menu about
 * Added save preferences
 * Added vibration
 * Added red pepper, slow down the snake
 * Added records dialog
 * Added alternate input turn mode
 * Added fast speed
 * and more
 */

public class MSnake extends Activity {

//    private static final String TAG = "MSnake";

    private SnakeView mSnakeView;
    
    private static String ICICLE_KEY = "snake-view";
    
    static final int DIALOG_ABOUT_ID = 0;
    static final int DIALOG_RECORDS_ID = 1;
    static final int DIALOG_WALLS_ID = 2;
    static final int DIALOG_SIZE_ID = 3;
    static final int DIALOG_SPEED_ID = 4;
    static final int DIALOG_NEWS_ID = 5;
    static final int DIALOG_EXIT_ID = 6;
    static final int DIALOG_INPUT_ID = 7;
    String x2 ="first";
//    boolean dResetCancel;
    
    /**
     * Called when Activity is first created. Turns off the title bar, sets up
     * the content views, and fires up the SnakeView.
     * 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       	setContentView(R.layout.snake_layout);

        mSnakeView = (SnakeView) findViewById(R.id.snake);
        mSnakeView.setTextView((TextView) findViewById(R.id.text));
        mSnakeView.setScoreView((TextView) findViewById(R.id.textscore));
        mSnakeView.setRecordView((TextView) findViewById(R.id.textrecord));

        int dHeight = getWindowManager().getDefaultDisplay().getHeight();
        int dWidth = getWindowManager().getDefaultDisplay().getWidth();
        mSnakeView.setTileSizes(dWidth, dHeight);

        // Restore preferences
        SharedPreferences settings = getPreferences(0);
        mSnakeView.restorePreferences(settings);
        setCorrectButtons();

        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mSnakeView.setMode(SnakeView.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
            }
        }
        if (mSnakeView.showNews18) showDialog(DIALOG_NEWS_ID);
//    	Log.d(TAG, "onCreate end");
        
        registerReceiver(receiver, new IntentFilter("myproject"));
        
    }

    protected void setCorrectButtons() {
        Button mButton[] = new Button[6];
      
        mButton[0] = (Button) findViewById(R.id.button0);
        mButton[1] = (Button) findViewById(R.id.button1);
        mButton[2] = (Button) findViewById(R.id.button2);
        mButton[3] = (Button) findViewById(R.id.button3);
        mButton[4] = (Button) findViewById(R.id.button4);
        mButton[5] = (Button) findViewById(R.id.button5);
        
        if (mSnakeView.inputMode == SnakeView.INPUT_MODE_2K) {
        	mButton[0].setVisibility(View.VISIBLE);
        	mButton[1].setVisibility(View.GONE);
        	mButton[2].setVisibility(View.GONE);
        	mButton[3].setVisibility(View.GONE);
        	mButton[4].setVisibility(View.GONE);
        	mButton[5].setVisibility(View.VISIBLE);
        } else if (mSnakeView.inputMode == SnakeView.INPUT_MODE_4K) {
        	mButton[0].setVisibility(View.GONE);
        	mButton[1].setVisibility(View.VISIBLE);
        	mButton[2].setVisibility(View.VISIBLE);
        	mButton[3].setVisibility(View.VISIBLE);
        	mButton[4].setVisibility(View.VISIBLE);
        	mButton[5].setVisibility(View.GONE);
        } else {
        	mButton[0].setVisibility(View.GONE);
        	mButton[1].setVisibility(View.GONE);
        	mButton[2].setVisibility(View.GONE);
        	mButton[3].setVisibility(View.GONE);
        	mButton[4].setVisibility(View.GONE);
        	mButton[5].setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Vibrator mvibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mSnakeView.setVibrator(mvibrator);
//    	Log.d(TAG, "onResume");
        registerReceiver(receiver, new IntentFilter("myproject"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity if RUNNING!!
        if (mSnakeView.getMode() == SnakeView.RUNNING) {
            mSnakeView.setMode(SnakeView.PAUSE);
            
        }
        registerReceiver(receiver, new IntentFilter("myproject"));
//    	Log.d(TAG, "onPause");
   }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getPreferences(0);
        mSnakeView.savePreferences(settings);
        registerReceiver(receiver, new IntentFilter("myproject"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }
    
    
    BroadcastReceiver receiver = new BroadcastReceiver() {
     	int count=0;
     	int storeing = 1;
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			Bundle bundle = intent.getExtras();
 			Log.i("Data Reception", "Data Recived in bundle");
 			if (bundle!=null) {
 				String directionx = bundle.getString("x");
 				Float dat = Float.valueOf(directionx);
 				int data = 0;
 				if(dat>0){
 					data=0;
 				}
 				else
 				{
 					data=1;
 				}
 				
 				String dataz = bundle.getString("z");
 				String datay = bundle.getString("y");
 				//Log.i("data in main class", data);
 				x2 = "";
 				if(count==0)
 				{
 					count++;
 					storeing = data;
 					Log.i("check", "count==0");
 				}
 				else
 				{
 					if(count<10 && storeing == data)
 					{
 						count++;
 						Log.i("check", "count<10 && storeing == data");
 						
 					}
 					else
 					{
 						if(count<10 && storeing != data)
 						{
 							count = 0;
 							Log.i("check", "count<10 && storeing != data");
 						}
 						else
 						{
 							if(count == 10)
 							{
 								mSnakeView.updateWithSensor(directionx, datay, dataz);
 								count++;
 								Log.i("Direction update", "count == 10");
 							}
 							else
 							{
 								if(storeing != data)
 								{
 									count=0;
 									Log.i("check", "storeing != data");
 								}
 								else
 								{
 									count++;
 									Log.i("check", "count++");
 								}
 							}
 						}
 					}
 				}
 				
 					
 				
 				
 				/*
 				*/
 				//val1.setText(x2);
 				//if ("stomp".equalsIgnoreCase(data)) {
 					//view.flyCow();	
 				//}
 				
 				//Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
 			}else{
 				Log.i("data in main class", "bundle null");
 				//Toast.makeText(getApplicationContext(), "not", Toast.LENGTH_SHORT).show();
 			}
 			//handleResult(bundle);
 		}
    };
       
    
    public void bIzquierda(View view) {
    	mSnakeView.bIzquierda();
    }
    
    public void bArriba(View view) {
    	mSnakeView.bArriba();
    }
    
    public void bAbajo(View view) {
    	mSnakeView.bAbajo();
    }
    
    public void bDerecha(View view) {
    	mSnakeView.bDerecha();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.snake_menu, menu);
    	menu.findItem(R.id.menu_about).setIcon(
        	getResources().getDrawable(android.R.drawable.ic_menu_info_details));
    	menu.findItem(R.id.menu_records).setIcon(
            getResources().getDrawable(android.R.drawable.ic_menu_view));
    	menu.findItem(R.id.menu_zoom).setIcon(
            getResources().getDrawable(android.R.drawable.ic_menu_zoom));
    	menu.findItem(R.id.menu_input).setIcon(
                getResources().getDrawable(android.R.drawable.ic_menu_manage));
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (mSnakeView.getMode() == SnakeView.RUNNING)
            mSnakeView.setMode(SnakeView.PAUSE);
        menu.findItem(R.id.menu_walls).setIcon(getResources().getDrawable(
        	mSnakeView.mUseWalls ? android.R.drawable.button_onoff_indicator_on :
        	android.R.drawable.button_onoff_indicator_off));
        menu.findItem(R.id.menu_speed).setIcon(getResources().getDrawable(
            	mSnakeView.mFast ? android.R.drawable.button_onoff_indicator_on :
            	android.R.drawable.button_onoff_indicator_off));
   	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_about:
        	showDialog(DIALOG_ABOUT_ID);
            return true;
        case R.id.menu_records:
        	showDialog(DIALOG_RECORDS_ID);
            return true;
        case R.id.menu_walls:
        	if (mSnakeView.getMode() == SnakeView.PAUSE)
        	    showDialog(DIALOG_WALLS_ID);
        	else {
          	    mSnakeView.changeWalls();
        	    item.setIcon(getResources().getDrawable(
               	    mSnakeView.mUseWalls ? android.R.drawable.button_onoff_indicator_on :
               	    android.R.drawable.button_onoff_indicator_off));
                mSnakeView.setMode(SnakeView.READY);
        	}
           return true;
        case R.id.menu_zoom:
        	if (mSnakeView.getMode() == SnakeView.PAUSE)
        	    showDialog(DIALOG_SIZE_ID);
        	else {
        	    mSnakeView.zoomTileSize();
                mSnakeView.setMode(SnakeView.READY);
        	}
            return true;
        case R.id.menu_speed:
        	if (mSnakeView.getMode() == SnakeView.PAUSE)
        	    showDialog(DIALOG_SPEED_ID);
        	else {
        	    mSnakeView.changeSpeed();
        	    item.setIcon(getResources().getDrawable(
               	    mSnakeView.mFast ? android.R.drawable.button_onoff_indicator_on :
               	    android.R.drawable.button_onoff_indicator_off));
                mSnakeView.setMode(SnakeView.READY);
        	}
           return true;
        case R.id.menu_input:
        	if (mSnakeView.getMode() == SnakeView.PAUSE)
        	    showDialog(DIALOG_INPUT_ID);
        	else {
        	    changeInputMode();
        	}
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    void changeInputMode() {
        CharSequence str = "";
        Resources res = getResources();
        
    	if (mSnakeView.inputMode == SnakeView.INPUT_MODE_4K) {
    		mSnakeView.inputMode = SnakeView.INPUT_MODE_2K;
    		str = res.getText(R.string.toast_input2k);
    	} else if (mSnakeView.inputMode == SnakeView.INPUT_MODE_2K) {
    		mSnakeView.inputMode = SnakeView.INPUT_MODE_OG;
    		mSnakeView.firstTime = true;
    		str = res.getText(R.string.toast_inputog);
    	} else {
    		mSnakeView.inputMode = SnakeView.INPUT_MODE_4K;
    		str = res.getText(R.string.toast_input4k);
    		mSnakeView.firstTime = true;
    	}
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    	setCorrectButtons();
        mSnakeView.setMode(SnakeView.READY);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        Resources res = getBaseContext().getResources();
        AlertDialog.Builder builder;
        
        switch(id) {
        case DIALOG_ABOUT_ID:
        	LayoutInflater inflatera = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        	View layouta = inflatera.inflate(R.layout.about_layout,
                    (ViewGroup) findViewById(R.id.about_layout_root));
        	AlertDialog.Builder buildera = new AlertDialog.Builder(this);
        	buildera.setTitle(R.string.about_title);
        	buildera.setIcon(R.drawable.icon);
        	buildera.setView(layouta);

        	buildera.setPositiveButton(R.string.dlg_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	dialog.cancel();
                    }
                });
        	buildera.setNegativeButton(R.string.news_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        	showDialog(DIALOG_NEWS_ID);
                        	dialog.cancel();
                        }
                    });
        	
        	dialog = buildera.create();
        	dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            break;
        case DIALOG_NEWS_ID:
        	LayoutInflater inflatern = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        	View layoutn = inflatern.inflate(R.layout.news_layout,
                    (ViewGroup) findViewById(R.id.news_layout_root));
        	AlertDialog.Builder buildern = new AlertDialog.Builder(this);
        	buildern.setTitle(R.string.news_title);
        	buildern.setIcon(R.drawable.headeattile);
        	buildern.setView(layoutn);

        	buildern.setPositiveButton(R.string.dlg_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	mSnakeView.showNews18 = false;
                    	dialog.cancel();
                    }
                });
        	
        	dialog = buildern.create();
        	dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            break;
        case DIALOG_RECORDS_ID:
        	LayoutInflater inflaterr = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        	View layoutr = inflaterr.inflate(R.layout.records_layout,
                    (ViewGroup) findViewById(R.id.records_layout_root));
        	AlertDialog.Builder builderr = new AlertDialog.Builder(this);
        	builderr.setTitle(R.string.records_title);
        	builderr.setIcon(R.drawable.appletile);
        	builderr.setView(layoutr);

        	builderr.setPositiveButton(R.string.dlg_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	dialog.cancel();
                    }
                });
        	
        	dialog = builderr.create();
        	dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            break;
        case DIALOG_WALLS_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(res.getString(R.string.dlg_walls_title))
        	       .setCancelable(false)
//        	       .setIcon(R.drawable.headtile)
        	       .setPositiveButton(res.getString(R.string.dlg_yes),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	           	   mSnakeView.changeWalls();
           	               mSnakeView.setMode(SnakeView.READY);
        	               dialog.cancel();
        	           }
        	       })
        	       .setNegativeButton(res.getString(R.string.dlg_no),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
       	                   dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();
        	break;
        case DIALOG_SIZE_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(res.getString(R.string.dlg_size_title))
        	       .setCancelable(false)
        	       .setPositiveButton(res.getString(R.string.dlg_yes),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	           	   mSnakeView.zoomTileSize();
           	               mSnakeView.setMode(SnakeView.READY);
        	               dialog.cancel();
        	           }
        	       })
        	       .setNegativeButton(res.getString(R.string.dlg_no),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
       	                   dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();
        	break;
        case DIALOG_SPEED_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(res.getString(R.string.dlg_speed_title))
        	       .setCancelable(false)
        	       .setPositiveButton(res.getString(R.string.dlg_yes),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	           	   mSnakeView.changeSpeed();
           	               mSnakeView.setMode(SnakeView.READY);
        	               dialog.cancel();
        	           }
        	       })
        	       .setNegativeButton(res.getString(R.string.dlg_no),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
       	                   dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();
        	break;
        case DIALOG_INPUT_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(res.getString(R.string.dlg_input_title))
        	       .setCancelable(false)
        	       .setPositiveButton(res.getString(R.string.dlg_yes),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   changeInputMode();
        	               dialog.cancel();
        	           }
        	       })
        	       .setNegativeButton(res.getString(R.string.dlg_no),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
       	                   dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();
        	break;
        case DIALOG_EXIT_ID:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(res.getString(R.string.dlg_exit_title))
        	       .setCancelable(false)
        	       .setPositiveButton(res.getString(R.string.dlg_yes),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   finish();
        	           }
        	       })
        	       .setNegativeButton(res.getString(R.string.dlg_no),
        	    	   new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
       	                   dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();
        	break;
        default:
            dialog = null;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch(id) {
        case DIALOG_RECORDS_ID:
            TextView mTView = (TextView) dialog.findViewById(R.id.records_layout_text);
            mTView.setText(mSnakeView.getRecordsText());
            break;
        }
    }

    @Override
    public void onBackPressed() {
    	if (mSnakeView.getMode() == SnakeView.RUNNING) {
            mSnakeView.setMode(SnakeView.PAUSE);
    	    showDialog(DIALOG_EXIT_ID);
    	} else if (mSnakeView.getMode() == SnakeView.PAUSE) {
    	    showDialog(DIALOG_EXIT_ID);
    	} else
    	    finish();
    }

}
