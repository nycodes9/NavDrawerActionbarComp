package com.ndevent.persistence.db;

import com.ndevent.BuildConfig;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author raj
 * One class per Table 
 */
public class NDEventsTable {

	public static final String TAG = NDEventsTable.class.getSimpleName();

	public static final String TABLE_EVENTS = "NDEvents";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_HOST = "host";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_STARTDATE = "startdate";
	public static final String COLUMN_ENDDATE = "enddate";

	private static final String DATABASE_CREATE = "create table " + TABLE_EVENTS + "(" + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_HOST + " text not null, " + 
			COLUMN_TITLE + " text not null," + 
			COLUMN_LOCATION + " text not null," + 
			COLUMN_DESC + " text," + 
			COLUMN_STARTDATE + " integer not null," + 
			COLUMN_ENDDATE + " integer" + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		onCreate(database);
	}
}
