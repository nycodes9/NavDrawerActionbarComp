package com.ndevent;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.ndevent.persistence.cp.NDEventsContentProvider;
import com.ndevent.persistence.db.NDEventsTable;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author raj
 * The fragment that handles the events details, hosted by {@link com.ndevent.NDEventsActivity} 
 */
public class NDEventDetailsFragment extends Fragment {

	public static final String TAG = NDEventDetailsFragment.class.getSimpleName();
	public static final String KEY_EVENT_ID_LONG = "event_id_long";
	
	/** Event id that is passed from {@link #NDEventsActivty()} */ 
	private long mEventID = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEventID = getArguments().getLong(KEY_EVENT_ID_LONG);
		if (BuildConfig.DEBUG) Log.d(TAG, "Event id : " + mEventID);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.layout_event_details, container, false);
		rootView.findViewById(R.id.event_details_actionsLL).setVisibility(View.VISIBLE);
		fillData(rootView);
		return rootView;
	}

	/**
	 * Given the view from inflater show the event details 
	 * @param view
	 */
	private void fillData(View view) {
		
		Uri eventIDUri = Uri.parse(NDEventsContentProvider.CONTENT_URI + "/" + mEventID);
		String[] projection = { NDEventsTable.COLUMN_ID, NDEventsTable.COLUMN_HOST, NDEventsTable.COLUMN_TITLE, NDEventsTable.COLUMN_DESC,
				NDEventsTable.COLUMN_LOCATION, NDEventsTable.COLUMN_STARTDATE, NDEventsTable.COLUMN_ENDDATE };
	    Cursor cursor = getActivity().getContentResolver().query(eventIDUri, projection, null, null,null);
	    if (cursor != null && cursor.moveToFirst()) {
	        
	        final long longAsDateTime = cursor.getLong(cursor.getColumnIndexOrThrow(NDEventsTable.COLUMN_STARTDATE));
			Date asDate = new Date(longAsDateTime);
	        
	        ((TextView)view.findViewById(R.id.eventMonthTV)).setText(sdfMonth.format(asDate));
	        ((TextView)view.findViewById(R.id.eventDayTV)).setText(sdfDay.format(asDate));
	        
	        final String title = cursor.getString(cursor.getColumnIndexOrThrow(NDEventsTable.COLUMN_TITLE));
	        ((TextView)view.findViewById(R.id.eventTitleTV)).setText(title);
	        
	        final String location = cursor.getString(cursor.getColumnIndexOrThrow(NDEventsTable.COLUMN_LOCATION));
	        ((TextView)view.findViewById(R.id.eventLocationTV)).setText(location);
	        
	        ((TextView)view.findViewById(R.id.eventTimeDateTV)).setText(sdfVerbose.format(asDate));
	        
	        final String desc = cursor.getString(cursor.getColumnIndexOrThrow(NDEventsTable.COLUMN_DESC));
	        ((TextView)view.findViewById(R.id.event_details_descTV)).setText(desc);
	        
	        final String host = cursor.getString(cursor.getColumnIndexOrThrow(NDEventsTable.COLUMN_HOST));
	        ((TextView)view.findViewById(R.id.event_details_detailsTV)).setText(host);
	        
	        /**
	         * Handle "Add to Calendar for API 14+ 
	         * @see http://developer.android.com/guide/topics/providers/calendar-provider.html#intent-insert
	         */
	        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
	        	view.findViewById(R.id.addToCalBtn).setVisibility(View.VISIBLE);
	        	((Button)view.findViewById(R.id.addToCalBtn)).setOnClickListener(new OnClickListener() {
					
					@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					@Override
					public void onClick(View v) {
						
						Intent i = new Intent(Intent.ACTION_INSERT)
									.setData(Events.CONTENT_URI)
									.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, longAsDateTime)
									.putExtra(Events.TITLE, title)
									.putExtra(Events.DESCRIPTION, desc)
									.putExtra(Events.EVENT_LOCATION, location)
									.putExtra(Events.ORGANIZER, host);
						startActivity(i);
					}
				});
	        } else {
	        	view.findViewById(R.id.addToCalBtn).setVisibility(View.GONE);
	        }
	        
	        
	        cursor.close();
	    }
		
	}

	/**
	 * Date format used to pretty the UI 
	 * @see http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
	 */
	SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM");
	SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
	SimpleDateFormat sdfVerbose = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm aaa");
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// This fragment supplies the actionbar title
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.event_details));
	}
}
