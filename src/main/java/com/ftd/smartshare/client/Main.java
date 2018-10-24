package com.ftd.smartshare.client;

import com.ftd.smartshare.client.commands.SmartShare;
import picocli.CommandLine;

class Main {

    public static void main(String[] args) {
//        CommandLine.run(new SmartShare()); // Pass cli arguments here
//        CommandLine.run(new SmartShare(), "-h");
//        CommandLine.run(new SmartShare(), "--version");
//        CommandLine.run(new SmartShare(), "upload");
//        CommandLine.run(new SmartShare(), "upload", ".gitignore");
        
        //CommandLine.run(new SmartShare(), "upload", "-t", "2", "-d", "1", "pom.xml", "password");
        //CommandLine.run(new SmartShare(), "summary", "pom.xml", "password");
        //CommandLine.run(new SmartShare(), "download", "pom.xml", "password");
        
        CommandLine.run(new SmartShare(), "upload", "pom.xml", "password", "-d", "3");

    	for(int i = 0; i < 3; i++) {
    		//System.out.println("Thread: " + i);
    		new Thread(new MultiClient()).start();
    	}
//        CommandLine.run(new SmartShare(), "upload", "pom.xml", "password", "-d", "2");
//        CommandLine.run(new SmartShare(), "summary", "pom.xml", "password");
//        CommandLine.run(new SmartShare(), "download", "pom.xml", "password");
//        CommandLine.run(new SmartShare(), "download", "pom.xml", "password");
        
//        CommandLine.run(new SmartShare(), "upload", "pom.xml", "password", "-d", "5");
//        CommandLine.run(new SmartShare(), "summary", "pom.xml", "password");
//        CommandLine.run(new SmartShare(), "download", "pom.xml", "password");
    }

}

