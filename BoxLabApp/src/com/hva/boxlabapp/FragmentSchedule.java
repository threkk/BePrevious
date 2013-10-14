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

		Calendar next3Month = Calendar.getInstance();
		next3Month.add(Calendar.MONTH, 2);

		CalendarPickerView calendar = (CalendarPickerView)view.findViewById(R.id.calendar_view);
		
		Date today = new Date();
		calendar.init(today, next3Month.getTime())
		    .withSelectedDate(today);
		
		return view;
	}
}
