package nl.hva.boxlabapp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nl.hva.boxlabapp.database.LibraryDatasource;
import nl.hva.boxlabapp.database.ScheduleDatasource;
import nl.hva.boxlabapp.entities.ExerciseEntryItem;
import nl.hva.boxlabapp.exercises.Exercise3DActivity;
import nl.hva.boxlabapp.utils.ScheduleActivitiesAdapter;

import nl.hva.boxlabapp.R;
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

/**
 * Schedule fragment. Needs the TimesSquare library which is in a side project.
 * When a day is clicked, it makes a query to the database to get the schedule
 * for that day. After,the right part of the screen shows the content for that
 * day. The user can press on each exercise in order to open the 3D activity.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 * @see Exercise3DActivity
 * @see MainActivity
 */
public class FragmentSchedule extends Fragment {

	public final static String TAG = "Fragment Schedule";
	/**
	 * Intent id.
	 */
	public final static String EXERCISE = "EXERCISE";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_schedule, container,
				false);

		// Calendar
		Calendar next2Month = Calendar.getInstance();
		next2Month.add(Calendar.MONTH, 2);

		CalendarPickerView calendar = (CalendarPickerView) view
				.findViewById(R.id.calendar_view);

		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			/**
			 * Based on the selected date, makes a query to all the entries which match with that date. Also queries the exercises to get the information needed for the entries.
			 * 
			 * @param date The date which has been chosen. All the queries will be related to this date.
			 * @see com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener#onDateSelected(java.util.Date)
			 */
			public void onDateSelected(Date date) {
				ScheduleDatasource dbSchedule = new ScheduleDatasource(
						getActivity());
				LibraryDatasource dbLibrary = new LibraryDatasource(
						getActivity());

				List<ExerciseEntryItem> entries = dbSchedule
						.getExercisesByDate(date);
				List<String> exercises = dbLibrary.getNames();

				// If true, hides the adapter, show the empty message.
				if (entries.isEmpty()) {
					getView().findViewById(R.id.schedule_activities)
							.setVisibility(View.GONE);
					getView().findViewById(R.id.warning).setVisibility(
							View.VISIBLE);
				} else {
					getView().findViewById(R.id.warning)
							.findViewById(R.id.warning)
							.setVisibility(View.GONE);
					getView().findViewById(R.id.schedule_activities)
							.setVisibility(View.VISIBLE);

					// Activities
					ExpandableListView list = (ExpandableListView) getView()
							.findViewById(R.id.schedule_activities);
					final ExpandableListAdapter adapter = new ScheduleActivitiesAdapter(
							entries, exercises, getActivity());
					list.setAdapter(adapter);

					list.setOnChildClickListener(new OnChildClickListener() {

						/**
						 * Loads the 3D activty for the exercise and with the
						 * data for that entry.
						 * 
						 * @see android.widget.ExpandableListView.OnChildClickListener#onChildClick(android.widget.ExpandableListView,
						 *      android.view.View, int, int, long)
						 */
						public boolean onChildClick(ExpandableListView parent,
								View v, int groupPosition, int childPosition,
								long id) {
							ExerciseEntryItem exercise = (ExerciseEntryItem) adapter
									.getChild(groupPosition, childPosition);
							Log.i(TAG, exercise.toString());
							Intent intent = new Intent(getActivity(),
									Exercise3DActivity.class);
							intent.putExtra(EXERCISE, exercise);
							startActivity(intent);
							return true;
						}
					});
				}
			}
		});

		// Set the default day to today.
		Date today = new Date();
		calendar.init(today, next2Month.getTime());

		return view;
	}

}
