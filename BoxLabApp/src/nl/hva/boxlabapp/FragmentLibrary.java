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

/**
 * Fragment with the library of exercises. The fragment loads a list view with
 * all the exercises which are stored in a static non-writable database. With
 * the information of the exercises, the HTML files are loaded. In order to add
 * more exercises, first they need to be written in HTML and after added to the
 * database. The files follow naming scheme "exercise00" changing the values of
 * the 00 for the id + 1 in the database.
 * 
 * It was supposed to implement also a read version of the files, but the health
 * students didn't provided us the files.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 * @see MainActivity
 */
public class FragmentLibrary extends ListFragment {

	/**
	 * Intent tag.
	 */
	public static final String URI = "URI";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Loads the layout and the database.
		View view = inflater.inflate(R.layout.fragment_library, container,
				false);
		LibraryDatasource db = new LibraryDatasource(getActivity());

		// Get the values and put them in the adapter.
		List<String> values = db.getNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.item_library, values);
		this.setListAdapter(adapter);

		// Base url for the webview.
		String url = "file:///android_asset/library/exercise";
		Intent intent = getActivity().getIntent();
		int index;
		// In case the intent is empty.
		try {
			index = intent.getExtras().getInt(URI, 1);
		} catch (NullPointerException e) {
			index = 1;
		}

		if (index < 10) {
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
		// Important: the name of the file is the position and id in the
		// database plus 1.
		String uri = db.getURIById(position + 1);

		WebView webview = (WebView) getView()
				.findViewById(R.id.library_webview);
		webview.loadUrl("file:///android_asset/library/" + uri);
	}
}
