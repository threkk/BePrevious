package com.hva.boxlabapp;



import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentExercises extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
//		Rajawali code
//		Exercise3DFragment fragment = new Exercise3DFragment();
//		
//		getFragmentManager()
//		.beginTransaction()
//		.add(R.id.fragment_exercise, fragment)
//		.commit();
		
		View view = inflater.inflate(R.layout.fragment_exercises, container, false);
		Button launch = (Button)view.findViewById(R.id.launch);
		launch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), Exercise3DActivity.class);
				startActivity(intent);
			}
		});
		
		return view;
	}
}
