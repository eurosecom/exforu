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


public class DatabaseHistoryEvents extends SQLiteOpenHelper {
	private static final String DATABASE_NAME2="db21";
	public static final String NAMEX="namex";
	public static final String PAIRX="pairx";
	public static final String DATEX="datex";
	public static final String TIMEX="timex";
	public static final String SCOPEX="scopex";
	public static final String PERIX="perix";
	public static final String DRUHX="druhx";
	public static final String DATMX="datmx";
	public static final String DATZX="datzx";
	
	
	public DatabaseHistoryEvents(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME2, null, 23);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db21) {
		
		db21.execSQL("CREATE TABLE historyevents (_id INTEGER PRIMARY KEY AUTOINCREMENT, namex TEXT, " +
				"pairx TEXT, datex TEXT, timex TEXT, scopex TEXT, perix TEXT,  " +
				"druhx TEXT, datmx TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP, datzx TIMESTAMP(14));");
		
		ContentValues cv21=new ContentValues();
		
		//i've got data from 3.8.2010
		cv21.put(NAMEX, "USD Interest rate change 01.01.2011 D1");
		cv21.put(PAIRX, "EURUSD");
		cv21.put(DATEX, "01-01-2011");
		cv21.put(TIMEX, "14:30");
		cv21.put(SCOPEX, "120");
		cv21.put(PERIX, "D1");
		cv21.put(DRUHX, "0");
		db21.insert("historyevents", NAMEX, cv21);
		
		cv21.put(NAMEX, "USD Interest rate change 30.12.2015 M5");
		cv21.put(PAIRX, "EURUSD");
		cv21.put(DATEX, "30-12-2015");
		cv21.put(TIMEX, "20:30");
		cv21.put(SCOPEX, "120");
		cv21.put(PERIX, "M5");
		cv21.put(DRUHX, "0");
		db21.insert("historyevents", NAMEX, cv21);
		
		cv21.put(NAMEX, "US Non Farm Payroll 01.08.2015 D1");
		cv21.put(PAIRX, "EURUSD");
		cv21.put(DATEX, "01-08-2015");
		cv21.put(TIMEX, "20:00");
		cv21.put(SCOPEX, "120");
		cv21.put(PERIX, "D1");
		cv21.put(DRUHX, "0");
		db21.insert("historyevents", NAMEX, cv21);
		
		cv21.put(NAMEX, "Today 05.01.2016 M5");
		cv21.put(PAIRX, "EURUSD");
		cv21.put(DATEX, "05-01-2016");
		cv21.put(TIMEX, "19:00");
		cv21.put(SCOPEX, "120");
		cv21.put(PERIX, "M5");
		cv21.put(DRUHX, "0");
		db21.insert("historyevents", NAMEX, cv21);
		

		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
		android.util.Log.w("historyevents", "Upgrading database, which will destroy all old data");
		db2.execSQL("DROP TABLE IF EXISTS historyevents");
		onCreate(db2);
	}
}