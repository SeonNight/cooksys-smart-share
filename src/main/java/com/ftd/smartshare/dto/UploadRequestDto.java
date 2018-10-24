package com.ftd.smartshare.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadRequestDto {
	@XmlElement
	private String filename; //name of file
	@XmlElement
	private byte[] file; //file
	@XmlElement
	private int expirayTime; //when will it die
	@XmlElement
	private int maxDownloads; //How many downloads it can do
	@XmlElement
	private String password; //text, not null
	
	public UploadRequestDto() {
		
	}

	public UploadRequestDto(String filename, byte[] file, int expirayTime, int maxDownloads,
			String password) {
		this.filename = filename;
		this.file = file;
		this.expirayTime = expirayTime;
		this.maxDownloads = maxDownloads;
		this.password = password;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public int getExpirayTime() {
		return expirayTime;
	}

	public void setExpirayTime(int expirayTime) {
		this.expirayTime = expirayTime;
	}

	public int getMaxDownloads() {
		return maxDownloads;
	}

	public void setMaxDownloads(int maxDownloads) {
		this.maxDownloads = maxDownloads;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UploadRequestDto [filename=" + filename + ", file.length=" + file.length + ", expirayTime="
				+ expirayTime + ", maxDownloads=" + maxDownloads + ", password=" + password + "]";
	}
}
