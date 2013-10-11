package com.hva.boxlabapp;

import java.util.Calendar;
import java.util.Date;

import com.squareup.timessquare.CalendarPickerView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSchedule extends Fragment{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View view = inflater.inflate(R.layout.fragment_schedule, container, false);
		
		
		// Testing the calendar model
		// Dimensions defined in the layout. They should be done in execution time 
		// in order to adapt to different screen sizes.
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		CalendarPickerView calendar = (CalendarPickerView)view.findViewById(R.id.calendar_view);
		
		Date today = new Date();
		calendar.init(today, nextYear.getTime())
		    .withSelectedDate(today);
		
		return view;
	}
}
