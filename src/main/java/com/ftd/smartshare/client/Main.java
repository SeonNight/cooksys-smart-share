package com.ftd.smartshare.client;

import com.ftd.smartshare.client.commands.SmartShare;

import picocli.CommandLine;

class Main {

	public static void main(String[] args) {
		// Print info
		System.out.println("--Print Info--");
		CommandLine.run(new SmartShare(), "-h");
		CommandLine.run(new SmartShare(), "--version");

		// Print up load info
		System.out.println("\n--Print Command Info--");
		CommandLine.run(new SmartShare(), "upload", "-h");
		CommandLine.run(new SmartShare(), "download", "-h");
		CommandLine.run(new SmartShare(), "summary", "-h");

		// Test upload, download, summary failure
		System.out.println("\n--Test Failure--");
		CommandLine.run(new SmartShare(), "upload", "-d", "1", "elfi", "password");
		CommandLine.run(new SmartShare(), "download", "elfi", "password");
		CommandLine.run(new SmartShare(), "summary", "elfi", "password");

		// Test upload, summary, download
		System.out.println("\n--Test Success--");
		CommandLine.run(new SmartShare(), "upload", "-d", "1", "test1.txt", "fireworks");
		CommandLine.run(new SmartShare(), "summary", "test1.txt", "fireworks");
		CommandLine.run(new SmartShare(), "download", "test1.txt", "fireworks");

		// Test upload, summary, download for mutiple files
		System.out.println("\n--Test Success Mult--");
		CommandLine.run(new SmartShare(), "upload", "-t", "60", "-d", "1", "test1.txt,test2.txt,test3.txt,test4.txt",
				"pleaseDon'tStopTheMusic");
		CommandLine.run(new SmartShare(), "download", "test1.txt,test2.txt,test3.txt,test4.txt",
				"pleaseDon'tStopTheMusic");

		// Threading Test
		System.out.println("\n--Threading--");
		CommandLine.run(new SmartShare(), "upload", "-t", "2", "-d", "5", "test1.txt,test2.txt,test3.txt,test4.txt",
				"password");

		for (int i = 0; i < 10; i++) {
			new Thread(new MultiClient("download", "test1.txt", "password")).start();
			new Thread(new MultiClient("download", "test2.txt", "password")).start();
			new Thread(new MultiClient("download", "test3.txt,test4.txt", "password")).start();
		}
	}

}
