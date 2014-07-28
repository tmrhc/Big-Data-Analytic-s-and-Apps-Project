package com.fgrim.msnake;

public class Utilities {
	
	static int calories= 0;
	static int leftmotions = 0;
	static int rightmotions = 0;
	static int downmotions = 0;
	static int upmotions = 0;
	
	public static void updatecalories(int i){
		calories += i;		
	}
	
	public static void updateleftmotions(int i){
		leftmotions += i;		
	}
	
	public static void updaterightmotions(int i){
		rightmotions+= i;		
	}
	
	public static void updateupmotions(int i){
		upmotions += i;		
	}
	
	public static void updatedownmotions(int i){
		downmotions += i;		
	}
	
	public static int returncalories(){
		return calories;		
	}
	
	public static int returnupmotions(){
		return upmotions;		
	}
	
	public static int returndownmotions(){
		return downmotions;		
	}
	
	public static int returnleftmotions(){
		return leftmotions;		
	}
	
	public static int returnrightmotions(){
		return rightmotions;		
	}
	

}
