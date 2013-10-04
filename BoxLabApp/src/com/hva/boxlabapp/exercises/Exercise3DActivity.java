package com.hva.boxlabapp.exercises;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hva.boxlabapp.gdx.Exercise3DObject;
import com.hva.boxlabapp.gdx.Exercise3DHandler;

public class Exercise3DActivity extends AndroidApplication implements Exercise3DHandler {
	
    @Override 
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        final Exercise3DObject ex = new Exercise3DObject(this);
        
        View gameView = initializeForView(ex, false);
        layout.addView(gameView);
        
        // You can add more things to the layout
        
        setContentView(layout);
        
    }

	@Override
	public int[] getPosition(boolean way) {
		int[] iT = {1,2,3};
		int[] iF = {3,2,1};
 		return way ? iT : iF;
	}
}