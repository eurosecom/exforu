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


public class DatabaseModels extends SQLiteOpenHelper {
	private static final String DATABASE_NAME61="db61";
	public static final String ITIME="itime";
	public static final String IOPEN="iopen";
	public static final String IVOLUME="ivolume";
	public static final String IORDER="iorder";
	public static final String ISYMBOL="isymbol";
	public static final String IPERIOD="iperiod";
	public static final String IDRUH="idruh";
	public static final String DATM="datm";
	public static final String DATZ="datz";
	public static final String IMEMO="imemo";
	public static final String ITP="itp";
	public static final String ISL="isl";
	
	
	public DatabaseModels(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME61, null, 5);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db61) {
		
		db61.execSQL("CREATE TABLE models (_id INTEGER PRIMARY KEY AUTOINCREMENT, itime TEXT, " +
				"iopen TEXT, ivolume TEXT, iorder TEXT, isymbol TEXT, iperiod TEXT,  " +
				"idruh TEXT, datm TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP, datz TIMESTAMP(14), "+ 
				"imemo TEXT, itp TEXT, isl TEXT ) ");
		
		ContentValues cv3=new ContentValues();
		
		cv3.put(ITIME, "0");
		cv3.put(IOPEN, "1.125");
		cv3.put(IVOLUME, "0");
		cv3.put(IORDER, "100001");
		cv3.put(ISYMBOL, "EURUSD");
		cv3.put(IPERIOD, "30");
		cv3.put(IDRUH, "1");
		cv3.put(IMEMO, "memo");
		cv3.put(ITP, "0");
		cv3.put(ISL, "0");
		db61.insert("models", ITIME, cv3);
		
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db61, int oldVersion, int newVersion) {
		android.util.Log.w("models", "Upgrading database, which will destroy all old data");
		db61.execSQL("DROP TABLE IF EXISTS models");
		onCreate(db61);
	}
}