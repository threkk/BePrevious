package com.hva.boxlabapp;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hva.boxlabapp.gdx.Exercise3DObject;

public class Exercise3DActivity extends AndroidApplication {
    @Override public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        View gameView = initializeForView((ApplicationListener) new Exercise3DObject(), false);
        layout.addView(gameView);
        
        // You can add more things to the layout
        
        setContentView(layout);
    }
}