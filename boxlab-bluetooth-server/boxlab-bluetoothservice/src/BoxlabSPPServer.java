import java.io.BufferedReader;
import java.io.BufferedWriter;

import nl.boxlab.model.BoxlabDTO;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;
import nl.boxlab.model.serializer.JSONEntitySerializer;
import nl.hva.btserver.io.FileReader;
import nl.hva.btserver.io.FileWriter;
import nl.hva.btserver.resources.Paths;

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
		String inputFromAndroid = "";
		String header = "";
		while ((header = bReader.readLine()) != null) {
			if (header.equals("EXIT")) {
				break;
			}
			if (!header.equals("SERVER_FLAG")) {
				inputFromAndroid = header;
			}

		}
		JSONEntitySerializer serializer = new JSONEntitySerializer();
		BoxlabDTO dtoFromAndroid = serializer.deserialize(BoxlabDTO.class, inputFromAndroid);
		
		FileWriter fileWriter = new FileWriter();
		fileWriter.appendToFile(Paths.FILE_ENTRIES_OUT, dtoFromAndroid.getExerciseEntries());
		fileWriter.appendToFile(Paths.FILE_FEEDBACK_OUT, dtoFromAndroid.getSensorFeedback());
		fileWriter.appendToFile(Paths.FILE_MESSAGES_OUT, dtoFromAndroid.getMessages());
		
		FileReader fileReader = new FileReader();
		BoxlabDTO dtoToAndroid = new BoxlabDTO();
		dtoToAndroid.setExerciseEntries(fileReader.readFromFile(Paths.FILE_ENTRIES_IN, ExerciseEntry.class));
		dtoToAndroid.setMessages(fileReader.readFromFile(Paths.FILE_MESSAGES_IN, Message.class));
		
		String output = serializer.serialize(dtoToAndroid);
		OutputStream outStream = connection.openOutputStream();
		PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream,
				"UTF-8"));

		
		pWriter.println(output + "\r\n");
		
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