package com.ftd.smartshare.client.commands.subcommands;

import com.ftd.smartshare.client.api.Api;
import com.ftd.smartshare.dto.DownloadRequestDto;

import picocli.CommandLine;

@CommandLine.Command(
        description = "Downloads a file(s)",
        name = "download",
        aliases = "d",
        mixinStandardHelpOptions = true
)
public class Download implements Runnable {

    @CommandLine.Parameters(arity="1", index = "0", split = ",", description = "Name of file(s) to be downloaded")
    private String[] fileName;

    @CommandLine.Parameters(arity="1", index = "1", description = "The password for the file(s)")
    private String password;

    public void run() {
    	for(String fn : fileName) {
	        System.out.println("Downloading " + fn);
	        //Download
	        if(Api.download(new DownloadRequestDto(fn, password))) {
	        	System.out.println("Downloaded");
	        } else {
	        	System.out.println("Download Failed");
	        }
    	}
    }

}
