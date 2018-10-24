package com.ftd.smartshare.client.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public final class Api {

    private static final String HOST    = "localhost";
    private static final int    PORT    = 3000;

    private Api () {
        throw new UnsupportedOperationException();
    }

    /**
     * Send download request
     *
     * @param downloadRequestDto    JAXB annotated class representing the download request
     * @return true if request was successful and false if unsuccessful
     */
    public static boolean download(DownloadRequestDto downloadRequestDto) {
    	try (
    		//Connect to server
    		Socket serverSocket = new Socket(HOST, PORT);
			// Used to send to server
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
			// Used to receive from server
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    	) {
    		//Create marshllar for UploadReuqestDto to sent to server
    		Marshaller marshaller = JAXBContext.newInstance(DownloadRequestDto.class).createMarshaller();
    		Unmarshaller unmarshaller = JAXBContext.newInstance(UploadRequestDto.class).createUnmarshaller();
    		StringWriter stringWriter = new StringWriter();
    		
    		//Tell server if we are uploaded or downloading
    		out.write("Download");
    		out.newLine();
    		out.flush();

    		marshaller.marshal(downloadRequestDto, stringWriter);
    		//Send UploadRequest to server
    		out.write(stringWriter.toString()); // push request it to server
			out.newLine(); // Push a new line
			out.flush();
			
			
			if(in.readLine().equals("Download Success")) {
				UploadRequestDto uploadRequest = (UploadRequestDto) unmarshaller.unmarshal(new StringReader(in.readLine()));
				try(
					OutputStream fileOutputStream = new FileOutputStream(new File(uploadRequest.getFilename()));
					//OutputStream fileOutputStream = new FileOutputStream(new File("OutputTest.txt"));
				) {
					fileOutputStream.write(uploadRequest.getFile());
				} catch (IOException e) {
					System.out.println("Client File Output Failed");
					e.printStackTrace();
					return false;
				}
			} else {
				return false;
			}
    		
    		return true;
		} catch (IOException e) {
			System.out.println("Download Failed: IO");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.out.println("Download Faild: JAXB");
			e.printStackTrace();
		}
		return false;
    }

    /**
     * Send upload request
     *
     * @param uploadRequestDto      JAXB annotated class representing the upload request
     * @return true if request was successful and false if unsuccessful
     */
    public static boolean upload(UploadRequestDto uploadRequestDto) {
    	try (
    		//Connect to server
    		Socket serverSocket = new Socket(HOST, PORT);
			// Used to send to server
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
			// Used to receive from server
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    	) {
    		//Create marshllar for UploadReuqestDto to sent to server
    		JAXBContext context = JAXBContext.newInstance(UploadRequestDto.class);
    		Marshaller marshaller = context.createMarshaller();
    		StringWriter stringWriter = new StringWriter();
    		
    		//Tell server if we are uploaded or downloading
    		out.write("Upload");
			out.newLine(); // Push a new line
			out.flush();

    		marshaller.marshal(uploadRequestDto, stringWriter);
    		//Send UploadRequest to server
    		out.write(stringWriter.toString()); // push request it to server
			out.newLine(); // Push a new line
			out.flush();
			
			if(!in.readLine().equals("Upload Success")) {
				return false;
			}
    		return true;
		} catch (IOException e) {
			System.out.println("Upload Failed: IO");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.out.println("Upload Faild: JAXB");
			e.printStackTrace();
		}
		return false;
    }

}
