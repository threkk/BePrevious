package com.hva.boxlabapp.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hva.boxlabapp.database.entities.Schedule;

public class ScheduleActivitiesAdapter extends BaseExpandableListAdapter {

	private List<Schedule> items;
	private List<String> exercises;
	private Context context;
	
	public ScheduleActivitiesAdapter(List<Schedule> items, List<String> exercises, Context context){
		this.items = new ArrayList<Schedule>();
		this.items.addAll(items);
		this.exercises = new ArrayList<String>();
		this.exercises.addAll(exercises);
		this.context = context;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return items.get(arg0);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		TextView view = new TextView(context);
		view.setText("Data about the exercise.");
		return view;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return 1;
	}

	@Override
	public Object getGroup(int arg0) {
		return items.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		TextView view = new TextView(context);
		view.setText(exercises.get(arg0));
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}
	
}
