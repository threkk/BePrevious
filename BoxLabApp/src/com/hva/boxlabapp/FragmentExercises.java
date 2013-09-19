package com.hva.boxlabapp;

import com.hva.boxlabapp.exercises.Exercise3DFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentExercises extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Exercise3DFragment fragment = new Exercise3DFragment();
		
		getFragmentManager()
		.beginTransaction()
		.add(R.id.fragment_exercise, fragment)
		.commit();
		
		return inflater.inflate(R.layout.fragment_exercises, container, false);
	}
}
