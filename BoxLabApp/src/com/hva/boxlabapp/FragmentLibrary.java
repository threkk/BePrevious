package com.hva.boxlabapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

public class FragmentLibrary extends ListFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_library, container, false);
		
		String[] values = new String[]{
				"Exercise 01",
				"Exercise 02",
				"Exercise 03"
		};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.library_item, values);
		this.setListAdapter(adapter);
		
		WebView webview = (WebView) view.findViewById(R.id.library_webview);
		webview.loadUrl("file:///android_asset/library/exercise01.html");
		
		return view;
	}
}
