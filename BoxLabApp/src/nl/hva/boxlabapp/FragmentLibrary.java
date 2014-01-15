package nl.hva.boxlabapp;

import java.util.List;

import nl.hva.boxlabapp.database.LibraryDatasource;

import nl.hva.boxlabapp.R;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentLibrary extends ListFragment {
	
	public static final String URI = "URI";
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_library, container, false);
		LibraryDatasource db = new LibraryDatasource(getActivity());
		
		List<String> values = db.getNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_library, values);
		this.setListAdapter(adapter);
		
		String url = "file:///android_asset/library/exercise";
		Intent intent = getActivity().getIntent();
		int index; 
		try {
			index = intent.getExtras().getInt(URI, 1);
		} catch (NullPointerException e) {
			index = 1;
		}
		
		if(index < 10) {
			url += "0" + index + ".html";
		} else {
			url += index + ".html";
		}
		
		WebView webview = (WebView) view.findViewById(R.id.library_webview);
		webview.loadUrl(url);
		
		return view;
	}

	 @Override
	  public void onListItemClick(ListView l, View v, int position, long id) {
		 	LibraryDatasource db = new LibraryDatasource(getActivity());
			String uri = db.getURIById(position+1);
			
			WebView webview = (WebView) getView().findViewById(R.id.library_webview);
			webview.loadUrl("file:///android_asset/library/"+uri);
	  }
}
