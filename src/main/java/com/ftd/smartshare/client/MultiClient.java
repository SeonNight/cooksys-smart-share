package com.ftd.smartshare.client;

import com.ftd.smartshare.client.commands.SmartShare;

import picocli.CommandLine;

public class MultiClient implements Runnable {
	private String filename = "pom.xml";
	private String password = "password";
	private String option = "summary";
	private String time = "5";
	private String download = "5";
	
	public MultiClient() {
		
	}
	public MultiClient(String option, String filename, String password) {
		this.filename = filename;
		this.password = password;
		this.option = option;
	}
	
	public MultiClient(String option, String filename, String password, String download, String time) {
		this.filename = filename;
		this.password = password;
		this.option = option;
		this.download = download;
		this.time = time;
	}
	
	public void run() {
		switch(option) {
			case "download":
				CommandLine.run(new SmartShare(), "download", filename, password);
				break;
			case "upload":
				CommandLine.run(new SmartShare(), "upload", "-d", download, "-t", time, filename, password);
				break;
			default:
			case "summary":
				CommandLine.run(new SmartShare(), "summary", filename, password);
				break;
		}
	}
}
