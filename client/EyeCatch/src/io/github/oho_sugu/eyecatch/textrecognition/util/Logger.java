package io.github.oho_sugu.eyecatch.textrecognition.util;

import android.util.Log;

public class Logger {
	static String TAG= "EyeCatch";
	public static void i(String string){
		Log.i(TAG,string);
	}
	public static void d(String string){
		Log.d(TAG,string);
	}
	public static void e(String string){
		Log.e(TAG,string);
	}
}
