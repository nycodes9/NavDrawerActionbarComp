package com.ndevent;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author raj
 * Base activity class to hold the drawer ui as well as the navigation fragments, 
 * only events fragment is implemented others are shown as a default text view inside the same fragments  
 */
public class BaseActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// navigation categories like Events, Classifieds, Documents etc. 
	private String[] mCategories;
	
	// The position of the events fragments  
	private static final int ND_EVENTS_POS = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_base);
		
		mCategories = getResources().getStringArray(R.array.categories);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mCategories));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, 
				mDrawerLayout, 
				R.drawable.ic_navigation_drawer, 
				R.string.drawer_open, 
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
				// TODO when nav drawer is closed but fragment is not replaced the title is not updated  
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
				supportInvalidateOptionsMenu(); 
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		// TODO app locked in portrait mode, savedInstanceState not handled 
		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	// The click listner for ListView in the navigation drawer
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
	
	/**
	 * when a category is chosen from navigation drawer fragments are replaced depending on position of the category 
	 * @param position
	 */
	private void selectItem(int position) {
		
		Fragment fragment;
		Bundle args;
		FragmentManager fragmentManager;
		
		switch (position) {
		case ND_EVENTS_POS:
			// update the main content by replacing fragments
			fragment = new NDEventsListFragment();
			args = new Bundle();
			fragment.setArguments(args);

			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			getSupportActionBar().setTitle(mCategories[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			
			break;

		default:
			
			fragment = new NDFragments();
			args = new Bundle();
			args.putString(NDFragments.ARG_FRAG_TYPE, mCategories[position]);
			fragment.setArguments(args);
			
			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			
			mDrawerList.setItemChecked(position, true);
			getSupportActionBar().setTitle(mCategories[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			
			break;
		}
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		// selecting a menu item closes the drawer
		mDrawerLayout.closeDrawer(mDrawerList);
		
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
