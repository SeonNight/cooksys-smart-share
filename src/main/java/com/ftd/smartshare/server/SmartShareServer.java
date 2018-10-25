package com.ftd.smartshare.server;

import java.io.IOException;
import java.net.ServerSocket;

public class SmartShareServer {
	private static final int PORT = 3000;

	public static void main(String[] args) {
		// Handler for SQL connections
		SQLRequestHandler requestHandler = new SQLRequestHandler();
		// Try making a server port
		try (ServerSocket serverSocket = new ServerSocket(PORT);) {
			while (true) {
				try {
					// Create new thread for each client
					new Thread(new SmartShareClientHandler(serverSocket.accept(), requestHandler)).start();
				} catch (IOException e) {
					System.out.println("Server Connection Failed: ");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("Server Failed To Open: ");
			e.printStackTrace();
		}
	}
}
