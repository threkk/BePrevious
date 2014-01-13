package nl.hva.btserver.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import nl.boxlab.model.BoxlabDTO;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.Message;
import nl.boxlab.model.serializer.JSONEntitySerializer;
import nl.hva.btserver.io.FileReader;
import nl.hva.btserver.io.FileWriter;
import nl.hva.btserver.resources.Paths;

/**
 * Class that implements an SPP Server which accepts single line of message from
 * an SPP client and sends a single line of response to the client.
 */
public class BoxlabSPPServer {

	public static final UUID UUID = new UUID("1101", true);
	public static final String BT_URL =
	        String.format("btspp://localhost:%s;name=boxlab server", UUID.toString());

	private final JSONEntitySerializer serializer;
	private final FileWriter writer;
	private final FileReader reader;
	private boolean running = false;

	public BoxlabSPPServer() {
		this.serializer = new JSONEntitySerializer();
		this.writer = new FileWriter();
		this.reader = new FileReader();
	}

	public void startServer() {
		this.running = true;
		while (running) {
			try {
				serverLoop();
			} catch (IOException e) {
				log("an error occured while trying to manager connection");
				e.printStackTrace();
			}
		}
	}

	public void stopServer() {
		this.running = false;
	}

	private void serverLoop() throws IOException
	{
		// open server url
		StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(BT_URL);

		// Wait for client connection
		log("Server Started. Waiting for clients to connect...");
		StreamConnection connection = notifier.acceptAndOpen();
		log("Client connected");

		// read input from client and write it to the files
		String inputString = readInput(connection.openInputStream());
		BoxlabDTO inputDTO = serializer.deserialize(BoxlabDTO.class, inputString);
		writeDTO(inputDTO);

		// write output from files to client
		BoxlabDTO outputDTO = readDTO();
		String outputString = serializer.serialize(outputDTO);
		OutputStream out = connection.openOutputStream();
		writeOutput(out, outputString);

		// clean up the connection
		connection.close();
		notifier.close();
	}

	private String readInput(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String input = "";
		String header = "";
		while ((header = reader.readLine()) != null) {
			if (header.equals("EXIT")) {
				break;
			}
			if (!header.equals("SERVER_FLAG")) {
				input = header;
			}
		}

		return input;
	}

	private void writeOutput(OutputStream out, String output) throws UnsupportedEncodingException {
		PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
		pWriter.println(output + "\r\n");
		pWriter.println("EXIT");
		pWriter.flush();
		pWriter.close();
	}

	private void writeDTO(BoxlabDTO dto) throws IOException {
		writer.appendToFile(Paths.FILE_ENTRIES_OUT, dto.getExerciseEntries());
		writer.appendToFile(Paths.FILE_FEEDBACK_OUT, dto.getSensorFeedback());
		writer.appendToFile(Paths.FILE_MESSAGES_OUT, dto.getMessages());
	}

	private BoxlabDTO readDTO() throws IOException {
		BoxlabDTO dto = new BoxlabDTO();
		dto.setExerciseEntries(reader.readFromFile(Paths.FILE_ENTRIES_IN, ExerciseEntry.class));
		dto.setMessages(reader.readFromFile(Paths.FILE_MESSAGES_IN, Message.class));

		return dto;
	}

	private void log(String message) {
		System.out.printf("[%s]:\t%s\n", new Date(), message);
	}
}