package com.hva.boxlabapp.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hva.boxlabapp.R;
import com.hva.boxlabapp.gdx.Exercise3DObject;
import com.hva.boxlabapp.gdx.Exercise3DHandler;
import com.hva.boxlabapp.shimmer.driver.ShimmerHandler;

public class Exercise3DActivity extends AndroidApplication implements Exercise3DHandler {
	
	private ShimmerHandler hip;
	private ShimmerHandler thigh;
	private ShimmerHandler shin;
	
    @Override 
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the handler. We need to do something with this when the pause and 
        // destroy events happen.
        hip = new ShimmerHandler();
        thigh = new ShimmerHandler();
        shin = new ShimmerHandler();
        
        // Also we need to do something to distinguish between them.
        
        LinearLayout layout = new LinearLayout(this);
        final Exercise3DObject ex = new Exercise3DObject(this);
      
        View contentView = LayoutInflater.from(this).inflate(R.layout.exercise_3d_content, null);
        layout.addView(contentView);
        
        View gameView = initializeForView(ex, false);
        layout.addView(gameView, LinearLayout.LayoutParams.WRAP_CONTENT);
       
        setContentView(layout);
        
    }

	@Override
	public int[][] getAccel() {
		// 3 sensors sending 3 outputs
		int[][] data = new int[3][3];
		data[0] = hip.readSensors();
		data[1] = thigh.readSensors();
		data[2] = shin.readSensors();
		return data;
	}
	
	@Override
	public float[][] getGyro(){
		float[][] gyro = {};
		return gyro;
	}
}