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

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SnakeView: implementation of a simple game of Snake
 */
public class SnakeView extends TileView {

//    private static final String TAG = "MSnake";

    /**
     * Current mode of application: READY to run, RUNNING, or you have already
     * lost. static final ints are used instead of an enum for performance
     * reasons.
     */
    private int mMode = READY;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    String x2;
    /**
     * Current direction the snake is headed.
     */
    private int mDirection = NORTH;
    private int mNextDirection = NORTH;
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;

    private int mBoardSize = BS_NORMAL;
    public static final int BS_BIG = 0;
    public static final int BS_NORMAL = 1;
    public static final int BS_SMALL = 2;
    private int tileSizes[] = {8,16,32};
    
    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
    private static final int BODY_TILE = 1;
    private static final int FOOD_TILE = 2;
    private static final int GREENFOOD_TILE = 3;
    private static final int REDFOOD_TILE = 4;
    private static final int WALL_TILE = 5;
    private static final int HEAD_TILE = 6;
    private static final int HEAD2_TILE = 7;
    private static final int HEADEAT_TILE = 8;
    private static final int HEADBAD_TILE = 9;
    
    private boolean mDrawHead2 = false;
    private boolean mDrawHeadEat = false;

    /**
     * mScore: used to track the number of apples captured mMoveDelay: number of
     * milliseconds between snake movements. This will decrease as apples are
     * captured.
     */
    private long mScore = 0;
    private long mMoveDelay = 500;
    public static final int RECORD_BIG_WON = 0;
    public static final int RECORD_BIG_WOFF = 1;
    public static final int RECORD_NORMAL_WON = 2;
    public static final int RECORD_NORMAL_WOFF = 3;
    public static final int RECORD_SMALL_WON = 4;
    public static final int RECORD_SMALL_WOFF = 5;
    public static final int RECORD_BIG_WON_FAST = 6;
    public static final int RECORD_BIG_WOFF_FAST = 7;
    public static final int RECORD_NORMAL_WON_FAST = 8;
    public static final int RECORD_NORMAL_WOFF_FAST = 9;
    public static final int RECORD_SMALL_WON_FAST = 10;
    public static final int RECORD_SMALL_WOFF_FAST = 11;
    private int indRecord = RECORD_NORMAL_WON;
    private long mRecords[] = new long[12];
    
    private int numcollision = 0;
    public boolean firstTime = true;
    public boolean showNews17 = true;
    public boolean showNews18 = true;

    public boolean noSmallSize = false;
    
    public static final int INPUT_MODE_4K = 0;
    public static final int INPUT_MODE_2K = 1;
    public static final int INPUT_MODE_OG = 2;
    public int inputMode = INPUT_MODE_OG;
    public boolean tlrInputMode = false;
    
    /**
     * mLastMove: tracks the absolute time when the snake last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
    private long mLastMove;
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;

    /**
     * mScoreText: text shows score and record
     */
    private TextView mScoreText;
    private TextView mRecordText;

    /**
     * mUseWalls: Use walls or not
     */
    public boolean mUseWalls = true;

    /**
     * mFast: Fast or Slow
     */
    public boolean mFast = false;

    /**
     * mSnakeTrail: a list of Coordinates that make up the snake's body
     * mAppleList: the secret location of the juicy apples the snake craves.
     */
    private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mAppleList = new ArrayList<Coordinate>();
    private Coordinate mRedApple = new Coordinate(1,1);
    private boolean mActiveRedApple = false;
    private Coordinate mGreenApple = new Coordinate(1,1);
    private boolean mActiveGreenApple = false;

    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();

