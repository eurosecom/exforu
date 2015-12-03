package com.eusecom.exforu;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;



@SuppressWarnings("deprecation")
public class SettingsActivity extends android.preference.PreferenceActivity {
	

	
	public static final String USER_ID = "userid";
	public static final String USER_PSW = "userpsw";
	public static final String USER_IDR = "useridr";
	public static final String USER_PSWR = "userpswr";
	public static final String REPEAT = "repeat";
	public static final String STREAMF = "streamf";
	public static final String ACCOUNTX = "accountx";
	public static final String PERIODX = "periodx";
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static String getPeriodx(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(PERIODX, "D1");
	}

	public static String getAccountx(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(ACCOUNTX, "0");
	}

	
	public static String getUserId(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_ID, "0");
	}
	
	public static String getUserPsw(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_PSW, "");
	}
	
	public static String getUserIdr(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_IDR, "0");
	}
	
	public static String getUserPswr(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(USER_PSWR, "");
	}
	
	public static String getRepeat(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(REPEAT, "60");
	}
	
	public static String getStreamf(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(STREAMF, "60");
	}
	

	
} 