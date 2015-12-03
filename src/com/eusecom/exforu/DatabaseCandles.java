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


public class DatabaseCandles extends SQLiteOpenHelper {
	private static final String DATABASE_NAME3="db3";
	public static final String TIME="time";
	public static final String OPEN="open";
	public static final String CLOSE="close";
	public static final String HIGH="high";
	public static final String LOW="low";
	public static final String PERIOD="period";
	public static final String DRUH="druh";
	public static final String DATM="datm";
	public static final String DATZ="datz";
	
	
	public DatabaseCandles(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME3, null, 11);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db3) {
		
		db3.execSQL("CREATE TABLE candles (_id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, " +
				"open TEXT, close TEXT, high TEXT, low TEXT, period TEXT,  " +
				"druh TEXT, datm TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP, datz TIMESTAMP(14));");
		
		ContentValues cv3=new ContentValues();
		
		cv3.put(TIME, "0");
		cv3.put(OPEN, "0");
		cv3.put(CLOSE, "0");
		cv3.put(HIGH, "0");
		cv3.put(LOW, "0");
		cv3.put(PERIOD, "30");
		cv3.put(DRUH, "1");
		db3.insert("candles", TIME, cv3);
		
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db3, int oldVersion, int newVersion) {
		android.util.Log.w("candles", "Upgrading database, which will destroy all old data");
		db3.execSQL("DROP TABLE IF EXISTS candles");
		onCreate(db3);
	}
}