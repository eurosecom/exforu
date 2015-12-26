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
	public static final String PAIR2="pair2";
	public static final String NICK2="nick2";
	public static final String MAIL2="mail2";
	public static final String UZID2="uzid2";
	public static final String NAME2="name2";
	public static final String PSWD2="pswd2";
	public static final String DRUH2="druh2";
	public static final String DATM2="datm2";
	public static final String DATZ2="datz2";
	
	
	public DatabaseHistoryEvents(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME2, null, 4);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db21) {
		
		db21.execSQL("CREATE TABLE historyevents (_id INTEGER PRIMARY KEY AUTOINCREMENT, pair2 TEXT, " +
				"nick2 TEXT, mail2 TEXT, uzid2 TEXT, name2 TEXT, pswd2 TEXT,  " +
				"druh2 TEXT, datm2 TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP, datz2 TIMESTAMP(14));");
		
		ContentValues cv21=new ContentValues();
		
		cv21.put(PAIR2, "USD Interest rate change 12.02.2006");
		cv21.put(NICK2, "EURUSD");
		cv21.put(MAIL2, "2006-02-12");
		cv21.put(UZID2, "14:30");
		cv21.put(NAME2, "120");
		cv21.put(PSWD2, "M5");
		cv21.put(DRUH2, "druh xxx");
		db21.insert("historyevents", PAIR2, cv21);
		
		cv21.put(PAIR2, "USD Interest rate change 16.12.2015");
		cv21.put(NICK2, "EURUSD");
		cv21.put(MAIL2, "2015-12-16");
		cv21.put(UZID2, "14:30");
		cv21.put(NAME2, "120");
		cv21.put(PSWD2, "M5");
		cv21.put(DRUH2, "druh xxx2");
		db21.insert("historyevents", PAIR2, cv21);
		
		cv21.put(PAIR2, "US Non Farm Payroll 21.11.2015");
		cv21.put(NICK2, "EURUSD");
		cv21.put(MAIL2, "2015-11-21");
		cv21.put(UZID2, "20:00");
		cv21.put(NAME2, "120");
		cv21.put(PSWD2, "M5");
		cv21.put(DRUH2, "druh xxx3");
		db21.insert("historyevents", PAIR2, cv21);
		

		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
		android.util.Log.w("historyevents", "Upgrading database, which will destroy all old data");
		db2.execSQL("DROP TABLE IF EXISTS historyevents");
		onCreate(db2);
	}
}