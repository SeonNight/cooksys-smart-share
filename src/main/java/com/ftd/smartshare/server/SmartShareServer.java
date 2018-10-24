package com.ftd.smartshare.server;

import java.io.IOException;
import java.net.ServerSocket;

public class SmartShareServer {
	private static final int PORT = 3000;

	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(PORT);) {
			while (true) {
				try {
					new Thread(new SmartShareClientHandler(serverSocket.accept())).start();
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
