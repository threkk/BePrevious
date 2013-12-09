import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;

import javax.bluetooth.*;
import javax.microedition.io.*;

/**
 * Class that implements an SPP Server which accepts single line of message from
 * an SPP client and sends a single line of response to the client.
 */
public class BoxlabSPPServer {

	// start server
	private void startServer() throws IOException {
		Filehandler fileHandler = new Filehandler();
		// Create a UUID for SPP
		UUID uuid = new UUID("1101", true);
		// Create the service url
		String connectionString = "btspp://localhost:" + uuid
				+ ";name=boxlab server";

		// open server url
		StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector
				.open(connectionString);

		// Wait for client connection
		System.out
				.println("\nServer Started. Waiting for clients to connect...");
		StreamConnection connection = streamConnNotifier.acceptAndOpen();

		System.out.println("Time:" + new Date() + "  got connection");
		// read string from spp client
		InputStream inStream = connection.openInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(
				inStream));

		// while(bReader.ready()){
		List<String> inputFromAndroid = new ArrayList<>();
		String header = "";
		while ((header = bReader.readLine()) != null) {
			if (header.equals("EXIT")) {
				break;
			}
			if (!header.equals("SERVER_FLAG")) {
				inputFromAndroid.add(header);
			}

		}
		fileHandler.writeToFile(inputFromAndroid);
		OutputStream outStream = connection.openOutputStream();
		PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream,
				"UTF-8"));

		List<String> readFromFiles = fileHandler.readFromFiles();
		for (String string : readFromFiles) {
			pWriter.println(string + "\r\n");
		}
		pWriter.println("EXIT");
		pWriter.close();
		connection.close();
		streamConnNotifier.close();

	}

	public static void main(String[] args) throws IOException {

		// display local device address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: " + localDevice.getBluetoothAddress());

		System.out.println("Name: " + localDevice.getFriendlyName());
		while (true) {
			BoxlabSPPServer sampleSPPServer = new BoxlabSPPServer();
			sampleSPPServer.startServer();
		}

	}
}