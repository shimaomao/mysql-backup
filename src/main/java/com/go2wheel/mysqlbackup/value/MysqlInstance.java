package com.go2wheel.mysqlbackup.value;

public class MysqlInstance {
	
	private int port = 3306;
	private String username;
	private String password;
	private String mycnfFile;
	
	private boolean readyForBackup;
	
	private LogBinSetting logBinSetting;
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMycnfFile() {
		return mycnfFile;
	}
	public void setMycnfFile(String mycnfFile) {
		this.mycnfFile = mycnfFile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LogBinSetting getLogBinSetting() {
		return logBinSetting;
	}

	public void setLogBinSetting(LogBinSetting logBinSetting) {
		this.logBinSetting = logBinSetting;
	}

	public boolean isReadyForBackup() {
		return readyForBackup;
	}

	public void setReadyForBackup(boolean readyForBackup) {
		this.readyForBackup = readyForBackup;
	}
}
