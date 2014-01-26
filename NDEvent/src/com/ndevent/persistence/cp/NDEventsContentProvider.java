package com.ndevent.persistence.cp;

import java.util.Arrays;
import java.util.HashSet;

import com.ndevent.persistence.db.NDDatabaseHelper;
import com.ndevent.persistence.db.NDEventsTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * 
 * @author raj
 * Content provider for events from ND, maybe other apps someday would like to read/write/modify from ND's events
 * mostly boiler plate code from @see http://www.vogella.com/tutorials/AndroidSQLite/article.html 
 */
public class NDEventsContentProvider extends ContentProvider {

	private NDDatabaseHelper database;

	// used for the UriMacher
	private static final int EVENTS = 10;
	private static final int EVENT_ID = 20;

	private static final String AUTHORITY = "com.ndevent.events.contentprovider";

	private static final String BASE_PATH = "events";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/events";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/event";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, EVENTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", EVENT_ID);
	}

	@Override
	public boolean onCreate() {
		database = new NDDatabaseHelper(getContext());
	    return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		checkColumns(projection);
		
		queryBuilder.setTables(NDEventsTable.TABLE_EVENTS);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
	    case EVENTS:
	      break;
	    case EVENT_ID :
	      // adding the ID to the original query
	      queryBuilder.appendWhere(NDEventsTable.COLUMN_ID + "=" + uri.getLastPathSegment());
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
		
		SQLiteDatabase db = database.getWritableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	    // making sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = {NDEventsTable.COLUMN_ID, NDEventsTable.COLUMN_HOST, NDEventsTable.COLUMN_TITLE, NDEventsTable.COLUMN_DESC,
				NDEventsTable.COLUMN_LOCATION, NDEventsTable.COLUMN_STARTDATE, NDEventsTable.COLUMN_ENDDATE };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case EVENTS:
	      id = sqlDB.insert(NDEventsTable.TABLE_EVENTS, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
