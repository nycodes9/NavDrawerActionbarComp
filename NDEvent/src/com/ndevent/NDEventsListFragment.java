package com.ndevent;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ndevent.persistence.cp.NDEventsContentProvider;
import com.ndevent.persistence.db.NDEventsTable;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author raj
 * Fragment that shows the list of upcoming events
 * TODO events are not sorted  
 */
public class NDEventsListFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	
	public static final String TAG = NDEventsListFragment.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// create new options is supplied from fragment
		setHasOptionsMenu(true);
		fillData();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEmptyText(getResources().getString(R.string.event_empty));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (BuildConfig.DEBUG) Log.d(TAG, "Item clicked : " + id);
		
		/**
		 * Event ID sent with intent to open {@link com.ndevent.NDEventDetailsFragment} 
		 */
		Intent i = new Intent(getActivity(), NDEventsActivity.class);
		i.putExtra(NDEventsActivity.KEY_FRAGMENT_TYPE, NDEventsActivity.FRAGMENT_TYPE_EVENT_DETAILS);
		i.putExtra(NDEventDetailsFragment.KEY_EVENT_ID_LONG, id);
		startActivity(i);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		MenuItem createNewMenuItem = menu.add("Create New");
		createNewMenuItem.setIcon(R.drawable.ic_action_newevent);

		MenuItemCompat.setShowAsAction(createNewMenuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

		/**
		 * TODO handle menu inflation from xml
		 */
		createNewMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				/**
				 * intent to open {@link com.ndevent.NDNewEventFragment} 
				 */
				Intent i = new Intent(getActivity(), NDEventsActivity.class);
				i.putExtra(NDEventsActivity.KEY_FRAGMENT_TYPE, NDEventsActivity.FRAGMENT_TYPE_NEW_EVENT);
				startActivity(i);
				return true;
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	// private Cursor cursor;
	private SimpleCursorAdapter adapter;

	private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { NDEventsTable.COLUMN_STARTDATE, NDEventsTable.COLUMN_STARTDATE,
				NDEventsTable.COLUMN_TITLE, NDEventsTable.COLUMN_LOCATION, NDEventsTable.COLUMN_STARTDATE };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.eventMonthTV, R.id.eventDayTV, R.id.eventTitleTV, R.id.eventLocationTV,
				R.id.eventTimeDateTV };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.list_item_event, null, from, to, 0);
		/**
		 * changing to custom view binding since one data {@link com.ndevent.persistence.db.NDEventsTable#COLUMN_STARTDATE}
		 * is used to populate more than one UI 
		 */
		adapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				
				if (columnIndex == cursor.getColumnIndex(NDEventsTable.COLUMN_STARTDATE)) {
					
					long longAsDateTime = cursor.getLong(columnIndex);
					Date asDate = new Date(longAsDateTime);
					
					if (null != view.findViewById(R.id.eventMonthTV)) {
						((TextView)view.findViewById(R.id.eventMonthTV)).setText(sdfMonth.format(asDate));
					}
					
					if (null != view.findViewById(R.id.eventDayTV)) {
						((TextView)view.findViewById(R.id.eventDayTV)).setText(sdfDay.format(asDate));
					}
					
					if (null != view.findViewById(R.id.eventTimeDateTV)) {
						((TextView)view.findViewById(R.id.eventTimeDateTV)).setText(sdfVerbose.format(asDate));
					}
					return true;
				} 
				
				// Handle rest as default
				return false;
			}
		});

		setListAdapter(adapter);
	}
	
	/**
	 * 
	 */
	SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM");
	SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
	SimpleDateFormat sdfVerbose = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm aaa");

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { NDEventsTable.COLUMN_ID, NDEventsTable.COLUMN_HOST, NDEventsTable.COLUMN_TITLE, NDEventsTable.COLUMN_DESC,
				NDEventsTable.COLUMN_LOCATION, NDEventsTable.COLUMN_STARTDATE, NDEventsTable.COLUMN_ENDDATE };
		CursorLoader cursorLoader = new CursorLoader(this.getActivity(), NDEventsContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}
}
