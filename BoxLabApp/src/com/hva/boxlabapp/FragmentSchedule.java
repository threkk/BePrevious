package com.hva.boxlabapp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hva.boxlabapp.database.LibraryDatasource;
import com.hva.boxlabapp.database.ScheduleDatasource;
import com.hva.boxlabapp.entities.Schedule;
import com.hva.boxlabapp.exercises.Exercise3DActivity;
import com.hva.boxlabapp.utils.ScheduleActivitiesAdapter;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class FragmentSchedule extends Fragment {

	public final static String TAG = "Fragment Schedule";
	public final static String EXERCISE = "EXERCISE";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_schedule, container,
				false);
		
		// Calendar
		Calendar next3Month = Calendar.getInstance();
		next3Month.add(Calendar.MONTH, 2);

		CalendarPickerView calendar = (CalendarPickerView) view
				.findViewById(R.id.calendar_view);

		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateSelected(Date date) {
				ScheduleDatasource db1 = new ScheduleDatasource(getActivity());
				LibraryDatasource db2 = new LibraryDatasource(getActivity());
				
				List<Schedule> calendar = db1.getExercisesByDate(date);
				List<String> exercises = db2.getNames();
				
				if(calendar.isEmpty()){
					getView().findViewById(R.id.schedule_activities).setVisibility(View.GONE);
					getView().findViewById(R.id.warning).setVisibility(View.VISIBLE);
				} else {
					getView().findViewById(R.id.warning).findViewById(R.id.warning).setVisibility(View.GONE);
					getView().findViewById(R.id.schedule_activities).setVisibility(View.VISIBLE);
					
					// Activities
					ExpandableListView list = (ExpandableListView) getView().findViewById(R.id.schedule_activities);				
					final ExpandableListAdapter adapter = new ScheduleActivitiesAdapter(calendar, exercises, getActivity());
					list.setAdapter(adapter);
					
					list.setOnChildClickListener(new OnChildClickListener() {
						
						public boolean onChildClick(ExpandableListView parent, View v,
								int groupPosition, int childPosition, long id) {
							Schedule exercise = (Schedule) adapter.getChild(groupPosition, childPosition);
							Log.e(TAG, exercise.toString());
							Intent intent = new Intent(getActivity(), Exercise3DActivity.class);
							intent.putExtra(EXERCISE, exercise);
							startActivity(intent);
							return true;
						}
					});
				}
			}
		});
		
		Date today = new Date();
		calendar.init(today, next3Month.getTime());

		return view;
	}

}
