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


public class DatabaseTemp extends SQLiteOpenHelper {
	private static final String DATABASE_NAME7="db7";
	public static final String FAVACT="favact";
	public static final String CANDL="candl";
	public static final String BUSE="buse";
	public static final String TRADE="trade";
	
	
	public DatabaseTemp(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME7, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db7) {
		
		db7.execSQL("CREATE TABLE temppar (_id INTEGER PRIMARY KEY AUTOINCREMENT, favact TEXT, " +
				"candl TEXT, buse TEXT, trade TEXT);");
		
		ContentValues cv7=new ContentValues();
		
		cv7.put(FAVACT, "0");
		cv7.put(CANDL, "0");
		cv7.put(BUSE, "0");
		cv7.put(TRADE, "0");

		db7.insert("temppar", FAVACT, cv7);
		
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db3, int oldVersion, int newVersion) {
		android.util.Log.w("temppar", "Upgrading database, which will destroy all old data");
		db3.execSQL("DROP TABLE IF EXISTS candles");
		onCreate(db3);
	}
}