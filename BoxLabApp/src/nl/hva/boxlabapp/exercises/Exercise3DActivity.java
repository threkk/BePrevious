package nl.hva.boxlabapp.exercises;

import java.util.List;

import nl.hva.boxlabapp.FragmentLibrary;
import nl.hva.boxlabapp.FragmentSchedule;
import nl.hva.boxlabapp.MainActivity;
import nl.hva.boxlabapp.database.DevicesDatasource;
import nl.hva.boxlabapp.database.LibraryDatasource;
import nl.hva.boxlabapp.devices.Device;
import nl.hva.boxlabapp.entities.ExerciseEntryItem;
import nl.hva.boxlabapp.gdx.Exercise3DHandler;
import nl.hva.boxlabapp.gdx.Exercise3DObject;
import nl.hva.boxlabapp.shimmer.driver.ShimmerHandler;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import nl.hva.boxlabapp.R;

public class Exercise3DActivity extends AndroidApplication implements
		Exercise3DHandler {
	private static final String TAG = Exercise3DActivity.class.getName();
	
	private ShimmerHandler chest, thigh, shin;
	private Device mChest, mThigh, mShin = null;
	private Exercise3DObject ex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Loading sensors
		DevicesDatasource dbDevices = new DevicesDatasource(this);
		List<Device> devices = dbDevices.getDevices();
		
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
		Log.d(TAG, "mThigh: " + mThigh.getMac());
		Log.d(TAG, "mShin: " + mShin.getMac());
		
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
		ExerciseEntryItem exercise = (ExerciseEntryItem) intent.getSerializableExtra(FragmentSchedule.EXERCISE);

		if(exercise != null) {
			final ExerciseEntryItem export = exercise;
			LibraryDatasource db = new LibraryDatasource(this);
			TextView exerciseName = (TextView) contentView.findViewById(R.id.exercise_3d_title);
			String name = "";
			name = db.getName(exercise.getExerciseId());
			exerciseName.setText(name);
			
			TextView exerciseNotes = (TextView) contentView.findViewById(R.id.exercise_3d_description);
			exerciseNotes.setText(exercise.getNote());
			
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
					intent.putExtra(FragmentLibrary.URI, export.getExerciseId());
					intent.putExtra(FragmentSchedule.EXERCISE, export);
					startActivity(intent);
				}
				
			});
			
		}
		layout.addView(contentView, width, height);

		// 3D
		ex = new Exercise3DObject(this);
		View gameView = initializeForView(ex, false);
		layout.addView(gameView, width, height);

		setContentView(layout);

	}
	
	public void onBackPressed() {
		super.finish();
	}

	@Override
	public void onDestroy(){
		//chest.disconnect();
		super.onDestroy();
		thigh.disconnect();
		shin.disconnect();
		thigh = null;
		shin = null;
		ex.dispose();
		ex = null;
	}
	
	@Override
	public Quaternion[] getRotation() {
		Quaternion[] data = new Quaternion[3];
//		// data[0] = chest.readSensors();
		data[1] = thigh.readMagnetometer();
		data[2] = shin.readMagnetometer();
		return data;
	}

	@Override
	public Vector3[] getTranslation() {
		Vector3[] data = new Vector3[3];
		//data[0] = chest.readAccelerometer();
		data[1] = thigh.readAccelerometer();
		data[2] = shin.readAccelerometer();
		return data;
	}

	@Override
	public boolean initSensors() {
		// chest.init(mChest, this);
		// trivial change
		thigh.init(mThigh, this);
		while(!thigh.isConnected());
		shin.init(mShin, this);
		while(!shin.isConnected());
		return thigh.isConnected() && shin.isConnected();
	}

	@Override
	public boolean isConnected() {
		Log.i(TAG, "Thigh: " + Boolean.valueOf(thigh.isConnected()));
		Log.i(TAG, "Shin: " + Boolean.valueOf(shin.isConnected()));
		return thigh.isConnected() && shin.isConnected();

	}

	@Override
	public void disconnect() {
		thigh.disconnect();
//		chest.disconnect();
		shin.disconnect();
	}
}