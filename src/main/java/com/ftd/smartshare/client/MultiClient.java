package com.ftd.smartshare.client;

import com.ftd.smartshare.client.commands.SmartShare;

import picocli.CommandLine;

public class MultiClient implements Runnable {
	private String filename = "pom.xml";
	private String password = "password";
	
	public MultiClient() {
		
	}
	public MultiClient(String filename, String password) {
		this.filename = filename;
		this.password = password;
	}
	
	public void run() {
		CommandLine.run(new SmartShare(), "summary", filename, password);
    	CommandLine.run(new SmartShare(), "download", filename, password);
	}
}
