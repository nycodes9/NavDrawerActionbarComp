package com.ndevent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.ndevent.persistence.cp.NDEventsContentProvider;
import com.ndevent.persistence.db.NDEventsTable;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author raj
 * Fragment for creating new events, used fancy date and time picker for trying 
 * @see https://github.com/derekbrameyer/android-betterpickers for samples, some issues with typeface 
 */
public class NDNewEventFragment extends Fragment implements CalendarDatePickerDialog.OnDateSetListener,
																RadialTimePickerDialog.OnTimeSetListener{

	private static final String TAG = NDNewEventFragment.class.getSimpleName();
	private EditText mTitleET;
	private Button mDateChooserBtn;
	private Button mTimeChooserBtn;
	private EditText mDescET;
	private Button mCancelBtn;
	// One calendar state is used for both month date and time
	private Calendar mEventStartCalendar;
	SimpleDateFormat dayFormat = new SimpleDateFormat("EEE MMM d yy");
	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == mEventStartCalendar) {
			mEventStartCalendar = Calendar.getInstance(); 
		}
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.layout_event_create, container, false);
		
		mTitleET = (EditText) rootView.findViewById(R.id.event_post_titleET);
		mDateChooserBtn = (Button) rootView.findViewById(R.id.event_post_calendardayBtn);
		mTimeChooserBtn = (Button) rootView.findViewById(R.id.event_post_timeBtn);
		mDescET = (EditText) rootView.findViewById(R.id.event_post_descET);
		mCancelBtn = (Button) rootView.findViewById(R.id.event_post_cancelBtn);
		
		mDateChooserBtn.setText(dayFormat.format(new Date(mEventStartCalendar.getTimeInMillis())));
		mDateChooserBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(NDNewEventFragment.this, 
						mEventStartCalendar.get(Calendar.YEAR), 
						mEventStartCalendar.get(Calendar.MONTH), 
						mEventStartCalendar.get(Calendar.DAY_OF_MONTH));
				calendarDatePickerDialog.show(getFragmentManager(), "Calendar_TAG");
			}
		});
		
		mTimeChooserBtn.setText(timeFormat.format(new Date(mEventStartCalendar.getTimeInMillis())));
		mTimeChooserBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO default time is current time, should be 12:00 am/pm ?  
				RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(NDNewEventFragment.this, 
                        		mEventStartCalendar.get(Calendar.HOUR_OF_DAY), 
                        		mEventStartCalendar.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(getActivity()));
                timePickerDialog.show(getFragmentManager(), "Time_TAG");
			}
		});
		
		mCancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// cancel has the same effect as "Back", using the navigation utils
				NavUtils.navigateUpFromSameTask(getActivity());
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Current fragment supplies the actionbar title
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.event_post_new));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		// TODO create menu from xml 
		MenuItem createMenuItem = menu.add("Create New Event");
		createMenuItem.setIcon(R.drawable.ic_action_post_new_event);
		
		MenuItemCompat.setShowAsAction(createMenuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		createMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				/**
				 * Right now only title is necessary to create a new event, refer specification doc 
				 */
				if (TextUtils.isEmpty(mTitleET.getText())) {
					Toast.makeText(getActivity(), "Cannot create and event with empty Title", Toast.LENGTH_SHORT).show();
					return true;
				}
				
				/**
				 * TODO Host and location is defaulted 
				 */
				ContentValues values = new ContentValues();
				values.put(NDEventsTable.COLUMN_TITLE, mTitleET.getText().toString());
				values.put(NDEventsTable.COLUMN_STARTDATE, mEventStartCalendar.getTimeInMillis());
				values.put(NDEventsTable.COLUMN_HOST, "Lindsey Booch");
				values.put(NDEventsTable.COLUMN_LOCATION, "Nextdoor HQ");
				values.put(NDEventsTable.COLUMN_DESC, mDescET.getText().toString());
				
				getActivity().getContentResolver().insert(NDEventsContentProvider.CONTENT_URI, values);
				
				// up is handled automatically
				NavUtils.navigateUpFromSameTask(getActivity());
				return true;
			}
		});
	}

	@Override
	public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
		mEventStartCalendar.set(year, monthOfYear, dayOfMonth);
		mDateChooserBtn.setText(dayFormat.format(new Date(mEventStartCalendar.getTimeInMillis())));
	}


	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		mEventStartCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mEventStartCalendar.set(Calendar.MINUTE, minute);
		mTimeChooserBtn.setText(timeFormat.format(new Date(mEventStartCalendar.getTimeInMillis())));
	}
}
