package com.ndevent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author raj
 * Other fragments that are not Events
 */
public class NDFragments extends Fragment {

	public static final String ARG_FRAG_TYPE = "fragment_type"; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.layout_fragment_generic, container, false);
		
		((TextView)rootView.findViewById(R.id.fragment_genericTV)).setText(getArguments().getString(ARG_FRAG_TYPE));
		
		return rootView;
	}
	
	
	
}
