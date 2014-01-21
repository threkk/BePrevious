package nl.hva.boxlabapp.exercises;

import java.util.List;

import nl.hva.boxlabapp.FragmentLibrary;
import nl.hva.boxlabapp.FragmentSchedule;
import nl.hva.boxlabapp.MainActivity;
import nl.hva.boxlabapp.database.DevicesDatasource;
import nl.hva.boxlabapp.database.LibraryDatasource;
import nl.hva.boxlabapp.devices.Device;
import nl.hva.boxlabapp.entities.ExerciseEntryItem;
import nl.hva.boxlabapp.gdx.Exercise3DObject;
import nl.hva.boxlabapp.shimmer.driver.ShimmerHandler;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import nl.hva.boxlabapp.R;

public class Exercise3DActivity extends AndroidApplication implements
		Exercise3DObject.SensorsHandler {
	
	private ShimmerHandler chest, thigh, shin = null;
	private Device mChest, mThigh, mShin = null;
	private Exercise3DObject ex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Loading sensors data
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
		chest = new ShimmerHandler(mChest);
		thigh = new ShimmerHandler(mThigh);
		shin = new ShimmerHandler(mShin);
		
		//chest.init(this);
		thigh.init(this);
		shin.init(this);
		
		// UI
		LinearLayout layout = new LinearLayout(this);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x / 2;
		int height = size.y;
		
		// 3D
		ex = new Exercise3DObject(this);
		View gameView = initializeForView(ex, false);
		
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
		layout.addView(gameView, width, height);
		setContentView(layout);
	}
	
	// Back button behaviour
	@Override
	public void onBackPressed() {
		super.finish();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.activate_chest :
			try {
				if(chest.isStreaming()) {
					chest.disconnect();
					item.setIcon(R.drawable.ic_play);
					Toast.makeText(this, "Chest sensor disconnected.", Toast.LENGTH_SHORT).show();
				} else {
					chest.connect();
					item.setIcon(R.drawable.ic_pause);
					Toast.makeText(this, "Chest sensor connected.", Toast.LENGTH_LONG).show();
				}
			} catch (NullPointerException oops) {
				Toast.makeText(this, "Chest sensor not found.", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.activate_thigh :
			try {
				if(thigh.isStreaming()) {
					thigh.disconnect();
					item.setIcon(R.drawable.ic_play);
					Toast.makeText(this, "Thigh sensor disconnected.", Toast.LENGTH_SHORT).show();
				} else {
					thigh.connect();
					item.setIcon(R.drawable.ic_pause);
					Toast.makeText(this, "Thigh sensor connected.", Toast.LENGTH_LONG).show();
				}
			} catch (NullPointerException oops) {
				Toast.makeText(this, "Thigh sensor not found.", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.activate_shin :
			try {
				if(shin.isStreaming()) {
					shin.disconnect();
					item.setIcon(R.drawable.ic_play);
					Toast.makeText(this, "Shin sensor disconnected.", Toast.LENGTH_SHORT).show();
				} else {
					shin.connect();
					item.setIcon(R.drawable.ic_pause);
					Toast.makeText(this, "Shin sensor connected.", Toast.LENGTH_LONG).show();
				}
			} catch (NullPointerException oops) {
				Toast.makeText(this, "Shin sensor not found.", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// Interface
	@Override
	public Quaternion[] getRotation() {
		Quaternion[] data = new Quaternion[3];
//		data[0] = chest.readSensors();
		data[1] = thigh.readMagnetometer();
		data[2] = shin.readMagnetometer();
		return data;
	}

	@Override
	public Vector3[] getTranslation() {
		Vector3[] data = new Vector3[3];
//      data[0] = chest.readAccelerometer();
		data[1] = thigh.readAccelerometer();
		data[2] = shin.readAccelerometer();
		return data;
	}

}