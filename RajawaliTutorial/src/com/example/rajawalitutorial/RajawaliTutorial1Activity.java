package com.example.rajawalitutorial;

import rajawali.RajawaliActivity;
import android.os.Bundle;
import android.view.Menu;

public class RajawaliTutorial1Activity extends RajawaliActivity {

	private RajawaliTutorial1Renderer mRenderer; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRenderer = new RajawaliTutorial1Renderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		super.setRenderer(mRenderer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rajawali_tutorial1, menu);
		return true;
	}

}
