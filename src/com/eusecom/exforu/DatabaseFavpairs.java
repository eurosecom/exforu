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


public class DatabaseFavpairs extends SQLiteOpenHelper {
	private static final String DATABASE_NAME2="db2";
	public static final String PAIR2="pair2";
	public static final String NICK2="nick2";
	public static final String MAIL2="mail2";
	public static final String UZID2="uzid2";
	public static final String NAME2="name2";
	public static final String PSWD2="pswd2";
	public static final String DRUH2="druh2";
	public static final String DATM2="datm2";
	public static final String DATZ2="datz2";
	
	
	public DatabaseFavpairs(Context context) {
		//ta 3ka je verzia databaze, nesmiem dat nizsiu ak zvysim vymaze tabulku a znovu ju vytvori
		super(context, DATABASE_NAME2, null, 4);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db2) {
		
		db2.execSQL("CREATE TABLE favpairs (_id INTEGER PRIMARY KEY AUTOINCREMENT, pair2 TEXT, " +
				"nick2 TEXT, mail2 TEXT, uzid2 TEXT, name2 TEXT, pswd2 TEXT,  " +
				"druh2 TEXT, datm2 TIMESTAMP(14) DEFAULT CURRENT_TIMESTAMP, datz2 TIMESTAMP(14));");
		
		ContentValues cv2=new ContentValues();
		
		cv2.put(PAIR2, "EURGBP");
		cv2.put(NICK2, "nnn1");
		cv2.put(MAIL2, "mmm1");
		cv2.put(UZID2, "uzid xxx");
		cv2.put(NAME2, "name xxx");
		cv2.put(PSWD2, "memo 1");
		cv2.put(DRUH2, "druh xxx");
		db2.insert("favpairs", PAIR2, cv2);
		
		cv2.put(PAIR2, "GBPUSD");
		cv2.put(NICK2, "nnn4");
		cv2.put(MAIL2, "mmm4");
		cv2.put(UZID2, "uzid xxx");
		cv2.put(NAME2, "name xxx");
		cv2.put(PSWD2, "memo 4");
		cv2.put(DRUH2, "druh xxx");
		db2.insert("favpairs", PAIR2, cv2);
		
		cv2.put(PAIR2, "USDJPY");
		cv2.put(NICK2, "nnn2");
		cv2.put(MAIL2, "mmm2");
		cv2.put(UZID2, "uzid xxx2");
		cv2.put(NAME2, "name xxx2");
		cv2.put(PSWD2, "memo 2");
		cv2.put(DRUH2, "druh xxx2");
		db2.insert("favpairs", PAIR2, cv2);
		
		cv2.put(PAIR2, "EURUSD");
		cv2.put(NICK2, "nnn3");
		cv2.put(MAIL2, "mmm3");
		cv2.put(UZID2, "uzid xxx3");
		cv2.put(NAME2, "name xxx3");
		cv2.put(PSWD2, "memo 3");
		cv2.put(DRUH2, "druh xxx3");
		db2.insert("favpairs", PAIR2, cv2);
		

		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
		android.util.Log.w("favpairs", "Upgrading database, which will destroy all old data");
		db2.execSQL("DROP TABLE IF EXISTS favpairs");
		onCreate(db2);
	}
}