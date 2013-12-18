package com.hva.boxlabapp.exercises;

import java.util.List;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hva.boxlabapp.FragmentLibrary;
import com.hva.boxlabapp.FragmentSchedule;
import com.hva.boxlabapp.MainActivity;
import com.hva.boxlabapp.R;
import com.hva.boxlabapp.database.DevicesDatasource;
import com.hva.boxlabapp.database.LibraryDatasource;
import com.hva.boxlabapp.devices.Device;
import com.hva.boxlabapp.entities.Schedule;
import com.hva.boxlabapp.gdx.Exercise3DObject;
import com.hva.boxlabapp.gdx.Exercise3DHandler;
import com.hva.boxlabapp.shimmer.driver.ShimmerHandler;

public class Exercise3DActivity extends AndroidApplication implements
		Exercise3DHandler {

	private ShimmerHandler chest;
	private ShimmerHandler thigh;
	private ShimmerHandler shin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Loading sensors
		DevicesDatasource dbDevices = new DevicesDatasource(this);
		List<Device> devices = dbDevices.getDevices();
		
		Device mChest = null;
		Device mThigh = null;
		Device mShin = null;
		
		for (Device device : devices) {
			switch(device.getPosition()) {
			case CHEST : mChest = device; break;
			case SHIN : mShin = device; break;
			case THIGH : mThigh = device; break;
			case SERVER : ;
			}
		}
		
//		Uncomment after testing.
//		if(mChest == null || mThigh == null || mShin == null) {
//			Toast.makeText(this, "Sensors missing", Toast.LENGTH_SHORT).show();
//			finish();
//		}
		
		// Creating handlers
		chest = new ShimmerHandler();
		thigh = new ShimmerHandler();
		shin = new ShimmerHandler();
		
		// chest.init(mChest, this);
		// thigh.init(mThigh, this);
		// shin.init(mShin, this);
		
		// UI
		LinearLayout layout = new LinearLayout(this);

		// Dimensions
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x / 2;
		int height = size.y;
		
		// Content
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.activity_exercise, null);
		Intent intent = getIntent();
		Schedule exercise = (Schedule) intent.getSerializableExtra(FragmentSchedule.EXERCISE);

		if(exercise != null) {
			final Schedule export = exercise;
			LibraryDatasource db = new LibraryDatasource(this);
			TextView exerciseName = (TextView) contentView.findViewById(R.id.exercise_3d_title);
			String name = "";
			name = db.getName(exercise.getExercise());
			exerciseName.setText(name);
			
			TextView exerciseNotes = (TextView) contentView.findViewById(R.id.exercise_3d_description);
			exerciseNotes.setText(exercise.getNotes());
			
			TextView exerciseSets = (TextView) contentView.findViewById(R.id.exercise_3d_set_counter);
			exerciseSets.setText("1");
			
			TextView exerciseMaxSets = (TextView) contentView.findViewById(R.id.exercise_3d_set_max);
			exerciseMaxSets.setText(String.valueOf(exercise.getRepetitions().size()));
			
			TextView exerciseReps = (TextView) contentView.findViewById(R.id.exercise_3d_reps_counter);
			exerciseReps.setText("0");
			
			TextView exerciseMaxReps = (TextView) contentView.findViewById(R.id.exercise_3d_reps_max);
			exerciseMaxReps.setText(String.valueOf(exercise.getRepetitions().get(0)));
			
			TextView help = (TextView) contentView.findViewById(R.id.exercise_3d_help);
			help.setVisibility(TextView.VISIBLE);
			help.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.putExtra(MainActivity.TAB, 1);
					intent.putExtra(FragmentLibrary.URI, export.getExercise());
					intent.putExtra(FragmentSchedule.EXERCISE, export);
					startActivity(intent);
				}
				
			});
			
		}
		layout.addView(contentView, width, height);

		// 3D
		final Exercise3DObject ex = new Exercise3DObject(this);
		View gameView = initializeForView(ex, false);
		layout.addView(gameView, width, height);

		setContentView(layout);

	}

	@Override
	public double[][] getData() {
		// 3 sensors sending 6 outputs, 3 accel, 3 gyro
		double[][] data = new double[3][6];
		data[0] = chest.readSensors();
		data[1] = thigh.readSensors();
		data[2] = shin.readSensors();
		return data;
	}
	
	public void onBackPressed() {
		this.finish();
	}
}