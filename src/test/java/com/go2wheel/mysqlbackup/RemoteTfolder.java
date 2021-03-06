package com.go2wheel.mysqlbackup;

import java.io.IOException;

import org.junit.rules.ExternalResource;

import com.go2wheel.mysqlbackup.exception.RunRemoteCommandException;
import com.go2wheel.mysqlbackup.util.SSHcommonUtil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class RemoteTfolder extends ExternalResource {
	
	private String remoteFolder;
	

	public String getRemoteFolder() {
		return remoteFolder;
	}

	private Session session;
	
	public RemoteTfolder(String remoteFolder) {
		this.remoteFolder = remoteFolder.endsWith("/") ? remoteFolder : remoteFolder + "/";
	}
	
	public void setSession(Session session) {
		this.session = session;
	}

	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder.endsWith("/") ? remoteFolder : remoteFolder + "/";
	}
	@Override
	protected void after() {
		if (this.session != null&& this.remoteFolder != null && this.remoteFolder.matches("/.*/.*")) {
			try {
				SSHcommonUtil.runRemoteCommand(session, String.format("rm -rf %s", this.remoteFolder));
			} catch (RunRemoteCommandException | JSchException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String newFile(String rel) {
		rel = rel.startsWith("/") ? rel.substring(1) : rel;
		return remoteFolder + rel;
	}
	
}
