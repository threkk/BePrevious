package nl.hva.boxlabapp.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class manages the Bluetooth connection with the Raspberry Pi. This class
 * is created ad-hoc for the Raspberry Pi server.
 * 
 * Many of the operation are potentially blocking operations, so the device can
 * freeze.
 * 
 * @author Alberto Mtnz de Murga
 * @version 1
 * 
 */
public class ConnectToRaspberryPi extends Thread {

	/**
	 * UUID needed to connect both devices. Must be the same in both sides. Why
	 * this one? Magic!
	 */
	public final static UUID SEC_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	/**
	 * Intent id.
	 */
	public final static String JSON = "JSON";

	// TODO: Fix this. No longer needed with the new data model.
	public final static String SEPARATOR = "–";
	private final static String TAG = ConnectToRaspberryPi.class.getName();
	// TODO: Change the flags for hashes.
	private final static String SERVER_FLAG = "SERVER_FLAG\n";
	private final static String EXIT = "EXIT";

	private final static int CONNECTING = 0;
	private final static int CONNECTED = 1;
	private final static int SENT = 2;

	private final BluetoothAdapter mBluetoothAdapter;
	private final BluetoothDevice mBluetoothDevice;
	private final BluetoothSocket mBluetoothSocket;
	private ManageConnection manager;
	private final Handler mHandler;
	public int state;

	/**
	 * Constructor. The tablet application will always connect to the server.
	 * During the creation of the object, it will establish a connection with
	 * the RP.
	 * 
	 * @param mac
	 *            Mac address of the Raspberry Pi.
	 * @param adapter
	 *            Bluetooth adapter of the device.
	 */
	public ConnectToRaspberryPi(String mac, BluetoothAdapter adapter,
			Handler handler) {
		mHandler = handler;

		mBluetoothAdapter = adapter;
		mBluetoothDevice = adapter.getRemoteDevice(mac);
		manager = null;

		state = CONNECTING;
		BluetoothSocket tmp = null;
		try {
			tmp = mBluetoothDevice.createRfcommSocketToServiceRecord(SEC_UUID);
		} catch (IOException oops) {
			Log.e(TAG, "Failed connection");
		}
		mBluetoothSocket = tmp;
	}

	/**
	 * This thread establish the connection with the server. this method
	 * shouldn't be called, instead use the start(). The server needs a flag
	 * before being able to read or write, so until the flag is received, it
	 * won't work. This waiting won't block the app, but it won't work.
	 */
	public void run() {
		// Cancel discovery to free the bt.
		mBluetoothAdapter.cancelDiscovery();

		try {
			// Try to connect
			mBluetoothSocket.connect();
		} catch (IOException oops) {
			try {
				// If you can connect maybe it is because the socket is still
				// open.
				mBluetoothSocket.close();
			} catch (IOException ooops) {
				Log.e(TAG, "The socket neither open or close.");
				ooops.printStackTrace();
			}
			return;
		}

		// The server needs to receive a flag first in order to work.
		try {
			byte[] flag = SERVER_FLAG.getBytes();
			mBluetoothSocket.getOutputStream().write(flag);
			Log.e(TAG, "Flag sent");
		} catch (IOException e) {
			Log.e(TAG, "Error sending flag");
		}

		manager = new ManageConnection(mBluetoothSocket);
		manager.start();
		state = CONNECTED;
	}

	/**
	 * This method will block the thread if it is not ready to send messages. It
	 * shouldn't happen but you never know.
	 * 
	 * @param msg
	 *            The array of bytes you want to send.
	 */

	public void write(byte[] msg) {
		// Until the server connection is established, you cannot send anything.
		// This can block the device.
		while (state != CONNECTED)
			;
		manager.write(msg);
	}

	/**
	 * Free the socket and destroy the manager.
	 */
	public void cancel() {
		if (manager != null) {
			manager.cancel();
			manager = null;
		} else {
			try {
				mBluetoothSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Problems closing the socket.");
			}
		}
	}

	/**
	 * This class handle all the I/O operations. It is a different thread to
	 * avoid the app to block while doing the I/O.
	 * 
	 * @author Alberto Mtnz de Murga
	 * @version 1
	 */

	private class ManageConnection extends Thread {

		private InputStream input;
		private OutputStream output;
		private BluetoothSocket mmBluetoothSocket;

		public ManageConnection(BluetoothSocket socket) {
			mmBluetoothSocket = socket;

			try {
				input = mmBluetoothSocket.getInputStream();
				output = mmBluetoothSocket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "Problems creating the manager.");
			}
		}

		/**
		 * Reads the buffer until receiving a EXIT flag. After, it sends a
		 * message to the handler with all the information read.
		 * 
		 */
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line = null;
			String msgContent = "";
			Log.i(TAG, "Reading...");
			try {
				while ((line = br.readLine()) != null) {
					if (line.equals(EXIT))
						break;
					msgContent += line;
				}
				this.write(EXIT.getBytes());
				Log.i(TAG, "All lines read");
			} catch (IOException oops) {
				Log.e(TAG, "Problems reading");
			}

			if (msgContent != "") {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString(JSON, msgContent);
				msg.setData(b);
				msg.what = 0;
				boolean ret = mHandler.sendMessage(msg);
				state = ConnectToRaspberryPi.SENT;
				if (ret) {
					Log.i(TAG, "Message sent");
					Log.i(TAG, msgContent);
				} else {
					Log.e(TAG, "Error sending the message.");
				}
			}
		}

		/**
		 * Sends the input to the server.
		 * 
		 * @param msg
		 *            The information to send. It can be whatever.
		 */
		public void write(byte[] msg) {
			try {
				output.write(msg);
			} catch (IOException e) {
				Log.e(TAG, "Problem writing.");
			}
		}

		/**
		 * Closes everything. Recommended to avoid blocking resources.
		 */
		public void cancel() {
			try {
				input.close();
				output.close();
				mmBluetoothSocket.close();
			} catch (Exception e) {
				Log.e(TAG, "Problems closing the socket.");
			}
			input = null;
			output = null;
			mmBluetoothSocket = null;
		}
	}
}