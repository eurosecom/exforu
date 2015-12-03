package com.eusecom.exforu;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;



@SuppressWarnings("deprecation")
public class SettingsActivity extends android.preference.PreferenceActivity {
	

	
	public static final String USER_ID = "userid";
	public static final String USER_PSW = "userpsw";
	public static final String REPEAT = "repeat";
	public static final String STREAMF = "streamf";
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.settings);
	}


	
	public static String getUserId(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_ID, "0");
	}
	
	public static String getUserPsw(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_PSW, "");
	}
	
	public static String getRepeat(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(REPEAT, "60");
	}
	
	public static String getStreamf(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(STREAMF, "60");
	}
	

	
} 