package com.ftd.smartshare.client.commands.subcommands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ftd.smartshare.client.api.Api;
import com.ftd.smartshare.dto.UploadRequestDto;
import com.ftd.smartshare.utils.PasswordGenerator;

import picocli.CommandLine;
import picocli.CommandLine.Option;

@CommandLine.Command(
        description = "Uploads file using a given 'password', expiration (60 minutes by default), a max downloads (1 by default)",
        name = "upload",
        aliases = "u",
        mixinStandardHelpOptions = true
)
public class Upload implements Runnable {

    @CommandLine.Parameters(arity="1", index = "0", description = "The file to be uploaded")
    private File file;

    @CommandLine.Parameters(arity="0", index = "1", description = "The password for the file")
    private String password = PasswordGenerator.generate();
    
    @Option(names = {"-t", "--time" }, arity = "0..1", description = "Set how many minutes for the file to expire")
    private int expiration = 60; //minutes
    
    @Option(names = {"-d", "--downloads"}, arity = "0..1", description = "Set how many times the file can be downloaded")
    private int maxDownloads = -1; //Default is no restrictions

    public void run() {
        System.out.println("Uploading: " + file.getAbsolutePath());
        System.out.println("Password will be printed below");
        System.out.println(password);
        
        //Maximum expiration 1440 minutes (24 hours), Minimum is 1
        if(expiration > 1440 || expiration < 1) {
        	System.out.println("Expiration must be between 1440 mintues (24 hourse) and 1 minute");
        	return;
        }
        
        //What is in file
        byte[] bytes = null;
        //Check to make sure file exists
        if(file.exists()) {
	        try (
	        		InputStream fileInputStream = new FileInputStream(file);
	        ) { 
	        	//Pull in bytes
		        bytes = new byte[fileInputStream.available()];
		        fileInputStream.read(bytes);
	
		        //Upload file
		        if(Api.upload(new UploadRequestDto(file.getName(), bytes, expiration, maxDownloads, password))) {
		        	System.out.println("Uploaded");
		        } else {
		        	System.out.println("Upload Failed");
		        }
			} catch (IOException e) {
	        	System.out.println("Upload Failed");
			}
        } else {
        	System.out.println("Upload Failed");
        }
        
        
    }


}
