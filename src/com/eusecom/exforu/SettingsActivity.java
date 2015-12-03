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
	public static final String COMMENT1 = "comment1";
	public static final String COMMENT2 = "comment2";
	public static final String COMMENT3 = "comment3";
	public static final String STARTLOT = "startlot";
	public static final String STEPLOT = "steplot";
	
	
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
	
	public static String getComment1(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(COMMENT1, "com1");
	}
	
	public static String getComment2(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(COMMENT2, "com2");
	}
	
	public static String getComment3(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(COMMENT3, "com3");
	}
	
	public static String getStartlot(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(STARTLOT, "0.1");
	}
	
	public static String getSteplot(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(STEPLOT, "0.01");
	}

	
} 