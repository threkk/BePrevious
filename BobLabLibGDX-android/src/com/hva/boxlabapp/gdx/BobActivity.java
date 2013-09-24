package com.hva.boxlabapp.gdx;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;

public class BobActivity extends AndroidApplication {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		config.useGL20 = true;
		initialize(new Exercise3DObject(), config);
	}

}
