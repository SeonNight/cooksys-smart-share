package com.ftd.smartshare.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SummaryDto {
	@XmlElement
	private Date timeCreated = null;
	@XmlElement
	private int downloadsRemaining;
	@XmlElement
	private int timeTilExpiration;
	
	public SummaryDto() {}
	
	public SummaryDto(Date timeCreated, int downloadsRemaining, int timeTilExpiration) {
		super();
		this.timeCreated = timeCreated;
		this.downloadsRemaining = downloadsRemaining;
		this.timeTilExpiration = timeTilExpiration;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	public int getDownloadsRemaining() {
		return downloadsRemaining;
	}
	public void setDownloadsRemaining(int downloadsRemaining) {
		this.downloadsRemaining = downloadsRemaining;
	}
	public int getTimeTilExpiration() {
		return timeTilExpiration;
	}
	public void setTimeTilExpiration(int timeTilExpiration) {
		this.timeTilExpiration = timeTilExpiration;
	}

	@Override
	public String toString() {
		return " Time Created = " + timeCreated + "\n Downloads Remaining = " + downloadsRemaining
				+ "\n Time Until Expiration = " + timeTilExpiration + " minutes";
	}
}
