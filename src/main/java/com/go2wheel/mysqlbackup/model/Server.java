package com.go2wheel.mysqlbackup.model;

import javax.validation.constraints.NotNull;

public class Server extends BaseModel {
	
	@NotNull
	private String host;
	
	private int coreNumber;
	
	@Override
	public String toString() {
		return String.format("[%s, %s]", getId(), host);
	}
	
	public Server() {}
	
	public Server(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getCoreNumber() {
		return coreNumber;
	}

	public void setCoreNumber(int coreNumber) {
		this.coreNumber = coreNumber;
	}
	
}
