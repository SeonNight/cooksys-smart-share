package com.ftd.smartshare.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ftd.smartshare.dto.DownloadRequestDto;
import com.ftd.smartshare.dto.UploadRequestDto;


public class SmartShareClientHandler implements Runnable {
	Socket clientSocket;
	
	public SmartShareClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void run() {
		try (
			// Used to send request to server
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			// Used to receive quotes from server
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		) {
			//Get information from client if it wants to upload or download
			String message = in.readLine();
			//Used for SQL requsts
			SQLRequestHandler requestHandler = new SQLRequestHandler();
    		JAXBContext upContext = JAXBContext.newInstance(UploadRequestDto.class);
			Unmarshaller upUnmarshaller = upContext.createUnmarshaller(); //Upload request
			Marshaller upMarshaller = upContext.createMarshaller(); //Marshaller to send file data back to Client
			Unmarshaller downUnmarshaller = JAXBContext.newInstance(DownloadRequestDto.class).createUnmarshaller(); //Download request

    		StringWriter stringWriter = new StringWriter(); //To write to client
    		
			//If the client wasts an upload
			if(message.equals("Upload")) {
				//Get upload request
				UploadRequestDto uploadRequest = (UploadRequestDto) upUnmarshaller.unmarshal(new StringReader(in.readLine()));
				//System.out.println(uploadRequest.toString());
				
				//Send to SQLRequestHandler then send to client the upload status
				if(requestHandler.setFile(uploadRequest)) {
		    		out.write("Upload Success");
					out.newLine(); // Push a new line
					out.flush();
				} else {
		    		out.write("Upload Failed");
					out.newLine(); // Push a new line
					out.flush();
				}
			} else if(message.equals("Download")) { //If the client wants and download
				//get download request
				DownloadRequestDto downloadRequest = (DownloadRequestDto) downUnmarshaller.unmarshal(new StringReader(in.readLine()));
				//System.out.println(downloadRequest.toString());
				
				//Send to SQLRequestHandler then send file to client
				UploadRequestDto downloadedFile = requestHandler.getFile(downloadRequest);
				if(downloadedFile == null) {
					//Download failed
		    		out.write("Download Failed");
					out.newLine(); // Push a new line
					out.flush();
					
				} else {
					//Download success
		    		out.write("Download Success");
					out.newLine(); // Push a new line
					out.flush();
					upMarshaller.marshal(downloadedFile, stringWriter);
					
		    		//Send downloaded file to client
		    		out.write(stringWriter.toString());
					out.newLine(); // Push a new line
					out.flush();
				}
				
				//Send back as uploadrequest //makes it simpler
			} else { //What the crap did the client ask for?
				System.out.println("Error: Not Upload or Download: " + message);
			}
			
		} catch (IOException e) {
			System.out.println("Server Failed: IO");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.out.println("Server Failed: JAXB");
			e.printStackTrace();
		}
	}
}
