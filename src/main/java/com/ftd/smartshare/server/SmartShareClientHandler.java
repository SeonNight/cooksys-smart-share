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
import com.ftd.smartshare.dto.SummaryDto;
import com.ftd.smartshare.dto.UploadRequestDto;

public class SmartShareClientHandler implements Runnable {
	Socket clientSocket;

	public SmartShareClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		try (
				// Used to send to client
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				// Used to receive from client
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			// Used for SQL requsts
			SQLRequestHandler requestHandler = new SQLRequestHandler();
			JAXBContext upContext = JAXBContext.newInstance(UploadRequestDto.class);

			Unmarshaller upUnmarshaller = upContext.createUnmarshaller(); // Upload request
			Unmarshaller downUnmarshaller = JAXBContext.newInstance(DownloadRequestDto.class).createUnmarshaller(); // Download

			Marshaller upMarshaller = upContext.createMarshaller(); // Marshaller to send file data back to Client
			Marshaller sumMarshaller = JAXBContext.newInstance(SummaryDto.class).createMarshaller(); // summary

			StringWriter stringWriter = new StringWriter(); // To write to client

			// Get information from Client on what the client wants to do
			String message = in.readLine();

			// If the client wasts an upload
			if (message.equals("Upload")) {
				// Get upload request
				UploadRequestDto uploadRequest = (UploadRequestDto) upUnmarshaller
						.unmarshal(new StringReader(in.readLine()));

				// Send to SQLRequestHandler then send to client the upload status
				if (requestHandler.setFile(uploadRequest)) {
					out.write("Upload Success");
					out.newLine();
					out.flush();
				} else {
					out.write("Upload Failed");
					out.newLine();
					out.flush();
				}
			} else if (message.equals("Download")) { // If the client wants and download
				// get download request
				DownloadRequestDto downloadRequest = (DownloadRequestDto) downUnmarshaller
						.unmarshal(new StringReader(in.readLine()));

				// Send to SQLRequestHandler then send file to client
				UploadRequestDto downloadedFile = requestHandler.getFile(downloadRequest);
				if (downloadedFile == null) {
					// Download failed
					out.write("Download Failed");
					out.newLine();
					out.flush();

				} else {
					// Download success
					out.write("Download Success");
					out.newLine();
					out.flush();

					// Send downloaded file to client
					upMarshaller.marshal(downloadedFile, stringWriter);
					out.write(stringWriter.toString());
					out.newLine();
					out.flush();
				}

				// Send back as uploadrequest //makes it simpler
			} else if (message.equals("Summary")) { // If the client wants a summary
				// get download request
				DownloadRequestDto downloadRequest = (DownloadRequestDto) downUnmarshaller
						.unmarshal(new StringReader(in.readLine()));

				// Send to SQLRequestHandler then send file to client
				SummaryDto summaryFile = requestHandler.getSummary(downloadRequest);
				if (summaryFile == null) {
					// Download failed
					out.write("Summary Failed");
					out.newLine();
					out.flush();

				} else {
					// Download success
					out.write("Summary Success");
					out.newLine();
					out.flush();

					// Send summary to Client
					sumMarshaller.marshal(summaryFile, stringWriter);
					out.write(stringWriter.toString());
					out.newLine(); // Push a new line
					out.flush();
				}
			} else { // What the crap did the client ask for? (There is no reason for it to come
						// here...)
				System.out.println("Error: Not Upload, Download, or Summary: " + message);
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