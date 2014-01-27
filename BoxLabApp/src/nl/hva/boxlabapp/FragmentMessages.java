package nl.hva.boxlabapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.hva.boxlabapp.database.MessagesDatasource;
import nl.hva.boxlabapp.entities.MessageItem;

import nl.hva.boxlabapp.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragment for the messages interface. Not too much to say.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 * @see MainActivity
 */
public class FragmentMessages extends Fragment {

	private static final String TAG = FragmentMessages.class.getName();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_messages, container,
				false);
		final ListView messages = (ListView) view
				.findViewById(R.id.messages_list);

		final MessagesDatasource db = new MessagesDatasource(getActivity());
		List<MessageItem> items = db.getMessages();

		final MessageAdapter adapter = new MessageAdapter(getActivity(), items);
		messages.setAdapter(adapter);

		final EditText input = (EditText) view
				.findViewById(R.id.messages_input);

		Button send = (Button) view.findViewById(R.id.messages_send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			/**
			 * As the application doesn't use internet and all the sync is done via bluetooth, all the messages for both users are stored in a database and sent during the synchronization.
			 * If the message is empty, it is not added or sent.
			 * When a message is sent, the view is refreshed.
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				MessageItem msg = new MessageItem();
				String message = input.getText().toString();
				Date date = new Date();

				if (!message.equals("")) {
					msg.setMessage(message);
					msg.setCreated(date);
					msg.setFromPatient(true);

					db.create(msg);
					Log.i(TAG, "Message added.");
					List<MessageItem> updated = db.getMessages();

					if (updated != null) {
						adapter.messages.clear();
						adapter.messages.addAll(updated);
					}

					input.setText("");
					adapter.notifyDataSetChanged();
					messages.setSelection(adapter.getCount() - 1);
				}
			}
		});

		messages.setSelection(adapter.getCount() - 1);

		return view;
	}

	/**
	 * Adapter for the messages. It needs a list of messages as parameter to
	 * fill the view. The user and the therapist have different colors in order
	 * to distinguish who sent the message.
	 * 
	 * @author Alberto Mtnz de Murga
	 * 
	 */
	private class MessageAdapter extends BaseAdapter {

		private Context context;
		/**
		 * List with all the messages.
		 */
		private final List<MessageItem> messages;

		/**
		 * Constructor.
		 * 
		 * @param context
		 * @param items
		 *            List with all the messages.
		 */
		public MessageAdapter(Context context, List<MessageItem> items) {
			this.context = context;
			this.messages = new ArrayList<MessageItem>();

			messages.addAll(items);
		}

		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return this.messages.get(position);
		}

		@Override
		/**
		 * Not used
		 */
		public long getItemId(int position) {
			return 0;
		}

		@Override
		/**
		 * Fills the view based on the message.
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.item_message, parent, false);

			MessageItem item = messages.get(position);

			TextView message = (TextView) view
					.findViewById(R.id.messages_message);
			message.setText(item.getMessage());

			TextView date = (TextView) view.findViewById(R.id.messages_date);
			date.setText(item.getCreated().toString());

			if (item.isFromPatient()) {
				TextView patient = (TextView) view
						.findViewById(R.id.messages_you);
				patient.setVisibility(TextView.VISIBLE);
			} else {
				TextView therapist = (TextView) view
						.findViewById(R.id.messages_therapist);
				therapist.setVisibility(TextView.VISIBLE);
			}

			return view;
		}

	}
}
