package com.go2wheel.mysqlbackup;

public class YmlConfigFort {

	private String sshHost;
	private String sshIdrsa;
	private String knownHosts;
	
	private boolean envExists;
	
	public String getSshHost() {
		return sshHost;
	}
	public void setSshHost(String sshHost) {
		this.sshHost = sshHost;
	}
	public String getSshIdrsa() {
		return sshIdrsa;
	}
	public void setSshIdrsa(String sshIdrsa) {
		this.sshIdrsa = sshIdrsa;
	}
	public String getKnownHosts() {
		return knownHosts;
	}
	public void setKnownHosts(String knownHosts) {
		this.knownHosts = knownHosts;
	}
	public boolean isEnvExists() {
		return envExists;
	}
	public void setEnvExists(boolean envExists) {
		this.envExists = envExists;
	}
	
	
}