package com.hva.boxlabapp.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hva.boxlabapp.R;
import com.hva.boxlabapp.gdx.Exercise3DObject;
import com.hva.boxlabapp.gdx.Exercise3DHandler;

public class Exercise3DActivity extends AndroidApplication implements Exercise3DHandler {
	
    @Override 
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        final Exercise3DObject ex = new Exercise3DObject(this);
      
        View contentView = LayoutInflater.from(this).inflate(R.layout.exercise_3d_content, null);
        layout.addView(contentView);
        
        View gameView = initializeForView(ex, false);
        layout.addView(gameView, LinearLayout.LayoutParams.WRAP_CONTENT);
       
        setContentView(layout);
        
    }

	@Override
	public int[] getPosition(boolean way) {
		int[] iT = {1,2,3};
		int[] iF = {3,2,1};
 		return way ? iT : iF;
	}
}