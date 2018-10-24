package com.ftd.smartshare.client.commands.subcommands;

import com.ftd.smartshare.client.api.Api;
import com.ftd.smartshare.dto.DownloadRequestDto;
import com.ftd.smartshare.utils.PasswordGenerator;

import picocli.CommandLine;
@CommandLine.Command(
        description = "Gets a summary of a file",
        name = "summary",
        aliases = "s",
        mixinStandardHelpOptions = true
)
public class Summary implements Runnable {
	@CommandLine.Parameters(arity="1", index = "0", description = "Name of file to be loaded")
	private String fileName;
	
	@CommandLine.Parameters(arity="0", index = "1", description = "The password for the file")
	private String password = PasswordGenerator.generate();
	
	public void run() {
	    System.out.println("Getting Summary of " + fileName);
	    
	    //Get summary
        if(Api.getSummary(new DownloadRequestDto(fileName, password))) {
			System.out.println("Summary Success");
        } else {
        	System.out.println("Summary Failed");
        }
	}

}