    /**
     */
    private Vibrator mVibrator;

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            SnakeView.this.update();
            SnakeView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    /**
     * Constructs a SnakeView based on inflation from XML
     * 
     * @param context
     * @param attrs
     */
    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i=0; i<12; i++) mRecords[i] = 0;
        initSnakeView();
   }

    public SnakeView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
        for (int i=0; i<12; i++) mRecords[i] = 0;
    	initSnakeView();
    }
    
    public void setTileSizes(int w, int h) {
        if (h < 300 || w < 300) noSmallSize = true; // qvga device
        if (h < 400 || w < 400) { // qvga & hvga devices
            tileSizes[0] = 6;
            tileSizes[1] = 12;
            tileSizes[2] = 24;
        }
    }

    private void initSnakeView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetBitmapTiles(10);
        loadBitmapTile(BODY_TILE, r.getDrawable(R.drawable.bodytile));
        loadBitmapTile(FOOD_TILE, r.getDrawable(R.drawable.appletile));
        loadBitmapTile(GREENFOOD_TILE, r.getDrawable(R.drawable.peppertile));
        loadBitmapTile(REDFOOD_TILE, r.getDrawable(R.drawable.redpeppertile));
        loadBitmapTile(WALL_TILE, r.getDrawable(R.drawable.walltile2));
        loadBitmapTile(HEAD_TILE, r.getDrawable(R.drawable.headtile));
        loadBitmapTile(HEAD2_TILE, r.getDrawable(R.drawable.head2tile));
        loadBitmapTile(HEADEAT_TILE, r.getDrawable(R.drawable.headeattile));
        loadBitmapTile(HEADBAD_TILE, r.getDrawable(R.drawable.headbadtile));
    	
    }

    public void initNewGame() {
        mSnakeTrail.clear();
        mAppleList.clear();

        // For now we're just going to load up a short default eastbound snake
        // that's just turned north
        mSnakeTrail.add(new Coordinate(7, 7));
        mSnakeTrail.add(new Coordinate(6, 7));
        mSnakeTrail.add(new Coordinate(5, 7));
        mSnakeTrail.add(new Coordinate(4, 7));
        mSnakeTrail.add(new Coordinate(3, 7));
        mSnakeTrail.add(new Coordinate(2, 7));
        mNextDirection = NORTH;

        // Two apples to start with
        mActiveGreenApple = false;
        mActiveRedApple = false;
        addRandomApple();
        addRandomApple();

        mMoveDelay = (mFast) ? 250 : 500;
        mScore = 0;
    }

    public void setVibrator(Vibrator v) {
    	mVibrator = v;
    }

    private void setRecordIndex() {
    	if (mUseWalls) {
    		if (mBoardSize == BS_SMALL) indRecord = RECORD_SMALL_WON;
    		else if (mBoardSize == BS_NORMAL) indRecord = RECORD_NORMAL_WON;
    		else indRecord = RECORD_BIG_WON;
    	} else {
    		if (mBoardSize == BS_SMALL) indRecord = RECORD_SMALL_WOFF;
    		else if (mBoardSize == BS_NORMAL) indRecord = RECORD_NORMAL_WOFF;
    		else indRecord = RECORD_BIG_WOFF;
    	}
    	if (mFast) indRecord += 6;
    }
    
    private int indrtline[] = {R.string.records_line0, R.string.records_line1,
    		R.string.records_line2, R.string.records_line3, R.string.records_line4,
    		R.string.records_line5, R.string.records_line6, R.string.records_line7,
    		R.string.records_line8, R.string.records_line9, R.string.records_line10,
    		R.string.records_line11};

    public String getRecordsText() {
    	String str = "";
        Resources res = getContext().getResources();
    	
        str = res.getString(R.string.records_linet);
        for (int i=0; i<12; i++) {
        	if (noSmallSize && (i==4 || i==5 || i==10 || i==11)) continue;
//        	if (i == indRecord) str = str + "<b>";
        	str = str + res.getString(indrtline[i]) + mRecords[i];
        	if (i == indRecord) str = str + "*";
//        	if (i == indRecord) str = str + "</b>";
//        	str = str + "<br>";
        }
    	str = str + "\n";
    	return str;
    }
    
    public void zoomTileSize(){
        Resources res = getContext().getResources();
        CharSequence str = "";

        if (mBoardSize == BS_SMALL) {
        	mBoardSize = BS_NORMAL;
        	mTileSize = tileSizes[BS_NORMAL];
    		str = res.getText(R.string.toast_zoomnormal);
        }
    	else if (mBoardSize == BS_NORMAL) {
        	mBoardSize = BS_BIG;
        	mTileSize = tileSizes[BS_BIG];
    		str = res.getText(R.string.toast_zoombig);
    	}
    	else {
    		// Don't use the small size in QVGA devices
    		if (!noSmallSize){
            	mBoardSize = BS_SMALL;
            	mTileSize = tileSizes[BS_SMALL];
    		    str = res.getText(R.string.toast_zoomsmall);
    		} else {
            	mBoardSize = BS_NORMAL;
            	mTileSize = tileSizes[BS_NORMAL];
        		str = res.getText(R.string.toast_zoomnormal);
    		}
    	}
        setRecordIndex();
    	recalcTileGrid();
    	initSnakeView();
		Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
    
    public void changeWalls(){
        Resources res = getContext().getResources();
        CharSequence str = "";

        mUseWalls = !mUseWalls;
    	clearTiles();
    	if (mUseWalls) {
    		str = res.getText(R.string.toast_wallson);
    	}
    	else {
    		str = res.getText(R.string.toast_wallsoff);
    	}
        setRecordIndex();
		Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
    
    public void changeSpeed(){
        Resources res = getContext().getResources();
        CharSequence str = "";

        mFast = !mFast;
    	clearTiles();
    	if (mFast) {
    		str = res.getText(R.string.toast_faston);
    	}
    	else {
    		str = res.getText(R.string.toast_fastoff);
    	}
        setRecordIndex();
		Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
    
    public void savePreferences(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putBoolean("mUseWalls", mUseWalls);
        editor.putBoolean("mFast", mFast);
        editor.putInt("mTileSize", mTileSize);
        editor.putInt("mBoardSize", mBoardSize);
        editor.putLong("mRecords0", mRecords[0]);
        editor.putLong("mRecords1", mRecords[1]);
        editor.putLong("mRecords2", mRecords[2]);
        editor.putLong("mRecords3", mRecords[3]);
        editor.putLong("mRecords4", mRecords[4]);
        editor.putLong("mRecords5", mRecords[5]);
        editor.putLong("mRecords6", mRecords[6]);
        editor.putLong("mRecords7", mRecords[7]);
        editor.putLong("mRecords8", mRecords[8]);
        editor.putLong("mRecords9", mRecords[9]);
        editor.putLong("mRecords10", mRecords[10]);
        editor.putLong("mRecords11", mRecords[11]);
        editor.putBoolean("showNews18", showNews18);
//        editor.putBoolean("tlrInputMode", tlrInputMode);
        editor.putInt("inputMode", inputMode);
        editor.commit();
    }

    public void restorePreferences(SharedPreferences settings) {
    	mUseWalls = settings.getBoolean("mUseWalls", true);
    	mFast = settings.getBoolean("mFast", false);
    	mTileSize = settings.getInt("mTileSize", tileSizes[BS_NORMAL]);
    	mBoardSize = settings.getInt("mBoardSize", BS_NORMAL);
    	mRecords[0] = settings.getLong("mRecords0", 0);
    	mRecords[1] = settings.getLong("mRecords1", 0);
    	mRecords[2] = settings.getLong("mRecords2", 0);
    	mRecords[3] = settings.getLong("mRecords3", 0);
    	mRecords[4] = settings.getLong("mRecords4", 0);
    	mRecords[5] = settings.getLong("mRecords5", 0);
    	mRecords[6] = settings.getLong("mRecords6", 0);
    	mRecords[7] = settings.getLong("mRecords7", 0);
    	mRecords[8] = settings.getLong("mRecords8", 0);
    	mRecords[9] = settings.getLong("mRecords9", 0);
    	mRecords[10] = settings.getLong("mRecords10", 0);
    	mRecords[11] = settings.getLong("mRecords11", 0);
    	showNews18 = settings.getBoolean("showNews18", true);
    	inputMode = settings.getInt("inputMode", INPUT_MODE_OG);
    	tlrInputMode = settings.getBoolean("tlrInputMode", false);
    	showNews17 = settings.getBoolean("showNews17", true);
    	if (!showNews17) inputMode = INPUT_MODE_4K;
    	if (tlrInputMode) inputMode = INPUT_MODE_2K;
    	initSnakeView();
    	setRecordIndex();
    }

    /**
     * Given a ArrayList of coordinates, we need to flatten them into an array of
     * ints before we can stuff them into a map for flattening and storage.
     * 
     * @param cvec : a ArrayList of Coordinate objects
     * @return : a simple array containing the x/y values of the coordinates
     * as [x1,y1,x2,y2,x3,y3...]
     */
    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    /**
     * Save game state so that the user does not lose anything
     * if the game process is killed while we are in the 
     * background.
     * 
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putInt("mNextDirection", Integer.valueOf(mNextDirection));
        map.putInt("mTileSize", Integer.valueOf(mTileSize));
        map.putInt("mBoardSize", Integer.valueOf(mBoardSize));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putLongArray("mRecords", mRecords);
        map.putInt("indRecords", Integer.valueOf(indRecord));
        map.putBoolean("mUseWalls", Boolean.valueOf(mUseWalls));
        map.putBoolean("mFast", Boolean.valueOf(mFast));
        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));
        map.putInt("RedApplex", mRedApple.x);
        map.putInt("RedAppley", mRedApple.y);
        map.putBoolean("mActiveRedApple", Boolean.valueOf(mActiveRedApple));
        map.putInt("GreenApplex", mGreenApple.x);
        map.putInt("GreenAppley", mGreenApple.y);
        map.putBoolean("mActiveGreenApple", Boolean.valueOf(mActiveGreenApple));
        map.putInt("inputMode", inputMode);

        return map;
    }

    /**
     * Given a flattened array of ordinate pairs, we reconstitute them into a
     * ArrayList of Coordinate objects
     * 
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return a ArrayList of Coordinates
     */
    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }

    /**
     * Restore game state if our process is being relaunched
     * 
     * @param icicle a Bundle containing the game state
     */
    public void restoreState(Bundle icicle) {
        setMode(PAUSE);

        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mTileSize = icicle.getInt("mTileSize");
        mBoardSize = icicle.getInt("mBoardSize");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mRecords = icicle.getLongArray("mRecords");
        indRecord = icicle.getInt("indRecord");
        mUseWalls = icicle.getBoolean("mUseWalls");
        mFast = icicle.getBoolean("mFast");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
        mRedApple.x = icicle.getInt("RedApplex");
        mRedApple.y = icicle.getInt("RedAppley");
        mActiveRedApple = icicle.getBoolean("mActiveRedApple");
        mGreenApple.x = icicle.getInt("GreenApplex");
        mGreenApple.y = icicle.getInt("GreenAppley");
        mActiveGreenApple = icicle.getBoolean("mActiveGreenApple");
        inputMode = icicle.getInt("inputMode");
    }

    /*
     * handles key events in the game. Update the direction our snake is traveling
     * based on the DPAD. Ignore events that would cause the snake to immediately
     * turn back on itself.
     * 
     * (non-Javadoc)
     * 
     * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
     */
    
   
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (mMode == READY) {
                // no initNewGame() because we have initiated it in setMode
                setMode(RUNNING);
                return (true);
            }

            if (mMode == LOSE) {
                initNewGame();
                setMode(RUNNING);
                return (true);
            }

            if (mMode == PAUSE) {
                setMode(RUNNING);
                return (true);
            }

            setMode(PAUSE);
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mDirection != SOUTH) {
                mNextDirection = NORTH;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mDirection != NORTH) {
                mNextDirection = SOUTH;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mDirection != EAST) {
                mNextDirection = WEST;
            }
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mDirection != WEST) {
                mNextDirection = EAST;
            }
            return (true);
        }

        return super.onKeyDown(keyCode, msg);
    }
    
    private float savedX;
    private float savedY;
    private int savedMode;
    
    static Float x11;
	static Float y11;
	static Float z11;
	
	
			
	
    public void updateWithSensor(String x, String y, String z) {
		// TODO Auto-generated method stub
		//String x2="";
    	Float directionx = Float.valueOf(x);
		Float directiony = Float.valueOf(z);
		Float directionz = Float.valueOf(y);
		x11= directionx;
		y11=directiony;
		z11=directionz;
		Log.i("Direction Update", "Called");
		if(directionx>0){
			x2 += "Up\t";
			if (mDirection != NORTH) {
                mNextDirection = SOUTH;
                Utilities.updatedownmotions(1);
                Utilities.updatecalories(10);
            }
		}
		else
		{
			x2 += "Down\t";
			 if (mDirection != SOUTH) {
	                mNextDirection = NORTH;
	                Utilities.updateupmotions(1);
	                Utilities.updatecalories(10);
	            }
		}
		if(directiony>0){
			x2 += "Up\t";
		}
		else
		{
			x2 += "Down\t";
		}
		if(directionz>0){
			x2 += "Up\t";
		}
		else
		{
			x2 += "Down\t";
		}
		
		
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		savedX = event.getX();
    		savedY = event.getY();
    		savedMode = mMode;
    		return (true);
    	}

    	if (event.getAction() != MotionEvent.ACTION_UP)
    		return (true);
    	
    	if (savedMode != mMode)
    		return (true);
    	
    	float distX = savedX - event.getX();
    	float distY = savedY - event.getY();
    	float adistX = Math.abs(distX);
    	float adistY = Math.abs(distY);

        if (adistX+adistY >= 10) {
        	if (adistX > adistY) {
        		if (distX > 0) {
                    if (mDirection != EAST) mNextDirection = WEST;
                    Utilities.updateleftmotions(1);
                    Utilities.updatecalories(10);
        		} else {
                    if (mDirection != WEST) mNextDirection = EAST;
                    Utilities.updaterightmotions(1);
                    Utilities.updatecalories(10);
        		}
        	} else {
        		if (distY > 0) {
                    if (mDirection != SOUTH) mNextDirection = NORTH;
                    Utilities.updateupmotions(1);
                    Utilities.updatecalories(10);
        		} else {
                    if (mDirection != NORTH) mNextDirection = SOUTH;
                    Utilities.updatedownmotions(1);
                    Utilities.updatecalories(10);
        		}
        	}
        }

        if (mMode == READY) {
            // no initNewGame() because we have initiated it in setMode
            setMode(RUNNING);
            // return (true);
        } else if (mMode == LOSE) {
            initNewGame();
            setMode(RUNNING);
            // return (true);
        } else if (mMode == PAUSE) {
            setMode(RUNNING);
            //return (true);
        } else if (mMode == RUNNING) {
        	if (adistX+adistY < 10) {
                setMode(PAUSE);
                //return (true);
        	}
        }

        return (true);
    }

    public void bIzquierda() {
    	if (inputMode == INPUT_MODE_2K) {
    		if (mDirection == EAST) mNextDirection = NORTH;
    		else if (mDirection == NORTH) mNextDirection = WEST;
    		else if (mDirection == WEST) mNextDirection = SOUTH;
    		else mNextDirection = EAST;
    		return;
    	}
        if (mDirection != EAST) {
            mNextDirection = WEST;
        }
    }

    public void bArriba() {
    	if (inputMode == INPUT_MODE_2K) return;
        if (mDirection != SOUTH) {
            mNextDirection = NORTH;
        }
    }
    
    public void bAbajo() {
    	if (inputMode == INPUT_MODE_2K) return;
        if (mDirection != NORTH) {
            mNextDirection = SOUTH;
        }
    }
    
    public void bDerecha() {
    	if (inputMode == INPUT_MODE_2K) {
    		if (mDirection == EAST) mNextDirection = SOUTH;
    		else if (mDirection == NORTH) mNextDirection = EAST;
    		else if (mDirection == WEST) mNextDirection = NORTH;
    		else mNextDirection = WEST;
    		return;
    	}
        if (mDirection != WEST) {
            mNextDirection = EAST;
        }
    }
    
    /**
     * Sets the TextView that will be used to give information (such as "Game
     * Over" to the user.
     * 
     * @param newView
     */
    public void setTextView(TextView newView) {
        mStatusText = newView;
    }

    /**
     * Sets the TextView that will be used to show score and record
     * 
     * @param newView
     */
    public void setScoreView(TextView newView) {
        mScoreText = newView;
    }

    public void setRecordView(TextView newView) {
        mRecordText = newView;
    }

    /**
     * Updates the current mode of the application (RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     * 
     * @param newMode
     */
    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        updateScore();

        if (newMode == RUNNING & oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            invalidate();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE) {
            str = res.getText(R.string.mode_pause);
        }
        if (newMode == READY) {
            if (!firstTime) {
            	initNewGame();
            	updateElements();
                invalidate();
            }
            str = res.getText(R.string.mode_ready);
        }
        if (newMode == LOSE) {
        	if (mScore > mRecords[indRecord]) {
        		mRecords[indRecord] = mScore;
                str = res.getString(R.string.mode_lose_prefix_cr) + mScore
                  + res.getString(R.string.mode_lose_suffix);
        	} else {
                str = res.getString(R.string.mode_lose_prefix) + mScore
                  + res.getString(R.string.mode_lose_suffix);
        	}
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    public int getMode() {
    	return mMode;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (firstTime && mMode == READY) {
        	initNewGame();
        	updateElements();
        	firstTime = false;
        }
    }

    /**
     * Selects a random location within the garden that is not currently covered
     * by the snake. Currently _could_ go into an infinite loop if the snake
     * currently fills the garden, but we'll leave discovery of this prize to a
     * truly excellent snake-player.
     * 
     */
    private Coordinate findFreeCoordinate() {
        Coordinate newCoord = null;
        boolean found = false;
        while (!found) {
            // Choose a new location for our apple
            int newX = 1 + RNG.nextInt(mXTileCount - 2);
            int newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = new Coordinate(newX, newY);

            // Make sure it's not already under the snake
            boolean collision = false;
            int snakelength = mSnakeTrail.size();
            for (int index = 0; index < snakelength; index++) {
                if (mSnakeTrail.get(index).equals(newCoord)) {
                    collision = true;
                	numcollision++;
                }
            }
            int applelength = mAppleList.size();
            for (int index = 0; index < applelength; index++) {
                if (mAppleList.get(index).equals(newCoord)) {
                    collision = true;
                	numcollision++;
                }
            }
            if (mActiveRedApple && mRedApple.equals(newCoord)) {
                collision = true;
            	numcollision++;
            }
            if (mActiveGreenApple && mGreenApple.equals(newCoord)) {
                collision = true;
            	numcollision++;
            }
            // if we're here and there's been no collision, then we have
            // a good location for an apple. Otherwise, we'll circle back
            // and try again
            found = !collision;
        }
        return newCoord;   	    
    }

    private void addRandomApple() {
        Coordinate newCoord = null;
        
        newCoord = findFreeCoordinate();
        mAppleList.add(newCoord);
    }

    private void addRedApple() {
    	mRedApple = findFreeCoordinate();
        mActiveRedApple = true;
    }

    private void addGreenApple() {
    	mGreenApple = findFreeCoordinate();
        mActiveGreenApple = true;
    }

    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the snake's location.
     */
    public void update() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastMove >= mMoveDelay) {
            	updateElements();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }
    }

    public void updateElements() {
       clearTiles();
       updateWalls();
       updateSnake();
       updateApples();
    }

    /**
     * Draws some walls.
     * 
     */
    private void updateWalls() {
    	if (!mUseWalls) return;
        for (int x = 0; x < mXTileCount; x++) {
            setTile(WALL_TILE, x, 0);
            setTile(WALL_TILE, x, mYTileCount - 1);
        }
        for (int y = 1; y < mYTileCount - 1; y++) {
            setTile(WALL_TILE, 0, y);
            setTile(WALL_TILE, mXTileCount - 1, y);
        }
    }

    /**
     * Draws some apples.
     * 
     */
    private void updateApples() {
        for (Coordinate c : mAppleList) {
            setTile(FOOD_TILE, c.x, c.y);
        }
        if (mActiveRedApple) {
            setTile(REDFOOD_TILE, mRedApple.x, mRedApple.y);
        }
        if (mActiveGreenApple) {
            setTile(GREENFOOD_TILE, mGreenApple.x, mGreenApple.y);
        }
    }

    /**
     * Figure out which way the snake is going, see if he's run into anything (the
     * walls, himself, or an apple). If he's not going to die, we then add to the
     * front and subtract from the rear in order to simulate motion. If we want to
     * grow him, we don't subtract from the rear.
     * 
     */
    private void updateSnake() {
        boolean growSnake = false;
        boolean mustAddRandomApple = false;
        boolean mustAddGreenApple = false;

        // grab the snake by the head
        Coordinate head = mSnakeTrail.get(0);
        Coordinate newHead = new Coordinate(1, 1);

        mDirection = mNextDirection;

        switch (mDirection) {
        case EAST: {
            newHead = new Coordinate(head.x + 1, head.y);
            break;
        }
        case WEST: {
            newHead = new Coordinate(head.x - 1, head.y);
            break;
        }
        case NORTH: {
            newHead = new Coordinate(head.x, head.y - 1);
            break;
        }
        case SOUTH: {
            newHead = new Coordinate(head.x, head.y + 1);
            break;
        }
        }

        // Collision detection with walls if we are suing them or adjust head position
        if (mUseWalls) {
            if ((newHead.x < 1) || (newHead.y < 1) || (newHead.x > mXTileCount - 2)
                    || (newHead.y > mYTileCount - 2)) {
                mVibrator.vibrate(300);
                setMode(LOSE);
                drawSnakeBad();
                return;
            }
        }
        else {
          	if (newHead.x < 0) newHead.x = mXTileCount - 1;
           	if (newHead.x > mXTileCount - 1) newHead.x = 0;
           	if (newHead.y < 0) newHead.y = mYTileCount - 1;
           	if (newHead.y > mYTileCount - 1) newHead.y = 0;
        }

        // Look for collisions with itself
        int snakelength = mSnakeTrail.size();
        for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {
            Coordinate c = mSnakeTrail.get(snakeindex);
            if (c.equals(newHead)) {
                mVibrator.vibrate(400);
                setMode(LOSE);
                drawSnakeBad();
                return;
            }
        }

        // Look for apples
        int applecount = mAppleList.size();
        for (int appleindex = 0; appleindex < applecount; appleindex++) {
            Coordinate c = mAppleList.get(appleindex);
            if (c.equals(newHead)) {
                mAppleList.remove(c);
                mustAddRandomApple = true;
                mScore++;
                mMoveDelay *= 0.95;
                updateScore();
                mVibrator.vibrate(100);
                mDrawHeadEat = true;
                growSnake = true;
                break;
            }
        }
        // Look for red apple
        if (mActiveRedApple && mRedApple.equals(newHead)){
        	mActiveRedApple = false;
        	mScore += 2;
        	mMoveDelay = (mFast) ? 150 : 300;
            updateScore();
            mVibrator.vibrate(100);
            mDrawHeadEat = true;
            mustAddGreenApple = true;
        }
        // Look for green apple
        if (mActiveGreenApple && mGreenApple.equals(newHead)){
        	mActiveGreenApple = false;
        	mScore += 3;
        	mMoveDelay /= 2;
            updateScore();
            mVibrator.vibrate(100);
            mDrawHeadEat = true;
            growSnake = true;
        }

        // push a new head onto the ArrayList and pull off the tail
        mSnakeTrail.add(0, newHead);
        // except if we want the snake to grow
        if (!growSnake) {
            mSnakeTrail.remove(mSnakeTrail.size() - 1);
        }

        int index = 0;
        for (Coordinate c : mSnakeTrail) {
            if (index == 0) {
            	if (mDrawHeadEat)
                    setTile(HEADEAT_TILE, c.x, c.y);
            	else if (mDrawHead2)
                    setTile(HEAD2_TILE, c.x, c.y);
            	else
                    setTile(HEAD_TILE, c.x, c.y);
            	mDrawHeadEat = false;
            	mDrawHead2 = !mDrawHead2;
            } else {
                setTile(BODY_TILE, c.x, c.y);
            }
            index++;
        }
        
        if (mustAddRandomApple) addRandomApple();
        if (mustAddGreenApple) addGreenApple();
        
        int limit = (mFast) ? 90 : 150;
        if (mMoveDelay < limit ){
        	if (!mActiveRedApple) {
        		addRedApple();
        		mActiveGreenApple = false;
        	}
        } else {
        	if (mActiveRedApple) {
        		mActiveRedApple = false;
        	}
        }
    }

    void drawSnakeBad() {
        int index = 0;
        for (Coordinate c : mSnakeTrail) {
            if (index == 0) {
                setTile(HEADBAD_TILE, c.x, c.y);
            } else {
                setTile(BODY_TILE, c.x, c.y);
            }
            index++;
        }
    }

    /**
     * 
     */
    private void updateScore() {
        CharSequence str = "";
        Resources res = getContext().getResources();

        str = " " + res.getString(R.string.score) + " " + mScore;
        mScoreText.setText(str);
        
       str = res.getString(R.string.record) + " " + mRecords[indRecord] + " ";
       mRecordText.setText(str);

//        + "        " + numcollision
//        + "   " + dWidth + "," + dHeight;
//        + "   " + "Delay" + mMoveDelay;
    }
    
    /**
     * Simple class containing two integer values and a comparison function.
     * There's probably something I should use instead, but this was quick and
     * easy to build.
     * 
     */
    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }

   
    
}
