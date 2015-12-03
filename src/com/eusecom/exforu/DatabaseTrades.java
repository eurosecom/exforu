/* Copyright (c) 2008-2009 -- CommonsWare, LLC

	 Licensed under the Apache License, Version 2.0 (the "License");
	 you may not use this file except in compliance with the License.
	 You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.
*/
	 
package com.eusecom.exforu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseTrades extends SQLiteOpenHelper {
	private static final String DATABASE_NAME3="db4";
	public static final String ITIME="itime";
	public static final String IOPEN="iopen";
	public static final String IVOLUME="ivolume";
	public static final String IORDER="iorder";
	public static final String ISYMBOL="isymbol";
	public static final String IPERIOD="iperiod";
	public static final String IDRUH="idruh";
	public static final String DATM="datm";
	public static final String DATZ="datz";
	
	
	public DatabaseTrades(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME3, null, 16);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db3) {
		
		db3.execSQL("CREATE TABLE trades (_id INTEGER PRIMARY KEY AUTOINCREMENT, itime TEXT, " +
				"iopen TEXT, ivolume TEXT, iorder TEXT, isymbol TEXT, iperiod TEXT,  " +
				"idruh TEXT, datm TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP, datz TIMESTAMP(14));");
		
		ContentValues cv3=new ContentValues();
		
		cv3.put(ITIME, "0");
		cv3.put(IOPEN, "0");
		cv3.put(IVOLUME, "0");
		cv3.put(IORDER, "0");
		cv3.put(ISYMBOL, "0");
		cv3.put(IPERIOD, "30");
		cv3.put(IDRUH, "1");
		db3.insert("trades", ITIME, cv3);
		
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db3, int oldVersion, int newVersion) {
		android.util.Log.w("trades", "Upgrading database, which will destroy all old data");
		db3.execSQL("DROP TABLE IF EXISTS trades");
		onCreate(db3);
	}
}