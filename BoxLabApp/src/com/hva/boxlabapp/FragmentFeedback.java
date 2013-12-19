package com.hva.boxlabapp;

import java.util.ArrayList;
import java.util.List;

import com.hva.boxlabapp.database.FeedbackDatasource;
import com.hva.boxlabapp.entities.MessageItem;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class FragmentFeedback extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View view = inflater.inflate(R.layout.fragment_feedback, container, false);
		ListView messages = (ListView) view.findViewById(R.id.feedback_list);
		List<String> strings = new ArrayList<String>();
		
		for(int i = 0; i < 50; i++) {
			strings.add("Hoi! " + i);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_feedback, strings);
		messages.setAdapter(adapter);
		return view;
	}
	
	private class MessageAdapter extends BaseAdapter {

		private Context context;
		private List<MessageItem> messages;
		
		public MessageAdapter(Context context, List<MessageItem> messages) {
			this.context = context;
			this.messages = new ArrayList<MessageItem>();
			
			FeedbackDatasource db = new FeedbackDatasource(context);
			List<MessageItem> items = db.getMessages();
			messages.addAll(items);
		}
		
		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.item_feedback, parent, false);

			return null;
		}
		
	}
}
