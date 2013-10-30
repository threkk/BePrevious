package com.hva.boxlabapp;

import java.util.List;

import com.hva.boxlabapp.database.ExercisesDatasource;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentLibrary extends ListFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_library, container, false);
		ExercisesDatasource db = new ExercisesDatasource(getActivity());
		
		List<String> values = db.getNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.library_item, values);
		this.setListAdapter(adapter);
		
		WebView webview = (WebView) view.findViewById(R.id.library_webview);
		// Change this.
		webview.loadUrl("file:///android_asset/library/exercise01.html");
		
		return view;
	}

	 @Override
	  public void onListItemClick(ListView l, View v, int position, long id) {
		 	ExercisesDatasource db = new ExercisesDatasource(getActivity());
			String uri = db.getURIById(position+1);
			
			WebView webview = (WebView) getView().findViewById(R.id.library_webview);
			webview.loadUrl("file:///android_asset/library/"+uri);
	  }
}
