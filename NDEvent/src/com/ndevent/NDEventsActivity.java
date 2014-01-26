package com.ndevent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

/**
 * 
 * @author raj
 * Activity that hosted all fragments related to events  
 */
public class NDEventsActivity extends ActionBarActivity {

	protected static final String FRAGMENT_TYPE_NEW_EVENT = NDNewEventFragment.class.getSimpleName();
	protected static final String FRAGMENT_TYPE_EVENT_DETAILS = NDEventDetailsFragment.class.getSimpleName();
	protected static final String KEY_FRAGMENT_TYPE = "fragmentType";

	private Fragment mTargetFragment;
	private String mFragmentTypeStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_events);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		if (null != getIntent()){
			mFragmentTypeStr = getIntent().getStringExtra(KEY_FRAGMENT_TYPE);
			
			if (mFragmentTypeStr.equals(FRAGMENT_TYPE_NEW_EVENT)) {
				mTargetFragment = new NDNewEventFragment();
			} else if (mFragmentTypeStr.equals(FRAGMENT_TYPE_EVENT_DETAILS)) {
				mTargetFragment = new NDEventDetailsFragment();
				mTargetFragment.setArguments(getIntent().getExtras());
			}
			
			getSupportFragmentManager().beginTransaction().replace(R.id.layout_activityFL, mTargetFragment).commit();
		}
		
	}
	
}
