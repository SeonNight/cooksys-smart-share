package com.ftd.smartshare.client;

import com.ftd.smartshare.client.commands.SmartShare;
import picocli.CommandLine;

class Main {

    public static void main(String[] args) {
    	//Print info
        System.out.println("--Print Info--");
        CommandLine.run(new SmartShare(), "-h");
        CommandLine.run(new SmartShare(), "--version");
        
        //Print up load info
        System.out.println("\n--Print Command Info--");
        CommandLine.run(new SmartShare(), "upload", "-h");
        CommandLine.run(new SmartShare(), "download", "-h");
        CommandLine.run(new SmartShare(), "summary", "-h");

        //Test upload, download, summary failure
        System.out.println("\n--Test Failure--");
        CommandLine.run(new SmartShare(), "upload", "-d", "1", "elfi", "password");
        CommandLine.run(new SmartShare(), "download", "elfi", "password");
        CommandLine.run(new SmartShare(), "summary", "elfi", "password");

        //Test upload, summary, download (Multiple files)
        System.out.println("\n--Test Success--");
        CommandLine.run(new SmartShare(), "upload", "-d", "2", ".gitignore", "password");
        CommandLine.run(new SmartShare(), "summary", ".gitignore", "password");
        CommandLine.run(new SmartShare(), "download", ".gitignore", "password");
        CommandLine.run(new SmartShare(), "upload", "-t", "2", "-d", "1", "pom.xml", "password");
        CommandLine.run(new SmartShare(), "summary", "pom.xml", "password");
        CommandLine.run(new SmartShare(), "download", "pom.xml", "password");
        CommandLine.run(new SmartShare(), "download", ".gitignore", "password");
        
        //Threading Test
        System.out.println("\n--Threading--");
        CommandLine.run(new SmartShare(), "upload", "-t", "2", "-d", "5", "pom.xml", "password");

    	for(int i = 0; i < 10; i++) {
    		new Thread(new MultiClient("download", "pom.xml", "password")).start();
    	}
    }

}

