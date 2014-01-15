package nl.hva.btserver;

import java.io.IOException;

import javax.bluetooth.LocalDevice;

import nl.hva.btserver.server.BoxlabSPPServer;

public class Launcher {
	public static void main(String[] args) throws IOException {
		// display local device address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("-- local device information --");
		System.out.println("Address: " + localDevice.getBluetoothAddress());
		System.out.println("Name: " + localDevice.getFriendlyName());
		System.out.println("-----");

		BoxlabSPPServer sampleSPPServer = new BoxlabSPPServer();
		sampleSPPServer.startServer();
	}
}
