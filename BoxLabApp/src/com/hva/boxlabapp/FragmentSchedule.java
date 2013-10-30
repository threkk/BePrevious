package com.hva.boxlabapp;

import java.util.Calendar;
import java.util.Date;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSchedule extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_schedule, container,
				false);

		Calendar next3Month = Calendar.getInstance();
		next3Month.add(Calendar.MONTH, 2);

		CalendarPickerView calendar = (CalendarPickerView) view
				.findViewById(R.id.calendar_view);

		Date today = new Date();
		calendar.init(today, next3Month.getTime()).withSelectedDate(today);

		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {
			
			// The listener is here. Now, we only have to change the content in the view.
			// Be careful with the order adding the views, because it can produce a
			// null pointer exception
			@Override
			public void onDateSelected(Date date) {
				Log.e("Cosa", "Pressed " + date);

			}
		});

		return view;
	}

}
