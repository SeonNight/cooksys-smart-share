package com.ftd.smartshare.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DownloadRequestDto {
	@XmlElement
	private String filename;
	@XmlElement
	private String password;

	public DownloadRequestDto() {

	}

	public DownloadRequestDto(String filename, String password) {
		this.filename = filename;
		this.password = password;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "DownloadRequestDto [filename=" + filename + ", password=" + password + "]";
	}
}
