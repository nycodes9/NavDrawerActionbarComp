package com.ndevent.persistence.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author raj
 * Local DB helper
 */
public class NDDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "NDEvents.db";
	private static final int DATABASE_VERSION = 1;

	public NDDatabaseHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		NDEventsTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		NDEventsTable.onUpgrade(db, oldVersion, newVersion);
	}

}
