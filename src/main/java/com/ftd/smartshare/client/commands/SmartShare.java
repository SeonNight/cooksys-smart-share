package com.ftd.smartshare.client.commands;

import com.ftd.smartshare.client.commands.subcommands.Download;
import com.ftd.smartshare.client.commands.subcommands.Summary;
import com.ftd.smartshare.client.commands.subcommands.Upload;

import picocli.CommandLine;

// TODO The customSynopsis needs to be updated if the commands are updated
@CommandLine.Command(
        description = "Uploads/downloads a 'secure' file using a given/generated file."
        		+ " Additional features include expirations (defaults to 60 minutes)"
        		+ " which delete the file after a specified time, max downloads (defaults to 1)"
        		+ " which deletes the file after the specified download limit has been reached,"
        		+ " and view the current status of a file.",
        name = "smartshare",
        mixinStandardHelpOptions = true,
        version = "v1.0",
        customSynopsis = {
                "\nsmartshare [-hV] [COMMAND]",
                "smartshare [upload|u] [OPTION]... <file> [password]",
                "smartshare [download|d] <file> <password>",
                "smartshare [summary|s] <file> <password>\n"
        },
        subcommands = {
                Download.class,
                Upload.class,
                Summary.class
        })
public class SmartShare implements Runnable {
    public void run() {
        CommandLine cl = new CommandLine(new SmartShare());
        cl.usage(System.out);
    }
}
